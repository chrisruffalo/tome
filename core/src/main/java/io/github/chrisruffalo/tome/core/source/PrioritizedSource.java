package io.github.chrisruffalo.tome.core.source;

import java.util.Optional;

/**
 *
 */
public class PrioritizedSource implements Source, Comparable<PrioritizedSource> {

    private int priority;

    private final Source internal;

    public PrioritizedSource(final int priority, Source internal) {
        this.internal = internal;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public Optional<Value> get(SourceContext sourceContext, String propertyName) {
        return internal.get(sourceContext, propertyName);
    }

    @Override
    public int compareTo(PrioritizedSource o) {
        return Integer.compare(o.getPriority(), this.getPriority());
    }
}
