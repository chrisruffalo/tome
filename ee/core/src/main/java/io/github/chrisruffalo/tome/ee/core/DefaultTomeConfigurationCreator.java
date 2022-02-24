package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;

public class DefaultTomeConfigurationCreator implements TomeConfigurationCreator {

    @Override
    public Configuration create() {
        return new DefaultConfiguration();
    }

}
