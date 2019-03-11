package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;


/**
 * https://github.com/jknack/handlebars.java/issues/657
 */
public class Issue657 extends AbstractTest {
    @Test
    public void shouldAllowES6LetOrConstLiterals() throws Exception {
        shouldCompileTo("{{#and great magnificent}}Hello 657{{/and}}", AbstractTest.$("great", true, "magnificent", true), "Hello 657");
    }
}

