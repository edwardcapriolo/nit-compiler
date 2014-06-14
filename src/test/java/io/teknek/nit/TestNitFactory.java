package io.teknek.nit;

import io.teknek.nit.NitDesc.NitSpec;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import junit.framework.Assert;
import groovy.lang.Closure;

import org.junit.Ignore;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import scala.collection.mutable.Map;

import clojure.lang.Var;

public class TestNitFactory {

  private NitFactory NitFactory = new NitFactory();
  
  @Test
  public void urlConstruct() throws NitException, MalformedURLException{
    NitDesc n = new NitDesc();
    String cname = "io.teknek.nit.TestObject";
    n.setSpec(NitDesc.NitSpec.JAVA_URL_CLASSLOADER);
    n.setTheClass(cname);
    File f = new File("src/test/resources/test-url.jar");
    Assert.assertTrue(f.exists());
    n.setScript(f.toURL().toString());
    Object oo = NitFactory.construct(n);
    Assert.assertEquals(cname, oo.getClass().getName());
  }
  
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
  
  @Test
  public void scala() throws NitException{
    NitDesc n = new NitDesc();
    n.setSpec(NitSpec.SCALA_CLOSURE);
    n.setScript("import scala.collection.mutable.Map;"+
    "{ (row: Map[String, Any]) => row.get(\"value\").collect { case v: Int if v > 21 => row } }");
    Object o = NitFactory.construct(n);
    Assert.assertTrue (o instanceof scala.Function1);
    scala.Function1 f = (scala.Function1) o;
    java.util.Map<String,Integer> m =  new java.util.HashMap<String,Integer>();
    m.put("value", 22);
    Object result = f.apply(scala.collection.JavaConversions.mapAsScalaMap(m));
    Assert.assertEquals("Some", result.getClass().getSimpleName());
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
