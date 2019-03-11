package com.github.jknack.handlebars.i243;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue243 extends AbstractTest {
    @Test
    public void zeroValueForJavaScriptHelper() throws IOException {
        shouldCompileTo("{{#each item}}{{getIndex @index}} {{/each}}", AbstractTest.$("item", new Object[]{ 10, 20, 30 }), "0 1 2 ");
    }

    @Test
    public void nullValueForJavaScriptHelper() throws IOException {
        shouldCompileTo("{{nullHelper item}}", AbstractTest.$("item", null), "NULL");
        shouldCompileTo("{{nullHelper item}}", AbstractTest.$("item", new Object()), "NOT_NULL");
    }
}

