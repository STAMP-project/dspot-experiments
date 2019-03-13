/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel.file;


import FileChannelConfiguration.DEFAULT_MAX_FILE_SIZE;
import LogFile.CachedFSUsableSpace;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.flume.channel.file.instrumentation.FileChannelCounter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Log.PREFIX;


public class TestLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestLog.class);

    private static final long MAX_FILE_SIZE = 1000;

    private static final int CAPACITY = 10000;

    private Log log;

    private File checkpointDir;

    private File[] dataDirs;

    private long transactionID;

    /**
     * Test that we can put, commit and then get. Note that get is
     * not transactional so the commit is not required.
     */
    @Test
    public void testPutGet() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long transactionID = ++(this.transactionID);
        FlumeEventPointer eventPointer = log.put(transactionID, eventIn);
        log.commitPut(transactionID);// this is not required since

        // get is not transactional
        FlumeEvent eventOut = log.get(eventPointer);
        Assert.assertNotNull(eventOut);
        Assert.assertEquals(eventIn.getHeaders(), eventOut.getHeaders());
        Assert.assertArrayEquals(eventIn.getBody(), eventOut.getBody());
    }

    @Test
    public void testRoll() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        log.shutdownWorker();
        Thread.sleep(1000);
        for (int i = 0; i < 1000; i++) {
            FlumeEvent eventIn = TestUtils.newPersistableEvent();
            long transactionID = ++(this.transactionID);
            FlumeEventPointer eventPointer = log.put(transactionID, eventIn);
            // get is not transactional
            FlumeEvent eventOut = log.get(eventPointer);
            Assert.assertNotNull(eventOut);
            Assert.assertEquals(eventIn.getHeaders(), eventOut.getHeaders());
            Assert.assertArrayEquals(eventIn.getBody(), eventOut.getBody());
        }
        int logCount = 0;
        for (File dataDir : dataDirs) {
            for (File logFile : dataDir.listFiles()) {
                if (logFile.getName().startsWith("log-")) {
                    logCount++;
                }
            }
        }
        // 93 (*2 for meta) files with TestLog.MAX_FILE_SIZE=1000
        Assert.assertEquals(186, logCount);
    }

    /**
     * After replay of the log, we should find the event because the put
     * was committed
     */
    @Test
    public void testPutCommit() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long transactionID = ++(this.transactionID);
        FlumeEventPointer eventPointerIn = log.put(transactionID, eventIn);
        log.commitPut(transactionID);
        log.close();
        log = new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        takeAndVerify(eventPointerIn, eventIn);
    }

    /**
     * After replay of the log, we should not find the event because the
     * put was rolled back
     */
    @Test
    public void testPutRollback() throws IOException, InterruptedException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long transactionID = ++(this.transactionID);
        log.put(transactionID, eventIn);
        log.rollback(transactionID);// rolled back so it should not be replayed

        log.close();
        log = new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        FlumeEventQueue queue = log.getFlumeEventQueue();
        Assert.assertNull(queue.removeHead(transactionID));
    }

    @Test
    public void testMinimumRequiredSpaceTooSmallOnStartup() throws IOException, InterruptedException {
        log.close();
        log = new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setMinimumRequiredSpace(Long.MAX_VALUE).setChannelCounter(new FileChannelCounter("testlog")).build();
        try {
            log.replay();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().startsWith("Usable space exhausted"));
        }
    }

    /**
     * There is a race here in that someone could take up some space
     */
    @Test
    public void testMinimumRequiredSpaceTooSmallForPut() throws IOException, InterruptedException {
        try {
            doTestMinimumRequiredSpaceTooSmallForPut();
        } catch (IOException e) {
            TestLog.LOGGER.info("Error during test, retrying", e);
            doTestMinimumRequiredSpaceTooSmallForPut();
        } catch (AssertionError e) {
            TestLog.LOGGER.info("Test failed, let's be sure it failed for good reason", e);
            doTestMinimumRequiredSpaceTooSmallForPut();
        }
    }

    /**
     * After replay of the log, we should not find the event because the take
     * was committed
     */
    @Test
    public void testPutTakeCommit() throws IOException, InterruptedException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long putTransactionID = ++(transactionID);
        FlumeEventPointer eventPointer = log.put(putTransactionID, eventIn);
        log.commitPut(putTransactionID);
        long takeTransactionID = ++(transactionID);
        log.take(takeTransactionID, eventPointer);
        log.commitTake(takeTransactionID);
        log.close();
        new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(1).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        FlumeEventQueue queue = log.getFlumeEventQueue();
        Assert.assertNull(queue.removeHead(0));
    }

    /**
     * After replay of the log, we should get the event because the take
     * was rolled back
     */
    @Test
    public void testPutTakeRollbackLogReplayV1() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        doPutTakeRollback(true);
    }

    @Test
    public void testPutTakeRollbackLogReplayV2() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        doPutTakeRollback(false);
    }

    @Test
    public void testCommitNoPut() throws IOException, InterruptedException {
        long putTransactionID = ++(transactionID);
        log.commitPut(putTransactionID);
        log.close();
        new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(1).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        FlumeEventQueue queue = log.getFlumeEventQueue();
        FlumeEventPointer eventPointerOut = queue.removeHead(0);
        Assert.assertNull(eventPointerOut);
    }

    @Test
    public void testCommitNoTake() throws IOException, InterruptedException {
        long putTransactionID = ++(transactionID);
        log.commitTake(putTransactionID);
        log.close();
        new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(1).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        FlumeEventQueue queue = log.getFlumeEventQueue();
        FlumeEventPointer eventPointerOut = queue.removeHead(0);
        Assert.assertNull(eventPointerOut);
    }

    @Test
    public void testRollbackNoPutTake() throws IOException, InterruptedException {
        long putTransactionID = ++(transactionID);
        log.rollback(putTransactionID);
        log.close();
        new Log.Builder().setCheckpointInterval(Long.MAX_VALUE).setMaxFileSize(DEFAULT_MAX_FILE_SIZE).setQueueSize(1).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        FlumeEventQueue queue = log.getFlumeEventQueue();
        FlumeEventPointer eventPointerOut = queue.removeHead(0);
        Assert.assertNull(eventPointerOut);
    }

    @Test
    public void testGetLogs() throws IOException {
        File logDir = dataDirs[0];
        List<File> expected = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            File log = new File(logDir, ((PREFIX) + i));
            expected.add(log);
            Assert.assertTrue(((log.isFile()) || (log.createNewFile())));
            File metaDataFile = Serialization.getMetaDataFile(log);
            File metaDataTempFile = Serialization.getMetaDataTempFile(metaDataFile);
            File logGzip = new File(logDir, (((PREFIX) + i) + ".gz"));
            Assert.assertTrue(((metaDataFile.isFile()) || (metaDataFile.createNewFile())));
            Assert.assertTrue(((metaDataTempFile.isFile()) || (metaDataTempFile.createNewFile())));
            Assert.assertTrue(((log.isFile()) || (logGzip.createNewFile())));
        }
        List<File> actual = LogUtils.getLogs(logDir);
        LogUtils.sort(actual);
        LogUtils.sort(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplayFailsWithAllEmptyLogMetaDataNormalReplay() throws IOException, InterruptedException {
        doTestReplayFailsWithAllEmptyLogMetaData(false);
    }

    @Test
    public void testReplayFailsWithAllEmptyLogMetaDataFastReplay() throws IOException, InterruptedException {
        doTestReplayFailsWithAllEmptyLogMetaData(true);
    }

    @Test
    public void testReplaySucceedsWithUnusedEmptyLogMetaDataNormalReplay() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long transactionID = ++(this.transactionID);
        FlumeEventPointer eventPointer = log.put(transactionID, eventIn);
        log.commitPut(transactionID);// this is not required since

        log.close();
        log = new Log.Builder().setCheckpointInterval(1L).setMaxFileSize(TestLog.MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setChannelCounter(new FileChannelCounter("testlog")).build();
        doTestReplaySucceedsWithUnusedEmptyLogMetaData(eventIn, eventPointer);
    }

    @Test
    public void testReplaySucceedsWithUnusedEmptyLogMetaDataFastReplay() throws IOException, InterruptedException, CorruptEventException, NoopRecordException {
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        long transactionID = ++(this.transactionID);
        FlumeEventPointer eventPointer = log.put(transactionID, eventIn);
        log.commitPut(transactionID);// this is not required since

        log.close();
        checkpointDir = Files.createTempDir();
        FileUtils.forceDeleteOnExit(checkpointDir);
        Assert.assertTrue(checkpointDir.isDirectory());
        log = new Log.Builder().setCheckpointInterval(1L).setMaxFileSize(TestLog.MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setChannelName("testlog").setUseFastReplay(true).setChannelCounter(new FileChannelCounter("testlog")).build();
        doTestReplaySucceedsWithUnusedEmptyLogMetaData(eventIn, eventPointer);
    }

    @Test
    public void testCachedFSUsableSpace() throws Exception {
        File fs = Mockito.mock(File.class);
        Mockito.when(fs.getUsableSpace()).thenReturn(Long.MAX_VALUE);
        LogFile.CachedFSUsableSpace cachedFS = new LogFile.CachedFSUsableSpace(fs, 1000L);
        Assert.assertEquals(cachedFS.getUsableSpace(), Long.MAX_VALUE);
        cachedFS.decrement(Integer.MAX_VALUE);
        Assert.assertEquals(cachedFS.getUsableSpace(), ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        try {
            cachedFS.decrement((-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Mockito.when(fs.getUsableSpace()).thenReturn(((Long.MAX_VALUE) - 1L));
        Thread.sleep(1100);
        Assert.assertEquals(cachedFS.getUsableSpace(), ((Long.MAX_VALUE) - 1L));
    }

    @Test
    public void testCheckpointOnClose() throws Exception {
        log.close();
        log = new Log.Builder().setCheckpointInterval(1L).setMaxFileSize(TestLog.MAX_FILE_SIZE).setQueueSize(TestLog.CAPACITY).setCheckpointDir(checkpointDir).setLogDirs(dataDirs).setCheckpointOnClose(true).setChannelName("testLog").setChannelCounter(new FileChannelCounter("testlog")).build();
        log.replay();
        // 1 Write One Event
        FlumeEvent eventIn = TestUtils.newPersistableEvent();
        log.put(transactionID, eventIn);
        log.commitPut(transactionID);
        // 2 Check state of checkpoint before close
        File checkPointMetaFile = FileUtils.listFiles(checkpointDir, new String[]{ "meta" }, false).iterator().next();
        long before = FileUtils.checksumCRC32(checkPointMetaFile);
        // 3 Close Log
        log.close();
        // 4 Verify that checkpoint was modified on close
        long after = FileUtils.checksumCRC32(checkPointMetaFile);
        Assert.assertFalse((before == after));
    }
}

