package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue459 extends v4Test {
    @Test
    public void stringLiteralInPartials() throws IOException {
        shouldCompileTo("{{> \"loop\"}}", v4Test.$("hash", v4Test.$("foo", "bar"), "partials", v4Test.$("loop", "{{foo}}")), "bar");
        shouldCompileTo("{{> 'loop'}}", v4Test.$("hash", v4Test.$("foo", "bar"), "partials", v4Test.$("loop", "{{foo}}")), "bar");
        shouldCompileTo("{{> \"loop\" this}}", v4Test.$("hash", v4Test.$("foo", "bar"), "partials", v4Test.$("loop", "{{foo}}")), "bar");
        shouldCompileTo("{{> \"loop\" h=1}}", v4Test.$("hash", v4Test.$("foo", "bar"), "partials", v4Test.$("loop", "{{foo}}{{h}}")), "bar1");
    }
}

