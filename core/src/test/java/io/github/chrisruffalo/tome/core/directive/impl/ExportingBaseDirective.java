package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.directive.DirectiveContext;
import io.github.chrisruffalo.tome.core.directive.DirectiveException;
import io.github.chrisruffalo.tome.core.token.Token;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ExportingBaseDirective extends BaseDirective {

    @Override
    public Optional<String> transform(Token token, DirectiveContext context) throws IOException, DirectiveException {
        return Optional.empty();
    }

    public Map<String, String> exportParseRemainingParameters(String remainder) {
        return this.parseRemainingParameters(new DirectiveContext(new SimpleDirectiveConfiguration()), remainder);
    }


}
