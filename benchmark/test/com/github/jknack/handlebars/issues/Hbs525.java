package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Hbs525 extends v4Test {
    @Test
    public void helperNameSpace() throws IOException {
        shouldCompileTo("{{nav.render 'main'}}", v4Test.$("helpers", v4Test.$("nav.render", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return context;
            }
        })), "main");
    }
}

