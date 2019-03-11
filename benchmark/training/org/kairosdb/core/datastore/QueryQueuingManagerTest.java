/**
 *
 */
/**
 * QueryQueuingManagerTest.java
 */
/**
 *
 */
/**
 * Copyright 2016, KairosDB Authors
 */
/**
 *
 */
package org.kairosdb.core.datastore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPointSet;


public class QueryQueuingManagerTest {
    private AtomicInteger runningCount;

    @Test(timeout = 3000)
    public void test_onePermit() throws InterruptedException {
        QueryQueuingManager manager = new QueryQueuingManager(1, "hostname");
        List<QueryQueuingManagerTest.Query> queries = new ArrayList<QueryQueuingManagerTest.Query>();
        queries.add(new QueryQueuingManagerTest.Query(manager, "1", 5));
        queries.add(new QueryQueuingManagerTest.Query(manager, "2", 5));
        queries.add(new QueryQueuingManagerTest.Query(manager, "3", 5));
        queries.add(new QueryQueuingManagerTest.Query(manager, "4", 5));
        queries.add(new QueryQueuingManagerTest.Query(manager, "5", 5));
        for (QueryQueuingManagerTest.Query query : queries) {
            query.start();
        }
        for (QueryQueuingManagerTest.Query query : queries) {
            query.join();
        }
        Map<Long, QueryQueuingManagerTest.Query> map = new HashMap<Long, QueryQueuingManagerTest.Query>();
        for (QueryQueuingManagerTest.Query query : queries) {
            Assert.assertThat(query.didRun, IsEqual.equalTo(true));
            map.put(query.queriesWatiting, query);
        }
        // Not sure which thread gets the semaphore first  so we add them to a map and verify that some thread
        // had 4 threads waiting, 3 threads, etc.
        Assert.assertThat(map.size(), IsEqual.equalTo(5));
        Assert.assertThat(map, Matchers.hasKey(4L));
        Assert.assertThat(map, Matchers.hasKey(3L));
        Assert.assertThat(map, Matchers.hasKey(2L));
        Assert.assertThat(map, Matchers.hasKey(1L));
        Assert.assertThat(map, Matchers.hasKey(0L));
    }

    @Test(timeout = 3000)
    public void test_onePermitSameHash() throws InterruptedException {
        QueryQueuingManager manager = new QueryQueuingManager(3, "hostname");
        QueryQueuingManagerTest.Query query1 = new QueryQueuingManagerTest.Query(manager, "1", 5);
        QueryQueuingManagerTest.Query query2 = new QueryQueuingManagerTest.Query(manager, "1", 5);
        QueryQueuingManagerTest.Query query3 = new QueryQueuingManagerTest.Query(manager, "1", 5);
        QueryQueuingManagerTest.Query query4 = new QueryQueuingManagerTest.Query(manager, "1", 5);
        QueryQueuingManagerTest.Query query5 = new QueryQueuingManagerTest.Query(manager, "1", 5);
        query1.start();
        query2.start();
        query3.start();
        query4.start();
        query5.start();
        query1.join();
        query2.join();
        query3.join();
        query4.join();
        query5.join();
        Assert.assertThat(query1.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query2.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query3.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query4.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query5.didRun, IsEqual.equalTo(true));
        // Number of collisions
        Assert.assertThat(manager.getMetrics(System.currentTimeMillis()).get(0).getDataPoints().get(0).getLongValue(), IsEqual.equalTo(4L));
    }

    @Test(timeout = 3000)
    public void test_EnoughPermitsDifferentHashes() throws InterruptedException {
        QueryQueuingManager manager = new QueryQueuingManager(3, "hostname");
        QueryQueuingManagerTest.Query query1 = new QueryQueuingManagerTest.Query(manager, "1", 3);
        QueryQueuingManagerTest.Query query2 = new QueryQueuingManagerTest.Query(manager, "2", 3);
        QueryQueuingManagerTest.Query query3 = new QueryQueuingManagerTest.Query(manager, "3", 3);
        query1.start();
        query2.start();
        query3.start();
        query1.join();
        query2.join();
        query3.join();
        Assert.assertThat(query1.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query2.didRun, IsEqual.equalTo(true));
        Assert.assertThat(query3.didRun, IsEqual.equalTo(true));
        List<DataPointSet> metrics = manager.getMetrics(System.currentTimeMillis());
        Assert.assertThat(metrics.get(0).getDataPoints().get(0).getLongValue(), IsEqual.equalTo(0L));
        Assert.assertThat(manager.getQueryWaitingCount(), IsEqual.equalTo(0));
    }

    private class Query extends Thread {
        private QueryQueuingManager manager;

        private String hash;

        private int waitCount;

        private boolean didRun = false;

        private long queriesWatiting;

        private Query(QueryQueuingManager manager, String hash, int waitCount) {
            this.manager = manager;
            this.hash = hash;
            this.waitCount = waitCount;
        }

        @Override
        public void run() {
            try {
                runningCount.incrementAndGet();
                manager.waitForTimeToRun(hash);
                while ((runningCount.get()) < (waitCount)) {
                    Thread.sleep(100);
                } 
                queriesWatiting = manager.getQueryWaitingCount();
            } catch (InterruptedException e) {
                Assert.assertFalse("InterruptedException", false);
            }
            didRun = true;
            manager.done(hash);
        }
    }
}

