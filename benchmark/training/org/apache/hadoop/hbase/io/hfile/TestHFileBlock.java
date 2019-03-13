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


import HFileBlock.FSReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.ChecksumType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BlockType.DATA;
import static HFileBlock.FILL_HEADER;


@Category({ IOTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestHFileBlock {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileBlock.class);

    // change this value to activate more logs
    private static final boolean detailedLogging = false;

    private static final boolean[] BOOLEAN_VALUES = new boolean[]{ false, true };

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileBlock.class);

    static final Algorithm[] COMPRESSION_ALGORITHMS = new Algorithm[]{ Algorithm.NONE, Algorithm.GZ };

    private static final int NUM_TEST_BLOCKS = 1000;

    private static final int NUM_READER_THREADS = 26;

    // Used to generate KeyValues
    private static int NUM_KEYVALUES = 50;

    private static int FIELD_LENGTH = 10;

    private static float CHANCE_TO_REPEAT = 0.6F;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private FileSystem fs;

    private final boolean includesMemstoreTS;

    private final boolean includesTag;

    public TestHFileBlock(boolean includesMemstoreTS, boolean includesTag) {
        this.includesMemstoreTS = includesMemstoreTS;
        this.includesTag = includesTag;
    }

    @Test
    public void testNoCompression() throws IOException {
        CacheConfig cacheConf = Mockito.mock(CacheConfig.class);
        Mockito.when(cacheConf.getBlockCache()).thenReturn(Optional.empty());
        HFileBlock block = TestHFileBlock.createTestV2Block(Algorithm.NONE, includesMemstoreTS, false).getBlockForCaching(cacheConf);
        Assert.assertEquals(4000, block.getUncompressedSizeWithoutHeader());
        Assert.assertEquals(4004, block.getOnDiskSizeWithoutHeader());
        Assert.assertTrue(block.isUnpacked());
    }

    @Test
    public void testGzipCompression() throws IOException {
        final String correctTestBlockStr = (((((((((((("DATABLK*\\x00\\x00\\x00>\\x00\\x00\\x0F\\xA0\\xFF\\xFF\\xFF\\xFF" + ("\\xFF\\xFF\\xFF\\xFF" + "\\x0")) + (ChecksumType.getDefaultChecksumType().getCode())) + "\\x00\\x00@\\x00\\x00\\x00\\x00[") + // gzip-compressed block: http://www.gzip.org/zlib/rfc-gzip.html
        "\\x1F\\x8B")// gzip magic signature
         + "\\x08")// Compression method: 8 = "deflate"
         + "\\x00")// Flags
         + "\\x00\\x00\\x00\\x00")// mtime
         + "\\x00")// XFL (extra flags)
         + // OS (0 = FAT filesystems, 3 = Unix). However, this field
        // sometimes gets set to 0 on Linux and Mac, so we reset it to 3.
        // This appears to be a difference caused by the availability
        // (and use) of the native GZ codec.
        "\\x03") + "\\xED\\xC3\\xC1\\x11\\x00 \\x08\\xC00DD\\xDD\\x7Fa") + "\\xD6\\xE8\\xA3\\xB9K\\x84`\\x96Q\\xD3\\xA8\\xDB\\xA8e\\xD4c") + "\\xD46\\xEA5\\xEA3\\xEA7\\xE7\\x00LI\\x5Cs\\xA0\\x0F\\x00\\x00") + "\\x00\\x00\\x00\\x00";// 4 byte checksum (ignored)

        final int correctGzipBlockLength = 95;
        final String testBlockStr = createTestBlockStr(Algorithm.GZ, correctGzipBlockLength, false);
        // We ignore the block checksum because createTestBlockStr can change the
        // gzip header after the block is produced
        Assert.assertEquals(correctTestBlockStr.substring(0, (correctGzipBlockLength - 4)), testBlockStr.substring(0, (correctGzipBlockLength - 4)));
    }

    @Test
    public void testReaderV2() throws IOException {
        testReaderV2Internals();
    }

    /**
     * Test encoding/decoding data blocks.
     *
     * @throws IOException
     * 		a bug or a problem with temporary files.
     */
    @Test
    public void testDataBlockEncoding() throws IOException {
        testInternals();
    }

    @Test
    public void testPreviousOffset() throws IOException {
        testPreviousOffsetInternals();
    }

    private class BlockReaderThread implements Callable<Boolean> {
        private final String clientId;

        private final FSReader hbr;

        private final List<Long> offsets;

        private final List<BlockType> types;

        private final long fileSize;

        public BlockReaderThread(String clientId, HFileBlock.FSReader hbr, List<Long> offsets, List<BlockType> types, long fileSize) {
            this.clientId = clientId;
            this.offsets = offsets;
            this.hbr = hbr;
            this.types = types;
            this.fileSize = fileSize;
        }

        @Override
        public Boolean call() throws Exception {
            Random rand = new Random(clientId.hashCode());
            long endTime = (System.currentTimeMillis()) + 10000;
            int numBlocksRead = 0;
            int numPositionalRead = 0;
            int numWithOnDiskSize = 0;
            while ((System.currentTimeMillis()) < endTime) {
                int blockId = rand.nextInt(TestHFileBlock.NUM_TEST_BLOCKS);
                long offset = offsets.get(blockId);
                // now we only support concurrent read with pread = true
                boolean pread = true;
                boolean withOnDiskSize = rand.nextBoolean();
                long expectedSize = (blockId == ((TestHFileBlock.NUM_TEST_BLOCKS) - 1) ? fileSize : offsets.get((blockId + 1))) - offset;
                HFileBlock b;
                try {
                    long onDiskSizeArg = (withOnDiskSize) ? expectedSize : -1;
                    b = hbr.readBlockData(offset, onDiskSizeArg, pread, false);
                } catch (IOException ex) {
                    TestHFileBlock.LOG.error(((((((("Error in client " + (clientId)) + " trying to read block at ") + offset) + ", pread=") + pread) + ", withOnDiskSize=") + withOnDiskSize), ex);
                    return false;
                }
                Assert.assertEquals(types.get(blockId), b.getBlockType());
                Assert.assertEquals(expectedSize, b.getOnDiskSizeWithHeader());
                Assert.assertEquals(offset, b.getOffset());
                ++numBlocksRead;
                if (pread)
                    ++numPositionalRead;

                if (withOnDiskSize)
                    ++numWithOnDiskSize;

            } 
            TestHFileBlock.LOG.info(((((((((("Client " + (clientId)) + " successfully read ") + numBlocksRead) + " blocks (with pread: ") + numPositionalRead) + ", with onDiskSize ") + "specified: ") + numWithOnDiskSize) + ")"));
            return true;
        }
    }

    @Test
    public void testConcurrentReading() throws Exception {
        testConcurrentReadingInternals();
    }

    @Test
    public void testBlockHeapSize() {
        testBlockHeapSizeInternals();
    }

    @Test
    public void testSerializeWithoutNextBlockMetadata() {
        int size = 100;
        int length = (HConstants.HFILEBLOCK_HEADER_SIZE) + size;
        byte[] byteArr = new byte[length];
        ByteBuffer buf = ByteBuffer.wrap(byteArr, 0, size);
        HFileContext meta = new HFileContextBuilder().build();
        HFileBlock blockWithNextBlockMetadata = new HFileBlock(DATA, size, size, (-1), buf, FILL_HEADER, (-1), 52, (-1), meta);
        HFileBlock blockWithoutNextBlockMetadata = new HFileBlock(DATA, size, size, (-1), buf, FILL_HEADER, (-1), (-1), (-1), meta);
        ByteBuffer buff1 = ByteBuffer.allocate(length);
        ByteBuffer buff2 = ByteBuffer.allocate(length);
        blockWithNextBlockMetadata.serialize(buff1, true);
        blockWithoutNextBlockMetadata.serialize(buff2, true);
        Assert.assertNotEquals(buff1, buff2);
        buff1.clear();
        buff2.clear();
        blockWithNextBlockMetadata.serialize(buff1, false);
        blockWithoutNextBlockMetadata.serialize(buff2, false);
        Assert.assertEquals(buff1, buff2);
    }
}

