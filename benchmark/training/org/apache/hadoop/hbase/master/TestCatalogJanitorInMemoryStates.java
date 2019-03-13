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
package org.apache.hadoop.hbase.master;


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.MetaMockingUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.assignment.AssignmentManager;
import org.apache.hadoop.hbase.testclassification.MasterTests;
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


@Category({ MasterTests.class, MediumTests.class })
public class TestCatalogJanitorInMemoryStates {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCatalogJanitorInMemoryStates.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCatalogJanitorInMemoryStates.class);

    @Rule
    public final TestName name = new TestName();

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] VALUE = Bytes.toBytes("testValue");

    /**
     * Test clearing a split parent from memory.
     */
    @Test
    public void testInMemoryParentCleanup() throws IOException, InterruptedException {
        final AssignmentManager am = TestCatalogJanitorInMemoryStates.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager();
        final ServerManager sm = TestCatalogJanitorInMemoryStates.TEST_UTIL.getHBaseCluster().getMaster().getServerManager();
        final CatalogJanitor janitor = TestCatalogJanitorInMemoryStates.TEST_UTIL.getHBaseCluster().getMaster().getCatalogJanitor();
        Admin admin = TestCatalogJanitorInMemoryStates.TEST_UTIL.getAdmin();
        admin.enableCatalogJanitor(false);
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestCatalogJanitorInMemoryStates.TEST_UTIL.createTable(tableName, TestCatalogJanitorInMemoryStates.FAMILY);
        int rowCount = TestCatalogJanitorInMemoryStates.TEST_UTIL.loadTable(t, TestCatalogJanitorInMemoryStates.FAMILY, false);
        RegionLocator locator = TestCatalogJanitorInMemoryStates.TEST_UTIL.getConnection().getRegionLocator(tableName);
        List<HRegionLocation> allRegionLocations = locator.getAllRegionLocations();
        // We need to create a valid split with daughter regions
        HRegionLocation parent = allRegionLocations.get(0);
        List<HRegionLocation> daughters = splitRegion(parent.getRegionInfo());
        TestCatalogJanitorInMemoryStates.LOG.info(("Parent region: " + parent));
        TestCatalogJanitorInMemoryStates.LOG.info(("Daughter regions: " + daughters));
        Assert.assertNotNull(("Should have found daughter regions for " + parent), daughters);
        Assert.assertTrue("Parent region should exist in RegionStates", am.getRegionStates().isRegionInRegionStates(parent.getRegionInfo()));
        Assert.assertTrue("Parent region should exist in ServerManager", sm.isRegionInServerManagerStates(parent.getRegionInfo()));
        // clean the parent
        Result r = MetaMockingUtil.getMetaTableRowResult(parent.getRegionInfo(), null, daughters.get(0).getRegionInfo(), daughters.get(1).getRegionInfo());
        janitor.cleanParent(parent.getRegionInfo(), r);
        Assert.assertFalse("Parent region should have been removed from RegionStates", am.getRegionStates().isRegionInRegionStates(parent.getRegionInfo()));
        Assert.assertFalse("Parent region should have been removed from ServerManager", sm.isRegionInServerManagerStates(parent.getRegionInfo()));
    }
}

