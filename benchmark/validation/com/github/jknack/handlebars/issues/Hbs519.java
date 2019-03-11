package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Hbs519 extends AbstractTest {
    @Test
    public void shouldCompileRawHelperStatementInsideConditional() throws IOException {
        shouldCompileTo("{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}", AbstractTest.$, AbstractTest.$("raw-helper", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn();
            }
        }), "{{testing}}");
    }

    @Test
    public void shouldGetTextVersionOfRawHelperInsideConditional() throws IOException {
        Assert.assertEquals("{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}", compile("{{#unless test}}{{{{raw-helper}}}}{{testing}}{{{{/raw-helper}}}}{{/unless}}").text());
    }
}

