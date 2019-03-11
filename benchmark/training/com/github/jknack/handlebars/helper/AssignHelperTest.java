package com.github.jknack.handlebars.helper;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AssignHelperTest extends AbstractTest {
    @Test
    public void assignResult() throws IOException {
        shouldCompileTo("{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}", $("type", "discounts"), "");
    }

    @Test
    public void assignContext() throws IOException {
        Context context = Context.newContext($("type", "discounts"));
        shouldCompileTo("{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}", context, "");
        Assert.assertEquals("benefits.discounts.title", context.data("benefitsTitle"));
    }
}

