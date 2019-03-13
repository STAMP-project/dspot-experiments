package com.github.jknack.handlebars.i287;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import java.io.IOException;
import org.junit.Test;


public class Issue287 {
    @Test
    public void compositeLoaderMustNotFailWithInlineTemplates() throws IOException {
        Handlebars handlebars = new Handlebars(new com.github.jknack.handlebars.io.CompositeTemplateLoader(new FileTemplateLoader("."), new ClassPathTemplateLoader()));
        handlebars.compileInline("{{issue287}}");
    }
}

