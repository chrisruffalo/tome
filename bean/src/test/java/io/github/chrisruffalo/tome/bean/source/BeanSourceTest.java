package io.github.chrisruffalo.tome.bean.source;

import io.github.chrisruffalo.tome.core.source.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BeanSourceTest {

    @Test
    public void testSimpleBean() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get("name").isPresent());
        Assertions.assertEquals("root", source.get("name").get().toString());
    }

    @Test
    public void testNull() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get("other").isPresent());
        Assertions.assertEquals("null", source.get("other").get().toString());
        Assertions.assertFalse(source.get("other.name").isPresent());
    }

    @Test
    public void testNotPresent() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertFalse(source.get("notAProperty").isPresent());
        Assertions.assertFalse(source.get("siblings[2].name").isPresent());
        Assertions.assertFalse(source.get("beanMap.root").isPresent());
    }

    @Test
    public void testCycle() {
        final TestBean bean = new TestBean("root");
        bean.setOther(bean);
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get("other.name").isPresent());
        Assertions.assertEquals("root", source.get("other.name").get().toString());
    }

    @Test
    public void testIndex() {
        final TestBean bean = new TestBean("root");
        bean.setOther(bean);
        bean.getSiblings().add(null);
        bean.getSiblings().add(bean);
        bean.getBeanMap().put("root", bean);

        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get("other.siblings[1].beanMap.root.name").isPresent());
        Assertions.assertEquals("root", source.get("other.siblings[1].beanMap.root.name").get().toString());
    }

}


