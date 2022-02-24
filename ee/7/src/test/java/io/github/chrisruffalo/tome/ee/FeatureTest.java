package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.ee.weld.WeldTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FeatureTest extends WeldTest {

    @BeforeAll
    public static void setup() {
        // publish system properties for system configuration
        System.setProperty("sys.arbitrary", "arbitrary_value");
    }

    @Test
    public void testBeanProperties() {
        FeatureBean bean = container().select(FeatureBean.class).get();

        Assertions.assertNotNull(bean.getApplication());

        Assertions.assertNotNull(bean.getSystem());
        Assertions.assertEquals("arbitrary_value", bean.getSystem().get("sys.arbitrary").orElse(null));
        Assertions.assertEquals("postgres://localhost:3306/application", bean.getDbUrl());
        Assertions.assertEquals("Could not connect to 'postgres://localhost:3306/application'", bean.getDbError());
    }

}
