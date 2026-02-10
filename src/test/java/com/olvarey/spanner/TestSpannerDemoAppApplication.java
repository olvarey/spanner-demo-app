package com.olvarey.spanner;

import org.springframework.boot.SpringApplication;

public class TestSpannerDemoAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpannerDemoAppApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
