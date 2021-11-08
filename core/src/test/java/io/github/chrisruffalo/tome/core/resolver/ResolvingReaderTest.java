package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.PropertySource;
import io.github.chrisruffalo.tome.test.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ResolvingReaderTest {

    private void testResolveAgainst(String inputFile, String expectedFile, final Configuration configuration) throws IOException {
        final Path source = TestUtil.getPathToTestResource(inputFile);
        final Path output = TestUtil.getTestOutputFile(source.getFileName().toString() + "_", ".output");

        try (
            final Reader processed = new ResolvingReader(Files.newBufferedReader(source), configuration);
            final Writer sink = Files.newBufferedWriter(output);
        ) {
            IOUtils.copy(processed, sink);
        }

        // file should be fundamentally unchanged
        final Path expected = TestUtil.getPathToTestResource(expectedFile);
        TestUtil.testTwoFilesLineByLine(expected, output);
    }

    @Test
    public void testSimplePropertiesResolution() throws IOException {
        final Properties properties = new Properties();
        properties.load(Files.newBufferedReader(TestUtil.getPathToTestResource("resolver/file.properties")));

        final Configuration configuration = new DefaultConfiguration();
        configuration.addSource(0, new PropertySource(properties));

        testResolveAgainst("resolver/file.txt", "resolver/expected/file.txt", configuration);
    }

    @Test
    public void testNullConfiguration() throws IOException {
        // this just runs against itself since nothing changes
        testResolveAgainst("resolver/file.txt", "resolver/file.txt", null);
    }

    @Test
    public void testEmptyConfiguration() throws IOException {
        // this just runs against itself since nothing changes
        testResolveAgainst("resolver/file.txt", "resolver/file.txt", new DefaultConfiguration());
    }

}
