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


import FilterList.Operator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.MultiRowRangeFilter.RowRange;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* This test is for the optimization added in HBASE-15243.
FilterList with two MultiRowRangeFilter's is constructed using Operator.MUST_PASS_ONE.
 */
@Category(MediumTests.class)
public class TestFilterListOrOperatorWithBlkCnt {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFilterListOrOperatorWithBlkCnt.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestFilterListOrOperatorWithBlkCnt.class);

    private byte[] family = Bytes.toBytes("family");

    private byte[] qf = Bytes.toBytes("qf");

    private byte[] value = Bytes.toBytes("val");

    private TableName tableName;

    private int numRows = 10000;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMultiRowRangeWithFilterListOrOperatorWithBlkCnt() throws IOException {
        tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFilterListOrOperatorWithBlkCnt.TEST_UTIL.createTable(tableName, family, Integer.MAX_VALUE);
        generateRows(numRows, ht, family, qf, value);
        Scan scan = new Scan();
        scan.setMaxVersions();
        long blocksStart = TestFilterListOrOperatorWithBlkCnt.getBlkAccessCount();
        List<RowRange> ranges1 = new ArrayList<>();
        ranges1.add(new RowRange(Bytes.toBytes(10), true, Bytes.toBytes(15), false));
        ranges1.add(new RowRange(Bytes.toBytes(9980), true, Bytes.toBytes(9985), false));
        MultiRowRangeFilter filter1 = new MultiRowRangeFilter(ranges1);
        List<RowRange> ranges2 = new ArrayList<>();
        ranges2.add(new RowRange(Bytes.toBytes(15), true, Bytes.toBytes(20), false));
        ranges2.add(new RowRange(Bytes.toBytes(9985), true, Bytes.toBytes(9990), false));
        MultiRowRangeFilter filter2 = new MultiRowRangeFilter(ranges2);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE);
        filterList.addFilter(filter1);
        filterList.addFilter(filter2);
        scan.setFilter(filterList);
        int resultsSize = getResultsSize(ht, scan);
        TestFilterListOrOperatorWithBlkCnt.LOG.info((("found " + resultsSize) + " results"));
        List<Cell> results1 = getScanResult(Bytes.toBytes(10), Bytes.toBytes(20), ht);
        List<Cell> results2 = getScanResult(Bytes.toBytes(9980), Bytes.toBytes(9990), ht);
        Assert.assertEquals(((results1.size()) + (results2.size())), resultsSize);
        long blocksEnd = TestFilterListOrOperatorWithBlkCnt.getBlkAccessCount();
        long diff = blocksEnd - blocksStart;
        TestFilterListOrOperatorWithBlkCnt.LOG.info(("Diff in number of blocks " + diff));
        /* Verify that we don't read all the blocks (8 in total). */
        Assert.assertEquals(4, diff);
        ht.close();
    }
}

