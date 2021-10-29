package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.directive.impl.IncludeDirective;

import java.util.HashMap;
import java.util.Map;

public class DirectiveFactory {

    private static final Map<String, Directive> directives = new HashMap<String, Directive>(){{
        put("include", new IncludeDirective());
    }};

    public static Directive get(final String directiveCommand) {
        return directives.get(directiveCommand);
    }

}
