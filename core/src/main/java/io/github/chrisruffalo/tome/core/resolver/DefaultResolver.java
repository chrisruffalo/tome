package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import io.github.chrisruffalo.tome.core.token.Handler;
import io.github.chrisruffalo.tome.core.token.Part;
import io.github.chrisruffalo.tome.core.token.Token;

import java.util.*;

public class DefaultResolver implements Resolver {

    @Override
    public Result resolve(ResolvingContext resolvingContext, String input, Handler handler, Source... sources) {
        return this.resolve(resolvingContext, handler, input, new HashSet<>(), new HashSet<>(), new HashMap<>(), new LinkedList<>(), sources);
    }

    private Result resolve(final ResolvingContext resolvingContext, final Handler handler, String input, final Set<String> previousSteps, final Set<String> allreadySeenParts, final Map<String, String> cache, List<Message> messages, final Source[] sources) {

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
                final List<Part> parts = token.getParts();

                for(Part part : parts) {
                    if (cache.containsKey(part.getText())) {
                        input = this.replace(input, token.getFullText(), cache.get(part.getText()));
                        break;
                    }

                    if (allreadySeenParts.contains(part.getText())) {
                        messages.add(new Message(Type.WARN, "The token '%s' contains a part '%s' that is recursively resolved", token.getFullText(), part.getText()));
                        break;
                    }
                    allreadySeenParts.add(part.getText());

                    // do a resolve on the token itself and use it if something changed
                    if (handler.containsToken(part.getText())) {
                        final Result partResult = this.resolve(resolvingContext, handler, part.getText(), new HashSet<>(), new HashSet<>(allreadySeenParts), cache, new LinkedList<>(), sources);
                        final String resolvedString = partResult.getResolved();
                        if (!part.getText().equals(resolvedString)) {
                            messages.addAll(partResult.getMessages());
                            input = this.replace(input, token.getFullText(), resolvedString);
                            cache.put(part.getText(), resolvedString);
                            break;
                         }else if (partResult.hasErrors() || partResult.hasWarnings()) {
                            messages.addAll(partResult.getMessages());
                        }
                    } else if (part.isQuoted()) {
                        input = this.replace(input, token.getFullText(), part.getText());
                    }

                    // try and resolve in sources
                    for (final Source source : sources) {
                        try {
                            final Optional<Value> found = source.get(SourceContext.from(resolvingContext), part.getText());
                            if (found.isPresent()) {
                                String foundString = found.get().toString();
                                if (handler.containsToken(foundString)) {
                                    final Result partResult = this.resolve(resolvingContext, handler, foundString, new HashSet<>(), new HashSet<>(allreadySeenParts), cache, new LinkedList<>(), sources);
                                    final String resolvedString = partResult.getResolved();
                                    if (!foundString.equals(resolvedString)) {
                                        input = this.replace(input, token.getFullText(), resolvedString);
                                        cache.put(part.getText(), resolvedString);
                                    } else if (partResult.hasErrors() || partResult.hasWarnings()) {
                                        messages.addAll(partResult.getMessages());
                                    }
                                } else {
                                    input = this.replace(input, token.getFullText(), foundString);
                                    cache.put(part.getText(), foundString);
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
        return source != null ? source.replace(what, with) : null;
    }

}
