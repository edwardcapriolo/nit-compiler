package io.teknek.nit;

import java.net.URL;

import junit.framework.Assert;
import groovy.lang.Closure;

import org.junit.Ignore;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import clojure.lang.Var;

public class TestNitFactory {

  private NitFactory NitFactory = new NitFactory();
  
  @Test
  public void constructAClosure() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.GROOVY_CLOSURE);
    n.setScript("{ tuple -> println(tuple); return 1 }");
    Closure c = NitFactory.construct(n);
    Assert.assertEquals(1, c.call("dude"));
  }
  
  @Test
  public void constructAClojClosure() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.CLOJURE_CLOSURE);
    n.setScript("(ns user) (defn fil [a] (if (= a  \"4\" ) a ))");
    Var v = NitFactory.construct(n);
    Assert.assertEquals("4", v.invoke("4"));
  }
  
  @Test
  public void constructAJavaScript() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVASCRIPT_CLOSURE);
    n.setScript("function over21(row) { if (row > 21) return true; else return false; }");
    Function f = NitFactory.construct(n);
    Context context = Context.enter();
    Scriptable scope = context.initStandardObjects();
    Assert.assertEquals(true, f.call(context, scope, scope, new Object[]{ 22 }));
    Assert.assertEquals(false, f.call(context, scope, scope, new Object[]{ 20 }));
  }
  
  @Test(expected=NullPointerException.class)
  public void constructABadClosureDoesntThrowUntilCalled() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.GROOVY_CLOSURE);
    n.setScript("{ tuple -> println(tuple) xyz ; return 1 }");
    Closure c = NitFactory.construct(n);
    Assert.assertEquals(1, c.call("dude"));
  } 
  
  @Test(expected=groovy.lang.MissingPropertyException.class)
  public void constructABadClosureDoesntThrow() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.GROOVY_CLOSURE);
    n.setScript("{ tuple -> println(tupled) xyz ; return 1 }");
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
    n.setConstructorArguments(new Object[] { "http", "teknek.io", "/some/cool/stuff" });
    URL u = NitFactory.construct(n);
    Assert.assertEquals("teknek.io", u.getHost());
  }
  
  @Test
  public void constuctClassWithConArgsAndParams() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH);
    n.setTheClass("java.net.URL");
    n.setConstructorParameters(new Class[] { String.class, String.class, String.class });
    n.setConstructorArguments(new Object[] { "http", "teknek.io", "/some/cool/stuff" });
    URL u = NitFactory.construct(n);
    Assert.assertEquals("teknek.io", u.getHost());
  }
  
  @Test
  public void groovyClassLoader() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.GROOVY_CLASS_LOADER);
    n.setScript(" public class A { \n"+
            "int x; \n"+
            "} ");
    Object o = NitFactory.construct(n);
    Assert.assertEquals("A", o.getClass().getName());
  }
  
  //Dont work in java 7 WTF
  @Ignore
  public void javaInJava() throws NitException {
    NitDesc n = new NitDesc();
    n.setSpec(NitDesc.NitSpec.JAVA_ON_JAVA);
    n.setScript(" public class B { \n"+
            "int x; \n"+
            "} ");
    n.setTheClass("B");
    Object o = NitFactory.construct(n);
    Assert.assertEquals("B", o.getClass().getName());
  }
}
