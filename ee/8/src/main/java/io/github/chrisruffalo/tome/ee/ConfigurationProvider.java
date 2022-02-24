package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.ee.annotations.Tome;
import io.github.chrisruffalo.tome.ee.core.DefaultTomeProvider;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationCreator;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Default
@ApplicationScoped
public class ConfigurationProvider extends DefaultTomeProvider {

    @Inject
    @Any
    Instance<TomeConfigurationCreator> creators;

    @Inject
    @Any
    Instance<TomeConfigurationProvider> providers;

    @Override
    public List<TomeConfigurationCreator> getCreators() {
        return this.creators.select(TomeConfigurationCreator.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<TomeConfigurationProvider> getProviders() {
        return this.providers.select(TomeConfigurationProvider.class).stream().collect(Collectors.toList());
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Produces
    @Tome
    public Configuration configuration(InjectionPoint injectionPoint) {
        final Tome tomeAnnotation = injectionPoint.getAnnotated().getAnnotation(Tome.class);
        Configuration configuration = null;
        if (tomeAnnotation != null) {
            Optional<Configuration> found = this.getConfiguration(tomeAnnotation.name());
            if (found.isPresent()) {
                configuration = found.get();
            }
        }
        return configuration;
    }
}
