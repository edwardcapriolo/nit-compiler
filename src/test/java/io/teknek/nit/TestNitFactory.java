package io.teknek.nit;

import java.net.URL;

import junit.framework.Assert;
import groovy.lang.Closure;

import org.junit.Test;

public class TestNitFactory {

  @Test
  public void constructAClosure() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.GROOVY_CLOSURE);
    n.setScript("{ tuple -> println(tuple); return 1 }");
    Closure c = NitFactory.construct(n);
    Assert.assertEquals(1, c.call("dude"));
  }
  
  
  @Test
  public void constructANoArgCllass() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH);
    n.setTheClass("java.lang.Object");
    Object o = NitFactory.construct(n);
    Assert.assertNotNull(o);
  }
  
  @Test
  public void constuctClassWithConArgs() throws NitException { 
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH);
    n.setTheClass("java.net.URL");
    n.setConstructorArguments( new Object[]{ "http", "teknek.io", "/some/cool/stuff"} );
    URL u = NitFactory.construct(n);
    Assert.assertEquals ("teknek.io", u.getHost());
  }
  
  @Test
  public void constuctClassWithConArgsAndParams() throws NitException { 
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH);
    n.setTheClass("java.net.URL");
    n.setConstructorParameters( new Class [] { String.class, String.class, String.class } );
    n.setConstructorArguments( new Object[]{ "http", "teknek.io", "/some/cool/stuff"} );
    URL u = NitFactory.construct(n);
    Assert.assertEquals ("teknek.io", u.getHost());
  }
  
}
