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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.regionserver.HRegion.RegionScannerImpl;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReturnCode.INCLUDE;
import static ReturnCode.NEXT_COL;
import static ReturnCode.NEXT_ROW;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestSwitchToStreamRead {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSwitchToStreamRead.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("stream");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUAL = Bytes.toBytes("cq");

    private static String VALUE_PREFIX;

    private static HRegion REGION;

    @Test
    public void test() throws IOException {
        try (RegionScannerImpl scanner = TestSwitchToStreamRead.REGION.getScanner(new Scan())) {
            StoreScanner storeScanner = ((StoreScanner) (scanner.getStoreHeapForTesting().getCurrentForTesting()));
            for (KeyValueScanner kvs : storeScanner.getAllScannersForTesting()) {
                if (kvs instanceof StoreFileScanner) {
                    StoreFileScanner sfScanner = ((StoreFileScanner) (kvs));
                    // starting from pread so we use shared reader here.
                    Assert.assertTrue(sfScanner.getReader().shared);
                }
            }
            List<Cell> cells = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                Assert.assertTrue(scanner.next(cells));
                Result result = Result.create(cells);
                Assert.assertEquals(((TestSwitchToStreamRead.VALUE_PREFIX) + i), Bytes.toString(result.getValue(TestSwitchToStreamRead.FAMILY, TestSwitchToStreamRead.QUAL)));
                cells.clear();
                scanner.shipped();
            }
            for (KeyValueScanner kvs : storeScanner.getAllScannersForTesting()) {
                if (kvs instanceof StoreFileScanner) {
                    StoreFileScanner sfScanner = ((StoreFileScanner) (kvs));
                    // we should have convert to use stream read now.
                    Assert.assertFalse(sfScanner.getReader().shared);
                }
            }
            for (int i = 500; i < 1000; i++) {
                Assert.assertEquals((i != 999), scanner.next(cells));
                Result result = Result.create(cells);
                Assert.assertEquals(((TestSwitchToStreamRead.VALUE_PREFIX) + i), Bytes.toString(result.getValue(TestSwitchToStreamRead.FAMILY, TestSwitchToStreamRead.QUAL)));
                cells.clear();
                scanner.shipped();
            }
        }
        // make sure all scanners are closed.
        for (HStoreFile sf : TestSwitchToStreamRead.REGION.getStore(TestSwitchToStreamRead.FAMILY).getStorefiles()) {
            Assert.assertFalse(sf.isReferencedInReads());
        }
    }

    public static final class MatchLastRowKeyFilter extends FilterBase {
        @Override
        public boolean filterRowKey(Cell cell) throws IOException {
            return (Bytes.toInt(cell.getRowArray(), cell.getRowOffset())) != 999;
        }
    }

    public static final class MatchLastRowCellNextColFilter extends FilterBase {
        @Override
        public ReturnCode filterCell(Cell c) throws IOException {
            if ((Bytes.toInt(c.getRowArray(), c.getRowOffset())) == 999) {
                return INCLUDE;
            } else {
                return NEXT_COL;
            }
        }
    }

    @Test
    public void testFilterCellNextCol() throws IOException {
        testFilter(new TestSwitchToStreamRead.MatchLastRowCellNextColFilter());
    }

    public static final class MatchLastRowCellNextRowFilter extends FilterBase {
        @Override
        public ReturnCode filterCell(Cell c) throws IOException {
            if ((Bytes.toInt(c.getRowArray(), c.getRowOffset())) == 999) {
                return ReturnCode.INCLUDE;
            } else {
                return NEXT_ROW;
            }
        }
    }

    @Test
    public void testFilterCellNextRow() throws IOException {
        testFilter(new TestSwitchToStreamRead.MatchLastRowCellNextRowFilter());
    }

    public static final class MatchLastRowFilterRowFilter extends FilterBase {
        private boolean exclude;

        @Override
        public void filterRowCells(List<Cell> kvs) throws IOException {
            Cell c = kvs.get(0);
            exclude = (Bytes.toInt(c.getRowArray(), c.getRowOffset())) != 999;
        }

        @Override
        public void reset() throws IOException {
            exclude = false;
        }

        @Override
        public boolean filterRow() throws IOException {
            return exclude;
        }

        @Override
        public boolean hasFilterRow() {
            return true;
        }
    }

    @Test
    public void testFilterRow() throws IOException {
        testFilter(new TestSwitchToStreamRead.MatchLastRowFilterRowFilter());
    }
}

