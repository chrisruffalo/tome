package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.directive.Directive;
import io.github.chrisruffalo.tome.core.directive.DirectiveContext;
import io.github.chrisruffalo.tome.core.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IncludeDirective implements Directive {

    @Override
    public Optional<String> transform(Token token, DirectiveContext context) {
        // do not visit twice
        if (context.hasVisited(this.getClass(), token.getFullText())) {
            return Optional.empty();
        }

        // need to have a command and a parameter for that command
        if (token.getParts().size() < 2) {
            return Optional.empty();
        }

        // the second part of the `include` directive is the actual file to include
        final String pathString = token.getParts().get(1);
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

        // mark the current token as visited
        context.visit(this.getClass(), token.getFullText());

        // read entire file using directive reader
        try (
            final BufferedReader fileReader = Files.newBufferedReader(path);
        ){
            final StringBuilder builder = new StringBuilder();
            final BufferedReader directiveReader = new BufferedReader(currentContext.getDirectiveHandler().reader(fileReader, currentContext));

            String readline = null;
            while((readline = directiveReader.readLine()) != null) {
                builder.append(readline).append(context.getLineSeparator());
            }

            return Optional.of(builder.toString());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not read all lines from included path '%s' (absolute: '%s')", pathString, path.toAbsolutePath()), e);
        }
    }
}
