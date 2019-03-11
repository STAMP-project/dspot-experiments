package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.v4Test;
import org.junit.Assert;
import org.junit.Test;


public class Issue634 extends v4Test {
    @Test
    public void shouldThrowHandlebarsExceptionWhenPartialBlockIsMissing() throws Exception {
        try {
            shouldCompileTo("{{> my-partial}}", v4Test.$("partials", v4Test.$("my-partial", "Hello {{> @partial-block}}")), null);
            Assert.fail("Must throw HandlebarsException");
        } catch (HandlebarsException x) {
            Assert.assertTrue(x.getMessage().contains("does not provide a @partial-block"));
        }
    }

    @Test
    public void shouldNotThrowHandlebarsException() throws Exception {
        shouldCompileTo("{{#> my-partial}}634{{/my-partial}}", v4Test.$("partials", v4Test.$("my-partial", "Hello {{> @partial-block}}")), "Hello 634");
    }
}

