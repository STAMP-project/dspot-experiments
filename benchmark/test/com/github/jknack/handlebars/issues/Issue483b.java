package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue483b extends v4Test {
    @Test
    public void shouldPassObjectResult() throws IOException {
        shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}", v4Test.$("hash", v4Test.$("arg", "foo")), "foo");
        shouldCompileTo("{{#if (equal arg 'foo')}}foo{{/if}}", v4Test.$("hash", v4Test.$("arg", "bar")), "");
    }
}

