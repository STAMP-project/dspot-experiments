package com.github.jknack.handlebars.i149;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class Issue149 extends AbstractTest {
    @Test
    public void negativeParam() throws IOException {
        shouldCompileTo("{{neg foo -1}}", AbstractTest.$("foo", "x"), AbstractTest.$("neg", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("" + context) + (options.param(0));
            }
        }), "x-1");
    }

    @Test
    public void negativeHash() throws IOException {
        shouldCompileTo("{{neg foo h=-1}}", AbstractTest.$("foo", "x"), AbstractTest.$("neg", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("" + context) + (options.hash("h"));
            }
        }), "x-1");
    }
}

