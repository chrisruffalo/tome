package io.github.chrisruffalo.tome.core.log;


import org.slf4j.LoggerFactory;

public class CountingLogger implements Logger {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CountingLogger.class);

    private int error = 0;
    private int exceptions = 0;
    private int warn = 0;
    private int info = 0;
    private int debug = 0;
    private int fine = 0;

    public void clear() {
        this.error = 0;
        this.exceptions = 0;
        this.warn = 0;
        this.info = 0;
        this.debug = 0;
        this.fine = 0;
    }

    @Override
    public void error(String message) {
        this.error++;
        logger.error(message);
    }

    @Override
    public void error(String message, Exception ex) {
        this.error++;
        this.exceptions++;
        this.logger.error(message, ex);
    }

    @Override
    public void warn(String message) {
        this.warn++;
        this.logger.warn(message);
    }

    @Override
    public void info(String message) {
        this.info++;
        this.logger.info(message);
    }

    @Override
    public void debug(String message) {
        this.debug++;
        this.logger.debug(message);
    }

    @Override
    public void fine(String message) {
        this.fine++;
        this.logger.trace(message);
    }

    public int getError() {
        return error;
    }

    public int getWarn() {
        return warn;
    }

    public int getInfo() {
        return info;
    }

    public int getDebug() {
        return debug;
    }

    public int getFine() {
        return fine;
    }

    public int getExceptions() {
        return exceptions;
    }
}
