package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue473 extends AbstractTest {
    @Test
    public void testWith() throws IOException {
        AbstractTest.Hash context = AbstractTest.$("needsPano", new Boolean(true), "group", AbstractTest.$("title", "test"));
        shouldCompileTo("{{#with group}}{{needsPano}}{{/with}}", context, "true");
    }

    @Test
    public void testBlock() throws IOException {
        AbstractTest.Hash context = AbstractTest.$("needsPano", new Boolean(true), "group", AbstractTest.$("title", "test"));
        shouldCompileTo("{{#group}}{{needsPano}}{{/group}}", context, "true");
    }
}

