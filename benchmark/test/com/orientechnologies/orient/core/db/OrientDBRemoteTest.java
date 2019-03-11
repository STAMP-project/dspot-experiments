package com.orientechnologies.orient.core.db;


import ODatabaseType.MEMORY;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 06/07/16.
 */
public class OrientDBRemoteTest {
    private static final String SERVER_DIRECTORY = "./target/dbfactory";

    private OServer server;

    private OrientDB factory;

    @Test
    public void createAndUseRemoteDatabase() {
        if (!(factory.exists("test")))
            factory.create("test", MEMORY);

        ODatabaseDocument db = factory.open("test", "admin", "admin");
        db.save(new ODocument(), db.getClusterNameById(db.getDefaultClusterId()));
        db.close();
    }

    // @Test(expected = OStorageExistsException.class)
    // TODO: Uniform database exist exceptions
    @Test(expected = OStorageException.class)
    public void doubleCreateRemoteDatabase() {
        factory.create("test", MEMORY);
        factory.create("test", MEMORY);
    }

    @Test
    public void createDropRemoteDatabase() {
        factory.create("test", MEMORY);
        Assert.assertTrue(factory.exists("test"));
        factory.drop("test");
        Assert.assertFalse(factory.exists("test"));
    }

    @Test
    public void testPool() {
        if (!(factory.exists("test")))
            factory.create("test", MEMORY);

        ODatabasePool pool = new ODatabasePool(factory, "test", "admin", "admin");
        ODatabaseDocument db = pool.acquire();
        db.save(new ODocument(), db.getClusterNameById(db.getDefaultClusterId()));
        db.close();
        pool.close();
    }

    @Test
    public void testMultiThread() {
        if (!(factory.exists("test")))
            factory.create("test", MEMORY);

        ODatabasePool pool = new ODatabasePool(factory, "test", "admin", "admin");
        // do a query and assert on other thread
        Runnable acquirer = () -> {
            ODatabaseDocument db = pool.acquire();
            try {
                Assert.assertThat(db.isActiveOnCurrentThread()).isTrue();
                List<ODocument> res = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery("SELECT * FROM OUser"));
                Assert.assertThat(res).hasSize(3);
            } finally {
                db.close();
            }
        };
        // spawn 20 threads
        List<CompletableFuture<Void>> futures = IntStream.range(0, 19).boxed().map(( i) -> CompletableFuture.runAsync(acquirer)).collect(Collectors.toList());
        futures.forEach(( cf) -> cf.join());
        pool.close();
    }

    @Test
    public void testListDatabases() {
        Assert.assertEquals(factory.list().size(), 0);
        factory.create("test", MEMORY);
        List<String> databases = factory.list();
        Assert.assertEquals(databases.size(), 1);
        Assert.assertTrue(databases.contains("test"));
    }

    @Test
    public void testCopyOpenedDatabase() {
        factory.create("test", MEMORY);
        ODatabaseDocument db1;
        try (ODatabaseDocumentInternal db = ((ODatabaseDocumentInternal) (factory.open("test", "admin", "admin")))) {
            db1 = db.copy();
        }
        db1.activateOnCurrentThread();
        Assert.assertFalse(db1.isClosed());
        db1.close();
    }
}

