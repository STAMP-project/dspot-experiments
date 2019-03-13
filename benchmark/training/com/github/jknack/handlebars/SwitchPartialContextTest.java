package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class SwitchPartialContextTest extends AbstractTest {
    @Test
    public void switchPartialContext() throws IOException {
        AbstractTest.Hash hash = AbstractTest.$("name", "root", "context", AbstractTest.$("name", "context", "child", AbstractTest.$("name", "child")));
        AbstractTest.Hash partials = AbstractTest.$("partial", "{{name}}");
        // shouldCompileToWithPartials("{{>partial}}", hash, partials, "root");
        // shouldCompileToWithPartials("{{>partial this}}", hash, partials, "root");
        shouldCompileToWithPartials("{{>partial context}}", hash, partials, "context");
        shouldCompileToWithPartials("{{>partial context.name}}", hash, partials, "root");
        shouldCompileToWithPartials("{{>partial context.child}}", hash, partials, "child");
    }

    @Test
    public void partialWithContext() throws IOException {
        String partial = "{{#this}}{{name}} {{/this}}";
        AbstractTest.Hash hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "moe"), AbstractTest.$("name", "curly") });
        shouldCompileToWithPartials("Dudes: {{>dude dudes}}", hash, AbstractTest.$("dude", partial), "Dudes: moe curly ");
    }
}

