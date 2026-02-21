package com.olvarey.spanner.common.config;

/** Shared configuration and channel key constants used across modules. */
public final class CommonConfigKeys {

  /** Spring property prefix for Pub/Sub enablement. */
  public static final String SPRING_GCP_PUBSUB_PREFIX = "spring.cloud.gcp.pubsub";

  /** Spring property key for enabling Pub/Sub integration. */
  public static final String PUBSUB_ENABLED_PROPERTY = "enabled";

  /** Application property prefix for Pub/Sub custom settings. */
  public static final String APP_PUBSUB_PREFIX = "app.pubsub";

  /** Spring Integration channel used for Pub/Sub inbound messages. */
  public static final String PUBSUB_INPUT_CHANNEL = "pubsubInputChannel";

  /** Global Spring Integration error channel. */
  public static final String ERROR_CHANNEL = "errorChannel";

  private CommonConfigKeys() {}
}
