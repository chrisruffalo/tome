package io.github.chrisruffalo.tome.core.log;

public class CountingLoggerFactory implements LoggerFactory {

    private static final CountingLogger INSTANCE = new CountingLogger();

    public CountingLogger get() {
        return INSTANCE;
    }

    @Override
    public Logger get(String loggerName) {
        return INSTANCE;
    }

    @Override
    public Logger get(Class<?> loggerClass) {
        return INSTANCE;
    }
}
