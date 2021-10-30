package io.github.chrisruffalo.tome.core.source;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PrefixedSourceTest {

    @Test
    public void testTooShort() {
        final Source prefix = new PrefixedSource("env.", new EnvironmentVariableSource());
        Assertions.assertFalse(prefix.get("s").isPresent());
    }

    @Test
    public void testPrefixMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("property", "value");
        final Source prefixedMapSource = new PrefixedSource("map.", new MapSource(map));

        // found
        Assertions.assertTrue(prefixedMapSource.get("map.property").isPresent());

        // not found
        Assertions.assertFalse(prefixedMapSource.get("mapproperty").isPresent());
        Assertions.assertFalse(prefixedMapSource.get("map..property").isPresent());
        Assertions.assertFalse(prefixedMapSource.get("m.property").isPresent());
        Assertions.assertFalse(prefixedMapSource.get("map.prop").isPresent());
    }

}
