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


import Algorithm.NONE;
import CacheConfig.CACHE_INDEX_BLOCKS_ON_WRITE_KEY;
import CellComparatorImpl.COMPARATOR;
import HFile.Reader;
import HFile.Writer;
import HFileBlock.BlockIterator;
import HFileBlock.FSReader;
import HFileBlockIndex.MAX_CHUNK_SIZE_KEY;
import KeyValue.KeyOnlyKeyValue;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.io.hfile.HFileBlockIndex.BlockIndexChunk;
import org.apache.hadoop.hbase.io.hfile.HFileBlockIndex.BlockIndexReader;
import org.apache.hadoop.hbase.nio.ByteBuff;
import org.apache.hadoop.hbase.nio.MultiByteBuff;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ClassSize;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BlockType.LEAF_INDEX;
import static HFileBlockIndex.SECONDARY_INDEX_ENTRY_OVERHEAD;


@RunWith(Parameterized.class)
@Category({ IOTests.class, MediumTests.class })
public class TestHFileBlockIndex {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileBlockIndex.class);

    public TestHFileBlockIndex(Compression.Algorithm compr) {
        this.compr = compr;
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileBlockIndex.class);

    private static final int NUM_DATA_BLOCKS = 1000;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int SMALL_BLOCK_SIZE = 4096;

    private static final int NUM_KV = 10000;

    private static FileSystem fs;

    private Path path;

    private Random rand;

    private long rootIndexOffset;

    private int numRootEntries;

    private int numLevels;

    private static final List<byte[]> keys = new ArrayList<>();

    private final Algorithm compr;

    private byte[] firstKeyInFile;

    private Configuration conf;

    private static final int[] INDEX_CHUNK_SIZES = new int[]{ 4096, 512, 384 };

    private static final int[] EXPECTED_NUM_LEVELS = new int[]{ 2, 3, 4 };

    private static final int[] UNCOMPRESSED_INDEX_SIZES = new int[]{ 19187, 21813, 23086 };

    private static final boolean includesMemstoreTS = true;

    static {
        assert (TestHFileBlockIndex.INDEX_CHUNK_SIZES.length) == (TestHFileBlockIndex.EXPECTED_NUM_LEVELS.length);
        assert (TestHFileBlockIndex.INDEX_CHUNK_SIZES.length) == (TestHFileBlockIndex.UNCOMPRESSED_INDEX_SIZES.length);
    }

    @Test
    public void testBlockIndex() throws IOException {
        testBlockIndexInternals(false);
        clear();
        testBlockIndexInternals(true);
    }

    /**
     * A wrapper around a block reader which only caches the results of the last
     * operation. Not thread-safe.
     */
    private static class BlockReaderWrapper implements HFile.CachingBlockReader {
        private FSReader realReader;

        private long prevOffset;

        private long prevOnDiskSize;

        private boolean prevPread;

        private HFileBlock prevBlock;

        public int hitCount = 0;

        public int missCount = 0;

        public BlockReaderWrapper(HFileBlock.FSReader realReader) {
            this.realReader = realReader;
        }

        @Override
        public void returnBlock(HFileBlock block) {
        }

        @Override
        public HFileBlock readBlock(long offset, long onDiskSize, boolean cacheBlock, boolean pread, boolean isCompaction, boolean updateCacheMetrics, BlockType expectedBlockType, DataBlockEncoding expectedDataBlockEncoding) throws IOException {
            if (((offset == (prevOffset)) && (onDiskSize == (prevOnDiskSize))) && (pread == (prevPread))) {
                hitCount += 1;
                return prevBlock;
            }
            missCount += 1;
            prevBlock = realReader.readBlockData(offset, onDiskSize, pread, false);
            prevOffset = offset;
            prevOnDiskSize = onDiskSize;
            prevPread = pread;
            return prevBlock;
        }
    }

    @Test
    public void testSecondaryIndexBinarySearch() throws IOException {
        int numTotalKeys = 99;
        Assert.assertTrue(((numTotalKeys % 2) == 1));// Ensure no one made this even.

        // We only add odd-index keys into the array that we will binary-search.
        int numSearchedKeys = (numTotalKeys - 1) / 2;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(numSearchedKeys);
        int curAllEntriesSize = 0;
        int numEntriesAdded = 0;
        // Only odd-index elements of this array are used to keep the secondary
        // index entries of the corresponding keys.
        int[] secondaryIndexEntries = new int[numTotalKeys];
        for (int i = 0; i < numTotalKeys; ++i) {
            byte[] k = RandomKeyValueUtil.randomOrderedKey(rand, (i * 2));
            KeyValue cell = new KeyValue(k, Bytes.toBytes("f"), Bytes.toBytes("q"), Bytes.toBytes("val"));
            // KeyValue cell = new KeyValue.KeyOnlyKeyValue(k, 0, k.length);
            TestHFileBlockIndex.keys.add(cell.getKey());
            String msgPrefix = ((("Key #" + i) + " (") + (Bytes.toStringBinary(k))) + "): ";
            StringBuilder padding = new StringBuilder();
            while (((msgPrefix.length()) + (padding.length())) < 70)
                padding.append(' ');

            msgPrefix += padding;
            if ((i % 2) == 1) {
                dos.writeInt(curAllEntriesSize);
                secondaryIndexEntries[i] = curAllEntriesSize;
                TestHFileBlockIndex.LOG.info(((((msgPrefix + "secondary index entry #") + ((i - 1) / 2)) + ", offset ") + curAllEntriesSize));
                curAllEntriesSize += (cell.getKey().length) + (SECONDARY_INDEX_ENTRY_OVERHEAD);
                ++numEntriesAdded;
            } else {
                secondaryIndexEntries[i] = -1;
                TestHFileBlockIndex.LOG.info((msgPrefix + "not in the searched array"));
            }
        }
        // Make sure the keys are increasing.
        for (int i = 0; i < ((TestHFileBlockIndex.keys.size()) - 1); ++i)
            Assert.assertTrue(((COMPARATOR.compare(new KeyValue.KeyOnlyKeyValue(TestHFileBlockIndex.keys.get(i), 0, TestHFileBlockIndex.keys.get(i).length), new KeyValue.KeyOnlyKeyValue(TestHFileBlockIndex.keys.get((i + 1)), 0, TestHFileBlockIndex.keys.get((i + 1)).length))) < 0));

        dos.writeInt(curAllEntriesSize);
        Assert.assertEquals(numSearchedKeys, numEntriesAdded);
        int secondaryIndexOffset = dos.size();
        Assert.assertEquals(((Bytes.SIZEOF_INT) * (numSearchedKeys + 2)), secondaryIndexOffset);
        for (int i = 1; i <= (numTotalKeys - 1); i += 2) {
            Assert.assertEquals(dos.size(), (secondaryIndexOffset + (secondaryIndexEntries[i])));
            long dummyFileOffset = TestHFileBlockIndex.getDummyFileOffset(i);
            int dummyOnDiskSize = TestHFileBlockIndex.getDummyOnDiskSize(i);
            TestHFileBlockIndex.LOG.debug(((((("Storing file offset=" + dummyFileOffset) + " and onDiskSize=") + dummyOnDiskSize) + " at offset ") + (dos.size())));
            dos.writeLong(dummyFileOffset);
            dos.writeInt(dummyOnDiskSize);
            TestHFileBlockIndex.LOG.debug(((("Stored key " + ((i - 1) / 2)) + " at offset ") + (dos.size())));
            dos.write(TestHFileBlockIndex.keys.get(i));
        }
        dos.writeInt(curAllEntriesSize);
        ByteBuffer nonRootIndex = ByteBuffer.wrap(baos.toByteArray());
        for (int i = 0; i < numTotalKeys; ++i) {
            byte[] searchKey = TestHFileBlockIndex.keys.get(i);
            byte[] arrayHoldingKey = new byte[(searchKey.length) + ((searchKey.length) / 2)];
            // To make things a bit more interesting, store the key we are looking
            // for at a non-zero offset in a new array.
            System.arraycopy(searchKey, 0, arrayHoldingKey, ((searchKey.length) / 2), searchKey.length);
            KeyValue.KeyOnlyKeyValue cell = new KeyValue.KeyOnlyKeyValue(arrayHoldingKey, ((searchKey.length) / 2), searchKey.length);
            int searchResult = BlockIndexReader.binarySearchNonRootIndex(cell, new MultiByteBuff(nonRootIndex), COMPARATOR);
            String lookupFailureMsg = ((("Failed to look up key #" + i) + " (") + (Bytes.toStringBinary(searchKey))) + ")";
            int expectedResult;
            int referenceItem;
            if ((i % 2) == 1) {
                // This key is in the array we search as the element (i - 1) / 2. Make
                // sure we find it.
                expectedResult = (i - 1) / 2;
                referenceItem = i;
            } else {
                // This key is not in the array but between two elements on the array,
                // in the beginning, or in the end. The result should be the previous
                // key in the searched array, or -1 for i = 0.
                expectedResult = (i / 2) - 1;
                referenceItem = i - 1;
            }
            Assert.assertEquals(lookupFailureMsg, expectedResult, searchResult);
            // Now test we can get the offset and the on-disk-size using a
            // higher-level API function.s
            boolean locateBlockResult = (BlockIndexReader.locateNonRootIndexEntry(new MultiByteBuff(nonRootIndex), cell, COMPARATOR)) != (-1);
            if (i == 0) {
                Assert.assertFalse(locateBlockResult);
            } else {
                Assert.assertTrue(locateBlockResult);
                String errorMsg = (("i=" + i) + ", position=") + (nonRootIndex.position());
                Assert.assertEquals(errorMsg, TestHFileBlockIndex.getDummyFileOffset(referenceItem), nonRootIndex.getLong());
                Assert.assertEquals(errorMsg, TestHFileBlockIndex.getDummyOnDiskSize(referenceItem), nonRootIndex.getInt());
            }
        }
    }

    @Test
    public void testBlockIndexChunk() throws IOException {
        BlockIndexChunk c = new BlockIndexChunk();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int N = 1000;
        int[] numSubEntriesAt = new int[N];
        int numSubEntries = 0;
        for (int i = 0; i < N; ++i) {
            baos.reset();
            DataOutputStream dos = new DataOutputStream(baos);
            c.writeNonRoot(dos);
            Assert.assertEquals(c.getNonRootSize(), dos.size());
            baos.reset();
            dos = new DataOutputStream(baos);
            c.writeRoot(dos);
            Assert.assertEquals(c.getRootSize(), dos.size());
            byte[] k = RandomKeyValueUtil.randomOrderedKey(rand, i);
            numSubEntries += (rand.nextInt(5)) + 1;
            TestHFileBlockIndex.keys.add(k);
            c.add(k, TestHFileBlockIndex.getDummyFileOffset(i), TestHFileBlockIndex.getDummyOnDiskSize(i), numSubEntries);
        }
        // Test the ability to look up the entry that contains a particular
        // deeper-level index block's entry ("sub-entry"), assuming a global
        // 0-based ordering of sub-entries. This is needed for mid-key calculation.
        for (int i = 0; i < N; ++i) {
            for (int j = (i == 0) ? 0 : numSubEntriesAt[(i - 1)]; j < (numSubEntriesAt[i]); ++j) {
                Assert.assertEquals(i, c.getEntryBySubEntry(j));
            }
        }
    }

    /**
     * Checks if the HeapSize calculator is within reason
     */
    @Test
    public void testHeapSizeForBlockIndex() throws IOException {
        Class<HFileBlockIndex.BlockIndexReader> cl = BlockIndexReader.class;
        long expected = ClassSize.estimateBase(cl, false);
        HFileBlockIndex.BlockIndexReader bi = new HFileBlockIndex.ByteArrayKeyBlockIndexReader(1);
        long actual = bi.heapSize();
        // Since the arrays in BlockIndex(byte [][] blockKeys, long [] blockOffsets,
        // int [] blockDataSizes) are all null they are not going to show up in the
        // HeapSize calculation, so need to remove those array costs from expected.
        // Already the block keys are not there in this case
        expected -= ClassSize.align((2 * (ClassSize.ARRAY)));
        if (expected != actual) {
            expected = ClassSize.estimateBase(cl, true);
            Assert.assertEquals(expected, actual);
        }
    }

    /**
     * to check if looks good when midKey on a leaf index block boundary
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMidKeyOnLeafIndexBlockBoundary() throws IOException {
        Path hfilePath = new Path(getDataTestDir(), "hfile_for_midkey");
        int maxChunkSize = 512;
        conf.setInt(MAX_CHUNK_SIZE_KEY, maxChunkSize);
        // should open hfile.block.index.cacheonwrite
        conf.setBoolean(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, true);
        CacheConfig cacheConf = new CacheConfig(conf, BlockCacheFactory.createBlockCache(conf));
        BlockCache blockCache = cacheConf.getBlockCache().get();
        // Evict all blocks that were cached-on-write by the previous invocation.
        blockCache.evictBlocksByHfileName(hfilePath.getName());
        // Write the HFile
        HFileContext meta = new HFileContextBuilder().withBlockSize(TestHFileBlockIndex.SMALL_BLOCK_SIZE).withCompression(NONE).withDataBlockEncoding(DataBlockEncoding.NONE).build();
        HFile.Writer writer = HFile.getWriterFactory(conf, cacheConf).withPath(TestHFileBlockIndex.fs, hfilePath).withFileContext(meta).create();
        Random rand = new Random(19231737);
        byte[] family = Bytes.toBytes("f");
        byte[] qualifier = Bytes.toBytes("q");
        int kvNumberToBeWritten = 16;
        // the new generated hfile will contain 2 leaf-index blocks and 16 data blocks,
        // midkey is just on the boundary of the first leaf-index block
        for (int i = 0; i < kvNumberToBeWritten; ++i) {
            byte[] row = RandomKeyValueUtil.randomOrderedFixedLengthKey(rand, i, 30);
            // Key will be interpreted by KeyValue.KEY_COMPARATOR
            KeyValue kv = new KeyValue(row, family, qualifier, EnvironmentEdgeManager.currentTime(), RandomKeyValueUtil.randomFixedLengthValue(rand, TestHFileBlockIndex.SMALL_BLOCK_SIZE));
            writer.append(kv);
        }
        writer.close();
        // close hfile.block.index.cacheonwrite
        conf.setBoolean(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, false);
        // Read the HFile
        HFile.Reader reader = HFile.createReader(TestHFileBlockIndex.fs, hfilePath, cacheConf, true, conf);
        boolean hasArrayIndexOutOfBoundsException = false;
        try {
            // get the mid-key.
            reader.midKey();
        } catch (ArrayIndexOutOfBoundsException e) {
            hasArrayIndexOutOfBoundsException = true;
        } finally {
            reader.close();
        }
        // to check if ArrayIndexOutOfBoundsException occurred
        Assert.assertFalse(hasArrayIndexOutOfBoundsException);
    }

    /**
     * Testing block index through the HFile writer/reader APIs. Allows to test
     * setting index block size through configuration, intermediate-level index
     * blocks, and caching index blocks on write.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testHFileWriterAndReader() throws IOException {
        Path hfilePath = new Path(getDataTestDir(), "hfile_for_block_index");
        CacheConfig cacheConf = new CacheConfig(conf, BlockCacheFactory.createBlockCache(conf));
        BlockCache blockCache = cacheConf.getBlockCache().get();
        for (int testI = 0; testI < (TestHFileBlockIndex.INDEX_CHUNK_SIZES.length); ++testI) {
            int indexBlockSize = TestHFileBlockIndex.INDEX_CHUNK_SIZES[testI];
            int expectedNumLevels = TestHFileBlockIndex.EXPECTED_NUM_LEVELS[testI];
            TestHFileBlockIndex.LOG.info(((("Index block size: " + indexBlockSize) + ", compression: ") + (compr)));
            // Evict all blocks that were cached-on-write by the previous invocation.
            blockCache.evictBlocksByHfileName(hfilePath.getName());
            conf.setInt(MAX_CHUNK_SIZE_KEY, indexBlockSize);
            Set<String> keyStrSet = new HashSet<>();
            byte[][] keys = new byte[TestHFileBlockIndex.NUM_KV][];
            byte[][] values = new byte[TestHFileBlockIndex.NUM_KV][];
            // Write the HFile
            {
                HFileContext meta = new HFileContextBuilder().withBlockSize(TestHFileBlockIndex.SMALL_BLOCK_SIZE).withCompression(compr).build();
                HFile.Writer writer = HFile.getWriterFactory(conf, cacheConf).withPath(TestHFileBlockIndex.fs, hfilePath).withFileContext(meta).create();
                Random rand = new Random(19231737);
                byte[] family = Bytes.toBytes("f");
                byte[] qualifier = Bytes.toBytes("q");
                for (int i = 0; i < (TestHFileBlockIndex.NUM_KV); ++i) {
                    byte[] row = RandomKeyValueUtil.randomOrderedKey(rand, i);
                    // Key will be interpreted by KeyValue.KEY_COMPARATOR
                    KeyValue kv = new KeyValue(row, family, qualifier, EnvironmentEdgeManager.currentTime(), RandomKeyValueUtil.randomValue(rand));
                    byte[] k = kv.getKey();
                    writer.append(kv);
                    keys[i] = k;
                    values[i] = CellUtil.cloneValue(kv);
                    keyStrSet.add(Bytes.toStringBinary(k));
                    if (i > 0) {
                        Assert.assertTrue(((PrivateCellUtil.compare(COMPARATOR, kv, keys[(i - 1)], 0, keys[(i - 1)].length)) > 0));
                    }
                }
                writer.close();
            }
            // Read the HFile
            HFile.Reader reader = HFile.createReader(TestHFileBlockIndex.fs, hfilePath, cacheConf, true, conf);
            Assert.assertEquals(expectedNumLevels, reader.getTrailer().getNumDataIndexLevels());
            Assert.assertTrue(Bytes.equals(keys[0], getKey()));
            Assert.assertTrue(Bytes.equals(keys[((TestHFileBlockIndex.NUM_KV) - 1)], getKey()));
            TestHFileBlockIndex.LOG.info(("Last key: " + (Bytes.toStringBinary(keys[((TestHFileBlockIndex.NUM_KV) - 1)]))));
            for (boolean pread : new boolean[]{ false, true }) {
                HFileScanner scanner = reader.getScanner(true, pread);
                for (int i = 0; i < (TestHFileBlockIndex.NUM_KV); ++i) {
                    checkSeekTo(keys, scanner, i);
                    checkKeyValue(("i=" + i), keys[i], values[i], ByteBuffer.wrap(getKey()), scanner.getValue());
                }
                Assert.assertTrue(scanner.seekTo());
                for (int i = (TestHFileBlockIndex.NUM_KV) - 1; i >= 0; --i) {
                    checkSeekTo(keys, scanner, i);
                    checkKeyValue(("i=" + i), keys[i], values[i], ByteBuffer.wrap(getKey()), scanner.getValue());
                }
            }
            // Manually compute the mid-key and validate it.
            HFile.Reader reader2 = reader;
            HFileBlock.FSReader fsReader = reader2.getUncachedBlockReader();
            HFileBlock.BlockIterator iter = fsReader.blockRange(0, reader.getTrailer().getLoadOnOpenDataOffset());
            HFileBlock block;
            List<byte[]> blockKeys = new ArrayList<>();
            while ((block = iter.nextBlock()) != null) {
                if ((block.getBlockType()) != (LEAF_INDEX))
                    return;

                ByteBuff b = block.getBufferReadOnly();
                int n = b.getIntAfterPosition(0);
                // One int for the number of items, and n + 1 for the secondary index.
                int entriesOffset = (Bytes.SIZEOF_INT) * (n + 2);
                // Get all the keys from the leaf index block. S
                for (int i = 0; i < n; ++i) {
                    int keyRelOffset = b.getIntAfterPosition(((Bytes.SIZEOF_INT) * (i + 1)));
                    int nextKeyRelOffset = b.getIntAfterPosition(((Bytes.SIZEOF_INT) * (i + 2)));
                    int keyLen = nextKeyRelOffset - keyRelOffset;
                    int keyOffset = (((b.arrayOffset()) + entriesOffset) + keyRelOffset) + (SECONDARY_INDEX_ENTRY_OVERHEAD);
                    byte[] blockKey = Arrays.copyOfRange(b.array(), keyOffset, (keyOffset + keyLen));
                    String blockKeyStr = Bytes.toString(blockKey);
                    blockKeys.add(blockKey);
                    // If the first key of the block is not among the keys written, we
                    // are not parsing the non-root index block format correctly.
                    Assert.assertTrue(("Invalid block key from leaf-level block: " + blockKeyStr), keyStrSet.contains(blockKeyStr));
                }
            } 
            // Validate the mid-key.
            Assert.assertEquals(Bytes.toStringBinary(blockKeys.get((((blockKeys.size()) - 1) / 2))), reader.midKey());
            Assert.assertEquals(TestHFileBlockIndex.UNCOMPRESSED_INDEX_SIZES[testI], reader.getTrailer().getUncompressedDataIndexSize());
            reader.close();
            reader2.close();
        }
    }

    @Test
    public void testIntermediateLevelIndicesWithLargeKeys() throws IOException {
        testIntermediateLevelIndicesWithLargeKeys(16);
    }

    @Test
    public void testIntermediateLevelIndicesWithLargeKeysWithMinNumEntries() throws IOException {
        // because of the large rowKeys, we will end up with a 50-level block index without sanity check
        testIntermediateLevelIndicesWithLargeKeys(2);
    }
}

