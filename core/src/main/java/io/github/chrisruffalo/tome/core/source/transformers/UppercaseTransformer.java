package io.github.chrisruffalo.tome.core.source.transformers;

import java.util.Optional;

public class UppercaseTransformer implements PropertyTransformer {

    @Override
    public Optional<String> apply(String s) {
        if (s != null) {
            return Optional.of(s.toUpperCase());
        }
        return Optional.empty();
    }

}
