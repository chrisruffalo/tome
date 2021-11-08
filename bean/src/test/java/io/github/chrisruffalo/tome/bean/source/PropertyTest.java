package io.github.chrisruffalo.tome.bean.source;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

public class PropertyTest {

    @Test
    public void testEmptyAndNull() {
        Assertions.assertNull(Property.parse(""));
        Assertions.assertNull(Property.parse(null));
    }

    @Test
    public void testSimpleProperty() {
        final Property simple = Property.parse("simpleProperty");
        Assertions.assertNotNull(simple);
        Assertions.assertEquals("simpleProperty", simple.segment());
        Assertions.assertFalse(simple.hasNext());
        Assertions.assertNull(simple.next());
    }

    @Test
    public void testMultiSegmentProperty() {
        final Property segmented = Property.parse("segment.segment2.segment3");
        Assertions.assertNotNull(segmented);
        Assertions.assertEquals("segment", segmented.segment());
        Assertions.assertTrue(segmented.hasNext());
        Assertions.assertEquals( "segment2", segmented.next().segment());
        Assertions.assertTrue(segmented.next().hasNext());
        Assertions.assertEquals( "segment3", segmented.next().next().segment());
        Assertions.assertFalse(segmented.next().next().hasNext());
    }

    @Test
    public void testIndexSegment() {
        final Property indexed = Property.parse("first.second[1231]");
        Assertions.assertTrue(indexed.hasNext());
        Assertions.assertEquals("second", indexed.next().segment());
        Assertions.assertTrue(indexed.next().hasNext());
        Property index = indexed.next().next();
        Assertions.assertEquals("[1231]", index.segment());

        Property both = Property.parse("first.second[1234][5678]");
        Assertions.assertNotNull(both.next().next());
        Assertions.assertNotNull(both.next().next().next());
    }

    @Test
    public void testComplex() {
        Property complex = Property.parse("first.second[1234][5678].key.third.fourth[123].nonsense[123]");
        final String[] segments = {
            "first",
            "second",
            "[1234]",
            "[5678]",
            "key",
            "third",
            "fourth",
            "[123]",
            "nonsense",
            "[123]"
        };

        int idx = 0;
        for (Property property : complex) {
            Assertions.assertEquals(segments[idx], property.segment(), String.format("The segment index %d should equal %s", idx, segments[idx]));
            idx++;
        }
    }

    @Test
    public void testUntilNull() {
        Property test = Property.parse("one.two.three");
        while(test != null) {
            test = test.next();
        }

        test = Property.parse("one.two.three");
        Iterator<Property> iterator = test.iterator();
        while(iterator.next() != null) {
            // deliberate empty for test case
        }
    }
}
