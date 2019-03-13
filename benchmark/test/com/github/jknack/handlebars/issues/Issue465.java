package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue465 extends v4Test {
    @Test
    public void withHbs3EscapingStrategy() throws IOException {
        shouldCompileTo("equals is {{eq}}", v4Test.$("hash", v4Test.$("eq", "=")), "equals is =");
    }
}

