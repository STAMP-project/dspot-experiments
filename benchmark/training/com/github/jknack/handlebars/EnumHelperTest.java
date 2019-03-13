package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class EnumHelperTest extends AbstractTest {
    public enum Helpers implements Helper<Object> {

        h1,
        h2,
        h3;
        @Override
        public Object apply(final Object context, final Options options) throws IOException {
            return name();
        }
    }

    @Test
    public void h1() throws IOException {
        shouldCompileTo("{{h1}}", AbstractTest.$, "h1");
    }

    @Test
    public void h2() throws IOException {
        shouldCompileTo("{{h2}}", AbstractTest.$, "h2");
    }

    @Test
    public void h3() throws IOException {
        shouldCompileTo("{{h3}}", AbstractTest.$, "h3");
    }
}

