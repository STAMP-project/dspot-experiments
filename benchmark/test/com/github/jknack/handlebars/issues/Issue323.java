package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class Issue323 extends AbstractTest {
    @Test
    public void subExpressionInHashArg() throws IOException {
        shouldCompileTo("{{someTemplate param1 param2 hashArg=(myHelper param3)}}", AbstractTest.$("param1", "a", "param2", "b", "param3", "c"), AbstractTest.$("someTemplate", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return (context + (options.param(0))) + (options.hash("hashArg"));
            }
        }, "myHelper", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return context;
            }
        }), "abc");
    }
}

