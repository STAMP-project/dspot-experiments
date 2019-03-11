package com.github.jknack.handlebars.i397;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue397 extends AbstractTest {
    @Test
    public void subexpressionSerializedToPlainText() throws IOException {
        shouldCompileTo("{{helper context_var param1=(concat \"a\" \"b\")}}", AbstractTest.$("context_var", "!"), "!a+b");
    }

    @Test
    public void subexpressionSerializedToPlainTextHashToString() throws IOException {
        Assert.assertEquals("{{helper context_var param1=(concat \"a\" \"b\")}}", compile("{{helper context_var param1=(concat \"a\" \"b\")}}").text());
    }

    @Test
    public void subexpressionSerializedToPlainTextParamToString() throws IOException {
        Assert.assertEquals("{{helper (concat \"a\" \"b\")}}", compile("{{helper (concat \"a\" \"b\")}}").text());
    }
}

