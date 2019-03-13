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


import HConstants.EMPTY_START_ROW;
import NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.favored.FavoredNodesManager;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ClientTests.class, MediumTests.class })
public class TestTableFavoredNodes {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableFavoredNodes.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableFavoredNodes.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int WAIT_TIMEOUT = 60000;

    private static final int SLAVES = 8;

    private FavoredNodesManager fnm;

    private Admin admin;

    private final byte[][] splitKeys = new byte[][]{ Bytes.toBytes(1), Bytes.toBytes(9) };

    private final int NUM_REGIONS = (splitKeys.length) + 1;

    @Rule
    public TestName name = new TestName();

    /* Create a table with FN enabled and check if all its regions have favored nodes set. */
    @Test
    public void testCreateTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestTableFavoredNodes.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"), splitKeys);
        TestTableFavoredNodes.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        // All regions should have favored nodes
        checkIfFavoredNodeInformationIsCorrect(tableName);
        List<HRegionInfo> regions = admin.getTableRegions(tableName);
        TestTableFavoredNodes.TEST_UTIL.deleteTable(tableName);
        checkNoFNForDeletedTable(regions);
    }

    /* Checks if favored node information is removed on table truncation. */
    @Test
    public void testTruncateTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestTableFavoredNodes.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"), splitKeys);
        TestTableFavoredNodes.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        // All regions should have favored nodes
        checkIfFavoredNodeInformationIsCorrect(tableName);
        List<HRegionInfo> regions = admin.getTableRegions(tableName);
        TestTableFavoredNodes.TEST_UTIL.truncateTable(tableName, true);
        checkNoFNForDeletedTable(regions);
        checkIfFavoredNodeInformationIsCorrect(tableName);
        regions = admin.getTableRegions(tableName);
        TestTableFavoredNodes.TEST_UTIL.truncateTable(tableName, false);
        checkNoFNForDeletedTable(regions);
        TestTableFavoredNodes.TEST_UTIL.deleteTable(tableName);
    }

    /* Check if daughters inherit at-least 2 FN from parent after region split. */
    @Test
    public void testSplitTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestTableFavoredNodes.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"), splitKeys);
        TestTableFavoredNodes.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        final int numberOfRegions = admin.getTableRegions(t.getName()).size();
        checkIfFavoredNodeInformationIsCorrect(tableName);
        byte[] splitPoint = Bytes.toBytes(0);
        RegionLocator locator = TestTableFavoredNodes.TEST_UTIL.getConnection().getRegionLocator(tableName);
        HRegionInfo parent = locator.getRegionLocation(splitPoint).getRegionInfo();
        List<ServerName> parentFN = fnm.getFavoredNodes(parent);
        Assert.assertNotNull(("FN should not be null for region: " + parent), parentFN);
        TestTableFavoredNodes.LOG.info("SPLITTING TABLE");
        admin.split(tableName, splitPoint);
        TestTableFavoredNodes.TEST_UTIL.waitUntilNoRegionsInTransition(TestTableFavoredNodes.WAIT_TIMEOUT);
        TestTableFavoredNodes.LOG.info("FINISHED WAITING ON RIT");
        waitUntilTableRegionCountReached(tableName, (numberOfRegions + 1));
        // All regions should have favored nodes    checkIfFavoredNodeInformationIsCorrect(tableName);
        // Get the daughters of parent.
        HRegionInfo daughter1 = locator.getRegionLocation(parent.getStartKey(), true).getRegionInfo();
        List<ServerName> daughter1FN = fnm.getFavoredNodes(daughter1);
        HRegionInfo daughter2 = locator.getRegionLocation(splitPoint, true).getRegionInfo();
        List<ServerName> daughter2FN = fnm.getFavoredNodes(daughter2);
        checkIfDaughterInherits2FN(parentFN, daughter1FN);
        checkIfDaughterInherits2FN(parentFN, daughter2FN);
        Assert.assertEquals("Daughter's PRIMARY FN should be PRIMARY of parent", parentFN.get(PRIMARY.ordinal()), daughter1FN.get(PRIMARY.ordinal()));
        Assert.assertEquals("Daughter's SECONDARY FN should be SECONDARY of parent", parentFN.get(SECONDARY.ordinal()), daughter1FN.get(SECONDARY.ordinal()));
        Assert.assertEquals("Daughter's PRIMARY FN should be PRIMARY of parent", parentFN.get(PRIMARY.ordinal()), daughter2FN.get(PRIMARY.ordinal()));
        Assert.assertEquals("Daughter's SECONDARY FN should be TERTIARY of parent", parentFN.get(TERTIARY.ordinal()), daughter2FN.get(SECONDARY.ordinal()));
        // Major compact table and run catalog janitor. Parent's FN should be removed
        TestTableFavoredNodes.TEST_UTIL.getMiniHBaseCluster().compact(tableName, true);
        admin.runCatalogScan();
        // Catalog cleanup is async. Wait on procedure to finish up.
        ProcedureTestingUtility.waitAllProcedures(TestTableFavoredNodes.TEST_UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor());
        // assertEquals("Parent region should have been cleaned", 1, admin.runCatalogScan());
        Assert.assertNull("Parent FN should be null", fnm.getFavoredNodes(parent));
        List<HRegionInfo> regions = admin.getTableRegions(tableName);
        // Split and Table Disable interfere with each other around region replicas
        // TODO. Meantime pause a few seconds.
        Threads.sleep(2000);
        TestTableFavoredNodes.LOG.info("STARTING DELETE");
        TestTableFavoredNodes.TEST_UTIL.deleteTable(tableName);
        checkNoFNForDeletedTable(regions);
    }

    /* Check if merged region inherits FN from one of its regions. */
    @Test
    public void testMergeTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestTableFavoredNodes.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"), splitKeys);
        TestTableFavoredNodes.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        checkIfFavoredNodeInformationIsCorrect(tableName);
        RegionLocator locator = TestTableFavoredNodes.TEST_UTIL.getConnection().getRegionLocator(tableName);
        HRegionInfo regionA = locator.getRegionLocation(EMPTY_START_ROW).getRegionInfo();
        HRegionInfo regionB = locator.getRegionLocation(splitKeys[0]).getRegionInfo();
        List<ServerName> regionAFN = fnm.getFavoredNodes(regionA);
        TestTableFavoredNodes.LOG.info(((("regionA: " + (regionA.getEncodedName())) + " with FN: ") + (fnm.getFavoredNodes(regionA))));
        TestTableFavoredNodes.LOG.info(((("regionB: " + (regionA.getEncodedName())) + " with FN: ") + (fnm.getFavoredNodes(regionB))));
        int countOfRegions = MetaTableAccessor.getRegionCount(TestTableFavoredNodes.TEST_UTIL.getConfiguration(), tableName);
        admin.mergeRegionsAsync(regionA.getEncodedNameAsBytes(), regionB.getEncodedNameAsBytes(), false).get(60, TimeUnit.SECONDS);
        TestTableFavoredNodes.TEST_UTIL.waitUntilNoRegionsInTransition(TestTableFavoredNodes.WAIT_TIMEOUT);
        waitUntilTableRegionCountReached(tableName, (countOfRegions - 1));
        // All regions should have favored nodes
        checkIfFavoredNodeInformationIsCorrect(tableName);
        HRegionInfo mergedRegion = locator.getRegionLocation(EMPTY_START_ROW).getRegionInfo();
        List<ServerName> mergedFN = fnm.getFavoredNodes(mergedRegion);
        Assert.assertArrayEquals("Merged region doesn't match regionA's FN", regionAFN.toArray(), mergedFN.toArray());
        // Major compact table and run catalog janitor. Parent FN should be removed
        TestTableFavoredNodes.TEST_UTIL.getMiniHBaseCluster().compact(tableName, true);
        Assert.assertEquals("Merge parents should have been cleaned", 1, admin.runCatalogScan());
        // Catalog cleanup is async. Wait on procedure to finish up.
        ProcedureTestingUtility.waitAllProcedures(TestTableFavoredNodes.TEST_UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor());
        Assert.assertNull("Parent FN should be null", fnm.getFavoredNodes(regionA));
        Assert.assertNull("Parent FN should be null", fnm.getFavoredNodes(regionB));
        List<HRegionInfo> regions = admin.getTableRegions(tableName);
        TestTableFavoredNodes.TEST_UTIL.deleteTable(tableName);
        checkNoFNForDeletedTable(regions);
    }

    /* Check favored nodes for system tables */
    @Test
    public void testSystemTables() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestTableFavoredNodes.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"), splitKeys);
        TestTableFavoredNodes.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        // All regions should have favored nodes
        checkIfFavoredNodeInformationIsCorrect(tableName);
        for (TableName sysTable : admin.listTableNamesByNamespace(SYSTEM_NAMESPACE_NAME_STR)) {
            List<HRegionInfo> regions = admin.getTableRegions(sysTable);
            for (HRegionInfo region : regions) {
                Assert.assertNull("FN should be null for sys region", fnm.getFavoredNodes(region));
            }
        }
        TestTableFavoredNodes.TEST_UTIL.deleteTable(tableName);
    }
}

