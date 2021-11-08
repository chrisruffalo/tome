package io.github.chrisruffalo.tome.core.directive.impl;

import io.github.chrisruffalo.tome.core.directive.Directive;
import io.github.chrisruffalo.tome.core.token.Part;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class BaseDirective implements Directive {

    private static final String EQUALS = "=";

    /**
     * Directly take parts and turn into a map of values starting at some index
     *
     * @param remaining the remaining parts
     * @param fromIndex the index starting after the directive has read all specific info
     * @return a map of parameters to the directive
     */
    protected Map<String, String> convertPartsToRemainingParameters(List<Part> remaining, int fromIndex) {
        final StringBuilder builder = new StringBuilder();
        for (int idx = fromIndex; idx < remaining.size(); idx++) {
            if(!builder.toString().isEmpty()) {
                builder.append(" ");
            }
            builder.append(remaining.get(idx));
        }
        return parseRemainingParameters(builder.toString());
    }

    /**
     * Given a string, parse any commands ("x = y" or "x=y" or "x   =         y") out of the
     * string in a map that can be used to determine the values. Strings where the next value
     * is not an = sign are put in the map alone.
     *
     * Expect a string like "indent=5 norecurse noresolve type=yaml" to parse to a map like:
     *   indent     => 5
     *   norecurse  => null
     *   noresolve  => null
     *   type       => yaml
     *
     * @param remainder the remaining string parts
     * @return a map of values contained in the string
     */
    protected Map<String, String> parseRemainingParameters(String remainder) {
        final Map<String, String> values = new HashMap<>();
        if(remainder == null || remainder.isEmpty()) {
            return values;
        }

        final Scanner scanner = new Scanner(remainder);
        String key = null;
        String previous = null;
        while (scanner.hasNext()) {
            final String current = scanner.next();

            // when we hit an equal sign the previous
            // character becomes the key
            if(EQUALS.equals(current)) {
                key = previous;
                previous = null;
                continue;
            }

            // this is of the form 'x=y' with no spaces
            if(current.contains(EQUALS)) {
                // if a previous value was found it should
                // be added as a key because this value
                // has a self-contained EQUALS
                if(previous != null) {
                    values.put(previous, null);
                    previous = null;
                }

                final String[] split = current.split(EQUALS);
                if (split.length == 2 && split[0].length() > 0 && split[1].length() > 0) {
                    values.put(split[0], split[1]);
                }
                continue;
            }

            if (key != null && !current.isEmpty()) {
                values.put(key, current);
                key = null;
            } else {
                // previous is going out of scope without
                // being assigned and it should be used
                // as a unary parameter
                if (previous != null) {
                    values.put(previous, null);
                }
                previous = current;
            }
        }

        // if previous value is not null
        // then add it to the map
        if(previous != null) {
            values.put(previous, null);
        }

        return values;
    }

}
