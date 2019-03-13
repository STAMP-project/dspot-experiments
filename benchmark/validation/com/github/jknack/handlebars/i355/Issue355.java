package com.github.jknack.handlebars.i355;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue355 extends AbstractTest {
    @Test
    public void shouldFormatZero() throws IOException {
        shouldCompileTo("{{numberFormat 0}}", AbstractTest.$, "0");
        shouldCompileTo("{{numberFormat 0 'currency'}}", AbstractTest.$, "$0.00");
        shouldCompileTo("{{numberFormat price 'currency'}}", AbstractTest.$("price", 0.0), "$0.00");
    }
}

