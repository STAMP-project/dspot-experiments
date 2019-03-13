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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ChecksumType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BloomType.ROWPREFIX_FIXED_LENGTH;


/**
 * Test TestRowPrefixBloomFilter
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestRowPrefixBloomFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRowPrefixBloomFilter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRowPrefixBloomFilter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private CacheConfig cacheConf = new CacheConfig(TestRowPrefixBloomFilter.TEST_UTIL.getConfiguration());

    private static final ChecksumType CKTYPE = ChecksumType.CRC32C;

    private static final int CKBYTES = 512;

    private boolean localfs = false;

    private static Configuration conf;

    private static FileSystem fs;

    private static Path testDir;

    private static final int BLOCKSIZE_SMALL = 8192;

    private static final float err = ((float) (0.01));

    private static final int prefixLength = 10;

    private static final String invalidFormatter = "%08d";

    private static final String prefixFormatter = "%010d";

    private static final String suffixFormatter = "%010d";

    private static final int prefixRowCount = 50;

    private static final int suffixRowCount = 10;

    private static final int fixedLengthExpKeys = TestRowPrefixBloomFilter.prefixRowCount;

    private static final BloomType bt = ROWPREFIX_FIXED_LENGTH;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRowPrefixBloomFilter() throws Exception {
        FileSystem fs = FileSystem.getLocal(TestRowPrefixBloomFilter.conf);
        float expErr = ((2 * (TestRowPrefixBloomFilter.prefixRowCount)) * (TestRowPrefixBloomFilter.suffixRowCount)) * (TestRowPrefixBloomFilter.err);
        int expKeys = TestRowPrefixBloomFilter.fixedLengthExpKeys;
        // write the file
        Path f = new Path(TestRowPrefixBloomFilter.testDir, name.getMethodName());
        writeStoreFile(f, TestRowPrefixBloomFilter.bt, expKeys);
        // read the file
        StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, TestRowPrefixBloomFilter.conf);
        reader.loadFileInfo();
        reader.loadBloomfilter();
        // check basic param
        Assert.assertEquals(TestRowPrefixBloomFilter.bt, reader.getBloomFilterType());
        Assert.assertEquals(TestRowPrefixBloomFilter.prefixLength, reader.getPrefixLength());
        Assert.assertEquals(expKeys, reader.getGeneralBloomFilter().getKeyCount());
        StoreFileScanner scanner = TestRowPrefixBloomFilter.getStoreFileScanner(reader);
        HStore store = Mockito.mock(HStore.class);
        Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(ColumnFamilyDescriptorBuilder.of("family"));
        // check false positives rate
        int falsePos = 0;
        int falseNeg = 0;
        for (int i = 0; i < (TestRowPrefixBloomFilter.prefixRowCount); i++) {
            // prefix rows
            String prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, i);
            for (int j = 0; j < (TestRowPrefixBloomFilter.suffixRowCount); j++) {
                // suffix rows
                String startRow = generateRowWithSuffix(prefixRow, j);
                String stopRow = generateRowWithSuffix(prefixRow, (j + 1));
                Scan scan = new Scan().withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));
                boolean exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
                boolean shouldPrefixRowExist = (i % 2) == 0;
                if (shouldPrefixRowExist) {
                    if (!exists) {
                        falseNeg++;
                    }
                } else {
                    if (exists) {
                        falsePos++;
                    }
                }
            }
        }
        for (int i = TestRowPrefixBloomFilter.prefixRowCount; i < ((TestRowPrefixBloomFilter.prefixRowCount) * 2); i++) {
            // prefix rows
            String row = String.format(TestRowPrefixBloomFilter.invalidFormatter, i);
            Scan scan = new Scan(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(row)));
            boolean exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
            boolean shouldPrefixRowExist = (i % 2) == 0;
            if (shouldPrefixRowExist) {
                if (!exists) {
                    falseNeg++;
                }
            } else {
                if (exists) {
                    falsePos++;
                }
            }
        }
        reader.close(true);// evict because we are about to delete the file

        fs.delete(f, true);
        Assert.assertEquals(("False negatives: " + falseNeg), 0, falseNeg);
        int maxFalsePos = ((int) (2 * expErr));
        Assert.assertTrue((((((("Too many false positives: " + falsePos) + " (err=") + (TestRowPrefixBloomFilter.err)) + ", expected no more than ") + maxFalsePos) + ")"), (falsePos <= maxFalsePos));
    }

    @Test
    public void testRowPrefixBloomFilterWithGet() throws Exception {
        FileSystem fs = FileSystem.getLocal(TestRowPrefixBloomFilter.conf);
        int expKeys = TestRowPrefixBloomFilter.fixedLengthExpKeys;
        // write the file
        Path f = new Path(TestRowPrefixBloomFilter.testDir, name.getMethodName());
        writeStoreFile(f, TestRowPrefixBloomFilter.bt, expKeys);
        StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, TestRowPrefixBloomFilter.conf);
        reader.loadFileInfo();
        reader.loadBloomfilter();
        StoreFileScanner scanner = TestRowPrefixBloomFilter.getStoreFileScanner(reader);
        HStore store = Mockito.mock(HStore.class);
        Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(ColumnFamilyDescriptorBuilder.of("family"));
        // Get with valid row style
        // prefix row in bloom
        String prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 2));
        String row = generateRowWithSuffix(prefixRow, 0);
        Scan scan = new Scan(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(row)));
        boolean exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertTrue(exists);
        // prefix row not in bloom
        prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 1));
        row = generateRowWithSuffix(prefixRow, 0);
        scan = new Scan(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(row)));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertFalse(exists);
        // Get with invalid row style
        // ROWPREFIX: the length of row is less than prefixLength
        // row in bloom
        row = String.format(TestRowPrefixBloomFilter.invalidFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) + 2));
        scan = new Scan(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(row)));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertTrue(exists);
        // row not in bloom
        row = String.format(TestRowPrefixBloomFilter.invalidFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) + 1));
        scan = new Scan(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(row)));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertFalse(exists);
        reader.close(true);// evict because we are about to delete the file

        fs.delete(f, true);
    }

    @Test
    public void testRowPrefixBloomFilterWithScan() throws Exception {
        FileSystem fs = FileSystem.getLocal(TestRowPrefixBloomFilter.conf);
        int expKeys = TestRowPrefixBloomFilter.fixedLengthExpKeys;
        // write the file
        Path f = new Path(TestRowPrefixBloomFilter.testDir, name.getMethodName());
        writeStoreFile(f, TestRowPrefixBloomFilter.bt, expKeys);
        StoreFileReader reader = new StoreFileReader(fs, f, cacheConf, true, new AtomicInteger(0), true, TestRowPrefixBloomFilter.conf);
        reader.loadFileInfo();
        reader.loadBloomfilter();
        StoreFileScanner scanner = TestRowPrefixBloomFilter.getStoreFileScanner(reader);
        HStore store = Mockito.mock(HStore.class);
        Mockito.when(store.getColumnFamilyDescriptor()).thenReturn(ColumnFamilyDescriptorBuilder.of("family"));
        // Scan with valid row style. startRow and stopRow have a common prefix.
        // And the length of the common prefix is no less than prefixLength.
        // prefix row in bloom
        String prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 2));
        String startRow = generateRowWithSuffix(prefixRow, 0);
        String stopRow = generateRowWithSuffix(prefixRow, 1);
        Scan scan = new Scan().withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));
        boolean exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertTrue(exists);
        // prefix row not in bloom
        prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 1));
        startRow = generateRowWithSuffix(prefixRow, 0);
        stopRow = generateRowWithSuffix(prefixRow, 1);
        scan = new Scan().withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertFalse(exists);
        // There is no common prefix between startRow and stopRow.
        prefixRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 2));
        startRow = generateRowWithSuffix(prefixRow, 0);
        scan = new Scan().withStartRow(Bytes.toBytes(startRow));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertTrue(exists);
        // startRow and stopRow have a common prefix.
        // But the length of the common prefix is less than prefixLength.
        String prefixStartRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 2));
        String prefixStopRow = String.format(TestRowPrefixBloomFilter.prefixFormatter, ((TestRowPrefixBloomFilter.prefixRowCount) - 1));
        startRow = generateRowWithSuffix(prefixStartRow, 0);
        stopRow = generateRowWithSuffix(prefixStopRow, 0);
        scan = new Scan().withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));
        exists = scanner.shouldUseScanner(scan, store, Long.MIN_VALUE);
        Assert.assertTrue(exists);
        reader.close(true);// evict because we are about to delete the file

        fs.delete(f, true);
    }
}

