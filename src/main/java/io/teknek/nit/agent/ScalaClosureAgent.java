package io.teknek.nit.agent;

import com.twitter.util.Eval;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public class ScalaClosureAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    Eval eval = new Eval();
    return eval.apply(nitDesc.getScript(), true);
  }

}
