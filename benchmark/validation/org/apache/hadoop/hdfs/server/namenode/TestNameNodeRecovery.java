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


import MetaRecoveryContext.FORCE_ALL;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.DeleteOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.OpInstanceCache;
import org.apache.hadoop.test.PathUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static EditLogFileOutputStream.MIN_PREALLOCATION_LENGTH;


/**
 * This tests data recovery mode for the NameNode.
 */
@RunWith(Parameterized.class)
public class TestNameNodeRecovery {
    private static boolean useAsyncEditLog;

    public TestNameNodeRecovery(Boolean async) {
        TestNameNodeRecovery.useAsyncEditLog = async;
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestNameNodeRecovery.class);

    private static final StartupOption recoverStartOpt = StartupOption.RECOVER;

    private static final File TEST_DIR = PathUtils.getTestDir(TestNameNodeRecovery.class);

    static {
        TestNameNodeRecovery.recoverStartOpt.setForce(FORCE_ALL);
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    /**
     * A test scenario for the edit log
     */
    private abstract static class EditLogTestSetup {
        /**
         * Set up the edit log.
         */
        public abstract void addTransactionsToLog(EditLogOutputStream elos, OpInstanceCache cache) throws IOException;

        /**
         * Get the transaction ID right before the transaction which causes the
         * normal edit log loading process to bail out-- or -1 if the first
         * transaction should be bad.
         */
        public abstract long getLastValidTxId();

        /**
         * Get the transaction IDs which should exist and be valid in this
         * edit log.
         */
        public abstract Set<Long> getValidTxIds();

        /**
         * Return the maximum opcode size we will use for input.
         */
        public int getMaxOpSize() {
            return DFSConfigKeys.DFS_NAMENODE_MAX_OP_SIZE_DEFAULT;
        }
    }

    /**
     * Test the scenario where we have an empty edit log.
     *
     * This class is also useful in testing whether we can correctly handle
     * various amounts of padding bytes at the end of the log.  We should be
     * able to handle any amount of padding (including no padding) without
     * throwing an exception.
     */
    private static class EltsTestEmptyLog extends TestNameNodeRecovery.EditLogTestSetup {
        private final int paddingLength;

        public EltsTestEmptyLog(int paddingLength) {
            this.paddingLength = paddingLength;
        }

        @Override
        public void addTransactionsToLog(EditLogOutputStream elos, OpInstanceCache cache) throws IOException {
            TestNameNodeRecovery.padEditLog(elos, paddingLength);
        }

        @Override
        public long getLastValidTxId() {
            return -1;
        }

        @Override
        public Set<Long> getValidTxIds() {
            return new HashSet<Long>();
        }
    }

    /**
     * Test an empty edit log
     */
    @Test(timeout = 180000)
    public void testEmptyLog() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestEmptyLog(0));
    }

    /**
     * Test an empty edit log with padding
     */
    @Test(timeout = 180000)
    public void testEmptyPaddedLog() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestEmptyLog(MIN_PREALLOCATION_LENGTH));
    }

    /**
     * Test an empty edit log with extra-long padding
     */
    @Test(timeout = 180000)
    public void testEmptyExtraPaddedLog() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestEmptyLog((3 * (MIN_PREALLOCATION_LENGTH))));
    }

    /**
     * Test using a non-default maximum opcode length.
     */
    private static class EltsTestNonDefaultMaxOpSize extends TestNameNodeRecovery.EditLogTestSetup {
        public EltsTestNonDefaultMaxOpSize() {
        }

        @Override
        public void addTransactionsToLog(EditLogOutputStream elos, OpInstanceCache cache) throws IOException {
            TestNameNodeRecovery.addDeleteOpcode(elos, cache, 0, "/foo");
            TestNameNodeRecovery.addDeleteOpcode(elos, cache, 1, "/supercalifragalisticexpialadocius.supercalifragalisticexpialadocius");
        }

        @Override
        public long getLastValidTxId() {
            return 0;
        }

        @Override
        public Set<Long> getValidTxIds() {
            return Sets.newHashSet(0L);
        }

        public int getMaxOpSize() {
            return 40;
        }
    }

    /**
     * Test an empty edit log with extra-long padding
     */
    @Test(timeout = 180000)
    public void testNonDefaultMaxOpSize() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestNonDefaultMaxOpSize());
    }

    /**
     * Test the scenario where an edit log contains some padding (0xff) bytes
     * followed by valid opcode data.
     *
     * These edit logs are corrupt, but all the opcodes should be recoverable
     * with recovery mode.
     */
    private static class EltsTestOpcodesAfterPadding extends TestNameNodeRecovery.EditLogTestSetup {
        private final int paddingLength;

        public EltsTestOpcodesAfterPadding(int paddingLength) {
            this.paddingLength = paddingLength;
        }

        @Override
        public void addTransactionsToLog(EditLogOutputStream elos, OpInstanceCache cache) throws IOException {
            TestNameNodeRecovery.padEditLog(elos, paddingLength);
            TestNameNodeRecovery.addDeleteOpcode(elos, cache, 0, "/foo");
        }

        @Override
        public long getLastValidTxId() {
            return 0;
        }

        @Override
        public Set<Long> getValidTxIds() {
            return Sets.newHashSet(0L);
        }
    }

    @Test(timeout = 180000)
    public void testOpcodesAfterPadding() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestOpcodesAfterPadding(MIN_PREALLOCATION_LENGTH));
    }

    @Test(timeout = 180000)
    public void testOpcodesAfterExtraPadding() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestOpcodesAfterPadding((3 * (MIN_PREALLOCATION_LENGTH))));
    }

    private static class EltsTestGarbageInEditLog extends TestNameNodeRecovery.EditLogTestSetup {
        private final long BAD_TXID = 4;

        private final long MAX_TXID = 10;

        @Override
        public void addTransactionsToLog(EditLogOutputStream elos, OpInstanceCache cache) throws IOException {
            for (long txid = 1; txid <= (MAX_TXID); txid++) {
                if (txid == (BAD_TXID)) {
                    byte[] garbage = new byte[]{ 1, 2, 3 };
                    elos.writeRaw(garbage, 0, garbage.length);
                } else {
                    DeleteOp op;
                    op = DeleteOp.getInstance(cache);
                    op.setTransactionId(txid);
                    op.setPath(("/foo." + txid));
                    op.setTimestamp(txid);
                    elos.write(op);
                }
            }
        }

        @Override
        public long getLastValidTxId() {
            return (BAD_TXID) - 1;
        }

        @Override
        public Set<Long> getValidTxIds() {
            return Sets.newHashSet(1L, 2L, 3L, 5L, 6L, 7L, 8L, 9L, 10L);
        }
    }

    /**
     * Test that we can successfully recover from a situation where there is
     * garbage in the middle of the edit log file output stream.
     */
    @Test(timeout = 180000)
    public void testSkipEdit() throws IOException {
        TestNameNodeRecovery.runEditLogTest(new TestNameNodeRecovery.EltsTestGarbageInEditLog());
    }

    /**
     * An algorithm for corrupting an edit log.
     */
    static interface Corruptor {
        /* Corrupt an edit log file.

        @param editFile   The edit log file
         */
        public void corrupt(File editFile) throws IOException;

        /* Explain whether we need to read the log in recovery mode

        @param finalized  True if the edit log in question is finalized.
                          We're a little more lax about reading unfinalized
                          logs.  We will allow a small amount of garbage at
                          the end.  In a finalized log, every byte must be
                          perfect.

        @return           Whether we need to read the log in recovery mode
         */
        public boolean needRecovery(boolean finalized);

        /* Get the name of this corruptor

        @return           The Corruptor name
         */
        public String getName();
    }

    static class TruncatingCorruptor implements TestNameNodeRecovery.Corruptor {
        @Override
        public void corrupt(File editFile) throws IOException {
            // Corrupt the last edit
            long fileLen = editFile.length();
            RandomAccessFile rwf = new RandomAccessFile(editFile, "rw");
            rwf.setLength((fileLen - 1));
            rwf.close();
        }

        @Override
        public boolean needRecovery(boolean finalized) {
            return finalized;
        }

        @Override
        public String getName() {
            return "truncated";
        }
    }

    static class PaddingCorruptor implements TestNameNodeRecovery.Corruptor {
        @Override
        public void corrupt(File editFile) throws IOException {
            // Add junk to the end of the file
            RandomAccessFile rwf = new RandomAccessFile(editFile, "rw");
            rwf.seek(editFile.length());
            for (int i = 0; i < 129; i++) {
                rwf.write(((byte) (0)));
            }
            rwf.write(13);
            rwf.write(14);
            rwf.write(10);
            rwf.write(13);
            rwf.close();
        }

        @Override
        public boolean needRecovery(boolean finalized) {
            // With finalized edit logs, we ignore what's at the end as long as we
            // can make it to the correct transaction ID.
            // With unfinalized edit logs, the finalization process ignores garbage
            // at the end.
            return false;
        }

        @Override
        public String getName() {
            return "padFatal";
        }
    }

    static class SafePaddingCorruptor implements TestNameNodeRecovery.Corruptor {
        private final byte padByte;

        public SafePaddingCorruptor(byte padByte) {
            this.padByte = padByte;
            assert ((this.padByte) == 0) || ((this.padByte) == (-1));
        }

        @Override
        public void corrupt(File editFile) throws IOException {
            // Add junk to the end of the file
            RandomAccessFile rwf = new RandomAccessFile(editFile, "rw");
            rwf.seek(editFile.length());
            rwf.write(((byte) (-1)));
            for (int i = 0; i < 1024; i++) {
                rwf.write(padByte);
            }
            rwf.close();
        }

        @Override
        public boolean needRecovery(boolean finalized) {
            return false;
        }

        @Override
        public String getName() {
            return "pad" + ((int) (padByte));
        }
    }

    /**
     * Test that we can successfully recover from a situation where the last
     * entry in the edit log has been truncated.
     */
    @Test(timeout = 180000)
    public void testRecoverTruncatedEditLog() throws IOException {
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.TruncatingCorruptor(), true);
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.TruncatingCorruptor(), false);
    }

    /**
     * Test that we can successfully recover from a situation where the last
     * entry in the edit log has been padded with garbage.
     */
    @Test(timeout = 180000)
    public void testRecoverPaddedEditLog() throws IOException {
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.PaddingCorruptor(), true);
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.PaddingCorruptor(), false);
    }

    /**
     * Test that don't need to recover from a situation where the last
     * entry in the edit log has been padded with 0.
     */
    @Test(timeout = 180000)
    public void testRecoverZeroPaddedEditLog() throws IOException {
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.SafePaddingCorruptor(((byte) (0))), true);
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.SafePaddingCorruptor(((byte) (0))), false);
    }

    /**
     * Test that don't need to recover from a situation where the last
     * entry in the edit log has been padded with 0xff bytes.
     */
    @Test(timeout = 180000)
    public void testRecoverNegativeOnePaddedEditLog() throws IOException {
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.SafePaddingCorruptor(((byte) (-1))), true);
        TestNameNodeRecovery.testNameNodeRecoveryImpl(new TestNameNodeRecovery.SafePaddingCorruptor(((byte) (-1))), false);
    }
}

