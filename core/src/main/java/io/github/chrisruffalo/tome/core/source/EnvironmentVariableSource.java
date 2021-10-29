package io.github.chrisruffalo.tome.core.source;

import java.util.Map;

public class EnvironmentVariableSource extends MapSource {

    public EnvironmentVariableSource() {
        super(System.getenv());
    }

}
