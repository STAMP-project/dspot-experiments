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
package org.apache.hadoop.hbase.replication.regionserver;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.TestReplicationBase;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestReplicator extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicator.class);

    static final Logger LOG = LoggerFactory.getLogger(TestReplicator.class);

    static final int NUM_ROWS = 10;

    @Test
    public void testReplicatorBatching() throws Exception {
        // Clear the tables
        truncateTable(TestReplicationBase.utility1, TestReplicationBase.tableName);
        truncateTable(TestReplicationBase.utility2, TestReplicationBase.tableName);
        // Replace the peer set up for us by the base class with a wrapper for this test
        TestReplicationBase.admin.addPeer("testReplicatorBatching", new ReplicationPeerConfig().setClusterKey(TestReplicationBase.utility2.getClusterKey()).setReplicationEndpointImpl(TestReplicator.ReplicationEndpointForTest.class.getName()), null);
        TestReplicator.ReplicationEndpointForTest.setBatchCount(0);
        TestReplicator.ReplicationEndpointForTest.setEntriesCount(0);
        try {
            TestReplicator.ReplicationEndpointForTest.pause();
            try {
                // Queue up a bunch of cells of size 8K. Because of RPC size limits, they will all
                // have to be replicated separately.
                final byte[] valueBytes = new byte[8 * 1024];
                for (int i = 0; i < (TestReplicator.NUM_ROWS); i++) {
                    TestReplicationBase.htable1.put(addColumn(TestReplicationBase.famName, null, valueBytes));
                }
            } finally {
                TestReplicator.ReplicationEndpointForTest.resume();
            }
            // Wait for replication to complete.
            Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.ExplainingPredicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    TestReplicator.LOG.info(("Count=" + (org.apache.hadoop.hbase.replication.regionserver.ReplicationEndpointForTest.getBatchCount())));
                    return (org.apache.hadoop.hbase.replication.regionserver.ReplicationEndpointForTest.getBatchCount()) >= (TestReplicator.NUM_ROWS);
                }

                @Override
                public String explainFailure() throws Exception {
                    return ("We waited too long for expected replication of " + (TestReplicator.NUM_ROWS)) + " entries";
                }
            });
            Assert.assertEquals("We sent an incorrect number of batches", TestReplicator.NUM_ROWS, TestReplicator.ReplicationEndpointForTest.getBatchCount());
            Assert.assertEquals("We did not replicate enough rows", TestReplicator.NUM_ROWS, TestReplicationBase.utility2.countRows(TestReplicationBase.htable2));
        } finally {
            TestReplicationBase.admin.removePeer("testReplicatorBatching");
        }
    }

    @Test
    public void testReplicatorWithErrors() throws Exception {
        // Clear the tables
        truncateTable(TestReplicationBase.utility1, TestReplicationBase.tableName);
        truncateTable(TestReplicationBase.utility2, TestReplicationBase.tableName);
        // Replace the peer set up for us by the base class with a wrapper for this test
        TestReplicationBase.admin.addPeer("testReplicatorWithErrors", new ReplicationPeerConfig().setClusterKey(TestReplicationBase.utility2.getClusterKey()).setReplicationEndpointImpl(TestReplicator.FailureInjectingReplicationEndpointForTest.class.getName()), null);
        TestReplicator.FailureInjectingReplicationEndpointForTest.setBatchCount(0);
        TestReplicator.FailureInjectingReplicationEndpointForTest.setEntriesCount(0);
        try {
            TestReplicator.FailureInjectingReplicationEndpointForTest.pause();
            try {
                // Queue up a bunch of cells of size 8K. Because of RPC size limits, they will all
                // have to be replicated separately.
                final byte[] valueBytes = new byte[8 * 1024];
                for (int i = 0; i < (TestReplicator.NUM_ROWS); i++) {
                    TestReplicationBase.htable1.put(addColumn(TestReplicationBase.famName, null, valueBytes));
                }
            } finally {
                TestReplicator.FailureInjectingReplicationEndpointForTest.resume();
            }
            // Wait for replication to complete.
            // We can expect 10 batches
            Waiter.waitFor(TestReplicationBase.conf1, 60000, new Waiter.ExplainingPredicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (org.apache.hadoop.hbase.replication.regionserver.FailureInjectingReplicationEndpointForTest.getEntriesCount()) >= (TestReplicator.NUM_ROWS);
                }

                @Override
                public String explainFailure() throws Exception {
                    return ("We waited too long for expected replication of " + (TestReplicator.NUM_ROWS)) + " entries";
                }
            });
            Assert.assertEquals("We did not replicate enough rows", TestReplicator.NUM_ROWS, TestReplicationBase.utility2.countRows(TestReplicationBase.htable2));
        } finally {
            TestReplicationBase.admin.removePeer("testReplicatorWithErrors");
        }
    }

    public static class ReplicationEndpointForTest extends HBaseInterClusterReplicationEndpoint {
        protected static AtomicInteger batchCount = new AtomicInteger(0);

        protected static int entriesCount;

        private static final Object latch = new Object();

        private static AtomicBoolean useLatch = new AtomicBoolean(false);

        public static void resume() {
            TestReplicator.ReplicationEndpointForTest.useLatch.set(false);
            synchronized(TestReplicator.ReplicationEndpointForTest.latch) {
                TestReplicator.ReplicationEndpointForTest.latch.notifyAll();
            }
        }

        public static void pause() {
            TestReplicator.ReplicationEndpointForTest.useLatch.set(true);
        }

        public static void await() throws InterruptedException {
            if (TestReplicator.ReplicationEndpointForTest.useLatch.get()) {
                TestReplicator.LOG.info("Waiting on latch");
                synchronized(TestReplicator.ReplicationEndpointForTest.latch) {
                    TestReplicator.ReplicationEndpointForTest.latch.wait();
                }
                TestReplicator.LOG.info("Waited on latch, now proceeding");
            }
        }

        public static int getBatchCount() {
            return TestReplicator.ReplicationEndpointForTest.batchCount.get();
        }

        public static void setBatchCount(int i) {
            TestReplicator.LOG.info(((("SetBatchCount=" + i) + ", old=") + (TestReplicator.ReplicationEndpointForTest.getBatchCount())));
            TestReplicator.ReplicationEndpointForTest.batchCount.set(i);
        }

        public static int getEntriesCount() {
            return TestReplicator.ReplicationEndpointForTest.entriesCount;
        }

        public static void setEntriesCount(int i) {
            TestReplicator.LOG.info(("SetEntriesCount=" + i));
            TestReplicator.ReplicationEndpointForTest.entriesCount = i;
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            try {
                TestReplicator.ReplicationEndpointForTest.await();
            } catch (InterruptedException e) {
                TestReplicator.LOG.warn("Interrupted waiting for latch", e);
            }
            return super.replicate(replicateContext);
        }

        @Override
        protected Callable<Integer> createReplicator(List<Entry> entries, int ordinal) {
            return () -> {
                int batchIndex = replicateEntries(entries, ordinal);
                TestReplicator.ReplicationEndpointForTest.entriesCount += entries.size();
                int count = TestReplicator.ReplicationEndpointForTest.batchCount.incrementAndGet();
                TestReplicator.LOG.info(((("Completed replicating batch " + (System.identityHashCode(entries))) + " count=") + count));
                return batchIndex;
            };
        }
    }

    public static class FailureInjectingReplicationEndpointForTest extends TestReplicator.ReplicationEndpointForTest {
        private final AtomicBoolean failNext = new AtomicBoolean(false);

        @Override
        protected Callable<Integer> createReplicator(List<Entry> entries, int ordinal) {
            return () -> {
                if (failNext.compareAndSet(false, true)) {
                    int batchIndex = replicateEntries(entries, ordinal);
                    TestReplicator.ReplicationEndpointForTest.entriesCount += entries.size();
                    int count = TestReplicator.ReplicationEndpointForTest.batchCount.incrementAndGet();
                    TestReplicator.LOG.info(((("Completed replicating batch " + (System.identityHashCode(entries))) + " count=") + count));
                    return batchIndex;
                } else
                    if (failNext.compareAndSet(true, false)) {
                        throw new ServiceException("Injected failure");
                    }

                return ordinal;
            };
        }
    }
}

