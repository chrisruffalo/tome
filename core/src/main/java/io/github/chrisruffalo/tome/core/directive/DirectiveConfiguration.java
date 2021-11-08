package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.Configuration;

import java.util.List;
import java.util.Optional;

public interface DirectiveConfiguration {

    String DEFAULT_DIRECTIVE_START_TOKEN = "%{";
    String DEFAULT_DIRECTIVE_END_TOKEN = "}%";

    /**
     * A list of root paths to look in when trying to
     * resolve a non-absolute file path. This should never return
     * null. No paths should be an empty list.
     *
     * @return a list of strings representing partial or absolute paths to search
     */
    List<String> getRootPaths();

    /**
     * A configuration to use (if one is available) in order further configure
     * directives. This method should never return null.
     *
     * @return empty if no configuration provided, a Configuration instance in the optional if ready for configuration
     */
    default Optional<Configuration> getConfiguration() { return Optional.empty(); }

    default String getDirectiveStartToken() {
        return DEFAULT_DIRECTIVE_START_TOKEN;
    }

    default String getDirectiveEndToken() {
        return DEFAULT_DIRECTIVE_END_TOKEN;
    }

}
