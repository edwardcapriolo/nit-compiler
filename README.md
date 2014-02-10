nit-compiler
============

Now Is Time Compiler (build gvoory scala and other dynamic classloader goodness from java)

Essentially my go-to-move is to bring groovy into a project for extensability. I wanted to build
a dynamic/reflection based project to use with teknek as well as other projects.


Usage
=====

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
