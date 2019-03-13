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
package org.apache.hadoop.hbase;


import Bytes.BYTES_COMPARATOR;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.LoadTestKVGenerator;
import org.apache.hadoop.hbase.util.MultiThreadedWriter;
import org.apache.hadoop.hbase.util.test.LoadTestDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CompareOperator.EQUAL;


/**
 * Integration test that verifies lazy CF loading during scans by doing repeated scans
 * with this feature while multiple threads are continuously writing values; and
 * verifying the result.
 */
@Category(IntegrationTests.class)
public class IntegrationTestLazyCfLoading {
    private static final TableName TABLE_NAME = TableName.valueOf(IntegrationTestLazyCfLoading.class.getSimpleName());

    private static final String TIMEOUT_KEY = "hbase.%s.timeout";

    private static final String ENCODING_KEY = "hbase.%s.datablock.encoding";

    /**
     * A soft test timeout; duration of the test, as such, depends on number of keys to put.
     */
    private static final int DEFAULT_TIMEOUT_MINUTES = 10;

    private static final int NUM_SERVERS = 1;

    /**
     * Set regions per server low to ensure splits happen during test
     */
    private static final int REGIONS_PER_SERVER = 3;

    private static final int KEYS_TO_WRITE_PER_SERVER = 20000;

    private static final int WRITER_THREADS = 10;

    private static final int WAIT_BETWEEN_SCANS_MS = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestLazyCfLoading.class);

    private IntegrationTestingUtility util = new IntegrationTestingUtility();

    private final IntegrationTestLazyCfLoading.DataGenerator dataGen = new IntegrationTestLazyCfLoading.DataGenerator();

    /**
     * Custom LoadTestDataGenerator. Uses key generation and verification from
     * LoadTestKVGenerator. Creates 3 column families; one with an integer column to
     * filter on, the 2nd one with an integer column that matches the first integer column (for
     * test-specific verification), and byte[] value that is used for general verification; and
     * the third one with just the value.
     */
    private static class DataGenerator extends LoadTestDataGenerator {
        private static final int MIN_DATA_SIZE = 4096;

        private static final int MAX_DATA_SIZE = 65536;

        public static final byte[] ESSENTIAL_CF = Bytes.toBytes("essential");

        public static final byte[] JOINED_CF1 = Bytes.toBytes("joined");

        public static final byte[] JOINED_CF2 = Bytes.toBytes("joined2");

        public static final byte[] FILTER_COLUMN = Bytes.toBytes("filter");

        public static final byte[] VALUE_COLUMN = Bytes.toBytes("val");

        public static final long ACCEPTED_VALUE = 1L;

        private static final Map<byte[], byte[][]> columnMap = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);

        private final AtomicLong expectedNumberOfKeys = new AtomicLong(0);

        private final AtomicLong totalNumberOfKeys = new AtomicLong(0);

        public DataGenerator() {
            super(IntegrationTestLazyCfLoading.DataGenerator.MIN_DATA_SIZE, IntegrationTestLazyCfLoading.DataGenerator.MAX_DATA_SIZE);
            IntegrationTestLazyCfLoading.DataGenerator.columnMap.put(IntegrationTestLazyCfLoading.DataGenerator.ESSENTIAL_CF, new byte[][]{ IntegrationTestLazyCfLoading.DataGenerator.FILTER_COLUMN });
            IntegrationTestLazyCfLoading.DataGenerator.columnMap.put(IntegrationTestLazyCfLoading.DataGenerator.JOINED_CF1, new byte[][]{ IntegrationTestLazyCfLoading.DataGenerator.FILTER_COLUMN, IntegrationTestLazyCfLoading.DataGenerator.VALUE_COLUMN });
            IntegrationTestLazyCfLoading.DataGenerator.columnMap.put(IntegrationTestLazyCfLoading.DataGenerator.JOINED_CF2, new byte[][]{ IntegrationTestLazyCfLoading.DataGenerator.VALUE_COLUMN });
        }

        public long getExpectedNumberOfKeys() {
            return expectedNumberOfKeys.get();
        }

        public long getTotalNumberOfKeys() {
            return totalNumberOfKeys.get();
        }

        @Override
        public byte[] getDeterministicUniqueKey(long keyBase) {
            return Bytes.toBytes(LoadTestKVGenerator.md5PrefixedKey(keyBase));
        }

        @Override
        public byte[][] getColumnFamilies() {
            return IntegrationTestLazyCfLoading.DataGenerator.columnMap.keySet().toArray(new byte[IntegrationTestLazyCfLoading.DataGenerator.columnMap.size()][]);
        }

        @Override
        public byte[][] generateColumnsForCf(byte[] rowKey, byte[] cf) {
            return IntegrationTestLazyCfLoading.DataGenerator.columnMap.get(cf);
        }

        @Override
        public byte[] generateValue(byte[] rowKey, byte[] cf, byte[] column) {
            if ((BYTES_COMPARATOR.compare(column, IntegrationTestLazyCfLoading.DataGenerator.FILTER_COLUMN)) == 0) {
                // Random deterministic way to make some values "on" and others "off" for filters.
                long value = (Long.parseLong(Bytes.toString(rowKey, 0, 4), 16)) & (IntegrationTestLazyCfLoading.DataGenerator.ACCEPTED_VALUE);
                if ((BYTES_COMPARATOR.compare(cf, IntegrationTestLazyCfLoading.DataGenerator.ESSENTIAL_CF)) == 0) {
                    totalNumberOfKeys.incrementAndGet();
                    if (value == (IntegrationTestLazyCfLoading.DataGenerator.ACCEPTED_VALUE)) {
                        expectedNumberOfKeys.incrementAndGet();
                    }
                }
                return Bytes.toBytes(value);
            } else
                if ((BYTES_COMPARATOR.compare(column, IntegrationTestLazyCfLoading.DataGenerator.VALUE_COLUMN)) == 0) {
                    return kvGenerator.generateRandomSizeValue(rowKey, cf, column);
                }

            String error = "Unknown column " + (Bytes.toString(column));
            assert false : error;
            throw new InvalidParameterException(error);
        }

        @Override
        public boolean verify(byte[] rowKey, byte[] cf, byte[] column, byte[] value) {
            if ((BYTES_COMPARATOR.compare(column, IntegrationTestLazyCfLoading.DataGenerator.FILTER_COLUMN)) == 0) {
                // Relies on the filter from getScanFilter being used.
                return (Bytes.toLong(value)) == (IntegrationTestLazyCfLoading.DataGenerator.ACCEPTED_VALUE);
            } else
                if ((BYTES_COMPARATOR.compare(column, IntegrationTestLazyCfLoading.DataGenerator.VALUE_COLUMN)) == 0) {
                    return LoadTestKVGenerator.verify(value, rowKey, cf, column);
                }

            return false;// some bogus value from read, we don't expect any such thing.

        }

        @Override
        public boolean verify(byte[] rowKey, byte[] cf, Set<byte[]> columnSet) {
            return (IntegrationTestLazyCfLoading.DataGenerator.columnMap.get(cf).length) == (columnSet.size());
        }

        public Filter getScanFilter() {
            SingleColumnValueFilter scf = new SingleColumnValueFilter(IntegrationTestLazyCfLoading.DataGenerator.ESSENTIAL_CF, IntegrationTestLazyCfLoading.DataGenerator.FILTER_COLUMN, EQUAL, Bytes.toBytes(IntegrationTestLazyCfLoading.DataGenerator.ACCEPTED_VALUE));
            scf.setFilterIfMissing(true);
            return scf;
        }
    }

    @Test
    public void testReadersAndWriters() throws Exception {
        Configuration conf = getConfiguration();
        String timeoutKey = String.format(IntegrationTestLazyCfLoading.TIMEOUT_KEY, this.getClass().getSimpleName());
        long maxRuntime = conf.getLong(timeoutKey, IntegrationTestLazyCfLoading.DEFAULT_TIMEOUT_MINUTES);
        long serverCount = getHBaseClusterInterface().getClusterMetrics().getLiveServerMetrics().size();
        long keysToWrite = serverCount * (IntegrationTestLazyCfLoading.KEYS_TO_WRITE_PER_SERVER);
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(IntegrationTestLazyCfLoading.TABLE_NAME);
        // Create multi-threaded writer and start it. We write multiple columns/CFs and verify
        // their integrity, therefore multi-put is necessary.
        MultiThreadedWriter writer = new MultiThreadedWriter(dataGen, conf, IntegrationTestLazyCfLoading.TABLE_NAME);
        writer.setMultiPut(true);
        IntegrationTestLazyCfLoading.LOG.info(("Starting writer; the number of keys to write is " + keysToWrite));
        // TODO : Need to see if tag support has to be given here in the integration test suite
        writer.start(1, keysToWrite, IntegrationTestLazyCfLoading.WRITER_THREADS);
        // Now, do scans.
        long now = EnvironmentEdgeManager.currentTime();
        long timeLimit = now + (maxRuntime * 60000);
        boolean isWriterDone = false;
        while ((now < timeLimit) && (!isWriterDone)) {
            IntegrationTestLazyCfLoading.LOG.info((("Starting the scan; wrote approximately " + (dataGen.getTotalNumberOfKeys())) + " keys"));
            isWriterDone = writer.isDone();
            if (isWriterDone) {
                IntegrationTestLazyCfLoading.LOG.info("Scanning full result, writer is done");
            }
            Scan scan = new Scan();
            for (byte[] cf : dataGen.getColumnFamilies()) {
                scan.addFamily(cf);
            }
            scan.setFilter(dataGen.getScanFilter());
            scan.setLoadColumnFamiliesOnDemand(true);
            // The number of keys we can expect from scan - lower bound (before scan).
            // Not a strict lower bound - writer knows nothing about filters, so we report
            // this from generator. Writer might have generated the value but not put it yet.
            long onesGennedBeforeScan = dataGen.getExpectedNumberOfKeys();
            long startTs = EnvironmentEdgeManager.currentTime();
            ResultScanner results = table.getScanner(scan);
            long resultCount = 0;
            Result result = null;
            // Verify and count the results.
            while ((result = results.next()) != null) {
                boolean isOk = writer.verifyResultAgainstDataGenerator(result, true, true);
                Assert.assertTrue((("Failed to verify [" + (Bytes.toString(result.getRow()))) + "]"), isOk);
                ++resultCount;
            } 
            long timeTaken = (EnvironmentEdgeManager.currentTime()) - startTs;
            // Verify the result count.
            long onesGennedAfterScan = dataGen.getExpectedNumberOfKeys();
            Assert.assertTrue((((("Read " + resultCount) + " keys when at most ") + onesGennedAfterScan) + " were generated "), (onesGennedAfterScan >= resultCount));
            if (isWriterDone) {
                Assert.assertTrue((((("Read " + resultCount) + " keys; the writer is done and ") + onesGennedAfterScan) + " keys were generated"), (onesGennedAfterScan == resultCount));
            } else
                if ((onesGennedBeforeScan * 0.9) > resultCount) {
                    IntegrationTestLazyCfLoading.LOG.warn((((("Read way too few keys (" + resultCount) + "/") + onesGennedBeforeScan) + ") - there might be a problem, or the writer might just be slow"));
                }

            IntegrationTestLazyCfLoading.LOG.info((("Scan took " + timeTaken) + "ms"));
            if (!isWriterDone) {
                Thread.sleep(IntegrationTestLazyCfLoading.WAIT_BETWEEN_SCANS_MS);
                now = EnvironmentEdgeManager.currentTime();
            }
        } 
        Assert.assertEquals("There are write failures", 0, writer.getNumWriteFailures());
        Assert.assertTrue("Writer is not done", isWriterDone);
        // Assert.fail("Boom!");
        connection.close();
    }
}

