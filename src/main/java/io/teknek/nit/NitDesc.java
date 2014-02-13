package io.teknek.nit;

public class NitDesc {
  public enum NitSpec { JAVA_LOCAL_CLASSPATH, JAVA_URL_CLASSLOADER, GROOVY_CLOSURE, GROOVY_CLASS_LOADER, CLOJURE_CLOSURE };
  protected NitSpec spec;
  
  /**
   * If a script is specified it is typically inline code in the form of a string 
   */
  protected String script;
  
  /**
   * Typically the fully qualified name of a a java class
   */
  protected String theClass;

  protected Class [] constructorParameters;
  
  protected Object [] constructorArguments;
  
  public Class[] getConstructorParameters() {
    return constructorParameters;
  }

  public void setConstructorParameters(Class[] constructorParameters) {
    this.constructorParameters = constructorParameters;
  }

  public Object [] getConstructorArguments() {
    return constructorArguments;
  }

  public void setConstructorArguments(Object[] constructorArguments) {
    this.constructorArguments = constructorArguments;
  }

  public NitSpec getSpec() {
    return spec;
  }

  public void setSpec(NitSpec spec) {
    this.spec = spec;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public String getTheClass() {
    return theClass;
  }

  public void setTheClass(String theClass) {
    this.theClass = theClass;
  }

  
  
}
