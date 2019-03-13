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


import CellComparatorImpl.COMPARATOR;
import Filter.ReturnCode;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for {@link SingleColumnValueExcludeFilter}. Because this filter
 * extends {@link SingleColumnValueFilter}, only the added functionality is
 * tested. That is, method filterCell(Cell).
 */
@Category({ FilterTests.class, SmallTests.class })
public class TestSingleColumnValueExcludeFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSingleColumnValueExcludeFilter.class);

    private static final byte[] ROW = Bytes.toBytes("test");

    private static final byte[] COLUMN_FAMILY = Bytes.toBytes("test");

    private static final byte[] COLUMN_QUALIFIER = Bytes.toBytes("foo");

    private static final byte[] COLUMN_QUALIFIER_2 = Bytes.toBytes("foo_2");

    private static final byte[] VAL_1 = Bytes.toBytes("a");

    private static final byte[] VAL_2 = Bytes.toBytes("ab");

    /**
     * Test the overridden functionality of filterCell(Cell)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilterCell() throws Exception {
        Filter filter = new SingleColumnValueExcludeFilter(TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER, CompareOperator.EQUAL, TestSingleColumnValueExcludeFilter.VAL_1);
        // A 'match' situation
        List<Cell> kvs = new ArrayList<>();
        KeyValue c = new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER_2, TestSingleColumnValueExcludeFilter.VAL_1);
        kvs.add(new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER_2, TestSingleColumnValueExcludeFilter.VAL_1));
        kvs.add(new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER, TestSingleColumnValueExcludeFilter.VAL_1));
        kvs.add(new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER_2, TestSingleColumnValueExcludeFilter.VAL_1));
        filter.filterRowCells(kvs);
        Assert.assertEquals("resultSize", 2, kvs.size());
        Assert.assertTrue("leftKV1", ((COMPARATOR.compare(kvs.get(0), c)) == 0));
        Assert.assertTrue("leftKV2", ((COMPARATOR.compare(kvs.get(1), c)) == 0));
        Assert.assertFalse("allRemainingWhenMatch", filter.filterAllRemaining());
        // A 'mismatch' situation
        filter.reset();
        // INCLUDE expected because test column has not yet passed
        c = new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER_2, TestSingleColumnValueExcludeFilter.VAL_1);
        Assert.assertTrue("otherColumn", ((filter.filterCell(c)) == (ReturnCode.INCLUDE)));
        // Test column will pass (wont match), expect NEXT_ROW
        c = new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER, TestSingleColumnValueExcludeFilter.VAL_2);
        Assert.assertTrue("testedMismatch", ((filter.filterCell(c)) == (ReturnCode.NEXT_ROW)));
        // After a mismatch (at least with LatestVersionOnly), subsequent columns are EXCLUDE
        c = new KeyValue(TestSingleColumnValueExcludeFilter.ROW, TestSingleColumnValueExcludeFilter.COLUMN_FAMILY, TestSingleColumnValueExcludeFilter.COLUMN_QUALIFIER_2, TestSingleColumnValueExcludeFilter.VAL_1);
        Assert.assertTrue("otherColumn", ((filter.filterCell(c)) == (ReturnCode.NEXT_ROW)));
    }
}

