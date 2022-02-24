package io.github.chrisruffalo.tome.ee.weld;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class WeldTest {

    private static final Weld weld = new Weld();
    private WeldContainer container;

    @BeforeEach
    public void init() {
        if (this.container == null) {
            weld.addPackages(true, this.getClass());
            this.container = weld.initialize();
        }
    }

    @AfterEach
    public void cleanup() {
        this.container.shutdown();
        this.container = null;
    }

    protected WeldContainer container() {
        return this.container;
    }

}
