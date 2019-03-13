package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue447 extends v4Test {
    @Test
    public void levels() throws IOException {
        shouldCompileTo("{{log 'message'}}", v4Test.$, "");
        shouldCompileTo("{{log 'a' 'b' 'c'}}", v4Test.$, "");
        shouldCompileTo("{{log 'message' level='info'}}", v4Test.$, "");
        shouldCompileTo("{{log 'message' level='debug'}}", v4Test.$, "");
        shouldCompileTo("{{log 'message' level='error'}}", v4Test.$, "");
        shouldCompileTo("{{log 'message' level='trace'}}", v4Test.$, "");
    }

    @Test
    public void logFn() throws IOException {
        shouldCompileTo("{{#log}}Name: {{name}}{{/log}}", v4Test.$("hash", v4Test.$("name", "John")), "");
    }
}

