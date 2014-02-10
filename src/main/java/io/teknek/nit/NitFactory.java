package io.teknek.nit;

import java.lang.reflect.InvocationTargetException;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;

public class NitFactory {

  public static <T extends Object> T construct(NitDesc nitDesc) throws NitException {
    if (nitDesc.spec == NitDesc.NitSpec.GROOVY_CLOSURE) {
      GroovyShell shell = new GroovyShell();
      Object result = shell.evaluate(nitDesc.getScript());
      if (result instanceof Closure) {
        return (T) result;
      } else {
        throw new NitException("result was not a groovyclosure " + result);
      }
    }
    if (nitDesc.spec.equals(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH)) {
      Class cl = null;
      try {
        cl = Class.forName(nitDesc.getTheClass());
      } catch (ClassNotFoundException e) {
        throw new NitException(e);
      }
      if (nitDesc.constructorArguments == null && nitDesc.getConstructorParameters() == null) {
        try {
          return (T) cl.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new NitException(e);
        }
      } else if (nitDesc.constructorArguments != null && nitDesc.getConstructorParameters() != null) {
        try {
          return (T) cl.getConstructor(nitDesc.getConstructorParameters()).newInstance(
                  nitDesc.getConstructorArguments());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          throw new NitException(e);
        }
      } else if (nitDesc.constructorArguments != null && nitDesc.getConstructorParameters() == null) {
        Class[] params = new Class[nitDesc.constructorArguments.length];
        for (int j = 0; j < nitDesc.getConstructorArguments().length; j++) {
          params[j] = nitDesc.getConstructorArguments()[j] == null ? null : nitDesc
                  .getConstructorArguments()[j].getClass();
        }
        try {
          return (T) cl.getConstructor(params).newInstance(
                  nitDesc.getConstructorArguments());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          throw new NitException(e);
        }
      } else {
        throw new NitException("did not supply enough information");
      }
    } else {
      throw new IllegalArgumentException("cant hit this");
    }
  }
}
