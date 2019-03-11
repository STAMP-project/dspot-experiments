package com.github.jknack.handlebars.i385;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue385 extends AbstractTest {
    @Test
    public void shouldHaveAccessToRoot() throws IOException {
        shouldCompileTo("{{#each array}}{{@root.foo}}{{/each}}", AbstractTest.$("foo", "bar", "array", new Object[]{ "foo" }), "bar");
    }
}

