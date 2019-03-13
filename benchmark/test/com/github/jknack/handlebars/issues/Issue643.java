package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Test;


public class Issue643 extends v4Test {
    @Test
    public void shouldAllowES6LetOrConstLiterals() throws Exception {
        shouldCompileTo(("template: {{empty}} " + "{{> partial}}"), v4Test.$("partials", v4Test.$("partial", "partial: {{empty}}"), "hash", v4Test.$("empty", false)), "template: false partial: false");
    }
}

