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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Testcase for newly added feature in HBASE-17143, such as startRow and stopRow
 * inclusive/exclusive, limit for rows, etc.
 */
@RunWith(Parameterized.class)
@Category({ MediumTests.class, ClientTests.class })
public class TestScannersFromClientSide2 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannersFromClientSide2.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("scan");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] CQ1 = Bytes.toBytes("cq1");

    private static byte[] CQ2 = Bytes.toBytes("cq2");

    @Parameterized.Parameter(0)
    public boolean batch;

    @Parameterized.Parameter(1)
    public boolean smallResultSize;

    @Parameterized.Parameter(2)
    public boolean allowPartial;

    @Test
    public void testScanWithLimit() throws Exception {
        testScan(1, true, 998, false, 900);// from first region to last region

        testScan(123, true, 345, true, 100);
        testScan(234, true, 456, false, 100);
        testScan(345, false, 567, true, 100);
        testScan(456, false, 678, false, 100);
    }

    @Test
    public void testScanWithLimitGreaterThanActualCount() throws Exception {
        testScan(1, true, 998, false, 1000);// from first region to last region

        testScan(123, true, 345, true, 200);
        testScan(234, true, 456, false, 200);
        testScan(345, false, 567, true, 200);
        testScan(456, false, 678, false, 200);
    }

    @Test
    public void testReversedScanWithLimit() throws Exception {
        testReversedScan(998, true, 1, false, 900);// from last region to first region

        testReversedScan(543, true, 321, true, 100);
        testReversedScan(654, true, 432, false, 100);
        testReversedScan(765, false, 543, true, 100);
        testReversedScan(876, false, 654, false, 100);
    }

    @Test
    public void testReversedScanWithLimitGreaterThanActualCount() throws Exception {
        testReversedScan(998, true, 1, false, 1000);// from last region to first region

        testReversedScan(543, true, 321, true, 200);
        testReversedScan(654, true, 432, false, 200);
        testReversedScan(765, false, 543, true, 200);
        testReversedScan(876, false, 654, false, 200);
    }

    @Test
    public void testStartRowStopRowInclusive() throws Exception {
        testScan(1, true, 998, false, (-1));// from first region to last region

        testScan(123, true, 345, true, (-1));
        testScan(234, true, 456, false, (-1));
        testScan(345, false, 567, true, (-1));
        testScan(456, false, 678, false, (-1));
    }

    @Test
    public void testReversedStartRowStopRowInclusive() throws Exception {
        testReversedScan(998, true, 1, false, (-1));// from last region to first region

        testReversedScan(543, true, 321, true, (-1));
        testReversedScan(654, true, 432, false, (-1));
        testReversedScan(765, false, 543, true, (-1));
        testReversedScan(876, false, 654, false, (-1));
    }
}

