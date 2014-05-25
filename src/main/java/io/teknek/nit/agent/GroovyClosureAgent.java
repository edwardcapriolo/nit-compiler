package io.teknek.nit.agent;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public class GroovyClosureAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    GroovyShell shell = new GroovyShell();
    Object result = shell.evaluate(nitDesc.getScript());
    if (result instanceof Closure) {
      return result;
    } else {
      throw new NitException("result was not a groovyclosure " + result);
    }
  }

}
