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
package org.apache.hadoop.hbase.regionserver;


import HFile.DEFAULT_COMPRESSION_ALGORITHM;
import KeyValue.Type;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.io.hfile.BlockType;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests {@link HFile} cache-on-write functionality for data blocks, non-root
 * index blocks, and Bloom filter blocks, as specified by the column family.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, MediumTests.class })
public class TestCacheOnWriteInSchema {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCacheOnWriteInSchema.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCacheOnWriteInSchema.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static final String DIR = getDataTestDir("TestCacheOnWriteInSchema").toString();

    private static byte[] table;

    private static byte[] family = Bytes.toBytes("family");

    private static final int NUM_KV = 25000;

    private static final Random rand = new Random(12983177L);

    /**
     * The number of valid key types possible in a store file
     */
    private static final int NUM_VALID_KEY_TYPES = (Type.values().length) - 2;

    private static enum CacheOnWriteType {

        DATA_BLOCKS(BlockType.DATA, BlockType.ENCODED_DATA),
        BLOOM_BLOCKS(BlockType.BLOOM_CHUNK),
        INDEX_BLOCKS(BlockType.LEAF_INDEX, BlockType.INTERMEDIATE_INDEX);
        private final BlockType blockType1;

        private final BlockType blockType2;

        private CacheOnWriteType(BlockType blockType) {
            this(blockType, blockType);
        }

        private CacheOnWriteType(BlockType blockType1, BlockType blockType2) {
            this.blockType1 = blockType1;
            this.blockType2 = blockType2;
        }

        public boolean shouldBeCached(BlockType blockType) {
            return (blockType == (blockType1)) || (blockType == (blockType2));
        }

        public ColumnFamilyDescriptorBuilder modifyFamilySchema(ColumnFamilyDescriptorBuilder builder) {
            switch (this) {
                case DATA_BLOCKS :
                    builder.setCacheDataOnWrite(true);
                    break;
                case BLOOM_BLOCKS :
                    builder.setCacheBloomsOnWrite(true);
                    break;
                case INDEX_BLOCKS :
                    builder.setCacheIndexesOnWrite(true);
                    break;
            }
            return builder;
        }
    }

    private final TestCacheOnWriteInSchema.CacheOnWriteType cowType;

    private Configuration conf;

    private final String testDescription;

    private HRegion region;

    private HStore store;

    private WALFactory walFactory;

    private FileSystem fs;

    public TestCacheOnWriteInSchema(TestCacheOnWriteInSchema.CacheOnWriteType cowType) {
        this.cowType = cowType;
        testDescription = ("[cacheOnWrite=" + cowType) + "]";
        System.out.println(testDescription);
    }

    @Test
    public void testCacheOnWriteInSchema() throws IOException {
        // Write some random data into the store
        StoreFileWriter writer = store.createWriterInTmp(Integer.MAX_VALUE, DEFAULT_COMPRESSION_ALGORITHM, false, true, false, false);
        writeStoreFile(writer);
        writer.close();
        // Verify the block types of interest were cached on write
        readStoreFile(writer.getPath());
    }
}

