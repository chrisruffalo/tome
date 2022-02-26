package io.github.chrisruffalo.tome.core.reader;

import io.github.chrisruffalo.tome.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class TransformingReaderTest {

    @Test
    public void testException() throws IOException {
        final Path source = TestUtil.getPathToTestResource("resolver/file.txt");
        try (
            final Reader processed = new FailingTransformingReader(Files.newBufferedReader(source));
        ) {
            processed.read(new char[1024], 0, 100);
        } catch (IOException ex) {
            Assertions.assertInstanceOf(TransformException.class, ex.getCause());
        }
    }

}
