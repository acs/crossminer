module mood::MHF

import Set;
import util::Math;

/*
 *  Method Hiding Factor (can also be used for Attribute Hiding Factor)
 */
 
real MHF(rel[loc class, loc method] containment, rel[loc class, loc method] visible, set[loc] allClasses) {
  numClasses = size(allClasses);
  numMethods = size(containment);

  MVF = size(visible - containment) / toReal(numMethods * (numClasses - 1)); // num visibility edges / max visibility edges

  return 1 - MVF;
} 

/*
num MHF(M3 model) {
  set[loc] classes = { c | c <- model@declarations<0>, isClass(c) };
  rel[loc, loc] packageContainment = { <p, c> | <p, c> <- model@containment, isPackage(p), isClass(c) };
  
  num numerator = 0;
  num denominator = 0;
  num TC = size(classes);
  
  for (<c, m> <- model@containment, isClass(c), isMethod(m)) {
    if (\public() in model@modifiers[m]) {
      numerator += 0;
    } else if (\protected() in model@modifiers[m]) {
      numerator += 1 - (size(((model@extends+)<1,0>)[c]) / (TC - 1));
    } else if (\private() in model@modifiers[m]) {
      numerator += 1;
    } else if (hasPackageModifier(model, m)) {
      numerator += 1 - (size((packageContainment<1,0> o packageContainment)[c]) / (TC - 1));
    }
    denominator += 1;
  }
  
  return numerator/denominator;
}*/