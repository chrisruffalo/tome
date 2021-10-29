package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.token.Handler;

/**
 * Resolve strings using given token handlers and value sources.
 */
public interface Resolver {

    /**
     * Given a string that may contain tokens: find the tokens and look up any values from the
     * given sources and replace the tokens with the found value.
     *
     * @param input given string that provides the input
     * @param handler the token handler that will find and split the tokens
     * @param sources the value sources that should be used
     * @return the string but with all the found tokens resolved or, if they cannot be resolved, the original token text
     */
    Result resolve(final String input, final Handler handler, final Source... sources);

}
