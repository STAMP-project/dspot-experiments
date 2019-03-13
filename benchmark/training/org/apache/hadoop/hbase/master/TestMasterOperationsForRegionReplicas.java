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


import JVMClusterUtil.RegionServerThread;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionReplicaUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterOperationsForRegionReplicas {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterOperationsForRegionReplicas.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionPlacement.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Connection CONNECTION = null;

    private static Admin ADMIN;

    private static int numSlaves = 2;

    private static Configuration conf;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCreateTableWithSingleReplica() throws Exception {
        final int numRegions = 3;
        final int numReplica = 1;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            HTableDescriptor desc = new HTableDescriptor(tableName);
            desc.setRegionReplication(numReplica);
            desc.addFamily(new HColumnDescriptor("family"));
            TestMasterOperationsForRegionReplicas.ADMIN.createTable(desc, Bytes.toBytes("A"), Bytes.toBytes("Z"), numRegions);
            validateNumberOfRowsInMeta(tableName, numRegions, TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            List<RegionInfo> hris = MetaTableAccessor.getTableRegions(TestMasterOperationsForRegionReplicas.ADMIN.getConnection(), tableName);
            assert (hris.size()) == (numRegions * numReplica);
        } finally {
            TestMasterOperationsForRegionReplicas.ADMIN.disableTable(tableName);
            TestMasterOperationsForRegionReplicas.ADMIN.deleteTable(tableName);
        }
    }

    @Test
    public void testCreateTableWithMultipleReplicas() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final int numRegions = 3;
        final int numReplica = 2;
        try {
            HTableDescriptor desc = new HTableDescriptor(tableName);
            desc.setRegionReplication(numReplica);
            desc.addFamily(new HColumnDescriptor("family"));
            TestMasterOperationsForRegionReplicas.ADMIN.createTable(desc, Bytes.toBytes("A"), Bytes.toBytes("Z"), numRegions);
            TestMasterOperationsForRegionReplicas.TEST_UTIL.waitTableEnabled(tableName);
            validateNumberOfRowsInMeta(tableName, numRegions, TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            List<RegionInfo> hris = MetaTableAccessor.getTableRegions(TestMasterOperationsForRegionReplicas.ADMIN.getConnection(), tableName);
            assert (hris.size()) == (numRegions * numReplica);
            // check that the master created expected number of RegionState objects
            for (int i = 0; i < numRegions; i++) {
                for (int j = 0; j < numReplica; j++) {
                    RegionInfo replica = RegionReplicaUtil.getRegionInfoForReplica(hris.get(i), j);
                    RegionState state = TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionState(replica);
                    assert state != null;
                }
            }
            List<Result> metaRows = MetaTableAccessor.fullScanRegions(TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            int numRows = 0;
            for (Result result : metaRows) {
                RegionLocations locations = MetaTableAccessor.getRegionLocations(result);
                RegionInfo hri = locations.getRegionLocation().getRegionInfo();
                if (!(hri.getTable().equals(tableName)))
                    continue;

                numRows += 1;
                HRegionLocation[] servers = locations.getRegionLocations();
                // have two locations for the replicas of a region, and the locations should be different
                assert (servers.length) == 2;
                assert !(servers[0].equals(servers[1]));
            }
            assert numRows == numRegions;
            // The same verification of the meta as above but with the SnapshotOfRegionAssignmentFromMeta
            // class
            validateFromSnapshotFromMeta(TestMasterOperationsForRegionReplicas.TEST_UTIL, tableName, numRegions, numReplica, TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            // Now kill the master, restart it and see if the assignments are kept
            ServerName master = TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseClusterInterface().getClusterMetrics().getMasterName();
            TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseClusterInterface().stopMaster(master);
            TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseClusterInterface().waitForMasterToStop(master, 30000);
            TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseClusterInterface().startMaster(master.getHostname(), master.getPort());
            TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseClusterInterface().waitForActiveAndReadyMaster();
            for (int i = 0; i < numRegions; i++) {
                for (int j = 0; j < numReplica; j++) {
                    RegionInfo replica = RegionReplicaUtil.getRegionInfoForReplica(hris.get(i), j);
                    RegionState state = TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionState(replica);
                    assert state != null;
                }
            }
            validateFromSnapshotFromMeta(TestMasterOperationsForRegionReplicas.TEST_UTIL, tableName, numRegions, numReplica, TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            // Now shut the whole cluster down, and verify the assignments are kept so that the
            // availability constraints are met. MiniHBaseCluster chooses arbitrary ports on each
            // restart. This messes with our being able to test that we retain locality. Therefore,
            // figure current cluster ports and pass them in on next cluster start so new cluster comes
            // up at same coordinates -- and the assignment retention logic has a chance to cut in.
            List<Integer> rsports = new ArrayList<>();
            for (JVMClusterUtil.RegionServerThread rst : TestMasterOperationsForRegionReplicas.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads()) {
                rsports.add(rst.getRegionServer().getRpcServer().getListenerAddress().getPort());
            }
            TestMasterOperationsForRegionReplicas.TEST_UTIL.shutdownMiniHBaseCluster();
            StartMiniClusterOption option = StartMiniClusterOption.builder().numRegionServers(TestMasterOperationsForRegionReplicas.numSlaves).rsPorts(rsports).build();
            TestMasterOperationsForRegionReplicas.TEST_UTIL.startMiniHBaseCluster(option);
            TestMasterOperationsForRegionReplicas.TEST_UTIL.waitTableAvailable(tableName);
            validateFromSnapshotFromMeta(TestMasterOperationsForRegionReplicas.TEST_UTIL, tableName, numRegions, numReplica, TestMasterOperationsForRegionReplicas.ADMIN.getConnection());
            // Now shut the whole cluster down, and verify regions are assigned even if there is only
            // one server running
            TestMasterOperationsForRegionReplicas.TEST_UTIL.shutdownMiniHBaseCluster();
            TestMasterOperationsForRegionReplicas.TEST_UTIL.startMiniHBaseCluster();
            TestMasterOperationsForRegionReplicas.TEST_UTIL.waitTableAvailable(tableName);
            validateSingleRegionServerAssignment(TestMasterOperationsForRegionReplicas.ADMIN.getConnection(), numRegions, numReplica);
            for (int i = 1; i < (TestMasterOperationsForRegionReplicas.numSlaves); i++) {
                // restore the cluster
                TestMasterOperationsForRegionReplicas.TEST_UTIL.getMiniHBaseCluster().startRegionServer();
            }
            // Check on alter table
            TestMasterOperationsForRegionReplicas.ADMIN.disableTable(tableName);
            assert TestMasterOperationsForRegionReplicas.ADMIN.isTableDisabled(tableName);
            // increase the replica
            desc.setRegionReplication((numReplica + 1));
            TestMasterOperationsForRegionReplicas.ADMIN.modifyTable(tableName, desc);
            TestMasterOperationsForRegionReplicas.ADMIN.enableTable(tableName);
            TestMasterOperationsForRegionReplicas.LOG.info(TestMasterOperationsForRegionReplicas.ADMIN.getTableDescriptor(tableName).toString());
            assert TestMasterOperationsForRegionReplicas.ADMIN.isTableEnabled(tableName);
            List<RegionInfo> regions = TestMasterOperationsForRegionReplicas.TEST_UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionsOfTable(tableName);
            Assert.assertTrue(((((("regions.size=" + (regions.size())) + ", numRegions=") + numRegions) + ", numReplica=") + numReplica), ((regions.size()) == (numRegions * (numReplica + 1))));
            // decrease the replica(earlier, table was modified to have a replica count of numReplica + 1)
            TestMasterOperationsForRegionReplicas.ADMIN.disableTable(tableName);
            desc.setRegionReplication(numReplica);
            TestMasterOperationsForRegionReplicas.ADMIN.modifyTable(tableName, desc);
            TestMasterOperationsForRegionReplicas.ADMIN.enableTable(tableName);
            assert TestMasterOperationsForRegionReplicas.ADMIN.isTableEnabled(tableName);
            regions = TestMasterOperationsForRegionReplicas.TEST_UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionsOfTable(tableName);
            assert (regions.size()) == (numRegions * numReplica);
            // also make sure the meta table has the replica locations removed
            hris = MetaTableAccessor.getTableRegions(TestMasterOperationsForRegionReplicas.ADMIN.getConnection(), tableName);
            assert (hris.size()) == (numRegions * numReplica);
            // just check that the number of default replica regions in the meta table are the same
            // as the number of regions the table was created with, and the count of the
            // replicas is numReplica for each region
            Map<RegionInfo, Integer> defaultReplicas = new HashMap<>();
            for (RegionInfo hri : hris) {
                Integer i;
                RegionInfo regionReplica0 = RegionReplicaUtil.getRegionInfoForDefaultReplica(hri);
                defaultReplicas.put(regionReplica0, ((i = defaultReplicas.get(regionReplica0)) == null ? 1 : i + 1));
            }
            assert (defaultReplicas.size()) == numRegions;
            Collection<Integer> counts = new java.util.HashSet(defaultReplicas.values());
            assert ((counts.size()) == 1) && (counts.contains(numReplica));
        } finally {
            TestMasterOperationsForRegionReplicas.ADMIN.disableTable(tableName);
            TestMasterOperationsForRegionReplicas.ADMIN.deleteTable(tableName);
        }
    }
}

