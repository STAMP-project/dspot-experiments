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


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
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
 * Test if the FilterWrapper retains the same semantics defined in the
 * {@link org.apache.hadoop.hbase.filter.Filter}
 */
@Category({ FilterTests.class, MediumTests.class })
public class TestFilterWrapper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFilterWrapper.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFilterWrapper.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf = null;

    private static Admin admin = null;

    private static TableName name = TableName.valueOf("test");

    private static Connection connection;

    @Test
    public void testFilterWrapper() {
        int kv_number = 0;
        int row_number = 0;
        try {
            Scan scan = new Scan();
            List<Filter> fs = new ArrayList<>();
            DependentColumnFilter f1 = new DependentColumnFilter(Bytes.toBytes("f1"), Bytes.toBytes("c5"), true, CompareOperator.EQUAL, new SubstringComparator("c5"));
            PageFilter f2 = new PageFilter(2);
            fs.add(f1);
            fs.add(f2);
            FilterList filter = new FilterList(fs);
            scan.setFilter(filter);
            Table table = TestFilterWrapper.connection.getTable(TestFilterWrapper.name);
            ResultScanner scanner = table.getScanner(scan);
            // row2 (c1-c4) and row3(c1-c4) are returned
            for (Result result : scanner) {
                row_number++;
                for (Cell kv : result.listCells()) {
                    TestFilterWrapper.LOG.debug(((kv_number + ". kv: ") + kv));
                    kv_number++;
                    Assert.assertEquals("Returned row is not correct", Bytes.toString(CellUtil.cloneRow(kv)), ("row" + (row_number + 1)));
                }
            }
            scanner.close();
            table.close();
        } catch (Exception e) {
            // no correct result is expected
            Assert.assertNull("Exception happens in scan", e);
        }
        TestFilterWrapper.LOG.debug("check the fetched kv number");
        Assert.assertEquals("We should get 8 results returned.", 8, kv_number);
        Assert.assertEquals("We should get 2 rows returned", 2, row_number);
    }
}

