package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.reader.TransformException;
import io.github.chrisruffalo.tome.core.reader.TransformingReader;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.core.token.Handler;
import io.github.chrisruffalo.tome.core.token.Token;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class DirectiveReader extends TransformingReader<DirectiveContext> {

    private final Handler tokenHandler;
    private DirectiveContext context;

    public DirectiveReader(final Reader input, final DirectiveContext currentContext) {
        this(input, currentContext.getDirectiveConfiguration());
        this.context = currentContext;
    }

    public DirectiveReader(final Reader input, final DirectiveConfiguration configuration) {
        super(input);

        this.context = new DirectiveContext(configuration);

        // configure context from configuration (if available) like adding roots
        if (configuration != null) {
            // set paths from configuration
            if (configuration.getRootPaths() != null) {
                context.setRoots(configuration.getRootPaths().stream().map(Paths::get).collect(Collectors.toList()));
            }
        }

        String startToken = DirectiveConfiguration.DEFAULT_DIRECTIVE_START_TOKEN;
        String endToken = DirectiveConfiguration.DEFAULT_DIRECTIVE_END_TOKEN;
        if (configuration != null) {
            if (configuration.getDirectiveStartToken() != null && !configuration.getDirectiveStartToken().isEmpty()) {
                startToken = configuration.getDirectiveStartToken();
            }
            if (configuration.getDirectiveEndToken() != null && !configuration.getDirectiveEndToken().isEmpty()) {
                endToken = configuration.getDirectiveEndToken();
            }
        }
        this.tokenHandler = new DefaultHandler(startToken, endToken, ' ');
    }

    @Override
    protected String transform(String line, DirectiveContext context) throws IOException, TransformException {
        // parse out tokens
        if(this.tokenHandler.containsToken(line)) {
            for (final Token token : this.tokenHandler.find(line)) {
                // get each part
                final String firstPart = token.getParts().get(0).getText();
                final Directive directive = DirectiveFactory.get(firstPart);
                if (directive != null) {
                    final Optional<String> transformResult = directive.transform(token, context);
                    if (transformResult.isPresent()) {
                        line = StringUtils.replace(line, token.getFullText(), transformResult.get());
                    }
                }
            }
        }
        return line;
    }

    @Override
    protected DirectiveContext getCurrentContext() {
        return this.context;
    }

}
