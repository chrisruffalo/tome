package io.github.chrisruffalo.tome.core;

import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.source.Source;

import java.util.Optional;

/**
 * This is the main API point of tome. This is what should be returned and used by extensions and implementors
 * to create a configuration that is usable and re-usable.
 */
public interface Configuration {

    /**
     * Add a source to the configurations.
     *
     * @param priority the priority of the source, higher priority sources are checked first by resolvers
     * @param source the source
     */
    void addSource(int priority, Source source);

    /**
     * Remove all sources from the configuration
     *
     */
    void clearSources();

    /**
     * Provide a log handling mechanism to the logger. If not provided the log messages
     * will be silent from the internal mechanism.
     *
     * @param logger the logger to use to handle messages from resolution
     */
    void setLogger(final Logger logger);

    /**
     * Given an expression containing multiple tokens resolve each token
     * as much as possible and return the resolved string.
     *
     * This method is useful for turing a string with multiple tokens
     * into the fully ready to use string.
     *
     * Ex: log.error(configuration.format("Could not connect to ${host}:${port}"));
     *
     * @param expression an expression to resolve
     * @return the resolved string
     */
    String format(final String expression);

    /**
     * Resolve the given property recursively through sources and resolving
     * any tokens that are found further.
     *
     * @param property the property to resolve
     * @return the resolved property, empty if the property could not be found in any source
     */
    Optional<String> get(final String property);

    /**
     * Use the get() method and then convert the output to
     * an integer. If the conversion fails, return empty.
     *
     * @param property the property to resolve
     * @return the resolved property as an integer if possible, empty otherwise
     */
    default Optional<Integer> getInteger(final String property) {
        final Optional<String> gotten = this.get(property);
        if (!gotten.isPresent()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(gotten.get()));
        } catch (Exception exception) {
            // todo: log?
        }
        return Optional.empty();
    }

    /**
     * Use the get() method and then convert the output to
     * a long. If the conversion fails, return empty.
     *
     * @param property the property to resolve
     * @return the resolved property as a long if possible, empty otherwise
     */
    default Optional<Long> getLong(final String property) {
        final Optional<String> gotten = this.get(property);
        if (!gotten.isPresent()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(gotten.get()));
        } catch (Exception exception) {
            // todo: log?
        }
        return Optional.empty();
    }

    /**
     * Use the get() method and then convert the output to
     * a double. If the conversion fails, return empty.
     *
     * @param property the property to resolve
     * @return the resolved property as a double if possible, empty otherwise
     */
    default Optional<Double> getDouble(final String property) {
        final Optional<String> gotten = this.get(property);
        if (!gotten.isPresent()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(gotten.get()));
        } catch (Exception exception) {
            // todo: log?
        }
        return Optional.empty();
    }

    /**
     * Use the get() method and then convert the output to
     * a boolean value. If the conversion fails, return empty.
     *
     * @param property the property to resolve
     * @return the resolved property as a boolean value if possible, empty otherwise
     */
    default Optional<Boolean> getBoolean(final String property) {
        final Optional<String> gotten = this.get(property);
        return gotten.map(Boolean::parseBoolean);
    }

}
