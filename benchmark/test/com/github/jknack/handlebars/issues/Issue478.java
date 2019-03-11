package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue478 extends v4Test {
    public static class Foo implements EscapingStrategy {
        @Override
        public CharSequence escape(CharSequence value) {
            return value.toString().replace("foo", "bar");
        }
    }

    public static class Bar implements EscapingStrategy {
        @Override
        public CharSequence escape(CharSequence value) {
            return value.toString().replace("bar", "$bar$");
        }
    }

    @Test
    public void shouldAllowToChainEscapeStrategy() throws IOException {
        shouldCompileTo("{{var}}", v4Test.$("hash", v4Test.$("var", "foo")), "$bar$");
    }
}

