package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.directive.impl.SimpleDirectiveConfiguration;
import io.github.chrisruffalo.tome.test.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectiveReaderTest {

    private void testProcessAgainst(String inputFile, String expectedFile) throws IOException {
        final SimpleDirectiveConfiguration configuration = new SimpleDirectiveConfiguration();

        // get/add directives path
        configuration.getRootPaths().add(TestUtil.getPathToTestResource("directives").toString());

        this.testProcessAgainst(inputFile, expectedFile, configuration);
    }

    private void testProcessAgainst(String inputFile, String expectedFile, final DirectiveConfiguration configuration) throws IOException {
        final Path source = TestUtil.getPathToTestResource(inputFile);
        final Path output = TestUtil.getTestOutputFile(source.getFileName().toString() + "_", ".output");

        try (
            final Reader processed = new DirectiveReader(Files.newBufferedReader(source), configuration);
            final Writer sink = Files.newBufferedWriter(output);
        ) {
            IOUtils.copy(processed, sink);
        }

        // file should be fundamentally unchanged
        final Path expected = TestUtil.getPathToTestResource(expectedFile);
        TestUtil.testTwoFilesLineByLine(expected, output);
    }

    @Test
    public void testNoDirectiveChange() throws IOException {
        testProcessAgainst("directives/plain.txt", "directives/plain.txt");
    }

    @Test
    public void testEmptyDirectiveNoChange() throws IOException {
        testProcessAgainst("directives/unchanged.txt", "directives/unchanged.txt");
    }

    @Test
    public void testIncludeEmptyFragment() throws IOException {
        testProcessAgainst("directives/include_empty.txt", "directives/expected/expected_empty.txt");
    }

    @Test
    public void testFlatInclude() throws IOException {
        testProcessAgainst("directives/include_flat.txt", "directives/expected/more_with_flat.txt");
    }

    @Test
    public void testSelfRecursion() throws IOException {
        testProcessAgainst("directives/self_recursion.txt", "directives/self_recursion.txt");
    }

    @Test
    public void testIndent() throws IOException {
        testProcessAgainst("directives/indent.yml", "directives/expected/indent.yml");
    }
}
