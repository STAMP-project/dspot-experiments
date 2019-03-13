package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue458 extends v4Test {
    static class Foo {
        private String baa;

        public Foo(final String baa) {
            this.baa = baa;
        }

        public String getBaa() {
            return baa;
        }
    }

    @Test
    public void shouldNotRenderNullValues() throws IOException {
        shouldCompileTo("{{baa}}", v4Test.$("hash", new Issue458.Foo(null)), "");
        shouldCompileTo("{{this.baa}}", v4Test.$("hash", new Issue458.Foo(null)), "");
        shouldCompileTo("{{#with this}}{{baa}}{{/with}}", v4Test.$("hash", new Issue458.Foo(null)), "");
    }
}

