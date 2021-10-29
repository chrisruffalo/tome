package io.github.chrisruffalo.tome.core.source;

/**
 * A found value returned from a property source. This is used as a non-null
 * envelope that represents the found value which may be null. The original
 * value is preserved and the toString() method is used to return a non-null
 * string value.
 */
public class Value {

    private final Object value;

    public Value(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (this.value instanceof String) {
            return (String) this.value;
        }
        return String.valueOf(value);
    }
}
