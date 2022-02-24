package io.github.chrisruffalo.tome.ee.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TomeConfiguration {

    String DEFAULT_NAME = "default";

    String name() default DEFAULT_NAME;

}
