package com.github.jknack.handlebars.i410;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import java.io.IOException;
import org.junit.Test;


public class Issue410 extends AbstractTest {
    @Test
    public void shouldNotThrowArrayIndexOutOfBoundsException() throws IOException {
        shouldCompileTo("{{msg 'p' 1 2 3}}", AbstractTest.$, "p123");
        shouldCompileTo("{{msg1 'p' 1 2 3}}", AbstractTest.$, "p123");
    }

    @Test(expected = HandlebarsException.class)
    public void shouldNotThrowArrayIndexOutOfBoundsExceptionErr() throws IOException {
        shouldCompileTo("{{msgerr 'p'}}", AbstractTest.$, "p123");
    }
}

