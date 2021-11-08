package io.github.chrisruffalo.tome.core.source;

import io.github.chrisruffalo.tome.core.source.transformers.PropertyTransformer;

import java.util.Optional;
import java.util.function.Function;

/**
 * Allows a convenient way to transform property names before sending on to another source. This
 * would be used, for example, to turn a property request like "java.io.tmpdir" into "JAVA_IO_TMPDIR"
 * so that it could be found among traditional environment variables.
 *
 * This is just an extension point for customization.
 */
public class PropertyTransformingSource extends DefaultSource {

    private final PropertyTransformer transformer;

    private final Source source;

    public PropertyTransformingSource(Source source, PropertyTransformer transformer) {
        this.source = source;
        this.transformer = transformer;
    }

    @Override
    public Optional<Value> get(String propertyName) {
        // use the transformer to transform the value so it can be used
        Optional<String> transformed = transformer.apply(propertyName);
        // if the transformer fails to transform a property name or
        // otherwise fails to crate output it will be considered as
        // filtering out / discarding the request and it will return
        // empty
        return transformed.flatMap(source::get);
    }
}
