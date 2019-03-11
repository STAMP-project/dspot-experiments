/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.schema;


import org.junit.Assert;
import org.junit.Test;


public class ServerTest {
    @Test
    public void testServerNameParsing() throws Exception {
        final String serverName = "LOCALHOST";
        final String fullServerName = String.format("[%s]", serverName);
        final Server server = new Server(fullServerName);
        Assert.assertEquals(serverName, server.getServerName());
        Assert.assertEquals(fullServerName, server.toString());
    }

    @Test
    public void testServerNameAndInstanceParsing() throws Exception {
        final String serverName = "LOCALHOST";
        final String serverInstanceName = "SQLSERVER";
        final String fullServerName = String.format("[%s\\%s]", serverName, serverInstanceName);
        final Server server = new Server(fullServerName);
        Assert.assertEquals(serverName, server.getServerName());
        Assert.assertEquals(serverInstanceName, server.getInstanceName());
        Assert.assertEquals(fullServerName, server.toString());
    }

    @Test
    public void testServerNameAndInstanceParsing2() throws Exception {
        String simpleName = "LOCALHOST";
        final Server server = new Server(simpleName);
        Assert.assertEquals(simpleName, server.getFullyQualifiedName());
    }

    @Test
    public void testServerNameAndInstanceParsingNull() throws Exception {
        final Server server = new Server(null);
        Assert.assertEquals("", server.getFullyQualifiedName());
    }

    @Test
    public void testServerNameAndInstancePassValues() throws Exception {
        final Server server = new Server("SERVER", "INSTANCE");
        Assert.assertEquals("SERVER", server.getServerName());
        Assert.assertEquals("INSTANCE", server.getInstanceName());
        Assert.assertEquals(String.format("[%s\\%s]", "SERVER", "INSTANCE"), server.getFullyQualifiedName());
    }

    @Test
    public void testServerNameNull() throws Exception {
        final Server server = new Server(null, "INSTANCE");
        Assert.assertEquals(null, server.getServerName());
        Assert.assertEquals("INSTANCE", server.getInstanceName());
        Assert.assertEquals("", server.getFullyQualifiedName());
    }

    @Test
    public void testServerNameEmpty() throws Exception {
        final Server server = new Server("", "INSTANCE");
        Assert.assertEquals("", server.getServerName());
        Assert.assertEquals("INSTANCE", server.getInstanceName());
        Assert.assertEquals("", server.getFullyQualifiedName());
    }

    @Test
    public void testInstanceNameNull() throws Exception {
        final Server server = new Server("LOCALHOST", null);
        Assert.assertEquals("LOCALHOST", server.getServerName());
        Assert.assertEquals(null, server.getInstanceName());
        Assert.assertEquals("[LOCALHOST]", server.getFullyQualifiedName());
    }

    @Test
    public void testInstanceNameEmpty() throws Exception {
        final Server server = new Server("LOCALHOST", "");
        Assert.assertEquals("LOCALHOST", server.getServerName());
        Assert.assertEquals("", server.getInstanceName());
        Assert.assertEquals("[LOCALHOST]", server.getFullyQualifiedName());
    }
}

