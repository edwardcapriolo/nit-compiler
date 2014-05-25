package io.teknek.nit.agent;

import java.io.IOException;
import java.io.StringReader;

import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Var;
import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public class ClojureClosureAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    try {
      RT.load("clojure/core");
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
    Object result =  Compiler.load(new StringReader(nitDesc.getScript()));
    if (result instanceof Var){
      return result;
    } else{
      throw new NitException("result did not compile into a clsure var"+ result);
    }
  }
  
}
