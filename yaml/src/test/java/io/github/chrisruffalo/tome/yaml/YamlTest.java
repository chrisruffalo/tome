package io.github.chrisruffalo.tome.yaml;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.configuration.DefaultConfiguration;
import io.github.chrisruffalo.tome.core.directive.DirectiveReader;
import io.github.chrisruffalo.tome.core.directive.impl.SimpleDirectiveConfiguration;
import io.github.chrisruffalo.tome.core.resolver.DefaultResolver;
import io.github.chrisruffalo.tome.core.resolver.Resolver;
import io.github.chrisruffalo.tome.core.resolver.ResolvingReader;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.core.source.bean.BeanSource;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.test.TestUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlTest {

    private void testFullResolve(String inputFile, String expectedFile, final Configuration configuration) throws IOException {
        final Path source = TestUtil.getPathToTestResource(inputFile);
        final Path output = TestUtil.getTestOutputFile(source.getFileName().toString() + "_", ".output");

        final SimpleDirectiveConfiguration directiveConfiguration = new SimpleDirectiveConfiguration();
        directiveConfiguration.setConfiguration(configuration);
        directiveConfiguration.getRootPaths().add(TestUtil.getPathToTestResource("").toAbsolutePath().toString());

        // load / bootstrap the configuration
        final Yaml yaml = new Yaml();
        try (final Reader yamlReader = new DirectiveReader(Files.newBufferedReader(source), directiveConfiguration) ){
            configuration.addSource(1000, new BeanSource(yaml.load(yamlReader)));
        }

        try (
            final Reader processed = new ResolvingReader(new DirectiveReader(Files.newBufferedReader(source), directiveConfiguration), configuration);
            final Writer sink = Files.newBufferedWriter(output);
        ) {
            IOUtils.copy(processed, sink);
        }

        // file should be fundamentally unchanged
        final Path expected = TestUtil.getPathToTestResource(expectedFile);
        TestUtil.testTwoFilesLineByLine(expected, output);
    }

    @Test
    public void testBeanSource() throws IOException {
        try (final Reader yamlReader = Files.newBufferedReader(TestUtil.getPathToTestResource("plain.yml")) ) {
            final Yaml yaml = new Yaml();
            final Source yamlSource = new BeanSource(yaml.load(yamlReader));

            Assertions.assertEquals("${ env.alt_host | 'google.com'}", yamlSource.get("env.host").orElse(new Value("no")).toString());

            final Resolver resolver = new DefaultResolver();
            Assertions.assertEquals("google.com", resolver.resolve(yamlSource.get("env.host").orElse(new Value("no")).toString(), new DefaultHandler(), yamlSource).getResolved());
        }
    }

    @Test
    public void testDirectiveLoading() throws IOException {
        final Configuration configuration = new DefaultConfiguration();
        testFullResolve("test.yml", "expected/expected.yml", configuration);
    }

    @Test
    public void testConfigurationLoad() throws IOException {
        // create configuration shell
        final Configuration configuration = new DefaultConfiguration();

        // create configuration for loading directives (allows resolving properties in directive commands)
        final SimpleDirectiveConfiguration directiveConfiguration = new SimpleDirectiveConfiguration();
        directiveConfiguration.getRootPaths().add(TestUtil.getPathToTestResource("").toAbsolutePath().toString());
        directiveConfiguration.setConfiguration(configuration);

        final Yaml yaml = new Yaml();
        YamlBean bean = null;

        // first load the configuration object using the bean as the source
        try (final Reader yamlReader = new DirectiveReader(Files.newBufferedReader(TestUtil.getPathToTestResource("test.yml")), directiveConfiguration)) {
            configuration.addSource(100, new BeanSource(yaml.load(yamlReader)));
        }

        // test formatting
        Assertions.assertEquals("connecting to 'http://google.com:8080/api' with user='admin' and password='nopasswordgiven'", configuration.format("connecting to '${resolved.url}' with user='${resolved.user}' and password='${resolved.pass}'"));
    }

    @Test
    public void testBeanLoad() throws IOException {
        // create configuration shell
        final Configuration configuration = new DefaultConfiguration();

        // create configuration for loading directives (allows resolving properties in directive commands)
        final SimpleDirectiveConfiguration directiveConfiguration = new SimpleDirectiveConfiguration();
        directiveConfiguration.getRootPaths().add(TestUtil.getPathToTestResource("").toAbsolutePath().toString());
        directiveConfiguration.setConfiguration(configuration);

        final Yaml yaml = new Yaml();
        YamlBean bean = null;

        // first load the configuration object using the bean as the source
        try (final Reader yamlReader = new DirectiveReader(Files.newBufferedReader(TestUtil.getPathToTestResource("test.yml")), directiveConfiguration)) {
            configuration.addSource(100, new BeanSource(yaml.load(yamlReader)));
        }

        // test formatting
        Assertions.assertEquals("connecting to 'http://google.com:8080/api' with user='admin' and password='nopasswordgiven'", configuration.format("connecting to '${resolved.url}' with user='${resolved.user}' and password='${resolved.pass}'"));

        // then, using the same approach, create a fully resolved reader
        try (
            final Reader yamlReader = new DirectiveReader(Files.newBufferedReader(TestUtil.getPathToTestResource("test.yml")), directiveConfiguration);
            final Reader resolvingReader = new ResolvingReader(yamlReader, configuration);
        ) {
            bean = yaml.loadAs(resolvingReader, YamlBean.class);
        }

        // ensure the bean has loaded fully resolved properties
        Assertions.assertNotNull(bean);
        Assertions.assertEquals("http://google.com:8080/api", bean.getResolved().getUrl());
        Assertions.assertEquals("admin", bean.getResolved().getUser());
        Assertions.assertEquals("nopasswordgiven", bean.getResolved().getPass());
    }

}
