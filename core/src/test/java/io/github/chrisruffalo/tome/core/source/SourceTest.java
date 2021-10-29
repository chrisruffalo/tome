package io.github.chrisruffalo.tome.core.source;

import org.junit.jupiter.api.Assertions;

import java.util.Optional;

public class SourceTest {

    protected void expectNotFound(final Source source, final String propertyName) {
        final Optional<Value> found = source.get(propertyName);
        found.ifPresent(s -> Assertions.fail(String.format("The source implementation %s should not provide '%s'='%s', expected to not find a value", source.getClass().getSimpleName(), propertyName, s)));
    }

    protected void expect(final Source source, final String propertyName, final String expected) {
        final Optional<Value> found = source.get(propertyName);
        if (!found.isPresent()) {
            Assertions.fail(String.format("The source implementation %s should provide '%s'='%s' but no value was found", source.getClass().getSimpleName(), propertyName, expected));
            return;
        }
        Assertions.assertEquals(expected, found.get().toString(), String.format("The source implementation %s should provide '%s'='%s' but was '%s'", source.getClass().getSimpleName(), propertyName, expected, found.get()));
    }

}
