package io.github.chrisruffalo.tome.core.directive;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.core.reader.TransformContext;

import java.nio.file.Path;
import java.util.*;

/**
 * The settings / configuration that should be applied
 * to all directive actions.
 */
public class DirectiveContext implements TransformContext {

    private final Map<Class<? extends Directive>, Set<String>> visits = new HashMap<>();

    private String lineSeparator = System.lineSeparator();

    private Configuration configuration;

    private final DirectiveConfiguration directiveConfiguration;

    /**
     * A list of paths to search when looking for relative files / fragments / etc
     */
    private List<Path> roots = new LinkedList<>();

    public DirectiveContext(final DirectiveConfiguration directiveConfiguration) {
        this.directiveConfiguration = directiveConfiguration;
    }

    DirectiveContext(final DirectiveConfiguration directiveConfiguration, Map<Class<? extends Directive>, Set<String>> visits) {
        this(directiveConfiguration);
        this.visits.putAll(visits);
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

    public Optional<Configuration> getConfiguration() {
        return Optional.ofNullable(configuration);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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
        final DirectiveContext context = new DirectiveContext(this.getDirectiveConfiguration(), this.visits);
        context.setRoots(roots);
        return context;
    }

    public DirectiveConfiguration getDirectiveConfiguration() {
        return this.directiveConfiguration;
    }
}
