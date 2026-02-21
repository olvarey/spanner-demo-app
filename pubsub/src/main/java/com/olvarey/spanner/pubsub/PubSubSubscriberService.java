package com.olvarey.spanner.pubsub;

import static com.olvarey.spanner.common.config.CommonConfigKeys.PUBSUB_ENABLED_PROPERTY;
import static com.olvarey.spanner.common.config.CommonConfigKeys.PUBSUB_INPUT_CHANNEL;
import static com.olvarey.spanner.common.config.CommonConfigKeys.SPRING_GCP_PUBSUB_PREFIX;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.olvarey.spanner.common.exception.CommonException;
import com.olvarey.spanner.iso20022.application.Pacs008MessageService;
import com.olvarey.spanner.iso20022.domain.Pacs008MessageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/** Consumes Pub/Sub messages, parses pacs.008 content, and logs extracted data. */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = SPRING_GCP_PUBSUB_PREFIX,
    name = PUBSUB_ENABLED_PROPERTY,
    havingValue = "true")
public class PubSubSubscriberService {

  private final Pacs008MessageService pacs008MessageService;

  /**
   * Processes messages from the Pub/Sub input channel and acknowledges them.
   *
   * @param payload message payload as XML text
   * @param originalMessage original Pub/Sub message metadata for acknowledgment
   */
  @ServiceActivator(inputChannel = PUBSUB_INPUT_CHANNEL)
  public void consume(
      String payload,
      @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE)
          BasicAcknowledgeablePubsubMessage originalMessage) {
    String pubSubMessageId = originalMessage.getPubsubMessage().getMessageId();
    try {
      Pacs008MessageData data = pacs008MessageService.parse(payload);
      log.info(
          "Received pacs.008 message pubSubMessageId={} transactionIdAttribute={} msgId={} txId={}"
              + " endToEndId={} amount={} currency={} numberOfTransactions={} parsedFields={}",
          pubSubMessageId,
          data.transactionIdAttribute(),
          data.messageId(),
          data.txId(),
          data.endToEndId(),
          data.amount(),
          data.currency(),
          data.numberOfTransactions(),
          data.parsedFields());
    } catch (CommonException ex) {
      log.error(
          "Invalid pacs.008 payload pubSubMessageId={} reason={}",
          pubSubMessageId,
          ex.getMessage());
    } catch (RuntimeException ex) {
      log.error(
          "Unexpected error processing pubSubMessageId={} reason={}",
          pubSubMessageId,
          ex.getMessage(),
          ex);
    } finally {
      originalMessage.ack();
    }
  }
}
