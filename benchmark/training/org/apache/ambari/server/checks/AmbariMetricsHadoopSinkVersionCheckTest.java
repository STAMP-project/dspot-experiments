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
package org.apache.ambari.server.checks;


import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.IN_PROGRESS;
import Resource.Type.Request;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariServer;
import org.apache.ambari.server.controller.internal.AbstractControllerResourceProvider;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.RequestDAO;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.repository.ClusterVersionSummary;
import org.apache.ambari.server.state.repository.VersionDefinitionXml;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.RepositoryVersion;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmbariServer.class, AbstractControllerResourceProvider.class, PropertyHelper.class })
public class AmbariMetricsHadoopSinkVersionCheckTest {
    private final Clusters m_clusters = Mockito.mock(Clusters.class);

    private final AmbariMetricsHadoopSinkVersionCompatibilityCheck m_check = new AmbariMetricsHadoopSinkVersionCompatibilityCheck();

    private final RepositoryVersionDAO repositoryVersionDAO = Mockito.mock(RepositoryVersionDAO.class);

    private ClusterVersionSummary m_clusterVersionSummary;

    private VersionDefinitionXml m_vdfXml;

    @Mock
    private RepositoryVersion m_repositoryVersion;

    @Mock
    private RepositoryVersionEntity m_repositoryVersionEntity;

    private MockCheckHelper m_checkHelper = new MockCheckHelper();

    final Map<String, Service> m_services = new HashMap<>();

    /**
     * Tests that the check is applicable when hive is installed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIsApplicable() throws Exception {
        Assert.assertTrue(m_check.getApplicableServices().contains("HDFS"));
        Assert.assertTrue(m_check.getApplicableServices().contains("AMBARI_METRICS"));
    }

    /**
     * Tests that the warning is correctly tripped when there are not enough
     * metastores.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testPerform() throws Exception {
        AmbariManagementController ambariManagementControllerMock = Mockito.mock(AmbariManagementController.class);
        PowerMockito.mockStatic(AmbariServer.class);
        Mockito.when(AmbariServer.getController()).thenReturn(ambariManagementControllerMock);
        ResourceProvider resourceProviderMock = Mockito.mock(ResourceProvider.class);
        PowerMockito.mockStatic(AbstractControllerResourceProvider.class);
        Mockito.when(AbstractControllerResourceProvider.getResourceProvider(ArgumentMatchers.eq(Request), ArgumentMatchers.any(AmbariManagementController.class))).thenReturn(resourceProviderMock);
        PowerMockito.mockStatic(PropertyHelper.class);
        org.apache.ambari.server.controller.spi.Request requestMock = Mockito.mock(org.apache.ambari.server.controller.spi.Request.class);
        Mockito.when(PropertyHelper.getCreateRequest(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(requestMock);
        Mockito.when(PropertyHelper.getPropertyId("Requests", "id")).thenReturn("requestIdProp");
        RequestStatus requestStatusMock = Mockito.mock(RequestStatus.class);
        Resource responseResourceMock = Mockito.mock(Resource.class);
        Mockito.when(resourceProviderMock.createResources(requestMock)).thenReturn(requestStatusMock);
        Mockito.when(requestStatusMock.getRequestResource()).thenReturn(responseResourceMock);
        Mockito.when(responseResourceMock.getPropertyValue(ArgumentMatchers.anyString())).thenReturn(100L);
        Clusters clustersMock = Mockito.mock(Clusters.class);
        Mockito.when(ambariManagementControllerMock.getClusters()).thenReturn(clustersMock);
        Cluster clusterMock = Mockito.mock(Cluster.class);
        Mockito.when(clustersMock.getCluster("c1")).thenReturn(clusterMock);
        Mockito.when(clusterMock.getHosts(ArgumentMatchers.eq("AMBARI_METRICS"), ArgumentMatchers.eq("METRICS_MONITOR"))).thenReturn(Collections.singleton("h1"));
        RequestDAO requestDAOMock = Mockito.mock(RequestDAO.class);
        RequestEntity requestEntityMock = Mockito.mock(RequestEntity.class);
        Mockito.when(requestDAOMock.findByPks(Collections.singleton(100L), true)).thenReturn(Collections.singletonList(requestEntityMock));
        Mockito.when(requestEntityMock.getStatus()).thenReturn(IN_PROGRESS).thenReturn(COMPLETED);
        Field requestDaoField = m_check.getClass().getDeclaredField("requestDAO");
        requestDaoField.setAccessible(true);
        requestDaoField.set(m_check, requestDAOMock);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(AmbariMetricsHadoopSinkVersionCompatibilityCheck.MIN_HADOOP_SINK_VERSION_PROPERTY_NAME, "2.7.0.0");
        ClusterInformation clusterInformation = new ClusterInformation("c1", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult check = m_check.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
    }

    @Test(timeout = 60000)
    public void testPerformFail() throws Exception {
        AmbariManagementController ambariManagementControllerMock = Mockito.mock(AmbariManagementController.class);
        PowerMockito.mockStatic(AmbariServer.class);
        Mockito.when(AmbariServer.getController()).thenReturn(ambariManagementControllerMock);
        ResourceProvider resourceProviderMock = Mockito.mock(ResourceProvider.class);
        PowerMockito.mockStatic(AbstractControllerResourceProvider.class);
        Mockito.when(AbstractControllerResourceProvider.getResourceProvider(ArgumentMatchers.eq(Request), ArgumentMatchers.any(AmbariManagementController.class))).thenReturn(resourceProviderMock);
        PowerMockito.mockStatic(PropertyHelper.class);
        org.apache.ambari.server.controller.spi.Request requestMock = Mockito.mock(org.apache.ambari.server.controller.spi.Request.class);
        Mockito.when(PropertyHelper.getCreateRequest(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(requestMock);
        Mockito.when(PropertyHelper.getPropertyId("Requests", "id")).thenReturn("requestIdProp");
        RequestStatus requestStatusMock = Mockito.mock(RequestStatus.class);
        Resource responseResourceMock = Mockito.mock(Resource.class);
        Mockito.when(resourceProviderMock.createResources(requestMock)).thenReturn(requestStatusMock);
        Mockito.when(requestStatusMock.getRequestResource()).thenReturn(responseResourceMock);
        Mockito.when(responseResourceMock.getPropertyValue(ArgumentMatchers.anyString())).thenReturn(101L);
        Clusters clustersMock = Mockito.mock(Clusters.class);
        Mockito.when(ambariManagementControllerMock.getClusters()).thenReturn(clustersMock);
        Cluster clusterMock = Mockito.mock(Cluster.class);
        Mockito.when(clustersMock.getCluster("c1")).thenReturn(clusterMock);
        Mockito.when(clusterMock.getHosts(ArgumentMatchers.eq("AMBARI_METRICS"), ArgumentMatchers.eq("METRICS_MONITOR"))).thenReturn(Collections.singleton("h1_fail"));
        RequestDAO requestDAOMock = Mockito.mock(RequestDAO.class);
        RequestEntity requestEntityMock = Mockito.mock(RequestEntity.class);
        Mockito.when(requestDAOMock.findByPks(Collections.singleton(101L), true)).thenReturn(Collections.singletonList(requestEntityMock));
        Mockito.when(requestEntityMock.getStatus()).thenReturn(IN_PROGRESS).thenReturn(FAILED);
        Field requestDaoField = m_check.getClass().getDeclaredField("requestDAO");
        requestDaoField.setAccessible(true);
        requestDaoField.set(m_check, requestDAOMock);
        Mockito.when(requestEntityMock.getRequestId()).thenReturn(101L);
        HostRoleCommandDAO hostRoleCommandDAOMock = Mockito.mock(HostRoleCommandDAO.class);
        HostRoleCommandEntity hrcEntityMock = Mockito.mock(HostRoleCommandEntity.class);
        Mockito.when(hostRoleCommandDAOMock.findByRequest(101L, true)).thenReturn(Collections.singletonList(hrcEntityMock));
        Mockito.when(hrcEntityMock.getStatus()).thenReturn(FAILED);
        Mockito.when(hrcEntityMock.getHostName()).thenReturn("h1_fail");
        Field hrcDaoField = m_check.getClass().getDeclaredField("hostRoleCommandDAO");
        hrcDaoField.setAccessible(true);
        hrcDaoField.set(m_check, hostRoleCommandDAOMock);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(AmbariMetricsHadoopSinkVersionCompatibilityCheck.MIN_HADOOP_SINK_VERSION_PROPERTY_NAME, "2.7.0.0");
        ClusterInformation clusterInformation = new ClusterInformation("c1", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult check = m_check.perform(request);
        Assert.assertEquals(FAIL, check.getStatus());
        Assert.assertTrue(check.getFailReason().contains("upgrade 'ambari-metrics-hadoop-sink'"));
        Assert.assertEquals(check.getFailedOn().size(), 1);
        Assert.assertTrue(check.getFailedOn().iterator().next().contains("h1_fail"));
    }
}

