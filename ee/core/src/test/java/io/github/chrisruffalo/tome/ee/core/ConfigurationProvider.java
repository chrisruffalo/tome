package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.ee.core.impl.ApplicationConfiguration;
import io.github.chrisruffalo.tome.ee.core.impl.SystemConfiguration;

import java.util.LinkedList;
import java.util.List;


public class ConfigurationProvider extends BaseTomeProvider {

    private static final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
    private static final SystemConfiguration systemConfiguration = new SystemConfiguration();

    @Override
    public List<TomeConfigurationCreator> getCreators() {
        return new LinkedList<TomeConfigurationCreator>(){{
            add(applicationConfiguration);
            add(systemConfiguration);
        }};
    }

    @Override
    public List<TomeConfigurationModifier> getModifiers() {
        return new LinkedList<TomeConfigurationModifier>(){{
            add(applicationConfiguration);
            add(systemConfiguration);
        }};
    }
}
