/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om;


import GenericTestUtils.LogCapturer;
import HddsConfigKeys.HDDS_SCM_CHILLMODE_ENABLED;
import HddsConfigKeys.HDDS_SCM_CHILLMODE_MIN_DATANODE;
import HddsConfigKeys.HDDS_SCM_CHILLMODE_THRESHOLD_PCT;
import HddsConfigKeys.HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT;
import OzoneConfigKeys.OZONE_METADATA_STORE_IMPL;
import OzoneConfigKeys.OZONE_METADATA_STORE_IMPL_LEVELDB;
import SCMEvents.CHILL_MODE_STATUS;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.chillmode.SCMChillModeManager;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.SCMContainerManager;
import org.apache.hadoop.hdds.scm.exceptions.SCMException;
import org.apache.hadoop.hdds.scm.protocolPB.StorageContainerLocationProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdds.scm.server.SCMClientProtocolServer;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.HddsDatanodeService;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.MiniOzoneClusterImpl;
import org.apache.hadoop.ozone.TestStorageContainerManagerHelper;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.om.helpers.OmBucketInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmVolumeArgs;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Ozone Manager operation in distributed handler scenario.
 */
public class TestScmChillMode {
    private static final Logger LOG = LoggerFactory.getLogger(TestScmChillMode.class);

    private static MiniOzoneCluster cluster = null;

    private static MiniOzoneCluster.Builder builder = null;

    private static OzoneConfiguration conf;

    private static OzoneManager om;

    private static StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient;

    @Rule
    public Timeout timeout = new Timeout((1000 * 200));

    @Test
    public void testChillModeOperations() throws Exception {
        final AtomicReference<MiniOzoneCluster> miniCluster = new AtomicReference<>();
        // Create {numKeys} random names keys.
        TestStorageContainerManagerHelper helper = new TestStorageContainerManagerHelper(TestScmChillMode.cluster, TestScmChillMode.conf);
        Map<String, OmKeyInfo> keyLocations = helper.createKeys(100, 4096);
        final List<ContainerInfo> containers = TestScmChillMode.cluster.getStorageContainerManager().getContainerManager().getContainers();
        GenericTestUtils.waitFor(() -> (containers.size()) >= 3, 100, 1000);
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        String userName = "user" + (RandomStringUtils.randomNumeric(5));
        String adminName = "admin" + (RandomStringUtils.randomNumeric(5));
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(volumeName).setBucketName(bucketName).setKeyName(keyName).setDataSize(1000).build();
        OmVolumeArgs volArgs = new OmVolumeArgs.Builder().setAdminName(adminName).setCreationTime(Time.monotonicNow()).setQuotaInBytes(10000).setVolume(volumeName).setOwnerName(userName).build();
        OmBucketInfo bucketInfo = new OmBucketInfo.Builder().setBucketName(bucketName).setIsVersionEnabled(false).setVolumeName(volumeName).build();
        TestScmChillMode.om.createVolume(volArgs);
        TestScmChillMode.om.createBucket(bucketInfo);
        TestScmChillMode.om.openKey(keyArgs);
        // om.commitKey(keyArgs, 1);
        TestScmChillMode.cluster.stop();
        new Thread(() -> {
            try {
                miniCluster.set(TestScmChillMode.builder.build());
            } catch (IOException e) {
                Assert.fail("failed");
            }
        }).start();
        StorageContainerManager scm;
        GenericTestUtils.waitFor(() -> {
            return (miniCluster.get()) != null;
        }, 100, (1000 * 3));
        TestScmChillMode.cluster = miniCluster.get();
        scm = TestScmChillMode.cluster.getStorageContainerManager();
        Assert.assertTrue(scm.isInChillMode());
        TestScmChillMode.om = miniCluster.get().getOzoneManager();
        LambdaTestUtils.intercept(OMException.class, "ChillModePrecheck failed for allocateBlock", () -> TestScmChillMode.om.openKey(keyArgs));
    }

    /**
     * Tests inChillMode & forceExitChillMode api calls.
     */
    @Test
    public void testIsScmInChillModeAndForceExit() throws Exception {
        final AtomicReference<MiniOzoneCluster> miniCluster = new AtomicReference<>();
        // Test 1: SCM should be out of chill mode.
        Assert.assertFalse(TestScmChillMode.storageContainerLocationClient.inChillMode());
        TestScmChillMode.cluster.stop();
        // Restart the cluster with same metadata dir.
        new Thread(() -> {
            try {
                miniCluster.set(TestScmChillMode.builder.build());
            } catch (IOException e) {
                Assert.fail("Cluster startup failed.");
            }
        }).start();
        GenericTestUtils.waitFor(() -> {
            return (miniCluster.get()) != null;
        }, 10, (1000 * 3));
        TestScmChillMode.cluster = miniCluster.get();
        // Test 2: Scm should be in chill mode as datanodes are not started yet.
        TestScmChillMode.storageContainerLocationClient = TestScmChillMode.cluster.getStorageContainerLocationClient();
        Assert.assertTrue(TestScmChillMode.storageContainerLocationClient.inChillMode());
        // Force scm out of chill mode.
        TestScmChillMode.cluster.getStorageContainerManager().getClientProtocolServer().forceExitChillMode();
        // Test 3: SCM should be out of chill mode.
        GenericTestUtils.waitFor(() -> {
            try {
                return !(TestScmChillMode.cluster.getStorageContainerManager().getClientProtocolServer().inChillMode());
            } catch ( e) {
                Assert.fail("Cluster");
                return false;
            }
        }, 10, (1000 * 5));
    }

    @Test(timeout = 300000)
    public void testSCMChillMode() throws Exception {
        MiniOzoneCluster.Builder clusterBuilder = MiniOzoneCluster.newBuilder(TestScmChillMode.conf).setHbInterval(1000).setNumDatanodes(3).setStartDataNodes(false).setHbProcessorInterval(500);
        MiniOzoneClusterImpl miniCluster = ((MiniOzoneClusterImpl) (clusterBuilder.build()));
        // Test1: Test chill mode  when there are no containers in system.
        Assert.assertTrue(miniCluster.getStorageContainerManager().isInChillMode());
        miniCluster.startHddsDatanodes();
        miniCluster.waitForClusterToBeReady();
        Assert.assertFalse(miniCluster.getStorageContainerManager().isInChillMode());
        // Test2: Test chill mode  when containers are there in system.
        // Create {numKeys} random names keys.
        TestStorageContainerManagerHelper helper = new TestStorageContainerManagerHelper(miniCluster, TestScmChillMode.conf);
        Map<String, OmKeyInfo> keyLocations = helper.createKeys((100 * 2), 4096);
        final List<ContainerInfo> containers = miniCluster.getStorageContainerManager().getContainerManager().getContainers();
        GenericTestUtils.waitFor(() -> (containers.size()) >= 3, 100, (1000 * 2));
        // Removing some container to keep them open.
        containers.remove(0);
        containers.remove(0);
        // Close remaining containers
        SCMContainerManager mapping = ((SCMContainerManager) (miniCluster.getStorageContainerManager().getContainerManager()));
        containers.forEach(( c) -> {
            try {
                mapping.updateContainerState(c.containerID(), HddsProtos.LifeCycleEvent.FINALIZE);
                mapping.updateContainerState(c.containerID(), LifeCycleEvent.CLOSE);
            } catch ( e) {
                LOG.info("Failed to change state of open containers.", e);
            }
        });
        miniCluster.stop();
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(SCMChillModeManager.getLogger());
        logCapturer.clearOutput();
        AtomicReference<MiniOzoneCluster> miniClusterOzone = new AtomicReference<>();
        new Thread(() -> {
            try {
                miniClusterOzone.set(clusterBuilder.setStartDataNodes(false).build());
            } catch (IOException e) {
                Assert.fail("failed");
            }
        }).start();
        StorageContainerManager scm;
        GenericTestUtils.waitFor(() -> {
            return (miniClusterOzone.get()) != null;
        }, 100, (1000 * 3));
        miniCluster = ((MiniOzoneClusterImpl) (miniClusterOzone.get()));
        scm = miniCluster.getStorageContainerManager();
        Assert.assertTrue(scm.isInChillMode());
        Assert.assertFalse(logCapturer.getOutput().contains("SCM exiting chill mode."));
        Assert.assertTrue(((scm.getCurrentContainerThreshold()) == 0));
        for (HddsDatanodeService dn : miniCluster.getHddsDatanodes()) {
            dn.start(null);
        }
        GenericTestUtils.waitFor(() -> (scm.getCurrentContainerThreshold()) == 1.0, 100, 20000);
        TestScmChillMode.cluster = miniCluster;
        double chillModeCutoff = TestScmChillMode.conf.getDouble(HDDS_SCM_CHILLMODE_THRESHOLD_PCT, HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT);
        Assert.assertTrue(((scm.getCurrentContainerThreshold()) >= chillModeCutoff));
        Assert.assertTrue(logCapturer.getOutput().contains("SCM exiting chill mode."));
        Assert.assertFalse(scm.isInChillMode());
    }

    @Test
    public void testSCMChillModeRestrictedOp() throws Exception {
        TestScmChillMode.conf.set(OZONE_METADATA_STORE_IMPL, OZONE_METADATA_STORE_IMPL_LEVELDB);
        TestScmChillMode.cluster.stop();
        TestScmChillMode.cluster = TestScmChillMode.builder.build();
        StorageContainerManager scm = TestScmChillMode.cluster.getStorageContainerManager();
        Assert.assertTrue(scm.isInChillMode());
        LambdaTestUtils.intercept(SCMException.class, "ChillModePrecheck failed for allocateContainer", () -> {
            scm.getClientProtocolServer().allocateContainer(ReplicationType.STAND_ALONE, ReplicationFactor.ONE, "");
        });
        TestScmChillMode.cluster.startHddsDatanodes();
        TestScmChillMode.cluster.waitForClusterToBeReady();
        Assert.assertFalse(scm.isInChillMode());
        TestStorageContainerManagerHelper helper = new TestStorageContainerManagerHelper(TestScmChillMode.cluster, TestScmChillMode.conf);
        helper.createKeys(10, 4096);
        SCMClientProtocolServer clientProtocolServer = TestScmChillMode.cluster.getStorageContainerManager().getClientProtocolServer();
        Assert.assertFalse(scm.getClientProtocolServer().getChillModeStatus());
        final List<ContainerInfo> containers = scm.getContainerManager().getContainers();
        scm.getEventQueue().fireEvent(CHILL_MODE_STATUS, true);
        GenericTestUtils.waitFor(() -> {
            return clientProtocolServer.getChillModeStatus();
        }, 50, (1000 * 5));
        Assert.assertTrue(clientProtocolServer.getChillModeStatus());
        LambdaTestUtils.intercept(SCMException.class, (((("Open container " + (containers.get(0).getContainerID())) + " ") + "doesn't have enough replicas to service this operation in Chill") + " mode."), () -> clientProtocolServer.getContainerWithPipeline(containers.get(0).getContainerID()));
    }

    @Test(timeout = 300000)
    public void testSCMChillModeDisabled() throws Exception {
        TestScmChillMode.cluster.stop();
        // If chill mode is disabled, cluster should not be in chill mode even if
        // min number of datanodes are not started.
        TestScmChillMode.conf.setBoolean(HDDS_SCM_CHILLMODE_ENABLED, false);
        TestScmChillMode.conf.setInt(HDDS_SCM_CHILLMODE_MIN_DATANODE, 3);
        TestScmChillMode.builder = MiniOzoneCluster.newBuilder(TestScmChillMode.conf).setHbInterval(1000).setHbProcessorInterval(500).setNumDatanodes(1);
        TestScmChillMode.cluster = TestScmChillMode.builder.build();
        StorageContainerManager scm = TestScmChillMode.cluster.getStorageContainerManager();
        Assert.assertFalse(scm.isInChillMode());
        // Even on SCM restart, cluster should be out of chill mode immediately.
        TestScmChillMode.cluster.restartStorageContainerManager();
        Assert.assertFalse(scm.isInChillMode());
    }
}

