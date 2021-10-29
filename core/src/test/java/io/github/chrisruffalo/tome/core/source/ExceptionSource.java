package io.github.chrisruffalo.tome.core.source;

import java.util.Optional;

/**
 * Throws an exception when you use it, for testing.
 */
public class ExceptionSource implements Source {

    @Override
    public Optional<Value> get(String propertyName) {
        throw new RuntimeException("Test exception");
    }
}
