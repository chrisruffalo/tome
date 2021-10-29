package io.github.chrisruffalo.tome.core.source;

import org.junit.jupiter.api.Test;

public class SystemPropertySourceTest extends SourceTest {

    @Test
    public void testKnownValue() {
        expect(new SystemPropertySource(), "java.io.tmpdir", System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void testMissingValue() {
        expectNotFound(new SystemPropertySource(), "__not_a_property");
    }

}
