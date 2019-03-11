package com.github.jknack.handlebars.i270;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class Issue270 extends AbstractTest {
    @Test
    public void charLiteral() throws IOException {
        shouldCompileTo("{{modifiers this 'clock'}}", AbstractTest.$, AbstractTest.$("modifiers", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.params[0].toString();
            }
        }), "clock");
    }
}

