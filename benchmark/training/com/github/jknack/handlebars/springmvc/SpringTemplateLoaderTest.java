package com.github.jknack.handlebars.springmvc;


import com.github.jknack.handlebars.io.TemplateSource;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;


public class SpringTemplateLoaderTest {
    @Test
    public void sourceAt() throws IOException {
        SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());
        TemplateSource source = loader.sourceAt("template");
        Assert.assertNotNull(source);
    }

    @Test(expected = IOException.class)
    public void fileNotFound() throws IOException {
        new SpringTemplateLoader(new DefaultResourceLoader()).sourceAt("missingFile");
    }
}

