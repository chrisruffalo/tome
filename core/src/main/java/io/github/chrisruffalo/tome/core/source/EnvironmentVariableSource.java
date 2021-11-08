package io.github.chrisruffalo.tome.core.source;

public class EnvironmentVariableSource extends MapSource {

    public EnvironmentVariableSource() {
        super(System.getenv());
    }

}
