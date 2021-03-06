package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.ee.annotations.Tome;
import io.github.chrisruffalo.tome.ee.core.BaseTomeProvider;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationCreator;
import io.github.chrisruffalo.tome.ee.core.TomeConfigurationModifier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Default
@ApplicationScoped
public class ConfigurationProvider extends BaseTomeProvider {

    @Inject
    @Any
    Instance<TomeConfigurationCreator> creators;

    @Inject
    @Any
    Instance<TomeConfigurationModifier> providers;

    @Override
    public List<TomeConfigurationCreator> getCreators() {
        final List<TomeConfigurationCreator> creators = new LinkedList<>();
        for (TomeConfigurationCreator creator : this.creators) {
            creators.add(creator);
        }
        return creators;
    }

    @Override
    public List<TomeConfigurationModifier> getModifiers() {
        final List<TomeConfigurationModifier> providers = new LinkedList<>();
        for (TomeConfigurationModifier creator : this.providers) {
            providers.add(creator);
        }
        return providers;
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
