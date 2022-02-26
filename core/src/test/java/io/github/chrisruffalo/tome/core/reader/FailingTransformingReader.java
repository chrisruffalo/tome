package io.github.chrisruffalo.tome.core.reader;

import java.io.IOException;
import java.io.Reader;

public class FailingTransformingReader  extends TransformingReader<FailingReaderContext> {

    public FailingTransformingReader(Reader in) {
        super(in);
    }

    @Override
    protected String transform(String line, FailingReaderContext context) throws IOException, TransformException {
        throw new TransformException();
    }

    @Override
    protected FailingReaderContext getCurrentContext() {
        return new FailingReaderContext();
    }
}
