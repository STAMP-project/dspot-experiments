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


import TableProcedureInterface.TableOperationType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.procedure.PeerProcedureInterface.PeerOperationType;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterProcedureSchedulerConcurrency {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterProcedureSchedulerConcurrency.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterProcedureSchedulerConcurrency.class);

    private MasterProcedureScheduler queue;

    @Test
    public void testConcurrentPeerOperations() throws Exception {
        TestMasterProcedureSchedulerConcurrency.TestPeerProcedureSet procSet = new TestMasterProcedureSchedulerConcurrency.TestPeerProcedureSet(queue);
        int NUM_ITEMS = 10;
        int NUM_PEERS = 5;
        AtomicInteger opsCount = new AtomicInteger(0);
        for (int i = 0; i < NUM_PEERS; ++i) {
            String peerId = String.format("test-peer-%04d", i);
            for (int j = 1; j < NUM_ITEMS; ++j) {
                procSet.addBack(new TestMasterProcedureScheduler.TestPeerProcedure(((i * 100) + j), peerId, PeerOperationType.ADD));
                opsCount.incrementAndGet();
            }
        }
        Assert.assertEquals(opsCount.get(), queue.size());
        Thread[] threads = new Thread[NUM_PEERS * 2];
        HashSet<String> concurrentPeers = new HashSet<>();
        ArrayList<String> failures = new ArrayList<>();
        AtomicInteger concurrentCount = new AtomicInteger(0);
        for (int i = 0; i < (threads.length); ++i) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    while ((opsCount.get()) > 0) {
                        try {
                            TestMasterProcedureScheduler.TestPeerProcedure proc = procSet.acquire();
                            if (proc == null) {
                                queue.signalAll();
                                if ((opsCount.get()) > 0) {
                                    continue;
                                }
                                break;
                            }
                            String peerId = proc.getPeerId();
                            synchronized(concurrentPeers) {
                                Assert.assertTrue(("unexpected concurrency on " + peerId), concurrentPeers.add(peerId));
                            }
                            Assert.assertTrue(((opsCount.decrementAndGet()) >= 0));
                            try {
                                long procId = getProcId();
                                int concurrent = concurrentCount.incrementAndGet();
                                Assert.assertTrue(((("inc-concurrent=" + concurrent) + " 1 <= concurrent <= ") + NUM_PEERS), ((concurrent >= 1) && (concurrent <= NUM_PEERS)));
                                TestMasterProcedureSchedulerConcurrency.LOG.debug(((((("[S] peerId=" + peerId) + " procId=") + procId) + " concurrent=") + concurrent));
                                Thread.sleep(2000);
                                concurrent = concurrentCount.decrementAndGet();
                                TestMasterProcedureSchedulerConcurrency.LOG.debug(((((("[E] peerId=" + peerId) + " procId=") + procId) + " concurrent=") + concurrent));
                                Assert.assertTrue(("dec-concurrent=" + concurrent), (concurrent < NUM_PEERS));
                            } finally {
                                synchronized(concurrentPeers) {
                                    Assert.assertTrue(concurrentPeers.remove(peerId));
                                }
                                procSet.release(proc);
                            }
                        } catch (Throwable e) {
                            TestMasterProcedureSchedulerConcurrency.LOG.error(("Failed " + (e.getMessage())), e);
                            synchronized(failures) {
                                failures.add(e.getMessage());
                            }
                        } finally {
                            queue.signalAll();
                        }
                    } 
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < (threads.length); ++i) {
            threads[i].join();
        }
        Assert.assertTrue(failures.toString(), failures.isEmpty());
        Assert.assertEquals(0, opsCount.get());
        Assert.assertEquals(0, queue.size());
    }

    /**
     * Verify that "write" operations for a single table are serialized,
     * but different tables can be executed in parallel.
     */
    @Test
    public void testConcurrentWriteOps() throws Exception {
        final TestMasterProcedureSchedulerConcurrency.TestTableProcSet procSet = new TestMasterProcedureSchedulerConcurrency.TestTableProcSet(queue);
        final int NUM_ITEMS = 10;
        final int NUM_TABLES = 4;
        final AtomicInteger opsCount = new AtomicInteger(0);
        for (int i = 0; i < NUM_TABLES; ++i) {
            TableName tableName = TableName.valueOf(String.format("testtb-%04d", i));
            for (int j = 1; j < NUM_ITEMS; ++j) {
                procSet.addBack(new TestMasterProcedureScheduler.TestTableProcedure(((i * 100) + j), tableName, TableOperationType.EDIT));
                opsCount.incrementAndGet();
            }
        }
        Assert.assertEquals(opsCount.get(), queue.size());
        final Thread[] threads = new Thread[NUM_TABLES * 2];
        final HashSet<TableName> concurrentTables = new HashSet<>();
        final ArrayList<String> failures = new ArrayList<>();
        final AtomicInteger concurrentCount = new AtomicInteger(0);
        for (int i = 0; i < (threads.length); ++i) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    while ((opsCount.get()) > 0) {
                        try {
                            Procedure proc = procSet.acquire();
                            if (proc == null) {
                                queue.signalAll();
                                if ((opsCount.get()) > 0) {
                                    continue;
                                }
                                break;
                            }
                            TableName tableId = procSet.getTableName(proc);
                            synchronized(concurrentTables) {
                                Assert.assertTrue(("unexpected concurrency on " + tableId), concurrentTables.add(tableId));
                            }
                            Assert.assertTrue(((opsCount.decrementAndGet()) >= 0));
                            try {
                                long procId = proc.getProcId();
                                int concurrent = concurrentCount.incrementAndGet();
                                Assert.assertTrue(((("inc-concurrent=" + concurrent) + " 1 <= concurrent <= ") + NUM_TABLES), ((concurrent >= 1) && (concurrent <= NUM_TABLES)));
                                TestMasterProcedureSchedulerConcurrency.LOG.debug(((((("[S] tableId=" + tableId) + " procId=") + procId) + " concurrent=") + concurrent));
                                Thread.sleep(2000);
                                concurrent = concurrentCount.decrementAndGet();
                                TestMasterProcedureSchedulerConcurrency.LOG.debug(((((("[E] tableId=" + tableId) + " procId=") + procId) + " concurrent=") + concurrent));
                                Assert.assertTrue(("dec-concurrent=" + concurrent), (concurrent < NUM_TABLES));
                            } finally {
                                synchronized(concurrentTables) {
                                    Assert.assertTrue(concurrentTables.remove(tableId));
                                }
                                procSet.release(proc);
                            }
                        } catch (Throwable e) {
                            TestMasterProcedureSchedulerConcurrency.LOG.error(("Failed " + (e.getMessage())), e);
                            synchronized(failures) {
                                failures.add(e.getMessage());
                            }
                        } finally {
                            queue.signalAll();
                        }
                    } 
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < (threads.length); ++i) {
            threads[i].join();
        }
        Assert.assertTrue(failures.toString(), failures.isEmpty());
        Assert.assertEquals(0, opsCount.get());
        Assert.assertEquals(0, queue.size());
        for (int i = 1; i <= NUM_TABLES; ++i) {
            final TableName table = TableName.valueOf(String.format("testtb-%04d", i));
            final TestMasterProcedureScheduler.TestTableProcedure dummyProc = new TestMasterProcedureScheduler.TestTableProcedure(100, table, TableOperationType.DELETE);
            Assert.assertTrue(("queue should be deleted, table=" + table), queue.markTableAsDeleted(table, dummyProc));
        }
    }

    @Test
    public void testMasterProcedureSchedulerPerformanceEvaluation() throws Exception {
        // Make sure the tool does not get stuck
        MasterProcedureSchedulerPerformanceEvaluation.main(new String[]{ "-num_ops", "1000" });
    }

    public static class TestTableProcSet {
        private final MasterProcedureScheduler queue;

        public TestTableProcSet(final MasterProcedureScheduler queue) {
            this.queue = queue;
        }

        public void addBack(Procedure proc) {
            queue.addBack(proc);
        }

        public void addFront(Procedure proc) {
            queue.addFront(proc);
        }

        public Procedure acquire() {
            Procedure proc = null;
            boolean waiting = true;
            while (waiting && ((queue.size()) > 0)) {
                proc = queue.poll(100000000L);
                if (proc == null)
                    continue;

                switch (getTableOperationType(proc)) {
                    case CREATE :
                    case DELETE :
                    case EDIT :
                        waiting = queue.waitTableExclusiveLock(proc, getTableName(proc));
                        break;
                    case READ :
                        waiting = queue.waitTableSharedLock(proc, getTableName(proc));
                        break;
                    default :
                        throw new UnsupportedOperationException();
                }
            } 
            return proc;
        }

        public void release(Procedure proc) {
            switch (getTableOperationType(proc)) {
                case CREATE :
                case DELETE :
                case EDIT :
                    queue.wakeTableExclusiveLock(proc, getTableName(proc));
                    break;
                case READ :
                    queue.wakeTableSharedLock(proc, getTableName(proc));
                    break;
                default :
                    throw new UnsupportedOperationException();
            }
        }

        public TableName getTableName(Procedure proc) {
            return ((TableProcedureInterface) (proc)).getTableName();
        }

        public TableOperationType getTableOperationType(Procedure proc) {
            return ((TableProcedureInterface) (proc)).getTableOperationType();
        }
    }

    public static class TestPeerProcedureSet {
        private final MasterProcedureScheduler queue;

        public TestPeerProcedureSet(final MasterProcedureScheduler queue) {
            this.queue = queue;
        }

        public void addBack(TestMasterProcedureScheduler.TestPeerProcedure proc) {
            queue.addBack(proc);
        }

        public TestMasterProcedureScheduler.TestPeerProcedure acquire() {
            TestMasterProcedureScheduler.TestPeerProcedure proc = null;
            boolean waiting = true;
            while (waiting && ((queue.size()) > 0)) {
                proc = ((TestMasterProcedureScheduler.TestPeerProcedure) (queue.poll(100000000L)));
                if (proc == null) {
                    continue;
                }
                switch (proc.getPeerOperationType()) {
                    case ADD :
                    case REMOVE :
                    case ENABLE :
                    case DISABLE :
                    case UPDATE_CONFIG :
                        waiting = queue.waitPeerExclusiveLock(proc, proc.getPeerId());
                        break;
                    case REFRESH :
                        waiting = false;
                        break;
                    default :
                        throw new UnsupportedOperationException();
                }
            } 
            return proc;
        }

        public void release(TestMasterProcedureScheduler.TestPeerProcedure proc) {
            switch (proc.getPeerOperationType()) {
                case ADD :
                case REMOVE :
                case ENABLE :
                case DISABLE :
                case UPDATE_CONFIG :
                    queue.wakePeerExclusiveLock(proc, proc.getPeerId());
                    break;
                case REFRESH :
                    break;
                default :
                    throw new UnsupportedOperationException();
            }
        }
    }
}

