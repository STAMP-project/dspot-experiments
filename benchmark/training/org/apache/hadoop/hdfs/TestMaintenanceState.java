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
package org.apache.hadoop.hdfs;


import AdminStates.DECOMMISSIONED;
import AdminStates.DECOMMISSION_INPROGRESS;
import AdminStates.ENTERING_MAINTENANCE;
import AdminStates.IN_MAINTENANCE;
import AdminStates.NORMAL;
import CommonConfigurationKeys.FS_DEFAULT_NAME_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY;
import DFSConfigKeys.DFS_NAMENODE_REPLICATION_MIN_KEY;
import DFSConfigKeys.DFS_REPLICATION_DEFAULT;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import DatanodeReportType.LIVE;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.ToolRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static DFSConfigKeys.DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_DEFAULT;


/**
 * This class tests node maintenance.
 */
public class TestMaintenanceState extends AdminStatesBaseTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestMaintenanceState.class);

    private static final long EXPIRATION_IN_MS = 50;

    private int minMaintenanceR = DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_DEFAULT;

    public TestMaintenanceState() {
        setUseCombinedHostFileManager();
    }

    /**
     * Test valid value range for the config namenode.maintenance.replication.min.
     */
    @Test(timeout = 60000)
    public void testMaintenanceMinReplConfigRange() {
        TestMaintenanceState.LOG.info("Setting testMaintenanceMinReplConfigRange");
        // Case 1: Maintenance min replication less allowed minimum 0
        setMinMaintenanceR((-1));
        try {
            startCluster(1, 1);
            Assert.fail(("Cluster start should fail when 'dfs.namenode.maintenance" + ".replication.min=-1'"));
        } catch (IOException e) {
            TestMaintenanceState.LOG.info(("Expected exception: " + e));
        }
        // Case 2: Maintenance min replication greater
        // allowed max of DFSConfigKeys.DFS_REPLICATION_KEY
        int defaultRepl = getConf().getInt(DFS_REPLICATION_KEY, DFS_REPLICATION_DEFAULT);
        setMinMaintenanceR((defaultRepl + 1));
        try {
            startCluster(1, 1);
            Assert.fail(((("Cluster start should fail when 'dfs.namenode.maintenance" + ".replication.min > ") + defaultRepl) + "'"));
        } catch (IOException e) {
            TestMaintenanceState.LOG.info(("Expected exception: " + e));
        }
    }

    /**
     * Verify a node can transition from AdminStates.ENTERING_MAINTENANCE to
     * AdminStates.NORMAL.
     */
    @Test(timeout = 360000)
    public void testTakeNodeOutOfEnteringMaintenance() throws Exception {
        TestMaintenanceState.LOG.info("Starting testTakeNodeOutOfEnteringMaintenance");
        final int replicas = 1;
        final Path file = new Path("/testTakeNodeOutOfEnteringMaintenance.dat");
        startCluster(1, 1);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, Long.MAX_VALUE, null, ENTERING_MAINTENANCE);
        // When node is in ENTERING_MAINTENANCE state, it can still serve read
        // requests
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, null, nodeOutofService);
        putNodeInService(0, nodeOutofService.getDatanodeUuid());
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Verify a AdminStates.ENTERING_MAINTENANCE node can expire and transition
     * to AdminStates.NORMAL upon timeout.
     */
    @Test(timeout = 360000)
    public void testEnteringMaintenanceExpiration() throws Exception {
        TestMaintenanceState.LOG.info("Starting testEnteringMaintenanceExpiration");
        final int replicas = 1;
        final Path file = new Path("/testEnteringMaintenanceExpiration.dat");
        startCluster(1, 1);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, Long.MAX_VALUE, null, ENTERING_MAINTENANCE);
        // Adjust the expiration.
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), ((Time.now()) + (TestMaintenanceState.EXPIRATION_IN_MS)), null, NORMAL);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Verify node stays in AdminStates.NORMAL with invalid expiration.
     */
    @Test(timeout = 360000)
    public void testInvalidExpiration() throws Exception {
        TestMaintenanceState.LOG.info("Starting testInvalidExpiration");
        final int replicas = 1;
        final Path file = new Path("/testInvalidExpiration.dat");
        startCluster(1, 1);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        // expiration has to be greater than Time.now().
        takeNodeOutofService(0, null, Time.now(), null, NORMAL);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * When a dead node is put to maintenance, it transitions directly to
     * AdminStates.IN_MAINTENANCE.
     */
    @Test(timeout = 360000)
    public void testPutDeadNodeToMaintenance() throws Exception {
        TestMaintenanceState.LOG.info("Starting testPutDeadNodeToMaintenance");
        final int replicas = 1;
        final Path file = new Path("/testPutDeadNodeToMaintenance.dat");
        startCluster(1, 1);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        final MiniDFSCluster.DataNodeProperties dnProp = getCluster().stopDataNode(0);
        DFSTestUtil.waitForDatanodeState(getCluster(), dnProp.datanode.getDatanodeUuid(), false, 20000);
        int deadInMaintenance = ns.getNumInMaintenanceDeadDataNodes();
        int liveInMaintenance = ns.getNumInMaintenanceLiveDataNodes();
        takeNodeOutofService(0, dnProp.datanode.getDatanodeUuid(), Long.MAX_VALUE, null, IN_MAINTENANCE);
        Assert.assertEquals((deadInMaintenance + 1), ns.getNumInMaintenanceDeadDataNodes());
        Assert.assertEquals(liveInMaintenance, ns.getNumInMaintenanceLiveDataNodes());
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * When a dead node is put to maintenance, it transitions directly to
     * AdminStates.IN_MAINTENANCE. Then AdminStates.IN_MAINTENANCE expires and
     * transitions to AdminStates.NORMAL.
     */
    @Test(timeout = 360000)
    public void testPutDeadNodeToMaintenanceWithExpiration() throws Exception {
        TestMaintenanceState.LOG.info("Starting testPutDeadNodeToMaintenanceWithExpiration");
        final Path file = new Path("/testPutDeadNodeToMaintenanceWithExpiration.dat");
        startCluster(1, 1);
        FileSystem fileSys = getCluster().getFileSystem(0);
        FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, 1, 1);
        MiniDFSCluster.DataNodeProperties dnProp = getCluster().stopDataNode(0);
        DFSTestUtil.waitForDatanodeState(getCluster(), dnProp.datanode.getDatanodeUuid(), false, 20000);
        int deadInMaintenance = ns.getNumInMaintenanceDeadDataNodes();
        int liveInMaintenance = ns.getNumInMaintenanceLiveDataNodes();
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, dnProp.datanode.getDatanodeUuid(), Long.MAX_VALUE, null, IN_MAINTENANCE);
        // Adjust the expiration.
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), ((Time.now()) + (TestMaintenanceState.EXPIRATION_IN_MS)), null, NORMAL);
        // no change
        Assert.assertEquals(deadInMaintenance, ns.getNumInMaintenanceDeadDataNodes());
        Assert.assertEquals(liveInMaintenance, ns.getNumInMaintenanceLiveDataNodes());
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Transition from decommissioned state to maintenance state.
     */
    @Test(timeout = 360000)
    public void testTransitionFromDecommissioned() throws IOException {
        TestMaintenanceState.LOG.info("Starting testTransitionFromDecommissioned");
        final Path file = new Path("/testTransitionFromDecommissioned.dat");
        startCluster(1, 4);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, 3, 1);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, 0, null, DECOMMISSIONED);
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), Long.MAX_VALUE, null, IN_MAINTENANCE);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Transition from decommissioned state to maintenance state.
     * After the maintenance state expires, it is transitioned to NORMAL.
     */
    @Test(timeout = 360000)
    public void testTransitionFromDecommissionedAndExpired() throws IOException {
        TestMaintenanceState.LOG.info("Starting testTransitionFromDecommissionedAndExpired");
        final Path file = new Path("/testTransitionFromDecommissionedAndExpired.dat");
        startCluster(1, 4);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, 3, 1);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, 0, null, DECOMMISSIONED);
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), Long.MAX_VALUE, null, IN_MAINTENANCE);
        // Adjust the expiration.
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), ((Time.now()) + (TestMaintenanceState.EXPIRATION_IN_MS)), null, NORMAL);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * When a node is put to maintenance, it first transitions to
     * AdminStates.ENTERING_MAINTENANCE. It makes sure all blocks have minimal
     * replication before it can be transitioned to AdminStates.IN_MAINTENANCE.
     * If node becomes dead when it is in AdminStates.ENTERING_MAINTENANCE, it
     * should stay in AdminStates.ENTERING_MAINTENANCE state.
     */
    @Test(timeout = 360000)
    public void testNodeDeadWhenInEnteringMaintenance() throws Exception {
        TestMaintenanceState.LOG.info("Starting testNodeDeadWhenInEnteringMaintenance");
        final int numNamenodes = 1;
        final int numDatanodes = 1;
        final int replicas = 1;
        final Path file = new Path("/testNodeDeadWhenInEnteringMaintenance.dat");
        startCluster(numNamenodes, numDatanodes);
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, TestMaintenanceState.getFirstBlockFirstReplicaUuid(fileSys, file), Long.MAX_VALUE, null, ENTERING_MAINTENANCE);
        Assert.assertEquals(1, ns.getNumEnteringMaintenanceDataNodes());
        MiniDFSCluster.DataNodeProperties dnProp = getCluster().stopDataNode(nodeOutofService.getXferAddr());
        DFSTestUtil.waitForDatanodeState(getCluster(), nodeOutofService.getDatanodeUuid(), false, 20000);
        DFSClient client = getDfsClient(0);
        Assert.assertEquals("maintenance node shouldn't be live", (numDatanodes - 1), client.datanodeReport(LIVE).length);
        Assert.assertEquals(1, ns.getNumEnteringMaintenanceDataNodes());
        getCluster().restartDataNode(dnProp, true);
        getCluster().waitActive();
        waitNodeState(nodeOutofService, ENTERING_MAINTENANCE);
        Assert.assertEquals(1, ns.getNumEnteringMaintenanceDataNodes());
        Assert.assertEquals("maintenance node should be live", numDatanodes, client.datanodeReport(LIVE).length);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * When a node is put to maintenance, it first transitions to
     * AdminStates.ENTERING_MAINTENANCE. It makes sure all blocks have
     * been properly replicated before it can be transitioned to
     * AdminStates.IN_MAINTENANCE. The expected replication count takes
     * DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY and
     * its file's replication factor into account.
     */
    @Test(timeout = 360000)
    public void testExpectedReplications() throws IOException {
        TestMaintenanceState.LOG.info("Starting testExpectedReplications");
        testExpectedReplication(1);
        testExpectedReplication(2);
        testExpectedReplication(3);
        testExpectedReplication(4);
    }

    /**
     * Verify a node can transition directly to AdminStates.IN_MAINTENANCE when
     * DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY is set to zero.
     */
    @Test(timeout = 360000)
    public void testZeroMinMaintenanceReplication() throws Exception {
        TestMaintenanceState.LOG.info("Starting testZeroMinMaintenanceReplication");
        setMinMaintenanceR(0);
        startCluster(1, 1);
        final Path file = new Path("/testZeroMinMaintenanceReplication.dat");
        final int replicas = 1;
        FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        takeNodeOutofService(0, null, Long.MAX_VALUE, null, IN_MAINTENANCE);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Verify a node can transition directly to AdminStates.IN_MAINTENANCE when
     * DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY is set to zero. Then later
     * transition to NORMAL after maintenance expiration.
     */
    @Test(timeout = 360000)
    public void testZeroMinMaintenanceReplicationWithExpiration() throws Exception {
        TestMaintenanceState.LOG.info("Starting testZeroMinMaintenanceReplicationWithExpiration");
        setMinMaintenanceR(0);
        startCluster(1, 1);
        final Path file = new Path("/testZeroMinMaintenanceReplicationWithExpiration.dat");
        FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, 1, 1);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, Long.MAX_VALUE, null, IN_MAINTENANCE);
        // Adjust the expiration.
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), ((Time.now()) + (TestMaintenanceState.EXPIRATION_IN_MS)), null, NORMAL);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Test file block replication lesser than maintenance minimum.
     */
    @Test(timeout = 360000)
    public void testFileBlockReplicationAffectingMaintenance() throws Exception {
        int defaultReplication = getConf().getInt(DFS_REPLICATION_KEY, DFS_REPLICATION_DEFAULT);
        int defaultMaintenanceMinRepl = getConf().getInt(DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY, DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_DEFAULT);
        // Case 1:
        // * Maintenance min larger than default min replication
        // * File block replication larger than maintenance min
        // * Initial data nodes not sufficient to remove all maintenance nodes
        // as file block replication is greater than maintenance min.
        // * Data nodes added later for the state transition to progress
        int maintenanceMinRepl = defaultMaintenanceMinRepl + 1;
        int fileBlockReplication = maintenanceMinRepl + 1;
        int numAddedDataNodes = 1;
        int numInitialDataNodes = (maintenanceMinRepl * 2) - numAddedDataNodes;
        Assert.assertTrue((maintenanceMinRepl <= defaultReplication));
        testFileBlockReplicationImpl(maintenanceMinRepl, numInitialDataNodes, numAddedDataNodes, fileBlockReplication);
        // Case 2:
        // * Maintenance min larger than default min replication
        // * File block replication lesser than maintenance min
        // * Initial data nodes after removal of maintenance nodes is still
        // sufficient for the file block replication.
        // * No new data nodes to be added, still the state transition happens
        maintenanceMinRepl = defaultMaintenanceMinRepl + 1;
        fileBlockReplication = maintenanceMinRepl - 1;
        numAddedDataNodes = 0;
        numInitialDataNodes = (maintenanceMinRepl * 2) - numAddedDataNodes;
        testFileBlockReplicationImpl(maintenanceMinRepl, numInitialDataNodes, numAddedDataNodes, fileBlockReplication);
    }

    /**
     * Transition from IN_MAINTENANCE to DECOMMISSIONED.
     */
    @Test(timeout = 360000)
    public void testTransitionToDecommission() throws IOException {
        TestMaintenanceState.LOG.info("Starting testTransitionToDecommission");
        final int numNamenodes = 1;
        final int numDatanodes = 4;
        startCluster(numNamenodes, numDatanodes);
        final Path file = new Path("testTransitionToDecommission.dat");
        final int replicas = 3;
        FileSystem fileSys = getCluster().getFileSystem(0);
        FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 25);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, TestMaintenanceState.getFirstBlockFirstReplicaUuid(fileSys, file), Long.MAX_VALUE, null, IN_MAINTENANCE);
        DFSClient client = getDfsClient(0);
        Assert.assertEquals("All datanodes must be alive", numDatanodes, client.datanodeReport(LIVE).length);
        // test 1, verify the replica in IN_MAINTENANCE state isn't in LocatedBlock
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), 0, null, DECOMMISSIONED);
        // test 2 after decommission has completed, the replication count is
        // replicas + 1 which includes the decommissioned node.
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas + 1), null);
        // test 3, put the node in service, replication count should restore.
        putNodeInService(0, nodeOutofService.getDatanodeUuid());
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, null);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Transition from decommissioning state to maintenance state.
     */
    @Test(timeout = 360000)
    public void testTransitionFromDecommissioning() throws IOException {
        TestMaintenanceState.LOG.info("Starting testTransitionFromDecommissioning");
        startCluster(1, 3);
        final Path file = new Path("/testTransitionFromDecommissioning.dat");
        final int replicas = 3;
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, 0, null, DECOMMISSION_INPROGRESS);
        takeNodeOutofService(0, nodeOutofService.getDatanodeUuid(), Long.MAX_VALUE, null, IN_MAINTENANCE);
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * First put a node in maintenance, then put a different node
     * in decommission. Make sure decommission process take
     * maintenance replica into account.
     */
    @Test(timeout = 360000)
    public void testDecommissionDifferentNodeAfterMaintenances() throws Exception {
        testDecommissionDifferentNodeAfterMaintenance(2);
        testDecommissionDifferentNodeAfterMaintenance(3);
        testDecommissionDifferentNodeAfterMaintenance(4);
    }

    /**
     * Verify if multiple DataNodes can transition to maintenance state
     * at the same time.
     */
    @Test(timeout = 360000)
    public void testMultipleNodesMaintenance() throws Exception {
        startCluster(1, 5);
        final Path file = new Path("/testMultipleNodesMaintenance.dat");
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        int repl = 3;
        AdminStatesBaseTest.writeFile(fileSys, file, repl, 1);
        DFSTestUtil.waitForReplication(((DistributedFileSystem) (fileSys)), file, ((short) (repl)), 10000);
        final DatanodeInfo[] nodes = TestMaintenanceState.getFirstBlockReplicasDatanodeInfos(fileSys, file);
        // Request maintenance for DataNodes 1 and 2 which has the file blocks.
        List<DatanodeInfo> maintenanceDN = takeNodeOutofService(0, Lists.newArrayList(nodes[0].getDatanodeUuid(), nodes[1].getDatanodeUuid()), Long.MAX_VALUE, null, null, IN_MAINTENANCE);
        // Verify file replication matches maintenance state min replication
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, 1, null, nodes[0]);
        // Put the maintenance nodes back in service
        for (DatanodeInfo datanodeInfo : maintenanceDN) {
            putNodeInService(0, datanodeInfo);
        }
        // Verify file replication catching up to the old state
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, repl, null);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    @Test(timeout = 360000)
    public void testChangeReplicationFactors() throws IOException {
        // Prior to any change, there is 1 maintenance node and 2 live nodes.
        // Replication factor is adjusted from 3 to 4.
        // After the change, given 1 maintenance + 2 live is less than the
        // newFactor, one live nodes will be added.
        testChangeReplicationFactor(3, 4, 3);
        // Replication factor is adjusted from 3 to 2.
        // After the change, given 2 live nodes is the same as the newFactor,
        // no live nodes will be invalidated.
        testChangeReplicationFactor(3, 2, 2);
        // Replication factor is adjusted from 3 to 1.
        // After the change, given 2 live nodes is greater than the newFactor,
        // one live nodes will be invalidated.
        testChangeReplicationFactor(3, 1, 1);
    }

    /**
     * Verify the following scenario.
     * a. Put a live node to maintenance => 1 maintenance, 2 live.
     * b. The maintenance node becomes dead => block map still has 1 maintenance,
     *    2 live.
     * c. Take the node out of maintenance => NN should schedule the replication
     *    and end up with 3 live.
     */
    @Test(timeout = 360000)
    public void testTakeDeadNodeOutOfMaintenance() throws Exception {
        TestMaintenanceState.LOG.info("Starting testTakeDeadNodeOutOfMaintenance");
        final int numNamenodes = 1;
        final int numDatanodes = 4;
        startCluster(numNamenodes, numDatanodes);
        final Path file = new Path("/testTakeDeadNodeOutOfMaintenance.dat");
        final int replicas = 3;
        final FileSystem fileSys = getCluster().getFileSystem(0);
        final FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, TestMaintenanceState.getFirstBlockFirstReplicaUuid(fileSys, file), Long.MAX_VALUE, null, IN_MAINTENANCE);
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        final DFSClient client = getDfsClient(0);
        Assert.assertEquals("All datanodes must be alive", numDatanodes, client.datanodeReport(LIVE).length);
        getCluster().stopDataNode(nodeOutofService.getXferAddr());
        DFSTestUtil.waitForDatanodeState(getCluster(), nodeOutofService.getDatanodeUuid(), false, 20000);
        Assert.assertEquals("maintenance node shouldn't be alive", (numDatanodes - 1), client.datanodeReport(LIVE).length);
        // Dead maintenance node's blocks should remain in block map.
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        // When dead maintenance mode is transitioned to out of maintenance mode,
        // its blocks should be removed from block map.
        // This will then trigger replication to restore the live replicas back
        // to replication factor.
        putNodeInService(0, nodeOutofService.getDatanodeUuid());
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, nodeOutofService, null);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Verify the following scenario.
     * a. Put a live node to maintenance => 1 maintenance, 2 live.
     * b. The maintenance node becomes dead => block map still has 1 maintenance,
     *    2 live.
     * c. Restart nn => block map only has 2 live => restore the 3 live.
     * d. Restart the maintenance dn => 1 maintenance, 3 live.
     * e. Take the node out of maintenance => over replication => 3 live.
     */
    @Test(timeout = 360000)
    public void testWithNNAndDNRestart() throws Exception {
        TestMaintenanceState.LOG.info("Starting testWithNNAndDNRestart");
        final int numNamenodes = 1;
        final int numDatanodes = 4;
        startCluster(numNamenodes, numDatanodes);
        final Path file = new Path("/testWithNNAndDNRestart.dat");
        final int replicas = 3;
        final FileSystem fileSys = getCluster().getFileSystem(0);
        FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 1);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, TestMaintenanceState.getFirstBlockFirstReplicaUuid(fileSys, file), Long.MAX_VALUE, null, IN_MAINTENANCE);
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        DFSClient client = getDfsClient(0);
        Assert.assertEquals("All datanodes must be alive", numDatanodes, client.datanodeReport(LIVE).length);
        MiniDFSCluster.DataNodeProperties dnProp = getCluster().stopDataNode(nodeOutofService.getXferAddr());
        DFSTestUtil.waitForDatanodeState(getCluster(), nodeOutofService.getDatanodeUuid(), false, 20000);
        Assert.assertEquals("maintenance node shouldn't be alive", (numDatanodes - 1), client.datanodeReport(LIVE).length);
        // Dead maintenance node's blocks should remain in block map.
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService);
        // restart nn, nn will restore 3 live replicas given it doesn't
        // know the maintenance node has the replica.
        getCluster().restartNameNode(0);
        ns = getCluster().getNamesystem(0);
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, null);
        // restart dn, nn has 1 maintenance replica and 3 live replicas.
        getCluster().restartDataNode(dnProp, true);
        getCluster().waitActive();
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, nodeOutofService);
        // Put the node in service, a redundant replica should be removed.
        putNodeInService(0, nodeOutofService.getDatanodeUuid());
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, null);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Machine under maintenance state won't be chosen for new block allocation.
     */
    @Test(timeout = 3600000)
    public void testWriteAfterMaintenance() throws IOException {
        TestMaintenanceState.LOG.info("Starting testWriteAfterMaintenance");
        startCluster(1, 3);
        final Path file = new Path("/testWriteAfterMaintenance.dat");
        int replicas = 3;
        final FileSystem fileSys = getCluster().getFileSystem(0);
        FSNamesystem ns = getCluster().getNamesystem(0);
        final DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, Long.MAX_VALUE, null, IN_MAINTENANCE);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas, 2);
        // Verify nodeOutofService wasn't chosen for write operation.
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, (replicas - 1), nodeOutofService, null);
        // Put the node back to service, live replicas should be restored.
        putNodeInService(0, nodeOutofService.getDatanodeUuid());
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, replicas, null);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * A node has blocks under construction when it is put to maintenance.
     * Given there are minReplication replicas somewhere else,
     * it can be transitioned to AdminStates.IN_MAINTENANCE.
     */
    @Test(timeout = 360000)
    public void testEnterMaintenanceWhenFileOpen() throws Exception {
        TestMaintenanceState.LOG.info("Starting testEnterMaintenanceWhenFileOpen");
        startCluster(1, 3);
        final Path file = new Path("/testEnterMaintenanceWhenFileOpen.dat");
        final FileSystem fileSys = getCluster().getFileSystem(0);
        AdminStatesBaseTest.writeIncompleteFile(fileSys, file, ((short) (3)), ((short) (2)));
        takeNodeOutofService(0, null, Long.MAX_VALUE, null, IN_MAINTENANCE);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    /**
     * Machine under maintenance state won't be chosen for invalidation.
     */
    @Test(timeout = 360000)
    public void testInvalidation() throws IOException {
        TestMaintenanceState.LOG.info("Starting testInvalidation");
        int numNamenodes = 1;
        int numDatanodes = 3;
        startCluster(numNamenodes, numDatanodes);
        Path file = new Path("/testInvalidation.dat");
        int replicas = 3;
        FileSystem fileSys = getCluster().getFileSystem(0);
        FSNamesystem ns = getCluster().getNamesystem(0);
        AdminStatesBaseTest.writeFile(fileSys, file, replicas);
        DatanodeInfo nodeOutofService = takeNodeOutofService(0, null, Long.MAX_VALUE, null, IN_MAINTENANCE);
        DFSClient client = getDfsClient(0);
        client.setReplication(file.toString(), ((short) (1)));
        // Verify the nodeOutofService remains in blocksMap.
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, 1, nodeOutofService);
        // Restart NN and verify the nodeOutofService remains in blocksMap.
        getCluster().restartNameNode(0);
        ns = getCluster().getNamesystem(0);
        TestMaintenanceState.checkWithRetry(ns, fileSys, file, 1, nodeOutofService);
        AdminStatesBaseTest.cleanupFile(fileSys, file);
    }

    @Test(timeout = 120000)
    public void testFileCloseAfterEnteringMaintenance() throws Exception {
        TestMaintenanceState.LOG.info("Starting testFileCloseAfterEnteringMaintenance");
        int expirationInMs = 30 * 1000;
        int numDataNodes = 3;
        int numNameNodes = 1;
        getConf().setInt(DFS_NAMENODE_REPLICATION_MIN_KEY, 2);
        startCluster(numNameNodes, numDataNodes);
        getCluster().waitActive();
        FSNamesystem fsn = getCluster().getNameNode().getNamesystem();
        List<String> hosts = new ArrayList<>();
        for (DataNode dn : getCluster().getDataNodes()) {
            hosts.add(dn.getDisplayName());
            putNodeInService(0, dn.getDatanodeUuid());
        }
        Assert.assertEquals(numDataNodes, fsn.getNumLiveDataNodes());
        Path openFile = new Path("/testClosingFileInMaintenance.dat");
        // Lets write 2 blocks of data to the openFile
        AdminStatesBaseTest.writeFile(getCluster().getFileSystem(), openFile, ((short) (3)));
        // Lets write some more data and keep the file open
        FSDataOutputStream fsDataOutputStream = getCluster().getFileSystem().append(openFile);
        byte[] bytes = new byte[1024];
        fsDataOutputStream.write(bytes);
        fsDataOutputStream.hsync();
        LocatedBlocks lbs = NameNodeAdapter.getBlockLocations(getCluster().getNameNode(0), openFile.toString(), 0, (3 * (AdminStatesBaseTest.blockSize)));
        DatanodeInfo[] dnInfos4LastBlock = lbs.getLastLocatedBlock().getLocations();
        // Request maintenance for DataNodes 1 and 2 which has the last block.
        takeNodeOutofService(0, Lists.newArrayList(dnInfos4LastBlock[0].getDatanodeUuid(), dnInfos4LastBlock[1].getDatanodeUuid()), ((Time.now()) + expirationInMs), null, null, ENTERING_MAINTENANCE);
        // Closing the file should succeed even when the
        // last blocks' nodes are entering maintenance.
        fsDataOutputStream.close();
        AdminStatesBaseTest.cleanupFile(getCluster().getFileSystem(), openFile);
    }

    @Test(timeout = 120000)
    public void testReportMaintenanceNodes() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
        TestMaintenanceState.LOG.info("Starting testReportMaintenanceNodes");
        int expirationInMs = 30 * 1000;
        int numNodes = 2;
        setMinMaintenanceR(numNodes);
        startCluster(1, numNodes);
        getCluster().waitActive();
        FileSystem fileSys = getCluster().getFileSystem(0);
        getConf().set(FS_DEFAULT_NAME_KEY, fileSys.getUri().toString());
        DFSAdmin dfsAdmin = new DFSAdmin(getConf());
        FSNamesystem fsn = getCluster().getNameNode().getNamesystem();
        Assert.assertEquals(numNodes, fsn.getNumLiveDataNodes());
        int ret = ToolRunner.run(dfsAdmin, new String[]{ "-report", "-enteringmaintenance", "-inmaintenance" });
        Assert.assertEquals(0, ret);
        Assert.assertThat(out.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Entering maintenance datanodes (0):"), CoreMatchers.containsString("In maintenance datanodes (0):"), CoreMatchers.not(CoreMatchers.containsString(getCluster().getDataNodes().get(0).getDisplayName())), CoreMatchers.not(CoreMatchers.containsString(getCluster().getDataNodes().get(1).getDisplayName())))));
        final Path file = new Path("/testReportMaintenanceNodes.dat");
        AdminStatesBaseTest.writeFile(fileSys, file, numNodes, 1);
        DatanodeInfo[] nodes = TestMaintenanceState.getFirstBlockReplicasDatanodeInfos(fileSys, file);
        // Request maintenance for DataNodes1. The DataNode1 will not transition
        // to the next state AdminStates.IN_MAINTENANCE immediately since there
        // are not enough candidate nodes to satisfy the min maintenance
        // replication.
        DatanodeInfo maintenanceDN = takeNodeOutofService(0, nodes[0].getDatanodeUuid(), ((Time.now()) + expirationInMs), null, null, ENTERING_MAINTENANCE);
        Assert.assertEquals(1, fsn.getNumEnteringMaintenanceDataNodes());
        // reset stream
        out.reset();
        err.reset();
        ret = ToolRunner.run(dfsAdmin, new String[]{ "-report", "-enteringmaintenance" });
        Assert.assertEquals(0, ret);
        Assert.assertThat(out.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Entering maintenance datanodes (1):"), CoreMatchers.containsString(nodes[0].getXferAddr()), CoreMatchers.not(CoreMatchers.containsString(nodes[1].getXferAddr())))));
        // reset stream
        out.reset();
        err.reset();
        // start a new datanode to make state transition to
        // AdminStates.IN_MAINTENANCE
        getCluster().startDataNodes(getConf(), 1, true, null, null);
        getCluster().waitActive();
        waitNodeState(maintenanceDN, IN_MAINTENANCE);
        Assert.assertEquals(1, fsn.getNumInMaintenanceLiveDataNodes());
        ret = ToolRunner.run(dfsAdmin, new String[]{ "-report", "-inmaintenance" });
        Assert.assertEquals(0, ret);
        Assert.assertThat(out.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("In maintenance datanodes (1):"), CoreMatchers.containsString(nodes[0].getXferAddr()), CoreMatchers.not(CoreMatchers.containsString(nodes[1].getXferAddr())), CoreMatchers.not(CoreMatchers.containsString(getCluster().getDataNodes().get(2).getDisplayName())))));
        AdminStatesBaseTest.cleanupFile(getCluster().getFileSystem(), file);
    }
}

