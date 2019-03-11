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
package org.apache.beam.sdk.io.hbase;


import ByteKeyRange.ALL_KEYS;
import CompareFilter.CompareOp;
import HBaseIO.Read;
import HBaseIO.Write;
import Pipeline.PipelineExecutionException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.hbase.HBaseIO.HBaseSource;
import org.apache.beam.sdk.io.range.ByteKey;
import org.apache.beam.sdk.io.range.ByteKeyRange;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test HBaseIO.
 */
@RunWith(JUnit4.class)
public class HBaseIOTest {
    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public HBaseIOTest.TemporaryHBaseTable tmpTable = new HBaseIOTest.TemporaryHBaseTable();

    private static HBaseTestingUtility htu;

    private static HBaseAdmin admin;

    private static final Configuration conf = HBaseConfiguration.create();

    private static final byte[] COLUMN_FAMILY = Bytes.toBytes("info");

    private static final byte[] COLUMN_NAME = Bytes.toBytes("name");

    private static final byte[] COLUMN_EMAIL = Bytes.toBytes("email");

    @Test
    public void testReadBuildsCorrectly() {
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId("table");
        Assert.assertEquals("table", read.getTableId());
        Assert.assertNotNull("configuration", read.getConfiguration());
    }

    @Test
    public void testReadBuildsCorrectlyInDifferentOrder() {
        HBaseIO.Read read = HBaseIO.read().withTableId("table").withConfiguration(HBaseIOTest.conf);
        Assert.assertEquals("table", read.getTableId());
        Assert.assertNotNull("configuration", read.getConfiguration());
    }

    @Test
    public void testWriteBuildsCorrectly() {
        HBaseIO.Write write = HBaseIO.write().withConfiguration(HBaseIOTest.conf).withTableId("table");
        Assert.assertEquals("table", write.getTableId());
        Assert.assertNotNull("configuration", write.getConfiguration());
    }

    @Test
    public void testWriteBuildsCorrectlyInDifferentOrder() {
        HBaseIO.Write write = HBaseIO.write().withTableId("table").withConfiguration(HBaseIOTest.conf);
        Assert.assertEquals("table", write.getTableId());
        Assert.assertNotNull("configuration", write.getConfiguration());
    }

    @Test
    public void testWriteValidationFailsMissingTable() {
        HBaseIO.Write write = HBaseIO.write().withConfiguration(HBaseIOTest.conf);
        thrown.expect(IllegalArgumentException.class);
        /* input */
        write.expand(null);
    }

    @Test
    public void testWriteValidationFailsMissingConfiguration() {
        HBaseIO.Write write = HBaseIO.write().withTableId("table");
        thrown.expect(IllegalArgumentException.class);
        /* input */
        write.expand(null);
    }

    /**
     * Tests that when reading from a non-existent table, the read fails.
     */
    @Test
    public void testReadingFailsTableDoesNotExist() {
        final String table = tmpTable.getName();
        // Exception will be thrown by read.expand() when read is applied.
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(String.format("Table %s does not exist", table));
        runReadTest(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), false, new ArrayList());
        runReadTest(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), true, new ArrayList());
    }

    /**
     * Tests that when reading from an empty table, the read succeeds.
     */
    @Test
    public void testReadingEmptyTable() throws Exception {
        final String table = tmpTable.getName();
        HBaseIOTest.createTable(table);
        runReadTest(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), false, new ArrayList());
        runReadTest(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), true, new ArrayList());
    }

    @Test
    public void testReading() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        HBaseIOTest.createAndWriteData(table, numRows);
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), false, numRows);
    }

    @Test
    public void testReadingSDF() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        HBaseIOTest.createAndWriteData(table, numRows);
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table), true, numRows);
    }

    /**
     * Tests reading all rows from a split table.
     */
    @Test
    public void testReadingWithSplits() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1500;
        final int numRegions = 4;
        final long bytesPerRow = 100L;
        HBaseIOTest.createAndWriteData(table, numRows);
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table);
        HBaseSource source = /* estimatedSizeBytes */
        new HBaseSource(read, null);
        List<? extends BoundedSource<Result>> splits = /* options */
        source.split(((numRows * bytesPerRow) / numRegions), null);
        // Test num splits and split equality.
        Assert.assertThat(splits, Matchers.hasSize(4));
        /* options */
        assertSourcesEqualReferenceSource(source, splits, null);
    }

    /**
     * Tests that a {@link HBaseSource} can be read twice, verifying its immutability.
     */
    @Test
    public void testReadingSourceTwice() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 10;
        HBaseIOTest.createAndWriteData(table, numRows);
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table);
        HBaseSource source = /* estimatedSizeBytes */
        new HBaseSource(read, null);
        Assert.assertThat(SourceTestUtils.readFromSource(source, null), Matchers.hasSize(numRows));
        // second read.
        Assert.assertThat(SourceTestUtils.readFromSource(source, null), Matchers.hasSize(numRows));
    }

    /**
     * Tests reading all rows using a filter.
     */
    @Test
    public void testReadingWithFilter() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        HBaseIOTest.createAndWriteData(table, numRows);
        String regex = ".*17.*";
        Filter filter = new org.apache.hadoop.hbase.filter.RowFilter(CompareOp.EQUAL, new RegexStringComparator(regex));
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withFilter(filter), false, 20);
    }

    @Test
    public void testReadingWithFilterSDF() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        HBaseIOTest.createAndWriteData(table, numRows);
        String regex = ".*17.*";
        Filter filter = new org.apache.hadoop.hbase.filter.RowFilter(CompareOp.EQUAL, new RegexStringComparator(regex));
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withFilter(filter), true, 20);
    }

    /**
     * Tests reading all rows using key ranges. Tests a prefix [), a suffix (], and a restricted range
     * [] and that some properties hold across them.
     */
    @Test
    public void testReadingKeyRangePrefix() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        final ByteKey startKey = ByteKey.copyFrom("2".getBytes(StandardCharsets.UTF_8));
        HBaseIOTest.createAndWriteData(table, numRows);
        // Test prefix: [beginning, startKey).
        final ByteKeyRange prefixRange = ALL_KEYS.withEndKey(startKey);
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withKeyRange(prefixRange), false, 126);
    }

    /**
     * Tests reading all rows using key ranges. Tests a prefix [), a suffix (], and a restricted range
     * [] and that some properties hold across them.
     */
    @Test
    public void testReadingKeyRangeSuffix() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        final ByteKey startKey = ByteKey.copyFrom("2".getBytes(StandardCharsets.UTF_8));
        HBaseIOTest.createAndWriteData(table, numRows);
        // Test suffix: [startKey, end).
        final ByteKeyRange suffixRange = ALL_KEYS.withStartKey(startKey);
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withKeyRange(suffixRange), false, 875);
    }

    /**
     * Tests reading all rows using key ranges. Tests a prefix [), a suffix (], and a restricted range
     * [] and that some properties hold across them.
     */
    @Test
    public void testReadingKeyRangeMiddle() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        final byte[] startRow = "2".getBytes(StandardCharsets.UTF_8);
        final byte[] stopRow = "9".getBytes(StandardCharsets.UTF_8);
        HBaseIOTest.createAndWriteData(table, numRows);
        // Test restricted range: [startKey, endKey).
        // This one tests the second signature of .withKeyRange
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withKeyRange(startRow, stopRow), false, 441);
    }

    @Test
    public void testReadingKeyRangeMiddleSDF() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 1001;
        final byte[] startRow = "2".getBytes(StandardCharsets.UTF_8);
        final byte[] stopRow = "9".getBytes(StandardCharsets.UTF_8);
        HBaseIOTest.createAndWriteData(table, numRows);
        // Test restricted range: [startKey, endKey).
        // This one tests the second signature of .withKeyRange
        runReadTestLength(HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table).withKeyRange(startRow, stopRow), true, 441);
    }

    /**
     * Tests dynamic work rebalancing exhaustively.
     */
    @Test
    public void testReadingSplitAtFractionExhaustive() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 7;
        HBaseIOTest.createAndWriteData(table, numRows);
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table);
        HBaseSource source = /* estimatedSizeBytes */
        new HBaseSource(read, null).withStartKey(ByteKey.of(48)).withEndKey(ByteKey.of(58));
        assertSplitAtFractionExhaustive(source, null);
    }

    /**
     * Unit tests of splitAtFraction.
     */
    @Test
    public void testReadingSplitAtFraction() throws Exception {
        final String table = tmpTable.getName();
        final int numRows = 10;
        HBaseIOTest.createAndWriteData(table, numRows);
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId(table);
        HBaseSource source = /* estimatedSizeBytes */
        new HBaseSource(read, null);
        // The value k is based on the partitioning schema for the data, in this test case,
        // the partitioning is HEX-based, so we start from 1/16m and the value k will be
        // around 1/256, so the tests are done in approximately k ~= 0.003922 steps
        double k = 0.003922;
        /* options */
        assertSplitAtFractionFails(source, 0, k, null);
        /* options */
        assertSplitAtFractionFails(source, 0, 1.0, null);
        // With 1 items read, all split requests past k will succeed.
        /* options */
        assertSplitAtFractionSucceedsAndConsistent(source, 1, k, null);
        /* options */
        assertSplitAtFractionSucceedsAndConsistent(source, 1, 0.666, null);
        // With 3 items read, all split requests past 3k will succeed.
        /* options */
        assertSplitAtFractionFails(source, 3, (2 * k), null);
        /* options */
        assertSplitAtFractionSucceedsAndConsistent(source, 3, (3 * k), null);
        /* options */
        assertSplitAtFractionSucceedsAndConsistent(source, 3, (4 * k), null);
        // With 6 items read, all split requests past 6k will succeed.
        /* options */
        assertSplitAtFractionFails(source, 6, (5 * k), null);
        /* options */
        assertSplitAtFractionSucceedsAndConsistent(source, 6, 0.7, null);
    }

    @Test
    public void testReadingDisplayData() {
        HBaseIO.Read read = HBaseIO.read().withConfiguration(HBaseIOTest.conf).withTableId("fooTable");
        DisplayData displayData = DisplayData.from(read);
        Assert.assertThat(displayData, hasDisplayItem("tableId", "fooTable"));
        Assert.assertThat(displayData, hasDisplayItem("configuration"));
    }

    /**
     * Tests that a record gets written to the service and messages are logged.
     */
    @Test
    public void testWriting() throws Exception {
        final String table = tmpTable.getName();
        final String key = "key";
        final String value = "value";
        final int numMutations = 100;
        HBaseIOTest.createTable(table);
        p.apply("multiple rows", Create.of(HBaseIOTest.makeMutations(key, value, numMutations))).apply("write", HBaseIO.write().withConfiguration(HBaseIOTest.conf).withTableId(table));
        p.run().waitUntilFinish();
        List<Result> results = HBaseIOTest.readTable(table, new Scan());
        Assert.assertEquals(numMutations, results.size());
    }

    /**
     * Tests that when writing to a non-existent table, the write fails.
     */
    @Test
    public void testWritingFailsTableDoesNotExist() {
        final String table = tmpTable.getName();
        // Exception will be thrown by write.expand() when writeToDynamic is applied.
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(String.format("Table %s does not exist", table));
        p.apply(Create.empty(HBaseMutationCoder.of())).apply("write", HBaseIO.write().withConfiguration(HBaseIOTest.conf).withTableId(table));
    }

    /**
     * Tests that when writing an element fails, the write fails.
     */
    @Test
    public void testWritingFailsBadElement() throws Exception {
        final String table = tmpTable.getName();
        final String key = "KEY";
        HBaseIOTest.createTable(table);
        p.apply(Create.of(HBaseIOTest.makeBadMutation(key))).apply(HBaseIO.write().withConfiguration(HBaseIOTest.conf).withTableId(table));
        thrown.expect(PipelineExecutionException.class);
        thrown.expectCause(Matchers.instanceOf(IllegalArgumentException.class));
        thrown.expectMessage("No columns to insert");
        p.run().waitUntilFinish();
    }

    @Test
    public void testWritingDisplayData() {
        final String table = tmpTable.getName();
        HBaseIO.Write write = HBaseIO.write().withTableId(table).withConfiguration(HBaseIOTest.conf);
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, hasDisplayItem("tableId", table));
    }

    private static class TemporaryHBaseTable extends ExternalResource {
        private String name;

        @Override
        protected void before() {
            name = "table_" + (UUID.randomUUID());
        }

        String getName() {
            return name;
        }
    }
}

