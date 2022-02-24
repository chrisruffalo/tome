package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Test, in a non-ee way, the provider
 */
public class BaseTomeProviderTest {

    // in an ee setting this would be an injected parent instance
    private static final ConfigurationProvider configurationProvider = new ConfigurationProvider();

    @BeforeAll
    public static void init() {
        configurationProvider.init();
    }

    @BeforeAll
    public static void setProperties() {
        System.setProperty("sys.configuration", "value");
    }

    @Test
    public void testApplicationConfiguration() {
        final Optional<Configuration> appConfiguration = configurationProvider.getConfiguration(TomeConfiguration.DEFAULT_NAME);
        Assertions.assertTrue(appConfiguration.isPresent());
        final Configuration app = appConfiguration.get();
        Assertions.assertNotEquals("value", app.format("${sys.configuration}"));
    }

    @Test
    public void testSystemConfiguration() {
        final Optional<Configuration> sysConfiguration = configurationProvider.getConfiguration("system");
        Assertions.assertTrue(sysConfiguration.isPresent());
        final Configuration sys = sysConfiguration.get();
        Assertions.assertEquals("value", sys.format("${sys.configuration}"));
    }

}
