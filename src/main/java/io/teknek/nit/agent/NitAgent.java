package io.teknek.nit.agent;

import io.teknek.nit.NitDesc;
import io.teknek.nit.NitException;

public interface NitAgent {
  public Object createInstance(NitDesc NitDesc) throws NitException;
}
