package com.orientechnologies.orient.server.network;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.server.OServer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


public class TestConcurrentCachedSequenceGenerationIT {
    static final int THREADS = 20;

    static final int RECORDS = 100;

    private OServer server;

    private OrientDB orientDB;

    @Test
    public void test() throws InterruptedException {
        AtomicLong failures = new AtomicLong(0);
        ODatabasePool pool = new ODatabasePool(orientDB, TestConcurrentCachedSequenceGenerationIT.class.getSimpleName(), "admin", "admin");
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < (TestConcurrentCachedSequenceGenerationIT.THREADS); i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    ODatabaseSession db = pool.acquire();
                    try {
                        for (int j = 0; j < (TestConcurrentCachedSequenceGenerationIT.RECORDS); j++) {
                            OVertex vert = db.newVertex("TestSequence");
                            Assert.assertNotNull(vert.getProperty("id"));
                            db.save(vert);
                        }
                    } catch (Exception e) {
                        failures.incrementAndGet();
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
                }
            };
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(0, failures.get());
    }
}

