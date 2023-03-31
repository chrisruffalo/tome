package io.github.chrisruffalo.tome.logging.slf4j;

import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;

public class Slf4jLoggerFactory implements LoggerFactory {


    @Override
    public Logger get(String loggerName) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(loggerName));
    }

    @Override
    public Logger get(Class<?> loggerClass) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(loggerClass));
    }
}
