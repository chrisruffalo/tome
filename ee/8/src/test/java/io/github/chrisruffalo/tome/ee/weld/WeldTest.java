package io.github.chrisruffalo.tome.ee.weld;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.BeforeEach;

public class WeldTest {

    private static final Weld weld = new Weld();
    private WeldContainer container;

    @BeforeEach
    public void init() {
        if (this.container == null) {
            weld.addPackages(true, this.getClass().getPackage());
            this.container = weld.initialize();
        }
    }

    protected WeldContainer container() {
        return this.container;
    }

}
