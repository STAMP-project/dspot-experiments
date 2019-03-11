package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.util.Arrays;
import org.junit.Test;


public class Issue567 extends v4Test {
    @Test
    public void shouldKeepContextOnBlockParameter() throws Exception {
        shouldCompileTo(("{{#each foo as |v k|}}" + ("  context is {{{.}}}" + "{{/each}}")), v4Test.$("hash", v4Test.$("foo", Arrays.asList("a", "b", "c"))), "  context is {v=a, k=0}  context is {v=b, k=1}  context is {v=c, k=2}");
    }
}

