package com.github.jknack.handlebars.i347;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue347 extends AbstractTest {
    @Test
    public void shouldEscapePropOnTopLevel() throws IOException {
        shouldCompileTo("{{ this.[foo bar] }}", AbstractTest.$("foo bar", "foo.bar"), "foo.bar");
        shouldCompileTo("{{ [foo bar] }}", AbstractTest.$("foo bar", "foo.bar"), "foo.bar");
    }
}

