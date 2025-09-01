package io.github.isharedoc.api.config;

import io.github.isharedoc.api.event.*;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.serialization().registerType(EventRequest.class);
        hints.serialization().registerType(EventSourceRecord.class);
        hints.serialization().registerType(SqsEventRecord.class);
        hints.serialization().registerType(DynamoStreamEventRecord.class);
        hints.serialization().registerType(DeleteFileMetadataEvent.class);
        hints.reflection().registerType(DynamoStreamEventRecord.class, TypeHint.Builder::withMembers);
        hints.reflection().registerType(DynamoStreamEventRecord.DynamoEventData.class, TypeHint.Builder::withMembers);
    }

}
