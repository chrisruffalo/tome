package io.github.chrisruffalo.tome.core.resolver;

import io.github.chrisruffalo.tome.core.message.Message;
import io.github.chrisruffalo.tome.core.message.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Normally a class like this is not something you would test on its own
 * but several times there have been tests that have failed due to bad logic
 * in this class. This serves as more of a bellwether for correctness than
 * any other testing strategy.
 */
public class ResultTest {

    @Test
    public void testGetMessageOfTypes() {
        final String output = "";
        final List<Message> messages = new LinkedList<>();
        messages.add(new Message(Type.FINE, "fine1"));
        messages.add(new Message(Type.DEBUG, "debug1"));
        messages.add(new Message(Type.WARN, "warn1"));
        messages.add(new Message(Type.WARN, "warn2"));
        messages.add(new Message(Type.ERROR, "error1"));
        messages.add(new Message(Type.ERROR, "error2"));
        messages.add(new Message(Type.ERROR, "error3"));

        final Result result = new Result(output, messages);
        Assertions.assertEquals(1, result.getMessages(Type.FINE).size());
        Assertions.assertEquals(1, result.getMessages(Type.DEBUG).size());
        Assertions.assertEquals(0, result.getMessages(Type.INFO).size());
        Assertions.assertEquals(2, result.getMessages(Type.WARN).size());
        Assertions.assertEquals(3, result.getMessages(Type.ERROR).size());
    }

    @Test
    public void testHasX() {
        final String output = "";
        List<Message> messages = new LinkedList<>();
        Result result = new Result(output, messages);

        Assertions.assertFalse(result.hasErrors());
        Assertions.assertFalse(result.hasWarnings());

        messages.add(new Message(Type.ERROR, "error1"));
        result = new Result(output, messages);

        Assertions.assertTrue(result.hasErrors());
        Assertions.assertFalse(result.hasWarnings());

        messages.add(new Message(Type.WARN, "warn1"));
        result = new Result(output, messages);

        Assertions.assertTrue(result.hasErrors());
        Assertions.assertTrue(result.hasWarnings());

        messages.clear();

        messages.add(new Message(Type.WARN, "warn1"));
        result = new Result(output, messages);

        Assertions.assertFalse(result.hasErrors());
        Assertions.assertTrue(result.hasWarnings());
    }

}
