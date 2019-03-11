package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue463b extends v4Test {
    @Test
    public void parentScopeResolution() throws IOException {
        shouldCompileTo("Hello {{#child}}{{value}}{{bestQB}}{{/child}}", v4Test.$("hash", v4Test.$("value", "Brett", "child", v4Test.$("bestQB", "Favre"))), "Hello BrettFavre");
    }
}

