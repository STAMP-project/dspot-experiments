package com.github.jknack.handlebars;


import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.URLTemplateLoader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue95 {
    @Test
    public void issue95() throws IOException {
        URLTemplateLoader loader = new ClassPathTemplateLoader("/issue95");
        Handlebars handlebars = new Handlebars(loader);
        handlebars.setInfiniteLoops(true);
        Template template = handlebars.compile("hbs/start");
        Assert.assertNotNull(template);
    }
}

