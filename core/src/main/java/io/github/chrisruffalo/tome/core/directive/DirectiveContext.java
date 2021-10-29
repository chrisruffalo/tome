package io.github.chrisruffalo.tome.core.directive;

import java.nio.file.Path;
import java.util.*;

/**
 * The settings / configuration that should be applied
 * to all directive actions.
 */
public class DirectiveContext {

    private final DirectiveHandler directiveHandler;

    private final Map<Class<? extends Directive>, Set<String>> visits = new HashMap<>();

    private String lineSeparator = System.lineSeparator();


    /**
     * A list of paths to search when looking for relative files / fragments / etc
     */
    private List<Path> roots = new LinkedList<>();

    public DirectiveContext(final DirectiveHandler directiveHandler) {
        this.directiveHandler = directiveHandler;
    }

    DirectiveContext(final DirectiveHandler directiveHandler, Map<Class<? extends Directive>, Set<String>> visits) {
        this.directiveHandler = directiveHandler;
        this.visits.putAll(visits);
    }

    public DirectiveHandler getDirectiveHandler() {
        return directiveHandler;
    }

    public void visit(final Class<? extends Directive> directiveClass, final String fullToken) {
        if(!this.visits.containsKey(directiveClass)) {
            this.visits.put(directiveClass, new HashSet<>());
        }
        this.visits.get(directiveClass).add(fullToken);
    }

    public boolean hasVisited(final Class<? extends Directive> directiveClass, final String fullToken) {
        return this.visits.containsKey(directiveClass) && this.visits.get(directiveClass).contains(fullToken);
    }

    public List<Path> getRoots() {
        return roots;
    }

    public void setRoots(List<Path> roots) {
        if (this.roots == null) {
            this.roots = new LinkedList<>();
        } else {
            this.roots.clear();
        }
        this.roots.addAll(roots);
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Used to split off another context when descending so that sibling token executions can re-resolve
     * the tokens but decedent ones can't trigger recursion.
     *
      * @return a copy of this context but that does not feed changes to the visit map or roots up to the parent context
     */
    public DirectiveContext split() {
        final DirectiveContext context = new DirectiveContext(this.getDirectiveHandler(), this.visits);
        context.setRoots(roots);
        return context;
    }
}
