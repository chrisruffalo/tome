package io.github.chrisruffalo.tome.ee;

import io.github.chrisruffalo.tome.core.Configuration;
import io.github.chrisruffalo.tome.ee.annotations.Tome;
import io.github.chrisruffalo.tome.ee.annotations.TomeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FeatureBean {

    @Inject
    @Tome
    Configuration application;

    @Inject
    @Tome(name = "system")
    Configuration system;

    @Inject
    @TomeValue(property = "db.url")
    String dbUrl;

    public Configuration getApplication() {
        return application;
    }

    public void setApplication(Configuration application) {
        this.application = application;
    }

    public Configuration getSystem() {
        return system;
    }

    public void setSystem(Configuration system) {
        this.system = system;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
}
