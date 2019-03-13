package com.github.jknack.handlebars.i310;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue310 extends AbstractTest {
    @Test
    public void commentWithClosingMustache() throws IOException {
        shouldCompileTo("{{!-- not a var}} --}}", AbstractTest.$, "");
    }

    @Test
    public void commentNotNestable() throws IOException {
        shouldCompileTo("{{! {{not}} a var}}", AbstractTest.$, " a var}}");
    }
}

