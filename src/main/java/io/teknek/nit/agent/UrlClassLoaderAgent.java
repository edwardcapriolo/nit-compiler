package io.teknek.nit.agent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public class UrlClassLoaderAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    List<URL> urls = parseSpecIntoUrlList(nitDesc.getScript().toString());
    try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]))) {
      Class<?> c = loader.loadClass(nitDesc.getTheClass());
      return c.newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
      throw new NitException(e);
    }
  }
  
  private static List<URL> parseSpecIntoUrlList(String spec){
    String [] split = spec.split(",");
    List<URL> urls = new ArrayList<URL>();
    for (String s: split){
      try {
        URL u = new URL(s);
        urls.add(u);
      } catch (MalformedURLException e) { 
        throw new RuntimeException(e);
      }
    }
    return urls;
  }

}
