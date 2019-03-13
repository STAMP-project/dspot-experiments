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
package org.apache.ambari.server.state.cluster;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.HostNotFoundException;
import org.apache.ambari.server.controller.AmbariSessionManager;
import org.apache.ambari.server.controller.internal.DeleteHostComponentStatusMetaData;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.configgroup.ConfigGroup;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;


public class ClusterImplTest {
    private static Injector injector;

    private static Clusters clusters;

    private static OrmTestHelper ormTestHelper;

    @Test
    public void testAddSessionAttributes() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        AmbariSessionManager sessionManager = createMock(AmbariSessionManager.class);
        ClusterImpl cluster = createMockBuilder(ClusterImpl.class).addMockedMethod("getSessionManager").addMockedMethod("getClusterName").addMockedMethod("getSessionAttributes").createMock();
        expect(cluster.getSessionManager()).andReturn(sessionManager);
        expect(cluster.getClusterName()).andReturn("c1");
        expect(cluster.getSessionAttributes()).andReturn(attributes);
        sessionManager.setAttribute("cluster_session_attributes:c1", attributes);
        replay(sessionManager, cluster);
        cluster.addSessionAttributes(attributes);
        verify(sessionManager, cluster);
    }

    @Test
    public void testSetSessionAttribute() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        attributes.put("foo2", "bar2");
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);
        updatedAttributes.put("foo2", "updated value");
        Map<String, Object> addedAttributes = new HashMap<>(updatedAttributes);
        updatedAttributes.put("foo3", "added value");
        AmbariSessionManager sessionManager = createMock(AmbariSessionManager.class);
        ClusterImpl cluster = createMockBuilder(ClusterImpl.class).addMockedMethod("getSessionManager").addMockedMethod("getClusterName").addMockedMethod("getSessionAttributes").createMock();
        expect(cluster.getSessionManager()).andReturn(sessionManager);
        expect(cluster.getClusterName()).andReturn("c1");
        expect(cluster.getSessionAttributes()).andReturn(attributes);
        sessionManager.setAttribute("cluster_session_attributes:c1", updatedAttributes);
        expectLastCall().once();
        expect(cluster.getSessionManager()).andReturn(sessionManager);
        expect(cluster.getClusterName()).andReturn("c1");
        expect(cluster.getSessionAttributes()).andReturn(updatedAttributes);
        sessionManager.setAttribute("cluster_session_attributes:c1", addedAttributes);
        expectLastCall().once();
        replay(sessionManager, cluster);
        cluster.setSessionAttribute("foo2", "updated value");
        cluster.setSessionAttribute("foo3", "added value");
        verify(sessionManager, cluster);
    }

    @Test
    public void testRemoveSessionAttribute() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        attributes.put("foo2", "bar2");
        Map<String, Object> trimmedAttributes = new HashMap<>(attributes);
        trimmedAttributes.remove("foo2");
        AmbariSessionManager sessionManager = createMock(AmbariSessionManager.class);
        ClusterImpl cluster = createMockBuilder(ClusterImpl.class).addMockedMethod("getSessionManager").addMockedMethod("getClusterName").addMockedMethod("getSessionAttributes").createMock();
        expect(cluster.getSessionManager()).andReturn(sessionManager);
        expect(cluster.getClusterName()).andReturn("c1");
        expect(cluster.getSessionAttributes()).andReturn(attributes);
        sessionManager.setAttribute("cluster_session_attributes:c1", trimmedAttributes);
        expectLastCall().once();
        replay(sessionManager, cluster);
        cluster.removeSessionAttribute("foo2");
        verify(sessionManager, cluster);
    }

    @Test
    public void testGetSessionAttributes() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        AmbariSessionManager sessionManager = createMock(AmbariSessionManager.class);
        ClusterImpl cluster = createMockBuilder(ClusterImpl.class).addMockedMethod("getSessionManager").addMockedMethod("getClusterName").createMock();
        expect(cluster.getSessionManager()).andReturn(sessionManager).anyTimes();
        expect(cluster.getClusterName()).andReturn("c1").anyTimes();
        expect(sessionManager.getAttribute("cluster_session_attributes:c1")).andReturn(attributes);
        expect(sessionManager.getAttribute("cluster_session_attributes:c1")).andReturn(null);
        replay(sessionManager, cluster);
        Assert.assertEquals(attributes, cluster.getSessionAttributes());
        Assert.assertEquals(Collections.<String, Object>emptyMap(), cluster.getSessionAttributes());
        verify(sessionManager, cluster);
    }

    @Test
    public void testDeleteService() throws Exception {
        // Given
        String serviceToDelete = "TEZ";
        String clusterName = "TEST_CLUSTER";
        String hostName1 = "HOST1";
        String hostName2 = "HOST2";
        String stackVersion = "HDP-2.1.1";
        String repoVersion = "2.1.1-1234";
        StackId stackId = new StackId(stackVersion);
        ClusterImplTest.ormTestHelper.createStack(stackId);
        ClusterImplTest.clusters.addCluster(clusterName, stackId);
        Cluster cluster = ClusterImplTest.clusters.getCluster(clusterName);
        RepositoryVersionEntity repositoryVersion = ClusterImplTest.ormTestHelper.getOrCreateRepositoryVersion(new StackId(stackVersion), repoVersion);
        ClusterImplTest.clusters.addHost(hostName1);
        ClusterImplTest.clusters.addHost(hostName2);
        Host host1 = ClusterImplTest.clusters.getHost(hostName1);
        host1.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        Host host2 = ClusterImplTest.clusters.getHost(hostName2);
        host2.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        ClusterImplTest.clusters.mapAndPublishHostsToCluster(Sets.newHashSet(hostName1, hostName2), clusterName);
        Service hdfs = cluster.addService("HDFS", repositoryVersion);
        ServiceComponent nameNode = hdfs.addServiceComponent("NAMENODE");
        nameNode.addServiceComponentHost(hostName1);
        ServiceComponent dataNode = hdfs.addServiceComponent("DATANODE");
        dataNode.addServiceComponentHost(hostName1);
        dataNode.addServiceComponentHost(hostName2);
        ServiceComponent hdfsClient = hdfs.addServiceComponent("HDFS_CLIENT");
        hdfsClient.addServiceComponentHost(hostName1);
        hdfsClient.addServiceComponentHost(hostName2);
        Service tez = cluster.addService(serviceToDelete, repositoryVersion);
        ServiceComponent tezClient = tez.addServiceComponent("TEZ_CLIENT");
        ServiceComponentHost tezClientHost1 = tezClient.addServiceComponentHost(hostName1);
        ServiceComponentHost tezClientHost2 = tezClient.addServiceComponentHost(hostName2);
        // When
        cluster.deleteService(serviceToDelete, new DeleteHostComponentStatusMetaData());
        // Then
        Assert.assertFalse("Deleted service should be removed from the service collection !", cluster.getServices().containsKey(serviceToDelete));
        Assert.assertEquals("All components of the deleted service should be removed from all hosts", 0, cluster.getServiceComponentHosts(serviceToDelete, null).size());
        boolean checkHost1 = !(cluster.getServiceComponentHosts(hostName1).contains(tezClientHost1));
        boolean checkHost2 = !(cluster.getServiceComponentHosts(hostName2).contains(tezClientHost2));
        Assert.assertTrue("All components of the deleted service should be removed from all hosts", (checkHost1 && checkHost2));
    }

    @Test
    public void testDeleteHost() throws Exception {
        // Given
        String clusterName = "TEST_DELETE_HOST";
        String hostName1 = "HOSTNAME1";
        String hostName2 = "HOSTNAME2";
        String hostToDelete = hostName2;
        StackId stackId = new StackId("HDP-2.1.1");
        ClusterImplTest.ormTestHelper.createStack(stackId);
        ClusterImplTest.clusters.addCluster(clusterName, stackId);
        Cluster cluster = ClusterImplTest.clusters.getCluster(clusterName);
        ClusterImplTest.clusters.addHost(hostName1);
        ClusterImplTest.clusters.addHost(hostName2);
        Host host1 = ClusterImplTest.clusters.getHost(hostName1);
        host1.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        Host host2 = ClusterImplTest.clusters.getHost(hostName2);
        host2.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        ClusterImplTest.clusters.mapAndPublishHostsToCluster(Sets.newHashSet(hostName1, hostName2), clusterName);
        // When
        ClusterImplTest.clusters.deleteHost(hostToDelete);
        // Then
        Assert.assertTrue(ClusterImplTest.clusters.getClustersForHost(hostToDelete).isEmpty());
        Assert.assertFalse(ClusterImplTest.clusters.getHostsForCluster(clusterName).containsKey(hostToDelete));
        Assert.assertFalse(cluster.getHosts().contains(hostToDelete));
        try {
            ClusterImplTest.clusters.getHost(hostToDelete);
            Assert.fail("getHost(hostName) should throw Exception when invoked for deleted host !");
        } catch (HostNotFoundException e) {
        }
    }

    @Test
    public void testGetClusterSize() throws Exception {
        // Given
        String clusterName = "TEST_CLUSTER_SIZE";
        String hostName1 = "host1";
        String hostName2 = "host2";
        StackId stackId = new StackId("HDP", "2.1.1");
        ClusterImplTest.ormTestHelper.createStack(stackId);
        ClusterImplTest.clusters.addCluster(clusterName, stackId);
        Cluster cluster = ClusterImplTest.clusters.getCluster(clusterName);
        ClusterImplTest.clusters.addHost(hostName1);
        ClusterImplTest.clusters.addHost(hostName2);
        Host host1 = ClusterImplTest.clusters.getHost(hostName1);
        host1.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        Host host2 = ClusterImplTest.clusters.getHost(hostName2);
        host2.setHostAttributes(ImmutableMap.of("os_family", "centos", "os_release_version", "6.0"));
        ClusterImplTest.clusters.mapAndPublishHostsToCluster(Sets.newHashSet(hostName1, hostName2), clusterName);
        // When
        int clusterSize = cluster.getClusterSize();
        // Then
        Assert.assertEquals(2, clusterSize);
    }

    @Test
    public void testGetConfigGroupsByServiceName() throws AmbariException {
        // Given
        String clusterName = "TEST_CONFIG_GROUPS";
        String hostName1 = "HOSTNAME1";
        String hostName2 = "HOSTNAME2";
        String hostToDelete = hostName2;
        StackId stackId = new StackId("HDP-2.1.1");
        String serviceToCheckName = "serviceName1";
        String serviceNotToCheckName = "serviceName2";
        ClusterImplTest.ormTestHelper.createStack(stackId);
        ClusterImplTest.clusters.addCluster(clusterName, stackId);
        Cluster cluster = ClusterImplTest.clusters.getCluster(clusterName);
        ConfigGroup serviceConfigGroup1 = createNiceMock(ConfigGroup.class);
        ConfigGroup serviceConfigGroup2 = createNiceMock(ConfigGroup.class);
        expect(serviceConfigGroup1.getId()).andReturn(1L).anyTimes();
        expect(serviceConfigGroup2.getId()).andReturn(2L).anyTimes();
        expect(serviceConfigGroup1.getServiceName()).andReturn(serviceToCheckName).anyTimes();
        expect(serviceConfigGroup2.getServiceName()).andReturn(serviceNotToCheckName).anyTimes();
        replay(serviceConfigGroup1, serviceConfigGroup2);
        cluster.addConfigGroup(serviceConfigGroup1);
        cluster.addConfigGroup(serviceConfigGroup2);
        Map<Long, ConfigGroup> configGroupsToCheck = cluster.getConfigGroupsByServiceName(serviceToCheckName);
        Assert.assertFalse(MapUtils.isEmpty(configGroupsToCheck));
        Assert.assertEquals(1L, configGroupsToCheck.size());
        Assert.assertTrue(configGroupsToCheck.keySet().contains(1L));
        Assert.assertEquals(serviceToCheckName, configGroupsToCheck.get(1L).getServiceName());
        verify(serviceConfigGroup1, serviceConfigGroup2);
    }
}

