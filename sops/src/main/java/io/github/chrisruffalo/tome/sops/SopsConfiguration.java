package io.github.chrisruffalo.tome.sops;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Programmatic pass through for the environment
 * values that are used to configure SOPS. This
 * is intended to be an incomplete list that
 * grows as it goes on.
 */
public class SopsConfiguration {

    public static class SopsConfigurationItem<VALUE_TYPE> {
        private final String key;
        private VALUE_TYPE value;
        private boolean set = false;

        public SopsConfigurationItem(final String key) {
            this.key = key;
        }

        protected String getKey() {
            return this.key;
        }

        protected boolean isSet() {
            return this.set;
        }

        protected void setValue(VALUE_TYPE value) {
            this.set = true;
            this.value = value;
        }

        protected VALUE_TYPE getValue() {
            return this.value;
        }

        protected String getStringValue() {
            if (this.value == null) {
                return "";
            }
            return String.valueOf(this.value);
        }

        /**
         * Creates a copy of the instance so that the original is not modified by downstream
         * actions.
         *
         * @return a copy of this instance with the value set if it is set on this instance
         */
        protected SopsConfigurationItem<VALUE_TYPE> instance() {
            final SopsConfigurationItem<VALUE_TYPE> instance = new SopsConfigurationItem<>(this.key);
            if (this.set) {
                instance.setValue(this.getValue());
            }
            return instance;
        }
    }

    //=========================================
    // List out known settings here but consumers
    // can always pass in custom values
    //=========================================

    /**
     * The path to the age key file
     */
    public static final SopsConfigurationItem<Path> ENV_SOPS_AGE_KEY_FILE = new SopsConfigurationItem<>("SOPS_AGE_KEY_FILE");

    /**
     * The aws profile to use
     */
    public static final SopsConfigurationItem<Path> COMMAND_AWS_PROFILE = new SopsConfigurationItem<>("--aws-profile");

    private final Map<String, SopsConfigurationItem<?>> environment = new HashMap<>();
    private final Map<String, SopsConfigurationItem<?>> commands = new HashMap<>();

    public SopsConfiguration() {

    }

    public SopsConfiguration(final SopsConfiguration sourceConfiguration) {
        sourceConfiguration.getEnvironment().values().forEach(item -> {
            this.environment.put(item.getKey(), item);
        });
    }

    private <INSTANCE_TYPE> SopsConfiguration set(SopsConfigurationItem<INSTANCE_TYPE> item, INSTANCE_TYPE value, Map<String, SopsConfigurationItem<?>> target) {
        final SopsConfigurationItem<INSTANCE_TYPE> clone = item.instance();
        clone.setValue(value);
        target.put(item.getKey(), clone);

        return this;
    }

    public <INSTANCE_TYPE> SopsConfiguration setEnvironment(SopsConfigurationItem<INSTANCE_TYPE> item, INSTANCE_TYPE value) {
        return set(item, value, this.environment);
    }

    public Map<String, SopsConfigurationItem<?>> getEnvironment() {
        return Collections.unmodifiableMap(this.environment);
    }

    public <INSTANCE_TYPE> SopsConfiguration setCommandLine(SopsConfigurationItem<INSTANCE_TYPE> item, INSTANCE_TYPE value) {
        return set(item, value, this.commands);
    }

    public Map<String, SopsConfigurationItem<?>> getCommandLine() {
        return Collections.unmodifiableMap(this.commands);
    }

}
