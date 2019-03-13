/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.compress;


import java.util.Arrays;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.DiskPageCompression;
import org.apache.ignite.internal.pagemem.PageUtils;
import org.apache.ignite.internal.processors.cache.persistence.tree.BPlusTree;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.BPlusIO;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.BPlusInnerIO;
import org.apache.ignite.internal.processors.cache.persistence.tree.io.BPlusLeafIO;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class CompressionProcessorTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int ITEM_SIZE = 6;// To fill the whole page.


    /**
     *
     */
    private int blockSize = 16;

    /**
     *
     */
    private int pageSize = 4 * 1024;

    /**
     *
     */
    private DiskPageCompression compression;

    /**
     *
     */
    private int compressLevel;

    /**
     *
     */
    private CompressionProcessor p;

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageCompact16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SKIP_GARBAGE;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageCompact128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SKIP_GARBAGE;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageCompact1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SKIP_GARBAGE;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageCompact2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SKIP_GARBAGE;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageZstd16() throws IgniteCheckedException {
        blockSize = 16;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageZstd128() throws IgniteCheckedException {
        blockSize = 128;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageZstd1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageZstd2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageSnappy16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SNAPPY;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageSnappy128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SNAPPY;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageSnappy1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SNAPPY;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageSnappy2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SNAPPY;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Fast16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Fast128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Fast1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Fast2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Slow16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Slow128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Slow1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDataPageLz4Slow2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestDataPage();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageCompact16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageCompact16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageZstd16() throws IgniteCheckedException {
        blockSize = 16;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageZstd16() throws IgniteCheckedException {
        blockSize = 16;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Fast16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Fast16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Slow16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Slow16() throws IgniteCheckedException {
        blockSize = 16;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageSnappy16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageSnappy16() throws IgniteCheckedException {
        blockSize = 16;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageCompact128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageCompact128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageZstd128() throws IgniteCheckedException {
        blockSize = 128;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageZstd128() throws IgniteCheckedException {
        blockSize = 128;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Fast128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Fast128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Slow128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Slow128() throws IgniteCheckedException {
        blockSize = 128;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageSnappy128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageSnappy128() throws IgniteCheckedException {
        blockSize = 128;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageCompact1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageCompact1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageZstd1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageZstd1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Fast1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Fast1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Slow1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Slow1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageSnappy1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageSnappy1k() throws IgniteCheckedException {
        blockSize = 1024;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageCompact2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageCompact2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SKIP_GARBAGE;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageZstd2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageZstd2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = ZSTD;
        compressLevel = CompressionProcessor.ZSTD_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Fast2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Fast2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MIN_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageLz4Slow2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageLz4Slow2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = LZ4;
        compressLevel = CompressionProcessor.LZ4_MAX_LEVEL;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testInnerPageSnappy2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestInnerIO.INNER_IO);
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testLeafPageSnappy2k() throws IgniteCheckedException {
        blockSize = 2 * 1024;
        compression = SNAPPY;
        doTestBTreePage(CompressionProcessorTest.TestLeafIO.LEAF_IO);
    }

    /**
     *
     */
    private static class Bytes {
        /**
         *
         */
        private final byte[] bytes;

        /**
         *
         *
         * @param bytes
         * 		Bytes.
         */
        private Bytes(byte[] bytes) {
            assert bytes != null;
            this.bytes = bytes;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            CompressionProcessorTest.Bytes bytes1 = ((CompressionProcessorTest.Bytes) (o));
            return Arrays.equals(bytes, bytes1.bytes);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }

    /**
     *
     */
    static class TestLeafIO extends BPlusLeafIO<byte[]> {
        /**
         *
         */
        static final CompressionProcessorTest.TestLeafIO LEAF_IO = new CompressionProcessorTest.TestLeafIO();

        /**
         *
         */
        TestLeafIO() {
            super(29501, 1, CompressionProcessorTest.ITEM_SIZE);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void storeByOffset(long pageAddr, int off, byte[] row) {
            PageUtils.putBytes(pageAddr, off, row);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void store(long dstPageAddr, int dstIdx, BPlusIO<byte[]> srcIo, long srcPageAddr, int srcIdx) throws IgniteCheckedException {
            storeByOffset(dstPageAddr, offset(dstIdx), srcIo.getLookupRow(null, srcPageAddr, srcIdx));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public byte[] getLookupRow(BPlusTree<byte[], ?> tree, long pageAddr, int idx) {
            return PageUtils.getBytes(pageAddr, offset(idx), itemSize);
        }
    }

    /**
     *
     */
    static class TestInnerIO extends BPlusInnerIO<byte[]> {
        /**
         *
         */
        static CompressionProcessorTest.TestInnerIO INNER_IO = new CompressionProcessorTest.TestInnerIO();

        /**
         *
         */
        TestInnerIO() {
            super(29502, 1, true, CompressionProcessorTest.ITEM_SIZE);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void storeByOffset(long pageAddr, int off, byte[] row) {
            PageUtils.putBytes(pageAddr, off, row);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void store(long dstPageAddr, int dstIdx, BPlusIO<byte[]> srcIo, long srcPageAddr, int srcIdx) throws IgniteCheckedException {
            storeByOffset(dstPageAddr, offset(dstIdx), srcIo.getLookupRow(null, srcPageAddr, srcIdx));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public byte[] getLookupRow(BPlusTree<byte[], ?> tree, long pageAddr, int idx) {
            return PageUtils.getBytes(pageAddr, offset(idx), itemSize);
        }
    }
}

