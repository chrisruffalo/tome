package io.github.chrisruffalo.tome.core.log;

/**
 * A logger with no implementation
 */
public class NoOpLogger implements Logger {

    @Override
    public void error(String message) {
        // no-op
    }

    @Override
    public void error(String message, Exception ex) {
        // no-op
    }

    @Override
    public void warn(String message) {
        // no-op
    }

    @Override
    public void info(String message) {
        // no-op
    }

    @Override
    public void debug(String message) {
        // no-op
    }

    @Override
    public void fine(String message) {
        // no-op
    }
}
