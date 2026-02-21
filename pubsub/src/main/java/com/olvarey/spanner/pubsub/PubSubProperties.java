package com.olvarey.spanner.pubsub;

import static com.olvarey.spanner.common.config.CommonConfigKeys.APP_PUBSUB_PREFIX;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties used to resolve Pub/Sub subscription resource names.
 *
 * @param projectId Google Cloud project ID
 * @param subscriptionId Pub/Sub subscription ID or full resource name
 */
@ConfigurationProperties(prefix = APP_PUBSUB_PREFIX)
public record PubSubProperties(String projectId, String subscriptionId) {

  /**
   * Returns whether a subscription identifier is configured.
   *
   * @return {@code true} when a non-blank subscription ID is present
   */
  public boolean hasSubscription() {
    return subscriptionId != null && !subscriptionId.isBlank();
  }

  /**
   * Resolves the subscription as a full Pub/Sub resource name when project ID is available.
   *
   * @return full subscription resource name or raw configured value
   */
  public String subscriptionName() {
    return resolveResourceName(subscriptionId, "subscriptions");
  }

  private String resolveResourceName(String value, String type) {
    if (value == null || value.isBlank()) {
      return "";
    }
    if (value.startsWith("projects/")) {
      return value;
    }
    if (projectId == null || projectId.isBlank()) {
      return value;
    }
    return "projects/" + projectId + "/" + type + "/" + value;
  }
}
