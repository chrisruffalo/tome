package io.github.chrisruffalo.tome.core.source.transformers;

import java.util.Optional;
import java.util.function.Function;

public interface PropertyTransformer extends Function<String, Optional<String>> {

}
