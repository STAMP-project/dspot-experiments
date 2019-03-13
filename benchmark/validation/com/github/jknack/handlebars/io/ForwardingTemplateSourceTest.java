package com.github.jknack.handlebars.io;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class ForwardingTemplateSourceTest {
    @Test
    public void content() throws IOException {
        TemplateSource source = createMock(TemplateSource.class);
        expect(source.content(StandardCharsets.UTF_8)).andReturn("abc");
        replay(source);
        Assert.assertEquals("abc", new ForwardingTemplateSource(source).content(StandardCharsets.UTF_8));
        verify(source);
    }

    @Test
    public void filename() throws IOException {
        String filename = "filename";
        TemplateSource source = createMock(TemplateSource.class);
        expect(source.filename()).andReturn(filename);
        replay(source);
        Assert.assertEquals("filename", new ForwardingTemplateSource(source).filename());
        verify(source);
    }

    @Test
    public void lastModified() throws IOException {
        long lastModified = 716L;
        TemplateSource source = createMock(TemplateSource.class);
        expect(source.lastModified()).andReturn(lastModified);
        replay(source);
        Assert.assertEquals(lastModified, new ForwardingTemplateSource(source).lastModified());
        verify(source);
    }
}

