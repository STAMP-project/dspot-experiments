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
package org.apache.ambari.server.controller.internal;


import Direction.UPGRADE;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_CHECK_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_CHECK_TYPE_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_CLUSTER_NAME_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_ID_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_REASON_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_STATUS_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_TARGET_REPOSITORY_VERSION_ID_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_UPGRADE_PACK_PROPERTY_ID;
import PreUpgradeCheckResourceProvider.UPGRADE_CHECK_UPGRADE_TYPE_PROPERTY_ID;
import RepositoryType.STANDARD;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckType.HOST;
import UpgradeType.NON_ROLLING;
import UpgradeType.ROLLING;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.checks.UpgradeCheckRegistry;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.sample.checks.SampleServiceCheck;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.stack.upgrade.UpgradePack.PrerequisiteCheckConfig;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.upgrade.UpgradeCheckStatus;
import org.apache.ambari.spi.upgrade.UpgradeCheckType;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;


/**
 * PreUpgradeCheckResourceProvider tests.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UpgradeCheckRegistry.class })
public class PreUpgradeCheckResourceProviderTest extends EasyMockSupport {
    private static final String TEST_SERVICE_CHECK_CLASS_NAME = "org.apache.ambari.server.sample.checks.SampleServiceCheck";

    private static final String CLUSTER_NAME = "Cluster100";

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetResources() throws Exception {
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        UpgradeHelper upgradeHelper = injector.getInstance(UpgradeHelper.class);
        RepositoryVersionDAO repoDao = injector.getInstance(RepositoryVersionDAO.class);
        RepositoryVersionEntity repo = createNiceMock(RepositoryVersionEntity.class);
        UpgradePack upgradePack = createNiceMock(UpgradePack.class);
        PrerequisiteCheckConfig config = createNiceMock(PrerequisiteCheckConfig.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ServiceInfo serviceInfo = createNiceMock(ServiceInfo.class);
        ClusterInformation clusterInformation = createNiceMock(ClusterInformation.class);
        expect(service.getDesiredRepositoryVersion()).andReturn(repo).atLeastOnce();
        StackId currentStackId = createNiceMock(StackId.class);
        StackId targetStackId = createNiceMock(StackId.class);
        AmbariMetaInfo ambariMetaInfo = injector.getInstance(AmbariMetaInfo.class);
        ambariMetaInfo.init();
        expectLastCall().anyTimes();
        Config actualConfig = createNiceMock(Config.class);
        DesiredConfig desiredConfig = createNiceMock(DesiredConfig.class);
        Map<String, DesiredConfig> configMap = Maps.newHashMap();
        configMap.put("config-type", desiredConfig);
        expect(desiredConfig.getTag()).andReturn("config-tag-1").atLeastOnce();
        expect(cluster.getDesiredConfigs()).andReturn(configMap).atLeastOnce();
        expect(cluster.getConfig("config-type", "config-tag-1")).andReturn(actualConfig).atLeastOnce();
        expect(cluster.buildClusterInformation()).andReturn(clusterInformation).anyTimes();
        expect(clusterInformation.getClusterName()).andReturn(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME).anyTimes();
        Map<String, Service> allServiceMap = new HashMap<>();
        allServiceMap.put("Service100", service);
        Map<String, ServiceInfo> allServiceInfoMap = new HashMap<>();
        allServiceInfoMap.put("Service100", serviceInfo);
        ServiceComponentHost serviceComponentHost = createNiceMock(ServiceComponentHost.class);
        expect(serviceComponentHost.getServiceName()).andReturn("Service100").atLeastOnce();
        expect(serviceComponentHost.getServiceComponentName()).andReturn("Component100").atLeastOnce();
        expect(serviceComponentHost.getHostName()).andReturn("c6401.ambari.apache.org").atLeastOnce();
        List<ServiceComponentHost> serviceComponentHosts = Lists.newArrayList();
        serviceComponentHosts.add(serviceComponentHost);
        expect(cluster.getServiceComponentHosts()).andReturn(serviceComponentHosts).atLeastOnce();
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(clusters.getCluster(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME)).andReturn(cluster).anyTimes();
        expect(cluster.getClusterName()).andReturn(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME).atLeastOnce();
        expect(cluster.getServices()).andReturn(allServiceMap).anyTimes();
        expect(cluster.getService("Service100")).andReturn(service).anyTimes();
        expect(cluster.getCurrentStackVersion()).andReturn(currentStackId).anyTimes();
        expect(currentStackId.getStackName()).andReturn("Stack100").anyTimes();
        expect(currentStackId.getStackVersion()).andReturn("1.0").anyTimes();
        expect(targetStackId.getStackId()).andReturn("Stack100-1.1").anyTimes();
        expect(targetStackId.getStackName()).andReturn("Stack100").anyTimes();
        expect(targetStackId.getStackVersion()).andReturn("1.1").anyTimes();
        expect(repoDao.findByPK(1L)).andReturn(repo).anyTimes();
        expect(repo.getStackId()).andReturn(targetStackId).atLeastOnce();
        expect(repo.getId()).andReturn(1L).atLeastOnce();
        expect(repo.getType()).andReturn(STANDARD).atLeastOnce();
        expect(repo.getVersion()).andReturn("1.1.0.0").atLeastOnce();
        expect(upgradeHelper.suggestUpgradePack(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME, currentStackId, targetStackId, UPGRADE, NON_ROLLING, "upgrade_pack11")).andReturn(upgradePack);
        List<String> prerequisiteChecks = new LinkedList<>();
        prerequisiteChecks.add(PreUpgradeCheckResourceProviderTest.TEST_SERVICE_CHECK_CLASS_NAME);
        expect(upgradePack.getPrerequisiteCheckConfig()).andReturn(config);
        expect(upgradePack.getPrerequisiteChecks()).andReturn(prerequisiteChecks).anyTimes();
        expect(upgradePack.getTarget()).andReturn("1.1.*.*").anyTimes();
        expect(upgradePack.getOwnerStackId()).andReturn(targetStackId).atLeastOnce();
        expect(upgradePack.getType()).andReturn(ROLLING).atLeastOnce();
        expect(ambariMetaInfo.getServices("Stack100", "1.0")).andReturn(allServiceInfoMap).anyTimes();
        String checks = ClassLoader.getSystemClassLoader().getResource("checks").getPath();
        expect(serviceInfo.getChecksFolder()).andReturn(new File(checks));
        URL url = new URL("file://foo");
        URLClassLoader classLoader = createNiceMock(URLClassLoader.class);
        expect(classLoader.getURLs()).andReturn(new URL[]{ url }).once();
        StackInfo stackInfo = createNiceMock(StackInfo.class);
        expect(ambariMetaInfo.getStack(targetStackId)).andReturn(stackInfo).atLeastOnce();
        expect(stackInfo.getLibraryClassLoader()).andReturn(classLoader).atLeastOnce();
        expect(stackInfo.getLibraryInstance(EasyMock.anyObject(), EasyMock.eq(PreUpgradeCheckResourceProviderTest.TEST_SERVICE_CHECK_CLASS_NAME))).andReturn(new SampleServiceCheck()).atLeastOnce();
        // mock out plugin check loading
        Reflections reflectionsMock = createNiceMock(Reflections.class);
        PowerMockito.whenNew(Reflections.class).withParameterTypes(Configuration.class).withArguments(Matchers.any(ConfigurationBuilder.class)).thenReturn(reflectionsMock);
        PowerMock.replay(Reflections.class);
        // replay
        replayAll();
        ResourceProvider provider = getPreUpgradeCheckResourceProvider(managementController, injector);
        // create the request
        Request request = PropertyHelper.getReadRequest(new HashSet());
        PredicateBuilder builder = new PredicateBuilder();
        Predicate predicate = builder.property(UPGRADE_CHECK_CLUSTER_NAME_PROPERTY_ID).equals(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME).and().property(UPGRADE_CHECK_UPGRADE_PACK_PROPERTY_ID).equals("upgrade_pack11").and().property(UPGRADE_CHECK_UPGRADE_TYPE_PROPERTY_ID).equals(NON_ROLLING).and().property(UPGRADE_CHECK_TARGET_REPOSITORY_VERSION_ID_ID).equals("1").toPredicate();
        Set<Resource> resources = Collections.emptySet();
        resources = provider.getResources(request, predicate);
        // make sure all of the checks ran and were returned in the response; some
        // of the checks are stripped out b/c they don't define any required upgrade
        // types. The value being asserted here is a combination of built-in checks
        // which are required for the upgrade type as well as any provided checks
        // discovered in the stack
        Assert.assertEquals(20, resources.size());
        // find the service check provided by the library classloader and verify it ran
        Resource customUpgradeCheck = null;
        for (Resource resource : resources) {
            String id = ((String) (resource.getPropertyValue(UPGRADE_CHECK_ID_PROPERTY_ID)));
            if (StringUtils.equals(id, "SAMPLE_SERVICE_CHECK")) {
                customUpgradeCheck = resource;
                break;
            }
        }
        Assert.assertNotNull(customUpgradeCheck);
        String description = ((String) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_CHECK_PROPERTY_ID)));
        Assert.assertEquals("Sample service check description.", description);
        UpgradeCheckStatus status = ((UpgradeCheckStatus) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_STATUS_PROPERTY_ID)));
        Assert.assertEquals(FAIL, status);
        String reason = ((String) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_REASON_PROPERTY_ID)));
        Assert.assertEquals("Sample service check always fails.", reason);
        UpgradeCheckType checkType = ((UpgradeCheckType) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_CHECK_TYPE_PROPERTY_ID)));
        Assert.assertEquals(HOST, checkType);
        String clusterName = ((String) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_CLUSTER_NAME_PROPERTY_ID)));
        Assert.assertEquals(PreUpgradeCheckResourceProviderTest.CLUSTER_NAME, clusterName);
        UpgradeType upgradeType = ((UpgradeType) (customUpgradeCheck.getPropertyValue(UPGRADE_CHECK_UPGRADE_TYPE_PROPERTY_ID)));
        Assert.assertEquals(NON_ROLLING, upgradeType);
        PowerMock.verifyAll();
    }
}

