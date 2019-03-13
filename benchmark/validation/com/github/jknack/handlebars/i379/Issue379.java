package com.github.jknack.handlebars.i379;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue379 extends AbstractTest {
    @Test
    public void shouldCompile379() throws IOException {
        shouldCompileTo("<div> [a:b] </div>", AbstractTest.$, "<div> [a:b] </div>");
    }

    @Test
    public void shouldRenderAcolonB() throws IOException {
        shouldCompileTo("<div> {{[a:b]}} </div>", AbstractTest.$("a:b", "a"), "<div> a </div>");
    }
}

