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


import NameNodeDirType.EDITS;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.JournalManager.CorruptionException;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileJournalManager {
    static final Logger LOG = LoggerFactory.getLogger(TestFileJournalManager.class);

    private Configuration conf;

    static {
        // No need to fsync for the purposes of tests. This makes
        // the tests run much faster.
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test the normal operation of loading transactions from
     * file journal manager. 3 edits directories are setup without any
     * failures. Test that we read in the expected number of transactions.
     */
    @Test
    public void testNormalOperation() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/normtest0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/normtest1"));
        File f3 = new File(((TestEditLog.TEST_DIR) + "/normtest2"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI(), f3.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 5);
        long numJournals = 0;
        for (StorageDirectory sd : storage.dirIterable(EDITS)) {
            FileJournalManager jm = new FileJournalManager(conf, sd, storage);
            Assert.assertEquals((6 * (TestEditLog.TXNS_PER_ROLL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
            numJournals++;
        }
        Assert.assertEquals(3, numJournals);
    }

    /**
     * Test that inprogress files are handled correct. Set up a single
     * edits directory. Fail on after the last roll. Then verify that the
     * logs have the expected number of transactions.
     */
    @Test
    public void testInprogressRecovery() throws IOException {
        File f = new File(((TestEditLog.TEST_DIR) + "/inprogressrecovery"));
        // abort after the 5th roll
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 5, new TestEditLog.AbortSpec(5, 0));
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((5 * (TestEditLog.TXNS_PER_ROLL)) + (TestEditLog.TXNS_PER_FAIL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
    }

    /**
     * Test a mixture of inprogress files and finalised. Set up 3 edits
     * directories and fail the second on the last roll. Verify that reading
     * the transactions, reads from the finalised directories.
     */
    @Test
    public void testInprogressRecoveryMixed() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/mixtest0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/mixtest1"));
        File f3 = new File(((TestEditLog.TEST_DIR) + "/mixtest2"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI(), f3.toURI());
        // abort after the 5th roll
        NNStorage storage = TestEditLog.setupEdits(editUris, 5, new TestEditLog.AbortSpec(5, 1));
        Iterator<StorageDirectory> dirs = storage.dirIterator(EDITS);
        StorageDirectory sd = dirs.next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals((6 * (TestEditLog.TXNS_PER_ROLL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
        sd = dirs.next();
        jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((5 * (TestEditLog.TXNS_PER_ROLL)) + (TestEditLog.TXNS_PER_FAIL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
        sd = dirs.next();
        jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals((6 * (TestEditLog.TXNS_PER_ROLL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
    }

    /**
     * Test that FileJournalManager behaves correctly despite inprogress
     * files in all its edit log directories. Set up 3 directories and fail
     * all on the last roll. Verify that the correct number of transaction
     * are then loaded.
     */
    @Test
    public void testInprogressRecoveryAll() throws IOException {
        File f1 = new File(((TestEditLog.TEST_DIR) + "/failalltest0"));
        File f2 = new File(((TestEditLog.TEST_DIR) + "/failalltest1"));
        File f3 = new File(((TestEditLog.TEST_DIR) + "/failalltest2"));
        List<URI> editUris = ImmutableList.of(f1.toURI(), f2.toURI(), f3.toURI());
        // abort after the 5th roll
        NNStorage storage = TestEditLog.setupEdits(editUris, 5, new TestEditLog.AbortSpec(5, 0), new TestEditLog.AbortSpec(5, 1), new TestEditLog.AbortSpec(5, 2));
        Iterator<StorageDirectory> dirs = storage.dirIterator(EDITS);
        StorageDirectory sd = dirs.next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((5 * (TestEditLog.TXNS_PER_ROLL)) + (TestEditLog.TXNS_PER_FAIL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
        sd = dirs.next();
        jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((5 * (TestEditLog.TXNS_PER_ROLL)) + (TestEditLog.TXNS_PER_FAIL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
        sd = dirs.next();
        jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((5 * (TestEditLog.TXNS_PER_ROLL)) + (TestEditLog.TXNS_PER_FAIL)), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalizeErrorReportedToNNStorage() throws IOException, InterruptedException {
        File f = new File(((TestEditLog.TEST_DIR) + "/filejournaltestError"));
        // abort after 10th roll
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10, new TestEditLog.AbortSpec(10, 0));
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        String sdRootPath = sd.getRoot().getAbsolutePath();
        FileUtil.chmod(sdRootPath, "-w", true);
        try {
            jm.finalizeLogSegment(0, 1);
        } finally {
            FileUtil.chmod(sdRootPath, "+w", true);
            Assert.assertTrue(storage.getRemovedStorageDirs().contains(sd));
        }
    }

    /**
     * Test that we can read from a stream created by FileJournalManager.
     * Create a single edits directory, failing it on the final roll.
     * Then try loading from the point of the 3rd roll. Verify that we read
     * the correct number of transactions from this point.
     */
    @Test
    public void testReadFromStream() throws IOException {
        File f = new File(((TestEditLog.TEST_DIR) + "/readfromstream"));
        // abort after 10th roll
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10, new TestEditLog.AbortSpec(10, 0));
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        long expectedTotalTxnCount = ((TestEditLog.TXNS_PER_ROLL) * 10) + (TestEditLog.TXNS_PER_FAIL);
        Assert.assertEquals(expectedTotalTxnCount, TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
        long skippedTxns = 3 * (TestEditLog.TXNS_PER_ROLL);// skip first 3 files

        long startingTxId = skippedTxns + 1;
        long numLoadable = TestFileJournalManager.getNumberOfTransactions(jm, startingTxId, true, false);
        Assert.assertEquals((expectedTotalTxnCount - skippedTxns), numLoadable);
    }

    /**
     * Make requests with starting transaction ids which don't match the beginning
     * txid of some log segments.
     *
     * This should succeed.
     */
    @Test
    public void testAskForTransactionsMidfile() throws IOException {
        File f = new File(((TestEditLog.TEST_DIR) + "/askfortransactionsmidfile"));
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10);
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        // 10 rolls, so 11 rolled files, 110 txids total.
        final int TOTAL_TXIDS = 10 * 11;
        for (int txid = 1; txid <= TOTAL_TXIDS; txid++) {
            Assert.assertEquals(((TOTAL_TXIDS - txid) + 1), TestFileJournalManager.getNumberOfTransactions(jm, txid, true, false));
        }
    }

    /**
     * Test that we receive the correct number of transactions when we count
     * the number of transactions around gaps.
     * Set up a single edits directory, with no failures. Delete the 4th logfile.
     * Test that getNumberOfTransactions returns the correct number of
     * transactions before this gap and after this gap. Also verify that if you
     * try to count on the gap that an exception is thrown.
     */
    @Test
    public void testManyLogsWithGaps() throws IOException {
        File f = new File(((TestEditLog.TEST_DIR) + "/manylogswithgaps"));
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10);
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        final long startGapTxId = (3 * (TestEditLog.TXNS_PER_ROLL)) + 1;
        final long endGapTxId = 4 * (TestEditLog.TXNS_PER_ROLL);
        File[] files = new File(f, "current").listFiles(new FilenameFilter() {
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
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals((startGapTxId - 1), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, true));
        Assert.assertEquals(0, TestFileJournalManager.getNumberOfTransactions(jm, startGapTxId, true, true));
        // rolled 10 times so there should be 11 files.
        Assert.assertEquals(((11 * (TestEditLog.TXNS_PER_ROLL)) - endGapTxId), TestFileJournalManager.getNumberOfTransactions(jm, (endGapTxId + 1), true, true));
    }

    /**
     * Test that we can load an edits directory with a corrupt inprogress file.
     * The corrupt inprogress file should be moved to the side.
     */
    @Test
    public void testManyLogsWithCorruptInprogress() throws IOException {
        File f = new File(((TestEditLog.TEST_DIR) + "/manylogswithcorruptinprogress"));
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10, new TestEditLog.AbortSpec(10, 0));
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        File[] files = new File(f, "current").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("edits_inprogress")) {
                    return true;
                }
                return false;
            }
        });
        Assert.assertEquals(files.length, 1);
        corruptAfterStartSegment(files[0]);
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        Assert.assertEquals(((10 * (TestEditLog.TXNS_PER_ROLL)) + 1), TestFileJournalManager.getNumberOfTransactions(jm, 1, true, false));
    }

    @Test
    public void testGetRemoteEditLog() throws IOException {
        StorageDirectory sd = FSImageTestUtil.mockStorageDirectory(EDITS, false, NNStorage.getFinalizedEditsFileName(1, 100), NNStorage.getFinalizedEditsFileName(101, 200), NNStorage.getInProgressEditsFileName(201), NNStorage.getFinalizedEditsFileName(1001, 1100));
        // passing null for NNStorage because this unit test will not use it
        FileJournalManager fjm = new FileJournalManager(conf, sd, null);
        Assert.assertEquals("[1,100],[101,200],[1001,1100]", TestFileJournalManager.getLogsAsString(fjm, 1));
        Assert.assertEquals("[101,200],[1001,1100]", TestFileJournalManager.getLogsAsString(fjm, 101));
        Assert.assertEquals("[101,200],[1001,1100]", TestFileJournalManager.getLogsAsString(fjm, 150));
        Assert.assertEquals("[1001,1100]", TestFileJournalManager.getLogsAsString(fjm, 201));
        Assert.assertEquals("Asking for a newer log than exists should return empty list", "", TestFileJournalManager.getLogsAsString(fjm, 9999));
    }

    /**
     * tests that passing an invalid dir to matchEditLogs throws IOException
     */
    @Test(expected = IOException.class)
    public void testMatchEditLogInvalidDirThrowsIOException() throws IOException {
        File badDir = new File("does not exist");
        FileJournalManager.matchEditLogs(badDir);
    }

    /**
     * Make sure that we starting reading the correct op when we request a stream
     * with a txid in the middle of an edit log file.
     */
    @Test
    public void testReadFromMiddleOfEditLog() throws IOException, CorruptionException {
        File f = new File(((TestEditLog.TEST_DIR) + "/readfrommiddleofeditlog"));
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10);
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        EditLogInputStream elis = TestFileJournalManager.getJournalInputStream(jm, 5, true);
        try {
            FSEditLogOp op = elis.readOp();
            Assert.assertEquals("read unexpected op", op.getTransactionId(), 5);
        } finally {
            IOUtils.cleanupWithLogger(TestFileJournalManager.LOG, elis);
        }
    }

    /**
     * Make sure that in-progress streams aren't counted if we don't ask for
     * them.
     */
    @Test
    public void testExcludeInProgressStreams() throws IOException, CorruptionException {
        File f = new File(((TestEditLog.TEST_DIR) + "/excludeinprogressstreams"));
        // Don't close the edit log once the files have been set up.
        NNStorage storage = TestEditLog.setupEdits(Collections.<URI>singletonList(f.toURI()), 10, false);
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        FileJournalManager jm = new FileJournalManager(conf, sd, storage);
        // If we exclude the in-progess stream, we should only have 100 tx.
        Assert.assertEquals(100, TestFileJournalManager.getNumberOfTransactions(jm, 1, false, false));
        EditLogInputStream elis = TestFileJournalManager.getJournalInputStream(jm, 90, false);
        try {
            FSEditLogOp lastReadOp = null;
            while ((lastReadOp = elis.readOp()) != null) {
                Assert.assertTrue(((lastReadOp.getTransactionId()) <= 100));
            } 
        } finally {
            IOUtils.cleanupWithLogger(TestFileJournalManager.LOG, elis);
        }
    }

    /**
     * Tests that internal renames are done using native code on platforms that
     * have it.  The native rename includes more detailed information about the
     * failure, which can be useful for troubleshooting.
     */
    @Test
    public void testDoPreUpgradeIOError() throws IOException {
        File storageDir = new File(TestEditLog.TEST_DIR, "preupgradeioerror");
        List<URI> editUris = Collections.singletonList(storageDir.toURI());
        NNStorage storage = TestEditLog.setupEdits(editUris, 5);
        StorageDirectory sd = storage.dirIterator(EDITS).next();
        Assert.assertNotNull(sd);
        // Change storage directory so that renaming current to previous.tmp fails.
        FileUtil.setWritable(storageDir, false);
        FileJournalManager jm = null;
        try {
            jm = new FileJournalManager(conf, sd, storage);
            exception.expect(IOException.class);
            if (NativeCodeLoader.isNativeCodeLoaded()) {
                exception.expectMessage("failure in native rename");
            }
            jm.doPreUpgrade();
        } finally {
            IOUtils.cleanupWithLogger(TestFileJournalManager.LOG, jm);
            // Restore permissions on storage directory and make sure we can delete.
            FileUtil.setWritable(storageDir, true);
            FileUtil.fullyDelete(storageDir);
        }
    }
}

