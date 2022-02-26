package io.github.chrisruffalo.tome.logging.slf4j;

import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.log.SpiLoggerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpiLoggerTest {

    @Test
    public void testSpiCreation() {
        final LoggerFactory loggerFactory = SpiLoggerFactory.get();
        Assertions.assertInstanceOf(Slf4jLoggerFactory.class, loggerFactory);
        final Logger logger = loggerFactory.get(this.getClass());
        Assertions.assertInstanceOf(Slf4jLogger.class, logger);

        // test
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.debug("debug");
        logger.fine("fine");
    }

}
