package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Assert;
import org.junit.Test;


public class Issue614 extends v4Test {
    @Test
    public void shouldGetTextFromElseIf() throws Exception {
        String text = compile("{{#if a}}a{{else if b}}b{{else}}c{{/if}}").text();
        Assert.assertEquals("{{#if a}}a{{else if b}}b{{else}}c{{/if}}", text);
    }
}

