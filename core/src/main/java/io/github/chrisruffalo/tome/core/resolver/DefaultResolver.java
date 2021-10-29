package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.core.token.Handler;
import io.github.chrisruffalo.tome.core.token.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class DefaultResolver implements Resolver {

    @Override
    public Result resolve(String input, Handler handler, Source... sources) {
        return this.resolve(handler, input, new HashSet<>(), new HashSet<>(), new HashMap<>(), new LinkedList<>(), sources);
    }


    private Result resolve(final Handler handler, String input, final Set<String> previousSteps, final Set<String> allreadySeenParts, final Map<String, String> cache, List<Message> messages, final Source[] sources) {

        // if there are no tokens in the input return output with a trace denoting that
        if (!handler.containsToken(input)) {
            messages.add(new Message(Type.FINE, "No tokens were found in the input '%s'", input));
            return new Result(input, messages);
        }

        while(!previousSteps.contains(input)) {
            // add the current step as the previous step
            previousSteps.add(input);

            final List<Token> tokens = handler.find(input);
            for(final Token token : tokens) {
                // get token parts to walk through each part for resolution
                final List<String> parts = token.getParts();

                for(String part : parts) {
                    if (cache.containsKey(part)) {
                        input = this.replace(input, token.getFullText(), cache.get(part));
                        break;
                    }

                    if (allreadySeenParts.contains(part)) {
                        messages.add(new Message(Type.WARN, "The token '%s' contains a part '%s' that is recursively resolved", token.getFullText(), part));
                        break;
                    }
                    allreadySeenParts.add(part);

                    // do a resolve on the token itself and use it if something changed
                    if (handler.containsToken(part)) {
                        final Result partResult = this.resolve(handler, part, new HashSet<>(), new HashSet<>(allreadySeenParts), cache, new LinkedList<>(), sources);
                        final String resolvedString = partResult.getResolved();
                        if (!part.equals(resolvedString)) {
                            messages.addAll(partResult.getMessages());
                            input = this.replace(input, token.getFullText(), resolvedString);
                            cache.put(part, resolvedString);
                            break;
                         }else if (partResult.hasErrors() || partResult.hasWarnings()) {
                            messages.addAll(partResult.getMessages());
                        }
                    }

                    // try and resolve in sources
                    for (final Source source : sources) {
                        try {
                            final Optional<Value> found = source.get(part);
                            if (found.isPresent()) {
                                String foundString = found.get().toString();
                                if (handler.containsToken(foundString)) {
                                    final Result partResult = this.resolve(handler, foundString, new HashSet<>(), new HashSet<>(allreadySeenParts), cache, new LinkedList<>(), sources);
                                    final String resolvedString = partResult.getResolved();
                                    if (!foundString.equals(resolvedString)) {
                                        input = this.replace(input, token.getFullText(), resolvedString);
                                        cache.put(part, resolvedString);
                                    } else if (partResult.hasErrors() || partResult.hasWarnings()) {
                                        messages.addAll(partResult.getMessages());
                                    }
                                } else {
                                    input = this.replace(input, token.getFullText(), foundString);
                                    cache.put(part, foundString);
                                }
                                break;
                            }
                        } catch (Exception ex) {
                            messages.add(new Message("Error found while using source %s: %s", ex, source.getClass().getName(), ex.getMessage()));
                        }
                    }
                }
            }
        }

        return new Result(input, messages);
    }

    private String replace(final String source, final String what, final String with) {
        return StringUtils.replace(source, what, with);
    }

}
