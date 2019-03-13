package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue477 extends v4Test {
    private String template = null;

    private v4Test.Hash data = null;

    private String expected = null;

    @Test
    public void partialWithHash() throws IOException {
        shouldCompileTo(template, data, expected);
    }
}

