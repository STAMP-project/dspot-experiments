package com.github.jknack.handlebars.i294;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue294 extends AbstractTest {
    @Test
    public void escapeVars() throws IOException {
        shouldCompileTo("\\{{foo}}", AbstractTest.$, "{{foo}}");
    }

    @Test
    public void escapeVarsWithText() throws IOException {
        shouldCompileTo("before \\{{foo}} after", AbstractTest.$, "before {{foo}} after");
    }

    @Test
    public void escapeVsUnescape() throws IOException {
        shouldCompileTo("\\{{foo}} {{foo}}", AbstractTest.$("foo", "bar"), "{{foo}} bar");
    }

    @Test
    public void escapeMultiline() throws IOException {
        shouldCompileTo("\\{{foo\n}}", AbstractTest.$("foo", "bar"), "{{foo\n}}");
    }

    @Test
    public void blockEscape() throws IOException {
        shouldCompileTo("\\{{#foo}}", AbstractTest.$("foo", "bar"), "{{#foo}}");
    }

    @Test
    public void blockEscapeWithParams() throws IOException {
        shouldCompileTo("\\{{#foo x a x}}", AbstractTest.$("foo", "bar"), "{{#foo x a x}}");
    }

    @Test
    public void escapeVarToText() throws IOException {
        Assert.assertEquals("\\{{foo}}", compile("\\{{foo}}").text());
    }
}

