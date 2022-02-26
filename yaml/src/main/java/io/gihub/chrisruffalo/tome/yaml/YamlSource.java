package io.gihub.chrisruffalo.tome.yaml;

import io.github.chrisruffalo.tome.bean.source.BeanSource;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A yaml source uses parsed yaml (parsed through
 * the load method) and a bean source delegate
 * to access the contents of the parsed yaml
 * as a map.
 */
public class YamlSource implements Source {

    private final Yaml yamlInstance;

    private BeanSource delegate;

    private Object read;

    public YamlSource() {
        this(new Yaml());
    }

    public YamlSource(final Yaml yaml) {
        this.yamlInstance = yaml;
        this.read = new Object();
        this.delegate = new BeanSource(read);
    }

    public YamlSource load(final Path path) throws IOException {
        try (
            final Reader input = Files.newBufferedReader(path)
        ) {
            return this.load(input);
        }
    }

    public YamlSource load(final String string) {
        this.read = yamlInstance.load(string);
        this.delegate = new BeanSource(this.read);
        return this;
    }

    public YamlSource load(final InputStream inputStream) {
        this.read = yamlInstance.load(inputStream);
        this.delegate = new BeanSource(this.read);
        return this;
    }

    public YamlSource load(final Reader reader) {
        this.read = yamlInstance.load(reader);
        this.delegate = new BeanSource(this.read);
        return this;
    }


    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {
        return this.delegate.get(new SourceContext(), propertyName);
    }
}
