package io.github.chrisruffalo.tome.core.configuration;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.log.CountingLogger;
import io.github.chrisruffalo.tome.core.resolver.DefaultResolver;
import io.github.chrisruffalo.tome.core.source.MapSource;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurationTest {

    @Test
    public void testEmpty() {
        final Configuration configuration = new DefaultConfiguration();
        Assertions.assertEquals("", configuration.format(""));
        Assertions.assertEquals("same", configuration.format("same"));
        Assertions.assertEquals("${ none }", configuration.format("${ none }"));
    }

    @Test
    public void testMissingHandlerAndOrResolver() {
        final DefaultConfiguration configuration = new DefaultConfiguration();
        final CountingLogger logger = new CountingLogger();
        configuration.setLogger(logger);

        configuration.setHandler(null);
        configuration.setResolver(null);

        Assertions.assertEquals("none", configuration.format("none"));
        Assertions.assertEquals(1, logger.getError());
        logger.clear();

        configuration.setHandler(new DefaultHandler());
        Assertions.assertEquals("none", configuration.format("none"));
        Assertions.assertEquals(1, logger.getError());
        logger.clear();

        configuration.setResolver(new DefaultResolver());
        configuration.setHandler(null);
        Assertions.assertEquals("none", configuration.format("none"));
        Assertions.assertEquals(1, logger.getError());
        logger.clear();

        configuration.setHandler(new DefaultHandler());
        Assertions.assertEquals("none", configuration.format("none"));
        Assertions.assertEquals(0, logger.getError());
    }

    @Test
    public void testSourcePriority() {
        final Map<String, String> baseMap = new HashMap<>();
        baseMap.put("all", "123");
        baseMap.put("base", "true");
        final Source baseSource = new MapSource(baseMap);

        final Map<String, String> middleMap = new HashMap<>();
        middleMap.put("all", "456");
        middleMap.put("base", "false");
        middleMap.put("middle", "middle");
        final Source middleSource = new MapSource(middleMap);

        final Map<String, String> topMap = new HashMap<>();
        topMap.put("all", "999");
        topMap.put("middle", "not middle");
        topMap.put("double", "124.90");
        topMap.put("long", String.valueOf(Long.MAX_VALUE));
        final Source topSource = new MapSource(topMap);

        // start with base configuration
        final Configuration configuration = new DefaultConfiguration();

        // check empty map
        Assertions.assertEquals(-12, configuration.getInteger("all").orElse(-12));

        configuration.addSource(0, baseSource);
        Assertions.assertEquals(123, configuration.getInteger("all").orElse(0));
        Assertions.assertEquals(true, configuration.getBoolean("base").orElse(false));
        Assertions.assertFalse(configuration.get("middle").isPresent());
        Assertions.assertFalse(configuration.getLong("long").isPresent());
        Assertions.assertFalse(configuration.getDouble("double").isPresent());
        Assertions.assertFalse(configuration.getInteger("value").isPresent());
        Assertions.assertFalse(configuration.getBoolean("boolean").isPresent());

        // add a higher priority source
        configuration.addSource(50, middleSource);
        Assertions.assertEquals(456, configuration.getInteger("all").orElse(0));
        Assertions.assertEquals(false, configuration.getBoolean("base").orElse(true));
        Assertions.assertEquals("middle", configuration.get("middle").orElse("not available"));
        Assertions.assertFalse(configuration.getDouble("middle").isPresent()); // can't parse
        Assertions.assertFalse(configuration.getInteger("middle").isPresent()); // can't parse
        Assertions.assertFalse(configuration.getLong("middle").isPresent()); // can't parse
        Assertions.assertFalse(configuration.getBoolean("middle").orElse(true)); // can't parse but parses to false

        // add the highest priority source
        configuration.addSource(100, topSource);
        Assertions.assertEquals(999, configuration.getInteger("all").orElse(0));
        Assertions.assertEquals(false, configuration.getBoolean("base").orElse(true)); // stays the same
        Assertions.assertEquals("not middle", configuration.get("middle").orElse("not available"));
        Assertions.assertEquals(124.90, configuration.getDouble("double").orElse(0.0));
        Assertions.assertEquals(Long.MAX_VALUE, configuration.getLong("long").orElse(0L));

        // clear sources and then nothing can be found
        configuration.clearSources();
        Assertions.assertEquals(0, configuration.getInteger("all").orElse(0));
        Assertions.assertEquals(false, configuration.getBoolean("base").orElse(false));
    }

    @Test
    public void testSamePriority() {
        final Map<String, String> firstMap = new HashMap<>();
        firstMap.put("shared", "123");
        firstMap.put("first", "first");
        final Source firstSource = new MapSource(firstMap);

        final Map<String, String> secondMap = new HashMap<>();
        secondMap.put("shared", "321");
        secondMap.put("second", "second");
        final Source secondSource = new MapSource(secondMap);

        final Configuration configuration = new DefaultConfiguration();
        configuration.addSource(0, firstSource);
        configuration.addSource(0, secondSource);

        Assertions.assertEquals("123 first second ${third}", configuration.format("${shared} ${first} ${second} ${third}"));
    }

}
