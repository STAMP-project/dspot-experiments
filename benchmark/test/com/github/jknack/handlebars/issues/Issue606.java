package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Test;


public class Issue606 extends v4Test {
    public static class Helpers {
        public int subtract(int a, int b) {
            return a - b;
        }
    }

    @Test
    public void shouldSupportNoneCharSequenceReturnsTypeFromHelperClass() throws Exception {
        shouldCompileTo("{{#if (subtract value 1)}}OK{{/if}}", v4Test.$("hash", v4Test.$("value", 2)), "OK");
        shouldCompileTo("{{#if (subtract value 1)}}OK{{/if}}", v4Test.$("hash", v4Test.$("value", 1)), "");
    }
}

