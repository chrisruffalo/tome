package io.github.chrisruffalo.tome.core.log;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoggerTest {

    @Test
    public void testLogger() {
        final CountingLogger logger = new CountingLogger();

        // test regular logging
        logger.fine("fine");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.error("error", new Exception("error"));

        Assertions.assertEquals(1, logger.getFine());
        Assertions.assertEquals(1, logger.getDebug());
        Assertions.assertEquals(1, logger.getInfo());
        Assertions.assertEquals(1, logger.getWarn());
        Assertions.assertEquals(2, logger.getError());
        Assertions.assertEquals(1, logger.getExceptions());

        logger.clear();

        // test handle switch
        logger.handle(new Message(Type.FINE, "handle fine"));
        logger.handle(new Message(Type.DEBUG, "handle debug"));
        logger.handle(new Message(Type.INFO, "handle info"));
        logger.handle(new Message(Type.WARN, "handle warn"));
        logger.handle(new Message(Type.ERROR, "handle error"));
        logger.handle(new Message("handle error with exception", new Exception("handled exception")));

        Assertions.assertEquals(1, logger.getFine());
        Assertions.assertEquals(1, logger.getDebug());
        Assertions.assertEquals(1, logger.getInfo());
        Assertions.assertEquals(1, logger.getWarn());
        Assertions.assertEquals(2, logger.getError());
        Assertions.assertEquals(1, logger.getExceptions());


    }

}
