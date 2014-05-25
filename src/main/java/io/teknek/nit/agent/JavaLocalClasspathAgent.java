package io.teknek.nit.agent;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;
import io.teknek.nit.NitFactory;

public class JavaLocalClasspathAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    Class cl = null;
    try {
      cl = Class.forName(nitDesc.getTheClass());
    } catch (ClassNotFoundException e) {
      throw new NitException(e);
    }
    return NitFactory.findMatchingConstructor(nitDesc, cl);
  }

}
