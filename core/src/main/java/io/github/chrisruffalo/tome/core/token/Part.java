package io.github.chrisruffalo.tome.core.token;

/**
 * A part of a token. This is the result of splitting a token.
 */
public class Part {

    /**
     * This is the text of the part that determines what needs
     * to be done / parsed / replaced. This should not include
     * the encasing literals in the event a literal is used.
     */
    private final String text;

    /**
     * True if the original text was enclosed in something
     * recognized as a quote. (Meaning that if it has no
     * tokens inside it then it should be handled as a block
     * of literal text.)
     */
    private final boolean quoted;

    public Part(String text, boolean quoted) {
        this.text = text;
        this.quoted = quoted;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.getText();
    }

    public boolean isQuoted() {
        return quoted;
    }
}
