package io.github.chrisruffalo.tome.core.source;

import java.util.Map;
import java.util.Optional;

/**
 * Uses Java Property objects as a source for property values
 */
public class MapSource extends DefaultSource {

    private final Map<String, String> source;

    public MapSource(final Map<String, String> given) {
        this.source = given;
    }

    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {
        if(this.source == null || propertyName == null || propertyName.isEmpty() || !this.source.containsKey(propertyName)) {
            return Optional.empty();
        }

        // return the source value
        return Optional.of(new Value(this.source.get(propertyName)));
    }
}
