package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.ee.annotations.TomeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.util.Optional;

@Default
@ApplicationScoped
public class ValueProvider {

    @Inject
    ConfigurationProvider configurationProvider;

    @Produces
    @TomeValue
    public String provideStringValue(final InjectionPoint injectionPoint) {
        final TomeValue annotation = this.getAnnotation(injectionPoint);
        final Configuration targetConfiguration = this.getConfiguration(annotation);
        if(annotation.property() != null && !annotation.property().isEmpty()) {
            return targetConfiguration.get(annotation.property()).orElse(annotation.defaultValue());
        } else if(annotation.format() != null && !annotation.format().isEmpty()) {
            return targetConfiguration.format(annotation.format());
        }
        return annotation.defaultValue();
    }

    private TomeValue getAnnotation(final InjectionPoint injectionPoint) {
        return injectionPoint.getAnnotated().getAnnotation(TomeValue.class);
    }

    private Configuration getConfiguration(final TomeValue annotation) {
        if (annotation.configuration() != null && !annotation.configuration().isEmpty()) {
            Optional<Configuration> config = configurationProvider.getConfiguration(annotation.configuration());
            if (config.isPresent()) {
                return config.get();
            }
        }
        return new DefaultConfiguration();
    }
}
