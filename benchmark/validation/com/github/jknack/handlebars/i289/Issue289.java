package com.github.jknack.handlebars.i289;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue289 extends AbstractTest {
    private AbstractTest.Hash helpers = AbstractTest.$("lowercase", new com.github.jknack.handlebars.Helper<Object>() {
        @Override
        public Object apply(final Object context, final Options options) throws IOException {
            return context.toString().toLowerCase();
        }
    }, "concat", new com.github.jknack.handlebars.Helper<Object>() {
        @Override
        public Object apply(final Object context, final Options options) throws IOException {
            return (options.param(0).toString()) + (options.param(1).toString());
        }
    });

    @Test
    public void subexpression() throws IOException {
        Assert.assertEquals("{{lowercase (concat \"string1\" \"string2\")}}", compile("{{lowercase (concat \"string1\" \"string2\")}}", helpers).text());
    }

    @Test
    public void subexpressionToJS() throws IOException {
        String js = "{\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n" + ("    return container.escapeExpression((helpers.lowercase || (depth0 && depth0.lowercase) || helpers.helperMissing).call(depth0 != null ? depth0 : {},(helpers.concat || (depth0 && depth0.concat) || helpers.helperMissing).call(depth0 != null ? depth0 : {},\"string1\",\"string2\",{\"name\":\"concat\",\"hash\":{},\"data\":data}),{\"name\":\"lowercase\",\"hash\":{},\"data\":data}));\n" + "},\"useData\":true}");
        Assert.assertEquals(js, compile("{{lowercase (concat \"string1\" \"string2\")}}", helpers).toJavaScript());
    }
}

