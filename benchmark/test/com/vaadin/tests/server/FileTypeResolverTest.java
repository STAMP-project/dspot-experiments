package com.vaadin.tests.server;


import FileTypeResolver.DEFAULT_MIME_TYPE;
import com.vaadin.util.FileTypeResolver;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class FileTypeResolverTest {
    private static final String FLASH_MIME_TYPE = "application/x-shockwave-flash";

    private static final String TEXT_MIME_TYPE = "text/plain";

    private static final String HTML_MIME_TYPE = "text/html";

    @Test
    public void testMimeTypes() {
        File plainFlash = new File("MyFlash.swf");
        File plainText = new File("/a/b/MyFlash.txt");
        File plainHtml = new File("c:\\MyFlash.html");
        // Flash
        Assert.assertEquals(FileTypeResolver.getMIMEType(plainFlash.getAbsolutePath()), FileTypeResolverTest.FLASH_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainFlash.getAbsolutePath()) + "?param1=value1")), FileTypeResolverTest.FLASH_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainFlash.getAbsolutePath()) + "?param1=value1&param2=value2")), FileTypeResolverTest.FLASH_MIME_TYPE);
        // Plain text
        Assert.assertEquals(FileTypeResolver.getMIMEType(plainText.getAbsolutePath()), FileTypeResolverTest.TEXT_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainText.getAbsolutePath()) + "?param1=value1")), FileTypeResolverTest.TEXT_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainText.getAbsolutePath()) + "?param1=value1&param2=value2")), FileTypeResolverTest.TEXT_MIME_TYPE);
        // Plain text
        Assert.assertEquals(FileTypeResolver.getMIMEType(plainHtml.getAbsolutePath()), FileTypeResolverTest.HTML_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainHtml.getAbsolutePath()) + "?param1=value1")), FileTypeResolverTest.HTML_MIME_TYPE);
        Assert.assertEquals(FileTypeResolver.getMIMEType(((plainHtml.getAbsolutePath()) + "?param1=value1&param2=value2")), FileTypeResolverTest.HTML_MIME_TYPE);
        // Filename missing
        Assert.assertEquals(DEFAULT_MIME_TYPE, FileTypeResolver.getMIMEType(""));
        Assert.assertEquals(DEFAULT_MIME_TYPE, FileTypeResolver.getMIMEType("?param1"));
    }

    @Test
    public void testExtensionCase() {
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.jpg"));
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.jPg"));
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPG"));
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPEG"));
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.Jpeg"));
        Assert.assertEquals("image/jpeg", FileTypeResolver.getMIMEType("abc.JPE"));
    }

    @Test
    public void testCustomMimeType() {
        Assert.assertEquals(DEFAULT_MIME_TYPE, FileTypeResolver.getMIMEType("vaadin.foo"));
        FileTypeResolver.addExtension("foo", "Vaadin Foo/Bar");
        FileTypeResolver.addExtension("FOO2", "Vaadin Foo/Bar2");
        Assert.assertEquals("Vaadin Foo/Bar", FileTypeResolver.getMIMEType("vaadin.foo"));
        Assert.assertEquals("Vaadin Foo/Bar2", FileTypeResolver.getMIMEType("vaadin.Foo2"));
    }
}

