package com.github.jknack.handlebars.i291;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue291 extends AbstractTest {
    @Test
    public void sourceLocation() throws IOException {
        shouldCompileTo("hello {{world}}", AbstractTest.$, "hello 1:8");
        shouldCompileTo("\nhello  {{world}}", AbstractTest.$, "\nhello  2:9");
    }
}

