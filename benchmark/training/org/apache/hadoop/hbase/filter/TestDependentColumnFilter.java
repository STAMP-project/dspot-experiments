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
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter.ReturnCode;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ FilterTests.class, SmallTests.class })
public class TestDependentColumnFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDependentColumnFilter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestDependentColumnFilter.class);

    private static final byte[][] ROWS = new byte[][]{ Bytes.toBytes("test1"), Bytes.toBytes("test2") };

    private static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("familyOne"), Bytes.toBytes("familyTwo") };

    private static final long STAMP_BASE = System.currentTimeMillis();

    private static final long[] STAMPS = new long[]{ (TestDependentColumnFilter.STAMP_BASE) - 100, (TestDependentColumnFilter.STAMP_BASE) - 200, (TestDependentColumnFilter.STAMP_BASE) - 300 };

    private static final byte[] QUALIFIER = Bytes.toBytes("qualifier");

    private static final byte[][] BAD_VALS = new byte[][]{ Bytes.toBytes("bad1"), Bytes.toBytes("bad2"), Bytes.toBytes("bad3") };

    private static final byte[] MATCH_VAL = Bytes.toBytes("match");

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    List<KeyValue> testVals;

    private HRegion region;

    /**
     * Test scans using a DependentColumnFilter
     */
    @Test
    public void testScans() throws Exception {
        Filter filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER);
        Scan scan = new Scan();
        scan.setFilter(filter);
        scan.setMaxVersions(Integer.MAX_VALUE);
        verifyScan(scan, 2, 8);
        // drop the filtering cells
        filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER, true);
        scan = new Scan();
        scan.setFilter(filter);
        scan.setMaxVersions(Integer.MAX_VALUE);
        verifyScan(scan, 2, 3);
        // include a comparator operation
        filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER, false, CompareOperator.EQUAL, new BinaryComparator(TestDependentColumnFilter.MATCH_VAL));
        scan = new Scan();
        scan.setFilter(filter);
        scan.setMaxVersions(Integer.MAX_VALUE);
        /* expecting to get the following 3 cells
        row 0
          put.add(FAMILIES[0], QUALIFIER, STAMPS[2], MATCH_VAL);
          put.add(FAMILIES[1], QUALIFIER, STAMPS[2], BAD_VALS[2]);
        row 1
          put.add(FAMILIES[0], QUALIFIER, STAMPS[2], MATCH_VAL);
         */
        verifyScan(scan, 2, 3);
        // include a comparator operation and drop comparator
        filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER, true, CompareOperator.EQUAL, new BinaryComparator(TestDependentColumnFilter.MATCH_VAL));
        scan = new Scan();
        scan.setFilter(filter);
        scan.setMaxVersions(Integer.MAX_VALUE);
        /* expecting to get the following 1 cell
        row 0
          put.add(FAMILIES[1], QUALIFIER, STAMPS[2], BAD_VALS[2]);
         */
        verifyScan(scan, 1, 1);
    }

    /**
     * Test that the filter correctly drops rows without a corresponding timestamp
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilterDropping() throws Exception {
        Filter filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER);
        List<Cell> accepted = new ArrayList<>();
        for (Cell val : testVals) {
            if ((filter.filterCell(val)) == (ReturnCode.INCLUDE)) {
                accepted.add(val);
            }
        }
        Assert.assertEquals("check all values accepted from filterCell", 5, accepted.size());
        filter.filterRowCells(accepted);
        Assert.assertEquals("check filterRow(List<KeyValue>) dropped cell without corresponding column entry", 4, accepted.size());
        // start do it again with dependent column dropping on
        filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[1], TestDependentColumnFilter.QUALIFIER, true);
        accepted.clear();
        for (KeyValue val : testVals) {
            if ((filter.filterCell(val)) == (ReturnCode.INCLUDE)) {
                accepted.add(val);
            }
        }
        Assert.assertEquals("check the filtering column cells got dropped", 2, accepted.size());
        filter.filterRowCells(accepted);
        Assert.assertEquals("check cell retention", 2, accepted.size());
    }

    /**
     * Test for HBASE-8794. Avoid NullPointerException in DependentColumnFilter.toString().
     */
    @Test
    public void testToStringWithNullComparator() {
        // Test constructor that implicitly sets a null comparator
        Filter filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER);
        Assert.assertNotNull(filter.toString());
        Assert.assertTrue("check string contains 'null' as compatator is null", filter.toString().contains("null"));
        // Test constructor with explicit null comparator
        filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER, true, CompareOperator.EQUAL, null);
        Assert.assertNotNull(filter.toString());
        Assert.assertTrue("check string contains 'null' as compatator is null", filter.toString().contains("null"));
    }

    @Test
    public void testToStringWithNonNullComparator() {
        Filter filter = new DependentColumnFilter(TestDependentColumnFilter.FAMILIES[0], TestDependentColumnFilter.QUALIFIER, true, CompareOperator.EQUAL, new BinaryComparator(TestDependentColumnFilter.MATCH_VAL));
        Assert.assertNotNull(filter.toString());
        Assert.assertTrue("check string contains comparator value", filter.toString().contains("match"));
    }
}

