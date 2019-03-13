package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Test;


public class Issue552 extends v4Test {
    @Test
    public void shouldKeepContextOnBlockParameter() throws Exception {
        shouldCompileTo("{{> button size='large'}}", v4Test.$("hash", v4Test.$, "partials", v4Test.$("button", ("<button class=\"button-{{size}}>\n" + ("    Button with size {{size}}\n" + "</button>")))), ("<button class=\"button-large>\n" + ("    Button with size large\n" + "</button>")));
    }
}

