package io.github.chrisruffalo.tome.core.source;

import java.util.Optional;
import java.util.Properties;

/**
 * Uses Java Property objects as a source for property values
 */
public class PropertySource extends DefaultSource {

    private final Properties source;

    public PropertySource(final Properties given) {
        this.source = given;
    }

    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {
        if(this.source == null || propertyName == null || propertyName.isEmpty() || !this.source.containsKey(propertyName)) {
            return Optional.empty();
        }

        // return the source value
        return Optional.of(new Value(this.source.getProperty(propertyName)));
    }
}
