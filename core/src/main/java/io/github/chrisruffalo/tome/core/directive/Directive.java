package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.token.Token;

import java.io.IOException;
import java.util.Optional;

/**
 * A directive takes a token and turns it into a string for inclusion back into the
 * original file. This is a bit of an abuse of the token system and may grow into a
 * specialized implementation if needed.
 */
public interface Directive {

    /**
     * Turns a directive token into a string that represents the output of a directive.
     *
     * @param token the input token
     * @param context the directive context
     * @return a string if a difference was made, otherwise an empty optional
     */
    Optional<String> transform(final Token token, DirectiveContext context) throws IOException, DirectiveException;

}
