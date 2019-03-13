package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue483 extends v4Test {
    @Test
    public void shouldPassObjectResult() throws IOException {
        shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}", v4Test.$("hash", v4Test.$("arg", "foo"), "helpers", v4Test.$("equal", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String left, final Options options) throws IOException {
                return left.equals(options.param(0));
            }
        })), "foo");
        shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}", v4Test.$("hash", v4Test.$("arg", "bar"), "helpers", v4Test.$("equal", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String left, final Options options) throws IOException {
                return left.equals(options.param(0));
            }
        })), "");
    }
}

