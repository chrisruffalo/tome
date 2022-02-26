package io.github.chrisruffalo.tome.core.source;

import io.github.chrisruffalo.tome.core.source.transformers.LowercaseTransformer;
import io.github.chrisruffalo.tome.core.source.transformers.UppercaseTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PropertyTransformingSourceTest {

    @Test
    public void testUppercase() {
        final Map<String, String> backer = new HashMap<>();
        backer.put("UPPER", "upper");
        backer.put("upper", "no");
        final Source backingSource = new MapSource(backer);

        // create a source that overlays the other with the transformer
        final Source source = new PropertyTransformingSource(backingSource, new UppercaseTransformer());
        Assertions.assertEquals("upper", source.get(new SourceContext(), "upper").orElse(new Value("")).toString());
        Assertions.assertEquals("upper", source.get(new SourceContext(), "uPper").orElse(new Value("")).toString());
        Assertions.assertEquals("upper", source.get(new SourceContext(), "UPPER").orElse(new Value("")).toString());
        Assertions.assertFalse(source.get(new SourceContext(), null).isPresent());
        Assertions.assertFalse(source.get(new SourceContext(), "").isPresent());

        // ensure the backing source doesn't do any of that after removal
        backer.remove("upper");
        Assertions.assertFalse(backingSource.get(new SourceContext(), "upper").isPresent());
        Assertions.assertFalse(backingSource.get(new SourceContext(), "uPpeR").isPresent());
        Assertions.assertTrue(backingSource.get(new SourceContext(), "UPPER").isPresent());
    }

    @Test
    public void testLowercase() {
        final Map<String, String> backer = new HashMap<>();
        backer.put("LOWER", "no");
        backer.put("lower", "lower");
        final Source backingSource = new MapSource(backer);

        // create a source that overlays the other with the transformer
        final Source source = new PropertyTransformingSource(backingSource, new LowercaseTransformer());
        Assertions.assertEquals("lower", source.get(new SourceContext(), "LOWER").orElse(new Value("")).toString());
        Assertions.assertEquals("lower", source.get(new SourceContext(), "loWer").orElse(new Value("")).toString());
        Assertions.assertEquals("lower", source.get(new SourceContext(), "lower").orElse(new Value("")).toString());
        Assertions.assertFalse(source.get(new SourceContext(), null).isPresent());
        Assertions.assertFalse(source.get(new SourceContext(), "").isPresent());

        // ensure the backing source doesn't do any of that after removal
        backer.remove("LOWER");
        Assertions.assertFalse(backingSource.get(new SourceContext(), "LOWER").isPresent());
        Assertions.assertFalse(backingSource.get(new SourceContext(), "lOwEr").isPresent());
        Assertions.assertTrue(backingSource.get(new SourceContext(), "lower").isPresent());
    }

}
