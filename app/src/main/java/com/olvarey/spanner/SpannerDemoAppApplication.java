package com.olvarey.spanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Entry point for the Spanner demo application. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SpannerDemoAppApplication {

  /**
   * Starts the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(SpannerDemoAppApplication.class, args);
  }
}
