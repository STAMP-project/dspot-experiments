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
package org.apache.hadoop.hbase.util;


import HBaseFsck.cmp;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.HBaseFsck.HbckInfo;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the comparator used by Hbck.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestHBaseFsckComparator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseFsckComparator.class);

    TableName table = TableName.valueOf("table1");

    TableName table2 = TableName.valueOf("table2");

    byte[] keyStart = Bytes.toBytes("");

    byte[] keyA = Bytes.toBytes("A");

    byte[] keyB = Bytes.toBytes("B");

    byte[] keyC = Bytes.toBytes("C");

    byte[] keyEnd = Bytes.toBytes("");

    @Test
    public void testEquals() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyB, 0);
        HbckInfo hi2 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyB, 0);
        Assert.assertEquals(0, cmp.compare(hi1, hi2));
        Assert.assertEquals(0, cmp.compare(hi2, hi1));
    }

    @Test
    public void testEqualsInstance() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyB, 0);
        HbckInfo hi2 = hi1;
        Assert.assertEquals(0, cmp.compare(hi1, hi2));
        Assert.assertEquals(0, cmp.compare(hi2, hi1));
    }

    @Test
    public void testDiffTable() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyC, 0);
        HbckInfo hi2 = TestHBaseFsckComparator.genHbckInfo(table2, keyA, keyC, 0);
        Assert.assertTrue(((cmp.compare(hi1, hi2)) < 0));
        Assert.assertTrue(((cmp.compare(hi2, hi1)) > 0));
    }

    @Test
    public void testDiffStartKey() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyStart, keyC, 0);
        HbckInfo hi2 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyC, 0);
        Assert.assertTrue(((cmp.compare(hi1, hi2)) < 0));
        Assert.assertTrue(((cmp.compare(hi2, hi1)) > 0));
    }

    @Test
    public void testDiffEndKey() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyB, 0);
        HbckInfo hi2 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyC, 0);
        Assert.assertTrue(((cmp.compare(hi1, hi2)) < 0));
        Assert.assertTrue(((cmp.compare(hi2, hi1)) > 0));
    }

    @Test
    public void testAbsEndKey() {
        HbckInfo hi1 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyC, 0);
        HbckInfo hi2 = TestHBaseFsckComparator.genHbckInfo(table, keyA, keyEnd, 0);
        Assert.assertTrue(((cmp.compare(hi1, hi2)) < 0));
        Assert.assertTrue(((cmp.compare(hi2, hi1)) > 0));
    }
}

