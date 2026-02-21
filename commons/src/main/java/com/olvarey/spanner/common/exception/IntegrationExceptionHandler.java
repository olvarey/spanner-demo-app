package com.olvarey.spanner.common.exception;

import static com.olvarey.spanner.common.config.CommonConfigKeys.ERROR_CHANNEL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/** Handles unprocessed messages from Spring Integration's global {@code errorChannel}. */
@Slf4j
@Component
public class IntegrationExceptionHandler {

  /**
   * Logs integration failures routed to the global error channel.
   *
   * @param errorMessage Spring Integration error message
   */
  @ServiceActivator(inputChannel = ERROR_CHANNEL)
  public void handle(Message<?> errorMessage) {
    Throwable throwable = resolveThrowable(errorMessage.getPayload());
    if (throwable != null) {
      log.error(
          "Unhandled integration error type={} message={}",
          throwable.getClass().getSimpleName(),
          throwable.getMessage(),
          throwable);
      return;
    }

    Object payload = errorMessage.getPayload();
    log.error(
        "Unhandled integration error payloadType={} payload={}",
        payload == null ? "null" : payload.getClass().getName(),
        payload);
  }

  private Throwable resolveThrowable(Object payload) {
    if (!(payload instanceof Throwable throwable)) {
      return null;
    }
    if (throwable instanceof MessagingException messagingException
        && messagingException.getCause() != null) {
      return messagingException.getCause();
    }
    return throwable;
  }
}
