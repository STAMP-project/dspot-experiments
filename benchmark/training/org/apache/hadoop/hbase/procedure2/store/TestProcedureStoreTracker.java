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
package org.apache.hadoop.hbase.procedure2.store;


import Procedure.NO_PROC_ID;
import ProcedureStoreTracker.DeleteState;
import ProcedureStoreTracker.DeleteState.MAYBE;
import ProcedureStoreTracker.DeleteState.NO;
import ProcedureStoreTracker.DeleteState.YES;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureStoreTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureStoreTracker.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureStoreTracker.class);

    @Test
    public void testSeqInsertAndDelete() {
        ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        Assert.assertTrue(tracker.isEmpty());
        final int MIN_PROC = 1;
        final int MAX_PROC = 1 << 10;
        // sequential insert
        for (int i = MIN_PROC; i < MAX_PROC; ++i) {
            tracker.insert(i);
            // All the proc that we inserted should not be deleted
            for (int j = MIN_PROC; j <= i; ++j) {
                Assert.assertEquals(NO, tracker.isDeleted(j));
            }
            // All the proc that are not yet inserted should be result as deleted
            for (int j = i + 1; j < MAX_PROC; ++j) {
                Assert.assertTrue(((tracker.isDeleted(j)) != (DeleteState.NO)));
            }
        }
        // sequential delete
        for (int i = MIN_PROC; i < MAX_PROC; ++i) {
            tracker.delete(i);
            // All the proc that we deleted should be deleted
            for (int j = MIN_PROC; j <= i; ++j) {
                Assert.assertEquals(YES, tracker.isDeleted(j));
            }
            // All the proc that are not yet deleted should be result as not deleted
            for (int j = i + 1; j < MAX_PROC; ++j) {
                Assert.assertEquals(NO, tracker.isDeleted(j));
            }
        }
        Assert.assertTrue(tracker.isEmpty());
    }

    @Test
    public void testPartialTracker() {
        ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        tracker.setPartialFlag(true);
        // nothing in the tracker, the state is unknown
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertEquals(MAYBE, tracker.isDeleted(1));
        Assert.assertEquals(MAYBE, tracker.isDeleted(579));
        // Mark 1 as deleted, now that is a known state
        tracker.setDeleted(1, true);
        tracker.dump();
        Assert.assertEquals(YES, tracker.isDeleted(1));
        Assert.assertEquals(MAYBE, tracker.isDeleted(2));
        Assert.assertEquals(MAYBE, tracker.isDeleted(579));
        // Mark 579 as non-deleted, now that is a known state
        tracker.setDeleted(579, false);
        Assert.assertEquals(YES, tracker.isDeleted(1));
        Assert.assertEquals(MAYBE, tracker.isDeleted(2));
        Assert.assertEquals(NO, tracker.isDeleted(579));
        Assert.assertEquals(MAYBE, tracker.isDeleted(577));
        Assert.assertEquals(MAYBE, tracker.isDeleted(580));
        tracker.setDeleted(579, true);
        tracker.setPartialFlag(false);
        Assert.assertTrue(tracker.isEmpty());
    }

    @Test
    public void testBasicCRUD() {
        ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        Assert.assertTrue(tracker.isEmpty());
        long[] procs = new long[]{ 1, 2, 3, 4, 5, 6 };
        tracker.insert(procs[0]);
        tracker.insert(procs[1], new long[]{ procs[2], procs[3], procs[4] });
        Assert.assertFalse(tracker.isEmpty());
        Assert.assertTrue(tracker.isAllModified());
        tracker.resetModified();
        Assert.assertFalse(tracker.isAllModified());
        for (int i = 0; i < 4; ++i) {
            tracker.update(procs[i]);
            Assert.assertFalse(tracker.isEmpty());
            Assert.assertFalse(tracker.isAllModified());
        }
        tracker.update(procs[4]);
        Assert.assertFalse(tracker.isEmpty());
        Assert.assertTrue(tracker.isAllModified());
        tracker.update(procs[5]);
        Assert.assertFalse(tracker.isEmpty());
        Assert.assertTrue(tracker.isAllModified());
        for (int i = 0; i < 5; ++i) {
            tracker.delete(procs[i]);
            Assert.assertFalse(tracker.isEmpty());
            Assert.assertTrue(tracker.isAllModified());
        }
        tracker.delete(procs[5]);
        Assert.assertTrue(tracker.isEmpty());
    }

    @Test
    public void testRandLoad() {
        final int NPROCEDURES = 2500;
        final int NRUNS = 5000;
        final ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        Random rand = new Random(1);
        for (int i = 0; i < NRUNS; ++i) {
            Assert.assertTrue(tracker.isEmpty());
            int count = 0;
            while (count < NPROCEDURES) {
                long procId = rand.nextLong();
                if (procId < 1)
                    continue;

                tracker.setDeleted(procId, ((i % 2) == 0));
                count++;
            } 
            tracker.reset();
        }
    }

    @Test
    public void testLoad() {
        final int MAX_PROCS = 1000;
        final ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        for (int numProcs = 1; numProcs < MAX_PROCS; ++numProcs) {
            for (int start = 1; start <= numProcs; ++start) {
                Assert.assertTrue(tracker.isEmpty());
                TestProcedureStoreTracker.LOG.debug(((("loading " + numProcs) + " procs from start=") + start));
                for (int i = start; i <= numProcs; ++i) {
                    tracker.setDeleted(i, false);
                }
                for (int i = 1; i < start; ++i) {
                    tracker.setDeleted(i, false);
                }
                tracker.reset();
            }
        }
    }

    @Test
    public void testDelete() {
        final ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        long[] procIds = new long[]{ 65, 1, 193 };
        for (int i = 0; i < (procIds.length); ++i) {
            tracker.insert(procIds[i]);
            tracker.dump();
        }
        for (int i = 0; i < (64 * 4); ++i) {
            boolean hasProc = false;
            for (int j = 0; j < (procIds.length); ++j) {
                if ((procIds[j]) == i) {
                    hasProc = true;
                    break;
                }
            }
            if (hasProc) {
                Assert.assertEquals(NO, tracker.isDeleted(i));
            } else {
                Assert.assertEquals(("procId=" + i), YES, tracker.isDeleted(i));
            }
        }
    }

    @Test
    public void testSetDeletedIfModified() {
        final ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        final long[] procIds = new long[]{ 1, 3, 7, 152, 512, 1024, 1025 };
        // test single proc
        for (int i = 0; i < (procIds.length); ++i) {
            tracker.insert(procIds[i]);
        }
        Assert.assertEquals(false, tracker.isEmpty());
        for (int i = 0; i < (procIds.length); ++i) {
            tracker.setDeletedIfModified(((procIds[i]) - 1));
            tracker.setDeletedIfModified(procIds[i]);
            tracker.setDeletedIfModified(((procIds[i]) + 1));
        }
        Assert.assertEquals(true, tracker.isEmpty());
        // test batch
        tracker.reset();
        for (int i = 0; i < (procIds.length); ++i) {
            tracker.insert(procIds[i]);
        }
        Assert.assertEquals(false, tracker.isEmpty());
        tracker.setDeletedIfModified(procIds);
        Assert.assertEquals(true, tracker.isEmpty());
    }

    @Test
    public void testGetActiveProcIds() {
        ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        for (int i = 0; i < 10000; i++) {
            tracker.insert((i * 10));
        }
        for (int i = 0; i < 10000; i += 2) {
            tracker.delete((i * 10));
        }
        long[] activeProcIds = tracker.getAllActiveProcIds();
        Assert.assertEquals(5000, activeProcIds.length);
        for (int i = 0; i < 5000; i++) {
            Assert.assertEquals((((2 * i) + 1) * 10), activeProcIds[i]);
        }
    }

    @Test
    public void testGetActiveMinProcId() {
        ProcedureStoreTracker tracker = new ProcedureStoreTracker();
        Assert.assertEquals(NO_PROC_ID, tracker.getActiveMinProcId());
        for (int i = 100; i < 1000; i = (2 * i) + 1) {
            tracker.insert(i);
        }
        for (int i = 100; i < 1000; i = (2 * i) + 1) {
            Assert.assertEquals(i, tracker.getActiveMinProcId());
            tracker.delete(i);
        }
        Assert.assertEquals(NO_PROC_ID, tracker.getActiveMinProcId());
    }
}

