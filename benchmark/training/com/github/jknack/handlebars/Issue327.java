package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue327 extends AbstractTest {
    @Test
    public void link() throws IOException {
        shouldCompileTo("{{link 'handlebars.java' 'https://github.com/jknack/handlebars.java'}}", AbstractTest.$, "<a href=\"https://github.com/jknack/handlebars.java\">handlebars.java</a>");
    }

    @Test
    public void calllink() throws IOException {
        shouldCompileTo("{{call-link 'handlebars.java' 'https://github.com/jknack/handlebars.java'}}", AbstractTest.$, "<a href=\"https://github.com/jknack/handlebars.java\">handlebars.java</a>");
    }
}

