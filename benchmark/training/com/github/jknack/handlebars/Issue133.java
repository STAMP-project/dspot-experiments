package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue133 extends AbstractTest {
    @Test
    public void issue133() throws IOException {
        shouldCompileTo("{{times nullvalue 3}}", AbstractTest.$("nullvalue", null), "");
        shouldCompileTo("{{times nullvalue 3}}", AbstractTest.$("nullvalue", "a"), "aaa");
    }
}

