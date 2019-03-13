package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ToStringTest {
    public static class UnsafeString {
        String underlying;

        public UnsafeString(final String underlying) {
            this.underlying = underlying;
        }

        @Override
        public String toString() {
            return ("<h1>" + (underlying)) + "</h1>";
        }
    }

    @Test
    public void unsafeString() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline("{{this}}");
        String result = template.apply(new ToStringTest.UnsafeString("Hello"));
        Assert.assertEquals("&lt;h1&gt;Hello&lt;/h1&gt;", result);
    }
}

