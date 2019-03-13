package com.github.jknack.handlebars.i311;


import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;


public class Issue311 extends AbstractTest {
    @Test
    public void propertyWithPeriod() throws Exception {
        shouldCompileTo("{{ this.[foo.bar] }}", AbstractTest.$("foo.bar", "baz"), "baz");
    }
}

