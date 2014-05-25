package io.teknek.nit;

import io.teknek.nit.agent.ClojureClosureAgent;
import io.teknek.nit.agent.GroovyClasspathAgent;
import io.teknek.nit.agent.GroovyClosureAgent;
import io.teknek.nit.agent.JavaLocalClasspathAgent;
import io.teknek.nit.agent.JavaOnJavaAgent;
import io.teknek.nit.agent.JavascriptClosureAgent;
import io.teknek.nit.agent.NitAgent;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class NitFactory {

  private EnumMap<NitDesc.NitSpec,NitAgent> agents = new EnumMap<NitDesc.NitSpec,NitAgent>(NitDesc.NitSpec.class);
  
  public <T extends Object> T construct(NitDesc nitDesc) throws NitException {
    NitAgent agent = agents.get(nitDesc.spec);
    if (agent == null){
      if (nitDesc.spec == NitDesc.NitSpec.GROOVY_CLASS_LOADER){
        agent = new GroovyClasspathAgent();
      } else if (nitDesc.spec == NitDesc.NitSpec.JAVASCRIPT_CLOSURE ) {
        agent = new JavascriptClosureAgent();
      } else if (nitDesc.spec == NitDesc.NitSpec.CLOJURE_CLOSURE) {
        agent = new ClojureClosureAgent();
      } else if (nitDesc.spec == NitDesc.NitSpec.GROOVY_CLOSURE) {
        agent = new GroovyClosureAgent();
      } else if (nitDesc.spec == NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH){
        agent = new JavaLocalClasspathAgent();
      } else if (nitDesc.spec == NitDesc.NitSpec.JAVA_ON_JAVA){ 
        agent = new JavaOnJavaAgent();
      } else {
        throw new NitException(nitDesc.spec + " not found");
      }
      agents.put(nitDesc.spec, agent);
    }
    return (T) agent.createInstance(nitDesc);
  }
  
  public static <T extends Object> T findMatchingConstructor(NitDesc nitDesc, Class cl) throws NitException{
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
    }
    throw new NitException("Bad");
  }
    
}


