package com.github.jknack.handlebars.i282;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class Issue282 extends AbstractTest {
    @Test(expected = HandlebarsException.class)
    public void missingSubexpression() throws Exception {
        AbstractTest.Hash helpers = AbstractTest.$("vowels", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return context.toString();
            }
        });
        shouldCompileTo("{{vowels (a)}}", AbstractTest.$, helpers, "");
    }

    @Test
    public void subexpression() throws Exception {
        AbstractTest.Hash helpers = AbstractTest.$("inner-helper", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((options.helperName) + "-") + context;
            }
        }, "outer-helper", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (context.toString()) + (options.params[0]);
            }
        });
        shouldCompileTo("{{outer-helper (inner-helper 'abc') 'def'}}", AbstractTest.$, helpers, "inner-helper-abcdef");
    }

    @Test
    public void vowels() throws Exception {
        AbstractTest.Hash helpers = AbstractTest.$("a", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (options.helperName) + context;
            }
        }, "e", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (options.helperName) + context;
            }
        }, "i", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (options.helperName) + context;
            }
        }, "o", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (options.helperName) + context;
            }
        }, "u", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.helperName;
            }
        }, "vowels", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return context.toString();
            }
        });
        shouldCompileTo("{{vowels (a (e (i (o (u)))))}}", AbstractTest.$, helpers, "aeiou");
    }

    @Test
    public void vowelsWithParams() throws Exception {
        AbstractTest.Hash helpers = AbstractTest.$("a", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((options.helperName) + ":") + (options.params[0])) + context;
            }
        }, "e", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((options.helperName) + ":") + (options.params[0])) + context;
            }
        }, "i", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((options.helperName) + ":") + (options.params[0])) + context;
            }
        }, "o", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((options.helperName) + ":") + (options.params[0])) + context;
            }
        }, "u", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((options.helperName) + ":") + context;
            }
        }, "vowels", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return context.toString();
            }
        });
        shouldCompileTo("{{vowels (a (e (i (o (u 5) 4) 3) 2) 1)}}", AbstractTest.$, helpers, "a:1e:2i:3o:4u:5");
    }
}

