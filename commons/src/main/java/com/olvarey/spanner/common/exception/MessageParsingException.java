package com.olvarey.spanner.common.exception;

/** Thrown when an incoming message cannot be parsed. */
public class MessageParsingException extends CommonException {

  /**
   * Creates a new parsing exception.
   *
   * @param message parsing error message
   * @param cause root cause
   */
  public MessageParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
