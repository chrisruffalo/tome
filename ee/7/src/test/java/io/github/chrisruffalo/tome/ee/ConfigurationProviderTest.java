package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.ee.weld.WeldTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationProviderTest extends WeldTest {

    @Test
    public void testProvider() {
        ConfigurationProvider provider = container().select(ConfigurationProvider.class).get();

        Assertions.assertEquals(2, provider.getModifiers().size());
        Assertions.assertEquals(2, provider.getCreators().size());
    }

}
