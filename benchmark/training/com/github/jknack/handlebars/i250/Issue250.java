package com.github.jknack.handlebars.i250;


import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;


public class Issue250 extends AbstractTest {
    @Test
    public void partialWithCustomContextLostParentContext() throws Exception {
        shouldCompileToWithPartials("{{> share page}}", AbstractTest.$("p", "parent", "page", AbstractTest.$("name", "share")), AbstractTest.$("share", "{{p}}"), "parent");
    }

    @Test
    public void partialWithDefaultContextLostParentContext() throws Exception {
        shouldCompileToWithPartials("{{> share}}", AbstractTest.$("p", "parent", "page", AbstractTest.$("name", "share")), AbstractTest.$("share", "{{p}}"), "parent");
    }
}

