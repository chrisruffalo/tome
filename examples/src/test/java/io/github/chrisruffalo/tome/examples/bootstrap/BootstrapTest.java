package io.github.chrisruffalo.tome.examples.bootstrap;

import io.github.chrisruffalo.tome.core.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BootstrapTest {

    @Test
    public void testBootstrap() {
        final Bootstrap bootstrap = new Bootstrap();
        final Configuration configuration = bootstrap.load();

        // assert that the configuration loaded the chained application.properties
        Assertions.assertEquals("admin:admin@localhost:3306", configuration.format("${db.user}:${db.pass}@${db.host}:${db.port}"));

        // assert that the system property is overridden by the higher precedent by showing it is equal to what is in the file (and not equal to what is in system properties)
        // by using the "orElse" on the optional we also show that it is present and a value was found in the configuration
        final String tmpDirProperty = "java.io.tmpdir";
        Assertions.assertNotEquals(System.getProperty(tmpDirProperty), configuration.get(tmpDirProperty).orElse(System.getProperty(tmpDirProperty)));
        Assertions.assertEquals("notreallyyourtmpdir", configuration.get(tmpDirProperty).orElse(System.getProperty(tmpDirProperty)));
    }

}
