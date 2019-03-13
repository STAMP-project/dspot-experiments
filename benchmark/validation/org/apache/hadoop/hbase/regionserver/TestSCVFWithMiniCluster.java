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
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/* This test verifies that the scenarios illustrated by HBASE-10850 work
w.r.t. essential column family optimization
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestSCVFWithMiniCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSCVFWithMiniCluster.class);

    private static final TableName HBASE_TABLE_NAME = TableName.valueOf("TestSCVFWithMiniCluster");

    private static final byte[] FAMILY_A = Bytes.toBytes("a");

    private static final byte[] FAMILY_B = Bytes.toBytes("b");

    private static final byte[] QUALIFIER_FOO = Bytes.toBytes("foo");

    private static final byte[] QUALIFIER_BAR = Bytes.toBytes("bar");

    private static Table htable;

    private static Filter scanFilter;

    private int expected = 1;

    /**
     * Test the filter by adding all columns of family A in the scan. (OK)
     */
    @Test
    public void scanWithAllQualifiersOfFamiliyA() throws IOException {
        /* Given */
        Scan scan = new Scan();
        scan.addFamily(TestSCVFWithMiniCluster.FAMILY_A);
        scan.setFilter(TestSCVFWithMiniCluster.scanFilter);
        verify(scan);
    }

    /**
     * Test the filter by adding all columns of family A and B in the scan. (KO: row '3' without
     * 'a:foo' qualifier is returned)
     */
    @Test
    public void scanWithAllQualifiersOfBothFamilies() throws IOException {
        /* When */
        Scan scan = new Scan();
        scan.setFilter(TestSCVFWithMiniCluster.scanFilter);
        verify(scan);
    }

    /**
     * Test the filter by adding 2 columns of family A and 1 column of family B in the scan. (KO: row
     * '3' without 'a:foo' qualifier is returned)
     */
    @Test
    public void scanWithSpecificQualifiers1() throws IOException {
        /* When */
        Scan scan = new Scan();
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_A, TestSCVFWithMiniCluster.QUALIFIER_FOO);
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_A, TestSCVFWithMiniCluster.QUALIFIER_BAR);
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_B, TestSCVFWithMiniCluster.QUALIFIER_BAR);
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_B, TestSCVFWithMiniCluster.QUALIFIER_FOO);
        scan.setFilter(TestSCVFWithMiniCluster.scanFilter);
        verify(scan);
    }

    /**
     * Test the filter by adding 1 column of family A (the one used in the filter) and 1 column of
     * family B in the scan. (OK)
     */
    @Test
    public void scanWithSpecificQualifiers2() throws IOException {
        /* When */
        Scan scan = new Scan();
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_A, TestSCVFWithMiniCluster.QUALIFIER_FOO);
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_B, TestSCVFWithMiniCluster.QUALIFIER_BAR);
        scan.setFilter(TestSCVFWithMiniCluster.scanFilter);
        verify(scan);
    }

    /**
     * Test the filter by adding 2 columns of family A in the scan. (OK)
     */
    @Test
    public void scanWithSpecificQualifiers3() throws IOException {
        /* When */
        Scan scan = new Scan();
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_A, TestSCVFWithMiniCluster.QUALIFIER_FOO);
        scan.addColumn(TestSCVFWithMiniCluster.FAMILY_A, TestSCVFWithMiniCluster.QUALIFIER_BAR);
        scan.setFilter(TestSCVFWithMiniCluster.scanFilter);
        verify(scan);
    }
}

