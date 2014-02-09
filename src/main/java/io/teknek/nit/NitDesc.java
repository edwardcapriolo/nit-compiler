package io.teknek.nit;

public class NitDesc {
  public enum NitSpec { JAVA_LOCAL_CLASSPATH, JAVA_URL_CLASSLOADER, GROOVY_CLOSURE };
  protected NitSpec spec;
  
  /**
   * If a script is specified it is typically inline code in the form of a string 
   */
  protected String script;
  
  /**
   * Typically the fully qualified name of a a java class
   */
  protected String theClass;

  protected Object[] constructorParameters;
  
  public Object[] getConstructorParameters() {
    return constructorParameters;
  }

  public void setConstructorParameters(Object[] constructorParameters) {
    this.constructorParameters = constructorParameters;
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
