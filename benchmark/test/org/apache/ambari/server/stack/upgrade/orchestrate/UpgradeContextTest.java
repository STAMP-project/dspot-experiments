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
package org.apache.ambari.server.stack.upgrade.orchestrate;


import Direction.DOWNGRADE;
import Direction.UPGRADE;
import RepositoryType.MAINT;
import RepositoryType.PATCH;
import RepositoryType.STANDARD;
import UpgradeResourceProvider.UPGRADE_DIRECTION;
import UpgradeResourceProvider.UPGRADE_REPO_VERSION_ID;
import UpgradeResourceProvider.UPGRADE_REVERT_UPGRADE_ID;
import UpgradeResourceProvider.UPGRADE_SKIP_PREREQUISITE_CHECKS;
import UpgradeResourceProvider.UPGRADE_TYPE;
import UpgradeType.NON_ROLLING;
import UpgradeType.ROLLING;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.UpgradeDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.orm.entities.UpgradeHistoryEntity;
import org.apache.ambari.server.stack.upgrade.Direction;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.repository.ClusterVersionSummary;
import org.apache.ambari.server.state.repository.VersionDefinitionXml;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Test;


/**
 * Tests {@link UpgradeContext}.
 */
public class UpgradeContextTest extends EasyMockSupport {
    private static final String HDFS_SERVICE_NAME = "HDFS";

    private static final String ZOOKEEPER_SERVICE_NAME = "ZOOKEEPER";

    /**
     * An existing upgrade which can be reverted.
     */
    @Mock
    private UpgradeEntity m_completedRevertableUpgrade;

    /**
     * The target repository of a completed upgrade.
     */
    @Mock
    private RepositoryVersionEntity m_targetRepositoryVersion;

    /**
     * The source repository of a completed upgrade.
     */
    @Mock
    private RepositoryVersionEntity m_sourceRepositoryVersion;

    /**
     * The cluster performing the upgrade.
     */
    @Mock
    private Cluster m_cluster;

    /**
     * HDFS
     */
    @Mock
    private Service m_hdfsService;

    /**
     * ZooKeeper
     */
    @Mock
    private Service m_zookeeperService;

    @Mock
    private UpgradeDAO m_upgradeDAO;

    @Mock
    private RepositoryVersionDAO m_repositoryVersionDAO;

    /**
     * Used to mock out what services will be provided to us by the VDF/cluster.
     */
    @Mock
    private ClusterVersionSummary m_clusterVersionSummary;

    /**
     *
     */
    @Mock
    private VersionDefinitionXml m_vdfXml;

    @Mock
    private AmbariMetaInfo m_ambariMetaInfo;

    /**
     * The upgrade history to return for the completed upgrade.
     */
    private List<UpgradeHistoryEntity> m_upgradeHistory = new ArrayList<>();

    /**
     * The cluster services.
     */
    private Map<String, Service> m_services = new HashMap<>();

    /**
     * Tests that the {@link UpgradeContext} for a normal upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFullUpgrade() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(m_targetRepositoryVersion.getType()).andReturn(STANDARD).atLeastOnce();
        expect(upgradeHelper.suggestUpgradePack(EasyMock.anyString(), EasyMock.anyObject(StackId.class), EasyMock.anyObject(StackId.class), EasyMock.anyObject(Direction.class), EasyMock.anyObject(UpgradeType.class), EasyMock.anyString())).andReturn(upgradePack).once();
        replayAll();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, ROLLING.name());
        requestMap.put(UPGRADE_DIRECTION, UPGRADE.name());
        requestMap.put(UPGRADE_REPO_VERSION_ID, m_targetRepositoryVersion.getId().toString());
        requestMap.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(UPGRADE, context.getDirection());
        Assert.assertEquals(STANDARD, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertFalse(context.isPatchRevert());
        Assert.assertFalse(context.getUpgradeSummary().isSwitchBits);
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a patch upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPatchUpgrade() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(m_clusterVersionSummary.getAvailableServiceNames()).andReturn(Sets.newHashSet(UpgradeContextTest.HDFS_SERVICE_NAME)).once();
        expect(m_targetRepositoryVersion.getType()).andReturn(PATCH).atLeastOnce();
        expect(m_targetRepositoryVersion.getRepositoryXml()).andReturn(m_vdfXml).once();
        expect(upgradeHelper.suggestUpgradePack(EasyMock.anyString(), EasyMock.anyObject(StackId.class), EasyMock.anyObject(StackId.class), EasyMock.anyObject(Direction.class), EasyMock.anyObject(UpgradeType.class), EasyMock.anyString())).andReturn(upgradePack).once();
        // make the cluster have 2 services just for fun (the VDF only has 1
        // service)
        expect(m_cluster.getService(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME)).andReturn(m_zookeeperService).anyTimes();
        m_services.put(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME, m_zookeeperService);
        Assert.assertEquals(2, m_services.size());
        replayAll();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, NON_ROLLING.name());
        requestMap.put(UPGRADE_DIRECTION, UPGRADE.name());
        requestMap.put(UPGRADE_REPO_VERSION_ID, m_targetRepositoryVersion.getId().toString());
        requestMap.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(UPGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertFalse(context.isPatchRevert());
        Assert.assertTrue(context.getUpgradeSummary().isSwitchBits);
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a maintenance upgrade.
     * Maintenance upgrades will only upgrade services which require it by
     * examining the versions included in the VDF.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMaintUpgrade() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(m_clusterVersionSummary.getAvailableServiceNames()).andReturn(Sets.newHashSet(UpgradeContextTest.HDFS_SERVICE_NAME)).once();
        expect(m_targetRepositoryVersion.getType()).andReturn(MAINT).atLeastOnce();
        expect(m_targetRepositoryVersion.getRepositoryXml()).andReturn(m_vdfXml).once();
        expect(upgradeHelper.suggestUpgradePack(EasyMock.anyString(), EasyMock.anyObject(StackId.class), EasyMock.anyObject(StackId.class), EasyMock.anyObject(Direction.class), EasyMock.anyObject(UpgradeType.class), EasyMock.anyString())).andReturn(upgradePack).once();
        // make the cluster have 2 services - one is already upgraded to a new
        // enough version
        expect(m_cluster.getService(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME)).andReturn(m_zookeeperService).anyTimes();
        m_services.put(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME, m_zookeeperService);
        Assert.assertEquals(2, m_services.size());
        replayAll();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, NON_ROLLING.name());
        requestMap.put(UPGRADE_DIRECTION, UPGRADE.name());
        requestMap.put(UPGRADE_REPO_VERSION_ID, m_targetRepositoryVersion.getId().toString());
        requestMap.put(UPGRADE_SKIP_PREREQUISITE_CHECKS, "true");
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(UPGRADE, context.getDirection());
        Assert.assertEquals(MAINT, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertFalse(context.isPatchRevert());
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a reversion has the correct
     * parameters set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRevert() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        Map<String, UpgradePack> map = ImmutableMap.<String, UpgradePack>builder().put("myUpgradePack", upgradePack).build();
        expect(ami.getUpgradePacks(EasyMock.anyString(), EasyMock.anyString())).andReturn(map).anyTimes();
        expect(m_upgradeDAO.findRevertable(1L)).andReturn(m_completedRevertableUpgrade).once();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, ROLLING.name());
        requestMap.put(UPGRADE_REVERT_UPGRADE_ID, "1");
        replayAll();
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(DOWNGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertTrue(context.isPatchRevert());
        Assert.assertTrue(context.getUpgradeSummary().isSwitchBits);
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a EU reversion has the correct
     * parameters set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRevertEU() throws Exception {
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        Map<String, UpgradePack> map = ImmutableMap.<String, UpgradePack>builder().put("myUpgradePack", upgradePack).build();
        expect(ami.getUpgradePacks(EasyMock.anyString(), EasyMock.anyString())).andReturn(map).anyTimes();
        expect(m_upgradeDAO.findRevertable(1L)).andReturn(m_completedRevertableUpgrade).once();
        expect(m_completedRevertableUpgrade.getUpgradeType()).andReturn(NON_ROLLING);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_REVERT_UPGRADE_ID, "1");
        replayAll();
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(DOWNGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(NON_ROLLING, context.getType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertTrue(context.isPatchRevert());
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a reversion has the correct
     * services included in the reversion if one of the services in the original
     * upgrade has since been deleted.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRevertWithDeletedService() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        // give the completed upgrade 2 services which can be reverted
        UpgradeHistoryEntity upgradeHistoryEntity = createNiceMock(UpgradeHistoryEntity.class);
        expect(upgradeHistoryEntity.getServiceName()).andReturn(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME).anyTimes();
        expect(upgradeHistoryEntity.getFromReposistoryVersion()).andReturn(m_sourceRepositoryVersion).anyTimes();
        expect(upgradeHistoryEntity.getTargetRepositoryVersion()).andReturn(m_targetRepositoryVersion).anyTimes();
        m_upgradeHistory.add(upgradeHistoryEntity);
        Map<String, UpgradePack> map = ImmutableMap.<String, UpgradePack>builder().put("myUpgradePack", upgradePack).build();
        expect(ami.getUpgradePacks(EasyMock.anyString(), EasyMock.anyString())).andReturn(map).anyTimes();
        expect(m_upgradeDAO.findRevertable(1L)).andReturn(m_completedRevertableUpgrade).once();
        // remove HDFS, add ZK
        m_services.remove(UpgradeContextTest.HDFS_SERVICE_NAME);
        expect(m_cluster.getService(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME)).andReturn(m_zookeeperService).anyTimes();
        m_services.put(UpgradeContextTest.ZOOKEEPER_SERVICE_NAME, m_zookeeperService);
        Assert.assertEquals(1, m_services.size());
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, ROLLING.name());
        requestMap.put(UPGRADE_REVERT_UPGRADE_ID, "1");
        replayAll();
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(DOWNGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertTrue(context.isPatchRevert());
        verifyAll();
    }

    /**
     * Tests that if a different {@link UpgradeEntity} is returned instead of the one
     * specified by the
     *
     * @throws Exception
     * 		
     */
    @Test(expected = AmbariException.class)
    public void testWrongUpgradeBeingReverted() throws Exception {
        Long upgradeIdBeingReverted = 1L;
        Long upgradeIdWhichCanBeReverted = 99L;
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(upgradeHelper.suggestUpgradePack(EasyMock.anyString(), EasyMock.anyObject(StackId.class), EasyMock.anyObject(StackId.class), EasyMock.anyObject(Direction.class), EasyMock.anyObject(UpgradeType.class), EasyMock.anyString())).andReturn(upgradePack).once();
        RepositoryVersionEntity repositoryVersionEntity = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersionEntity.getVersion()).andReturn("1.2.3.4").anyTimes();
        UpgradeEntity wrongRevertableUpgrade = createNiceMock(UpgradeEntity.class);
        expect(wrongRevertableUpgrade.getId()).andReturn(upgradeIdWhichCanBeReverted).atLeastOnce();
        expect(wrongRevertableUpgrade.getRepositoryVersion()).andReturn(repositoryVersionEntity).atLeastOnce();
        expect(m_upgradeDAO.findRevertable(1L)).andReturn(wrongRevertableUpgrade).once();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, ROLLING.name());
        requestMap.put(UPGRADE_REVERT_UPGRADE_ID, upgradeIdBeingReverted.toString());
        replayAll();
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(DOWNGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertTrue(context.isPatchRevert());
        verifyAll();
    }

    /**
     * Tests that the {@link UpgradeContext} for a patch downgrade has the
     * correcting scope/orchestration set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDowngradeForPatch() throws Exception {
        UpgradeHelper upgradeHelper = createNiceMock(UpgradeHelper.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        Map<String, UpgradePack> map = ImmutableMap.<String, UpgradePack>builder().put("myUpgradePack", upgradePack).build();
        expect(ami.getUpgradePacks(EasyMock.anyString(), EasyMock.anyString())).andReturn(map).anyTimes();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UPGRADE_TYPE, NON_ROLLING.name());
        requestMap.put(UPGRADE_DIRECTION, DOWNGRADE.name());
        replayAll();
        UpgradeContext context = new UpgradeContext(m_cluster, requestMap, null, upgradeHelper, m_upgradeDAO, m_repositoryVersionDAO, configHelper, ami);
        Assert.assertEquals(DOWNGRADE, context.getDirection());
        Assert.assertEquals(PATCH, context.getOrchestrationType());
        Assert.assertEquals(1, context.getSupportedServices().size());
        Assert.assertFalse(context.isPatchRevert());
        verifyAll();
    }
}

