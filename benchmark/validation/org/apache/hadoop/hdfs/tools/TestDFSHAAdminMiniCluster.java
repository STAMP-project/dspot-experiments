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
package org.apache.hadoop.hdfs.tools;


import DFSConfigKeys.DFS_HA_FENCE_METHODS_KEY;
import HAServiceState.STANDBY;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAAdmin;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Tests for HAAdmin command with {@link MiniDFSCluster} set up in HA mode.
 */
public class TestDFSHAAdminMiniCluster {
    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(HAAdmin.class), Level.TRACE);
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestDFSHAAdminMiniCluster.class);

    private MiniDFSCluster cluster;

    private Configuration conf;

    private DFSHAAdmin tool;

    private final ByteArrayOutputStream errOutBytes = new ByteArrayOutputStream();

    private String errOutput;

    private int nn1Port;

    @Test
    public void testGetServiceState() throws Exception {
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
        Assert.assertEquals(0, runTool("-getServiceState", "nn2"));
        cluster.transitionToActive(0);
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
        NameNodeAdapter.enterSafeMode(cluster.getNameNode(0), false);
        Assert.assertEquals(0, runTool("-getServiceState", "nn1"));
    }

    @Test
    public void testStateTransition() throws Exception {
        NameNode nnode1 = cluster.getNameNode(0);
        Assert.assertTrue(nnode1.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToActive", "nn1"));
        Assert.assertFalse(nnode1.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToStandby", "nn1"));
        Assert.assertTrue(nnode1.isStandbyState());
        NameNode nnode2 = cluster.getNameNode(1);
        Assert.assertTrue(nnode2.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToActive", "nn2"));
        Assert.assertFalse(nnode2.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToStandby", "nn2"));
        Assert.assertTrue(nnode2.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToObserver", "nn2"));
        Assert.assertFalse(nnode2.isStandbyState());
        Assert.assertTrue(nnode2.isObserverState());
    }

    @Test
    public void testObserverTransition() throws Exception {
        NameNode nnode1 = cluster.getNameNode(0);
        Assert.assertTrue(nnode1.isStandbyState());
        // Should be able to transition from STANDBY to OBSERVER
        Assert.assertEquals(0, runTool("-transitionToObserver", "nn1"));
        Assert.assertFalse(nnode1.isStandbyState());
        Assert.assertTrue(nnode1.isObserverState());
        // Transition from Observer to Observer should be no-op
        Assert.assertEquals(0, runTool("-transitionToObserver", "nn1"));
        Assert.assertTrue(nnode1.isObserverState());
        // Should also be able to transition back from OBSERVER to STANDBY
        Assert.assertEquals(0, runTool("-transitionToStandby", "nn1"));
        Assert.assertTrue(nnode1.isStandbyState());
        Assert.assertFalse(nnode1.isObserverState());
    }

    @Test
    public void testObserverIllegalTransition() throws Exception {
        NameNode nnode1 = cluster.getNameNode(0);
        Assert.assertTrue(nnode1.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToActive", "nn1"));
        Assert.assertFalse(nnode1.isStandbyState());
        Assert.assertTrue(nnode1.isActiveState());
        // Should NOT be able to transition from ACTIVE to OBSERVER
        Assert.assertEquals((-1), runTool("-transitionToObserver", "nn1"));
        Assert.assertTrue(nnode1.isActiveState());
        // Should NOT be able to transition from OBSERVER to ACTIVE
        Assert.assertEquals(0, runTool("-transitionToStandby", "nn1"));
        Assert.assertTrue(nnode1.isStandbyState());
        Assert.assertEquals(0, runTool("-transitionToObserver", "nn1"));
        Assert.assertTrue(nnode1.isObserverState());
        Assert.assertEquals((-1), runTool("-transitionToActive", "nn1"));
        Assert.assertFalse(nnode1.isActiveState());
    }

    @Test
    public void testTryFailoverToSafeMode() throws Exception {
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        NameNodeAdapter.enterSafeMode(cluster.getNameNode(0), false);
        Assert.assertEquals((-1), runTool("-failover", "nn2", "nn1"));
        Assert.assertTrue(("Bad output: " + (errOutput)), errOutput.contains(("is not ready to become active: " + "The NameNode is in safemode")));
    }

    /**
     * Test failover with various options
     */
    @Test
    public void testFencer() throws Exception {
        // Test failover with no fencer
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2"));
        // Set up fencer to write info about the fencing target into a
        // tmp file, so we can verify that the args were substituted right
        File tmpFile = File.createTempFile("testFencer", ".txt");
        tmpFile.deleteOnExit();
        if (Shell.WINDOWS) {
            conf.set(DFS_HA_FENCE_METHODS_KEY, ((("shell(echo %target_nameserviceid%.%target_namenodeid% " + "%target_port% %dfs_ha_namenode_id% > ") + (tmpFile.getAbsolutePath())) + ")"));
        } else {
            conf.set(DFS_HA_FENCE_METHODS_KEY, ((("shell(echo -n $target_nameserviceid.$target_namenodeid " + "$target_port $dfs_ha_namenode_id > ") + (tmpFile.getAbsolutePath())) + ")"));
        }
        // Test failover with fencer
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-transitionToActive", "nn1"));
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2"));
        // Test failover with fencer and nameservice
        Assert.assertEquals(0, runTool("-ns", "minidfs-ns", "-failover", "nn2", "nn1"));
        // Fencer has not run yet, since none of the above required fencing
        Assert.assertEquals("", Files.toString(tmpFile, Charsets.UTF_8));
        // Test failover with fencer and forcefence option
        Assert.assertEquals(0, runTool("-failover", "nn1", "nn2", "--forcefence"));
        // The fence script should run with the configuration from the target
        // node, rather than the configuration from the fencing node. Strip
        // out any trailing spaces and CR/LFs which may be present on Windows.
        String fenceCommandOutput = Files.toString(tmpFile, Charsets.UTF_8).replaceAll(" *[\r\n]+", "");
        Assert.assertEquals((("minidfs-ns.nn1 " + (nn1Port)) + " nn1"), fenceCommandOutput);
        tmpFile.delete();
        // Test failover with forceactive option
        Assert.assertEquals(0, runTool("-failover", "nn2", "nn1", "--forceactive"));
        // Fencing should not occur, since it was graceful
        Assert.assertFalse(tmpFile.exists());
        // Test failover with not fencer and forcefence option
        conf.unset(DFS_HA_FENCE_METHODS_KEY);
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
        Assert.assertFalse(tmpFile.exists());
        // Test failover with bad fencer and forcefence option
        conf.set(DFS_HA_FENCE_METHODS_KEY, "foobar!");
        tool.setConf(conf);
        Assert.assertEquals((-1), runTool("-failover", "nn1", "nn2", "--forcefence"));
        Assert.assertFalse(tmpFile.exists());
        // Test failover with force fence listed before the other arguments
        conf.set(DFS_HA_FENCE_METHODS_KEY, TestDFSHAAdmin.getFencerTrueCommand());
        tool.setConf(conf);
        Assert.assertEquals(0, runTool("-failover", "--forcefence", "nn1", "nn2"));
    }

    @Test
    public void testCheckHealth() throws Exception {
        Assert.assertEquals(0, runTool("-checkHealth", "nn1"));
        Assert.assertEquals(0, runTool("-checkHealth", "nn2"));
    }

    /**
     * Test case to check whether both the name node is active or not
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTransitionToActiveWhenOtherNamenodeisActive() throws Exception {
        NameNode nn1 = cluster.getNameNode(0);
        NameNode nn2 = cluster.getNameNode(1);
        if (((nn1.getState()) != null) && (!(nn1.getState().equals(STANDBY.name())))) {
            cluster.transitionToStandby(0);
        }
        if (((nn2.getState()) != null) && (!(nn2.getState().equals(STANDBY.name())))) {
            cluster.transitionToStandby(1);
        }
        // Making sure both the namenode are in standby state
        Assert.assertTrue(nn1.isStandbyState());
        Assert.assertTrue(nn2.isStandbyState());
        // Triggering the transition for both namenode to Active
        runTool("-transitionToActive", "nn1");
        runTool("-transitionToActive", "nn2");
        Assert.assertFalse("Both namenodes cannot be active", ((nn1.isActiveState()) && (nn2.isActiveState())));
        /* In this test case, we have deliberately shut down nn1 and this will
        cause HAAAdmin#isOtherTargetNodeActive to throw an Exception 
        and transitionToActive for nn2 with  forceActive switch will succeed 
        even with Exception
         */
        cluster.shutdownNameNode(0);
        if (((nn2.getState()) != null) && (!(nn2.getState().equals(STANDBY.name())))) {
            cluster.transitionToStandby(1);
        }
        // Making sure both the namenode (nn2) is in standby state
        Assert.assertTrue(nn2.isStandbyState());
        Assert.assertFalse(cluster.isNameNodeUp(0));
        runTool("-transitionToActive", "nn2", "--forceactive");
        Assert.assertTrue("Namenode nn2 should be active", nn2.isActiveState());
    }
}

