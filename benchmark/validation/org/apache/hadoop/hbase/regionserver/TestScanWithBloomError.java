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


import Compression.Algorithm.GZ;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test a multi-column scanner when there is a Bloom filter false-positive.
 * This is needed for the multi-column Bloom filter optimization.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestScanWithBloomError {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScanWithBloomError.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScanWithBloomError.class);

    private static final String TABLE_NAME = "ScanWithBloomError";

    private static final String FAMILY = "myCF";

    private static final byte[] FAMILY_BYTES = Bytes.toBytes(TestScanWithBloomError.FAMILY);

    private static final String ROW = "theRow";

    private static final String QUALIFIER_PREFIX = "qual";

    private static final byte[] ROW_BYTES = Bytes.toBytes(TestScanWithBloomError.ROW);

    private static NavigableSet<Integer> allColIds = new TreeSet<>();

    private HRegion region;

    private BloomType bloomType;

    private FileSystem fs;

    private Configuration conf;

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    public TestScanWithBloomError(BloomType bloomType) {
        this.bloomType = bloomType;
    }

    @Test
    public void testThreeStoreFiles() throws IOException {
        region = TestScanWithBloomError.TEST_UTIL.createTestRegion(TestScanWithBloomError.TABLE_NAME, new HColumnDescriptor(TestScanWithBloomError.FAMILY).setCompressionType(GZ).setBloomFilterType(bloomType).setMaxVersions(TestMultiColumnScanner.MAX_VERSIONS));
        createStoreFile(new int[]{ 1, 2, 6 });
        createStoreFile(new int[]{ 1, 2, 3, 7 });
        createStoreFile(new int[]{ 1, 9 });
        scanColSet(new int[]{ 1, 4, 6, 7 }, new int[]{ 1, 6, 7 });
        HBaseTestingUtility.closeRegionAndWAL(region);
    }
}

