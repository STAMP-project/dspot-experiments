/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY;
import StoragePolicySatisfierMode.EXTERNAL;
import StoragePolicySatisfierMode.NONE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.ReconfigurationException;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNameNodeReconfigure {
    public static final Logger LOG = LoggerFactory.getLogger(TestNameNodeReconfigure.class);

    private MiniDFSCluster cluster;

    private final int customizedBlockInvalidateLimit = 500;

    @Test
    public void testReconfigureCallerContextEnabled() throws ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        final FSNamesystem nameSystem = nameNode.getNamesystem();
        // try invalid values
        nameNode.reconfigureProperty(CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY, "text");
        verifyReconfigureCallerContextEnabled(nameNode, nameSystem, false);
        // enable CallerContext
        nameNode.reconfigureProperty(CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY, "true");
        verifyReconfigureCallerContextEnabled(nameNode, nameSystem, true);
        // disable CallerContext
        nameNode.reconfigureProperty(CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY, "false");
        verifyReconfigureCallerContextEnabled(nameNode, nameSystem, false);
        // revert to default
        nameNode.reconfigureProperty(CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY, null);
        // verify default
        Assert.assertEquals(((CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY) + " has wrong value"), false, nameSystem.getCallerContextEnabled());
        Assert.assertEquals(((CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY) + " has wrong value"), null, nameNode.getConf().get(CommonConfigurationKeysPublic.HADOOP_CALLER_CONTEXT_ENABLED_KEY));
    }

    /**
     * Test to reconfigure enable/disable IPC backoff
     */
    @Test
    public void testReconfigureIPCBackoff() throws ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        NameNodeRpcServer nnrs = ((NameNodeRpcServer) (nameNode.getRpcServer()));
        String ipcClientRPCBackoffEnable = NameNode.buildBackoffEnableKey(nnrs.getClientRpcServer().getPort());
        // try invalid values
        verifyReconfigureIPCBackoff(nameNode, nnrs, ipcClientRPCBackoffEnable, false);
        // enable IPC_CLIENT_RPC_BACKOFF
        nameNode.reconfigureProperty(ipcClientRPCBackoffEnable, "true");
        verifyReconfigureIPCBackoff(nameNode, nnrs, ipcClientRPCBackoffEnable, true);
        // disable IPC_CLIENT_RPC_BACKOFF
        nameNode.reconfigureProperty(ipcClientRPCBackoffEnable, "false");
        verifyReconfigureIPCBackoff(nameNode, nnrs, ipcClientRPCBackoffEnable, false);
        // revert to default
        nameNode.reconfigureProperty(ipcClientRPCBackoffEnable, null);
        Assert.assertEquals((ipcClientRPCBackoffEnable + " has wrong value"), false, nnrs.getClientRpcServer().isClientBackoffEnabled());
        Assert.assertEquals((ipcClientRPCBackoffEnable + " has wrong value"), null, nameNode.getConf().get(ipcClientRPCBackoffEnable));
    }

    /**
     * Test to reconfigure interval of heart beat check and re-check.
     */
    @Test
    public void testReconfigureHearbeatCheck() throws ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        final DatanodeManager datanodeManager = nameNode.namesystem.getBlockManager().getDatanodeManager();
        // change properties
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, ("" + 6));
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, ("" + ((10 * 60) * 1000)));
        // try invalid values
        try {
            nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, "text");
            Assert.fail("ReconfigurationException expected");
        } catch (ReconfigurationException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof NumberFormatException));
        }
        try {
            nameNode.reconfigureProperty(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, "text");
            Assert.fail("ReconfigurationException expected");
        } catch (ReconfigurationException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof NumberFormatException));
        }
        // verify change
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), 6, nameNode.getConf().getLong(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT));
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), 6, datanodeManager.getHeartbeatInterval());
        Assert.assertEquals(((DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY) + " has wrong value"), ((10 * 60) * 1000), nameNode.getConf().getInt(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT));
        Assert.assertEquals(((DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY) + " has wrong value"), ((10 * 60) * 1000), datanodeManager.getHeartbeatRecheckInterval());
        // change to a value with time unit
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, "1m");
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), 60, nameNode.getConf().getLong(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT));
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), 60, datanodeManager.getHeartbeatInterval());
        // revert to defaults
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, null);
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, null);
        // verify defaults
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), null, nameNode.getConf().get(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY));
        Assert.assertEquals(((DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY) + " has wrong value"), DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT, datanodeManager.getHeartbeatInterval());
        Assert.assertEquals(((DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY) + " has wrong value"), null, nameNode.getConf().get(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY));
        Assert.assertEquals(((DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY) + " has wrong value"), DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT, datanodeManager.getHeartbeatRecheckInterval());
    }

    /**
     * Tests enable/disable Storage Policy Satisfier dynamically when
     * "dfs.storage.policy.enabled" feature is disabled.
     *
     * @throws ReconfigurationException
     * 		
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testReconfigureSPSWithStoragePolicyDisabled() throws IOException, ReconfigurationException {
        // shutdown cluster
        cluster.shutdown();
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_STORAGE_POLICY_ENABLED_KEY, false);
        cluster = new MiniDFSCluster.Builder(conf).build();
        cluster.waitActive();
        final NameNode nameNode = cluster.getNameNode();
        verifySPSEnabled(nameNode, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE, false);
        // enable SPS internally by keeping DFS_STORAGE_POLICY_ENABLED_KEY
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, EXTERNAL.toString());
        // Since DFS_STORAGE_POLICY_ENABLED_KEY is disabled, SPS can't be enabled.
        Assert.assertNull((("SPS shouldn't start as " + (DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY)) + " is disabled"), nameNode.getNamesystem().getBlockManager().getSPSManager());
        verifySPSEnabled(nameNode, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, EXTERNAL, false);
        Assert.assertEquals(((DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY) + " has wrong value"), EXTERNAL.toString(), nameNode.getConf().get(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_DEFAULT));
    }

    /**
     * Tests enable/disable Storage Policy Satisfier dynamically.
     */
    @Test(timeout = 30000)
    public void testReconfigureStoragePolicySatisfierEnabled() throws ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        verifySPSEnabled(nameNode, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE, false);
        // try invalid values
        try {
            nameNode.reconfigureProperty(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, "text");
            Assert.fail("ReconfigurationException expected");
        } catch (ReconfigurationException e) {
            GenericTestUtils.assertExceptionContains(("For enabling or disabling storage policy satisfier, must " + "pass either internal/external/none string value only"), e.getCause());
        }
        // disable SPS
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        verifySPSEnabled(nameNode, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE, false);
        // enable external SPS
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, EXTERNAL.toString());
        Assert.assertEquals(((DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY) + " has wrong value"), false, nameNode.getNamesystem().getBlockManager().getSPSManager().isSatisfierRunning());
        Assert.assertEquals(((DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY) + " has wrong value"), EXTERNAL.toString(), nameNode.getConf().get(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_DEFAULT));
    }

    /**
     * Test to satisfy storage policy after disabled storage policy satisfier.
     */
    @Test(timeout = 30000)
    public void testSatisfyStoragePolicyAfterSatisfierDisabled() throws IOException, ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        // disable SPS
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        verifySPSEnabled(nameNode, DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE, false);
        Path filePath = new Path("/testSPS");
        DistributedFileSystem fileSystem = cluster.getFileSystem();
        fileSystem.create(filePath);
        fileSystem.setStoragePolicy(filePath, "COLD");
        try {
            fileSystem.satisfyStoragePolicy(filePath);
            Assert.fail("Expected to fail, as storage policy feature has disabled.");
        } catch (RemoteException e) {
            GenericTestUtils.assertExceptionContains(("Cannot request to satisfy storage policy " + (("when storage policy satisfier feature has been disabled" + " by admin. Seek for an admin help to enable it ") + "or use Mover tool.")), e);
        }
    }

    @Test
    public void testBlockInvalidateLimitAfterReconfigured() throws ReconfigurationException {
        final NameNode nameNode = cluster.getNameNode();
        final DatanodeManager datanodeManager = nameNode.namesystem.getBlockManager().getDatanodeManager();
        Assert.assertEquals(((DFSConfigKeys.DFS_BLOCK_INVALIDATE_LIMIT_KEY) + " is not correctly set"), customizedBlockInvalidateLimit, datanodeManager.getBlockInvalidateLimit());
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, Integer.toString(6));
        // 20 * 6 = 120 < 500
        // Invalid block limit should stay same as before after reconfiguration.
        Assert.assertEquals(((DFSConfigKeys.DFS_BLOCK_INVALIDATE_LIMIT_KEY) + " is not honored after reconfiguration"), customizedBlockInvalidateLimit, datanodeManager.getBlockInvalidateLimit());
        nameNode.reconfigureProperty(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, Integer.toString(50));
        // 20 * 50 = 1000 > 500
        // Invalid block limit should be reset to 1000
        Assert.assertEquals(((DFSConfigKeys.DFS_BLOCK_INVALIDATE_LIMIT_KEY) + " is not reconfigured correctly"), 1000, datanodeManager.getBlockInvalidateLimit());
    }
}

