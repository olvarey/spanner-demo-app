package com.olvarey.spanner.common.exception;

/** Base runtime exception for application-level failures. */
public class CommonException extends RuntimeException {

  /**
   * Creates a new exception with a message.
   *
   * @param message exception message
   */
  public CommonException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with a message and cause.
   *
   * @param message exception message
   * @param cause root cause
   */
  public CommonException(String message, Throwable cause) {
    super(message, cause);
  }
}
