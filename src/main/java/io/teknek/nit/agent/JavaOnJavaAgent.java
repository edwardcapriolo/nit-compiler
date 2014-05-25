package io.teknek.nit.agent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;
import io.teknek.nit.NitFactory;

public class JavaOnJavaAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {

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
      return NitFactory.findMatchingConstructor(nitDesc, cl);
    } else {
      throw new NitException("could not compile");
    }

  }

}
