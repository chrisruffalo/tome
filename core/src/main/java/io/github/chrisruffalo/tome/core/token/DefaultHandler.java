package io.github.chrisruffalo.tome.core.token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A token handler that looks for tokens that start with the starting token and
 * end with the end token. Unbalanced tokens will produce variant behavior with
 * unclosed tokens returning an empty list and tokens with too many closures just
 * returning the first plausible set. This handler works from left to right.
 *
 * This handler is "quote aware" and will basically "turn off" parsing of quote
 * until it finds the next matching quote. Nested quotes should alternate like:
 * "then ' then ` then ` then ' then ". Having unbalanced / mismatched literals
 * will lead to similar issues as having mismatched tokens.
 *
 * Examples:
 * "the ${adjective} dog" -> returns token: {parts: adjective}
 * "the ${adjective | 'good'} dog" -> returns token: {parts: adjective, 'good'}
 * "the ${adjective | '${defaultAdjective}'} dog" -> returns token: {parts: adjective, '${defaultAdjective}'}
 */
public class DefaultHandler implements Handler {

    public static final String DEFAULT_START_TOKEN = "${";
    public static final String DEFAULT_END_TOKEN = "}";
    public static final Character DEFAULT_SEPARATOR = '|';

    private static final Set<Character> DEFAULT_QUOTES = new HashSet<Character>(){{
        add('\'');
        add('"');
        add('`');
    }};

    private final String startToken;

    private final String endToken;

    private final char separator;

    private final Set<Character> quotes = new HashSet<>(DEFAULT_QUOTES);

    public DefaultHandler() {
        this(DEFAULT_START_TOKEN, DEFAULT_END_TOKEN, DEFAULT_SEPARATOR);
    }

    public DefaultHandler(final String startToken, final String endToken) {
        this(startToken, endToken, DEFAULT_SEPARATOR);
    }

    public DefaultHandler(final String startToken, final String endToken, final Character separator) {
        this.startToken = startToken != null && !startToken.isEmpty() ? startToken : DEFAULT_START_TOKEN;
        this.endToken = endToken != null && !endToken.isEmpty() ? endToken : DEFAULT_END_TOKEN;
        this.separator = separator != null ? separator : DEFAULT_SEPARATOR;
    }

    @Override
    public List<Token> find(String source) {
        if (!this.containsToken(source)) {
            return Collections.emptyList();
        }

        final List<Token> tokens = new LinkedList<>();

        long depth = 0;
        Character currentQuote = null;
        Token currentToken = null;
        for(int idx = 0; idx < source.length(); idx++) {
            // determine if we are inside a token at all
            final boolean inToken = depth > 0;

            // handle special logic that only applies inside of a token at any depth
            if (inToken) {
                // if we are in a quoted block keep going until we find the end of the quoted block
                if (currentQuote != null && !currentQuote.equals(source.charAt(idx))) {
                    continue;
                } else if(currentQuote == null && quotes.contains(source.charAt(idx))) {
                    currentQuote = source.charAt(idx);
                    continue;
                }
                currentQuote = null;
            }

            if (depth >= 1 && source.substring(idx).startsWith(endToken)) {
                depth--;
                if (depth == 0) {
                    currentToken.setEndToken(endToken);
                    currentToken.setFullText(source.substring(currentToken.getStartIndex(), idx + currentToken.getEndToken().length()));
                    currentToken.setInnerText(currentToken.getFullText().substring(currentToken.getStartToken().length(), currentToken.getFullText().length() - currentToken.getEndToken().length()));
                    currentToken.setParts(this.separate(currentToken.getInnerText(), currentToken.getPropertySeparator()));
                    // only add to list if there are parts because one part at least is going to be required for resolution
                    if(!currentToken.getParts().isEmpty()) {
                        tokens.add(currentToken);
                    }
                    currentToken = null;
                    idx += endToken.length() - 1; // we do a -1 because the `continue` makes the for loop add it back
                    continue;
                }
            }

            if(source.substring(idx).startsWith(startToken)) {
                depth++;
                if (depth == 1) { // only start the new token when the depth is 1 after incrementing
                    currentToken = new Token();
                    currentToken.setStartToken(startToken);
                    currentToken.setStartIndex(idx);
                    currentToken.setPropertySeparator(separator);
                }
                idx += startToken.length() - 1; // we do a -1 because the `continue` makes the for loop add it back
            }
        }

        return tokens;
    }

    /**
     * When given the inner text of a token this should separate "parts" which are splits out from the original
     * inner text so that the resolver can walk through the parts and decide what to do based on each part.
     *
     * Each part is trimmed so that there is no leading or trailing space. This means that quoted blocks should
     * start and end with the same quote character but have no trailing or leading space. Token parts that
     * intend to have spaces in them should be quoted.
     *
     * @param innerText the full inner text
     * @return a list of the parts found, separated by this handlers split character
     */
    private List<Part> separate(final String innerText, final Character separator) {
        // there is no null guard here. null strings are a bug and should come out as a null pointer exception

        final List<Part> parts = new LinkedList<>();

        if (innerText.contains(String.valueOf(separator))) {
            // now we do a quote-aware and token aware split of the parts
            int depth = 0;
            StringBuilder partBuilder = new StringBuilder();
            Character currentQuote = null;
            for (int idx = 0; idx < innerText.length(); idx++) {
                // if we are in a quoted block keep going until we find the end of the quoted block
                if (currentQuote != null && !currentQuote.equals(innerText.charAt(idx))) {
                    partBuilder.append(innerText.charAt(idx));
                    continue;
                } else if (currentQuote == null && quotes.contains(innerText.charAt(idx))) {
                    currentQuote = innerText.charAt(idx);
                    partBuilder.append(innerText.charAt(idx));
                    continue;
                }
                currentQuote = null;

                if (depth >= 1 && innerText.substring(idx).startsWith(this.endToken)) {
                    depth--;
                    partBuilder.append(this.endToken);
                    idx += endToken.length() - 1; // we do a -1 because the `continue` makes the for loop add it back
                    continue;
                }

                if (innerText.substring(idx).startsWith(this.startToken)) {
                    depth++;
                    partBuilder.append(this.startToken);
                    idx += startToken.length() - 1; // we do a -1 because the `continue` makes the for loop add it back
                    continue;
                }

                // if the separator is the current text then add the part that is currently being built to
                // the list of parts and restart the loop
                if (depth == 0 && this.separator == innerText.charAt(idx)) {
                    parts.add(new Part(partBuilder.toString(), false));
                    partBuilder = new StringBuilder();
                    continue;
                }

                // continue building the part
                partBuilder.append(innerText.charAt(idx));
            }

            // if an uncompleted part is in the builder then add it
            String part = partBuilder.toString();
            if (!part.isEmpty()) {
                parts.add(new Part(part, false));
            }
        } else {
            parts.add(new Part(innerText, false));
        }

        // clean and collect parts
        return parts.stream()
            .filter(Objects::nonNull)
            .map(part -> {
                // trim the strings that were added as parts
                String partToMap = part.getText();
                partToMap = partToMap.trim();
                // if the string was quoted the individual part shouldn't be
                if (partToMap.length() >= 2 && quotes.contains(partToMap.charAt(0)) && partToMap.charAt(0) == partToMap.charAt(partToMap.length() - 1)) {
                    return new Part(partToMap.substring(1, partToMap.length() - 1), true);
                }
                return new Part(partToMap, part.isQuoted());
            })
            .filter(part -> !part.getText().isEmpty())
            .collect(Collectors.toList());
    }

    @Override
    public boolean containsToken(String source) {
        if (source == null || source.length() < (startToken.length() + endToken.length()) || !source.contains(startToken)) {
            return false;
        }

        final int afterStartTokenIndex = source.indexOf(startToken) + startToken.length();
        return source.substring(afterStartTokenIndex).contains(endToken)
              && !source.substring(afterStartTokenIndex, source.indexOf(endToken, afterStartTokenIndex)).trim().isEmpty()
              ;
    }
}
