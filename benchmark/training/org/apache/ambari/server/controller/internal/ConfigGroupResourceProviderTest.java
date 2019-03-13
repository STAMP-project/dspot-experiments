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


import ConfigGroupResourceProvider.CLUSTER_NAME;
import ConfigGroupResourceProvider.DESIRED_CONFIGS;
import ConfigGroupResourceProvider.GROUP_NAME;
import ConfigGroupResourceProvider.HOSTS;
import ConfigGroupResourceProvider.HOST_NAME;
import ConfigGroupResourceProvider.ID;
import ConfigGroupResourceProvider.TAG;
import com.google.inject.Binder;
import com.google.inject.Module;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ConfigGroupRequest;
import org.apache.ambari.server.controller.ConfigGroupResponse;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.configgroup.ConfigGroup;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


public class ConfigGroupResourceProviderTest {
    private HostDAO hostDAO = null;

    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(HostDAO.class).toInstance(hostDAO);
        }
    }

    @Test
    public void testCreateConfigGroupAsAmbariAdministrator() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateConfigGroupAsClusterAdministrator() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testCreateConfigGroupAsClusterOperator() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testCreateConfigGroupAsServiceAdministrator() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateConfigGroupAsServiceOperator() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createServiceOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateConfigGroupAsClusterUser() throws Exception {
        testCreateConfigGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testDuplicateNameConfigGroupAsAmbariAdministrator() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDuplicateNameConfigGroupAsClusterAdministrator() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testDuplicateNameConfigGroupAsClusterOperator() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testDuplicateNameConfigGroupAsServiceAdministrator() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDuplicateNameConfigGroupAsServiceOperator() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createServiceOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDuplicateNameConfigGroupAsClusterUser() throws Exception {
        testDuplicateNameConfigGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testUpdateConfigGroupWithWrongConfigType() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);
        ConfigHelper configHelper = createNiceMock(ConfigHelper.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Host h1 = createNiceMock(Host.class);
        Host h2 = createNiceMock(Host.class);
        HostEntity hostEntity1 = createMock(HostEntity.class);
        HostEntity hostEntity2 = createMock(HostEntity.class);
        final ConfigGroup configGroup = createNiceMock(ConfigGroup.class);
        ConfigGroupResponse configGroupResponse = createNiceMock(ConfigGroupResponse.class);
        expect(cluster.isConfigTypeExists("core-site")).andReturn(false).anyTimes();
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAuthName()).andReturn("admin").anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getHost("h1")).andReturn(h1);
        expect(clusters.getHost("h2")).andReturn(h2);
        expect(hostDAO.findByName("h1")).andReturn(hostEntity1).anyTimes();
        expect(hostDAO.findById(1L)).andReturn(hostEntity1).anyTimes();
        expect(hostDAO.findByName("h2")).andReturn(hostEntity2).anyTimes();
        expect(hostDAO.findById(2L)).andReturn(hostEntity2).anyTimes();
        expect(hostEntity1.getHostId()).andReturn(1L).atLeastOnce();
        expect(hostEntity2.getHostId()).andReturn(2L).atLeastOnce();
        expect(h1.getHostId()).andReturn(1L).anyTimes();
        expect(h2.getHostId()).andReturn(2L).anyTimes();
        expect(configGroup.getName()).andReturn("test-1").anyTimes();
        expect(configGroup.getId()).andReturn(25L).anyTimes();
        expect(configGroup.getTag()).andReturn("tag-1").anyTimes();
        expect(configGroup.convertToResponse()).andReturn(configGroupResponse).anyTimes();
        expect(configGroupResponse.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(configGroupResponse.getId()).andReturn(25L).anyTimes();
        expect(cluster.getConfigGroups()).andStubAnswer(new org.easymock.IAnswer<Map<Long, ConfigGroup>>() {
            @Override
            public Map<Long, ConfigGroup> answer() throws Throwable {
                Map<Long, ConfigGroup> configGroupMap = new HashMap<>();
                configGroupMap.put(configGroup.getId(), configGroup);
                return configGroupMap;
            }
        });
        replay(managementController, clusters, cluster, configGroup, response, configGroupResponse, configHelper, hostDAO, hostEntity1, hostEntity2, h1, h2);
        ResourceProvider provider = getConfigGroupResourceProvider(managementController);
        Map<String, Object> properties = new LinkedHashMap<>();
        Set<Map<String, Object>> hostSet = new HashSet<>();
        Map<String, Object> host1 = new HashMap<>();
        host1.put(HOST_NAME, "h1");
        hostSet.add(host1);
        Map<String, Object> host2 = new HashMap<>();
        host2.put(HOST_NAME, "h2");
        hostSet.add(host2);
        Set<Map<String, Object>> configSet = new HashSet<>();
        Map<String, String> configMap = new HashMap<>();
        Map<String, Object> configs = new HashMap<>();
        configs.put("type", "core-site");
        configs.put("tag", "version100");
        configMap.put("key1", "value1");
        configs.put("properties", configMap);
        configSet.add(configs);
        properties.put(CLUSTER_NAME, "Cluster100");
        properties.put(GROUP_NAME, "test-1");
        properties.put(TAG, "tag-1");
        properties.put(HOSTS, hostSet);
        properties.put(DESIRED_CONFIGS, configSet);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        Request request = PropertyHelper.getUpdateRequest(properties, mapRequestProps);
        Predicate predicate = new PredicateBuilder().property(CLUSTER_NAME).equals("Cluster100").and().property(ID).equals(25L).toPredicate();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        SystemException systemException = null;
        try {
            provider.updateResources(request, predicate);
        } catch (SystemException e) {
            systemException = e;
        }
        Assert.assertNotNull(systemException);
        verify(managementController, clusters, cluster, configGroup, response, configGroupResponse, configHelper, hostDAO, hostEntity1, hostEntity2, h1, h2);
    }

    @Test
    public void testUpdateConfigGroupAsAmbariAdministrator() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateConfigGroupAsClusterAdministrator() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateConfigGroupAsClusterOperator() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testUpdateConfigGroupAsServiceAdministrator() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateConfigGroupAsServiceOperator() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createServiceOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateConfigGroupAsClusterUser() throws Exception {
        testUpdateConfigGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetConfigGroupAsAmbariAdministrator() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetConfigGroupAsClusterAdministrator() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetConfigGroupAsClusterOperator() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetConfigGroupAsServiceAdministrator() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetConfigGroupAsServiceOperator() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetConfigGroupAsClusterUser() throws Exception {
        testGetConfigGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testDeleteConfigGroupAsAmbariAdministrator() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDeleteConfigGroupAsClusterAdministrator() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testDeleteConfigGroupAsClusterOperator() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testDeleteConfigGroupAsServiceAdministrator() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteConfigGroupAsServiceOperator() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createServiceOperator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteConfigGroupAsClusterUser() throws Exception {
        testDeleteConfigGroup(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetConfigGroupRequest_populatesConfigAttributes() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        ConfigGroupResourceProvider resourceProvider = getConfigGroupResourceProvider(managementController);
        Set<Map<String, String>> desiredConfigProperties = new HashSet<>();
        Map<String, String> desiredConfig1 = new HashMap<>();
        desiredConfig1.put("tag", "version2");
        desiredConfig1.put("type", "type1");
        desiredConfig1.put("properties/key1", "value1");
        desiredConfig1.put("properties/key2", "value2");
        desiredConfig1.put("properties_attributes/attr1/key1", "true");
        desiredConfig1.put("properties_attributes/attr1/key2", "false");
        desiredConfig1.put("properties_attributes/attr2/key1", "15");
        desiredConfigProperties.add(desiredConfig1);
        Map<String, Object> properties = new HashMap<>();
        properties.put("ConfigGroup/hosts", new HashMap<String, String>() {
            {
                put("host_name", "ambari1");
            }
        });
        properties.put("ConfigGroup/cluster_name", "c");
        properties.put("ConfigGroup/desired_configs", desiredConfigProperties);
        ConfigGroupRequest request = resourceProvider.getConfigGroupRequest(properties);
        Assert.assertNotNull(request);
        Map<String, Config> configMap = request.getConfigs();
        Assert.assertNotNull(configMap);
        Assert.assertEquals(1, configMap.size());
        Assert.assertTrue(configMap.containsKey("type1"));
        Config config = configMap.get("type1");
        Assert.assertEquals("type1", config.getType());
        Map<String, String> configProperties = config.getProperties();
        Assert.assertNotNull(configProperties);
        Assert.assertEquals(2, configProperties.size());
        Assert.assertEquals("value1", configProperties.get("key1"));
        Assert.assertEquals("value2", configProperties.get("key2"));
        Map<String, Map<String, String>> configAttributes = config.getPropertiesAttributes();
        Assert.assertNotNull(configAttributes);
        Assert.assertEquals(2, configAttributes.size());
        Assert.assertTrue(configAttributes.containsKey("attr1"));
        Map<String, String> attr1 = configAttributes.get("attr1");
        Assert.assertNotNull(attr1);
        Assert.assertEquals(2, attr1.size());
        Assert.assertEquals("true", attr1.get("key1"));
        Assert.assertEquals("false", attr1.get("key2"));
        Assert.assertTrue(configAttributes.containsKey("attr2"));
        Map<String, String> attr2 = configAttributes.get("attr2");
        Assert.assertNotNull(attr2);
        Assert.assertEquals(1, attr2.size());
        Assert.assertEquals("15", attr2.get("key1"));
    }
}

