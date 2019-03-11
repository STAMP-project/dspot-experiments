package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class EachObjectWithLast extends AbstractTest {
    @Test
    public void eachObjectWithLast() throws IOException {
        shouldCompileTo("{{#each goodbyes}}{{#if @last}}{{text}}! {{/if}}{{/each}}cruel {{world}}!", AbstractTest.$("goodbyes", AbstractTest.$("foo", AbstractTest.$("text", "goodbye"), "bar", AbstractTest.$("text", "Goodbye")), "world", "world"), "Goodbye! cruel world!");
    }
}

