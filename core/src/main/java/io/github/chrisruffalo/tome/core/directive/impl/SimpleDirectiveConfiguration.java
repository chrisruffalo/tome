package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.directive.DirectiveConfiguration;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimpleDirectiveConfiguration implements DirectiveConfiguration  {

    private String startToken = DEFAULT_DIRECTIVE_START_TOKEN;
    private String endToken = DEFAULT_DIRECTIVE_END_TOKEN;

    private List<String> rootPaths;

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

    public String getStartToken() {
        return startToken;
    }

    public void setStartToken(String startToken) {
        this.startToken = startToken;
    }

    public String getEndToken() {
        return endToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }
}
