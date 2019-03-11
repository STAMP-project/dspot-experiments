package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class Issue500 extends v4Test {
    @Test
    public void shouldEachHelperIterateOverJSNativeArrays() throws IOException {
        shouldCompileTo("{{#each (chunk array 2) }}{{this}}{{/each}}", v4Test.$("hash", v4Test.$("array", new String[]{ "a", "b", "c", "d" })), "abcd24");
        shouldCompileTo("{{#each (chunk array 2) }}{{this}}{{/each}}", v4Test.$("hash", v4Test.$("array", Arrays.asList("a", "b", "c", "d"))), "abcd24");
    }
}

