package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import org.junit.Test;


public class Issue479 extends v4Test {
    @Test
    public void partialWithHashOverride() throws Exception {
        shouldCompileTo("{{> dude _greeting=\"Hello\"}}", v4Test.$("hash", v4Test.$("name", "Elliot", "_greeting", "Good Morning"), "partials", v4Test.$("dude", "{{_greeting}} {{name}}!")), "Hello Elliot!");
    }

    @Test
    public void partialWithHash() throws Exception {
        shouldCompileTo("{{> dude}}", v4Test.$("hash", v4Test.$("name", "Elliot", "_greeting", "Good Morning"), "partials", v4Test.$("dude", "{{_greeting}} {{name}}!")), "Good Morning Elliot!");
    }
}

