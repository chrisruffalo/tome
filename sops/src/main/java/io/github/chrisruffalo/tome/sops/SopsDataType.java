package io.github.chrisruffalo.tome.sops;

public enum SopsDataType {

    JSON("json"),
    YAML("yaml"),
    DOTENV("dotenv"),
    BINARY("binary")
    ; // prevents merge conflicts

    private final String argument;

    SopsDataType(final String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }
}
