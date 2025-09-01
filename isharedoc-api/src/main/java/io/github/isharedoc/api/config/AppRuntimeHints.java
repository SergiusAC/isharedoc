package io.github.isharedoc.api.config;

import io.github.isharedoc.api.event.DeleteFileMetadataEvent;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.serialization().registerType(DeleteFileMetadataEvent.class);
    }

}
