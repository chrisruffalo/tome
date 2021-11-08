package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.directive.DirectiveContext;
import io.github.chrisruffalo.tome.core.directive.DirectiveException;
import io.github.chrisruffalo.tome.core.directive.DirectiveReader;
import io.github.chrisruffalo.tome.core.token.Part;
import io.github.chrisruffalo.tome.core.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IncludeDirective extends BaseDirective {

    private static final String INDENT_FIRST = "indent_first";
    private static final String INDENT = "indent";

    @Override
    public Optional<String> transform(Token token, DirectiveContext context) throws IOException, DirectiveException {
        // do not visit twice
        if (context.hasVisited(this.getClass(), token.getFullText())) {
            return Optional.empty();
        }

        // need to have a command and a parameter for that command
        final List<Part> parts = token.getParts();
        if (parts == null || parts.size() < 2) {
            return Optional.empty();
        }

        // the second part of the `include` directive is the actual file to include
        Part pathPart = parts.get(1);
        String pathString = pathPart.toString();

        // re-convert all the other parts to a string and then use those to create
        // the map of values that could be parameters to this directive
        final Map<String, String> parameters = this.convertPartsToRemainingParameters(parts, 2);

        // how far to indent lines
        int indent = 0;
        String indentString = "";
        if (parameters.containsKey(INDENT)) {
            final String stringIndent = parameters.get(INDENT);
            try {
                indent = Integer.parseInt(stringIndent);
                indentString = String.join("", Collections.nCopies(indent, " "));
            } catch (NumberFormatException nfe) {
                // todo: throw exception
            }
        }

        // do not indent the first line (let the natural placement/indentation stand)
        boolean indentFirst = parameters.containsKey(INDENT_FIRST);

        // if a configuration object was provided to the configuration then use it to resolve th
        // path further. this allows environment or other specific types of include to be applied
        final Optional<Configuration> configuration = context.getConfiguration();
        if (configuration.isPresent()) {
            pathString = configuration.get().format(pathString);
        }

        if (pathString == null || pathString.isEmpty()) {
            return Optional.empty();
        }

        // resolve path against roots or against local context
        Path path = null;
        for (final Path root : context.getRoots()) {
            final Path trial = Paths.get(root.toAbsolutePath().toString(), pathString);
            if (Files.exists(trial)) {
                path = trial;
                break;
            }
        }

        // just try and resolve from here
        if (path == null) {
            path = Paths.get(pathString);
        }

        // if no file found return empty
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        // split the context for recursive descent
        final DirectiveContext currentContext = context.split();

        // add the parent to the path roots so that we can resolve siblings without needing to know where we came from
        //currentContext.getRoots().add(path.getParent());

        // mark the current token as visited
        context.visit(this.getClass(), token.getFullText());

        // read entire file using directive reader
        try (
            final Reader fileReader = Files.newBufferedReader(path);
            final Reader directiveReader = new DirectiveReader(fileReader, currentContext);
            final BufferedReader bufferedInputReader = new BufferedReader(directiveReader);
        ){
            final StringBuilder builder = new StringBuilder();

            String readline = null;
            while((readline = bufferedInputReader.readLine()) != null) {
                // apply indent
                if (indentFirst || builder.toString().length() > 0) {
                    if (indent > 0) {
                        builder.append(indentString);
                    }
                }
                builder.append(readline).append(context.getLineSeparator());
            }

            // get the result and remove an end line from the end because that
            // needs to be handled by the callers (line separation in the file
            // or at the reader). if this is not done here then there will be
            // doubled line endings after every include
            String result = builder.toString();
            if (result.endsWith(context.getLineSeparator())) {
                result = result.substring(0, result.length() - context.getLineSeparator().length());
            }

            return Optional.of(result);
        }
    }
}
