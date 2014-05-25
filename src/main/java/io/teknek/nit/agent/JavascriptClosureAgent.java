package io.teknek.nit.agent;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public class JavascriptClosureAgent implements NitAgent {

  @Override
  public Object createInstance(NitDesc nitDesc) throws NitException {
    Context context = Context.enter();
    Scriptable scope = context.initStandardObjects();
    // scope //source //sourcename //linenumber // security domain
    Function function = context.compileFunction(scope, nitDesc.getScript(), "nit-source", 1, null);
    return function;
  }

}
