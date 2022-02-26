package io.github.chrisruffalo.tome.core.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoggerFactoryTest {

    @Test
    public void testGetLogger() {
        final Logger logger = LoggerFactory.get();
        Assertions.assertInstanceOf(CountingLogger.class, logger);
    }

}
