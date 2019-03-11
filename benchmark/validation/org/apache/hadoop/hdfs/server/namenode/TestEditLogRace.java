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
package org.apache.hadoop.hdfs.server.namenode;


import FSEditLog.LOG;
import FSEditLogOp.OpInstanceCache;
import FSEditLogOp.SetOwnerOp;
import NamenodeRole.NAMENODE;
import SafeModeAction.SAFEMODE_ENTER;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.JournalSet.JournalAndStream;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This class tests various synchronization bugs in FSEditLog rolling
 * and namespace saving.
 */
@RunWith(Parameterized.class)
public class TestEditLogRace {
    static {
        GenericTestUtils.setLogLevel(FSEditLog.LOG, Level.DEBUG);
    }

    private static boolean useAsyncEditLog;

    public TestEditLogRace(boolean useAsyncEditLog) {
        TestEditLogRace.useAsyncEditLog = useAsyncEditLog;
    }

    private static final String NAME_DIR = (MiniDFSCluster.getBaseDirectory()) + "name-0-1";

    private static final Logger LOG = LoggerFactory.getLogger(TestEditLogRace.class);

    // This test creates NUM_THREADS threads and each thread continuously writes
    // transactions
    static final int NUM_THREADS = 16;

    /**
     * The number of times to roll the edit log during the test. Since this
     * tests for a race condition, higher numbers are more likely to find
     * a bug if it exists, but the test will take longer.
     */
    static final int NUM_ROLLS = 30;

    /**
     * The number of times to save the fsimage and create an empty edit log.
     */
    static final int NUM_SAVE_IMAGE = 30;

    private final List<TestEditLogRace.Transactions> workers = new ArrayList<TestEditLogRace.Transactions>();

    private static final int NUM_DATA_NODES = 1;

    /**
     * Several of the test cases work by introducing a sleep
     * into an operation that is usually fast, and then verifying
     * that another operation blocks for at least this amount of time.
     * This value needs to be significantly longer than the average
     * time for an fsync() or enterSafeMode().
     */
    private static final int BLOCK_TIME = 4;// 4 sec pretty generous


    // 
    // an object that does a bunch of transactions
    // 
    static class Transactions implements Runnable {
        final NamenodeProtocols nn;

        final MiniDFSCluster cluster;

        FileSystem fs;

        short replication = 3;

        long blockSize = 64;

        volatile boolean stopped = false;

        volatile Thread thr;

        final AtomicReference<Throwable> caught;

        Transactions(MiniDFSCluster cluster, AtomicReference<Throwable> caught) {
            this.cluster = cluster;
            this.nn = cluster.getNameNodeRpc();
            try {
                this.fs = cluster.getFileSystem();
            } catch (IOException e) {
                caught.set(e);
            }
            this.caught = caught;
        }

        // add a bunch of transactions.
        @Override
        public void run() {
            thr = Thread.currentThread();
            FsPermission p = new FsPermission(((short) (511)));
            int i = 0;
            while (!(stopped)) {
                try {
                    String dirname = (("/thr-" + (thr.getId())) + "-dir-") + i;
                    if ((i % 2) == 0) {
                        Path dirnamePath = new Path(dirname);
                        fs.mkdirs(dirnamePath);
                        fs.delete(dirnamePath, true);
                    } else {
                        nn.mkdirs(dirname, p, true);
                        nn.delete(dirname, true);
                    }
                } catch (SafeModeException sme) {
                    // This is OK - the tests will bring NN in and out of safemode
                } catch (Throwable e) {
                    // This is OK - the tests will bring NN in and out of safemode
                    if ((e instanceof RemoteException) && (getClassName().contains("SafeModeException"))) {
                        return;
                    }
                    TestEditLogRace.LOG.warn("Got error in transaction thread", e);
                    caught.compareAndSet(null, e);
                    break;
                }
                i++;
            } 
        }

        public void stop() {
            stopped = true;
        }

        public Thread getThread() {
            return thr;
        }
    }

    /**
     * Tests rolling edit logs while transactions are ongoing.
     */
    @Test
    public void testEditLogRolling() throws Exception {
        // start a cluster
        Configuration conf = getConf();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLogRace.NUM_DATA_NODES).build();
        FileSystem fileSys = null;
        AtomicReference<Throwable> caughtErr = new AtomicReference<Throwable>();
        try {
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final NamenodeProtocols nn = cluster.getNameNode().getRpcServer();
            FSImage fsimage = cluster.getNamesystem().getFSImage();
            StorageDirectory sd = fsimage.getStorage().getStorageDir(0);
            startTransactionWorkers(cluster, caughtErr);
            long previousLogTxId = 1;
            for (int i = 0; (i < (TestEditLogRace.NUM_ROLLS)) && ((caughtErr.get()) == null); i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
                TestEditLogRace.LOG.info((("Starting roll " + i) + "."));
                CheckpointSignature sig = nn.rollEditLog();
                long nextLog = sig.curSegmentTxId;
                String logFileName = NNStorage.getFinalizedEditsFileName(previousLogTxId, (nextLog - 1));
                previousLogTxId += verifyEditLogs(cluster.getNamesystem(), fsimage, logFileName, previousLogTxId);
                Assert.assertEquals(previousLogTxId, nextLog);
                File expectedLog = NNStorage.getInProgressEditsFile(sd, previousLogTxId);
                Assert.assertTrue((("Expect " + expectedLog) + " to exist"), expectedLog.exists());
            }
        } finally {
            stopTransactionWorkers();
            if ((caughtErr.get()) != null) {
                throw new RuntimeException(caughtErr.get());
            }
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    /**
     * Tests saving fs image while transactions are ongoing.
     */
    @Test
    public void testSaveNamespace() throws Exception {
        // start a cluster
        Configuration conf = getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        AtomicReference<Throwable> caughtErr = new AtomicReference<Throwable>();
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLogRace.NUM_DATA_NODES).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final FSNamesystem namesystem = cluster.getNamesystem();
            FSImage fsimage = namesystem.getFSImage();
            FSEditLog editLog = fsimage.getEditLog();
            startTransactionWorkers(cluster, caughtErr);
            for (int i = 0; (i < (TestEditLogRace.NUM_SAVE_IMAGE)) && ((caughtErr.get()) == null); i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }
                TestEditLogRace.LOG.info((("Save " + i) + ": entering safe mode"));
                namesystem.enterSafeMode(false);
                // Verify edit logs before the save
                // They should start with the first edit after the checkpoint
                long logStartTxId = (fsimage.getStorage().getMostRecentCheckpointTxId()) + 1;
                verifyEditLogs(namesystem, fsimage, NNStorage.getInProgressEditsFileName(logStartTxId), logStartTxId);
                TestEditLogRace.LOG.info((("Save " + i) + ": saving namespace"));
                namesystem.saveNamespace(0, 0);
                TestEditLogRace.LOG.info((("Save " + i) + ": leaving safemode"));
                long savedImageTxId = fsimage.getStorage().getMostRecentCheckpointTxId();
                // Verify that edit logs post save got finalized and aren't corrupt
                verifyEditLogs(namesystem, fsimage, NNStorage.getFinalizedEditsFileName(logStartTxId, savedImageTxId), logStartTxId);
                // The checkpoint id should be 1 less than the last written ID, since
                // the log roll writes the "BEGIN" transaction to the new log.
                Assert.assertEquals(fsimage.getStorage().getMostRecentCheckpointTxId(), ((editLog.getLastWrittenTxId()) - 1));
                namesystem.leaveSafeMode(false);
                TestEditLogRace.LOG.info((("Save " + i) + ": complete"));
            }
        } finally {
            stopTransactionWorkers();
            if ((caughtErr.get()) != null) {
                throw new RuntimeException(caughtErr.get());
            }
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    /**
     * The logSync() method in FSEditLog is unsynchronized whiel syncing
     * so that other threads can concurrently enqueue edits while the prior
     * sync is ongoing. This test checks that the log is saved correctly
     * if the saveImage occurs while the syncing thread is in the unsynchronized middle section.
     *
     * This replicates the following manual test proposed by Konstantin:
     *   I start the name-node in debugger.
     *   I do -mkdir and stop the debugger in logSync() just before it does flush.
     *   Then I enter safe mode with another client
     *   I start saveNamepsace and stop the debugger in
     *     FSImage.saveFSImage() -> FSEditLog.createEditLogFile()
     *     -> EditLogFileOutputStream.create() ->
     *     after truncating the file but before writing LAYOUT_VERSION into it.
     *   Then I let logSync() run.
     *   Then I terminate the name-node.
     *   After that the name-node wont start, since the edits file is broken.
     */
    @Test
    public void testSaveImageWhileSyncInProgress() throws Exception {
        Configuration conf = getConf();
        NameNode.initMetrics(conf, NAMENODE);
        DFSTestUtil.formatNameNode(conf);
        final FSNamesystem namesystem = FSNamesystem.loadFromDisk(conf);
        try {
            FSImage fsimage = namesystem.getFSImage();
            FSEditLog editLog = fsimage.getEditLog();
            JournalAndStream jas = editLog.getJournals().get(0);
            EditLogFileOutputStream spyElos = Mockito.spy(((EditLogFileOutputStream) (jas.getCurrentStream())));
            jas.setCurrentStreamForTests(spyElos);
            final AtomicReference<Throwable> deferredException = new AtomicReference<Throwable>();
            final CountDownLatch waitToEnterFlush = new CountDownLatch(1);
            final Thread doAnEditThread = new Thread() {
                @Override
                public void run() {
                    try {
                        TestEditLogRace.LOG.info("Starting mkdirs");
                        namesystem.mkdirs("/test", new org.apache.hadoop.fs.permission.PermissionStatus("test", "test", new FsPermission(((short) (493)))), true);
                        TestEditLogRace.LOG.info("mkdirs complete");
                    } catch (Throwable ioe) {
                        TestEditLogRace.LOG.error("Got exception", ioe);
                        deferredException.set(ioe);
                        waitToEnterFlush.countDown();
                    }
                }
            };
            Answer<Void> blockingFlush = new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    TestEditLogRace.LOG.info("Flush called");
                    if ((TestEditLogRace.useAsyncEditLog) || ((Thread.currentThread()) == doAnEditThread)) {
                        TestEditLogRace.LOG.info("edit thread: Telling main thread we made it to flush section...");
                        // Signal to main thread that the edit thread is in the racy section
                        waitToEnterFlush.countDown();
                        TestEditLogRace.LOG.info((("edit thread: sleeping for " + (TestEditLogRace.BLOCK_TIME)) + "secs"));
                        Thread.sleep(((TestEditLogRace.BLOCK_TIME) * 1000));
                        TestEditLogRace.LOG.info("Going through to flush. This will allow the main thread to continue.");
                    }
                    invocation.callRealMethod();
                    TestEditLogRace.LOG.info("Flush complete");
                    return null;
                }
            };
            Mockito.doAnswer(blockingFlush).when(spyElos).flush();
            doAnEditThread.start();
            // Wait for the edit thread to get to the logsync unsynchronized section
            TestEditLogRace.LOG.info("Main thread: waiting to enter flush...");
            waitToEnterFlush.await();
            Assert.assertNull(deferredException.get());
            TestEditLogRace.LOG.info("Main thread: detected that logSync is in unsynchronized section.");
            TestEditLogRace.LOG.info("Trying to enter safe mode.");
            TestEditLogRace.LOG.info((("This should block for " + (TestEditLogRace.BLOCK_TIME)) + "sec, since flush will sleep that long"));
            long st = Time.now();
            namesystem.setSafeMode(SAFEMODE_ENTER);
            long et = Time.now();
            TestEditLogRace.LOG.info("Entered safe mode");
            // Make sure we really waited for the flush to complete!
            Assert.assertTrue(((et - st) > (((TestEditLogRace.BLOCK_TIME) - 1) * 1000)));
            // Once we're in safe mode, save namespace.
            namesystem.saveNamespace(0, 0);
            TestEditLogRace.LOG.info("Joining on edit thread...");
            doAnEditThread.join();
            Assert.assertNull(deferredException.get());
            // We did 3 edits: begin, txn, and end
            Assert.assertEquals(3, verifyEditLogs(namesystem, fsimage, NNStorage.getFinalizedEditsFileName(1, 3), 1));
            // after the save, just the one "begin"
            Assert.assertEquals(1, verifyEditLogs(namesystem, fsimage, NNStorage.getInProgressEditsFileName(4), 4));
        } finally {
            TestEditLogRace.LOG.info("Closing nn");
            if (namesystem != null)
                namesystem.close();

        }
    }

    /**
     * Most of the FSNamesystem methods have a synchronized section where they
     * update the name system itself and write to the edit log, and then
     * unsynchronized, they call logSync. This test verifies that, if an
     * operation has written to the edit log but not yet synced it,
     * we wait for that sync before entering safe mode.
     */
    @Test
    public void testSaveRightBeforeSync() throws Exception {
        Configuration conf = getConf();
        NameNode.initMetrics(conf, NAMENODE);
        DFSTestUtil.formatNameNode(conf);
        final FSNamesystem namesystem = FSNamesystem.loadFromDisk(conf);
        try {
            FSImage fsimage = namesystem.getFSImage();
            final FSEditLog editLog = fsimage.getEditLog();
            final AtomicReference<Throwable> deferredException = new AtomicReference<Throwable>();
            final CountDownLatch sleepingBeforeSync = new CountDownLatch(1);
            final Thread doAnEditThread = new Thread() {
                @Override
                public void run() {
                    try {
                        TestEditLogRace.LOG.info("Starting setOwner");
                        namesystem.writeLock();
                        try {
                            editLog.logSetOwner("/", "test", "test");
                        } finally {
                            namesystem.writeUnlock();
                        }
                        sleepingBeforeSync.countDown();
                        TestEditLogRace.LOG.info((("edit thread: sleeping for " + (TestEditLogRace.BLOCK_TIME)) + "secs"));
                        Thread.sleep(((TestEditLogRace.BLOCK_TIME) * 1000));
                        editLog.logSync();
                        TestEditLogRace.LOG.info("edit thread: logSync complete");
                    } catch (Throwable ioe) {
                        TestEditLogRace.LOG.error("Got exception", ioe);
                        deferredException.set(ioe);
                        sleepingBeforeSync.countDown();
                    }
                }
            };
            doAnEditThread.setDaemon(true);
            doAnEditThread.start();
            TestEditLogRace.LOG.info("Main thread: waiting to just before logSync...");
            sleepingBeforeSync.await(200, TimeUnit.MILLISECONDS);
            Assert.assertNull(deferredException.get());
            TestEditLogRace.LOG.info("Main thread: detected that logSync about to be called.");
            TestEditLogRace.LOG.info("Trying to enter safe mode.");
            long st = Time.now();
            namesystem.setSafeMode(SAFEMODE_ENTER);
            long et = Time.now();
            TestEditLogRace.LOG.info((("Entered safe mode after " + (et - st)) + "ms"));
            // Make sure we didn't wait for the thread that did a logEdit but
            // not logSync.  Going into safemode does a logSyncAll that will flush
            // its edit.
            Assert.assertTrue(((et - st) < (((TestEditLogRace.BLOCK_TIME) / 2) * 1000)));
            // Once we're in safe mode, save namespace.
            namesystem.saveNamespace(0, 0);
            TestEditLogRace.LOG.info("Joining on edit thread...");
            doAnEditThread.join();
            Assert.assertNull(deferredException.get());
            // We did 3 edits: begin, txn, and end
            Assert.assertEquals(3, verifyEditLogs(namesystem, fsimage, NNStorage.getFinalizedEditsFileName(1, 3), 1));
            // after the save, just the one "begin"
            Assert.assertEquals(1, verifyEditLogs(namesystem, fsimage, NNStorage.getInProgressEditsFileName(4), 4));
        } finally {
            TestEditLogRace.LOG.info("Closing nn");
            if (namesystem != null)
                namesystem.close();

        }
    }

    @Test(timeout = 180000)
    public void testDeadlock() throws Throwable {
        GenericTestUtils.setLogLevel(FSEditLog.LOG, Level.INFO);
        Configuration conf = getConf();
        NameNode.initMetrics(conf, NAMENODE);
        DFSTestUtil.formatNameNode(conf);
        final FSNamesystem namesystem = FSNamesystem.loadFromDisk(conf);
        final AtomicBoolean done = new AtomicBoolean(false);
        final Semaphore blockerSemaphore = new Semaphore(0);
        final CountDownLatch startSpamLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            final FSEditLog editLog = namesystem.getEditLog();
            FSEditLogOp.OpInstanceCache cache = editLog.cache.get();
            final FSEditLogOp op = SetOwnerOp.getInstance(cache).setSource("/").setUser("u").setGroup("g");
            // don't reset fields so instance can be reused.
            final FSEditLogOp reuseOp = Mockito.spy(op);
            Mockito.doNothing().when(reuseOp).reset();
            // only job is spam edits.  it will fill the queue when the test
            // loop injects the blockingOp.
            Future[] logSpammers = new Future[16];
            for (int i = 0; i < (logSpammers.length); i++) {
                final int ii = i;
                logSpammers[i] = executor.submit(new Callable() {
                    @Override
                    public Void call() throws Exception {
                        Thread.currentThread().setName(("Log spammer " + ii));
                        // wait until a blocking edit op notifies us to go.
                        startSpamLatch.await();
                        for (int i = 0; (!(done.get())) && (i < 1000000); i++) {
                            // do not logSync here because we need to congest the queue.
                            editLog.logEdit(reuseOp);
                            if ((i % 2048) == 0) {
                                TestEditLogRace.LOG.info(((("thread[" + ii) + "] edits=") + i));
                            }
                        }
                        Assert.assertTrue("too many edits", done.get());
                        return null;
                    }
                });
            }
            // the tx id is set while the edit log monitor is held, so this will
            // effectively stall the async processing thread which will cause the
            // edit queue to fill up.
            final FSEditLogOp blockingOp = Mockito.spy(op);
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    // flip the latch to unleash the spamming threads to congest
                    // the queue.
                    startSpamLatch.countDown();
                    // wait until unblocked after a synchronized thread is started.
                    blockerSemaphore.acquire();
                    invocation.callRealMethod();
                    return null;
                }
            }).when(blockingOp).setTransactionId(Mockito.anyLong());
            // don't reset fields so instance can be reused.
            Mockito.doNothing().when(blockingOp).reset();
            // repeatedly overflow the queue and verify it doesn't deadlock.
            for (int i = 0; i < 8; i++) {
                // when the blockingOp is logged, it triggers the latch to unleash the
                // spammers to overflow the edit queue, then waits for a permit
                // from blockerSemaphore that will be released at the bottom of
                // this loop.
                Future blockingEdit = executor.submit(new Callable() {
                    @Override
                    public Void call() throws Exception {
                        Thread.currentThread().setName("Log blocker");
                        editLog.logEdit(blockingOp);
                        editLog.logSync();
                        return null;
                    }
                });
                // wait for spammers to seize up the edit log.
                long startTxId = editLog.getLastWrittenTxIdWithoutLock();
                final long[] txIds = new long[]{ startTxId, startTxId, startTxId };
                GenericTestUtils.waitFor(new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        txIds[0] = txIds[1];
                        txIds[1] = txIds[2];
                        txIds[2] = editLog.getLastWrittenTxIdWithoutLock();
                        return (((txIds[0]) == (txIds[1])) && ((txIds[1]) == (txIds[2]))) && ((txIds[2]) > startTxId);
                    }
                }, 100, 10000);
                // callers that synchronize on the edit log while the queue is full
                // are prone to deadlock if the locking is incorrect.  at this point:
                // 1. the blocking edit is holding the log's monitor.
                // 2. the spammers have filled the queue.
                // 3. the spammers are blocked waiting to queue another edit.
                // Now we'll start another thread to synchronize on the log (simulates
                // what log rolling does), unblock the op currently holding the
                // monitor, and ensure deadlock does not occur.
                CountDownLatch readyLatch = new CountDownLatch(1);
                Future synchedEdits = executor.submit(new Callable() {
                    @Override
                    public Void call() throws Exception {
                        Thread.currentThread().setName("Log synchronizer");
                        // the sync is CRUCIAL for this test.  it's what causes edit
                        // log rolling to deadlock when queue is full.
                        readyLatch.countDown();
                        synchronized(editLog) {
                            editLog.logEdit(reuseOp);
                            editLog.logSync();
                        }
                        return null;
                    }
                });
                // unblock the edit jammed in setting its txid.  queued edits should
                // start flowing and the synced edits should complete.
                readyLatch.await();
                blockerSemaphore.release();
                blockingEdit.get();
                synchedEdits.get();
            }
            // tell spammers to stop.
            done.set(true);
            for (int i = 0; i < (logSpammers.length); i++) {
                logSpammers[i].get();
            }
            // just make sure everything can be synced.
            editLog.logSyncAll();
        } finally {
            TestEditLogRace.LOG.info("Closing nn");
            executor.shutdownNow();
            if (namesystem != null) {
                namesystem.getFSImage().getStorage().close();
                namesystem.close();
            }
        }
    }
}

