package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue109 extends AbstractTest {
    @Test
    public void emptyStringMustacheBlock() throws IOException {
        shouldCompileTo("{{#empty}}truthy{{/empty}}", AbstractTest.$("empty", ""), "");
    }

    @Test
    public void emptyStringElseBlock() throws IOException {
        shouldCompileTo("{{^empty}}falsy{{/empty}}", AbstractTest.$("empty", ""), "falsy");
    }

    @Test
    public void emptyStringIfBlock() throws IOException {
        shouldCompileTo("{{#if empty}}truthy{{else}}falsy{{/if}}", AbstractTest.$("empty", ""), "falsy");
    }

    @Test
    public void noEmptyStringMustacheBlock() throws IOException {
        shouldCompileTo("{{#nonempty}}truthy{{/nonempty}}", AbstractTest.$("nonempty", "xyz"), "truthy");
    }

    @Test
    public void noEmptyStringElseBlock() throws IOException {
        shouldCompileTo("{{#nonempty}}falsy{{/nonempty}}", AbstractTest.$("nonempty", "xyz"), "falsy");
    }

    @Test
    public void noEmptyStringIfBlock() throws IOException {
        shouldCompileTo("{{#if nonempty}}truthy{{/if}}", AbstractTest.$("nonempty", "xyz"), "truthy");
    }

    @Test
    public void nullMustacheBlock() throws IOException {
        shouldCompileTo("{{#null}}truthy{{/null}}", AbstractTest.$, "");
    }

    @Test
    public void nullElseBlock() throws IOException {
        shouldCompileTo("{{^null}}falsy{{/null}}", AbstractTest.$, "falsy");
    }

    @Test
    public void nullIfBlock() throws IOException {
        shouldCompileTo("{{#if null}}truthy{{else}}falsy{{/if}}", AbstractTest.$, "falsy");
    }
}

