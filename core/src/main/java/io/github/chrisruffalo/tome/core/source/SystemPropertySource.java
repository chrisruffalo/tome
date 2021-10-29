package io.github.chrisruffalo.tome.core.source;

import java.util.Properties;

/**
 * Provides access to System Properties as a Source
 */
public class SystemPropertySource extends PropertySource {

    public SystemPropertySource() {
        super(System.getProperties());
    }

}
