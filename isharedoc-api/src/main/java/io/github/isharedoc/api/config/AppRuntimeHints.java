package io.github.isharedoc.api.config;

import io.github.isharedoc.api.event.DeleteFileMetadataEvent;
import io.github.isharedoc.api.event.DynamoStreamEventRecord;
import io.github.isharedoc.api.event.EventSourceRecord;
import io.github.isharedoc.api.event.SqsEventRecord;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.serialization().registerType(EventSourceRecord.class);
        hints.serialization().registerType(SqsEventRecord.class);
        hints.serialization().registerType(DynamoStreamEventRecord.class);
        hints.serialization().registerType(DeleteFileMetadataEvent.class);
    }

}
