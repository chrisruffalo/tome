package io.github.chrisruffalo.tome.ee.annotations;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Tome {

    @Nonbinding
    String name() default io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration.DEFAULT_NAME;

}
