package io.github.chrisruffalo.tome.core.source;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class EnvironmentVariableSourceTest extends SourceTest {

    @Test
    public void testKnownValue() {
        final String envValName = new LinkedList<>(System.getenv().keySet()).get(0);
        expect(new EnvironmentVariableSource(), envValName, System.getenv(envValName));
    }

    @Test
    public void testMissingValue() {
        expectNotFound(new EnvironmentVariableSource(), "__NOT_AN_ENVIRONMENT_VARIABLE");
    }

}
