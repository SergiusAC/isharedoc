package io.github.isharedoc.api.config;

import io.github.isharedoc.api.event.*;
import lombok.SneakyThrows;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @SneakyThrows
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.serialization().registerType(EventRequest.class);
        hints.serialization().registerType(EventSourceRecord.class);
        hints.serialization().registerType(SqsEventRecord.class);
        hints.serialization().registerType(DynamoStreamEventRecord.class);
        hints.serialization().registerType(DeleteFileMetadataEvent.class);
    }

}
