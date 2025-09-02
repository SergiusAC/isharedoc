package io.github.isharedoc.api.config;

import io.github.isharedoc.api.event.DynamoStreamEventRecord;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({DynamoStreamEventRecord.EventData.class})
public class ReflectionConfig {
}
