package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.core.token.Handler;
import io.github.chrisruffalo.tome.core.token.Token;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the implementation of directives for a given file. This is a very simple implementation
 * that buffers the output into strings in memory.
 *
 */
public class DirectiveHandler {

    private final DirectiveConfiguration configuration;

    public DirectiveHandler(final DirectiveConfiguration configuration) {
        this.configuration = configuration;
    }

    public Reader reader(final Reader reader) throws IOException {
        return reader(new BufferedReader(reader));
    }


    public Reader reader(final BufferedReader inputReader) throws IOException {
        // create directive context
        final DirectiveContext context = new DirectiveContext(this);

        // configure context from configuration (if available) like adding roots
        if (this.configuration != null) {
            if (configuration.getRootPaths() != null) {
                context.setRoots(this.configuration.getRootPaths().stream().map(Paths::get).collect(Collectors.toList()));
            }
        }

        return reader(inputReader, context);
    }

    public Reader reader(final BufferedReader inputReader, DirectiveContext context) throws IOException {
        if (inputReader == null) {
            return new StringReader("");
        }

        // buffer for lines
        final List<String> lines = new LinkedList<>();

        // create handler from configuration
        final Handler handler = createHandler();

        String currentLine = null;
        while((currentLine = inputReader.readLine()) != null) {
            // parse out tokens
            if(handler.containsToken(currentLine)) {
                for(final Token token : handler.find(currentLine)) {
                    // get each part
                    final String firstPart = token.getParts().get(0);
                    final Directive directive = DirectiveFactory.get(firstPart);
                    if (directive != null) {
                        final Optional<String> transformResult = directive.transform(token, context);
                        if (transformResult.isPresent()) {
                            currentLine = StringUtils.replace(currentLine, token.getFullText(), transformResult.get());
                            // this is an OBE error that keeps appearing in stacked output
                            if (currentLine.endsWith(context.getLineSeparator())) {
                                currentLine = currentLine.substring(0, currentLine.length() - context.getLineSeparator().length());
                            }
                        }
                    }
                }
            }
            // read lines
            lines.add(currentLine);
        }

        return new StringReader(String.join(context.getLineSeparator(), lines));
    }

    /**
     * The directive handler uses a special token handler to find and split tokens. It uses the
     * space as a separator so other values (like file paths) with spaces need to be quoted in
     * order to parse properly.
     *
     * At some point it may make sense for this to be a specialized token handler for directives.
     *
     * @return a handler specialized for parsing directives.
     */
    private Handler createHandler() {
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
        return new DefaultHandler(startToken, endToken, ' ');
    }

    public InputStream inputStream(final InputStream inputStream) throws IOException {
        return new ReaderInputStream(reader(new InputStreamReader(inputStream)), Charset.defaultCharset());
    }

}
