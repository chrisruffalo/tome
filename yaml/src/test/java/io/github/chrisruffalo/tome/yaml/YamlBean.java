package io.github.chrisruffalo.tome.yaml;

import java.util.HashMap;
import java.util.Map;

public class YamlBean {

    private Map<String, String> env = new HashMap<>();

    private Application resolved;

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public Application getResolved() {
        return resolved;
    }

    public void setResolved(Application resolved) {
        this.resolved = resolved;
    }
}
