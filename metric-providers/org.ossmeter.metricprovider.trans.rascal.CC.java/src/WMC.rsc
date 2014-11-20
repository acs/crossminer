module WMC

import lang::java::m3::AST;
import lang::java::m3::Core;
import IO;
import Node;
import List;
import String;
import Map;
import ValueIO;

import org::ossmeter::metricprovider::MetricProvider;
import org::ossmeter::metricprovider::ProjectDelta;
import CC;


@metric{WMCJava}
@doc{Cyclomatic complexity is a measure of the number of unique control flow paths in the methods of a class. This indicates how many different test cases
you would need to test the method. A high number indicates also a lot of work to understand the method. The weighted method count for a class is the sum
of the cyclomatic complexity measures of all methods in the class. This metric is a basic metric for further processing downstream. It is not easily compared between projects.}
@friendlyName{Weighted Method Count (Java)}
@appliesTo{java()}
@uses = ("CCJava" : "methodCC")
map[loc class, int wmcCount] getWMC(
	ProjectDelta delta = ProjectDelta::\empty(),
	map[loc, loc] workingCopies = (),
	rel[Language, loc, M3] m3s = {},
	map[loc, int] methodCC = (),
	map[loc, int] prev = ()
	)
{
	map[loc, int] result = prev;
	changed = getChangedFilesInWorkingCopyFolders(delta, workingCopies);
	
	// remove results for changed files
	result -= (file : 0 | file <- changed); 
	
	for (file <- changed, m3 <- m3s[java(), file]) {
		result += (cl : (0 | it + methodCC[m]?0 | m <- m3@containment[cl], isMethod(m)) | <cl, _> <- m3@containment, isClass(cl));
	}
	 
	return result;
}

@metric{CCJava}
@doc{Cyclomatic complexity is a measure of the number of unique control flow paths in the methods of a class. This indicates how many different test cases
you would need to test the method. A high number indicates also a lot of work to understand the method.  This metric is a basic metric for further processing downstream. It is not easily compared between projects.}
@friendlyName{McCabe's Cyclomatic Complexity Metric (Java)}
@appliesTo{java()}
map[loc, int] getCC(ProjectDelta delta = ProjectDelta::\empty(),
    map[loc, loc] workingCopies = (),
    rel[Language, loc, AST] asts = {},
    map[loc, int] prev = ()
  ) 
{
  map[loc method, int cc] result = ();
  changed = getChangedFilesInWorkingCopyFolders(delta, workingCopies);
  
  for (file <- changed, ast <- asts[java(), file]) {
    visit (ast) {
      case Declaration d: \method(_, _, _, _, _) :  result += (d@decl : countCC(d));
      case Declaration d: \method(_, _, _, _) : result += (d@decl : countCC(d));
      case Declaration d: \constructor(_, _, _, _) : result += (d@decl : countCC(d));
    }
  }
  
  return result;
}

int countCC(Declaration ast) {
  int count = 1;
  
  visit(ast) {
    case \foreach(Declaration parameter, Expression collection, Statement body): count += 1;
    case \for(list[Expression] initializers, Expression condition, list[Expression] updaters, Statement body): count += 1;
    case \if(Expression condition, Statement thenBranch, Statement elseBranch): count += 1;
    case \for(list[Expression] initializers, list[Expression] updaters, Statement body): count += 1;
    case \if(Expression condition, Statement thenBranch): count += 1;
    case \case(Expression expression): count += 1;
    case \while(Expression condition, Statement body): count += 1;
    case \do(Statement body, Expression condition): count += 1;
    case \catch(Declaration exception, Statement body): count += 1;
    case \infix(Expression lhs, "||", Expression rhs, list[Expression] extendedOperands): count += 1 + size(extendedOperands);
    case \infix(Expression lhs, "&&", Expression rhs, list[Expression] extendedOperands): count += 1 + size(extendedOperands);
    
    // for embedded declarations we have already counted the nested methods (visit is bottom-up), 
    // so lets remove them again.
    // this should not happen too often, so its not much of a performance penalty.
    case \newObject(_, _, _, Declaration nested)    : count -= countCC(nested);
    case \newObject(_, _, Declaration nested)       : count -= countCC(nested);
    case \declarationExpression(Declaration nested) : count -= countCC(nested);
  }
  
  return count;
}

@metric{CCOverJavaMethods}
@doc{Calculates how cyclomatic complexity is spread over the methods of a system. If high CC is localized, then this may be easily fixed but if many methods have high complexity, then the project may be at risk. This metric is good to compare between projects.}
@friendlyName{ccovermethodsJava}
@appliesTo{java()}
@uses{("CCJava" : "methodCC")}
@historic
real giniCCOverMethodsJava(map[loc, int] methodCC = ()) {
  return giniCCOverMethods(methodCC);
}


@metric{CCHistogramJava}
@doc{Number of Java methods per CC risk factor, counts the number of methods which are in a low, medium or high risk factor. The histogram can be compared between projects to indicate which is probably easier to maintain on a method-by-method basis.}
@friendlyName{Number of Java methods per CC risk factor}
@appliesTo{java()}
@uses{("CCJava" : "methodCC")}
@historic
map[str, int] CCHistogramJava(map[loc, int] methodCC = ()) {
  return CCHistogram(methodCC);
}


@metric{CCJavaFactoid}
@doc{The cyclometic complexity of the project's Java code indicates on the lowest code level (inside the body of methods) how hard the code to test is and also provides contra indications for underdstandability.}
@friendlyName{CCJavaFactoid}
@appliesTo{java()}
@uses{("CCHistogramJava" : "hist")}
Factoid CC(map[str, int] hist = ()) {
  return CCFactoid(hist, "Java");
}

