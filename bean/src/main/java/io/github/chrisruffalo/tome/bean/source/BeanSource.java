package io.github.chrisruffalo.tome.bean.source;

import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.SourceContext;
import io.github.chrisruffalo.tome.core.source.Value;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

/**
 * A bean source uses bean introspection to look up the
 * bean values. Properties are accessed via bean notation
 * like "parent.child.field" and similar according to the
 * rules of apache beans.
 */
public class BeanSource implements Source {

    private final Object bean;

    public BeanSource(Object bean) {
        this.bean = bean;
    }

    public Optional<Value> get(SourceContext sourceContext, final String property) {
        Property current = Property.parse(property);
        Object gotten = this.bean;
        while(current != null) {
            try {
                // we don't want a null from a map we want a does not contain
                if (gotten instanceof Map) {
                    final Map<?,?> gottenMap = (Map<?,?>)gotten;
                    if (!gottenMap.containsKey(current.segment())) {
                        return Optional.empty();
                    }
                }
                // continue with resolution
                gotten = PropertyUtils.getProperty(gotten, current.segment());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IndexOutOfBoundsException e) {
                // the value does not exist
                return Optional.empty();
            }
            if (gotten == null) {
                // if there are more segments but the current segment is null then it is a non-value
                if (current.hasNext()) {
                    return Optional.empty();
                }
                break;
            }
            current = current.next();
        }
        return Optional.of(new Value(gotten));
    }

}
