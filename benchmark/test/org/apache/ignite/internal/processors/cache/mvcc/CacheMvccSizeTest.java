/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.mvcc;


import CacheAtomicityMode.TRANSACTIONAL;
import CacheMode.REPLICATED;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.transactions.TransactionDuplicateKeyException;
import org.junit.Test;

import static java.lang.Thread.State.RUNNABLE;


/**
 *
 */
public class CacheMvccSizeTest extends CacheMvccAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSql() throws Exception {
        startGridsMultiThreaded(2);
        CacheMvccSizeTest.createTable(grid(0));
        checkSizeModificationByOperation("insert into person values(1, 'a')", true, 1);
        checkSizeModificationByOperation("insert into person values(1, 'a')", false, 0);
        checkSizeModificationByOperation(( personTbl) -> personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')")), ( personTbl) -> {
            try {
                personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
            } catch (Exception e) {
                if ((e.getCause()) instanceof TransactionDuplicateKeyException)
                    assertTrue(e.getMessage().startsWith("Duplicate key during INSERT ["));
                else {
                    e.printStackTrace();
                    fail("Unexpected exceptions");
                }
            }
        }, false, 0);
        checkSizeModificationByOperation("merge into person(id, name) values(1, 'a')", true, 1);
        checkSizeModificationByOperation("merge into person(id, name) values(1, 'a')", false, 0);
        checkSizeModificationByOperation("insert into person values(1, 'a')", "merge into person(id, name) values(1, 'b')", true, 0);
        checkSizeModificationByOperation("update person set name = 'b' where id = 1", true, 0);
        checkSizeModificationByOperation("insert into person values(1, 'a')", "update person set name = 'b' where id = 1", true, 0);
        checkSizeModificationByOperation("insert into person values(1, 'a')", "delete from person where id = 1", true, (-1));
        checkSizeModificationByOperation("insert into person values(1, 'a')", "delete from person where id = 1", false, 0);
        checkSizeModificationByOperation("delete from person where id = 1", true, 0);
        checkSizeModificationByOperation("insert into person values(1, 'a')", "select * from person", true, 0);
        checkSizeModificationByOperation("select * from person", true, 0);
        checkSizeModificationByOperation("insert into person values(1, 'a')", "select * from person where id = 1 for update", true, 0);
        checkSizeModificationByOperation("select * from person where id = 1 for update", true, 0);
        checkSizeModificationByOperation(( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
            personTbl.query(CacheMvccSizeTest.q("insert into person values(%d, 'b')", CacheMvccSizeTest.keyInSamePartition(grid(0), "person", 1)));
            personTbl.query(CacheMvccSizeTest.q("insert into person values(%d, 'c')", CacheMvccSizeTest.keyInDifferentPartition(grid(0), "person", 1)));
        }, true, 3);
        checkSizeModificationByOperation(( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
        }, true, 0);
        checkSizeModificationByOperation(( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
            personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
        }, true, 1);
        checkSizeModificationByOperation(( personTbl) -> personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')")), ( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
            personTbl.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
        }, true, 0);
        checkSizeModificationByOperation(( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'a')"));
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
        }, true, 0);
        checkSizeModificationByOperation(( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'a')"));
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
            personTbl.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'a')"));
        }, true, 1);
        checkSizeModificationByOperation(( personTbl) -> personTbl.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'a')")), ( personTbl) -> {
            personTbl.query(CacheMvccSizeTest.q("delete from person where id = 1"));
            personTbl.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'a')"));
        }, true, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testInsertDeleteConcurrent() throws Exception {
        startGridsMultiThreaded(2);
        IgniteCache<?, ?> tbl0 = CacheMvccSizeTest.createTable(grid(0));
        SqlFieldsQuery insert = new SqlFieldsQuery("insert into person(id, name) values(?, 'a')");
        SqlFieldsQuery delete = new SqlFieldsQuery("delete from person where id = ?");
        CompletableFuture<Integer> insertFut = CompletableFuture.supplyAsync(() -> {
            int cnt = 0;
            for (int i = 0; i < 1000; i++)
                cnt += update(insert.setArgs(ThreadLocalRandom.current().nextInt(10)), tbl0);

            return cnt;
        });
        CompletableFuture<Integer> deleteFut = CompletableFuture.supplyAsync(() -> {
            int cnt = 0;
            for (int i = 0; i < 1000; i++)
                cnt += update(delete.setArgs(ThreadLocalRandom.current().nextInt(10)), tbl0);

            return cnt;
        });
        int expSize = (insertFut.join()) - (deleteFut.join());
        assertEquals(expSize, tbl0.size());
        assertEquals(expSize, CacheMvccSizeTest.table(grid(1)).size());
        assertEquals(expSize, tbl0.size(CachePeekMode.BACKUP));
        assertEquals(expSize, CacheMvccSizeTest.table(grid(1)).size(CachePeekMode.BACKUP));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testWriteConflictDoesNotChangeSize() throws Exception {
        startGridsMultiThreaded(2);
        IgniteCache<?, ?> tbl0 = CacheMvccSizeTest.createTable(grid(0));
        tbl0.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
        tbl0.query(CacheMvccSizeTest.q("begin"));
        tbl0.query(CacheMvccSizeTest.q("delete from person where id = 1"));
        CompletableFuture<Void> conflictingStarted = new CompletableFuture<>();
        CompletableFuture<Void> fut = CompletableFuture.runAsync(() -> {
            tbl0.query(CacheMvccSizeTest.q("begin"));
            try {
                tbl0.query(CacheMvccSizeTest.q("select * from person")).getAll();
                conflictingStarted.complete(null);
                tbl0.query(CacheMvccSizeTest.q("merge into person(id, name) values(1, 'b')"));
            } finally {
                tbl0.query(CacheMvccSizeTest.q("commit"));
            }
        });
        conflictingStarted.join();
        tbl0.query(CacheMvccSizeTest.q("commit"));
        try {
            fut.join();
        } catch (Exception e) {
            if ((e.getCause().getCause()) instanceof IgniteSQLException)
                assertTrue(e.getMessage().contains("Failed to finish transaction because it has been rolled back"));
            else {
                e.printStackTrace();
                fail("Unexpected exception");
            }
        }
        assertEquals(0, tbl0.size());
        assertEquals(0, CacheMvccSizeTest.table(grid(1)).size());
        assertEquals(0, tbl0.size(CachePeekMode.BACKUP));
        assertEquals(0, CacheMvccSizeTest.table(grid(1)).size(CachePeekMode.BACKUP));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDeleteChangesSizeAfterUnlock() throws Exception {
        startGridsMultiThreaded(2);
        IgniteCache<?, ?> tbl0 = CacheMvccSizeTest.createTable(grid(0));
        tbl0.query(CacheMvccSizeTest.q("insert into person values(1, 'a')"));
        tbl0.query(CacheMvccSizeTest.q("begin"));
        tbl0.query(CacheMvccSizeTest.q("select * from person where id = 1 for update")).getAll();
        CompletableFuture<Thread> asyncThread = new CompletableFuture<>();
        CompletableFuture<Void> fut = CompletableFuture.runAsync(() -> {
            tbl0.query(CacheMvccSizeTest.q("begin"));
            try {
                tbl0.query(CacheMvccSizeTest.q("select * from person")).getAll();
                asyncThread.complete(Thread.currentThread());
                tbl0.query(CacheMvccSizeTest.q("delete from person where id = 1"));
            } finally {
                tbl0.query(CacheMvccSizeTest.q("commit"));
            }
        });
        Thread concThread = asyncThread.join();
        // wait until concurrent thread blocks awaiting entry mvcc lock release
        while (((concThread.getState()) == (RUNNABLE)) && (!(Thread.currentThread().isInterrupted())));
        tbl0.query(CacheMvccSizeTest.q("commit"));
        fut.join();
        assertEquals(0, tbl0.size());
        assertEquals(0, CacheMvccSizeTest.table(grid(1)).size());
        assertEquals(0, tbl0.size(CachePeekMode.BACKUP));
        assertEquals(0, CacheMvccSizeTest.table(grid(1)).size(CachePeekMode.BACKUP));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDataStreamerModifiesReplicatedCacheSize() throws Exception {
        startGridsMultiThreaded(2);
        IgniteEx ignite = grid(0);
        ignite.createCache(new org.apache.ignite.configuration.CacheConfiguration("test").setAtomicityMode(TRANSACTIONAL).setCacheMode(REPLICATED));
        try (IgniteDataStreamer<Object, Object> streamer = ignite.dataStreamer("test")) {
            streamer.addData(1, "a");
            streamer.addData(CacheMvccSizeTest.keyInDifferentPartition(ignite, "test", 1), "b");
        }
        assertEquals(2, ignite.cache("test").size());
        assertEquals(1, grid(0).cache("test").localSize());
        assertEquals(1, grid(0).cache("test").localSize(CachePeekMode.BACKUP));
        assertEquals(1, grid(1).cache("test").localSize());
        assertEquals(1, grid(1).cache("test").localSize(CachePeekMode.BACKUP));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSizeIsConsistentAfterRebalance() throws Exception {
        IgniteEx ignite = startGrid(0);
        IgniteCache<?, ?> tbl = CacheMvccSizeTest.createTable(ignite);
        for (int i = 0; i < 100; i++)
            tbl.query(CacheMvccSizeTest.q("insert into person values(?, ?)").setArgs(i, i));

        startGrid(1);
        awaitPartitionMapExchange();
        IgniteCache<?, ?> tbl0 = grid(0).cache("person");
        IgniteCache<?, ?> tbl1 = grid(1).cache("person");
        assert ((tbl0.localSize()) != 0) && ((tbl1.localSize()) != 0);
        assertEquals(100, tbl1.size());
        assertEquals(100, ((tbl0.localSize()) + (tbl1.localSize())));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSizeIsConsistentAfterRebalanceDuringInsert() throws Exception {
        IgniteEx ignite = startGrid(0);
        IgniteCache<?, ?> tbl = CacheMvccSizeTest.createTable(ignite);
        Future<?> f = null;
        for (int i = 0; i < 100; i++) {
            if (i == 50)
                f = ForkJoinPool.commonPool().submit(() -> startGrid(1));

            tbl.query(CacheMvccSizeTest.q("insert into person values(?, ?)").setArgs(i, i));
        }
        f.get();
        awaitPartitionMapExchange();
        IgniteCache<?, ?> tbl0 = grid(0).cache("person");
        IgniteCache<?, ?> tbl1 = grid(1).cache("person");
        assert ((tbl0.localSize()) != 0) && ((tbl1.localSize()) != 0);
        assertEquals(100, tbl1.size());
        assertEquals(100, ((tbl0.localSize()) + (tbl1.localSize())));
    }
}

