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
package org.apache.hadoop.hbase;


import HConstants.VERSIONS;
import java.io.IOException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Verify that the HColumnDescriptor version is set correctly by default, hbase-site.xml, and user
 * input
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestHColumnDescriptorDefaultVersions {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHColumnDescriptorDefaultVersions.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = null;

    private static final byte[] FAMILY = Bytes.toBytes("cf0");

    @Test
    public void testCreateTableWithDefault() throws IOException {
        Admin admin = TestHColumnDescriptorDefaultVersions.TEST_UTIL.getAdmin();
        // Create a table with one family
        HTableDescriptor baseHtd = new HTableDescriptor(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        HColumnDescriptor hcd = new HColumnDescriptor(TestHColumnDescriptorDefaultVersions.FAMILY);
        baseHtd.addFamily(hcd);
        admin.createTable(baseHtd);
        admin.disableTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        try {
            // Verify the column descriptor
            verifyHColumnDescriptor(1, TestHColumnDescriptorDefaultVersions.TABLE_NAME, TestHColumnDescriptorDefaultVersions.FAMILY);
        } finally {
            admin.deleteTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        }
    }

    @Test
    public void testCreateTableWithDefaultFromConf() throws Exception {
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.shutdownMiniCluster();
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.getConfiguration().setInt("hbase.column.max.version", 3);
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.startMiniCluster(1);
        Admin admin = TestHColumnDescriptorDefaultVersions.TEST_UTIL.getAdmin();
        // Create a table with one family
        HTableDescriptor baseHtd = new HTableDescriptor(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        HColumnDescriptor hcd = new HColumnDescriptor(TestHColumnDescriptorDefaultVersions.FAMILY);
        hcd.setMaxVersions(TestHColumnDescriptorDefaultVersions.TEST_UTIL.getConfiguration().getInt("hbase.column.max.version", 1));
        baseHtd.addFamily(hcd);
        admin.createTable(baseHtd);
        admin.disableTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        try {
            // Verify the column descriptor
            verifyHColumnDescriptor(3, TestHColumnDescriptorDefaultVersions.TABLE_NAME, TestHColumnDescriptorDefaultVersions.FAMILY);
        } finally {
            admin.deleteTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        }
    }

    @Test
    public void testCreateTableWithSetVersion() throws Exception {
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.shutdownMiniCluster();
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.getConfiguration().setInt("hbase.column.max.version", 3);
        TestHColumnDescriptorDefaultVersions.TEST_UTIL.startMiniCluster(1);
        Admin admin = TestHColumnDescriptorDefaultVersions.TEST_UTIL.getAdmin();
        // Create a table with one family
        HTableDescriptor baseHtd = new HTableDescriptor(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        HColumnDescriptor hcd = new HColumnDescriptor(TestHColumnDescriptorDefaultVersions.FAMILY);
        hcd.setMaxVersions(5);
        baseHtd.addFamily(hcd);
        admin.createTable(baseHtd);
        admin.disableTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        try {
            // Verify the column descriptor
            verifyHColumnDescriptor(5, TestHColumnDescriptorDefaultVersions.TABLE_NAME, TestHColumnDescriptorDefaultVersions.FAMILY);
        } finally {
            admin.deleteTable(TestHColumnDescriptorDefaultVersions.TABLE_NAME);
        }
    }

    @Test
    public void testHColumnDescriptorCachedMaxVersions() throws Exception {
        HColumnDescriptor hcd = new HColumnDescriptor(TestHColumnDescriptorDefaultVersions.FAMILY);
        hcd.setMaxVersions(5);
        // Verify the max version
        Assert.assertEquals(5, hcd.getMaxVersions());
        // modify the max version
        hcd.setValue(Bytes.toBytes(VERSIONS), Bytes.toBytes("8"));
        // Verify the max version
        Assert.assertEquals(8, hcd.getMaxVersions());
    }
}

