package com.github.jknack.handlebars;


import HelperRegistry.HELPER_MISSING;
import java.io.IOException;
import org.junit.Test;


public class HelperMissingTest extends AbstractTest {
    /**
     * Mustache fallback.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void helperMissingOk() throws IOException {
        shouldCompileTo("{{missing}}", new Object(), "");
    }

    @Test
    public void helperMissingName() throws IOException {
        shouldCompileTo("{{varx 7}}", AbstractTest.$, AbstractTest.$(HELPER_MISSING, new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.helperName;
            }
        }), "varx");
    }

    @Test
    public void helperBlockMissingName() throws IOException {
        shouldCompileTo("{{#varz 7}}{{/varz}}", AbstractTest.$, AbstractTest.$(HELPER_MISSING, new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.helperName;
            }
        }), "varz");
    }

    /**
     * Mustache fallback.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void blockHelperMissingOk() throws IOException {
        shouldCompileTo("{{#missing}}This is a mustache fallback{{/missing}}", new Object(), "");
    }

    /**
     * Handlebars syntax, it MUST fail.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = HandlebarsException.class)
    public void helperMissingFail() throws IOException {
        shouldCompileTo("{{missing x}}", new Object(), "must fail");
    }

    @Test(expected = HandlebarsException.class)
    public void blockHelperMissingFail() throws IOException {
        shouldCompileTo("{{#missing x}}This is a mustache fallback{{/missing}}", new Object(), "must fail");
    }

    @Test
    public void helperMissingOverride() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$(HELPER_MISSING, new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return "empty";
            }
        });
        shouldCompileTo("{{missing x}}", new Object(), helpers, "empty");
    }

    @Test
    public void blockHelperMissingOverride() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$(HELPER_MISSING, new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn.text();
            }
        });
        shouldCompileTo("{{#missing x}}Raw display{{/missing}}", new Object(), helpers, "Raw display");
    }
}

