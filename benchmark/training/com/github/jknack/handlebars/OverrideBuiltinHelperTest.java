package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class OverrideBuiltinHelperTest extends v4Test {
    @Test
    public void overrideEach() throws IOException {
        shouldCompileTo("{{#each this}}{{this}}{{/each}}", v4Test.$("hash", new Object[]{ 1, 2, 3 }), "custom");
    }
}

