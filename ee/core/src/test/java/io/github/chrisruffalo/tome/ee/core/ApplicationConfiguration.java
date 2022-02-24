package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;

@TomeConfiguration
public class ApplicationConfiguration implements TomeConfigurationCreator, TomeConfigurationModifier {

    @Override
    public Configuration create() {
        return new DefaultConfiguration();
    }

    @Override
    public void configure(Configuration configuration) {
        // add application level configuration
    }

}
