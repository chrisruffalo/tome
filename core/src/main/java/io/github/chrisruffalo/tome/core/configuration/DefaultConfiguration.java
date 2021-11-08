package io.github.chrisruffalo.tome.core.configuration;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.resolver.DefaultResolver;
import io.github.chrisruffalo.tome.core.resolver.Resolver;
import io.github.chrisruffalo.tome.core.resolver.Result;
import io.github.chrisruffalo.tome.core.source.PrioritizedSource;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.core.token.Handler;

import java.util.*;

public class DefaultConfiguration implements Configuration {

    private final List<PrioritizedSource> sources = new ArrayList<>(0);
    private Resolver resolver = new DefaultResolver();
    private Handler handler = new DefaultHandler();
    private Logger logger = null;

    public DefaultConfiguration() {

    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void addSource(int priority, Source source) {
        this.sources.add(new PrioritizedSource(priority, source));
        Collections.sort(this.sources);
    }

    @Override
    public void clearSources() {
        this.sources.clear();
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String format(String expression) {
        if (this.resolver == null || this.handler == null) {
            // if a logger is present then allow it to produce error messages
            if (logger != null) {
                if (this.resolver == null && this.handler == null) {
                    logger.error(String.format("The expression '%s' could not be resolved because the configuration is missing a resolver and a handler", expression));
                } else if (this.resolver == null) {
                    logger.error(String.format("The expression '%s' could not be resolved because the configuration is missing a resolver", expression));
                } else {
                    logger.error(String.format("The expression '%s' could not be resolved because the configuration is missing a handler", expression));
                }
            }
            return expression;
        }
        final Result result = this.resolver.resolve(expression, handler, this.sources.toArray(new PrioritizedSource[0]));
        if (logger != null) {
            result.getMessages().forEach(logger::handle);
        }
        return result.getResolved();
    }

    @Override
    public Optional<String> get(String property) {
        Optional<Optional<Value>> foundValue = this.sources.stream().map(source -> source.get(property)).filter(Optional::isPresent).findFirst();
        if (foundValue.isPresent() && foundValue.get().isPresent()) {
            Optional<Value> valueOptional = foundValue.get();
            final String expression = valueOptional.get().toString();
            return Optional.ofNullable(this.format(expression));
        }
        return Optional.empty();
    }
}
