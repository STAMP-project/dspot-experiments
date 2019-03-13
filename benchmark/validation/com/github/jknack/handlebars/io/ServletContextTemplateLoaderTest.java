package com.github.jknack.handlebars.io;


import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;


public class ServletContextTemplateLoaderTest {
    @Test
    public void sourceAt() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("template");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void subFolder() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml").sourceAt("mustache/specs/comments");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void subFolderwithDashAtBeginning() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml").sourceAt("/mustache/specs/comments");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFound() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("notExist");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void setBasePath() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources/mustache/specs");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml").sourceAt("comments");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void setBasePathWithDash() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources/mustache/specs/");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml").sourceAt("comments");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void nullSuffix() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources/");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", null).sourceAt("noextension");
        Assert.assertNotNull(source);
        verify(servletContext);
    }

    @Test
    public void emotySuffix() throws IOException {
        ServletContext servletContext = createMock(ServletContext.class);
        expectGetResource(servletContext, "src/test/resources/");
        replay(servletContext);
        TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", "").sourceAt("noextension");
        Assert.assertNotNull(source);
        verify(servletContext);
    }
}

