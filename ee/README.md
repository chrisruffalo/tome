# Java/Jakarta EE

## Overview

Tome has modules that use the EE CDI API to provide configuration support to EE-based applications. The code
for each EE version is fairly similar but is separated into distinctly versioned modules so that compatibility
can be ascertained.

## Use

Each module has its own examples but the rough order of operations is to have a configuration creator
(annotated with @TomeConfiguration) to create/construct the configuration. You can also add configuration
modifiers that modify the configuration (also annotated with @TomeConfiguration).

Once the configuration objects are created they can be used in beans to provide property values, resolve a 
formatted configuration string, or directly inject the value itself.

```java
// constructor
@TomeConfiguration(name = "application")
public class BasicConfigurationCreator implements TomeConfigurationCreator {

    public Configuration create() {
        final Configuration conf = new DefaultConfiguration();
        // add new handler
        // configure other stuff
        // add default source
        return conf;
    }
    
}

// modifier
@TomeConfiguration(name = "application")
public class ConfigurationSourceModifier implements TomeConfigurationModifier {

    public void configure(final Configuration configuration) {
        // add a new yaml source        
        configuration.addSource(new YamlSource().load(Paths.get("/path/to/file.yml")));
    }
    
}

// application bean
@ApplicationScoped
public class Application {
    
    @Inject
    @TomeValue(name = "application", property = "remote.url")
    String remoteUrl;
    
    @PostConstruct
    public void init() {
        // show remote url
        log.info("created application connecting to {}", remoteUrl);
    }
}
```

While each EE module has slightly different implementation details the functionality from the API level 
is roughly the same.