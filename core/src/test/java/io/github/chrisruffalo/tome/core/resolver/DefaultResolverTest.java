package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.message.Type;
import io.github.chrisruffalo.tome.core.source.ExceptionSource;
import io.github.chrisruffalo.tome.core.source.MapSource;
import io.github.chrisruffalo.tome.core.source.Source;
import io.github.chrisruffalo.tome.core.token.DefaultHandler;
import io.github.chrisruffalo.tome.core.token.Handler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class DefaultResolverTest {

    @Test
    public void testNoTokens() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final Result result = resolver.resolve("same", handler);
        Assertions.assertEquals("same", result.getResolved());
        Assertions.assertFalse(result.hasErrors());
        Assertions.assertFalse(result.getMessages(Type.FINE).get(0).getMessage().isEmpty());
    }

    @Test
    public void testBasicResolution() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final HashMap<String, String> sourceMap = new HashMap<>();
        sourceMap.put("adjective", "good");
        final Source mapSource = new MapSource(sourceMap);

        Assertions.assertEquals("he is a ${adjective} dog", resolver.resolve("he is a ${adjective} dog", handler).getResolved());
        Assertions.assertEquals("he is a good dog", resolver.resolve("he is a ${adjective} dog", handler, mapSource).getResolved());
        Assertions.assertEquals("he is a good dog", resolver.resolve("he is a ${notfound | adjective} dog", handler, mapSource).getResolved());
        Assertions.assertEquals("he is a very good dog", resolver.resolve("he is a ${notfound | 'very ${adjective}'} dog", handler, mapSource).getResolved());
    }

    @Test
    public void testErroredResolution() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final Result result = resolver.resolve("there must be one ${token} that goes to a source", handler, new ExceptionSource());

        // ensure that an error was collected
        Assertions.assertTrue(result.hasErrors());
    }

    @Test
    public void testCachedResult() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final HashMap<String, String> sourceMap = new HashMap<>();
        sourceMap.put("adjective", "good");
        final Source mapSource = new MapSource(sourceMap);

        final Result result = resolver.resolve("he is a ${adjective} ${'${ adjective }'} dog", handler, mapSource);
        Assertions.assertEquals("he is a good good dog", result.getResolved());
    }

    @Test
    public void testDescentResolution() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final HashMap<String, String> sourceMap = new HashMap<>();
        sourceMap.put("adjective", "${betterAdjective}");
        sourceMap.put("betterAdjective", "${bestAdjective}");
        sourceMap.put("bestAdjective", "good");
        final Source mapSource = new MapSource(sourceMap);

        final Result result = resolver.resolve("he is a ${adjective} dog", handler, mapSource);
        Assertions.assertEquals("he is a good dog", result.getResolved());
    }

    @Test
    public void testRecursionProtection() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final HashMap<String, String> sourceMap = new HashMap<>();
        sourceMap.put("adjective", "${betterAdjective}");
        sourceMap.put("betterAdjective", "${bestAdjective}");
        sourceMap.put("bestAdjective", "${adjective}");
        final Source mapSource = new MapSource(sourceMap);

        Result result = resolver.resolve("he is a ${adjective} dog", handler, mapSource);
        Assertions.assertEquals("he is a ${adjective} dog", result.getResolved());

        result = resolver.resolve("he is a ${${'${adjective}'} | 'good'} dog", handler, mapSource);
        Assertions.assertTrue(result.hasWarnings());
        // TODO: make the following work
        //Assertions.assertEquals("he is a good dog", result.getResolved());
        Assertions.assertFalse(result.getMessages(Type.WARN).get(0).getMessage().isEmpty());
    }

    @Test
    public void testRecursionNextProperty() {
        final Handler handler = new DefaultHandler();
        final Resolver resolver = new DefaultResolver();
        final HashMap<String, String> sourceMap = new HashMap<>();
        sourceMap.put("adjective", "${betterAdjective}");
        sourceMap.put("betterAdjective", "${bestAdjective}");
        sourceMap.put("bestAdjective", "${adjective}");
        sourceMap.put("nonRecursive", "good");
        final Source mapSource = new MapSource(sourceMap);

        final Result result = resolver.resolve("he is a ${adjective | nonRecursive} dog", handler, mapSource);
        Assertions.assertEquals("he is a good dog", result.getResolved());
    }

}
