package io.github.chrisruffalo.tome.ee.impl;

import io.gihub.chrisruffalo.tome.yaml.YamlSource;
import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationCreator;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationProvider;
import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;
import io.github.chrisruffalo.tome.test.TestUtil;

@TomeConfiguration
public class ApplicationConfiguration implements TomeConfigurationCreator, TomeConfigurationProvider {

    @Override
    public Configuration create() {
        return new DefaultConfiguration();
    }

    @Override
    public void configure(Configuration configuration) {
        try {
            configuration.addSource(new YamlSource().load(TestUtil.getPathToTestResource("application.yml")));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
