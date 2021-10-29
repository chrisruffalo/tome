package io.github.chrisruffalo.tome.core.token;

import java.util.List;

/**
 * An interface for finding tokens given a source string.
 */
public interface Handler {

    /**
     * Return a list of tokens found in a given string. Each token should
     * handle its own logic. The choice of finding nested strings or complex
     * strings is left to the implementation.
     *
     * @param source the source string
     * @return a list of tokens found in the source string. this list should never be null. an empty list signifies no tokens found.
     */
    List<Token> find(final String source);

    /**
     * Returns true if the target string contains at least one token. To put it more explicitly the string has a start
     * token, some part or part in the token, and then and end token. This method should attempt to be more optimized
     * than just `!find(source).isEmpty();`.
     *
     * @param source the source string
     * @return true if a valid token is contained in the string, false otherwise
     */
    boolean containsToken(final String source);

}
