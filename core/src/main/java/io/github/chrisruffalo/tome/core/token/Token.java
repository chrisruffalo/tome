package io.github.chrisruffalo.tome.core.token;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a "token" found in a string that is a candidate for value replacement
 */
public class Token {

    /**
     * The full text from the start of the start token string to the end of the
     * end token string
     */
    private String fullText;

    /**
     * The position from the containing string that this token starts at.
     */
    private int startIndex;

    /**
     * The text minus the start and end tokens.
     */
    private String innerText;

    /**
     * The characters that start the entire token
     */
    private String startToken;

    /**
     * The characters that end the token
     */
    private String endToken;

    /**
     * The character value that separates one property of a token from the next
     */
    private Character propertySeparator;

    /**
     * The list of split/separated parts in the token (the individual token parts). A property
     * may itself be a token and should be further parsed/resolved to find that.
     *
     */
    private List<Part> parts = new LinkedList<>();

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }

    public String getStartToken() {
        return startToken;
    }

    public void setStartToken(String startToken) {
        this.startToken = startToken;
    }

    public String getEndToken() {
        return endToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    public Character getPropertySeparator() {
        return propertySeparator;
    }

    public void setPropertySeparator(Character propertySeparator) {
        this.propertySeparator = propertySeparator;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
