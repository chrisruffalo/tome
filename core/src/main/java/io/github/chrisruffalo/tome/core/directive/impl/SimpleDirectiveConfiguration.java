package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.directive.DirectiveConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SimpleDirectiveConfiguration implements DirectiveConfiguration  {

    private String directiveStartToken = DEFAULT_DIRECTIVE_START_TOKEN;
    private String directiveEndToken = DEFAULT_DIRECTIVE_END_TOKEN;

    private List<String> rootPaths;

    private Configuration configuration;

    public SimpleDirectiveConfiguration() {

    }

    @Override
    public Optional<Configuration> getConfiguration() {
        return Optional.ofNullable(configuration);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public List<String> getRootPaths() {
        if (this.rootPaths == null) {
            this.rootPaths = new LinkedList<>();
        }
        return rootPaths;
    }

    public void setRootPaths(List<String> rootPaths) {
        this.rootPaths = rootPaths;
    }

    public String getDirectiveStartToken() {
        return directiveStartToken;
    }

    public void setDirectiveStartToken(String directiveStartToken) {
        this.directiveStartToken = directiveStartToken;
    }

    public String getDirectiveEndToken() {
        return directiveEndToken;
    }

    public void setDirectiveEndToken(String directiveEndToken) {
        this.directiveEndToken = directiveEndToken;
    }
}
