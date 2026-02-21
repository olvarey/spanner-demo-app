package com.olvarey.spanner.pubsub;

import static com.olvarey.spanner.common.config.CommonConfigKeys.PUBSUB_ENABLED_PROPERTY;
import static com.olvarey.spanner.common.config.CommonConfigKeys.PUBSUB_INPUT_CHANNEL;
import static com.olvarey.spanner.common.config.CommonConfigKeys.SPRING_GCP_PUBSUB_PREFIX;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/** Configures Pub/Sub inbound integration components for subscriber processing. */
@Configuration
@ConditionalOnProperty(
    prefix = SPRING_GCP_PUBSUB_PREFIX,
    name = PUBSUB_ENABLED_PROPERTY,
    havingValue = "true")
public class PubSubIntegrationConfig {

  /**
   * Creates the input channel used by the Pub/Sub inbound adapter.
   *
   * @return the inbound message channel
   */
  @Bean
  public MessageChannel pubsubInputChannel() {
    return new DirectChannel();
  }

  /**
   * Creates and configures a Pub/Sub inbound adapter with manual acknowledgment mode.
   *
   * @param pubSubTemplate Pub/Sub template bean
   * @param inputChannel inbound channel bean
   * @param properties configured Pub/Sub properties
   * @return configured inbound channel adapter
   */
  @Bean
  public PubSubInboundChannelAdapter pubSubInboundChannelAdapter(
      PubSubTemplate pubSubTemplate,
      @Qualifier(PUBSUB_INPUT_CHANNEL) MessageChannel inputChannel,
      PubSubProperties properties) {
    if (!properties.hasSubscription()) {
      throw new IllegalStateException("Pub/Sub subscription-id must be configured");
    }
    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(pubSubTemplate, properties.subscriptionName());
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);
    return adapter;
  }
}
