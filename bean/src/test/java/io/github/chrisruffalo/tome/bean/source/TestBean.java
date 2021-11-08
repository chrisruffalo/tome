package io.github.chrisruffalo.tome.bean.source;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestBean {

    private final String name;

    private TestBean other;

    private final Map<String, TestBean> beanMap = new HashMap<>();

    private  final List<TestBean> siblings = new LinkedList<>();

    public TestBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOther(TestBean other) {
        this.other = other;
    }

    public TestBean getOther() {
        return other;
    }

    public Map<String, TestBean> getBeanMap() {
        return beanMap;
    }

    public List<TestBean> getSiblings() {
        return siblings;
    }
}
