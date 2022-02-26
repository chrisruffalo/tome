package io.github.chrisruffalo.tome.core.source;

import io.github.chrisruffalo.tome.core.log.Logger;
import io.github.chrisruffalo.tome.core.log.LoggerFactory;
import io.github.chrisruffalo.tome.core.resolver.ResolvingContext;

/**
 * The context for a source (get) action. This should provide
 * the api with a place to hang configuration and logging that
 * are related to the configuration instance.
 */
public class SourceContext {

    private Logger logger;

    public SourceContext() {
        this.logger = LoggerFactory.get();
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public static SourceContext from(final ResolvingContext resolvingContext) {
        final SourceContext context = new SourceContext();
        context.setLogger(resolvingContext.getLogger());
        return context;
    }
}
