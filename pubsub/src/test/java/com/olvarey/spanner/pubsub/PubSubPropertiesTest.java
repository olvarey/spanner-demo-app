package com.olvarey.spanner.pubsub;

import static com.olvarey.spanner.common.config.CommonConfigKeys.APP_PUBSUB_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

/** Tests binding and resolution behavior for {@link PubSubProperties}. */
@SpringBootTest(
    classes = PubSubPropertiesTest.TestConfig.class,
    properties = {
      APP_PUBSUB_PREFIX + ".project-id=test-project",
      APP_PUBSUB_PREFIX + ".subscription-id=notes-sub"
    })
class PubSubPropertiesTest {

  @Autowired private PubSubProperties properties;

  /** Verifies properties bind and resource names resolve correctly. */
  @Test
  void shouldLoadAndBindPubSubProperties() {
    assertThat(properties.projectId()).isEqualTo("test-project");
    assertThat(properties.subscriptionId()).isEqualTo("notes-sub");
    assertThat(properties.subscriptionName())
        .isEqualTo("projects/test-project/subscriptions/notes-sub");
    assertThat(properties.hasSubscription()).isTrue();
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(PubSubProperties.class)
  static class TestConfig {}
}
