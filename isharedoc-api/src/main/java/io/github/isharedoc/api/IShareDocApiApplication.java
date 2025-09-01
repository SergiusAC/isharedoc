package io.github.isharedoc.api;

import io.github.isharedoc.api.config.AppRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ImportRuntimeHints;

@ConfigurationPropertiesScan
@ImportRuntimeHints(AppRuntimeHints.class)
@SpringBootApplication
public class IShareDocApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IShareDocApiApplication.class, args);
	}

}
