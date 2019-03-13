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
package org.apache.hadoop.hbase.io.encoding;


import BloomType.NONE;
import HFile.FORMAT_VERSION_KEY;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.io.hfile.BlockCacheFactory;
import org.apache.hadoop.hbase.io.hfile.LruBlockCache;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests encoded seekers by loading and reading values.
 */
@Category({ IOTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestEncodedSeekers {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEncodedSeekers.class);

    private static final String TABLE_NAME = "encodedSeekersTable";

    private static final String CF_NAME = "encodedSeekersCF";

    private static final byte[] CF_BYTES = Bytes.toBytes(TestEncodedSeekers.CF_NAME);

    private static final int MAX_VERSIONS = 5;

    private static final int BLOCK_SIZE = 64 * 1024;

    private static final int MIN_VALUE_SIZE = 30;

    private static final int MAX_VALUE_SIZE = 60;

    private static final int NUM_ROWS = 1003;

    private static final int NUM_COLS_PER_ROW = 20;

    private static final int NUM_HFILES = 4;

    private static final int NUM_ROWS_PER_FLUSH = (TestEncodedSeekers.NUM_ROWS) / (TestEncodedSeekers.NUM_HFILES);

    private final HBaseTestingUtility testUtil = HBaseTestingUtility.createLocalHTU();

    private final DataBlockEncoding encoding;

    private final boolean includeTags;

    private final boolean compressTags;

    /**
     * Enable when debugging
     */
    private static final boolean VERBOSE = false;

    public TestEncodedSeekers(DataBlockEncoding encoding, boolean includeTags, boolean compressTags) {
        this.encoding = encoding;
        this.includeTags = includeTags;
        this.compressTags = compressTags;
    }

    @Test
    public void testEncodedSeeker() throws IOException {
        System.err.println(((((("Testing encoded seekers for encoding : " + (encoding)) + ", includeTags : ") + (includeTags)) + ", compressTags : ") + (compressTags)));
        if (includeTags) {
            testUtil.getConfiguration().setInt(FORMAT_VERSION_KEY, 3);
        }
        LruBlockCache cache = ((LruBlockCache) (BlockCacheFactory.createBlockCache(testUtil.getConfiguration())));
        // Need to disable default row bloom filter for this test to pass.
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.newBuilder(TestEncodedSeekers.CF_BYTES).setMaxVersions(TestEncodedSeekers.MAX_VERSIONS).setDataBlockEncoding(encoding).setBlocksize(TestEncodedSeekers.BLOCK_SIZE).setBloomFilterType(NONE).setCompressTags(compressTags).build();
        HRegion region = testUtil.createTestRegion(TestEncodedSeekers.TABLE_NAME, cfd, cache);
        // write the data, but leave some in the memstore
        doPuts(region);
        // verify correctness when memstore contains data
        doGets(region);
        // verify correctness again after compacting
        region.compact(false);
        doGets(region);
        Map<DataBlockEncoding, Integer> encodingCounts = cache.getEncodingCountsForTest();
        // Ensure that compactions don't pollute the cache with unencoded blocks
        // in case of in-cache-only encoding.
        System.err.println(("encodingCounts=" + encodingCounts));
        Assert.assertEquals(1, encodingCounts.size());
        DataBlockEncoding encodingInCache = encodingCounts.keySet().iterator().next();
        Assert.assertEquals(encoding, encodingInCache);
        Assert.assertTrue(((encodingCounts.get(encodingInCache)) > 0));
    }
}

