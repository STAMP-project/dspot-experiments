package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class WriteIntoContextTest extends AbstractTest {
    @Test
    public void shouldBeAbleToWriteIntoContext() throws IOException {
        shouldCompileTo("{{set \"foo\" this}}{{foo}}", "bar", "bar");
    }

    @Test
    public void shouldBeAbleToWriteIntoContextWhenInBlockHelper() throws IOException {
        shouldCompileTo("{{#with data}}{{set \"foo\" field}}{{foo}}{{/with}}", "{\"data\" : {\"field\": \"bar\"}}", "bar");
    }

    @Test
    public void shouldBeAbleToWriteIntoContextWhenInPartial() throws IOException {
        shouldCompileToWithPartials("{{> partial}}", "bar", constructPartials("partial", "{{set \"foo\" this}}{{foo}}"), "bar");
    }

    public static class SetHelperClass {
        public String set(String key, Object value, Options options) throws NoSuchFieldException {
            options.context.combine(key, value);
            return "";
        }
    }
}

