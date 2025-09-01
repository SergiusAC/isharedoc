package io.github.isharedoc.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProps(
    String awsBucketName,
    String awsSqsUrl,
    String awsTableName
) {}
