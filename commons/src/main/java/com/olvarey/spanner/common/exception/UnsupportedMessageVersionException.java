package com.olvarey.spanner.common.exception;

/** Thrown when a message version is recognized but unsupported. */
public class UnsupportedMessageVersionException extends CommonException {

  /**
   * Creates a new unsupported-version exception.
   *
   * @param message unsupported version details
   */
  public UnsupportedMessageVersionException(String message) {
    super(message);
  }
}
