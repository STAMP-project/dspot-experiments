/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.master.procedure;


import HBaseMarkers.FATAL;
import ProcedureStore.ProcedureStoreListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.TestProcedure;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.procedure2.store.wal.WALProcedureStore;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestWALProcedureStoreOnHDFS {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALProcedureStoreOnHDFS.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALProcedureStoreOnHDFS.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private WALProcedureStore store;

    private ProcedureStoreListener stopProcedureListener = new ProcedureStore.ProcedureStoreListener() {
        @Override
        public void postSync() {
        }

        @Override
        public void abortProcess() {
            TestWALProcedureStoreOnHDFS.LOG.error(FATAL, "Abort the Procedure Store");
            store.stop(true);
        }
    };

    @Test(expected = RuntimeException.class)
    public void testWalAbortOnLowReplication() throws Exception {
        setupDFS();
        Assert.assertEquals(3, TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().getDataNodes().size());
        TestWALProcedureStoreOnHDFS.LOG.info("Stop DataNode");
        TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().stopDataNode(0);
        Assert.assertEquals(2, TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().getDataNodes().size());
        store.insert(new TestProcedure(1, (-1)), null);
        for (long i = 2; store.isRunning(); ++i) {
            Assert.assertEquals(2, TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().getDataNodes().size());
            store.insert(new TestProcedure(i, (-1)), null);
            Thread.sleep(100);
        }
        Assert.assertFalse(store.isRunning());
    }

    @Test
    public void testWalAbortOnLowReplicationWithQueuedWriters() throws Exception {
        setupDFS();
        Assert.assertEquals(3, TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().getDataNodes().size());
        store.registerListener(new ProcedureStore.ProcedureStoreListener() {
            @Override
            public void postSync() {
                Threads.sleepWithoutInterrupt(2000);
            }

            @Override
            public void abortProcess() {
            }
        });
        final AtomicInteger reCount = new AtomicInteger(0);
        Thread[] thread = new Thread[((store.getNumThreads()) * 2) + 1];
        for (int i = 0; i < (thread.length); ++i) {
            final long procId = i + 1L;
            thread[i] = new Thread(() -> {
                try {
                    TestWALProcedureStoreOnHDFS.LOG.debug(("[S] INSERT " + procId));
                    store.insert(new TestProcedure(procId, (-1)), null);
                    TestWALProcedureStoreOnHDFS.LOG.debug(("[E] INSERT " + procId));
                } catch (RuntimeException e) {
                    reCount.incrementAndGet();
                    TestWALProcedureStoreOnHDFS.LOG.debug(((("[F] INSERT " + procId) + ": ") + (e.getMessage())));
                }
            });
            thread[i].start();
        }
        Thread.sleep(1000);
        TestWALProcedureStoreOnHDFS.LOG.info("Stop DataNode");
        TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().stopDataNode(0);
        Assert.assertEquals(2, TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().getDataNodes().size());
        for (int i = 0; i < (thread.length); ++i) {
            thread[i].join();
        }
        Assert.assertFalse(store.isRunning());
        Assert.assertTrue(reCount.toString(), (((reCount.get()) >= (store.getNumThreads())) && ((reCount.get()) < (thread.length))));
    }

    @Test
    public void testWalRollOnLowReplication() throws Exception {
        TestWALProcedureStoreOnHDFS.UTIL.getConfiguration().setInt("dfs.namenode.replication.min", 1);
        setupDFS();
        int dnCount = 0;
        store.insert(new TestProcedure(1, (-1)), null);
        TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().restartDataNode(dnCount);
        for (long i = 2; i < 100; ++i) {
            store.insert(new TestProcedure(i, (-1)), null);
            waitForNumReplicas(3);
            Thread.sleep(100);
            if ((i % 30) == 0) {
                TestWALProcedureStoreOnHDFS.LOG.info("Restart Data Node");
                TestWALProcedureStoreOnHDFS.UTIL.getDFSCluster().restartDataNode(((++dnCount) % 3));
            }
        }
        Assert.assertTrue(store.isRunning());
    }
}

