package io.github.chrisruffalo.tome.examples.bootstrap;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.PropertySource;
import io.github.chrisruffalo.tome.core.source.SystemPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class Bootstrap {

    public Configuration load() {
        final Configuration configuration = new DefaultConfiguration();

        // add the system properties as the lowest priority source
        configuration.addSource(0, new SystemPropertySource());

        // look at system properties to find file that we have cleverly set there
        String property = "app.bootstrap.dir";
        try {
            System.setProperty(property, Paths.get(Thread.currentThread().getContextClassLoader().getResource("bootstrap").toURI()).toAbsolutePath().toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        final Optional<Path> bootstrapPropertiesPath = configuration.paths("${app.bootstrap.dir}","bootstrap.properties");
        if (!bootstrapPropertiesPath.isPresent()) {
            throw new RuntimeException(String.format("Could not find path property in property '%s' even though we set it there", property));
        }
        final Properties bootstrapProperties = new Properties();
        try {
            bootstrapProperties.load(Files.newBufferedReader(bootstrapPropertiesPath.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // the source is added to the properties
        configuration.addSource(1, new PropertySource(bootstrapProperties));

        // we are going to take this one step further and use the knowledge in the bootstrap file to chain load the
        // next properties file
        final Optional<Path> applicationPropertiesPath = configuration.paths("${application.properties}");
        if (!applicationPropertiesPath.isPresent()) {
            throw new RuntimeException(String.format("Could not find path property in property '%s' even though we set it there", property));
        }
        final Properties applicationProperties = new Properties();
        try {
            applicationProperties.load(Files.newBufferedReader(applicationPropertiesPath.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // the application is added to the properties at a higher priority
        configuration.addSource(100, new PropertySource(applicationProperties));

        // the configuration is loaded with 3 sources, the system properties, the bootstrap file, and the actual application properties
        return configuration;
    }

}
