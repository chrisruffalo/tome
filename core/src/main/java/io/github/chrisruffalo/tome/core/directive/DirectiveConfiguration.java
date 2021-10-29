package io.github.chrisruffalo.tome.core.directive;

import java.util.List;

public interface DirectiveConfiguration {

    String DEFAULT_DIRECTIVE_START_TOKEN = "%{";
    String DEFAULT_DIRECTIVE_END_TOKEN = "}%";

    /**
     * A list of root paths to look in when trying to
     * resolve a non-absolute file path.
     *
     * @return a list of strings representing partial or absolute paths to search
     */
    List<String> getRootPaths();

    default String getDirectiveStartToken() {
        return DEFAULT_DIRECTIVE_START_TOKEN;
    }

    default String getDirectiveEndToken() {
        return DEFAULT_DIRECTIVE_END_TOKEN;
    }

}
