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
package org.apache.hadoop.hbase.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-21032, where use the wrong readType from a Scan instance which is actually a
 * get scan and cause returning only 1 cell per rpc call.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestGetScanColumnsWithNewVersionBehavior {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGetScanColumnsWithNewVersionBehavior.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE = TableName.valueOf("table");

    private static final byte[] CF = new byte[]{ 'c', 'f' };

    private static final byte[] ROW = new byte[]{ 'r', 'o', 'w' };

    private static final byte[] COLA = new byte[]{ 'a' };

    private static final byte[] COLB = new byte[]{ 'b' };

    private static final byte[] COLC = new byte[]{ 'c' };

    private static final long TS = 42;

    @Test
    public void test() throws IOException {
        try (Table t = TestGetScanColumnsWithNewVersionBehavior.TEST_UTIL.getConnection().getTable(TestGetScanColumnsWithNewVersionBehavior.TABLE)) {
            Cell[] expected = new Cell[2];
            expected[0] = new KeyValue(TestGetScanColumnsWithNewVersionBehavior.ROW, TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLA, TestGetScanColumnsWithNewVersionBehavior.TS, TestGetScanColumnsWithNewVersionBehavior.COLA);
            expected[1] = new KeyValue(TestGetScanColumnsWithNewVersionBehavior.ROW, TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLC, TestGetScanColumnsWithNewVersionBehavior.TS, TestGetScanColumnsWithNewVersionBehavior.COLC);
            Put p = new Put(TestGetScanColumnsWithNewVersionBehavior.ROW);
            p.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLA, TestGetScanColumnsWithNewVersionBehavior.TS, TestGetScanColumnsWithNewVersionBehavior.COLA);
            p.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLB, TestGetScanColumnsWithNewVersionBehavior.TS, TestGetScanColumnsWithNewVersionBehavior.COLB);
            p.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLC, TestGetScanColumnsWithNewVersionBehavior.TS, TestGetScanColumnsWithNewVersionBehavior.COLC);
            t.put(p);
            // check get request
            Get get = new Get(TestGetScanColumnsWithNewVersionBehavior.ROW);
            get.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLA);
            get.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLC);
            Result getResult = t.get(get);
            Assert.assertArrayEquals(expected, getResult.rawCells());
            // check scan request
            Scan scan = new Scan(TestGetScanColumnsWithNewVersionBehavior.ROW);
            scan.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLA);
            scan.addColumn(TestGetScanColumnsWithNewVersionBehavior.CF, TestGetScanColumnsWithNewVersionBehavior.COLC);
            ResultScanner scanner = t.getScanner(scan);
            List scanResult = new ArrayList<Cell>();
            for (Result result = scanner.next(); result != null; result = scanner.next()) {
                scanResult.addAll(result.listCells());
            }
            Assert.assertArrayEquals(expected, scanResult.toArray(new Cell[scanResult.size()]));
        }
    }
}

