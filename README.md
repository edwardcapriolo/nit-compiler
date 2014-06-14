nit-compiler
============

Now Is Time Compiler.

An easy way to leverage interpreted JVM languages from java. As well doing URL classloading, reflection, and hot-loading. NitWraps this up into a single project.

Support for JavaScript, Clojure, Groovy, URL Class loading, and Scala. Up next Jython, JRuby.

Usage
=====

Nit lets you eval a string and compile it into code using one of several interpreted languages.

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



In the long run nit will (probably) do a bunch of "Crazy" stuff like compile java inside java, build jars of java compiled inside java etc.
