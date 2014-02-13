package io.teknek.nit;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Var;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import java.net.URLClassLoader;
import java.nio.charset.Charset;


public class NitFactory {

  public static <T extends Object> T construct(NitDesc nitDesc) throws NitException {
    if (nitDesc.spec == NitDesc.NitSpec.JAVA_ON_JAVA) {
      

      
      //http://stackoverflow.com/questions/12173294/compiling-fully-in-memory-with-javax-tools-javacompiler
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
      StringWriter sw = new StringWriter();
      PrintWriter out = new PrintWriter(sw);
      out.write(nitDesc.getScript());
      out.close();
      JavaFileObject file = new JavaSourceFromString(nitDesc.getTheClass(), sw.toString());
      out.close();
      Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
 
      
      //StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,Locale.ENGLISH,
                                                                //            Charset.defaultCharset());


      CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);
      boolean success = task.call();
      for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
        System.out.println(diagnostic.getCode());
        System.out.println(diagnostic.getKind());
        System.out.println(diagnostic.getPosition());
        System.out.println(diagnostic.getStartPosition());
        System.out.println(diagnostic.getEndPosition());
        System.out.println(diagnostic.getSource());
        System.out.println(diagnostic.getMessage(null));

      }
      if (success){
        Class cl;
        try {
          cl = Class.forName(nitDesc.getTheClass());
          //cl = fileManager.getClassLoader(null).loadClass(nitDesc.getTheClass());
          //cl = compiler.getStandardFileManager(null, null, null).getClassLoader(null).loadClass(nitDesc.getTheClass());
          // Load in the class; Class.childclass should be located in
          // the directory file:/class/demo/
          //cl = loader.loadClass(nitDesc.getTheClass());
          
          
        } catch (ClassNotFoundException e) {
          throw new NitException(e);
        }
        return findMatchingConstructor(nitDesc, cl);
      } else {
        throw new NitException("could not compile");
      }
      
    } else if (nitDesc.spec == NitDesc.NitSpec.JAVASCRIPT_CLOSURE ) {
      Context context = Context.enter();
      Scriptable scope = context.initStandardObjects();
      //scope //source //sourcename //linenumber // security domain
      
      Function function = context.compileFunction(scope, nitDesc.getScript(), "filter", 1, null);
      return (T) function;
    } else if (nitDesc.spec == NitDesc.NitSpec.CLOJURE_CLOSURE ) {
      try {
        RT.load("clojure/core");
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
      }
      Object result =  Compiler.load(new StringReader(nitDesc.getScript()));
      if (result instanceof Var){
        return (T) result;
      } else{
        throw new NitException("result did not compile into a clsure var"+ result);
      }
    } else if (nitDesc.spec == NitDesc.NitSpec.GROOVY_CLOSURE) {
      GroovyShell shell = new GroovyShell();
      Object result = shell.evaluate(nitDesc.getScript());
      if (result instanceof Closure) {
        return (T) result;
      } else {
        throw new NitException("result was not a groovyclosure " + result);
      }
    } else if (nitDesc.spec.equals(NitDesc.NitSpec.GROOVY_CLASS_LOADER)) {
      GroovyClassLoader gc = new GroovyClassLoader();
      //TODO autoclose?
      try {
        Class<?> cl = gc.parseClass(nitDesc.getScript());
        return findMatchingConstructor(nitDesc, cl);
      } finally {
        try {
          gc.close();
        } catch (IOException e) { }
      }
    } else if (nitDesc.spec.equals(NitDesc.NitSpec.JAVA_LOCAL_CLASSPATH)) {
      Class cl = null;
      try {
        cl = Class.forName(nitDesc.getTheClass());
      } catch (ClassNotFoundException e) {
        throw new NitException(e);
      }
      return findMatchingConstructor(nitDesc, cl);
    } else {
      throw new IllegalArgumentException("cant hit this");
    }
  }
  
  protected static <T extends Object> T findMatchingConstructor(NitDesc nitDesc, Class cl) throws NitException{
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

class JavaSourceFromString extends SimpleJavaFileObject {
  final String code;

  JavaSourceFromString(String name, String code) {
    super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
  
}


