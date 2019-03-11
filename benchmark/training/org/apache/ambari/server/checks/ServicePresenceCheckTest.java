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


import ServicePresenceCheck.NEW_SERVICES_PROPERTY_NAME;
import ServicePresenceCheck.NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME;
import ServicePresenceCheck.REMOVED_SERVICES_PROPERTY_NAME;
import ServicePresenceCheck.REPLACED_SERVICES_PROPERTY_NAME;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.RepositoryVersion;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link ServicePresenceCheck}
 */
public class ServicePresenceCheckTest {
    private final Clusters m_clusters = Mockito.mock(Clusters.class);

    private final ServicePresenceCheck m_check = new ServicePresenceCheck();

    final RepositoryVersion m_repositoryVersion = Mockito.mock(RepositoryVersion.class);

    @Test
    public void testPerformPass() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME, "MyServiceOne, MyServiceTwo");
        checkProperties.put(REMOVED_SERVICES_PROPERTY_NAME, "RemovedServiceOne, RemovedServiceTwo");
        checkProperties.put(REPLACED_SERVICES_PROPERTY_NAME, "OldServiceOne, OldServiceTwo");
        checkProperties.put(NEW_SERVICES_PROPERTY_NAME, "NewServiceOne, NewServiceTwo");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testPerformHasNoUpgradeSupportServices() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("ATLAS", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME, "Atlas, MyService");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testPerformHasReplacedServices() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("ATLAS", Mockito.mock(Service.class));
        services.put("OLDSERVICE", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(REPLACED_SERVICES_PROPERTY_NAME, "Atlas, OldService");
        checkProperties.put(NEW_SERVICES_PROPERTY_NAME, "Atlas2, NewService");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testPerformHasRemovedServices() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("ATLAS", Mockito.mock(Service.class));
        services.put("OLDSERVICE", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(REMOVED_SERVICES_PROPERTY_NAME, "OldService");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testPerformMixOne() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("ATLAS", Mockito.mock(Service.class));
        services.put("REMOVEDSERVICE", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME, "MyServiceOne, MyServiceTwo");
        checkProperties.put(REPLACED_SERVICES_PROPERTY_NAME, "Atlas, OldService");
        checkProperties.put(NEW_SERVICES_PROPERTY_NAME, "Atlas2, NewService");
        checkProperties.put(REMOVED_SERVICES_PROPERTY_NAME, "RemovedService");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testPerformMixTwo() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("OLDSERVICE", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME, "Atlas, MyService");
        checkProperties.put(REPLACED_SERVICES_PROPERTY_NAME, "OldService");
        checkProperties.put(NEW_SERVICES_PROPERTY_NAME, "NewService");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testPerformMixThree() throws Exception {
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(m_clusters.getCluster("cluster")).thenReturn(cluster);
        Map<String, Service> services = new HashMap<>();
        services.put("ATLAS", Mockito.mock(Service.class));
        services.put("HDFS", Mockito.mock(Service.class));
        services.put("STORM", Mockito.mock(Service.class));
        services.put("RANGER", Mockito.mock(Service.class));
        Mockito.when(cluster.getServices()).thenReturn(services);
        Map<String, String> checkProperties = new HashMap<>();
        checkProperties.put(NO_UPGRADE_SUPPORT_SERVICES_PROPERTY_NAME, "Atlas, HDFS");
        checkProperties.put(REPLACED_SERVICES_PROPERTY_NAME, "Storm, Ranger");
        checkProperties.put(NEW_SERVICES_PROPERTY_NAME, "Storm2, Ranger2");
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, checkProperties, null);
        UpgradeCheckResult result = m_check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }
}

