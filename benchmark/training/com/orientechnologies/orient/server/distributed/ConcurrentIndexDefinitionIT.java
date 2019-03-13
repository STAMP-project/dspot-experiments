package com.orientechnologies.orient.server.distributed;


import OClass.INDEX_TYPE.NOTUNIQUE;
import OType.STRING;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.server.OServer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentIndexDefinitionIT {
    private OServer server0;

    private OServer server1;

    private OServer server2;

    private OrientDB remote;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> future = executor.submit(() -> {
            ODatabaseSession session = remote.open("test", "admin", "admin");
            OClass clazz = session.createClass("Test");
            clazz.createProperty("test", STRING).createIndex(NOTUNIQUE);
        });
        Future<?> future1 = executor.submit(() -> {
            ODatabaseSession session = remote.open("test", "admin", "admin");
            OClass clazz = session.createClass("Test1");
            clazz.createProperty("test1", STRING).createIndex(NOTUNIQUE);
        });
        future.get();
        future1.get();
        executor.shutdown();
        ODatabaseSession session = remote.open("test", "admin", "admin");
        Assert.assertTrue(session.getMetadata().getSchema().existsClass("Test"));
        Assert.assertFalse(session.getMetadata().getSchema().getClass("Test").getIndexes().isEmpty());
        Assert.assertTrue(session.getMetadata().getSchema().existsClass("Test1"));
        Assert.assertFalse(session.getMetadata().getSchema().getClass("Test1").getIndexes().isEmpty());
        OrientDB remote1 = new OrientDB("remote:localhost:2425", "root", "test", OrientDBConfig.defaultConfig());
        ODatabaseSession session1 = remote1.open("test", "admin", "admin");
        Assert.assertTrue(session1.getMetadata().getSchema().existsClass("Test"));
        Assert.assertFalse(session1.getMetadata().getSchema().getClass("Test").getIndexes().isEmpty());
        Assert.assertTrue(session1.getMetadata().getSchema().existsClass("Test1"));
        Assert.assertFalse(session1.getMetadata().getSchema().getClass("Test1").getIndexes().isEmpty());
        session1.close();
        remote1.close();
        OrientDB remote2 = new OrientDB("remote:localhost:2426", "root", "test", OrientDBConfig.defaultConfig());
        // Make sure the created database is propagated
        ODatabaseSession session2 = remote2.open("test", "admin", "admin");
        Assert.assertTrue(session2.getMetadata().getSchema().existsClass("Test"));
        Assert.assertFalse(session2.getMetadata().getSchema().getClass("Test").getIndexes().isEmpty());
        Assert.assertTrue(session2.getMetadata().getSchema().existsClass("Test1"));
        Assert.assertFalse(session2.getMetadata().getSchema().getClass("Test1").getIndexes().isEmpty());
        session2.close();
        remote2.close();
    }
}

