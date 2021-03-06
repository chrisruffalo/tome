package io.github.chrisruffalo.tome.core.configuration;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.log.SpiLoggerFactory;
import io.github.chrisruffalo.tome.core.log.NoOpLogger;
import io.github.chrisruffalo.tome.core.resolver.DefaultResolver;
import io.github.chrisruffalo.tome.core.resolver.Resolver;
import io.github.chrisruffalo.tome.core.resolver.ResolvingContext;
import io.github.chrisruffalo.tome.core.resolver.Result;
import io.github.chrisruffalo.tome.core.source.PrioritizedSource;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.core.token.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultConfiguration implements Configuration {

    private final List<PrioritizedSource> sources = new ArrayList<>(0);
    private Resolver resolver = new DefaultResolver();
    private Handler handler = new DefaultHandler();
    private LoggerFactory loggerFactory = SpiLoggerFactory.get();

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
    public void setLoggerFactory(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    protected Logger getLogger() {
        if (this.loggerFactory == null) {
            return new NoOpLogger();
        }
        final Logger logger = loggerFactory.get(this.getClass());
        if (logger == null) {
            return new NoOpLogger();
        }
        return logger;
    }

    @Override
    public String format(String expression) {
        final Logger logger = getLogger();
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
        final Result result = this.resolver.resolve(ResolvingContext.from(this), expression, handler, this.sources.toArray(new PrioritizedSource[0]));
        if (logger != null) {
            result.getMessages().forEach(logger::handle);
        }
        return result.getResolved();
    }

    @Override
    public Optional<String> get(String property) {
        final ResolvingContext resolvingContext = ResolvingContext.from(this);
        Optional<Optional<Value>> foundValue = this.sources.stream().map(source -> source.get(SourceContext.from(resolvingContext), property)).filter(Optional::isPresent).findFirst();
        if (foundValue.isPresent() && foundValue.get().isPresent()) {
            Optional<Value> valueOptional = foundValue.get();
            final String expression = valueOptional.get().toString();
            return Optional.ofNullable(this.format(expression));
        }
        return Optional.empty();
    }
}
