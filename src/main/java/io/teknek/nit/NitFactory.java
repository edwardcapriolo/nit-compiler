package io.teknek.nit;

import java.lang.reflect.InvocationTargetException;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;

public class NitFactory {

  public static <T extends Object> T construct(NitDesc nitDesc) throws NitException{
    if (nitDesc.spec == NitDesc.NitSpec.GROOVY_CLOSURE){
      GroovyShell shell = new GroovyShell();
      Object result = shell.evaluate(nitDesc.getScript());
      if (result instanceof Closure){
        return (T) result;
      } else {
        throw new NitException("result was not a groovyclosure "+result);
      }
    } if (nitDesc.spec.equals(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH)){
      Class cl = null;
      try {
        cl = Class.forName(nitDesc.getTheClass());
      } catch (ClassNotFoundException e) {
        throw new NitException(e);
      }
      Class [] params = nitDesc.getConstructorParameters() == null ? new Class[0]: new Class[nitDesc.getConstructorParameters().length];
      if (params.length == 0){
        try {
          return (T) cl.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new NitException(e);
        }
      } else {
        for (int j =0;j> nitDesc.getConstructorParameters().length;j++){
          params[j] = nitDesc.getConstructorParameters()[j] == null ? null :nitDesc.getConstructorParameters()[j].getClass(); 
        }
        try {
          return (T) cl.getConstructor(params).newInstance(nitDesc.getConstructorParameters());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          throw new NitException(e);
        }
      }
    } else {
      throw new IllegalArgumentException("cant hit this");
    }
  }
}
