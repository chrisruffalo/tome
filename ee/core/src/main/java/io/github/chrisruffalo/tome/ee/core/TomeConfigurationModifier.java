package io.github.chrisruffalo.tome.ee.core;

import io.github.chrisruffalo.tome.core.Configuration;

/**
 * Implement this class to customize configuration for your application
 */
public interface TomeConfigurationModifier {

    /**
     * The EE container will call this configuration method for each
     * annotated tome configuration class. Implementors of this method
     * should use this to add sources and other configuration. These
     * methods can be combined/composed to overlay named configurations.
     *
     * @param configuration the configuration that is being modified
     */
    void configure(final Configuration configuration);

}
