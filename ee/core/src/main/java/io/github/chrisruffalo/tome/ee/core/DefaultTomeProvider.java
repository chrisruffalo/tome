package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;

import java.util.*;

public abstract class DefaultTomeProvider {

    public abstract List<TomeConfigurationCreator> getCreators();

    public abstract List<TomeConfigurationModifier> getProviders();

    private final Map<String, Configuration> configurationMap = new HashMap<>();

    public void init() {
        final Set<String> configurationNames = new LinkedHashSet<>();

        final Map<String, TomeConfigurationCreator> configurationCreatorMap = new HashMap<>();
        final Map<String, List<TomeConfigurationModifier>> configurationProviderMap = new HashMap<>();

        // get creator for name
        this.getCreators().forEach(creator -> {
            // get name from annotation
            final TomeConfiguration tomeConfigurationAnnotation = creator.getClass().getAnnotation(TomeConfiguration.class);
            final String name = tomeConfigurationAnnotation.name();

            // log
            if (configurationCreatorMap.containsKey(name)) {
                // todo: log that you can't have more than one non-default creator for the same name
                return;
            }

            // set in map
            configurationNames.add(name);
            configurationCreatorMap.put(name, creator);
        });

        // get providers and map to name
        this.getProviders().forEach(provider -> {
            final TomeConfiguration tomeConfigurationAnnotation = provider.getClass().getAnnotation(TomeConfiguration.class);
            final String name = tomeConfigurationAnnotation.name();

            if(!configurationProviderMap.containsKey(name)) {
                configurationProviderMap.put(name, new LinkedList<>());
            }

            configurationNames.add(name);
            configurationProviderMap.get(name).add(provider);
        });

        // set up configuration for each name
        configurationNames.forEach(name -> {
            final TomeConfigurationCreator creator = configurationCreatorMap.getOrDefault(name, new DefaultTomeConfigurationCreator());
            final List<TomeConfigurationModifier> providers = configurationProviderMap.getOrDefault(name, Collections.emptyList());

            final Configuration configuration = creator.create();
            providers.forEach(provider -> {
                provider.configure(configuration);
            });

            configurationMap.put(name, configuration);
        });
    }

    public Optional<Configuration> getConfiguration(final String name) {
        return Optional.ofNullable(this.configurationMap.get(name));
    }

}
