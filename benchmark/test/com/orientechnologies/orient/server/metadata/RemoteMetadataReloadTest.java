package com.orientechnologies.orient.server.metadata;


import ODatabase.ATTRIBUTES.LOCALELANGUAGE;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.server.OServer;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class RemoteMetadataReloadTest {
    private static final String SERVER_DIRECTORY = "./target/metadata-reload";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testStorageUpdate() throws InterruptedException {
        database.command(" ALTER DATABASE LOCALELANGUAGE  ?", Locale.GERMANY.getLanguage());
        Assert.assertEquals(database.get(LOCALELANGUAGE), Locale.GERMANY.getLanguage());
    }

    @Test
    public void testSchemaUpdate() throws InterruptedException {
        database.command(" create class X");
        TestCase.assertTrue(database.getMetadata().getSchema().existsClass("X"));
    }

    @Test
    public void testIndexManagerUpdate() throws InterruptedException {
        database.command(" create class X");
        database.command(" create property X.y STRING");
        database.command(" create index X.y on X(y) NOTUNIQUE");
        TestCase.assertTrue(database.getMetadata().getIndexManager().existsIndex("X.y"));
    }

    @Test
    public void testFunctionUpdate() throws InterruptedException {
        database.command("CREATE FUNCTION test \"print(\'\\nTest!\')\"");
        Assert.assertNotNull(database.getMetadata().getFunctionLibrary().getFunction("test"));
    }

    @Test
    public void testSequencesUpdate() throws InterruptedException {
        database.command("CREATE SEQUENCE test TYPE CACHED");
        Assert.assertNotNull(database.getMetadata().getSequenceLibrary().getSequence("test"));
    }
}

