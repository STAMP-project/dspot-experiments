package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue488 extends AbstractTest {
    @Test
    public void utf8() throws IOException {
        shouldCompileTo("{{i18n \"utf8\"}}", AbstractTest.$, "Bonjour ? tous.");
    }
}

