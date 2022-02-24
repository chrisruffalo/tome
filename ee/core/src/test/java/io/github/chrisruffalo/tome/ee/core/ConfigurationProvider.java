package io.github.chrisruffalo.tome.ee.core;

import java.util.LinkedList;
import java.util.List;


public class ConfigurationProvider extends DefaultTomeProvider {

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
    public List<TomeConfigurationProvider> getProviders() {
        return new LinkedList<TomeConfigurationProvider>(){{
            add(applicationConfiguration);
            add(systemConfiguration);
        }};
    }
}
