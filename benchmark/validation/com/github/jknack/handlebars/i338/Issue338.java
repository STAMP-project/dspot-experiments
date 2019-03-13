package com.github.jknack.handlebars.i338;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class Issue338 extends AbstractTest {
    public static class HelperSource {
        public CharSequence each(final Object ctx, final Options options) {
            return "each";
        }

        public CharSequence unless(final Object ctx, final Options options) {
            return "unless";
        }
    }

    @Test
    public void shouldNotFailOnOverride() throws IOException {
        shouldCompileTo("{{each}}", AbstractTest.$, "each");
        shouldCompileTo("{{unless}}", AbstractTest.$, "unless");
    }
}

