package com.orientechnologies.orient.core.db;


import ODatabaseType.MEMORY;
import ODatabaseType.PLOCAL;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 08/04/16.
 */
public class OrientDBEmbeddedTests {
    @Test
    public void testCompatibleUrl() {
        try (OrientDB orientDb = new OrientDB("plocal:", OrientDBConfig.defaultConfig())) {
        }
        try (OrientDB orientDb = new OrientDB("memory:", OrientDBConfig.defaultConfig())) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongUrlFalure() {
        new OrientDB("wrong", OrientDBConfig.defaultConfig());
    }

    @Test
    public void createAndUseEmbeddedDatabase() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        if (!(orientDb.exists("test")))
            orientDb.create("test", MEMORY);

        ODatabaseSession db = orientDb.open("test", "admin", "admin");
        db.save(new ODocument(), db.getClusterNameById(db.getDefaultClusterId()));
        db.close();
        orientDb.close();
    }

    @Test(expected = ODatabaseException.class)
    public void testEmbeddedDoubleCreate() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        try {
            orientDb.create("test", MEMORY);
            orientDb.create("test", MEMORY);
        } finally {
            orientDb.close();
        }
    }

    @Test
    public void createDropEmbeddedDatabase() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        try {
            orientDb.create("test", MEMORY);
            Assert.assertTrue(orientDb.exists("test"));
            orientDb.drop("test");
            Assert.assertFalse(orientDb.exists("test"));
        } finally {
            orientDb.close();
        }
    }

    @Test
    public void testMultiThread() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        if (!(orientDb.exists("test")))
            orientDb.create("test", MEMORY);

        ODatabasePool pool = new ODatabasePool(orientDb, "test", "admin", "admin");
        // do a query and assert on other thread
        Runnable acquirer = () -> {
            ODatabaseSession db = pool.acquire();
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
        orientDb.close();
    }

    @Test
    public void testListDatabases() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        // OrientDBInternal orientDb = OrientDBInternal.fromUrl("local:.", null);
        Assert.assertEquals(orientDb.list().size(), 0);
        orientDb.create("test", MEMORY);
        List<String> databases = orientDb.list();
        Assert.assertEquals(databases.size(), 1);
        Assert.assertTrue(databases.contains("test"));
        orientDb.close();
    }

    @Test
    public void testRegisterDatabase() {
        OrientDBEmbedded orientDb = ((OrientDBEmbedded) (getInternal()));
        Assert.assertEquals(orientDb.listDatabases("", "").size(), 0);
        orientDb.initCustomStorage("database1", "./target/databases/database1", "", "");
        try (ODatabaseSession db = orientDb.open("database1", "admin", "admin")) {
            Assert.assertEquals("database1", db.getName());
        }
        orientDb.initCustomStorage("database2", "./target/databases/database2", "", "");
        try (ODatabaseSession db = orientDb.open("database2", "admin", "admin")) {
            Assert.assertEquals("database2", db.getName());
        }
        orientDb.drop("database1", null, null);
        orientDb.drop("database2", null, null);
        orientDb.close();
    }

    @Test
    public void testCopyOpenedDatabase() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.create("test", MEMORY);
        ODatabaseSession db1;
        try (ODatabaseDocumentInternal db = ((ODatabaseDocumentInternal) (orientDb.open("test", "admin", "admin")))) {
            db1 = db.copy();
        }
        db1.activateOnCurrentThread();
        Assert.assertFalse(db1.isClosed());
        db1.close();
        orientDb.close();
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseCreate() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.create("test", MEMORY);
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseOpen() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.open("test", "", "");
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseList() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.list();
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseExists() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.exists("");
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseOpenPoolInternal() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.openPool("", "", "", OrientDBConfig.defaultConfig());
    }

    @Test(expected = ODatabaseException.class)
    public void testUseAfterCloseDrop() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.close();
        orientDb.drop("");
    }

    @Test
    public void testPoolByUrl() {
        ODatabasePool pool = new ODatabasePool("embedded:./target/some", "admin", "admin");
        pool.close();
    }

    @Test
    public void testOpenKeepClean() {
        OrientDB orientDb = new OrientDB("embedded:./", OrientDBConfig.defaultConfig());
        try {
            orientDb.open("test", "admin", "admin");
        } catch (Exception e) {
            // ignore
        }
        Assert.assertFalse(orientDb.exists("test"));
        orientDb.close();
    }

    @Test
    public void testOrientDBDatabaseOnlyMemory() {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.create("test", MEMORY);
        ODatabaseSession db = orientDb.open("test", "admin", "admin");
        db.save(new ODocument(), db.getClusterNameById(db.getDefaultClusterId()));
        db.close();
        orientDb.close();
    }

    @Test(expected = ODatabaseException.class)
    public void testOrientDBDatabaseOnlyMemoryFailPlocal() {
        try (OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig())) {
            orientDb.create("test", PLOCAL);
        }
    }

    @Test
    public void createForceCloseOpen() throws InterruptedException {
        OrientDB orientDB = new OrientDB("embedded:./target/", OrientDBConfig.defaultConfig());
        orientDB.create("test", PLOCAL);
        forceDatabaseClose("test");
        ODatabaseSession db1 = orientDB.open("test", "admin", "admin");
        Assert.assertFalse(db1.isClosed());
        db1.close();
        orientDB.drop("test");
        orientDB.close();
    }

    @Test(expected = ODatabaseException.class)
    public void testOpenNotExistDatabase() {
        try (OrientDB orientDB = new OrientDB("embedded:./target/", OrientDBConfig.defaultConfig())) {
            orientDB.open("one", "two", "three");
        }
    }

    @Test
    public void testExecutor() throws InterruptedException, ExecutionException {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.create("test", MEMORY);
        OrientDBInternal internal = OrientDBInternal.extract(orientDb);
        Future<Boolean> result = internal.execute("test", "admin", ( session) -> {
            if ((session.isClosed()) && ((session.getUser()) == null)) {
                return false;
            }
            return true;
        });
        Assert.assertTrue(result.get());
        orientDb.close();
    }

    @Test
    public void testExecutorNoAuthorization() throws InterruptedException, ExecutionException {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDb.create("test", MEMORY);
        OrientDBInternal internal = OrientDBInternal.extract(orientDb);
        Future<Boolean> result = internal.executeNoAuthorization("test", ( session) -> {
            if ((session.isClosed()) && ((session.getUser()) != null)) {
                return false;
            }
            return true;
        });
        Assert.assertTrue(result.get());
        orientDb.close();
    }

    @Test
    public void testScheduler() throws InterruptedException {
        OrientDB orientDb = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        OrientDBInternal internal = OrientDBInternal.extract(orientDb);
        CountDownLatch latch = new CountDownLatch(2);
        internal.schedule(new TimerTask() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 10, 10);
        Assert.assertTrue(latch.await(30, TimeUnit.MILLISECONDS));
        CountDownLatch once = new CountDownLatch(1);
        internal.scheduleOnce(new TimerTask() {
            @Override
            public void run() {
                once.countDown();
            }
        }, 10);
        Assert.assertTrue(once.await(20, TimeUnit.MILLISECONDS));
    }
}

