package com.github.jknack.handlebars.i204;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue204 extends AbstractTest {
    @Test
    public void ifElseBlockMustBeIncludedInRawText() throws IOException {
        Assert.assertEquals("{{#if true}}true{{else}}false{{/if}}", compile("{{#if true}}true{{else}}false{{/if}}").text());
        Assert.assertEquals("{{#if true}}true{{^}}false{{/if}}", compile("{{#if true}}true{{^}}false{{/if}}").text());
    }
}

