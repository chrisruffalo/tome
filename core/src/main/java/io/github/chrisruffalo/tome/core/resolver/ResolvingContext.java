package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.log.SpiLoggerFactory;
import io.github.chrisruffalo.tome.core.reader.TransformContext;

public class ResolvingContext implements TransformContext {

    private LoggerFactory loggerFactory;

    public ResolvingContext() {
        this.loggerFactory = SpiLoggerFactory.get();
    }

    public void setLoggerFactory(final LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public static ResolvingContext from(final Configuration configuration) {
        final ResolvingContext context = new ResolvingContext();
        context.setLoggerFactory(configuration.getLoggerFactory());
        return context;
    }

}
