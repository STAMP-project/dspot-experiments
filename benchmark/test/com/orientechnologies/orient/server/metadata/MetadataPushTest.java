package com.orientechnologies.orient.server.metadata;


import ODatabase.ATTRIBUTES.LOCALELANGUAGE;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.server.OServer;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 23/05/17.
 */
public class MetadataPushTest {
    private static final String SERVER_DIRECTORY = "./target/metadata-push";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument database;

    private OrientDB secondOrientDB;

    private ODatabaseDocument secondDatabase;

    @Test
    public void testStorageUpdate() throws InterruptedException {
        database.activateOnCurrentThread();
        database.command(" ALTER DATABASE LOCALELANGUAGE  ?", Locale.GERMANY.getLanguage());
        // Push done in background for now, do not guarantee update before command return.
        Thread.sleep(500);
        secondDatabase.activateOnCurrentThread();
        Assert.assertEquals(secondDatabase.get(LOCALELANGUAGE), Locale.GERMANY.getLanguage());
    }

    @Test
    public void testSchemaUpdate() throws InterruptedException {
        database.activateOnCurrentThread();
        database.command(" create class X");
        // Push done in background for now, do not guarantee update before command return.
        Thread.sleep(500);
        secondDatabase.activateOnCurrentThread();
        TestCase.assertTrue(secondDatabase.getMetadata().getSchema().existsClass("X"));
    }

    @Test
    public void testIndexManagerUpdate() throws InterruptedException {
        database.activateOnCurrentThread();
        database.command(" create class X");
        database.command(" create property X.y STRING");
        database.command(" create index X.y on X(y) NOTUNIQUE");
        // Push done in background for now, do not guarantee update before command return.
        Thread.sleep(500);
        secondDatabase.activateOnCurrentThread();
        TestCase.assertTrue(secondDatabase.getMetadata().getIndexManager().existsIndex("X.y"));
    }

    @Test
    public void testFunctionUpdate() throws InterruptedException {
        database.activateOnCurrentThread();
        database.command("CREATE FUNCTION test \"print(\'\\nTest!\')\"");
        // Push done in background for now, do not guarantee update before command return.
        Thread.sleep(500);
        secondDatabase.activateOnCurrentThread();
        Assert.assertNotNull(secondDatabase.getMetadata().getFunctionLibrary().getFunction("test"));
    }

    @Test
    public void testSequencesUpdate() throws InterruptedException {
        database.activateOnCurrentThread();
        database.command("CREATE SEQUENCE test TYPE CACHED");
        // Push done in background for now, do not guarantee update before command return.
        Thread.sleep(500);
        secondDatabase.activateOnCurrentThread();
        Assert.assertNotNull(secondDatabase.getMetadata().getSequenceLibrary().getSequence("test"));
    }
}

