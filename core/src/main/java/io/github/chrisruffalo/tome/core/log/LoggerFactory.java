package io.github.chrisruffalo.tome.core.log;

public interface LoggerFactory {

    Logger get(final String loggerName);

    Logger get(final Class<?> loggerClass);

}
