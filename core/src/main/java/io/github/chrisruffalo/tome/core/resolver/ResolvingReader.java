package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.reader.TransformException;
import io.github.chrisruffalo.tome.core.reader.TransformingReader;

import java.io.IOException;
import java.io.Reader;

public class ResolvingReader extends TransformingReader<ResolvingContext> {

    final Configuration configuration;

    public ResolvingReader(final Reader in, Configuration configuration) {
        super(in);
        this.configuration = configuration;
    }

    @Override
    protected String transform(String line, ResolvingContext context) throws IOException, TransformException {
        if (configuration != null) {
            return this.configuration.format(line);
        }
        return line;
    }

    @Override
    protected ResolvingContext getCurrentContext() {
        return new ResolvingContext();
    }
}
