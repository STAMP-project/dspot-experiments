package com.vaadin.server;


import org.junit.Assert;
import org.junit.Test;


public class VaadinServletTest {
    @Test
    public void testGetLastPathParameter() {
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com"));
        Assert.assertEquals(";a", VaadinServlet.getLastPathParameter("http://myhost.com;a"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/hello"));
        Assert.assertEquals(";b=c", VaadinServlet.getLastPathParameter("http://myhost.com/hello;b=c"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/hello/"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/hello;a/"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/hello;a=1/"));
        Assert.assertEquals(";b", VaadinServlet.getLastPathParameter("http://myhost.com/hello/;b"));
        Assert.assertEquals(";b=1", VaadinServlet.getLastPathParameter("http://myhost.com/hello/;b=1"));
        Assert.assertEquals(";b=1,c=2", VaadinServlet.getLastPathParameter("http://myhost.com/hello/;b=1,c=2"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/hello/;b=1,c=2/"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;a/"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;a=1/"));
        Assert.assertEquals(";b", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;b"));
        Assert.assertEquals(";b=1", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;b=1"));
        Assert.assertEquals(";b=1,c=2", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2"));
        Assert.assertEquals("", VaadinServlet.getLastPathParameter("http://myhost.com/a;hello/;b=1,c=2/"));
    }

    @Test
    public void getStaticFilePath() {
        VaadinServlet servlet = new VaadinServlet();
        // Mapping: /VAADIN/*
        // /VAADIN
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("/VAADIN", null)));
        // /VAADIN/ - not really sensible but still interpreted as a resource
        // request
        Assert.assertEquals("/VAADIN/", servlet.getStaticFilePath(createServletRequest("/VAADIN", "/")));
        // /VAADIN/vaadinBootstrap.js
        Assert.assertEquals("/VAADIN/vaadinBootstrap.js", servlet.getStaticFilePath(createServletRequest("/VAADIN", "/vaadinBootstrap.js")));
        // /VAADIN/foo bar.js
        Assert.assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(createServletRequest("/VAADIN", "/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        Assert.assertEquals("/VAADIN/..", servlet.getStaticFilePath(createServletRequest("/VAADIN", "/..")));
        // Mapping: /*
        // /
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("", null)));
        // /VAADIN
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("", "/VAADIN")));
        // /VAADIN/
        Assert.assertEquals("/VAADIN/", servlet.getStaticFilePath(createServletRequest("", "/VAADIN/")));
        // /VAADIN/foo bar.js
        Assert.assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(createServletRequest("", "/VAADIN/foo bar.js")));
        // /VAADIN/.. - not normalized and disallowed in this method
        Assert.assertEquals("/VAADIN/..", servlet.getStaticFilePath(createServletRequest("", "/VAADIN/..")));
        // /BAADIN/foo.js
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("", "/BAADIN/foo.js")));
        // Mapping: /myservlet/*
        // /myservlet
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("/myservlet", null)));
        // /myservlet/VAADIN
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("/myservlet", "/VAADIN")));
        // /myservlet/VAADIN/
        Assert.assertEquals("/VAADIN/", servlet.getStaticFilePath(createServletRequest("/myservlet", "/VAADIN/")));
        // /myservlet/VAADIN/foo bar.js
        Assert.assertEquals("/VAADIN/foo bar.js", servlet.getStaticFilePath(createServletRequest("/myservlet", "/VAADIN/foo bar.js")));
        // /myservlet/VAADIN/.. - not normalized and disallowed in this method
        Assert.assertEquals("/VAADIN/..", servlet.getStaticFilePath(createServletRequest("/myservlet", "/VAADIN/..")));
        // /myservlet/BAADIN/foo.js
        Assert.assertNull(servlet.getStaticFilePath(createServletRequest("/myservlet", "/BAADIN/foo.js")));
    }
}

