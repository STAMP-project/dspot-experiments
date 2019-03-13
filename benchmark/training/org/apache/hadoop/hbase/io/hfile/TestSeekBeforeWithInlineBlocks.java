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


import BloomFilterFactory.IO_STOREFILE_BLOOM_BLOCK_SIZE;
import BloomFilterUtil.PREFIX_LENGTH_KEY;
import HConstants.HFILE_BLOCK_CACHE_SIZE_KEY;
import HFile.FORMAT_VERSION_KEY;
import HFile.Reader;
import HFileBlockIndex.MAX_CHUNK_SIZE_KEY;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.fs.HFileSystem;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.regionserver.StoreFileWriter;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HFile.MAX_FORMAT_VERSION;
import static HFile.MIN_FORMAT_VERSION_WITH_TAGS;


@Category({ IOTests.class, MediumTests.class })
public class TestSeekBeforeWithInlineBlocks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSeekBeforeWithInlineBlocks.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSeekBeforeWithInlineBlocks.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int NUM_KV = 10000;

    private static final int DATA_BLOCK_SIZE = 4096;

    private static final int BLOOM_BLOCK_SIZE = 1024;

    private static final int[] INDEX_CHUNK_SIZES = new int[]{ 65536, 4096, 1024 };

    private static final int[] EXPECTED_NUM_LEVELS = new int[]{ 1, 2, 3 };

    private static final Random RAND = new Random(192537);

    private static final byte[] FAM = Bytes.toBytes("family");

    private FileSystem fs;

    private Configuration conf;

    /**
     * Scanner.seekBefore() could fail because when seeking to a previous HFile data block, it needs
     * to know the size of that data block, which it calculates using current data block offset and
     * the previous data block offset.  This fails to work when there are leaf-level index blocks in
     * the scannable section of the HFile, i.e. starting in HFileV2.  This test will try seekBefore()
     * on a flat (single-level) and multi-level (2,3) HFile and confirm this bug is now fixed.  This
     * bug also happens for inline Bloom blocks for the same reasons.
     */
    @Test
    public void testMultiIndexLevelRandomHFileWithBlooms() throws IOException {
        conf = TestSeekBeforeWithInlineBlocks.TEST_UTIL.getConfiguration();
        TestSeekBeforeWithInlineBlocks.TEST_UTIL.getConfiguration().setInt(PREFIX_LENGTH_KEY, 10);
        // Try out different HFile versions to ensure reverse scan works on each version
        for (int hfileVersion = MIN_FORMAT_VERSION_WITH_TAGS; hfileVersion <= (MAX_FORMAT_VERSION); hfileVersion++) {
            conf.setInt(FORMAT_VERSION_KEY, hfileVersion);
            fs = HFileSystem.get(conf);
            // Try out different bloom types because inline Bloom blocks break seekBefore()
            for (BloomType bloomType : BloomType.values()) {
                // Test out HFile block indices of various sizes/levels
                for (int testI = 0; testI < (TestSeekBeforeWithInlineBlocks.INDEX_CHUNK_SIZES.length); testI++) {
                    int indexBlockSize = TestSeekBeforeWithInlineBlocks.INDEX_CHUNK_SIZES[testI];
                    int expectedNumLevels = TestSeekBeforeWithInlineBlocks.EXPECTED_NUM_LEVELS[testI];
                    TestSeekBeforeWithInlineBlocks.LOG.info(String.format("Testing HFileVersion: %s, BloomType: %s, Index Levels: %s", hfileVersion, bloomType, expectedNumLevels));
                    conf.setInt(MAX_CHUNK_SIZE_KEY, indexBlockSize);
                    conf.setInt(IO_STOREFILE_BLOOM_BLOCK_SIZE, TestSeekBeforeWithInlineBlocks.BLOOM_BLOCK_SIZE);
                    conf.setInt(PREFIX_LENGTH_KEY, 10);
                    Cell[] cells = new Cell[TestSeekBeforeWithInlineBlocks.NUM_KV];
                    Path hfilePath = new Path(getDataTestDir(), String.format("testMultiIndexLevelRandomHFileWithBlooms-%s-%s-%s", hfileVersion, bloomType, testI));
                    // Disable caching to prevent it from hiding any bugs in block seeks/reads
                    conf.setFloat(HFILE_BLOCK_CACHE_SIZE_KEY, 0.0F);
                    CacheConfig cacheConf = new CacheConfig(conf);
                    // Write the HFile
                    {
                        HFileContext meta = new HFileContextBuilder().withBlockSize(TestSeekBeforeWithInlineBlocks.DATA_BLOCK_SIZE).build();
                        StoreFileWriter storeFileWriter = new StoreFileWriter.Builder(conf, cacheConf, fs).withFilePath(hfilePath).withFileContext(meta).withBloomType(bloomType).build();
                        for (int i = 0; i < (TestSeekBeforeWithInlineBlocks.NUM_KV); i++) {
                            byte[] row = RandomKeyValueUtil.randomOrderedKey(TestSeekBeforeWithInlineBlocks.RAND, i);
                            byte[] qual = RandomKeyValueUtil.randomRowOrQualifier(TestSeekBeforeWithInlineBlocks.RAND);
                            byte[] value = RandomKeyValueUtil.randomValue(TestSeekBeforeWithInlineBlocks.RAND);
                            KeyValue kv = new KeyValue(row, TestSeekBeforeWithInlineBlocks.FAM, qual, value);
                            storeFileWriter.append(kv);
                            cells[i] = kv;
                        }
                        storeFileWriter.close();
                    }
                    // Read the HFile
                    HFile.Reader reader = HFile.createReader(fs, hfilePath, cacheConf, true, conf);
                    // Sanity check the HFile index level
                    Assert.assertEquals(expectedNumLevels, reader.getTrailer().getNumDataIndexLevels());
                    // Check that we can seekBefore in either direction and with both pread
                    // enabled and disabled
                    for (boolean pread : new boolean[]{ false, true }) {
                        HFileScanner scanner = reader.getScanner(true, pread);
                        checkNoSeekBefore(cells, scanner, 0);
                        for (int i = 1; i < (TestSeekBeforeWithInlineBlocks.NUM_KV); i++) {
                            checkSeekBefore(cells, scanner, i);
                            checkCell(cells[(i - 1)], scanner.getCell());
                        }
                        Assert.assertTrue(scanner.seekTo());
                        for (int i = (TestSeekBeforeWithInlineBlocks.NUM_KV) - 1; i >= 1; i--) {
                            checkSeekBefore(cells, scanner, i);
                            checkCell(cells[(i - 1)], scanner.getCell());
                        }
                        checkNoSeekBefore(cells, scanner, 0);
                        scanner.close();
                    }
                    reader.close();
                }
            }
        }
    }
}

