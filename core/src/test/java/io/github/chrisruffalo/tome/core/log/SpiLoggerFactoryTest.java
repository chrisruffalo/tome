package io.github.chrisruffalo.tome.core.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpiLoggerFactoryTest {

    @Test
    public void testGetLogger() {
        final LoggerFactory loggerFactory = SpiLoggerFactory.get();
        Assertions.assertInstanceOf(CountingLoggerFactory.class, loggerFactory);
        Assertions.assertInstanceOf(CountingLogger.class, loggerFactory.get(this.getClass()));
    }

}
