package io.github.chrisruffalo.tome.test;

import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Shared utilities related to finding and opening files
 * during tests as well as anything else we can use to
 * prevent repeating code.
 */
public class TestUtil {

    private final static String JAVA_TMP_DIR_KEY = "java.io.tmpdir";
    private final static String SUB_DIR = "tome-tmp";

    public static Path getPathToTestResource(final String resource) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) {
            throw new RuntimeException(String.format("Could not find a path for resource '%s', check that it is available on the classpath or resource root", resource));
        }
        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Could not parse URL to URI '%s', if the path exists this is a bug", url));
        }
    }

    public static Path getTestOutputFile(final String name, final String suffix) throws IOException {
        final String tmpDir = System.getProperty(JAVA_TMP_DIR_KEY);
        if (tmpDir != null && !tmpDir.isEmpty()) {
            final Path dir = Paths.get(tmpDir, SUB_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            return Files.createTempFile(dir, name, suffix);
        } else {
            final Path target = Paths.get("target");
            if (Files.exists(target)) {
                final Path dir = Paths.get(target.toAbsolutePath().toString(), SUB_DIR);
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                return Files.createTempFile(dir, name, suffix);
            }
        }

        // use the tmp dir to output files but create some structure
        return Files.createTempFile(name, suffix);
    }

    /**
     * Performs assertions on each line of the file so that we can see what is wrong on a given line if needed
     * and also fail within the junit framework. This should ignore line ending differences.
     */
    public static void testTwoFilesLineByLine(final Path expectedFile, final Path actualFile) throws IOException {
        try (
            final BufferedReader expectedSource = Files.newBufferedReader(expectedFile);
            final BufferedReader actualSource = Files.newBufferedReader(actualFile);
        ) {
            int line = 1;
            String sourceLine = null;
            while((sourceLine = expectedSource.readLine()) != null) {
                Assertions.assertEquals(sourceLine, actualSource.readLine(), String.format("[%s] File content mismatch on line %d", actualFile, line));
                line++;
            }
            final String remainingLine = actualSource.readLine();
            if (remainingLine != null) {
                Assertions.fail(String.format("[%s] Read content on line %d '%s' after end of expected output file", actualFile, line, remainingLine));
            }
        }
    }

}
