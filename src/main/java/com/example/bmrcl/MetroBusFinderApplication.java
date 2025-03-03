package com.example.bmrcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetroBusFinderApplication {
	public static void main(String[] args) {
		SpringApplication.run(WavefrontProperties.Application.class, args);
	}
}