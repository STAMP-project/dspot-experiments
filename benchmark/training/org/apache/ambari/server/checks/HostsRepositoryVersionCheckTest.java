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


import MaintenanceState.OFF;
import RepositoryVersionState.INSTALLED;
import RepositoryVersionState.NOT_REQUIRED;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.state.CheckHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
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
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for HostsRepositoryVersionCheck
 */
@RunWith(MockitoJUnitRunner.class)
public class HostsRepositoryVersionCheckTest {
    private final Clusters clusters = Mockito.mock(Clusters.class);

    private final HostVersionDAO hostVersionDAO = Mockito.mock(HostVersionDAO.class);

    private final RepositoryVersionDAO repositoryVersionDAO = Mockito.mock(RepositoryVersionDAO.class);

    @Mock
    private ClusterVersionSummary m_clusterVersionSummary;

    @Mock
    private VersionDefinitionXml m_vdfXml;

    @Mock
    private RepositoryVersion m_repositoryVersion;

    @Mock
    private RepositoryVersionEntity m_repositoryVersionEntity;

    private MockCheckHelper m_checkHelper = new MockCheckHelper();

    final Map<String, Service> m_services = new HashMap<>();

    @Test
    public void testPerform() throws Exception {
        final HostsRepositoryVersionCheck hostsRepositoryVersionCheck = new HostsRepositoryVersionCheck();
        hostsRepositoryVersionCheck.clustersProvider = new com.google.inject.Provider<Clusters>() {
            @Override
            public Clusters get() {
                return clusters;
            }
        };
        hostsRepositoryVersionCheck.repositoryVersionDaoProvider = new com.google.inject.Provider<RepositoryVersionDAO>() {
            @Override
            public RepositoryVersionDAO get() {
                return repositoryVersionDAO;
            }
        };
        hostsRepositoryVersionCheck.hostVersionDaoProvider = new com.google.inject.Provider<HostVersionDAO>() {
            @Override
            public HostVersionDAO get() {
                return hostVersionDAO;
            }
        };
        hostsRepositoryVersionCheck.checkHelperProvider = new com.google.inject.Provider<CheckHelper>() {
            @Override
            public CheckHelper get() {
                return m_checkHelper;
            }
        };
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(cluster.getDesiredStackVersion()).thenReturn(new StackId());
        Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);
        final Map<String, Host> hosts = new HashMap<>();
        final Host host1 = Mockito.mock(Host.class);
        final Host host2 = Mockito.mock(Host.class);
        final Host host3 = Mockito.mock(Host.class);
        Mockito.when(host1.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host2.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host3.getMaintenanceState(1L)).thenReturn(OFF);
        hosts.put("host1", host1);
        hosts.put("host2", host2);
        hosts.put("host3", host3);
        Mockito.when(clusters.getHostsForCluster("cluster")).thenReturn(hosts);
        Mockito.when(repositoryVersionDAO.findByStackAndVersion(Mockito.any(StackId.class), Mockito.anyString())).thenReturn(null);
        Mockito.when(repositoryVersionDAO.findByStackAndVersion(Mockito.any(StackEntity.class), Mockito.anyString())).thenReturn(null);
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, null, null);
        UpgradeCheckResult check = hostsRepositoryVersionCheck.perform(request);
        Assert.assertEquals(FAIL, check.getStatus());
        StackEntity stackEntity = new StackEntity();
        stackEntity.setStackName("HDP");
        stackEntity.setStackVersion("2.0.6");
        final RepositoryVersionEntity repositoryVersion = new RepositoryVersionEntity();
        repositoryVersion.setStack(stackEntity);
        Mockito.when(repositoryVersionDAO.findByStackAndVersion(Mockito.any(StackId.class), Mockito.anyString())).thenReturn(repositoryVersion);
        Mockito.when(repositoryVersionDAO.findByStackAndVersion(Mockito.any(StackEntity.class), Mockito.anyString())).thenReturn(repositoryVersion);
        final HostVersionEntity hostVersion = new HostVersionEntity();
        hostVersion.setState(INSTALLED);
        Mockito.when(hostVersionDAO.findByClusterStackVersionAndHost(Mockito.anyString(), Mockito.any(StackId.class), Mockito.anyString(), Mockito.anyString())).thenReturn(hostVersion);
        check = hostsRepositoryVersionCheck.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
    }

    @Test
    public void testPerformWithVersion() throws Exception {
        final HostsRepositoryVersionCheck hostsRepositoryVersionCheck = new HostsRepositoryVersionCheck();
        hostsRepositoryVersionCheck.clustersProvider = new com.google.inject.Provider<Clusters>() {
            @Override
            public Clusters get() {
                return clusters;
            }
        };
        hostsRepositoryVersionCheck.repositoryVersionDaoProvider = new com.google.inject.Provider<RepositoryVersionDAO>() {
            @Override
            public RepositoryVersionDAO get() {
                return repositoryVersionDAO;
            }
        };
        hostsRepositoryVersionCheck.hostVersionDaoProvider = new com.google.inject.Provider<HostVersionDAO>() {
            @Override
            public HostVersionDAO get() {
                return hostVersionDAO;
            }
        };
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(cluster.getDesiredStackVersion()).thenReturn(new StackId());
        Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);
        final Map<String, Host> hosts = new HashMap<>();
        final Host host1 = Mockito.mock(Host.class);
        final Host host2 = Mockito.mock(Host.class);
        final Host host3 = Mockito.mock(Host.class);
        Mockito.when(host1.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host2.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host3.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host1.getHostName()).thenReturn("host1");
        Mockito.when(host2.getHostName()).thenReturn("host2");
        Mockito.when(host3.getHostName()).thenReturn("host3");
        hosts.put("host1", host1);
        hosts.put("host2", host2);
        hosts.put("host3", host3);
        Mockito.when(clusters.getHostsForCluster("cluster")).thenReturn(hosts);
        HostVersionEntity hve = new HostVersionEntity();
        hve.setRepositoryVersion(m_repositoryVersionEntity);
        hve.setState(INSTALLED);
        for (String hostName : hosts.keySet()) {
            Mockito.when(hostVersionDAO.findByClusterStackVersionAndHost("cluster", m_repositoryVersionEntity.getStackId(), m_repositoryVersion.getVersion(), hostName)).thenReturn(hve);
        }
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, null, null);
        UpgradeCheckResult check = hostsRepositoryVersionCheck.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
    }

    @Test
    public void testPerformWithVersionNotRequired() throws Exception {
        final HostsRepositoryVersionCheck hostsRepositoryVersionCheck = new HostsRepositoryVersionCheck();
        hostsRepositoryVersionCheck.clustersProvider = new com.google.inject.Provider<Clusters>() {
            @Override
            public Clusters get() {
                return clusters;
            }
        };
        hostsRepositoryVersionCheck.repositoryVersionDaoProvider = new com.google.inject.Provider<RepositoryVersionDAO>() {
            @Override
            public RepositoryVersionDAO get() {
                return repositoryVersionDAO;
            }
        };
        hostsRepositoryVersionCheck.hostVersionDaoProvider = new com.google.inject.Provider<HostVersionDAO>() {
            @Override
            public HostVersionDAO get() {
                return hostVersionDAO;
            }
        };
        final Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.getClusterId()).thenReturn(1L);
        Mockito.when(cluster.getDesiredStackVersion()).thenReturn(new StackId());
        Mockito.when(clusters.getCluster("cluster")).thenReturn(cluster);
        final Map<String, Host> hosts = new HashMap<>();
        final Host host1 = Mockito.mock(Host.class);
        final Host host2 = Mockito.mock(Host.class);
        final Host host3 = Mockito.mock(Host.class);
        Mockito.when(host1.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host2.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host3.getMaintenanceState(1L)).thenReturn(OFF);
        Mockito.when(host1.getHostName()).thenReturn("host1");
        Mockito.when(host2.getHostName()).thenReturn("host2");
        Mockito.when(host3.getHostName()).thenReturn("host3");
        hosts.put("host1", host1);
        hosts.put("host2", host2);
        hosts.put("host3", host3);
        Mockito.when(clusters.getHostsForCluster("cluster")).thenReturn(hosts);
        HostVersionEntity hve = new HostVersionEntity();
        hve.setRepositoryVersion(m_repositoryVersionEntity);
        hve.setState(NOT_REQUIRED);
        for (String hostName : hosts.keySet()) {
            Mockito.when(hostVersionDAO.findByClusterStackVersionAndHost("cluster", m_repositoryVersionEntity.getStackId(), m_repositoryVersion.getVersion(), hostName)).thenReturn(hve);
        }
        ClusterInformation clusterInformation = new ClusterInformation("cluster", false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, null, null);
        UpgradeCheckResult check = hostsRepositoryVersionCheck.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
    }
}

