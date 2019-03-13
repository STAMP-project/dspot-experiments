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
package org.apache.hadoop.hbase.filter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTestConst;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReturnCode.INCLUDE;
import static ReturnCode.SKIP;


/**
 * To test behavior of filters at server from region side.
 */
@Category(SmallTests.class)
public class TestFilterFromRegionSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFilterFromRegionSide.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static HRegion REGION;

    private static TableName TABLE_NAME = TableName.valueOf("TestFilterFromRegionSide");

    private static int NUM_ROWS = 5;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[][] ROWS = HTestConst.makeNAscii(TestFilterFromRegionSide.ROW, TestFilterFromRegionSide.NUM_ROWS);

    // Should keep this value below 10 to keep generation of expected kv's simple. If above 10 then
    // table/row/cf1/... will be followed by table/row/cf10/... instead of table/row/cf2/... which
    // breaks the simple generation of expected kv's
    private static int NUM_FAMILIES = 5;

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES = HTestConst.makeNAscii(TestFilterFromRegionSide.FAMILY, TestFilterFromRegionSide.NUM_FAMILIES);

    private static int NUM_QUALIFIERS = 5;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[][] QUALIFIERS = HTestConst.makeNAscii(TestFilterFromRegionSide.QUALIFIER, TestFilterFromRegionSide.NUM_QUALIFIERS);

    private static int VALUE_SIZE = 1024;

    private static byte[] VALUE = Bytes.createMaxByteArray(TestFilterFromRegionSide.VALUE_SIZE);

    private static int NUM_COLS = (TestFilterFromRegionSide.NUM_FAMILIES) * (TestFilterFromRegionSide.NUM_QUALIFIERS);

    @Test
    public void testFirstKeyOnlyFilterAndBatch() throws IOException {
        Scan scan = new Scan();
        scan.setFilter(new FirstKeyOnlyFilter());
        scan.setBatch(1);
        InternalScanner scanner = TestFilterFromRegionSide.REGION.getScanner(scan);
        List<Cell> results = new ArrayList<>();
        for (int i = 0; i < (TestFilterFromRegionSide.NUM_ROWS); i++) {
            results.clear();
            scanner.next(results);
            Assert.assertEquals(1, results.size());
            Cell cell = results.get(0);
            Assert.assertArrayEquals(TestFilterFromRegionSide.ROWS[i], Bytes.copy(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()));
        }
        Assert.assertFalse(scanner.next(results));
        scanner.close();
    }

    public static class FirstSeveralCellsFilter extends FilterBase {
        private int count = 0;

        @Override
        public void reset() {
            count = 0;
        }

        @Override
        public boolean filterRowKey(Cell cell) throws IOException {
            return false;
        }

        @Override
        public ReturnCode filterCell(final Cell v) {
            if (((count)++) < (TestFilterFromRegionSide.NUM_COLS)) {
                return INCLUDE;
            }
            return SKIP;
        }

        public static Filter parseFrom(final byte[] pbBytes) {
            return new TestFilterFromRegionSide.FirstSeveralCellsFilter();
        }
    }

    @Test
    public void testFirstSeveralCellsFilterAndBatch() throws IOException {
        Scan scan = new Scan();
        scan.setFilter(new TestFilterFromRegionSide.FirstSeveralCellsFilter());
        scan.setBatch(TestFilterFromRegionSide.NUM_COLS);
        InternalScanner scanner = TestFilterFromRegionSide.REGION.getScanner(scan);
        List<Cell> results = new ArrayList<>();
        for (int i = 0; i < (TestFilterFromRegionSide.NUM_ROWS); i++) {
            results.clear();
            scanner.next(results);
            Assert.assertEquals(TestFilterFromRegionSide.NUM_COLS, results.size());
            Cell cell = results.get(0);
            Assert.assertArrayEquals(TestFilterFromRegionSide.ROWS[i], Bytes.copy(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()));
            Assert.assertArrayEquals(TestFilterFromRegionSide.FAMILIES[0], Bytes.copy(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()));
            Assert.assertArrayEquals(TestFilterFromRegionSide.QUALIFIERS[0], Bytes.copy(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()));
        }
        Assert.assertFalse(scanner.next(results));
        scanner.close();
    }
}

