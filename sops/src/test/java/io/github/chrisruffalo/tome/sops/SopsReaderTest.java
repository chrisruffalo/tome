package io.github.chrisruffalo.tome.sops;

import io.gihub.chrisruffalo.tome.yaml.YamlSource;
import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.PrefixedSource;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SopsReaderTest {

    @BeforeAll
    public static void configureKeys() {
        System.setProperty("sops.SOPS_AGE_KEY_FILE", TestUtil.getPathToTestResource("keys.txt").toString());
    }

    @Test
    public void testFileInputSops() throws IOException {
        final Path input = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource source = new YamlSource();
        try (final SopsReader reader = new SopsReader(input)) {
            source.load(reader);
        }

        Assertions.assertEquals("me", source.get("db.user").orElse(new Value(null)).toString());
        Assertions.assertEquals("127.0.0.1:3306", source.get("db.url").orElse(new Value(null)).toString());
        Assertions.assertEquals("creation", source.get("db.password").orElse(new Value(null)).toString());
        Assertions.assertEquals("easy", source.get("accounts[1].password").orElse(new Value(null)).toString());
    }

    @Test
    public void testReaderInputSops() throws IOException {
        final Path input = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource source = new YamlSource();
        try (final SopsReader reader = new SopsReader(Files.newBufferedReader(input), SopsDataType.YAML)) {
            source.load(reader);
        }

        Assertions.assertEquals("me", source.get("db.user").orElse(new Value(null)).toString());
        Assertions.assertEquals("127.0.0.1:3306", source.get("db.url").orElse(new Value(null)).toString());
        Assertions.assertEquals("creation", source.get("db.password").orElse(new Value(null)).toString());
        Assertions.assertEquals("easy", source.get("accounts[1].password").orElse(new Value(null)).toString());
    }

    @Test
    public void testComposition() throws IOException {
        final Path sensitiveInput = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(sensitiveInput));
        final Path input = TestUtil.getPathToTestResource("shared.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource sensitiveSource = new YamlSource();
        try (final SopsReader reader = new SopsReader(sensitiveInput)) {
            sensitiveSource.load(reader);
        }

        final YamlSource source = new YamlSource().load(input);

        final Configuration configuration = new DefaultConfiguration();
        configuration.addSource(source);
        configuration.addSource(new PrefixedSource("enc.", sensitiveSource));

        Assertions.assertEquals("postgres://me:creation@127.0.0.1:3306/default/dev", configuration.get("db.full_url").orElse(null));
    }

}
