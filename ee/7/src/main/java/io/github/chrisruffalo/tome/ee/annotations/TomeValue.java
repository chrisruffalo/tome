package io.github.chrisruffalo.tome.ee.annotations;

import io.github.chrisruffalo.tome.ee.core.annotations.TomeConfiguration;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TomeValue {

    @Nonbinding
    String configuration() default TomeConfiguration.DEFAULT_NAME;

    @Nonbinding
    String property() default "";

    @Nonbinding
    String format() default "";

    @Nonbinding
    String defaultValue() default "";

}
