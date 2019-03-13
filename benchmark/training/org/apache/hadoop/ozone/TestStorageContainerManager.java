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
package org.apache.hadoop.ozone;


import ExitUtil.ExitException;
import HddsConfigKeys.OZONE_METADATA_DIRS;
import HddsVersionInfo.HDDS_VERSION_INFO;
import NodeType.SCM;
import OzoneConfigKeys.OZONE_ADMINISTRATORS;
import OzoneConfigKeys.OZONE_BLOCK_DELETING_SERVICE_INTERVAL;
import OzoneConfigKeys.OZONE_ENABLED;
import ScmConfigKeys.OZONE_SCM_BLOCK_DELETION_MAX_RETRY;
import ScmConfigKeys.OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL;
import ScmConfigKeys.OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT;
import StartupOption.INIT;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hdds.HddsConfigKeys;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.DeletedBlocksTransaction;
import org.apache.hadoop.hdds.scm.ScmInfo;
import org.apache.hadoop.hdds.scm.XceiverClientManager;
import org.apache.hadoop.hdds.scm.block.DeletedBlockLog;
import org.apache.hadoop.hdds.scm.block.SCMBlockDeletingService;
import org.apache.hadoop.hdds.scm.exceptions.SCMException;
import org.apache.hadoop.hdds.scm.node.NodeManager;
import org.apache.hadoop.hdds.scm.server.SCMStorageConfig;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.protocol.commands.DeleteBlocksCommand;
import org.apache.hadoop.ozone.protocol.commands.SCMCommand;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ExitUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class that exercises the StorageContainerManager.
 */
public class TestStorageContainerManager {
    private static XceiverClientManager xceiverClientManager = new XceiverClientManager(new OzoneConfiguration());

    private static final Logger LOG = LoggerFactory.getLogger(TestStorageContainerManager.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testRpcPermission() throws Exception {
        // Test with default configuration
        OzoneConfiguration defaultConf = new OzoneConfiguration();
        testRpcPermissionWithConf(defaultConf, "unknownUser", true);
        // Test with ozone.administrators defined in configuration
        OzoneConfiguration ozoneConf = new OzoneConfiguration();
        ozoneConf.setStrings(OZONE_ADMINISTRATORS, "adminUser1, adminUser2");
        // Non-admin user will get permission denied.
        testRpcPermissionWithConf(ozoneConf, "unknownUser", true);
        // Admin user will pass the permission check.
        testRpcPermissionWithConf(ozoneConf, "adminUser2", false);
    }

    @Test
    public void testBlockDeletionTransactions() throws Exception {
        int numKeys = 5;
        OzoneConfiguration conf = new OzoneConfiguration();
        conf.setTimeDuration(HddsConfigKeys.HDDS_CONTAINER_REPORT_INTERVAL, 100, TimeUnit.MILLISECONDS);
        conf.setTimeDuration(HddsConfigKeys.HDDS_COMMAND_STATUS_REPORT_INTERVAL, 100, TimeUnit.MILLISECONDS);
        conf.setTimeDuration(OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL, 3000, TimeUnit.MILLISECONDS);
        conf.setInt(OZONE_SCM_BLOCK_DELETION_MAX_RETRY, 5);
        conf.setTimeDuration(OZONE_BLOCK_DELETING_SERVICE_INTERVAL, 1, TimeUnit.SECONDS);
        // Reset container provision size, otherwise only one container
        // is created by default.
        conf.setInt(OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT, numKeys);
        MiniOzoneCluster cluster = MiniOzoneCluster.newBuilder(conf).setHbInterval(100).build();
        cluster.waitForClusterToBeReady();
        try {
            DeletedBlockLog delLog = cluster.getStorageContainerManager().getScmBlockManager().getDeletedBlockLog();
            Assert.assertEquals(0, delLog.getNumOfValidTransactions());
            // Create {numKeys} random names keys.
            TestStorageContainerManagerHelper helper = new TestStorageContainerManagerHelper(cluster, conf);
            Map<String, OmKeyInfo> keyLocations = helper.createKeys(numKeys, 4096);
            // Wait for container report
            Thread.sleep(1000);
            for (OmKeyInfo keyInfo : keyLocations.values()) {
                OzoneTestUtils.closeContainers(keyInfo.getKeyLocationVersions(), cluster.getStorageContainerManager());
            }
            Map<Long, List<Long>> containerBlocks = createDeleteTXLog(delLog, keyLocations, helper);
            Set<Long> containerIDs = containerBlocks.keySet();
            // Verify a few TX gets created in the TX log.
            Assert.assertTrue(((delLog.getNumOfValidTransactions()) > 0));
            // Once TXs are written into the log, SCM starts to fetch TX
            // entries from the log and schedule block deletions in HB interval,
            // after sometime, all the TX should be proceed and by then
            // the number of containerBlocks of all known containers will be
            // empty again.
            GenericTestUtils.waitFor(() -> {
                try {
                    return (delLog.getNumOfValidTransactions()) == 0;
                } catch ( e) {
                    return false;
                }
            }, 1000, 10000);
            Assert.assertTrue(helper.getAllBlocks(containerIDs).isEmpty());
            // Continue the work, add some TXs that with known container names,
            // but unknown block IDs.
            for (Long containerID : containerBlocks.keySet()) {
                // Add 2 TXs per container.
                delLog.addTransaction(containerID, Collections.singletonList(RandomUtils.nextLong()));
                delLog.addTransaction(containerID, Collections.singletonList(RandomUtils.nextLong()));
            }
            // Verify a few TX gets created in the TX log.
            Assert.assertTrue(((delLog.getNumOfValidTransactions()) > 0));
            // These blocks cannot be found in the container, skip deleting them
            // eventually these TX will success.
            GenericTestUtils.waitFor(() -> {
                try {
                    return (delLog.getFailedTransactions().size()) == 0;
                } catch ( e) {
                    return false;
                }
            }, 1000, 10000);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testBlockDeletingThrottling() throws Exception {
        int numKeys = 15;
        OzoneConfiguration conf = new OzoneConfiguration();
        conf.setTimeDuration(HddsConfigKeys.HDDS_CONTAINER_REPORT_INTERVAL, 1, TimeUnit.SECONDS);
        conf.setInt(OZONE_SCM_BLOCK_DELETION_MAX_RETRY, 5);
        conf.setTimeDuration(OZONE_BLOCK_DELETING_SERVICE_INTERVAL, 100, TimeUnit.MILLISECONDS);
        conf.setInt(OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT, numKeys);
        MiniOzoneCluster cluster = MiniOzoneCluster.newBuilder(conf).setHbInterval(1000).setHbProcessorInterval(3000).build();
        cluster.waitForClusterToBeReady();
        DeletedBlockLog delLog = cluster.getStorageContainerManager().getScmBlockManager().getDeletedBlockLog();
        Assert.assertEquals(0, delLog.getNumOfValidTransactions());
        int limitSize = 1;
        // Reset limit value to 1, so that we only allow one TX is dealt per
        // datanode.
        SCMBlockDeletingService delService = cluster.getStorageContainerManager().getScmBlockManager().getSCMBlockDeletingService();
        delService.setBlockDeleteTXNum(limitSize);
        // Create {numKeys} random names keys.
        TestStorageContainerManagerHelper helper = new TestStorageContainerManagerHelper(cluster, conf);
        Map<String, OmKeyInfo> keyLocations = helper.createKeys(numKeys, 4096);
        // Wait for container report
        Thread.sleep(5000);
        for (OmKeyInfo keyInfo : keyLocations.values()) {
            OzoneTestUtils.closeContainers(keyInfo.getKeyLocationVersions(), cluster.getStorageContainerManager());
        }
        createDeleteTXLog(delLog, keyLocations, helper);
        // Verify a few TX gets created in the TX log.
        Assert.assertTrue(((delLog.getNumOfValidTransactions()) > 0));
        // Verify the size in delete commands is expected.
        GenericTestUtils.waitFor(() -> {
            NodeManager nodeManager = cluster.getStorageContainerManager().getScmNodeManager();
            List<SCMCommand> commands = nodeManager.processHeartbeat(nodeManager.getNodes(NodeState.HEALTHY).get(0));
            if (commands != null) {
                for (SCMCommand cmd : commands) {
                    if ((cmd.getType()) == SCMCommandProto.Type.deleteBlocksCommand) {
                        List<DeletedBlocksTransaction> deletedTXs = ((DeleteBlocksCommand) (cmd)).blocksTobeDeleted();
                        return (deletedTXs != null) && ((deletedTXs.size()) == limitSize);
                    }
                }
            }
            return false;
        }, 500, 10000);
    }

    @Test
    public void testSCMInitialization() throws Exception {
        OzoneConfiguration conf = new OzoneConfiguration();
        final String path = GenericTestUtils.getTempPath(UUID.randomUUID().toString());
        Path scmPath = Paths.get(path, "scm-meta");
        conf.set(OZONE_METADATA_DIRS, scmPath.toString());
        INIT.setClusterId("testClusterId");
        // This will initialize SCM
        StorageContainerManager.scmInit(conf);
        SCMStorageConfig scmStore = new SCMStorageConfig(conf);
        Assert.assertEquals(SCM, scmStore.getNodeType());
        Assert.assertEquals("testClusterId", scmStore.getClusterID());
        INIT.setClusterId("testClusterIdNew");
        StorageContainerManager.scmInit(conf);
        Assert.assertEquals(SCM, scmStore.getNodeType());
        Assert.assertEquals("testClusterId", scmStore.getClusterID());
    }

    @Test
    public void testSCMReinitialization() throws Exception {
        OzoneConfiguration conf = new OzoneConfiguration();
        final String path = GenericTestUtils.getTempPath(UUID.randomUUID().toString());
        Path scmPath = Paths.get(path, "scm-meta");
        conf.set(OZONE_METADATA_DIRS, scmPath.toString());
        // This will set the cluster id in the version file
        MiniOzoneCluster cluster = MiniOzoneCluster.newBuilder(conf).setNumDatanodes(1).build();
        cluster.waitForClusterToBeReady();
        INIT.setClusterId("testClusterId");
        // This will initialize SCM
        StorageContainerManager.scmInit(conf);
        SCMStorageConfig scmStore = new SCMStorageConfig(conf);
        Assert.assertEquals(SCM, scmStore.getNodeType());
        Assert.assertNotEquals("testClusterId", scmStore.getClusterID());
        cluster.shutdown();
    }

    @Test
    public void testSCMInitializationFailure() throws IOException, AuthenticationException {
        OzoneConfiguration conf = new OzoneConfiguration();
        final String path = GenericTestUtils.getTempPath(UUID.randomUUID().toString());
        Path scmPath = Paths.get(path, "scm-meta");
        conf.set(OZONE_METADATA_DIRS, scmPath.toString());
        conf.setBoolean(OZONE_ENABLED, true);
        exception.expect(SCMException.class);
        exception.expectMessage("SCM not initialized due to storage config failure");
        StorageContainerManager.createSCM(null, conf);
    }

    @Test
    public void testSCMInitializationReturnCode() throws IOException, AuthenticationException {
        ExitUtil.disableSystemExit();
        OzoneConfiguration conf = new OzoneConfiguration();
        conf.setBoolean(OZONE_ENABLED, true);
        // Set invalid args
        String[] invalidArgs = new String[]{ "--zxcvbnm" };
        exception.expect(ExitException.class);
        exception.expectMessage("ExitException");
        StorageContainerManager.createSCM(invalidArgs, conf);
    }

    @Test
    public void testScmInfo() throws Exception {
        OzoneConfiguration conf = new OzoneConfiguration();
        final String path = GenericTestUtils.getTempPath(UUID.randomUUID().toString());
        Path scmPath = Paths.get(path, "scm-meta");
        conf.set(OZONE_METADATA_DIRS, scmPath.toString());
        conf.setBoolean(OZONE_ENABLED, true);
        SCMStorageConfig scmStore = new SCMStorageConfig(conf);
        String clusterId = UUID.randomUUID().toString();
        String scmId = UUID.randomUUID().toString();
        scmStore.setClusterId(clusterId);
        scmStore.setScmId(scmId);
        // writes the version file properties
        scmStore.initialize();
        StorageContainerManager scm = StorageContainerManager.createSCM(null, conf);
        // Reads the SCM Info from SCM instance
        ScmInfo scmInfo = scm.getClientProtocolServer().getScmInfo();
        Assert.assertEquals(clusterId, scmInfo.getClusterId());
        Assert.assertEquals(scmId, scmInfo.getScmId());
        String expectedVersion = HDDS_VERSION_INFO.getVersion();
        String actualVersion = scm.getSoftwareVersion();
        Assert.assertEquals(expectedVersion, actualVersion);
    }
}

