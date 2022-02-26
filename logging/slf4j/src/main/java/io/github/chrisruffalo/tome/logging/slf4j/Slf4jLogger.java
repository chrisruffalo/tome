package io.github.chrisruffalo.tome.logging.slf4j;

import io.github.chrisruffalo.tome.core.log.Logger;

public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger delegate;

    public Slf4jLogger(final org.slf4j.Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public void error(String message) {
        this.delegate.error(message);
    }

    @Override
    public void error(String message, Exception ex) {
        this.delegate.error(message, ex);
    }

    @Override
    public void warn(String message) {
        this.delegate.warn(message);
    }

    @Override
    public void info(String message) {
       this.delegate.info(message);
    }

    @Override
    public void debug(String message) {
        this.delegate.debug(message);
    }

    @Override
    public void fine(String message) {
        this.delegate.trace(message);
    }
}
