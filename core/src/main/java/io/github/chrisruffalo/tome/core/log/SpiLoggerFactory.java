package io.github.chrisruffalo.tome.core.log;

import java.util.ServiceLoader;

/**
 * Creates a logger as needed based on the SPI
 */
public class SpiLoggerFactory {

    private static final LoggerFactory DEFAULT = new NoOpLoggerFactory();

    private static volatile LoggerFactory found;

    public static LoggerFactory get() {
        // look up logger factory by spi
        if (found == null) {
            synchronized (DEFAULT) {
                if (found == null) {
                    final ServiceLoader<LoggerFactory> loader = ServiceLoader.load(LoggerFactory.class);
                    for(LoggerFactory logger : loader) {
                        if (found == null && logger != null) {
                            found = logger;
                            found.get(LoggerFactory.class).debug(String.format("Tome is using the %s logger factory for logging", found.getClass().toString()));
                        } else if (found != null && logger != null) {
                            found.get(LoggerFactory.class).warn(String.format("Found more than one log factory instance configured through SPI, %s will not be used", logger.getClass().getName()));
                        }
                    }
                }
            }
        }

        // if there is a found instance return it
        if (found != null) {
            return found;
        }

        // if nothing is found, return default logger
        return DEFAULT;
    }

}
