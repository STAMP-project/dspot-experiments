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


import Int64Value.Builder;
import WALProcedureStore.EXEC_WAL_CLEANUP_ON_LOAD_CONF_KEY;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureStateSerializer;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.procedure2.SequentialProcedure;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStoreTracker;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hbase.thirdparty.com.google.protobuf.Int64Value;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestWALProcedureStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALProcedureStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALProcedureStore.class);

    private static final int PROCEDURE_STORE_SLOTS = 1;

    private WALProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    @Test
    public void testEmptyRoll() throws Exception {
        for (int i = 0; i < 10; ++i) {
            procStore.periodicRollForTesting();
        }
        Assert.assertEquals(1, procStore.getActiveLogs().size());
        FileStatus[] status = fs.listStatus(logDir);
        Assert.assertEquals(1, status.length);
    }

    @Test
    public void testRestartWithoutData() throws Exception {
        for (int i = 0; i < 10; ++i) {
            final ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
            storeRestart(loader);
        }
        TestWALProcedureStore.LOG.info(("ACTIVE WALs " + (procStore.getActiveLogs())));
        Assert.assertEquals(1, procStore.getActiveLogs().size());
        FileStatus[] status = fs.listStatus(logDir);
        Assert.assertEquals(1, status.length);
    }

    /**
     * Tests that tracker for all old logs are loaded back after procedure store is restarted.
     */
    @Test
    public void trackersLoadedForAllOldLogs() throws Exception {
        for (int i = 0; i <= 20; ++i) {
            procStore.insert(new ProcedureTestingUtility.TestProcedure(i), null);
            if ((i > 0) && ((i % 5) == 0)) {
                ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
                storeRestart(loader);
            }
        }
        Assert.assertEquals(5, procStore.getActiveLogs().size());
        for (int i = 0; i < ((procStore.getActiveLogs().size()) - 1); ++i) {
            ProcedureStoreTracker tracker = procStore.getActiveLogs().get(i).getTracker();
            Assert.assertTrue(((tracker != null) && (!(tracker.isEmpty()))));
        }
    }

    @Test
    public void testWalCleanerSequentialClean() throws Exception {
        final Procedure<?>[] procs = new Procedure[5];
        ArrayList<ProcedureWALFile> logs = null;
        // Insert procedures and roll wal after every insert.
        for (int i = 0; i < (procs.length); i++) {
            procs[i] = new TestWALProcedureStore.TestSequentialProcedure();
            procStore.insert(procs[i], null);
            procStore.rollWriterForTesting();
            logs = procStore.getActiveLogs();
            Assert.assertEquals(logs.size(), (i + 2));// Extra 1 for current ongoing wal.

        }
        // Delete procedures in sequential order make sure that only the corresponding wal is deleted
        // from logs list.
        final int[] deleteOrder = new int[]{ 0, 1, 2, 3, 4 };
        for (int i = 0; i < (deleteOrder.length); i++) {
            procStore.delete(procs[deleteOrder[i]].getProcId());
            procStore.removeInactiveLogsForTesting();
            Assert.assertFalse(logs.get(deleteOrder[i]).toString(), procStore.getActiveLogs().contains(logs.get(deleteOrder[i])));
            Assert.assertEquals(procStore.getActiveLogs().size(), ((procs.length) - i));
        }
    }

    // Test that wal cleaner doesn't create holes in wal files list i.e. it only deletes files if
    // they are in the starting of the list.
    @Test
    public void testWalCleanerNoHoles() throws Exception {
        final Procedure<?>[] procs = new Procedure[5];
        ArrayList<ProcedureWALFile> logs = null;
        // Insert procedures and roll wal after every insert.
        for (int i = 0; i < (procs.length); i++) {
            procs[i] = new TestWALProcedureStore.TestSequentialProcedure();
            procStore.insert(procs[i], null);
            procStore.rollWriterForTesting();
            logs = procStore.getActiveLogs();
            Assert.assertEquals((i + 2), logs.size());// Extra 1 for current ongoing wal.

        }
        for (int i = 1; i < (procs.length); i++) {
            procStore.delete(procs[i].getProcId());
        }
        Assert.assertEquals(((procs.length) + 1), procStore.getActiveLogs().size());
        procStore.delete(procs[0].getProcId());
        Assert.assertEquals(1, procStore.getActiveLogs().size());
    }

    @Test
    public void testWalCleanerUpdates() throws Exception {
        TestWALProcedureStore.TestSequentialProcedure p1 = new TestWALProcedureStore.TestSequentialProcedure();
        TestWALProcedureStore.TestSequentialProcedure p2 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(p1, null);
        procStore.insert(p2, null);
        procStore.rollWriterForTesting();
        ProcedureWALFile firstLog = procStore.getActiveLogs().get(0);
        procStore.update(p1);
        procStore.rollWriterForTesting();
        procStore.update(p2);
        procStore.rollWriterForTesting();
        procStore.removeInactiveLogsForTesting();
        Assert.assertFalse(procStore.getActiveLogs().contains(firstLog));
    }

    @Test
    public void testWalCleanerUpdatesDontLeaveHoles() throws Exception {
        TestWALProcedureStore.TestSequentialProcedure p1 = new TestWALProcedureStore.TestSequentialProcedure();
        TestWALProcedureStore.TestSequentialProcedure p2 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(p1, null);
        procStore.insert(p2, null);
        procStore.rollWriterForTesting();// generates first log with p1 + p2

        ProcedureWALFile log1 = procStore.getActiveLogs().get(0);
        procStore.update(p2);
        procStore.rollWriterForTesting();// generates second log with p2

        ProcedureWALFile log2 = procStore.getActiveLogs().get(1);
        procStore.update(p2);
        procStore.rollWriterForTesting();// generates third log with p2

        procStore.removeInactiveLogsForTesting();// Shouldn't remove 2nd log.

        Assert.assertEquals(4, procStore.getActiveLogs().size());
        procStore.update(p1);
        procStore.rollWriterForTesting();// generates fourth log with p1

        procStore.removeInactiveLogsForTesting();// Should remove first two logs.

        Assert.assertEquals(3, procStore.getActiveLogs().size());
        Assert.assertFalse(procStore.getActiveLogs().contains(log1));
        Assert.assertFalse(procStore.getActiveLogs().contains(log2));
    }

    @Test
    public void testWalCleanerWithEmptyRolls() throws Exception {
        final Procedure<?>[] procs = new Procedure[3];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new TestWALProcedureStore.TestSequentialProcedure();
            procStore.insert(procs[i], null);
        }
        Assert.assertEquals(1, procStore.getActiveLogs().size());
        procStore.rollWriterForTesting();
        Assert.assertEquals(2, procStore.getActiveLogs().size());
        procStore.rollWriterForTesting();
        Assert.assertEquals(3, procStore.getActiveLogs().size());
        for (int i = 0; i < (procs.length); ++i) {
            procStore.update(procs[i]);
            procStore.rollWriterForTesting();
            procStore.rollWriterForTesting();
            if (i < ((procs.length) - 1)) {
                Assert.assertEquals((3 + ((i + 1) * 2)), procStore.getActiveLogs().size());
            }
        }
        Assert.assertEquals(7, procStore.getActiveLogs().size());
        for (int i = 0; i < (procs.length); ++i) {
            procStore.delete(procs[i].getProcId());
            Assert.assertEquals((7 - ((i + 1) * 2)), procStore.getActiveLogs().size());
        }
        Assert.assertEquals(1, procStore.getActiveLogs().size());
    }

    @Test
    public void testEmptyLogLoad() throws Exception {
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(0, loader.getMaxProcId());
        Assert.assertEquals(0, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
    }

    @Test
    public void testLoad() throws Exception {
        Set<Long> procIds = new HashSet<>();
        // Insert something in the log
        Procedure<?> proc1 = new TestWALProcedureStore.TestSequentialProcedure();
        procIds.add(proc1.getProcId());
        procStore.insert(proc1, null);
        Procedure<?> proc2 = new TestWALProcedureStore.TestSequentialProcedure();
        Procedure<?>[] child2 = new Procedure[2];
        child2[0] = new TestWALProcedureStore.TestSequentialProcedure();
        child2[1] = new TestWALProcedureStore.TestSequentialProcedure();
        procIds.add(proc2.getProcId());
        procIds.add(child2[0].getProcId());
        procIds.add(child2[1].getProcId());
        procStore.insert(proc2, child2);
        // Verify that everything is there
        verifyProcIdsOnRestart(procIds);
        // Update and delete something
        procStore.update(proc1);
        procStore.update(child2[1]);
        procStore.delete(child2[1].getProcId());
        procIds.remove(child2[1].getProcId());
        // Verify that everything is there
        verifyProcIdsOnRestart(procIds);
        // Remove 4 byte from the trailers
        procStore.stop(false);
        FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(3, logs.length);
        for (int i = 0; i < (logs.length); ++i) {
            corruptLog(logs[i], 4);
        }
        verifyProcIdsOnRestart(procIds);
    }

    @Test
    public void testNoTrailerDoubleRestart() throws Exception {
        // log-0001: proc 0, 1 and 2 are inserted
        Procedure<?> proc0 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(proc0, null);
        Procedure<?> proc1 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(proc1, null);
        Procedure<?> proc2 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(proc2, null);
        procStore.rollWriterForTesting();
        // log-0002: proc 1 deleted
        procStore.delete(proc1.getProcId());
        procStore.rollWriterForTesting();
        // log-0003: proc 2 is update
        procStore.update(proc2);
        procStore.rollWriterForTesting();
        // log-0004: proc 2 deleted
        procStore.delete(proc2.getProcId());
        // stop the store and remove the trailer
        procStore.stop(false);
        FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(4, logs.length);
        for (int i = 0; i < (logs.length); ++i) {
            corruptLog(logs[i], 4);
        }
        // Test Load 1
        // Restart the store (avoid cleaning up the files, to check the rebuilded trackers)
        htu.getConfiguration().setBoolean(EXEC_WAL_CLEANUP_ON_LOAD_CONF_KEY, false);
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(1, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
        // Test Load 2
        Assert.assertEquals(5, fs.listStatus(logDir).length);
        loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(1, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
        // remove proc-0
        procStore.delete(proc0.getProcId());
        procStore.periodicRollForTesting();
        Assert.assertEquals(1, fs.listStatus(logDir).length);
        storeRestart(loader);
    }

    @Test
    public void testProcIdHoles() throws Exception {
        // Insert
        for (int i = 0; i < 100; i += 2) {
            procStore.insert(new ProcedureTestingUtility.TestProcedure(i), null);
            if ((i > 0) && ((i % 10) == 0)) {
                ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
                storeRestart(loader);
                Assert.assertEquals(0, loader.getCorruptedCount());
                Assert.assertEquals(((i / 2) + 1), loader.getLoadedCount());
            }
        }
        Assert.assertEquals(10, procStore.getActiveLogs().size());
        // Delete
        for (int i = 0; i < 100; i += 2) {
            procStore.delete(i);
        }
        Assert.assertEquals(1, procStore.getActiveLogs().size());
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(0, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
    }

    @Test
    public void testCorruptedTrailer() throws Exception {
        // Insert something
        for (int i = 0; i < 100; ++i) {
            procStore.insert(new TestWALProcedureStore.TestSequentialProcedure(), null);
        }
        // Stop the store
        procStore.stop(false);
        // Remove 4 byte from the trailer
        FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(1, logs.length);
        corruptLog(logs[0], 4);
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(100, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
    }

    @Test
    public void testCorruptedTrailersRebuild() throws Exception {
        final Procedure<?>[] procs = new Procedure[6];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new TestWALProcedureStore.TestSequentialProcedure();
        }
        // Log State (I=insert, U=updated, D=delete)
        // | log 1 | log 2 | log 3 |
        // 0 | I, D  |       |       |
        // 1 | I     |       |       |
        // 2 | I     | D     |       |
        // 3 | I     | U     |       |
        // 4 |       | I     | D     |
        // 5 |       |       | I     |
        procStore.insert(procs[0], null);
        procStore.insert(procs[1], null);
        procStore.insert(procs[2], null);
        procStore.insert(procs[3], null);
        procStore.delete(procs[0].getProcId());
        procStore.rollWriterForTesting();
        procStore.delete(procs[2].getProcId());
        procStore.update(procs[3]);
        procStore.insert(procs[4], null);
        procStore.rollWriterForTesting();
        procStore.delete(procs[4].getProcId());
        procStore.insert(procs[5], null);
        // Stop the store
        procStore.stop(false);
        // Remove 4 byte from the trailers
        final FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(3, logs.length);
        for (int i = 0; i < (logs.length); ++i) {
            corruptLog(logs[i], 4);
        }
        // Restart the store (avoid cleaning up the files, to check the rebuilded trackers)
        htu.getConfiguration().setBoolean(EXEC_WAL_CLEANUP_ON_LOAD_CONF_KEY, false);
        final ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(3, loader.getLoadedCount());// procs 1, 3 and 5

        Assert.assertEquals(0, loader.getCorruptedCount());
        // Check the Trackers
        final ArrayList<ProcedureWALFile> walFiles = procStore.getActiveLogs();
        TestWALProcedureStore.LOG.info(("WALs " + walFiles));
        Assert.assertEquals(4, walFiles.size());
        TestWALProcedureStore.LOG.info(("Checking wal " + (walFiles.get(0))));
        TestWALProcedureStore.assertUpdated(walFiles.get(0).getTracker(), procs, new int[]{ 0, 1, 2, 3 }, new int[]{ 4, 5 });
        TestWALProcedureStore.LOG.info(("Checking wal " + (walFiles.get(1))));
        TestWALProcedureStore.assertUpdated(walFiles.get(1).getTracker(), procs, new int[]{ 2, 3, 4 }, new int[]{ 0, 1, 5 });
        TestWALProcedureStore.LOG.info(("Checking wal " + (walFiles.get(2))));
        TestWALProcedureStore.assertUpdated(walFiles.get(2).getTracker(), procs, new int[]{ 4, 5 }, new int[]{ 0, 1, 2, 3 });
        TestWALProcedureStore.LOG.info("Checking global tracker ");
        TestWALProcedureStore.assertDeleted(procStore.getStoreTracker(), procs, new int[]{ 0, 2, 4 }, new int[]{ 1, 3, 5 });
    }

    @Test
    public void testCorruptedEntries() throws Exception {
        // Insert something
        for (int i = 0; i < 100; ++i) {
            procStore.insert(new TestWALProcedureStore.TestSequentialProcedure(), null);
        }
        // Stop the store
        procStore.stop(false);
        // Remove some byte from the log
        // (enough to cut the trailer and corrupt some entries)
        FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(1, logs.length);
        corruptLog(logs[0], 1823);
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertTrue(((procStore.getCorruptedLogs()) != null));
        Assert.assertEquals(1, procStore.getCorruptedLogs().size());
        Assert.assertEquals(87, loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
    }

    @Test
    public void testCorruptedProcedures() throws Exception {
        // Insert root-procedures
        ProcedureTestingUtility.TestProcedure[] rootProcs = new ProcedureTestingUtility.TestProcedure[10];
        for (int i = 1; i <= (rootProcs.length); i++) {
            rootProcs[(i - 1)] = new ProcedureTestingUtility.TestProcedure(i, 0);
            procStore.insert(rootProcs[(i - 1)], null);
            rootProcs[(i - 1)].addStackId(0);
            procStore.update(rootProcs[(i - 1)]);
        }
        // insert root-child txn
        procStore.rollWriterForTesting();
        for (int i = 1; i <= (rootProcs.length); i++) {
            ProcedureTestingUtility.TestProcedure b = new ProcedureTestingUtility.TestProcedure(((rootProcs.length) + i), i);
            rootProcs[(i - 1)].addStackId(1);
            procStore.insert(rootProcs[(i - 1)], new Procedure[]{ b });
        }
        // insert child updates
        procStore.rollWriterForTesting();
        for (int i = 1; i <= (rootProcs.length); i++) {
            procStore.update(new ProcedureTestingUtility.TestProcedure(((rootProcs.length) + i), i));
        }
        // Stop the store
        procStore.stop(false);
        // the first log was removed,
        // we have insert-txn and updates in the others so everything is fine
        FileStatus[] logs = fs.listStatus(logDir);
        Assert.assertEquals(Arrays.toString(logs), 2, logs.length);
        Arrays.sort(logs, new Comparator<FileStatus>() {
            @Override
            public int compare(FileStatus o1, FileStatus o2) {
                return o1.getPath().getName().compareTo(o2.getPath().getName());
            }
        });
        ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        storeRestart(loader);
        Assert.assertEquals(((rootProcs.length) * 2), loader.getLoadedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
        // Remove the second log, we have lost all the root/parent references
        fs.delete(logs[0].getPath(), false);
        loader.reset();
        storeRestart(loader);
        Assert.assertEquals(0, loader.getLoadedCount());
        Assert.assertEquals(rootProcs.length, loader.getCorruptedCount());
        for (Procedure<?> proc : loader.getCorrupted()) {
            Assert.assertTrue(proc.toString(), ((proc.getParentProcId()) <= (rootProcs.length)));
            Assert.assertTrue(proc.toString(), (((proc.getProcId()) > (rootProcs.length)) && ((proc.getProcId()) <= ((rootProcs.length) * 2))));
        }
    }

    @Test
    public void testRollAndRemove() throws IOException {
        // Insert something in the log
        Procedure<?> proc1 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(proc1, null);
        Procedure<?> proc2 = new TestWALProcedureStore.TestSequentialProcedure();
        procStore.insert(proc2, null);
        // roll the log, now we have 2
        procStore.rollWriterForTesting();
        Assert.assertEquals(2, procStore.getActiveLogs().size());
        // everything will be up to date in the second log
        // so we can remove the first one
        procStore.update(proc1);
        procStore.update(proc2);
        Assert.assertEquals(1, procStore.getActiveLogs().size());
        // roll the log, now we have 2
        procStore.rollWriterForTesting();
        Assert.assertEquals(2, procStore.getActiveLogs().size());
        // remove everything active
        // so we can remove all the logs
        procStore.delete(proc1.getProcId());
        procStore.delete(proc2.getProcId());
        Assert.assertEquals(1, procStore.getActiveLogs().size());
    }

    @Test
    public void testFileNotFoundDuringLeaseRecovery() throws IOException {
        final ProcedureTestingUtility.TestProcedure[] procs = new ProcedureTestingUtility.TestProcedure[3];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new ProcedureTestingUtility.TestProcedure((i + 1), 0);
            procStore.insert(procs[i], null);
        }
        procStore.rollWriterForTesting();
        for (int i = 0; i < (procs.length); ++i) {
            procStore.update(procs[i]);
            procStore.rollWriterForTesting();
        }
        procStore.stop(false);
        FileStatus[] status = fs.listStatus(logDir);
        Assert.assertEquals(((procs.length) + 1), status.length);
        // simulate another active master removing the wals
        procStore = new WALProcedureStore(htu.getConfiguration(), logDir, null, new WALProcedureStore.LeaseRecovery() {
            private int count = 0;

            @Override
            public void recoverFileLease(FileSystem fs, Path path) throws IOException {
                if ((++(count)) <= 2) {
                    fs.delete(path, false);
                    TestWALProcedureStore.LOG.debug(((("Simulate FileNotFound at count=" + (count)) + " for ") + path));
                    throw new FileNotFoundException(("test file not found " + path));
                }
                TestWALProcedureStore.LOG.debug(((("Simulate recoverFileLease() at count=" + (count)) + " for ") + path));
            }
        });
        final ProcedureTestingUtility.LoadCounter loader = new ProcedureTestingUtility.LoadCounter();
        procStore.start(TestWALProcedureStore.PROCEDURE_STORE_SLOTS);
        procStore.recoverLease();
        procStore.load(loader);
        Assert.assertEquals(procs.length, loader.getMaxProcId());
        Assert.assertEquals(1, loader.getRunnableCount());
        Assert.assertEquals(0, loader.getCompletedCount());
        Assert.assertEquals(0, loader.getCorruptedCount());
    }

    @Test
    public void testLogFileAleadExists() throws IOException {
        final boolean[] tested = new boolean[]{ false };
        WALProcedureStore mStore = Mockito.spy(procStore);
        Answer<Boolean> ans = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                long logId = ((Long) (invocationOnMock.getArgument(0))).longValue();
                switch (((int) (logId))) {
                    case 2 :
                        // Create a file so that real rollWriter() runs into file exists condition
                        Path logFilePath = mStore.getLogFilePath(logId);
                        mStore.getFileSystem().create(logFilePath);
                        break;
                    case 3 :
                        // Success only when we retry with logId 3
                        tested[0] = true;
                    default :
                        break;
                }
                return ((Boolean) (invocationOnMock.callRealMethod()));
            }
        };
        // First time Store has one log file, next id will be 2
        Mockito.doAnswer(ans).when(mStore).rollWriter(2);
        // next time its 3
        Mockito.doAnswer(ans).when(mStore).rollWriter(3);
        mStore.recoverLease();
        Assert.assertTrue(tested[0]);
    }

    @Test
    public void testLoadChildren() throws Exception {
        ProcedureTestingUtility.TestProcedure a = new ProcedureTestingUtility.TestProcedure(1, 0);
        ProcedureTestingUtility.TestProcedure b = new ProcedureTestingUtility.TestProcedure(2, 1);
        ProcedureTestingUtility.TestProcedure c = new ProcedureTestingUtility.TestProcedure(3, 1);
        // INIT
        procStore.insert(a, null);
        // Run A first step
        a.addStackId(0);
        procStore.update(a);
        // Run A second step
        a.addStackId(1);
        procStore.insert(a, new Procedure[]{ b, c });
        // Run B first step
        b.addStackId(2);
        procStore.update(b);
        // Run C first and last step
        c.addStackId(3);
        procStore.update(c);
        // Run B second setp
        b.addStackId(4);
        procStore.update(b);
        // back to A
        a.addStackId(5);
        a.setSuccessState();
        procStore.delete(a, new long[]{ getProcId(), getProcId() });
        restartAndAssert(3, 0, 1, 0);
    }

    @Test
    public void testBatchDelete() throws Exception {
        for (int i = 1; i < 10; ++i) {
            procStore.insert(new ProcedureTestingUtility.TestProcedure(i), null);
        }
        // delete nothing
        long[] toDelete = new long[]{ 1, 2, 3, 4 };
        procStore.delete(toDelete, 2, 0);
        ProcedureTestingUtility.LoadCounter loader = restartAndAssert(9, 9, 0, 0);
        for (int i = 1; i < 10; ++i) {
            Assert.assertEquals(true, loader.isRunnable(i));
        }
        // delete the full "toDelete" array (2, 4, 6, 8)
        toDelete = new long[]{ 2, 4, 6, 8 };
        procStore.delete(toDelete, 0, toDelete.length);
        loader = restartAndAssert(9, 5, 0, 0);
        for (int i = 1; i < 10; ++i) {
            Assert.assertEquals(((i % 2) != 0), loader.isRunnable(i));
        }
        // delete a slice of "toDelete" (1, 3)
        toDelete = new long[]{ 5, 7, 1, 3, 9 };
        procStore.delete(toDelete, 2, 2);
        loader = restartAndAssert(9, 3, 0, 0);
        for (int i = 1; i < 10; ++i) {
            Assert.assertEquals(((i > 3) && ((i % 2) != 0)), loader.isRunnable(i));
        }
        // delete a single item (5)
        toDelete = new long[]{ 5 };
        procStore.delete(toDelete, 0, 1);
        loader = restartAndAssert(9, 2, 0, 0);
        for (int i = 1; i < 10; ++i) {
            Assert.assertEquals(((i > 5) && ((i % 2) != 0)), loader.isRunnable(i));
        }
        // delete remaining using a slice of "toDelete" (7, 9)
        toDelete = new long[]{ 0, 7, 9 };
        procStore.delete(toDelete, 1, 2);
        loader = restartAndAssert(0, 0, 0, 0);
        for (int i = 1; i < 10; ++i) {
            Assert.assertEquals(false, loader.isRunnable(i));
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        final int count = 10;
        final ProcedureTestingUtility.TestProcedure[] procs = new ProcedureTestingUtility.TestProcedure[count];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new ProcedureTestingUtility.TestProcedure((i + 1));
        }
        procStore.insert(procs);
        restartAndAssert(count, count, 0, 0);
        for (int i = 0; i < (procs.length); ++i) {
            final long procId = getProcId();
            procStore.delete(procId);
            restartAndAssert((procId != count ? count : 0), (count - (i + 1)), 0, 0);
        }
        procStore.removeInactiveLogsForTesting();
        Assert.assertEquals(("WALs=" + (procStore.getActiveLogs())), 1, procStore.getActiveLogs().size());
    }

    @Test
    public void testWALDirAndWALArchiveDir() throws IOException {
        Configuration conf = htu.getConfiguration();
        procStore = createWALProcedureStore(conf);
        Assert.assertEquals(procStore.getFileSystem(), procStore.getWalArchiveDir().getFileSystem(conf));
    }

    public static class TestSequentialProcedure extends SequentialProcedure<Void> {
        private static long seqid = 0;

        public TestSequentialProcedure() {
            setProcId((++(TestWALProcedureStore.TestSequentialProcedure.seqid)));
        }

        @Override
        protected Procedure<Void>[] execute(Void env) {
            return null;
        }

        @Override
        protected void rollback(Void env) {
        }

        @Override
        protected boolean abort(Void env) {
            return false;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
            long procId = getProcId();
            if ((procId % 2) == 0) {
                Int64Value.Builder builder = Int64Value.newBuilder().setValue(procId);
                serializer.serialize(builder.build());
            }
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
            long procId = getProcId();
            if ((procId % 2) == 0) {
                Int64Value value = serializer.deserialize(Int64Value.class);
                Assert.assertEquals(procId, value.getValue());
            }
        }
    }
}

