package com.orientechnologies.orient.server;


import ODatabaseType.MEMORY;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.junit.Assert;
import org.junit.Test;


public class OServerDatabaseOperationsTest {
    private static final String SERVER_DIRECTORY = "./target/db";

    private OServer server;

    @Test
    public void testServerLoginDatabase() {
        server.serverLogin("root", "root", "list");
    }

    @Test
    public void testCreateOpenDatabase() {
        server.createDatabase("test", MEMORY, OrientDBConfig.defaultConfig());
        Assert.assertTrue(server.existsDatabase("test"));
        ODatabaseSession session = server.openDatabase("test");
        Assert.assertNotNull(session);
        session.close();
    }
}

