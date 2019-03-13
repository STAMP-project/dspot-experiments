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
package org.apache.hadoop.hbase.io.hfile;


import BlockType.DATA;
import Compression.Algorithm;
import HFileBlock.FSReader;
import HFileBlock.FSReaderImpl;
import HFileBlock.Writer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.fs.HFileSystem;
import org.apache.hadoop.hbase.io.FSDataInputStreamWrapper;
import org.apache.hadoop.hbase.nio.ByteBuff;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.ChecksumType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.io.compress.Compression.Algorithm.GZ;
import static org.apache.hadoop.hbase.io.compress.Compression.Algorithm.NONE;


@Category({ IOTests.class, SmallTests.class })
public class TestChecksum {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestChecksum.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileBlock.class);

    static final Algorithm[] COMPRESSION_ALGORITHMS = new Algorithm[]{ NONE, GZ };

    static final int[] BYTES_PER_CHECKSUM = new int[]{ 50, 500, 688, 16 * 1024, (16 * 1024) + 980, 64 * 1024 };

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private FileSystem fs;

    private HFileSystem hfs;

    @Test
    public void testNewBlocksHaveDefaultChecksum() throws IOException {
        Path path = new Path(getDataTestDir(), "default_checksum");
        FSDataOutputStream os = fs.create(path);
        HFileContext meta = new HFileContextBuilder().build();
        HFileBlock.Writer hbw = new HFileBlock.Writer(null, meta);
        DataOutputStream dos = hbw.startWriting(DATA);
        for (int i = 0; i < 1000; ++i)
            dos.writeInt(i);

        hbw.writeHeaderAndData(os);
        int totalSize = hbw.getOnDiskSizeWithHeader();
        os.close();
        // Use hbase checksums.
        Assert.assertEquals(true, hfs.useHBaseChecksum());
        FSDataInputStreamWrapper is = new FSDataInputStreamWrapper(fs, path);
        meta = new HFileContextBuilder().withHBaseCheckSum(true).build();
        HFileBlock.FSReader hbr = new HFileBlock.FSReaderImpl(is, totalSize, ((HFileSystem) (fs)), path, meta);
        HFileBlock b = hbr.readBlockData(0, (-1), false, false);
        Assert.assertEquals(b.getChecksumType(), ChecksumType.getDefaultChecksumType().getCode());
    }

    /**
     * Test all checksum types by writing and reading back blocks.
     */
    @Test
    public void testAllChecksumTypes() throws IOException {
        List<ChecksumType> cktypes = new java.util.ArrayList(Arrays.asList(ChecksumType.values()));
        for (Iterator<ChecksumType> itr = cktypes.iterator(); itr.hasNext();) {
            ChecksumType cktype = itr.next();
            Path path = new Path(getDataTestDir(), ("checksum" + (cktype.getName())));
            FSDataOutputStream os = fs.create(path);
            HFileContext meta = new HFileContextBuilder().withChecksumType(cktype).build();
            HFileBlock.Writer hbw = new HFileBlock.Writer(null, meta);
            DataOutputStream dos = hbw.startWriting(DATA);
            for (int i = 0; i < 1000; ++i) {
                dos.writeInt(i);
            }
            hbw.writeHeaderAndData(os);
            int totalSize = hbw.getOnDiskSizeWithHeader();
            os.close();
            // Use hbase checksums.
            Assert.assertEquals(true, hfs.useHBaseChecksum());
            FSDataInputStreamWrapper is = new FSDataInputStreamWrapper(fs, path);
            meta = new HFileContextBuilder().withHBaseCheckSum(true).build();
            HFileBlock.FSReader hbr = new HFileBlock.FSReaderImpl(is, totalSize, ((HFileSystem) (fs)), path, meta);
            HFileBlock b = hbr.readBlockData(0, (-1), false, false);
            ByteBuff data = b.getBufferWithoutHeader();
            for (int i = 0; i < 1000; i++) {
                Assert.assertEquals(i, data.getInt());
            }
            boolean exception_thrown = false;
            try {
                data.getInt();
            } catch (BufferUnderflowException e) {
                exception_thrown = true;
            }
            Assert.assertTrue(exception_thrown);
            Assert.assertEquals(0, HFile.getAndResetChecksumFailuresCount());
        }
    }

    /**
     * Introduce checksum failures and check that we can still read
     * the data
     */
    @Test
    public void testChecksumCorruption() throws IOException {
        testChecksumCorruptionInternals(false);
        testChecksumCorruptionInternals(true);
    }

    /**
     * Test different values of bytesPerChecksum
     */
    @Test
    public void testChecksumChunks() throws IOException {
        testChecksumInternals(false);
        testChecksumInternals(true);
    }

    /**
     * This class is to test checksum behavior when data is corrupted. It mimics the following
     * behavior:
     *  - When fs checksum is disabled, hbase may get corrupted data from hdfs. If verifyChecksum
     *  is true, it means hbase checksum is on and fs checksum is off, so we corrupt the data.
     *  - When fs checksum is enabled, hdfs will get a different copy from another node, and will
     *    always return correct data. So we don't corrupt the data when verifyChecksum for hbase is
     *    off.
     */
    private static class CorruptedFSReaderImpl extends HFileBlock.FSReaderImpl {
        /**
         * If set to true, corrupt reads using readAtOffset(...).
         */
        boolean corruptDataStream = false;

        public CorruptedFSReaderImpl(FSDataInputStreamWrapper istream, long fileSize, FileSystem fs, Path path, HFileContext meta) throws IOException {
            super(istream, fileSize, ((HFileSystem) (fs)), path, meta);
        }

        @Override
        protected HFileBlock readBlockDataInternal(FSDataInputStream is, long offset, long onDiskSizeWithHeaderL, boolean pread, boolean verifyChecksum, boolean updateMetrics) throws IOException {
            if (verifyChecksum) {
                corruptDataStream = true;
            }
            HFileBlock b = super.readBlockDataInternal(is, offset, onDiskSizeWithHeaderL, pread, verifyChecksum, updateMetrics);
            corruptDataStream = false;
            return b;
        }

        @Override
        protected int readAtOffset(FSDataInputStream istream, byte[] dest, int destOffset, int size, boolean peekIntoNextBlock, long fileOffset, boolean pread) throws IOException {
            int returnValue = super.readAtOffset(istream, dest, destOffset, size, peekIntoNextBlock, fileOffset, pread);
            if (!(corruptDataStream)) {
                return returnValue;
            }
            // Corrupt 3rd character of block magic of next block's header.
            if (peekIntoNextBlock) {
                dest[((destOffset + size) + 3)] = 0;
            }
            // We might be reading this block's header too, corrupt it.
            dest[(destOffset + 1)] = 0;
            // Corrupt non header data
            if (size > (hdrSize)) {
                dest[((destOffset + (hdrSize)) + 1)] = 0;
            }
            return returnValue;
        }
    }
}

