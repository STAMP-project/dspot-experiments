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


import Compression.Algorithm;
import KeyValue.Type;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BlockType.BLOOM_CHUNK;
import static BlockType.DATA;
import static BlockType.ENCODED_DATA;
import static BlockType.INTERMEDIATE_INDEX;
import static BlockType.LEAF_INDEX;
import static CacheConfig.CACHE_BLOCKS_ON_WRITE_KEY;
import static CacheConfig.CACHE_BLOOM_BLOCKS_ON_WRITE_KEY;
import static CacheConfig.CACHE_INDEX_BLOCKS_ON_WRITE_KEY;


/**
 * Tests {@link HFile} cache-on-write functionality for the following block
 * types: data blocks, non-root index blocks, and Bloom filter blocks.
 */
@RunWith(Parameterized.class)
@Category({ IOTests.class, LargeTests.class })
public class TestCacheOnWrite {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCacheOnWrite.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCacheOnWrite.class);

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private Configuration conf;

    private CacheConfig cacheConf;

    private FileSystem fs;

    private Random rand = new Random(12983177L);

    private Path storeFilePath;

    private BlockCache blockCache;

    private String testDescription;

    private final TestCacheOnWrite.CacheOnWriteType cowType;

    private final Algorithm compress;

    private final boolean cacheCompressedData;

    private static final int DATA_BLOCK_SIZE = 2048;

    private static final int NUM_KV = 25000;

    private static final int INDEX_BLOCK_SIZE = 512;

    private static final int BLOOM_BLOCK_SIZE = 4096;

    private static final BloomType BLOOM_TYPE = BloomType.ROWCOL;

    private static final int CKBYTES = 512;

    /**
     * The number of valid key types possible in a store file
     */
    private static final int NUM_VALID_KEY_TYPES = (Type.values().length) - 2;

    private static enum CacheOnWriteType {

        DATA_BLOCKS(CACHE_BLOCKS_ON_WRITE_KEY, DATA, ENCODED_DATA),
        BLOOM_BLOCKS(CACHE_BLOOM_BLOCKS_ON_WRITE_KEY, BLOOM_CHUNK),
        INDEX_BLOCKS(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, LEAF_INDEX, INTERMEDIATE_INDEX);
        private final String confKey;

        private final BlockType blockType1;

        private final BlockType blockType2;

        private CacheOnWriteType(String confKey, BlockType blockType) {
            this(confKey, blockType, blockType);
        }

        private CacheOnWriteType(String confKey, BlockType blockType1, BlockType blockType2) {
            this.blockType1 = blockType1;
            this.blockType2 = blockType2;
            this.confKey = confKey;
        }

        public boolean shouldBeCached(BlockType blockType) {
            return (blockType == (blockType1)) || (blockType == (blockType2));
        }

        public void modifyConf(Configuration conf) {
            for (TestCacheOnWrite.CacheOnWriteType cowType : TestCacheOnWrite.CacheOnWriteType.values()) {
                conf.setBoolean(cowType.confKey, (cowType == (this)));
            }
        }
    }

    public TestCacheOnWrite(TestCacheOnWrite.CacheOnWriteType cowType, Compression.Algorithm compress, boolean cacheCompressedData, BlockCache blockCache) {
        this.cowType = cowType;
        this.compress = compress;
        this.cacheCompressedData = cacheCompressedData;
        this.blockCache = blockCache;
        testDescription = ((((("[cacheOnWrite=" + cowType) + ", compress=") + compress) + ", cacheCompressedData=") + cacheCompressedData) + "]";
        TestCacheOnWrite.LOG.info(testDescription);
    }

    @Test
    public void testStoreFileCacheOnWrite() throws IOException {
        testStoreFileCacheOnWriteInternals(false);
        testStoreFileCacheOnWriteInternals(true);
    }

    @Test
    public void testNotCachingDataBlocksDuringCompaction() throws IOException, InterruptedException {
        testNotCachingDataBlocksDuringCompactionInternals(false);
        testNotCachingDataBlocksDuringCompactionInternals(true);
    }
}

