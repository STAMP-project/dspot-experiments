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
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test if Scan.setRowPrefixFilter works as intended.
 */
@Category({ FilterTests.class, MediumTests.class })
public class TestScanRowPrefix extends FilterTestingCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScanRowPrefix.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScanRowPrefix.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testPrefixScanning() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        FilterTestingCluster.createTable(tableName, "F");
        Table table = FilterTestingCluster.openTable(tableName);
        /**
         * Note that about half of these tests were relevant for an different implementation approach
         * of setRowPrefixFilter. These test cases have been retained to ensure that also the
         * edge cases found there are still covered.
         */
        final byte[][] rowIds = new byte[][]{ new byte[]{ ((byte) (17)) }, // 0
        new byte[]{ ((byte) (18)) }, // 1
        new byte[]{ ((byte) (18)), ((byte) (35)), ((byte) (255)), ((byte) (254)) }, // 2
        new byte[]{ ((byte) (18)), ((byte) (35)), ((byte) (255)), ((byte) (255)) }, // 3
        new byte[]{ ((byte) (18)), ((byte) (35)), ((byte) (255)), ((byte) (255)), ((byte) (0)) }// 4
        // 4
        // 4
        , new byte[]{ ((byte) (18)), ((byte) (35)), ((byte) (255)), ((byte) (255)), ((byte) (1)) }// 5
        // 5
        // 5
        , new byte[]{ ((byte) (18)), ((byte) (36)) }, // 6
        new byte[]{ ((byte) (18)), ((byte) (36)), ((byte) (0)) }, // 7
        new byte[]{ ((byte) (18)), ((byte) (36)), ((byte) (0)), ((byte) (0)) }, // 8
        new byte[]{ ((byte) (18)), ((byte) (37)) }, // 9
        new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }// 10
        // 10
        // 10
         };
        for (byte[] rowId : rowIds) {
            Put p = new Put(rowId);
            // Use the rowId as the column qualifier
            p.addColumn(Bytes.toBytes("F"), rowId, Bytes.toBytes("Dummy value"));
            table.put(p);
        }
        byte[] prefix0 = new byte[]{  };
        List<byte[]> expected0 = new ArrayList<>(16);
        expected0.addAll(Arrays.asList(rowIds));// Expect all rows

        byte[] prefix1 = new byte[]{ ((byte) (18)), ((byte) (35)) };
        List<byte[]> expected1 = new ArrayList<>(16);
        expected1.add(rowIds[2]);
        expected1.add(rowIds[3]);
        expected1.add(rowIds[4]);
        expected1.add(rowIds[5]);
        byte[] prefix2 = new byte[]{ ((byte) (18)), ((byte) (35)), ((byte) (255)), ((byte) (255)) };
        List<byte[]> expected2 = new ArrayList<>();
        expected2.add(rowIds[3]);
        expected2.add(rowIds[4]);
        expected2.add(rowIds[5]);
        byte[] prefix3 = new byte[]{ ((byte) (18)), ((byte) (36)) };
        List<byte[]> expected3 = new ArrayList<>();
        expected3.add(rowIds[6]);
        expected3.add(rowIds[7]);
        expected3.add(rowIds[8]);
        byte[] prefix4 = new byte[]{ ((byte) (255)), ((byte) (255)) };
        List<byte[]> expected4 = new ArrayList<>();
        expected4.add(rowIds[10]);
        // ========
        // PREFIX 0
        Scan scan = new Scan();
        scan.setRowPrefixFilter(prefix0);
        verifyScanResult(table, scan, expected0, "Scan empty prefix failed");
        // ========
        // PREFIX 1
        scan = new Scan();
        scan.setRowPrefixFilter(prefix1);
        verifyScanResult(table, scan, expected1, "Scan normal prefix failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after prefix reset failed");
        scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(prefix1));
        verifyScanResult(table, scan, expected1, "Double check on column prefix failed");
        // ========
        // PREFIX 2
        scan = new Scan();
        scan.setRowPrefixFilter(prefix2);
        verifyScanResult(table, scan, expected2, "Scan edge 0xFF prefix failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after prefix reset failed");
        scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(prefix2));
        verifyScanResult(table, scan, expected2, "Double check on column prefix failed");
        // ========
        // PREFIX 3
        scan = new Scan();
        scan.setRowPrefixFilter(prefix3);
        verifyScanResult(table, scan, expected3, "Scan normal with 0x00 ends failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after prefix reset failed");
        scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(prefix3));
        verifyScanResult(table, scan, expected3, "Double check on column prefix failed");
        // ========
        // PREFIX 4
        scan = new Scan();
        scan.setRowPrefixFilter(prefix4);
        verifyScanResult(table, scan, expected4, "Scan end prefix failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after prefix reset failed");
        scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(prefix4));
        verifyScanResult(table, scan, expected4, "Double check on column prefix failed");
        // ========
        // COMBINED
        // Prefix + Filter
        scan = new Scan();
        scan.setRowPrefixFilter(prefix1);
        verifyScanResult(table, scan, expected1, "Prefix filter failed");
        scan.setFilter(new ColumnPrefixFilter(prefix2));
        verifyScanResult(table, scan, expected2, "Combined Prefix + Filter failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected2, "Combined Prefix + Filter; removing Prefix failed");
        scan.setFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after Filter reset failed");
        // ========
        // Reversed: Filter + Prefix
        scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(prefix2));
        verifyScanResult(table, scan, expected2, "Test filter failed");
        scan.setRowPrefixFilter(prefix1);
        verifyScanResult(table, scan, expected2, "Combined Filter + Prefix failed");
        scan.setFilter(null);
        verifyScanResult(table, scan, expected1, "Combined Filter + Prefix ; removing Filter failed");
        scan.setRowPrefixFilter(null);
        verifyScanResult(table, scan, expected0, "Scan after prefix reset failed");
    }
}

