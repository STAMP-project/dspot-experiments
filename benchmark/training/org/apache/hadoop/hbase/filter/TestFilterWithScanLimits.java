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


import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test if Filter is incompatible with scan-limits
 */
@Category({ FilterTests.class, MediumTests.class })
public class TestFilterWithScanLimits extends FilterTestingCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFilterWithScanLimits.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFilterWithScanLimits.class);

    private static final TableName tableName = TableName.valueOf("scanWithLimit");

    private static final String columnFamily = "f1";

    @Test
    public void testScanWithLimit() {
        int kv_number = 0;
        try {
            Scan scan = new Scan();
            // set batch number as 2, which means each Result should contain 2 KVs at most
            scan.setBatch(2);
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(TestFilterWithScanLimits.columnFamily), Bytes.toBytes("c5"), CompareOperator.EQUAL, new SubstringComparator("2_c5"));
            // add filter after batch defined
            scan.setFilter(filter);
            Table table = FilterTestingCluster.openTable(TestFilterWithScanLimits.tableName);
            ResultScanner scanner = table.getScanner(scan);
            // Expect to get following row
            // row2 => <f1:c1, 2_c1>, <f1:c2, 2_c2>,
            // row2 => <f1:c3, 2_c3>, <f1:c4, 2_c4>,
            // row2 => <f1:c5, 2_c5>
            for (Result result : scanner) {
                for (Cell kv : result.listCells()) {
                    kv_number++;
                    TestFilterWithScanLimits.LOG.debug(((kv_number + ". kv: ") + kv));
                }
            }
            scanner.close();
            table.close();
        } catch (Exception e) {
            // no correct result is expected
            Assert.assertNotNull("No IncompatibleFilterException catched", e);
        }
        TestFilterWithScanLimits.LOG.debug("check the fetched kv number");
        Assert.assertEquals("We should not get result(s) returned.", 0, kv_number);
    }
}

