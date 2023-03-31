package io.github.chrisruffalo.tome.core.source;

import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.log.SpiLoggerFactory;
import io.github.chrisruffalo.tome.core.resolver.ResolvingContext;

/**
 * The context for a source (get) action. This should provide
 * the api with a place to hang configuration and logging that
 * are related to the configuration instance.
 */
public class SourceContext {

    private LoggerFactory loggerFactory;

    public SourceContext() {
        this.loggerFactory = SpiLoggerFactory.get();
    }

    public void setLoggerFactory(final LoggerFactory log) {
        this.loggerFactory = log;
    }

    public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public static SourceContext from(final ResolvingContext resolvingContext) {
        final SourceContext context = new SourceContext();
        context.setLoggerFactory(resolvingContext.getLoggerFactory());
        return context;
    }
}
