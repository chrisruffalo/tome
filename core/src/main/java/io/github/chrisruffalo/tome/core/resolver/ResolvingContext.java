package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.reader.TransformContext;

public class ResolvingContext implements TransformContext {

    private Logger logger;

    public ResolvingContext() {
        this.logger = LoggerFactory.get();
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public static ResolvingContext from(final Configuration configuration) {
        final ResolvingContext context = new ResolvingContext();
        context.setLogger(configuration.getLogger());
        return context;
    }

}
