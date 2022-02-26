package io.github.chrisruffalo.tome.core.log;

import java.util.ServiceLoader;

/**
 * Creates a logger as needed based on the SPI
 */
public class LoggerFactory {

    private static final Logger DEFAULT = new NoOpLogger();

    private static volatile Logger found;

    public static Logger get() {
        // look up logger by spi
        if (found == null) {
            synchronized (DEFAULT) {
                if (found == null) {
                    final ServiceLoader<Logger> loader = ServiceLoader.load(Logger.class);
                    for(Logger logger : loader) {
                        if (found == null && logger != null) {
                            found = logger;
                            found.debug(String.format("Tome is using the %s logger for logging", found.getClass().toString()));
                        } else if (found != null && logger != null) {
                            found.warn(String.format("Found more than one log instance configured through SPI, %s will not be used", logger.getClass().getName()));
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
