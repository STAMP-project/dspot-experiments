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


import Compression.Algorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test various seek optimizations for correctness and check if they are
 * actually saving I/O operations.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, MediumTests.class })
public class TestSeekOptimizations {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSeekOptimizations.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSeekOptimizations.class);

    // Constants
    private static final String FAMILY = "myCF";

    private static final byte[] FAMILY_BYTES = Bytes.toBytes(TestSeekOptimizations.FAMILY);

    private static final int PUTS_PER_ROW_COL = 50;

    private static final int DELETES_PER_ROW_COL = 10;

    private static final int NUM_ROWS = 3;

    private static final int NUM_COLS = 3;

    private static final boolean VERBOSE = false;

    /**
     * Disable this when this test fails hopelessly and you need to debug a
     * simpler case.
     */
    private static final boolean USE_MANY_STORE_FILES = true;

    private static final int[][] COLUMN_SETS = new int[][]{ new int[]{  }// All columns
    // All columns
    // All columns
    , new int[]{ 0 }, new int[]{ 1 }, new int[]{ 0, 2 }, new int[]{ 1, 2 }, new int[]{ 0, 1, 2 } };

    // Both start row and end row are inclusive here for the purposes of this
    // test.
    private static final int[][] ROW_RANGES = new int[][]{ new int[]{ -1, -1 }, new int[]{ 0, 1 }, new int[]{ 1, 1 }, new int[]{ 1, 2 }, new int[]{ 0, 2 } };

    private static final int[] MAX_VERSIONS_VALUES = new int[]{ 1, 2 };

    // Instance variables
    private HRegion region;

    private Put put;

    private Delete del;

    private Random rand;

    private Set<Long> putTimestamps = new HashSet<>();

    private Set<Long> delTimestamps = new HashSet<>();

    private List<Cell> expectedKVs = new ArrayList<>();

    private Algorithm comprAlgo;

    private BloomType bloomType;

    private long totalSeekDiligent;

    private long totalSeekLazy;

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    public TestSeekOptimizations(Compression.Algorithm comprAlgo, BloomType bloomType) {
        this.comprAlgo = comprAlgo;
        this.bloomType = bloomType;
    }

    @Test
    public void testMultipleTimestampRanges() throws IOException {
        // enable seek counting
        StoreFileScanner.instrument();
        region = TestSeekOptimizations.TEST_UTIL.createTestRegion("testMultipleTimestampRanges", new HColumnDescriptor(TestSeekOptimizations.FAMILY).setCompressionType(comprAlgo).setBloomFilterType(bloomType).setMaxVersions(3));
        // Delete the given timestamp and everything before.
        final long latestDelTS = (TestSeekOptimizations.USE_MANY_STORE_FILES) ? 1397 : -1;
        createTimestampRange(1, 50, (-1));
        createTimestampRange(51, 100, (-1));
        if (TestSeekOptimizations.USE_MANY_STORE_FILES) {
            createTimestampRange(100, 500, 127);
            createTimestampRange(900, 1300, (-1));
            createTimestampRange(1301, 2500, latestDelTS);
            createTimestampRange(2502, 2598, (-1));
            createTimestampRange(2599, 2999, (-1));
        }
        prepareExpectedKVs(latestDelTS);
        for (int[] columnArr : TestSeekOptimizations.COLUMN_SETS) {
            for (int[] rowRange : TestSeekOptimizations.ROW_RANGES) {
                for (int maxVersions : TestSeekOptimizations.MAX_VERSIONS_VALUES) {
                    for (boolean lazySeekEnabled : new boolean[]{ false, true }) {
                        testScan(columnArr, lazySeekEnabled, rowRange[0], rowRange[1], maxVersions);
                    }
                }
            }
        }
        final double seekSavings = 1 - (((totalSeekLazy) * 1.0) / (totalSeekDiligent));
        System.err.println((((((((((((("For bloom=" + (bloomType)) + ", compr=") + (comprAlgo)) + " total seeks without optimization: ") + (totalSeekDiligent)) + ", with optimization: ") + (totalSeekLazy)) + " (") + (String.format("%.2f%%", (((totalSeekLazy) * 100.0) / (totalSeekDiligent))))) + "), savings: ") + (String.format("%.2f%%", (100.0 * seekSavings)))) + "\n"));
        // Test that lazy seeks are buying us something. Without the actual
        // implementation of the lazy seek optimization this will be 0.
        final double expectedSeekSavings = 0.0;
        Assert.assertTrue((((("Lazy seek is only saving " + (String.format("%.2f%%", (seekSavings * 100)))) + " seeks but should ") + "save at least ") + (String.format("%.2f%%", (expectedSeekSavings * 100)))), (seekSavings >= expectedSeekSavings));
    }
}

