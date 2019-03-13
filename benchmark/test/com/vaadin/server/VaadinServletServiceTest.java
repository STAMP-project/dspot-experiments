package com.vaadin.server;


import org.junit.Assert;
import org.junit.Test;


public class VaadinServletServiceTest {
    VaadinServlet servlet;

    @Test
    public void testServletToContextRootRelativePath() throws Exception {
        String location;
        /* SERVLETS */
        // http://dummy.host:8080/contextpath/servlet
        // should return . (relative url resolving to /contextpath)
        location = testLocation("http://dummy.host:8080", "/contextpath", "/servlet", "");
        Assert.assertEquals(".", location);
        // http://dummy.host:8080/contextpath/servlet/
        // should return ./.. (relative url resolving to /contextpath)
        location = testLocation("http://dummy.host:8080", "/contextpath", "/servlet", "/");
        Assert.assertEquals("./..", location);
        // http://dummy.host:8080/servlet
        // should return "."
        location = testLocation("http://dummy.host:8080", "", "/servlet", "");
        Assert.assertEquals(".", location);
        // http://dummy.host/contextpath/servlet/extra/stuff
        // should return ./../.. (relative url resolving to /contextpath)
        location = testLocation("http://dummy.host", "/contextpath", "/servlet", "/extra/stuff");
        Assert.assertEquals("./../..", location);
        // http://dummy.host/contextpath/servlet/extra/stuff/
        // should return ./../../.. (relative url resolving to /contextpath)
        location = testLocation("http://dummy.host", "/contextpath", "/servlet", "/extra/stuff/");
        Assert.assertEquals("./../../..", location);
        // http://dummy.host/context/path/servlet/extra/stuff
        // should return ./../.. (relative url resolving to /context/path)
        location = testLocation("http://dummy.host", "/context/path", "/servlet", "/extra/stuff");
        Assert.assertEquals("./../..", location);
    }
}

