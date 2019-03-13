package com.github.jknack.handlebars.i334;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue334 extends AbstractTest {
    @Test
    public void withHelperSpec() throws IOException {
        shouldCompileTo("{{#each this}}{{@first}}{{/each}}", AbstractTest.$("one", 1, "two", 2), "first");
    }
}

