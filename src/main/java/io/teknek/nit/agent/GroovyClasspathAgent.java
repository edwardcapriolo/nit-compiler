package io.teknek.nit.agent;

import java.io.IOException;

import groovy.lang.GroovyClassLoader;
import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;
import io.teknek.nit.NitFactory;

public class GroovyClasspathAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    GroovyClassLoader gc = new GroovyClassLoader();
    try {
      Class<?> cl = gc.parseClass(nitDesc.getScript());
      return NitFactory.findMatchingConstructor(nitDesc, cl);
    } finally {
      try {
        gc.close();
      } catch (IOException e) { }
    }
  }

}
