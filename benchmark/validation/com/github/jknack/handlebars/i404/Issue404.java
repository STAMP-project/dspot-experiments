package com.github.jknack.handlebars.i404;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue404 extends AbstractTest {
    @Test
    public void shouldEscapeVarInsideQuotes() throws IOException {
        shouldCompileTo("\"\\{{var}}\"", AbstractTest.$, "\"{{var}}\"");
        shouldCompileTo("<tag attribute=\"\\{{var}}\"/>", AbstractTest.$, "<tag attribute=\"{{var}}\"/>");
    }
}

