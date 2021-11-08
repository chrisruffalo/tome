package io.github.chrisruffalo.tome.core.log;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;

/**
 * Adapts messages coming from inside the resolver to
 * actual log messages if provided.
 */
public interface Logger {

    void error(String message);

    void error(String message, Exception ex);

    void warn(String message);

    void info(String message);

    void debug(String message);

    void fine(String message);

    default void handle(Message message) {
        // can't handle this
        if (message == null || message.getType() == null) {
            return;
        }
        switch (message.getType()) {
            case FINE:
                this.fine(message.getMessage());
                break;
            case DEBUG:
                this.debug(message.getMessage());
                break;
            case WARN:
                this.warn(message.getMessage());
                break;
            case ERROR:
                if(message.getException() != null) {
                    this.error(message.getMessage(), message.getException());
                } else {
                    this.error(message.getMessage());
                }
                break;
            default:
                this.info(message.getMessage());
        }
    }

}
