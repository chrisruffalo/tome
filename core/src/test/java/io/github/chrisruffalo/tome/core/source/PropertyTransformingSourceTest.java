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
        Assertions.assertEquals("upper", source.get("upper").orElse(new Value("")).toString());
        Assertions.assertEquals("upper", source.get("uPper").orElse(new Value("")).toString());
        Assertions.assertEquals("upper", source.get("UPPER").orElse(new Value("")).toString());
        Assertions.assertFalse(source.get(null).isPresent());
        Assertions.assertFalse(source.get("").isPresent());

        // ensure the backing source doesn't do any of that after removal
        backer.remove("upper");
        Assertions.assertFalse(backingSource.get("upper").isPresent());
        Assertions.assertFalse(backingSource.get("uPpeR").isPresent());
        Assertions.assertTrue(backingSource.get("UPPER").isPresent());
    }

    @Test
    public void testLowercase() {
        final Map<String, String> backer = new HashMap<>();
        backer.put("LOWER", "no");
        backer.put("lower", "lower");
        final Source backingSource = new MapSource(backer);

        // create a source that overlays the other with the transformer
        final Source source = new PropertyTransformingSource(backingSource, new LowercaseTransformer());
        Assertions.assertEquals("lower", source.get("LOWER").orElse(new Value("")).toString());
        Assertions.assertEquals("lower", source.get("loWer").orElse(new Value("")).toString());
        Assertions.assertEquals("lower", source.get("lower").orElse(new Value("")).toString());
        Assertions.assertFalse(source.get(null).isPresent());
        Assertions.assertFalse(source.get("").isPresent());

        // ensure the backing source doesn't do any of that after removal
        backer.remove("LOWER");
        Assertions.assertFalse(backingSource.get("LOWER").isPresent());
        Assertions.assertFalse(backingSource.get("lOwEr").isPresent());
        Assertions.assertTrue(backingSource.get("lower").isPresent());
    }

}
