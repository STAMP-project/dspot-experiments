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


import BloomFilterFactory.IO_STOREFILE_BLOOM_ENABLED;
import BloomFilterFactory.IO_STOREFILE_BLOOM_ERROR_RATE;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.BloomFilterUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BloomType.ROW;
import static BloomType.ROWCOL;


/**
 * Tests writing Bloom filter blocks in the same part of the file as data
 * blocks.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestCompoundBloomFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompoundBloomFilter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestCompoundBloomFilter.class);

    private static final int NUM_TESTS = 9;

    private static final BloomType[] BLOOM_TYPES = new BloomType[]{ ROW, ROW, ROWCOL, ROWCOL, ROW, ROWCOL, ROWCOL, ROWCOL, ROW };

    private static final int[] NUM_KV;

    static {
        final int N = 10000;// Only used in initialization.

        NUM_KV = new int[]{ 21870, N, N, N, N, 1000, N, 7500, 7500 };
        assert (TestCompoundBloomFilter.NUM_KV.length) == (TestCompoundBloomFilter.NUM_TESTS);
    }

    private static final int[] BLOCK_SIZES;

    static {
        final int blkSize = 65536;
        BLOCK_SIZES = new int[]{ 512, 1000, blkSize, blkSize, blkSize, 128, 300, blkSize, blkSize };
        assert (TestCompoundBloomFilter.BLOCK_SIZES.length) == (TestCompoundBloomFilter.NUM_TESTS);
    }

    /**
     * Be careful not to specify too high a Bloom filter block size, otherwise
     * there will only be one oversized chunk and the observed false positive
     * rate will be too low.
     */
    private static final int[] BLOOM_BLOCK_SIZES = new int[]{ 1000, 4096, 4096, 4096, 8192, 128, 1024, 600, 600 };

    static {
        assert (TestCompoundBloomFilter.BLOOM_BLOCK_SIZES.length) == (TestCompoundBloomFilter.NUM_TESTS);
    }

    private static final double[] TARGET_ERROR_RATES = new double[]{ 0.025, 0.01, 0.015, 0.01, 0.03, 0.01, 0.01, 0.07, 0.07 };

    static {
        assert (TestCompoundBloomFilter.TARGET_ERROR_RATES.length) == (TestCompoundBloomFilter.NUM_TESTS);
    }

    /**
     * A false positive rate that is obviously too high.
     */
    private static final double TOO_HIGH_ERROR_RATE;

    static {
        double m = 0;
        for (double errorRate : TestCompoundBloomFilter.TARGET_ERROR_RATES)
            m = Math.max(m, errorRate);

        TOO_HIGH_ERROR_RATE = m + 0.03;
    }

    private static Configuration conf;

    private static CacheConfig cacheConf;

    private FileSystem fs;

    /**
     * A message of the form "in test#&lt;number>:" to include in logging.
     */
    private String testIdMsg;

    private static final int GENERATION_SEED = 2319;

    private static final int EVALUATION_SEED = 135;

    private BlockCache blockCache;

    @Test
    public void testCompoundBloomFilter() throws IOException {
        TestCompoundBloomFilter.conf.setBoolean(IO_STOREFILE_BLOOM_ENABLED, true);
        for (int t = 0; t < (TestCompoundBloomFilter.NUM_TESTS); ++t) {
            TestCompoundBloomFilter.conf.setFloat(IO_STOREFILE_BLOOM_ERROR_RATE, ((float) (TestCompoundBloomFilter.TARGET_ERROR_RATES[t])));
            testIdMsg = ("in test #" + t) + ":";
            Random generationRand = new Random(TestCompoundBloomFilter.GENERATION_SEED);
            List<KeyValue> kvs = createSortedKeyValues(generationRand, TestCompoundBloomFilter.NUM_KV[t]);
            BloomType bt = TestCompoundBloomFilter.BLOOM_TYPES[t];
            Path sfPath = writeStoreFile(t, bt, kvs);
            readStoreFile(t, bt, kvs, sfPath);
        }
    }

    @Test
    public void testCompoundBloomSizing() {
        int bloomBlockByteSize = 4096;
        int bloomBlockBitSize = bloomBlockByteSize * 8;
        double targetErrorRate = 0.01;
        long maxKeysPerChunk = BloomFilterUtil.idealMaxKeys(bloomBlockBitSize, targetErrorRate);
        long bloomSize1 = bloomBlockByteSize * 8;
        long bloomSize2 = BloomFilterUtil.computeBitSize(maxKeysPerChunk, targetErrorRate);
        double bloomSizeRatio = (bloomSize2 * 1.0) / bloomSize1;
        Assert.assertTrue(((Math.abs((bloomSizeRatio - 0.9999))) < 1.0E-4));
    }

    @Test
    public void testCreateKey() {
        byte[] row = Bytes.toBytes("myRow");
        byte[] qualifier = Bytes.toBytes("myQualifier");
        // Mimic what Storefile.createBloomKeyValue() does
        byte[] rowKey = KeyValueUtil.createFirstOnRow(row, 0, row.length, new byte[0], 0, 0, row, 0, 0).getKey();
        byte[] rowColKey = KeyValueUtil.createFirstOnRow(row, 0, row.length, new byte[0], 0, 0, qualifier, 0, qualifier.length).getKey();
        KeyValue rowKV = KeyValueUtil.createKeyValueFromKey(rowKey);
        KeyValue rowColKV = KeyValueUtil.createKeyValueFromKey(rowColKey);
        Assert.assertEquals(rowKV.getTimestamp(), rowColKV.getTimestamp());
        Assert.assertEquals(Bytes.toStringBinary(rowKV.getRowArray(), rowKV.getRowOffset(), rowKV.getRowLength()), Bytes.toStringBinary(rowColKV.getRowArray(), rowColKV.getRowOffset(), rowColKV.getRowLength()));
        Assert.assertEquals(0, rowKV.getQualifierLength());
    }
}

