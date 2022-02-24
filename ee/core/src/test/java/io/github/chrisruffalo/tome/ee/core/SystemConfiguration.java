package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.EnvironmentVariableSource;
import io.github.chrisruffalo.tome.core.source.SystemPropertySource;
import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;

@TomeConfiguration(name = "system")
public class SystemConfiguration implements TomeConfigurationCreator, TomeConfigurationModifier {

    @Override
    public Configuration create() {
        return new DefaultConfiguration();
    }

    @Override
    public void configure(Configuration configuration) {
        configuration.addSource(new SystemPropertySource());
        configuration.addSource(new EnvironmentVariableSource());
    }
}
