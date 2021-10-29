package io.github.chrisruffalo.tome.core.message;

/**
 * A message from the resolver that contains any information, warnings, or debugging information
 * that happened during the resolution result.
 */
public class Message {

    private final Type type;

    private final String message;

    private Exception exception = null;

    public Message(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(Type type, String messagef, Object... parameters) {
        this.type = type;
        this.message = String.format(messagef, parameters);
    }

    public Message(String message, Exception ex) {
        this(Type.ERROR, message);
        this.exception = ex;
    }

    public Message(String messagef, Exception ex, Object... parameters) {
        this(String.format(messagef, parameters), ex);
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
