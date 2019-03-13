package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue463 extends v4Test {
    @Test
    public void parentScopeResolution() throws IOException {
        shouldCompileTo("Hello {{#child}}{{value}}{{bestQB}}{{/child}}", v4Test.$("hash", v4Test.$("value", "Brett", "child", v4Test.$("bestQB", "Favre"))), "Hello Favre");
    }

    @Test
    public void parentScopeResolutionDataContext() throws IOException {
        shouldCompileTo("{{#each p.list}}{{@index}}.{{title}}.{{/each}}", v4Test.$("hash", v4Test.$("p", v4Test.$("list", new Object[]{ v4Test.$("title", "A"), v4Test.$("title", "B") }))), "0.A.1.B.");
    }
}

