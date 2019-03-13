package com.github.jknack.handlebars.cache;


import NullTemplateCache.INSTANCE;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class NullTemplateCacheTest {
    @Test
    public void clear() {
        INSTANCE.clear();
    }

    @Test
    public void evict() {
        TemplateSource source = createMock(TemplateSource.class);
        replay(source);
        INSTANCE.evict(source);
        verify(source);
    }

    @Test
    public void get() throws IOException {
        TemplateSource source = createMock(TemplateSource.class);
        Template template = createMock(Template.class);
        Parser parser = createMock(Parser.class);
        expect(parser.parse(source)).andReturn(template);
        replay(source, parser, template);
        Template result = INSTANCE.get(source, parser);
        Assert.assertEquals(template, result);
        verify(source, parser, template);
    }
}

