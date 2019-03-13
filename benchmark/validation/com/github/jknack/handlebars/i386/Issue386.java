package com.github.jknack.handlebars.i386;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue386 extends AbstractTest {
    @Test
    public void blockHelperShouldNotIntroduceANewContext() throws IOException {
        shouldCompileTo("{{#partial \"body\"}}{{&this}}{{/partial}}{{block \"body\"}}", AbstractTest.$("foo", "bar"), "{foo&#x3D;bar}");
    }

    @Test
    public void partialShouldNotIntroduceANewContext() throws IOException {
        shouldCompileToWithPartials("{{> partial}}", AbstractTest.$("foo", "bar"), AbstractTest.$("partial", "{{&this}}"), "{foo=bar}");
    }
}

