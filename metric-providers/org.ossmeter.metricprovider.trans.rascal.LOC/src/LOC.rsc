module LOC

import analysis::m3::Core;
import analysis::m3::AST;
import analysis::graphs::Graph;
import org::ossmeter::metricprovider::MetricProvider;

import analysis::statistics::Frequency;
import analysis::statistics::Inference;

import Prelude;

import Generic;
 
@metric{genericLOC}
@doc{Physical lines of code simply counts the number of newline characters (OS independent) in a source code file. 
The metric can be used to compare the volume between two systems.}
@friendlyName{Language independent physical lines of code}
@appliesTo{generic()}
map[loc, int] countLoc(rel[Language, loc, AST] asts = {}) {
  return (f:size(ls) | <generic(), f, lines(ls)> <- asts);
}   

real giniLOC(map[loc, int] locs) {
  dist = distribution(locs);
  if (size(dist) < 2) {
  	throw undefined("Not enough LOC data available.", |tmp:///|);
  }
  return gini([<x, dist[x]> | x <- dist]);
}

@metric{genericLOCoverFiles}
@doc{We find out how evenly the code is spread over files. The number should be quite stable over time. A jump in this metric indicates a large change in the code base. If the code is focused in only a few very large files then this may be a contra-indicator for quality.}
@friendlyName{Spread of code over files}
@appliesTo{generic()}
@historic
real giniLOCOverFiles(rel[Language, loc, AST] asts = {}) {
  return giniLOC(countLoc(asts=asts));
}


@metric{locPerLanguage}
@doc{Physical lines of code simply counts the number of newline characters (OS independent) in a source code file. We accumulate this number per programming language. 
The metric can be used to compare the volume between two systems and to assess in which programming language the bulk of the code is written.}
@friendlyName{Physical lines of code per language}
@appliesTo{generic()}
@uses{("genericLOC" :"genericLoc")}
@historic
map[str, int] locPerLanguage(rel[Language, loc, AST] asts = {}, map[loc, int] genericLoc = ()) {
  map[str, int] result = ();
  set[loc] filesWithLanguageDetected = {};
  
  // first count LOC of files with extracted ASTs
  for (<l, f, a> <- asts, l != generic(), f in genericLoc) {
    result["<getName(l)>"]?0 += genericLoc[f];
    filesWithLanguageDetected += {f};
  }
  
  // then guess languages of other files by their extension
  for (<l, f, a> <- asts, f notin filesWithLanguageDetected) {
  	lang = estimateLanguageByFileExtension(f);
  	if (lang != "") {
  	  result[lang]?0 += genericLoc[f]?0;
  	}
  }
  
  return result;
}


@metric{codeSize}
@doc{The size/volume of a system is telling for the expected complexity and cost of ownership. The type and number of programming languages used also indicate
complexity and the kind of expertise needed to maintain the system. We combine this information with the number of people working on the project because a large
project is expected to be supported by a large community.}
@friendlyName{Code Size}
@appliesTo{generic()}
@uses{("locPerLanguage": "locPerLanguage",
       "org.ossmeter.metricprovider.trans.rascal.activecommitters.projectAge": "projectAge",
       "org.ossmeter.metricprovider.trans.rascal.activecommitters.numberOfActiveCommittersLongTerm": "numberOfActiveCommittersLongTerm")}
Factoid codeSize(
	map[str, int] locPerLanguage = (),
	int projectAge = -1,
	int numberOfActiveCommittersLongTerm = -1
) {
  if (isEmpty(locPerLanguage)) {
    throw undefined("No LOC data available", |unknown:///|);
  }

  // generic() is already removed in locPerLanguage
  lrel[str, int] sorted = sort(toRel(locPerLanguage),
    bool (tuple[str, int] a, tuple[str, int] b) {
    	return a[1] > b[1]; // sort from high to low
    });

  totalSize = ( 0 | it + locPerLanguage[l] | l <- locPerLanguage );

  mainLang = sorted[0];

  txt = "The total size of the code base is <totalSize> physical lines of code. The main development language of the project is <mainLang[0]>, with <mainLang[1]> physical lines of code.";
  if (size(sorted) > 1) {
    otherTxt = intercalate(", ", ["<l[0]> (<l[1]>)" | l <- sorted[1..]]);
  
    txt += " The following <size(sorted) - 1> other languages were recognized: <otherTxt>.";
  }
  
  if (projectAge > 0) {
  	txt += " The age of the code base is <projectAge> days.";
  }

  if (numberOfActiveCommittersLongTerm > 0) {
    txt += " In the last 12 months there have been <numberOfActiveCommittersLongTerm> people working on this project.";
  }

  stars = 1;
  
  if (totalSize < 1000000) { // 1 star if LOC > 1M
    stars += 1;
    
    if (totalSize < 500000) {
      stars += 1;
    }
  
    if (size(locPerLanguage) <= 2) {
      stars += 1;
    }
  }
  
  return factoid(txt, starLookup[stars]); // star rating by language level? weighted by LOC? // http://www.cs.bsu.edu/homepages/dmz/cs697/langtbl.htm	
}
