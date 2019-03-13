package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class RawHelperTest extends AbstractTest {
    @Test
    public void helperForRawBlockGetsRawContent() throws IOException {
        shouldCompileTo("{{{{raw}}}} {{test}} {{{{/raw}}}}", AbstractTest.$, AbstractTest.$("raw", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn();
            }
        }), " {{test}} ");
    }

    @Test
    public void text() throws IOException {
        Assert.assertEquals("{{{{raw}}}} {{test}} {{{{/raw}}}}", compile("{{{{raw}}}} {{test}} {{{{/raw}}}}").text());
    }

    @Test
    public void helperForRawBlockGetsParameters() throws IOException {
        shouldCompileTo("{{{{raw 1 2 3}}}} {{test}} {{{{/raw}}}}", AbstractTest.$, AbstractTest.$("raw", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((options.fn().toString()) + context) + (options.param(0))) + (options.param(1));
            }
        }), " {{test}} 123");
    }
}

