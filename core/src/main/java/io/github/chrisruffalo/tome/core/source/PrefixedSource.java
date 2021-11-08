package io.github.chrisruffalo.tome.core.source;

import java.util.Optional;

/**
 * Delegates to the names in another source but requires a prefix
 * to be given. The prefix is removed before being passed to the
 * child source.
 */
public class PrefixedSource extends DefaultSource {

    private final String prefix;
    private final Source prefixed;

    public PrefixedSource(final String prefix, Source prefixed) {
        this.prefix = prefix;
        this.prefixed = prefixed;
    }

    @Override
    public Optional<Value> get(String propertyName) {
        // the property name must be longer than the prefix or we will
        // not be able to remove it
        if(propertyName == null || propertyName.length() <= prefix.length()) {
            return Optional.empty();
        }

        // if the property name given starts with the prefix remove
        // the prefix and then continue on to the delegated source
        if (propertyName.startsWith(prefix)) {
            propertyName = propertyName.substring(prefix.length());
            return this.prefixed.get(propertyName);
        }

        return Optional.empty();
    }
}
