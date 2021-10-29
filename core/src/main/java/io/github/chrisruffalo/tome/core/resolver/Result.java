package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Result {

    private final String resolved;

    private final List<Message> messages;

    public Result(String resolved, List<Message> messages) {
        this.resolved = resolved;
        this.messages = messages != null ? new LinkedList<>(messages) : Collections.emptyList();
    }

    public String getResolved() {
        return this.resolved;
    }

    public List<Message> getMessages(final Type ofType) {
        return this.getMessages().stream().filter(message -> ofType.equals(message.getType())).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return this.getMessages().stream().anyMatch(message -> Type.ERROR.equals(message.getType()));
    }

    public boolean hasWarnings() {
        return this.getMessages().stream().anyMatch(message -> Type.WARN.equals(message.getType()));
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
}
