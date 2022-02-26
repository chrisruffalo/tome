package io.github.chrisruffalo.tome.sops;

import io.gihub.chrisruffalo.tome.yaml.YamlSource;
import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.source.PrefixedSource;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SopsReaderTest {

    private static final SopsConfiguration conf = new SopsConfiguration();

    @BeforeAll
    public static void configure() {
        conf.setEnvironment(SopsConfiguration.ENV_SOPS_AGE_KEY_FILE, TestUtil.getPathToTestResource("keys.txt"));
    }

    @Test
    public void testFileInputSops() throws IOException {
        // set on this test because other tests don't need it since they use the configuration
        System.setProperty("sops.SOPS_AGE_KEY_FILE", TestUtil.getPathToTestResource("keys.txt").toString());

        final Path input = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource source = new YamlSource();
        try (final SopsReader reader = new SopsReader(input)) {
            source.load(reader);
        }

        Assertions.assertEquals("me", source.get(new SourceContext(), "db.user").orElse(new Value(null)).toString());
        Assertions.assertEquals("127.0.0.1:3306", source.get(new SourceContext(), "db.url").orElse(new Value(null)).toString());
        Assertions.assertEquals("creation", source.get(new SourceContext(), "db.password").orElse(new Value(null)).toString());
        Assertions.assertEquals("easy", source.get(new SourceContext(), "accounts[1].password").orElse(new Value(null)).toString());

        // done with this test so try and unset/remove the property
        System.setProperty("sops.SOPS_AGE_KEY_FILE", "");
    }

    @Test
    public void testReaderInputSops() throws IOException {
        final Path input = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource source = new YamlSource();
        try (final SopsReader reader = new SopsReader(conf, Files.newBufferedReader(input), SopsDataType.YAML)) {
            source.load(reader);
        }

        Assertions.assertEquals("me", source.get(new SourceContext(), "db.user").orElse(new Value(null)).toString());
        Assertions.assertEquals("127.0.0.1:3306", source.get(new SourceContext(), "db.url").orElse(new Value(null)).toString());
        Assertions.assertEquals("creation", source.get(new SourceContext(), "db.password").orElse(new Value(null)).toString());
        Assertions.assertEquals("easy", source.get(new SourceContext(), "accounts[1].password").orElse(new Value(null)).toString());
    }

    @Test
    public void testComposition() throws IOException {
        final Path sensitiveInput = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(sensitiveInput));
        final Path input = TestUtil.getPathToTestResource("shared.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource sensitiveSource = new YamlSource();
        try (final SopsReader reader = new SopsReader(conf, sensitiveInput)) {
            sensitiveSource.load(reader);
        }

        final YamlSource source = new YamlSource().load(input);

        final Configuration configuration = new DefaultConfiguration();
        configuration.addSource(source);
        configuration.addSource(new PrefixedSource("enc.", sensitiveSource));

        Assertions.assertEquals("postgres://me:creation@127.0.0.1:3306/default/dev", configuration.get("db.full_url").orElse(null));
    }

    @Test
    public void testFailedConfiguration() throws IOException {
        // create configuration
        final SopsConfiguration configuration = new SopsConfiguration(conf);
        // clone and override
        configuration.setEnvironment(SopsConfiguration.ENV_SOPS_AGE_KEY_FILE, Paths.get("keys.notthere"));
        // original configuration is unchanged
        Assertions.assertEquals(TestUtil.getPathToTestResource("keys.txt"), conf.getEnvironment().get(SopsConfiguration.ENV_SOPS_AGE_KEY_FILE.getKey()).getValue());

        final Path input = TestUtil.getPathToTestResource("sensitive.enc.yml");
        Assertions.assertTrue(Files.exists(input));

        final YamlSource source = new YamlSource();
        // create reader with configuration
        try (final SopsReader reader = new SopsReader(configuration, input)) {
            Assertions.assertThrows(RuntimeException.class, () -> source.load(reader));
        }
    }

}
