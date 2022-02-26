package io.github.chrisruffalo.tome.core.source;

import java.util.Optional;

/**
 * A source is a type that allows the property resolver to access named properties
 * on an item. This could apply to anything that has a named structure for accessing
 * properties and sub-properties.
 *
 */
public interface Source {

    /**
     * All sources must implement a method that turns a property name into a value. The
     * returned value can contain tokens to be further resolved by the resolver.
     *
     * A source should return an empty Optional to represent not finding a value. A Value
     * may contain a null object which will resolve as the value implementation dictates. A null
     * Value instance should never be presented.
     *
     *
     *
     * @param sourceContext the context that the source request is being made under
     * @param propertyName the (non-null, non-empty) name of the property to return (not resolve) from the source
     * @return either an empty optional for not found or a Value instance containing the value found in the source
     */
    Optional<Value> get(SourceContext sourceContext, final String propertyName);

}
