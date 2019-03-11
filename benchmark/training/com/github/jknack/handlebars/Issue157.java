package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue157 extends AbstractTest {
    @Test
    public void whitespacesAndSpecialCharactersInTemplateNames() throws IOException {
        Handlebars handlebars = new Handlebars();
        Assert.assertEquals("works!", handlebars.compile("space between").apply(AbstractTest.$));
    }
}

