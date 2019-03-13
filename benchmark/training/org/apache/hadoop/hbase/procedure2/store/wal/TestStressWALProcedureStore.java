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
package org.apache.hadoop.hbase.procedure2.store.wal;


import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestStressWALProcedureStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStressWALProcedureStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALProcedureStore.class);

    private static final int PROCEDURE_STORE_SLOTS = 8;

    private WALProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    @Test
    public void testInsertUpdateDelete() throws Exception {
        final long LAST_PROC_ID = 19999;
        final Thread[] thread = new Thread[TestStressWALProcedureStore.PROCEDURE_STORE_SLOTS];
        final AtomicLong procCounter = new AtomicLong(((long) (Math.round(((Math.random()) * 100)))));
        for (int i = 0; i < (thread.length); ++i) {
            thread[i] = new Thread() {
                @Override
                public void run() {
                    Random rand = new Random();
                    ProcedureTestingUtility.TestProcedure proc;
                    do {
                        // After HBASE-15579 there may be gap in the procId sequence, trying to simulate that.
                        long procId = procCounter.addAndGet((1 + (rand.nextInt(3))));
                        proc = new ProcedureTestingUtility.TestProcedure(procId);
                        // Insert
                        procStore.insert(proc, null);
                        // Update
                        for (int i = 0, nupdates = rand.nextInt(10); i <= nupdates; ++i) {
                            try {
                                Thread.sleep(0, rand.nextInt(15));
                            } catch (InterruptedException e) {
                            }
                            procStore.update(proc);
                        }
                        // Delete
                        procStore.delete(getProcId());
                    } while ((getProcId()) < LAST_PROC_ID );
                }
            };
            thread[i].start();
        }
        for (int i = 0; i < (thread.length); ++i) {
            thread[i].join();
        }
        procStore.getStoreTracker().dump();
        Assert.assertTrue(((procCounter.get()) >= LAST_PROC_ID));
        Assert.assertTrue(procStore.getStoreTracker().isEmpty());
        Assert.assertEquals(1, procStore.getActiveLogs().size());
    }
}

