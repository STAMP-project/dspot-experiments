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
package org.apache.hadoop.hbase.rest;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SkipFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
public class TestScannersWithFilters {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannersWithFilters.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScannersWithFilters.class);

    private static final TableName TABLE = TableName.valueOf("TestScannersWithFilters");

    private static final byte[][] ROWS_ONE = new byte[][]{ Bytes.toBytes("testRowOne-0"), Bytes.toBytes("testRowOne-1"), Bytes.toBytes("testRowOne-2"), Bytes.toBytes("testRowOne-3") };

    private static final byte[][] ROWS_TWO = new byte[][]{ Bytes.toBytes("testRowTwo-0"), Bytes.toBytes("testRowTwo-1"), Bytes.toBytes("testRowTwo-2"), Bytes.toBytes("testRowTwo-3") };

    private static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("testFamilyOne"), Bytes.toBytes("testFamilyTwo") };

    private static final byte[][] QUALIFIERS_ONE = new byte[][]{ Bytes.toBytes("testQualifierOne-0"), Bytes.toBytes("testQualifierOne-1"), Bytes.toBytes("testQualifierOne-2"), Bytes.toBytes("testQualifierOne-3") };

    private static final byte[][] QUALIFIERS_TWO = new byte[][]{ Bytes.toBytes("testQualifierTwo-0"), Bytes.toBytes("testQualifierTwo-1"), Bytes.toBytes("testQualifierTwo-2"), Bytes.toBytes("testQualifierTwo-3") };

    private static final byte[][] VALUES = new byte[][]{ Bytes.toBytes("testValueOne"), Bytes.toBytes("testValueTwo") };

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final HBaseRESTTestingUtility REST_TEST_UTIL = new HBaseRESTTestingUtility();

    private static Client client;

    private static JAXBContext context;

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    private static long numRows = ((long) (TestScannersWithFilters.ROWS_ONE.length)) + (TestScannersWithFilters.ROWS_TWO.length);

    private static long colsPerRow = ((long) (TestScannersWithFilters.FAMILIES.length)) * (TestScannersWithFilters.QUALIFIERS_ONE.length);

    @Test
    public void testNoFilter() throws Exception {
        // No filter
        long expectedRows = TestScannersWithFilters.numRows;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        // Both families
        Scan s = new Scan();
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        // One family
        s = new Scan();
        s.addFamily(TestScannersWithFilters.FAMILIES[0]);
        TestScannersWithFilters.verifyScan(s, expectedRows, (expectedKeys / 2));
    }

    @Test
    public void testPrefixFilter() throws Exception {
        // Grab rows from group one (half of total)
        long expectedRows = (TestScannersWithFilters.numRows) / 2;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        Scan s = new Scan();
        s.setFilter(new org.apache.hadoop.hbase.filter.PrefixFilter(Bytes.toBytes("testRowOne")));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
    }

    @Test
    public void testPageFilter() throws Exception {
        // KVs in first 6 rows
        KeyValue[] expectedKVs = new KeyValue[]{ // testRowOne-0
        new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-2
        new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-3
        new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        // Grab all 6 rows
        long expectedRows = 6;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        Scan s = new Scan();
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScanFull(s, expectedKVs);
        // Grab first 4 rows (6 cols per row)
        expectedRows = 4;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        s = new Scan();
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScanFull(s, Arrays.copyOf(expectedKVs, 24));
        // Grab first 2 rows
        expectedRows = 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        s = new Scan();
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScanFull(s, Arrays.copyOf(expectedKVs, 12));
        // Grab first row
        expectedRows = 1;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        s = new Scan();
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        s.setFilter(new PageFilter(expectedRows));
        TestScannersWithFilters.verifyScanFull(s, Arrays.copyOf(expectedKVs, 6));
    }

    @Test
    public void testInclusiveStopFilter() throws Exception {
        // Grab rows from group one
        // If we just use start/stop row, we get total/2 - 1 rows
        long expectedRows = ((TestScannersWithFilters.numRows) / 2) - 1;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        Scan s = new Scan(Bytes.toBytes("testRowOne-0"), Bytes.toBytes("testRowOne-3"));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        // Now use start row with inclusive stop filter
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        s = new Scan(Bytes.toBytes("testRowOne-0"));
        s.setFilter(new org.apache.hadoop.hbase.filter.InclusiveStopFilter(Bytes.toBytes("testRowOne-3")));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        // Grab rows from group two
        // If we just use start/stop row, we get total/2 - 1 rows
        expectedRows = ((TestScannersWithFilters.numRows) / 2) - 1;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        s = new Scan(Bytes.toBytes("testRowTwo-0"), Bytes.toBytes("testRowTwo-3"));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
        // Now use start row with inclusive stop filter
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        s = new Scan(Bytes.toBytes("testRowTwo-0"));
        s.setFilter(new org.apache.hadoop.hbase.filter.InclusiveStopFilter(Bytes.toBytes("testRowTwo-3")));
        TestScannersWithFilters.verifyScan(s, expectedRows, expectedKeys);
    }

    @Test
    public void testQualifierFilter() throws Exception {
        // Match two keys (one from each family) in half the rows
        long expectedRows = (TestScannersWithFilters.numRows) / 2;
        long expectedKeys = 2;
        Filter f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        Scan s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys less than same qualifier
        // Expect only two keys (one from each family) in half the rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = 2;
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.LESS, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys less than or equal
        // Expect four keys (two from each family) in half the rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = 4;
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys not equal
        // Expect four keys (two from each family)
        // Only look in first group of rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = 4;
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        s = new Scan(HConstants.EMPTY_START_ROW, Bytes.toBytes("testRowTwo"));
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys greater or equal
        // Expect four keys (two from each family)
        // Only look in first group of rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = 4;
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        s = new Scan(HConstants.EMPTY_START_ROW, Bytes.toBytes("testRowTwo"));
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys greater
        // Expect two keys (one from each family)
        // Only look in first group of rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = 2;
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.GREATER, new BinaryComparator(Bytes.toBytes("testQualifierOne-2")));
        s = new Scan(HConstants.EMPTY_START_ROW, Bytes.toBytes("testRowTwo"));
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys not equal to
        // Look across rows and fully validate the keys and ordering
        // Expect varied numbers of keys, 4 per row in group one, 6 per row in
        // group two
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(TestScannersWithFilters.QUALIFIERS_ONE[2]));
        s = new Scan();
        s.setFilter(f);
        KeyValue[] kvs = new KeyValue[]{ // testRowOne-0
        new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-2
        new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-3
        new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
        // Test across rows and groups with a regex
        // Filter out "test*-2"
        // Expect 4 keys per row across both groups
        f = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.NOT_EQUAL, new RegexStringComparator("test.+-2"));
        s = new Scan();
        s.setFilter(f);
        kvs = new KeyValue[]{ // testRowOne-0
        new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-2
        new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-3
        new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
    }

    @Test
    public void testRowFilter() throws Exception {
        // Match a single row, all keys
        long expectedRows = 1;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        Filter f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        Scan s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match a two rows, one from each group, using regex
        expectedRows = 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator("testRow.+-2"));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match rows less than
        // Expect all keys in one row
        expectedRows = 1;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.LESS, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match rows less than or equal
        // Expect all keys in two rows
        expectedRows = 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match rows not equal
        // Expect all keys in all but one row
        expectedRows = (TestScannersWithFilters.numRows) - 1;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys greater or equal
        // Expect all keys in all but one row
        expectedRows = (TestScannersWithFilters.numRows) - 1;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match keys greater
        // Expect all keys in all but two rows
        expectedRows = (TestScannersWithFilters.numRows) - 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.GREATER, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match rows not equal to testRowTwo-2
        // Look across rows and fully validate the keys and ordering
        // Should see all keys in all rows but testRowTwo-2
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testRowOne-2")));
        s = new Scan();
        s.setFilter(f);
        KeyValue[] kvs = new KeyValue[]{ // testRowOne-0
        new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowOne-3
        new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
        // Test across rows and groups with a regex
        // Filter out everything that doesn't match "*-2"
        // Expect all keys in two rows
        f = new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator(".+-2"));
        s = new Scan();
        s.setFilter(f);
        kvs = new KeyValue[]{ // testRowOne-2
        new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_ONE[3], TestScannersWithFilters.VALUES[0]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
    }

    @Test
    public void testValueFilter() throws Exception {
        // Match group one rows
        long expectedRows = (TestScannersWithFilters.numRows) / 2;
        long expectedKeys = TestScannersWithFilters.colsPerRow;
        Filter f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("testValueOne")));
        Scan s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match group two rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("testValueTwo")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match all values using regex
        expectedRows = TestScannersWithFilters.numRows;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new RegexStringComparator("testValue((One)|(Two))"));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values less than
        // Expect group one rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.LESS, new BinaryComparator(Bytes.toBytes("testValueTwo")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values less than or equal
        // Expect all rows
        expectedRows = TestScannersWithFilters.numRows;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testValueTwo")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values less than or equal
        // Expect group one rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testValueOne")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values not equal
        // Expect half the rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testValueOne")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values greater or equal
        // Expect all rows
        expectedRows = TestScannersWithFilters.numRows;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("testValueOne")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values greater
        // Expect half rows
        expectedRows = (TestScannersWithFilters.numRows) / 2;
        expectedKeys = TestScannersWithFilters.colsPerRow;
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.GREATER, new BinaryComparator(Bytes.toBytes("testValueOne")));
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, expectedRows, expectedKeys);
        // Match values not equal to testValueOne
        // Look across rows and fully validate the keys and ordering
        // Should see all keys in all group two rows
        f = new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testValueOne")));
        s = new Scan();
        s.setFilter(f);
        KeyValue[] kvs = new KeyValue[]{ // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
    }

    @Test
    public void testSkipFilter() throws Exception {
        // Test for qualifier regex: "testQualifierOne-2"
        // Should only get rows from second group, and all keys
        Filter f = new SkipFilter(new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("testQualifierOne-2"))));
        Scan s = new Scan();
        s.setFilter(f);
        KeyValue[] kvs = new KeyValue[]{ // testRowTwo-0
        new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-2
        new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), // testRowTwo-3
        new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[2], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[1], TestScannersWithFilters.QUALIFIERS_TWO[3], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
    }

    @Test
    public void testFilterList() throws Exception {
        // Test getting a single row, single key using Row, Qualifier, and Value
        // regular expression and substring filters
        // Use must pass all
        List<Filter> filters = new ArrayList<>(3);
        filters.add(new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator(".+-2")));
        filters.add(new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new RegexStringComparator(".+-2")));
        filters.add(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("One")));
        Filter f = new org.apache.hadoop.hbase.filter.FilterList(Operator.MUST_PASS_ALL, filters);
        Scan s = new Scan();
        s.addFamily(TestScannersWithFilters.FAMILIES[0]);
        s.setFilter(f);
        KeyValue[] kvs = new KeyValue[]{ new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[2], TestScannersWithFilters.VALUES[0]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
        // Test getting everything with a MUST_PASS_ONE filter including row, qf,
        // val, regular expression and substring filters
        filters.clear();
        filters.add(new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.EQUAL, new RegexStringComparator(".+Two.+")));
        filters.add(new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new RegexStringComparator(".+-2")));
        filters.add(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("One")));
        f = new org.apache.hadoop.hbase.filter.FilterList(Operator.MUST_PASS_ONE, filters);
        s = new Scan();
        s.setFilter(f);
        TestScannersWithFilters.verifyScanNoEarlyOut(s, TestScannersWithFilters.numRows, TestScannersWithFilters.colsPerRow);
    }

    @Test
    public void testFirstKeyOnlyFilter() throws Exception {
        Scan s = new Scan();
        s.setFilter(new FirstKeyOnlyFilter());
        // Expected KVs, the first KV from each of the remaining 6 rows
        KeyValue[] kvs = new KeyValue[]{ new KeyValue(TestScannersWithFilters.ROWS_ONE[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_ONE[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_ONE[0], TestScannersWithFilters.VALUES[0]), new KeyValue(TestScannersWithFilters.ROWS_TWO[0], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[2], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]), new KeyValue(TestScannersWithFilters.ROWS_TWO[3], TestScannersWithFilters.FAMILIES[0], TestScannersWithFilters.QUALIFIERS_TWO[0], TestScannersWithFilters.VALUES[1]) };
        TestScannersWithFilters.verifyScanFull(s, kvs);
    }
}

