package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class GlobalDelimsTest extends AbstractTest {
    @Test
    public void customDelims() throws IOException {
        shouldCompileTo("<<hello>>", AbstractTest.$("hello", "hi"), "hi");
    }
}

