package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue181 extends AbstractTest {
    @Test
    public void blockWithContent() throws IOException {
        shouldCompileTo("{{#block \"name\"}}block{{/block}}", AbstractTest.$, "block");
    }

    @Test
    public void blockWithoutContent() throws IOException {
        shouldCompileTo("{{block \"name\"}}", AbstractTest.$, "");
    }

    @Test
    public void partialBlockWithContent() throws IOException {
        shouldCompileTo("{{#partial \"name\"}}partial{{/partial}}{{#block \"name\"}}block{{/block}}", AbstractTest.$, "partial");
    }

    @Test
    public void partialBlockWithoutContent() throws IOException {
        shouldCompileTo("{{#partial \"name\"}}partial{{/partial}}{{block \"name\"}}", AbstractTest.$, "partial");
    }

    @Test
    public void partialWithoutContentBlockWithContent() throws IOException {
        shouldCompileTo("{{partial \"name\"}}{{#block \"name\"}}block{{/block}}", AbstractTest.$, "block");
    }
}

