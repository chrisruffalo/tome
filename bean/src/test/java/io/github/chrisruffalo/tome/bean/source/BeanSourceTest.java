package io.github.chrisruffalo.tome.bean.source;

import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BeanSourceTest {

    @Test
    public void testSimpleBean() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get(new SourceContext(), "name").isPresent());
        Assertions.assertEquals("root", source.get(new SourceContext(), "name").get().toString());
    }

    @Test
    public void testNull() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get(new SourceContext(), "other").isPresent());
        Assertions.assertEquals("null", source.get(new SourceContext(), "other").get().toString());
        Assertions.assertFalse(source.get(new SourceContext(), "other.name").isPresent());
    }

    @Test
    public void testNotPresent() {
        final TestBean bean = new TestBean("root");
        final Source source = new BeanSource(bean);

        Assertions.assertFalse(source.get(new SourceContext(), "notAProperty").isPresent());
        Assertions.assertFalse(source.get(new SourceContext(), "siblings[2].name").isPresent());
        Assertions.assertFalse(source.get(new SourceContext(), "beanMap.root").isPresent());
    }

    @Test
    public void testCycle() {
        final TestBean bean = new TestBean("root");
        bean.setOther(bean);
        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get(new SourceContext(), "other.name").isPresent());
        Assertions.assertEquals("root", source.get(new SourceContext(), "other.name").get().toString());
    }

    @Test
    public void testIndex() {
        final TestBean bean = new TestBean("root");
        bean.setOther(bean);
        bean.getSiblings().add(null);
        bean.getSiblings().add(bean);
        bean.getBeanMap().put("root", bean);

        final Source source = new BeanSource(bean);

        Assertions.assertTrue(source.get(new SourceContext(), "other.siblings[1].beanMap.root.name").isPresent());
        Assertions.assertEquals("root", source.get(new SourceContext(), "other.siblings[1].beanMap.root.name").get().toString());
    }

}


