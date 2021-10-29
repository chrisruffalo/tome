package io.github.chrisruffalo.tome.core.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DefaultHandlerTest {

    @Test
    public void basicHasTokenTest() {
        final DefaultHandler handler = new DefaultHandler();

        // these have a token
        Assertions.assertTrue(handler.containsToken("the ${adjective} dog"));
        Assertions.assertTrue(handler.containsToken("${                test                }"));
        Assertions.assertTrue(handler.containsToken("${t}"));
        Assertions.assertTrue(handler.containsToken("${adjective}"));

        // these do not
        Assertions.assertFalse(handler.containsToken(null));
        Assertions.assertFalse(handler.containsToken(""));
        Assertions.assertFalse(handler.containsToken("${"));
        Assertions.assertFalse(handler.containsToken("${}"));
        Assertions.assertFalse(handler.containsToken("${ }"));
        Assertions.assertFalse(handler.containsToken("${                                }"));
        Assertions.assertFalse(handler.containsToken("}${"));
    }

    @Test
    public void testSimpleToken() {
        final DefaultHandler handler = new DefaultHandler();
        final Token token = handler.find("a ${token}").get(0);

        Assertions.assertEquals("${token}", token.getFullText());
        Assertions.assertEquals("token", token.getInnerText());
    }

    @Test
    public void testNoTokens() {
        final DefaultHandler handler = new DefaultHandler();
        Assertions.assertTrue(handler.find(null).isEmpty());
        Assertions.assertTrue(handler.find("").isEmpty());
        Assertions.assertTrue(handler.find("this is just a sentence").isEmpty());
        Assertions.assertTrue(handler.find("this is just a start ${").isEmpty());
        Assertions.assertTrue(handler.find("this is just an end }").isEmpty());
    }

    @Test
    public void testTokenCounting() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("one ${token} two ${token} and ${more}");
        Assertions.assertEquals(3, tokens.size());
        tokens = handler.find("${token}${token}${more}");
        Assertions.assertEquals(3, tokens.size());
        tokens = handler.find("${${token}}${token}${more}");
        Assertions.assertEquals(3, tokens.size());
    }

    @Test
    public void testNestingTokens() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${${${nest}}}");
        Assertions.assertEquals("${${${nest}}}", tokens.get(0).getFullText());
        Assertions.assertEquals("${${nest}}", tokens.get(0).getInnerText());

        // first level of finding the inner parts
        tokens = handler.find(tokens.get(0).getParts().get(0));
        Assertions.assertEquals("${${nest}}", tokens.get(0).getFullText());
        Assertions.assertEquals("${nest}", tokens.get(0).getInnerText());
        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals(1, tokens.get(0).getParts().size());
        Assertions.assertEquals("${nest}", tokens.get(0).getInnerText());
        Assertions.assertEquals("${nest}", tokens.get(0).getParts().get(0));

        // second level of finding the inner parts
        tokens = handler.find(tokens.get(0).getParts().get(0));
        Assertions.assertEquals("${nest}", tokens.get(0).getFullText());
        Assertions.assertEquals("nest", tokens.get(0).getInnerText());
        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals(1, tokens.get(0).getParts().size());
        Assertions.assertEquals("nest", tokens.get(0).getParts().get(0));
    }

    @Test
    public void testSimpleSplit() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${ property1 | property2 }");
        Token token = tokens.get(0);

        Assertions.assertEquals("${ property1 | property2 }", token.getFullText());
        Assertions.assertEquals(" property1 | property2 ", token.getInnerText());
        Assertions.assertEquals(2, token.getParts().size());
        Assertions.assertEquals("property1", token.getParts().get(0));
        Assertions.assertEquals("property2", token.getParts().get(1));
    }

    @Test
    public void testNestedSplit() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${ property1 | ${property2a | property2b} | ${property3} }");
        Token token = tokens.get(0);

        Assertions.assertEquals(3, token.getParts().size());
        Assertions.assertEquals("property1", token.getParts().get(0));
        Assertions.assertEquals("${property2a | property2b}", token.getParts().get(1));
        Assertions.assertEquals("${property3}", token.getParts().get(2));
    }

    @Test
    public void testQuotedPart() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${ 'property1' }");
        Token token = tokens.get(0);
        Assertions.assertEquals("property1", token.getParts().get(0));
    }

    @Test
    public void testQuotedSplit() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${ 'property1 | ${property2a | property2b} | ${property3} '    |     property2}");
        Token token = tokens.get(0);

        Assertions.assertEquals(2, token.getParts().size());
        Assertions.assertEquals("property1 | ${property2a | property2b} | ${property3} ", token.getParts().get(0));
        Assertions.assertEquals("property2", token.getParts().get(1));
    }

    @Test
    public void testDifferentSeparator() {
        final DefaultHandler handler = new DefaultHandler(DefaultHandler.DEFAULT_START_TOKEN, DefaultHandler.DEFAULT_END_TOKEN, ':');
        List<Token> tokens = handler.find("${ 'property1 : ${property2a : property2b} : ${property3} '    :     property2}");
        Token token = tokens.get(0);

        Assertions.assertEquals(2, token.getParts().size());
        Assertions.assertEquals("property1 : ${property2a : property2b} : ${property3} ", token.getParts().get(0));
        Assertions.assertEquals("property2", token.getParts().get(1));
    }

    @Test
    public void testDifferentStartEndTokens() {
        final DefaultHandler handler = new DefaultHandler("{{", "}}");
        List<Token> tokens = handler.find("{{ property1 | property2 }}");
        Token token = tokens.get(0);

        Assertions.assertEquals("{{ property1 | property2 }}", token.getFullText());
        Assertions.assertEquals(" property1 | property2 ", token.getInnerText());
        Assertions.assertEquals(2, token.getParts().size());
        Assertions.assertEquals("property1", token.getParts().get(0));
        Assertions.assertEquals("property2", token.getParts().get(1));
    }

    @Test
    public void testDifferentStartEndTokensAndSeparator() {
        final DefaultHandler handler = new DefaultHandler("{{", "}}", ':');
        List<Token> tokens = handler.find("{{ property1 : property2 }}");
        Token token = tokens.get(0);

        Assertions.assertEquals("{{ property1 : property2 }}", token.getFullText());
        Assertions.assertEquals(" property1 : property2 ", token.getInnerText());
        Assertions.assertEquals(2, token.getParts().size());
        Assertions.assertEquals("property1", token.getParts().get(0));
        Assertions.assertEquals("property2", token.getParts().get(1));
    }

    @Test
    public void testUnbalancedQuotes() {
        final DefaultHandler handler = new DefaultHandler();
        List<Token> tokens = handler.find("${ 'gonna start a quote and end it without an end }");
        Assertions.assertEquals(0, tokens.size());

        tokens = handler.find("${ 'gonna start a quote and end it without another \" }");
        Assertions.assertEquals(0, tokens.size());

        tokens = handler.find("${ 'gonna start a quote and end it \" after a mismatched quote in the middle' }");
        Assertions.assertEquals(1, tokens.size());

        tokens = handler.find("${ 'gonna start a quote and end it ` after a mismatched quote in the middle' }");
        Assertions.assertEquals(1, tokens.size());

        tokens = handler.find("${ \"gonna start a quote and end it ` after a mismatched quote in the middle\" }");
        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals("gonna start a quote and end it ` after a mismatched quote in the middle", tokens.get(0).getParts().get(0));
    }

    @Test
    public void testSameToken() {
        final DefaultHandler handler = new DefaultHandler("@@", "@@");
        // these have a token
        Assertions.assertTrue(handler.containsToken("the @@adjective@@ dog"));
        Assertions.assertTrue(handler.containsToken("@@                test                @@"));
        Assertions.assertTrue(handler.containsToken("@@t@@"));
        Assertions.assertTrue(handler.containsToken("@@adjective@@"));

        // these do not
        Assertions.assertFalse(handler.containsToken(null));
        Assertions.assertFalse(handler.containsToken(""));
        Assertions.assertFalse(handler.containsToken("@@"));
        Assertions.assertFalse(handler.containsToken("@@@"));
        Assertions.assertFalse(handler.containsToken("@@ @@"));
        Assertions.assertFalse(handler.containsToken("@@                                @@"));
        Assertions.assertFalse(handler.containsToken("@@@@"));

        // find a token
        Token token = handler.find("a @@ token @@").get(0);
        Assertions.assertEquals("@@ token @@", token.getFullText());
        Assertions.assertEquals(" token ", token.getInnerText());

        // no nesting is possible
        Assertions.assertEquals(0, handler.find("a @@ @@token@@ @@").size());
    }

}
