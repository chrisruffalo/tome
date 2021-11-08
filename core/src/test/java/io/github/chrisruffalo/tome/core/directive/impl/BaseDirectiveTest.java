package io.github.chrisruffalo.tome.core.directive.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BaseDirectiveTest {

    @Test
    public void testAsInDocumentation() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("indent=5 norecurse noresolve calculate = true frog type=yaml");

        Assertions.assertEquals("5", parameters.get("indent"));
        Assertions.assertEquals("yaml", parameters.get("type"));
        Assertions.assertEquals("true", parameters.get("calculate"));
        Assertions.assertTrue(parameters.containsKey("norecurse"));
        Assertions.assertTrue(parameters.containsKey("noresolve"));
        Assertions.assertTrue(parameters.containsKey("frog"));
    }

    @Test
    public void testSoloNoSpaces() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("indent=5");

        Assertions.assertEquals("5", parameters.get("indent"));
    }

    @Test
    public void testSoloSpaces() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("indent    =     5");

        Assertions.assertEquals("5", parameters.get("indent"));
    }

    @Test
    public void testSoloUnary() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("frog");

        Assertions.assertTrue(parameters.containsKey("frog"));
    }

    @Test
    public void testAfterUnary() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("frog indent=5");

        Assertions.assertTrue(parameters.containsKey("frog"));
        Assertions.assertEquals("5", parameters.get("indent"));
    }

    @Test
    public void testAfterUnaryWithSpaces() {
        final ExportingBaseDirective exportingBaseDirective = new ExportingBaseDirective();
        final Map<String, String> parameters = exportingBaseDirective.exportParseRemainingParameters("frog indent = 5");

        Assertions.assertTrue(parameters.containsKey("frog"));
        Assertions.assertEquals("5", parameters.get("indent"));
    }
}

