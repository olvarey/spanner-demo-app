package com.olvarey.spanner.common.exception;

/** Thrown when an incoming message fails validation rules. */
public class MessageValidationException extends CommonException {

  /**
   * Creates a new validation exception.
   *
   * @param message validation message
   */
  public MessageValidationException(String message) {
    super(message);
  }
}
