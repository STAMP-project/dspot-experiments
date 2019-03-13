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


import DFSConfigKeys.DFS_NAMENODE_ACLS_ENABLED_KEY;
import DFSConfigKeys.DFS_NAMENODE_HANDLER_COUNT_KEY;
import FSEditLog.LOG;
import FSEditLogLoader.EditLogValidation;
import FSEditLogLoader.PositionTrackingInputStream;
import FSEditLogOp.Reader;
import FSEditLogOpCodes.OP_INVALID;
import FSEditLogOpCodes.OP_MKDIR;
import NameNodeDirType.EDITS;
import NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.metrics.NameNodeMetrics;
import org.apache.hadoop.hdfs.util.XMLUtils.InvalidXmlException;
import org.apache.hadoop.hdfs.util.XMLUtils.Stanza;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.ExitUtil.ExitException;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Time;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import static FSEditLogOpCodes.OP_INVALID;


/**
 * This class tests the creation and validation of a checkpoint.
 */
@RunWith(Parameterized.class)
public class TestEditLog {
    static {
        GenericTestUtils.setLogLevel(FSEditLog.LOG, Level.ALL);
    }

    private static boolean useAsyncEditLog;

    public TestEditLog(Boolean async) {
        TestEditLog.useAsyncEditLog = async;
    }

    /**
     * A garbage mkdir op which is used for testing
     * {@link EditLogFileInputStream#scanEditLog(File, long, boolean)}
     */
    public static class GarbageMkdirOp extends FSEditLogOp {
        public GarbageMkdirOp() {
            super(OP_MKDIR);
        }

        @Override
        void resetSubFields() {
            // nop
        }

        @Override
        void readFields(DataInputStream in, int logVersion) throws IOException {
            throw new IOException("cannot decode GarbageMkdirOp");
        }

        @Override
        public void writeFields(DataOutputStream out) throws IOException {
            // write in some garbage content
            Random random = new Random();
            byte[] content = new byte[(random.nextInt(16)) + 1];
            random.nextBytes(content);
            out.write(content);
        }

        @Override
        protected void toXml(ContentHandler contentHandler) throws SAXException {
            throw new UnsupportedOperationException("Not supported for GarbageMkdirOp");
        }

        @Override
        void fromXml(Stanza st) throws InvalidXmlException {
            throw new UnsupportedOperationException("Not supported for GarbageMkdirOp");
        }
    }

    static final Logger LOG = LoggerFactory.getLogger(TestEditLog.class);

    static final int NUM_DATA_NODES = 0;

    // This test creates NUM_THREADS threads and each thread does
    // 2 * NUM_TRANSACTIONS Transactions concurrently.
    static final int NUM_TRANSACTIONS = 100;

    static final int NUM_THREADS = 100;

    static final File TEST_DIR = PathUtils.getTestDir(TestEditLog.class);

    /**
     * An edits log with 3 edits from 0.20 - the result of
     * a fresh namesystem followed by hadoop fs -touchz /myfile
     */
    static final byte[] HADOOP20_SOME_EDITS = StringUtils.hexStringToByte(("ffff ffed 0a00 0000 0000 03fa e100 0000" + (((((((((((("0005 0007 2f6d 7966 696c 6500 0133 000d" + "3132 3932 3331 3634 3034 3138 3400 0d31") + "3239 3233 3136 3430 3431 3834 0009 3133") + "3432 3137 3732 3800 0000 0004 746f 6464") + "0a73 7570 6572 6772 6f75 7001 a400 1544") + "4653 436c 6965 6e74 5f2d 3136 3136 3535") + "3738 3931 000b 3137 322e 3239 2e35 2e33") + "3209 0000 0005 0007 2f6d 7966 696c 6500") + "0133 000d 3132 3932 3331 3634 3034 3138") + "3400 0d31 3239 3233 3136 3430 3431 3834") + "0009 3133 3432 3137 3732 3800 0000 0004") + "746f 6464 0a73 7570 6572 6772 6f75 7001") + "a4ff 0000 0000 0000 0000 0000 0000 0000")).replace(" ", ""));

    static {
        // No need to fsync for the purposes of tests. This makes
        // the tests run much faster.
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    static final byte TRAILER_BYTE = OP_INVALID.getOpCode();

    private static final int CHECKPOINT_ON_STARTUP_MIN_TXNS = 100;

    // 
    // an object that does a bunch of transactions
    // 
    static class Transactions implements Runnable {
        final FSNamesystem namesystem;

        final int numTransactions;

        final short replication = 3;

        final long blockSize = 64;

        final int startIndex;

        Transactions(FSNamesystem ns, int numTx, int startIdx) {
            namesystem = ns;
            numTransactions = numTx;
            startIndex = startIdx;
        }

        // add a bunch of transactions.
        @Override
        public void run() {
            PermissionStatus p = namesystem.createFsOwnerPermissions(new FsPermission(((short) (511))));
            FSEditLog editLog = namesystem.getEditLog();
            for (int i = 0; i < (numTransactions); i++) {
                INodeFile inode = new INodeFile(namesystem.dir.allocateNewInodeId(), null, p, 0L, 0L, BlockInfo.EMPTY_ARRAY, replication, blockSize);
                inode.toUnderConstruction("", "");
                editLog.logOpenFile(("/filename" + ((startIndex) + i)), inode, false, false);
                editLog.logCloseFile(("/filename" + ((startIndex) + i)), inode);
                editLog.logSync();
            }
        }
    }

    /**
     * Test case for an empty edit log from a prior version of Hadoop.
     */
    @Test
    public void testPreTxIdEditLogNoEdits() throws Exception {
        FSNamesystem namesys = Mockito.mock(FSNamesystem.class);
        namesys.dir = Mockito.mock(FSDirectory.class);
        long numEdits = // just version number
        testLoad(StringUtils.hexStringToByte("ffffffed"), namesys);
        Assert.assertEquals(0, numEdits);
    }

    /**
     * Test case for loading a very simple edit log from a format
     * prior to the inclusion of edit transaction IDs in the log.
     */
    @Test
    public void testPreTxidEditLogWithEdits() throws Exception {
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            final FSNamesystem namesystem = cluster.getNamesystem();
            long numEdits = testLoad(TestEditLog.HADOOP20_SOME_EDITS, namesystem);
            Assert.assertEquals(3, numEdits);
            // Sanity check the edit
            HdfsFileStatus fileInfo = namesystem.getFileInfo("/myfile", false, false, false);
            Assert.assertEquals("supergroup", fileInfo.getGroup());
            Assert.assertEquals(3, fileInfo.getReplication());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Simple test for writing to and rolling the edit log.
     */
    @Test
    public void testSimpleEditLog() throws IOException {
        // start a cluster
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final FSNamesystem namesystem = cluster.getNamesystem();
            FSImage fsimage = namesystem.getFSImage();
            final FSEditLog editLog = fsimage.getEditLog();
            assertExistsInStorageDirs(cluster, EDITS, NNStorage.getInProgressEditsFileName(1));
            editLog.logSetReplication("fakefile", ((short) (1)));
            editLog.logSync();
            editLog.rollEditLog(CURRENT_LAYOUT_VERSION);
            assertExistsInStorageDirs(cluster, EDITS, NNStorage.getFinalizedEditsFileName(1, 3));
            assertExistsInStorageDirs(cluster, EDITS, NNStorage.getInProgressEditsFileName(4));
            editLog.logSetReplication("fakefile", ((short) (2)));
            editLog.logSync();
            editLog.close();
        } finally {
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    /**
     * Tests transaction logging in dfs.
     */
    @Test
    public void testMultiThreadedEditLog() throws IOException {
        testEditLog(2048);
        // force edit buffer to automatically sync on each log of edit log entry
        testEditLog(1);
    }

    @Test
    public void testSyncBatching() throws Exception {
        if (TestEditLog.useAsyncEditLog) {
            // semantics are completely differently since edits will be auto-synced
            return;
        }
        // start a cluster
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        ExecutorService threadA = Executors.newSingleThreadExecutor();
        ExecutorService threadB = Executors.newSingleThreadExecutor();
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final FSNamesystem namesystem = cluster.getNamesystem();
            FSImage fsimage = namesystem.getFSImage();
            final FSEditLog editLog = fsimage.getEditLog();
            Assert.assertEquals("should start with only the BEGIN_LOG_SEGMENT txn synced", 1, editLog.getSyncTxId());
            // Log an edit from thread A
            doLogEdit(threadA, editLog, "thread-a 1");
            Assert.assertEquals("logging edit without syncing should do not affect txid", 1, editLog.getSyncTxId());
            // Log an edit from thread B
            doLogEdit(threadB, editLog, "thread-b 1");
            Assert.assertEquals("logging edit without syncing should do not affect txid", 1, editLog.getSyncTxId());
            // Now ask to sync edit from B, which should sync both edits.
            doCallLogSync(threadB, editLog);
            Assert.assertEquals("logSync from second thread should bump txid up to 3", 3, editLog.getSyncTxId());
            // Now ask to sync edit from A, which was already batched in - thus
            // it should increment the batch count metric
            doCallLogSync(threadA, editLog);
            Assert.assertEquals("logSync from first thread shouldn't change txid", 3, editLog.getSyncTxId());
            // Should have incremented the batch count exactly once
            MetricsAsserts.assertCounter("TransactionsBatchedInSync", 1L, MetricsAsserts.getMetrics("NameNodeActivity"));
        } finally {
            threadA.shutdown();
            threadB.shutdown();
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    /**
     * Test what happens with the following sequence:
     *
     *  Thread A writes edit
     *  Thread B calls logSyncAll
     *           calls close() on stream
     *  Thread A calls logSync
     *
     * This sequence is legal and can occur if enterSafeMode() is closely
     * followed by saveNamespace.
     */
    @Test
    public void testBatchedSyncWithClosedLogs() throws Exception {
        // start a cluster
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        ExecutorService threadA = Executors.newSingleThreadExecutor();
        ExecutorService threadB = Executors.newSingleThreadExecutor();
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final FSNamesystem namesystem = cluster.getNamesystem();
            FSImage fsimage = namesystem.getFSImage();
            final FSEditLog editLog = fsimage.getEditLog();
            // Log an edit from thread A
            doLogEdit(threadA, editLog, "thread-a 1");
            // async log is doing batched syncs in background.  logSync just ensures
            // the edit is durable, so the txid may increase prior to sync
            if (!(TestEditLog.useAsyncEditLog)) {
                Assert.assertEquals("logging edit without syncing should do not affect txid", 1, editLog.getSyncTxId());
            }
            // logSyncAll in Thread B
            doCallLogSyncAll(threadB, editLog);
            Assert.assertEquals("logSyncAll should sync thread A's transaction", 2, editLog.getSyncTxId());
            // Close edit log
            editLog.close();
            // Ask thread A to finish sync (which should be a no-op)
            doCallLogSync(threadA, editLog);
        } finally {
            threadA.shutdown();
            threadB.shutdown();
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testEditChecksum() throws Exception {
        // start a cluster
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
        cluster.waitActive();
        fileSys = cluster.getFileSystem();
        final FSNamesystem namesystem = cluster.getNamesystem();
        FSImage fsimage = namesystem.getFSImage();
        final FSEditLog editLog = fsimage.getEditLog();
        fileSys.mkdirs(new Path("/tmp"));
        Iterator<StorageDirectory> iter = fsimage.getStorage().dirIterator(EDITS);
        LinkedList<StorageDirectory> sds = new LinkedList<StorageDirectory>();
        while (iter.hasNext()) {
            sds.add(iter.next());
        } 
        editLog.close();
        cluster.shutdown();
        for (StorageDirectory sd : sds) {
            File editFile = NNStorage.getFinalizedEditsFile(sd, 1, 3);
            Assert.assertTrue(editFile.exists());
            long fileLen = editFile.length();
            TestEditLog.LOG.debug(((("Corrupting Log File: " + editFile) + " len: ") + fileLen));
            RandomAccessFile rwf = new RandomAccessFile(editFile, "rw");
            rwf.seek((fileLen - 4));// seek to checksum bytes

            int b = rwf.readInt();
            rwf.seek((fileLen - 4));
            rwf.writeInt((b + 1));
            rwf.close();
        }
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).format(false).build();
            Assert.fail("should not be able to start");
        } catch (IOException e) {
            // expected
            Assert.assertNotNull("Cause of exception should be ChecksumException", e.getCause());
            Assert.assertEquals("Cause of exception should be ChecksumException", ChecksumException.class, e.getCause().getClass());
        }
    }

    /**
     * Test what happens if the NN crashes when it has has started but
     * had no transactions written.
     */
    @Test
    public void testCrashRecoveryNoTransactions() throws Exception {
        testCrashRecovery(0);
    }

    /**
     * Test what happens if the NN crashes when it has has started and
     * had a few transactions written
     */
    @Test
    public void testCrashRecoveryWithTransactions() throws Exception {
        testCrashRecovery(150);
    }

    // should succeed - only one corrupt log dir
    @Test
    public void testCrashRecoveryEmptyLogOneDir() throws Exception {
        doTestCrashRecoveryEmptyLog(false, true, true);
    }

    // should fail - seen_txid updated to 3, but no log dir contains txid 3
    @Test
    public void testCrashRecoveryEmptyLogBothDirs() throws Exception {
        doTestCrashRecoveryEmptyLog(true, true, false);
    }

    // should succeed - only one corrupt log dir
    @Test
    public void testCrashRecoveryEmptyLogOneDirNoUpdateSeenTxId() throws Exception {
        doTestCrashRecoveryEmptyLog(false, false, true);
    }

    // should succeed - both log dirs corrupt, but seen_txid never updated
    @Test
    public void testCrashRecoveryEmptyLogBothDirsNoUpdateSeenTxId() throws Exception {
        doTestCrashRecoveryEmptyLog(true, false, true);
    }

    private static class EditLogByteInputStream extends EditLogInputStream {
        private final InputStream input;

        private final long len;

        private int version;

        private Reader reader = null;

        private PositionTrackingInputStream tracker = null;

        public EditLogByteInputStream(byte[] data) throws IOException {
            len = data.length;
            input = new ByteArrayInputStream(data);
            BufferedInputStream bin = new BufferedInputStream(input);
            DataInputStream in = new DataInputStream(bin);
            version = EditLogFileInputStream.readLogVersion(in, true);
            tracker = new FSEditLogLoader.PositionTrackingInputStream(in);
            in = new DataInputStream(tracker);
            reader = Reader.create(in, tracker, version);
        }

        @Override
        public long getFirstTxId() {
            return HdfsServerConstants.INVALID_TXID;
        }

        @Override
        public long getLastTxId() {
            return HdfsServerConstants.INVALID_TXID;
        }

        @Override
        public long length() throws IOException {
            return len;
        }

        @Override
        public long getPosition() {
            return tracker.getPos();
        }

        @Override
        protected FSEditLogOp nextOp() throws IOException {
            return reader.readOp(false);
        }

        @Override
        public int getVersion(boolean verifyVersion) throws IOException {
            return version;
        }

        @Override
        public void close() throws IOException {
            input.close();
        }

        @Override
        public String getName() {
            return "AnonEditLogByteInputStream";
        }

        @Override
        public boolean isInProgress() {
            return true;
        }

        @Override
        public void setMaxOpSize(int maxOpSize) {
            reader.setMaxOpSize(maxOpSize);
        }

        @Override
        public boolean isLocalLog() {
            return true;
        }
    }

    @Test
    public void testFailedOpen() throws Exception {
        File logDir = new File(TestEditLog.TEST_DIR, "testFailedOpen");
        logDir.mkdirs();
        ExitUtil.disableSystemExit();
        FSEditLog log = FSImageTestUtil.createStandaloneEditLog(logDir);
        try {
            FileUtil.setWritable(logDir, false);
            log.openForWrite(CURRENT_LAYOUT_VERSION);
            Assert.fail("Did no throw exception on only having a bad dir");
        } catch (ExitException ee) {
            GenericTestUtils.assertExceptionContains("too few journals successfully started", ee);
        } finally {
            FileUtil.setWritable(logDir, true);
            log.close();
            ExitUtil.resetFirstExitException();
        }
    }

    /**
     * Regression test for HDFS-1112/HDFS-3020. Ensures that, even if
     * logSync isn't called periodically, the edit log will sync itself.
     */
    @Test
    public void testAutoSync() throws Exception {
        File logDir = new File(TestEditLog.TEST_DIR, "testAutoSync");
        logDir.mkdirs();
        FSEditLog log = FSImageTestUtil.createStandaloneEditLog(logDir);
        String oneKB = StringUtils.byteToHexString(new byte[500]);
        try {
            log.openForWrite(CURRENT_LAYOUT_VERSION);
            NameNodeMetrics mockMetrics = Mockito.mock(NameNodeMetrics.class);
            log.setMetricsForTests(mockMetrics);
            for (int i = 0; i < 400; i++) {
                log.logDelete(oneKB, 1L, false);
            }
            // After ~400KB, we're still within the 512KB buffer size
            Mockito.verify(mockMetrics, Mockito.times(0)).addSync(Mockito.anyLong());
            // After ~400KB more, we should have done an automatic sync
            for (int i = 0; i < 400; i++) {
                log.logDelete(oneKB, 1L, false);
            }
            Mockito.verify(mockMetrics, Mockito.times(1)).addSync(Mockito.anyLong());
        } finally {
            log.close();
        }
    }

    /**
     * Tests the getEditLogManifest function using mock storage for a number
     * of different situations.
     */
    @Test
    public void testEditLogManifestMocks() throws IOException {
        NNStorage storage;
        FSEditLog log;
        // Simple case - different directories have the same
        // set of logs, with an in-progress one at end
        storage = mockStorageWithEdits("[1,100]|[101,200]|[201,]", "[1,100]|[101,200]|[201,]");
        log = TestEditLog.getFSEditLog(storage);
        log.initJournalsForWrite();
        Assert.assertEquals("[[1,100], [101,200]] CommittedTxId: 200", log.getEditLogManifest(1).toString());
        Assert.assertEquals("[[101,200]] CommittedTxId: 200", log.getEditLogManifest(101).toString());
        // Another simple case, different directories have different
        // sets of files
        storage = mockStorageWithEdits("[1,100]|[101,200]", "[1,100]|[201,300]|[301,400]");// nothing starting at 101

        log = TestEditLog.getFSEditLog(storage);
        log.initJournalsForWrite();
        Assert.assertEquals(("[[1,100], [101,200], [201,300], [301,400]]" + " CommittedTxId: 400"), log.getEditLogManifest(1).toString());
        // Case where one directory has an earlier finalized log, followed
        // by a gap. The returned manifest should start after the gap.
        storage = // gap from 101 to 300
        mockStorageWithEdits("[1,100]|[301,400]", "[301,400]|[401,500]");
        log = TestEditLog.getFSEditLog(storage);
        log.initJournalsForWrite();
        Assert.assertEquals("[[301,400], [401,500]] CommittedTxId: 500", log.getEditLogManifest(1).toString());
        // Case where different directories have different length logs
        // starting at the same txid - should pick the longer one
        storage = // short log at 101
        mockStorageWithEdits("[1,100]|[101,150]", "[1,50]|[101,200]");// short log at 1

        log = TestEditLog.getFSEditLog(storage);
        log.initJournalsForWrite();
        Assert.assertEquals("[[1,100], [101,200]] CommittedTxId: 200", log.getEditLogManifest(1).toString());
        Assert.assertEquals("[[101,200]] CommittedTxId: 200", log.getEditLogManifest(101).toString());
        // Case where the first storage has an inprogress while
        // the second has finalised that file (i.e. the first failed
        // recently)
        storage = mockStorageWithEdits("[1,100]|[101,]", "[1,100]|[101,200]");
        log = TestEditLog.getFSEditLog(storage);
        log.initJournalsForWrite();
        Assert.assertEquals("[[1,100], [101,200]] CommittedTxId: 200", log.getEditLogManifest(1).toString());
        Assert.assertEquals("[[101,200]] CommittedTxId: 200", log.getEditLogManifest(101).toString());
    }

    /**
     * Specification for a failure during #setupEdits
     */
    static class AbortSpec {
        final int roll;

        final int logindex;

        /**
         * Construct the failure specification.
         *
         * @param roll
         * 		number to fail after. e.g. 1 to fail after the first roll
         * @param logindex
         * 		index of journal to fail.
         */
        AbortSpec(int roll, int logindex) {
            this.roll = roll;
            this.logindex = logindex;
        }
    }

    static final int TXNS_PER_ROLL = 10;

    static final int TXNS_PER_FAIL = 2;

    /**
     * Test loading an editlog which has had both its storage fail
     * on alternating rolls. Two edit log directories are created.
     * The first one fails on odd rolls, the second on even. Test
     * that we are able to load the entire editlog regardless.
     */
    @Test
    public void testAlternatingJournalFailure() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/alternatingjournaltest0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/alternatingjournaltest1"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 10, new TestEditLog.AbortSpec(1, 0), new TestEditLog.AbortSpec(2, 1), new TestEditLog.AbortSpec(3, 0), new TestEditLog.AbortSpec(4, 1), new TestEditLog.AbortSpec(5, 0), new TestEditLog.AbortSpec(6, 1), new TestEditLog.AbortSpec(7, 0), new TestEditLog.AbortSpec(8, 1), new TestEditLog.AbortSpec(9, 0), new TestEditLog.AbortSpec(10, 1));
        long totaltxnread = 0;
        FSEditLog editlog = TestEditLog.getFSEditLog(storage);
        editlog.initJournalsForWrite();
        long startTxId = 1;
        Iterable<EditLogInputStream> editStreams = editlog.selectInputStreams(startTxId, ((TestEditLog.TXNS_PER_ROLL) * 11));
        for (EditLogInputStream edits : editStreams) {
            FSEditLogLoader.EditLogValidation val = FSEditLogLoader.scanEditLog(edits, Long.MAX_VALUE);
            long read = ((val.getEndTxId()) - (edits.getFirstTxId())) + 1;
            TestEditLog.LOG.info(((("Loading edits " + edits) + " read ") + read));
            Assert.assertEquals(startTxId, edits.getFirstTxId());
            startTxId += read;
            totaltxnread += read;
        }
        editlog.close();
        storage.close();
        Assert.assertEquals(((TestEditLog.TXNS_PER_ROLL) * 11), totaltxnread);
    }

    /**
     * Test loading an editlog with gaps. A single editlog directory
     * is set up. On of the edit log files is deleted. This should
     * fail when selecting the input streams as it will not be able
     * to select enough streams to load up to 4*TXNS_PER_ROLL.
     * There should be 4*TXNS_PER_ROLL transactions as we rolled 3
     * times.
     */
    @Test
    public void testLoadingWithGaps() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/gaptest0"));
        List<URI> editUris = ImmutableList.of(f1.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 3);
        final long startGapTxId = (1 * (TestEditLog.TXNS_PER_ROLL)) + 1;
        final long endGapTxId = 2 * (TestEditLog.TXNS_PER_ROLL);
        File[] files = new File(f1, "current").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(NNStorage.getFinalizedEditsFileName(startGapTxId, endGapTxId))) {
                    return true;
                }
                return false;
            }
        });
        Assert.assertEquals(1, files.length);
        Assert.assertTrue(files[0].delete());
        FSEditLog editlog = TestEditLog.getFSEditLog(storage);
        editlog.initJournalsForWrite();
        long startTxId = 1;
        try {
            editlog.selectInputStreams(startTxId, (4 * (TestEditLog.TXNS_PER_ROLL)));
            Assert.fail("Should have thrown exception");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains(("Gap in transactions. Expected to be able to read up until " + ("at least txid 40 but unable to find any edit logs containing " + "txid 11")), ioe);
        }
    }

    static byte[][] invalidSequenecs = null;

    /**
     * "Fuzz" test for the edit log.
     *
     * This tests that we can read random garbage from the edit log without
     * crashing the JVM or throwing an unchecked exception.
     */
    @Test
    public void testFuzzSequences() throws IOException {
        final int MAX_GARBAGE_LENGTH = 512;
        final int MAX_INVALID_SEQ = 5000;
        // The seed to use for our random number generator.  When given the same
        // seed, Java.util.Random will always produce the same sequence of values.
        // This is important because it means that the test is deterministic and
        // repeatable on any machine.
        final int RANDOM_SEED = 123;
        Random r = new Random(RANDOM_SEED);
        for (int i = 0; i < MAX_INVALID_SEQ; i++) {
            byte[] garbage = new byte[r.nextInt(MAX_GARBAGE_LENGTH)];
            r.nextBytes(garbage);
            TestEditLog.validateNoCrash(garbage);
        }
    }

    /**
     * Test edit log failover.  If a single edit log is missing, other
     * edits logs should be used instead.
     */
    @Test
    public void testEditLogFailOverFromMissing() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/failover0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/failover1"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 3);
        final long startErrorTxId = (1 * (TestEditLog.TXNS_PER_ROLL)) + 1;
        final long endErrorTxId = 2 * (TestEditLog.TXNS_PER_ROLL);
        File[] files = new File(f1, "current").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(NNStorage.getFinalizedEditsFileName(startErrorTxId, endErrorTxId))) {
                    return true;
                }
                return false;
            }
        });
        Assert.assertEquals(1, files.length);
        Assert.assertTrue(files[0].delete());
        FSEditLog editlog = TestEditLog.getFSEditLog(storage);
        editlog.initJournalsForWrite();
        long startTxId = 1;
        Collection<EditLogInputStream> streams = null;
        try {
            streams = editlog.selectInputStreams(startTxId, (4 * (TestEditLog.TXNS_PER_ROLL)));
            TestEditLog.readAllEdits(streams, startTxId);
        } catch (IOException e) {
            TestEditLog.LOG.error("edit log failover didn't work", e);
            Assert.fail("Edit log failover didn't work");
        } finally {
            IOUtils.cleanup(null, streams.toArray(new EditLogInputStream[0]));
        }
    }

    /**
     * Test edit log failover from a corrupt edit log
     */
    @Test
    public void testEditLogFailOverFromCorrupt() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/failover0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/failover1"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 3);
        final long startErrorTxId = (1 * (TestEditLog.TXNS_PER_ROLL)) + 1;
        final long endErrorTxId = 2 * (TestEditLog.TXNS_PER_ROLL);
        File[] files = new File(f1, "current").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(NNStorage.getFinalizedEditsFileName(startErrorTxId, endErrorTxId))) {
                    return true;
                }
                return false;
            }
        });
        Assert.assertEquals(1, files.length);
        long fileLen = files[0].length();
        TestEditLog.LOG.debug(((("Corrupting Log File: " + (files[0])) + " len: ") + fileLen));
        RandomAccessFile rwf = new RandomAccessFile(files[0], "rw");
        rwf.seek((fileLen - 4));// seek to checksum bytes

        int b = rwf.readInt();
        rwf.seek((fileLen - 4));
        rwf.writeInt((b + 1));
        rwf.close();
        FSEditLog editlog = TestEditLog.getFSEditLog(storage);
        editlog.initJournalsForWrite();
        long startTxId = 1;
        Collection<EditLogInputStream> streams = null;
        try {
            streams = editlog.selectInputStreams(startTxId, (4 * (TestEditLog.TXNS_PER_ROLL)));
            TestEditLog.readAllEdits(streams, startTxId);
        } catch (IOException e) {
            TestEditLog.LOG.error("edit log failover didn't work", e);
            Assert.fail("Edit log failover didn't work");
        } finally {
            IOUtils.cleanup(null, streams.toArray(new EditLogInputStream[0]));
        }
    }

    /**
     * Test creating a directory with lots and lots of edit log segments
     */
    @Test
    public void testManyEditLogSegments() throws IOException {
        final int NUM_EDIT_LOG_ROLLS = 1000;
        // start a cluster
        Configuration conf = TestEditLog.getConf();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            final FSNamesystem namesystem = cluster.getNamesystem();
            FSImage fsimage = namesystem.getFSImage();
            final FSEditLog editLog = fsimage.getEditLog();
            for (int i = 0; i < NUM_EDIT_LOG_ROLLS; i++) {
                editLog.logSetReplication(("fakefile" + i), ((short) (i % 3)));
                assertExistsInStorageDirs(cluster, EDITS, NNStorage.getInProgressEditsFileName(((i * 3) + 1)));
                editLog.logSync();
                editLog.rollEditLog(CURRENT_LAYOUT_VERSION);
                assertExistsInStorageDirs(cluster, EDITS, NNStorage.getFinalizedEditsFileName(((i * 3) + 1), ((i * 3) + 3)));
            }
            editLog.close();
        } finally {
            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
        // How long does it take to read through all these edit logs?
        long startTime = Time.now();
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestEditLog.NUM_DATA_NODES).build();
            cluster.waitActive();
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
        long endTime = Time.now();
        double delta = ((float) (endTime - startTime)) / 1000.0;
        TestEditLog.LOG.info(String.format("loaded %d edit log segments in %.2f seconds", NUM_EDIT_LOG_ROLLS, delta));
    }

    /**
     * Edit log op instances are cached internally using thread-local storage.
     * This test checks that the cached instances are reset in between different
     * transactions processed on the same thread, so that we don't accidentally
     * apply incorrect attributes to an inode.
     *
     * @throws IOException
     * 		if there is an I/O error
     */
    @Test
    public void testResetThreadLocalCachedOps() throws IOException {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_NAMENODE_ACLS_ENABLED_KEY, true);
        // Set single handler thread, so all transactions hit same thread-local ops.
        conf.setInt(DFS_NAMENODE_HANDLER_COUNT_KEY, 1);
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            fileSys = cluster.getFileSystem();
            // Create /dir1 with a default ACL.
            Path dir1 = new Path("/dir1");
            fileSys.mkdirs(dir1);
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE));
            fileSys.modifyAclEntries(dir1, aclSpec);
            // /dir1/dir2 is expected to clone the default ACL.
            Path dir2 = new Path("/dir1/dir2");
            fileSys.mkdirs(dir2);
            // /dir1/file1 is expected to clone the default ACL.
            Path file1 = new Path("/dir1/file1");
            fileSys.create(file1).close();
            // /dir3 is not a child of /dir1, so must not clone the default ACL.
            Path dir3 = new Path("/dir3");
            fileSys.mkdirs(dir3);
            // /file2 is not a child of /dir1, so must not clone the default ACL.
            Path file2 = new Path("/file2");
            fileSys.create(file2).close();
            // Restart and assert the above stated expectations.
            IOUtils.cleanupWithLogger(TestEditLog.LOG, fileSys);
            cluster.restartNameNode();
            fileSys = cluster.getFileSystem();
            Assert.assertFalse(fileSys.getAclStatus(dir1).getEntries().isEmpty());
            Assert.assertFalse(fileSys.getAclStatus(dir2).getEntries().isEmpty());
            Assert.assertFalse(fileSys.getAclStatus(file1).getEntries().isEmpty());
            Assert.assertTrue(fileSys.getAclStatus(dir3).getEntries().isEmpty());
            Assert.assertTrue(fileSys.getAclStatus(file2).getEntries().isEmpty());
        } finally {
            IOUtils.cleanupWithLogger(TestEditLog.LOG, fileSys);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<>(log);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadActivelyUpdatedLog() throws Exception {
        final TestEditLog.TestAppender appender = new TestEditLog.TestAppender();
        LogManager.getRootLogger().addAppender(appender);
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_NAMENODE_ACLS_ENABLED_KEY, true);
        // Set single handler thread, so all transactions hit same thread-local ops.
        conf.setInt(DFS_NAMENODE_HANDLER_COUNT_KEY, 1);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FSImage fsimage = cluster.getNamesystem().getFSImage();
            StorageDirectory sd = fsimage.getStorage().getStorageDir(0);
            final DistributedFileSystem fileSys = cluster.getFileSystem();
            DFSInotifyEventInputStream events = fileSys.getInotifyEventStream();
            fileSys.mkdirs(new Path("/test"));
            fileSys.mkdirs(new Path("/test/dir1"));
            fileSys.delete(new Path("/test/dir1"), true);
            fsimage.getEditLog().logSync();
            fileSys.mkdirs(new Path("/test/dir2"));
            final File inProgressEdit = NNStorage.getInProgressEditsFile(sd, 1);
            Assert.assertTrue(inProgressEdit.exists());
            EditLogFileInputStream elis = new EditLogFileInputStream(inProgressEdit);
            FSEditLogOp op;
            long pos = 0;
            while (true) {
                op = elis.readOp();
                if ((op != null) && ((op.opCode) != (OP_INVALID))) {
                    pos = elis.getPosition();
                } else {
                    break;
                }
            } 
            elis.close();
            Assert.assertTrue((pos > 0));
            RandomAccessFile rwf = new RandomAccessFile(inProgressEdit, "rw");
            rwf.seek(pos);
            Assert.assertEquals(rwf.readByte(), ((byte) (-1)));
            rwf.seek((pos + 1));
            rwf.writeByte(2);
            rwf.close();
            events.poll();
            String pattern = "Caught exception after reading (.*) ops";
            Pattern r = Pattern.compile(pattern);
            final List<LoggingEvent> log = appender.getLog();
            for (LoggingEvent event : log) {
                Matcher m = r.matcher(event.getRenderedMessage());
                if (m.find()) {
                    Assert.fail("Should not try to read past latest syned edit log op");
                }
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            LogManager.getRootLogger().removeAppender(appender);
        }
    }
}

