package io.github.chrisruffalo.tome.core.source.bean;

import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.source.Value;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

public class BeanSource implements Source {

    private final Object bean;

    public BeanSource(Object bean) {
        this.bean = bean;
    }

    public Optional<Value> get(final String property) {
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
