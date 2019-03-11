package com.github.jknack.handlebars.i418;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue418 extends AbstractTest {
    @Test
    public void lookupHelper() throws IOException {
        shouldCompileTo("{{#each series}} {{lookup ../types this}}{{/each}}", AbstractTest.$("series", new String[]{ "Test A", "Test B", "Test C" }, "types", AbstractTest.$("Test A", "bar", "Test B", "bar", "Test C", "line")), " bar bar line");
    }
}

