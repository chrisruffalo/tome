package io.github.chrisruffalo.tome.core.log;

public class NoOpLoggerFactory implements LoggerFactory {


    @Override
    public Logger get(String loggerName) {
        return new NoOpLogger();
    }

    @Override
    public Logger get(Class<?> loggerClass) {
        return new NoOpLogger();
    }
}
