package com.orientechnologies.orient.server.metadata;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class RemoteSimpleSchemaTest {
    private static final String SERVER_DIRECTORY = "./target/metadata-push";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testCreateClassIfNotExist() {
        database.createClassIfNotExist("test");
        Assert.assertTrue(database.getMetadata().getSchema().existsClass("test"));
        Assert.assertTrue(database.getMetadata().getSchema().existsClass("TEST"));
        database.createClassIfNotExist("TEST");
        database.createClassIfNotExist("test");
    }

    @Test
    public void testNotCaseSensitiveDrop() {
        database.createClass("test");
        Assert.assertTrue(database.getMetadata().getSchema().existsClass("test"));
        database.getMetadata().getSchema().dropClass("TEST");
        Assert.assertFalse(database.getMetadata().getSchema().existsClass("test"));
    }
}

