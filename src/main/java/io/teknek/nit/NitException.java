package io.teknek.nit;

public class NitException extends Exception{

  public NitException() {
    super();
  }

  public NitException(String message, Throwable cause, boolean enableSuppression,
          boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NitException(String message, Throwable cause) {
    super(message, cause);
  }

  public NitException(String message) {
    super(message);
  }

  public NitException(Throwable cause) {
    super(cause);
  }

}
