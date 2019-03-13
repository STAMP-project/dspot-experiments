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


import ProvisionClusterRequest.QUICKLINKS_PROFILE_FILTERS_PROPERTY;
import ProvisionClusterRequest.QUICKLINKS_PROFILE_SERVICES_PROPERTY;
import TopologyRequest.Type.PROVISION;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.security.encryption.CredentialStoreType;
import org.apache.ambari.server.state.quicklinksprofile.QuickLinksProfileBuilderTest;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.BlueprintFactory;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.HostGroupInfo;
import org.apache.ambari.server.topology.InvalidTopologyTemplateException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for ProvisionClusterRequest.
 */
@SuppressWarnings("unchecked")
public class ProvisionClusterRequestTest {
    private static final String CLUSTER_NAME = "cluster_name";

    private static final String BLUEPRINT_NAME = "blueprint_name";

    private static final BlueprintFactory blueprintFactory = createStrictMock(BlueprintFactory.class);

    private static final Blueprint blueprint = createNiceMock(Blueprint.class);

    private static final ResourceProvider hostResourceProvider = createMock(ResourceProvider.class);

    private static final Configuration blueprintConfig = new Configuration(Collections.emptyMap(), Collections.emptyMap());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testHostNameSpecified() throws Exception {
        // reset host resource provider expectations to none since we are not specifying a host predicate
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestPropertiesNameOnly(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ProvisionClusterRequest provisionClusterRequest = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals(ProvisionClusterRequestTest.CLUSTER_NAME, provisionClusterRequest.getClusterName());
        Assert.assertEquals(PROVISION, provisionClusterRequest.getType());
        Assert.assertEquals(String.format("Provision Cluster '%s'", ProvisionClusterRequestTest.CLUSTER_NAME), provisionClusterRequest.getDescription());
        Assert.assertSame(ProvisionClusterRequestTest.blueprint, provisionClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = provisionClusterRequest.getHostGroupInfo();
        Assert.assertEquals(1, hostGroupInfo.size());
        // group1
        // host info
        HostGroupInfo group1Info = hostGroupInfo.get("group1");
        Assert.assertEquals("group1", group1Info.getHostGroupName());
        Assert.assertEquals(1, group1Info.getHostNames().size());
        Assert.assertTrue(group1Info.getHostNames().contains("host1.mydomain.com"));
        Assert.assertEquals(1, group1Info.getRequestedHostCount());
        Assert.assertNull(group1Info.getPredicate());
        // configuration
        Configuration group1Configuration = group1Info.getConfiguration();
        Assert.assertNull(group1Configuration.getParentConfiguration());
        Assert.assertEquals(1, group1Configuration.getProperties().size());
        Map<String, String> group1TypeProperties = group1Configuration.getProperties().get("foo-type");
        Assert.assertEquals(2, group1TypeProperties.size());
        Assert.assertEquals("prop1Value", group1TypeProperties.get("hostGroup1Prop1"));
        Assert.assertEquals("prop2Value", group1TypeProperties.get("hostGroup1Prop2"));
        Assert.assertTrue(group1Configuration.getAttributes().isEmpty());
        // cluster scoped configuration
        Configuration clusterScopeConfiguration = provisionClusterRequest.getConfiguration();
        Assert.assertSame(ProvisionClusterRequestTest.blueprintConfig, clusterScopeConfiguration.getParentConfiguration());
        Assert.assertEquals(1, clusterScopeConfiguration.getProperties().size());
        Map<String, String> clusterScopedProperties = clusterScopeConfiguration.getProperties().get("someType");
        Assert.assertEquals(1, clusterScopedProperties.size());
        Assert.assertEquals("someValue", clusterScopedProperties.get("property1"));
        // attributes
        Map<String, Map<String, Map<String, String>>> clusterScopedAttributes = clusterScopeConfiguration.getAttributes();
        Assert.assertEquals(1, clusterScopedAttributes.size());
        Map<String, Map<String, String>> clusterScopedTypeAttributes = clusterScopedAttributes.get("someType");
        Assert.assertEquals(1, clusterScopedTypeAttributes.size());
        Map<String, String> clusterScopedTypePropertyAttributes = clusterScopedTypeAttributes.get("attribute1");
        Assert.assertEquals(1, clusterScopedTypePropertyAttributes.size());
        Assert.assertEquals("someAttributePropValue", clusterScopedTypePropertyAttributes.get("property1"));
    }

    @Test
    public void testHostCountSpecified() throws Exception {
        // reset host resource provider expectations to none since we are not specifying a host predicate
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestPropertiesCountOnly(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ProvisionClusterRequest provisionClusterRequest = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals(ProvisionClusterRequestTest.CLUSTER_NAME, provisionClusterRequest.getClusterName());
        Assert.assertEquals(PROVISION, provisionClusterRequest.getType());
        Assert.assertEquals(String.format("Provision Cluster '%s'", ProvisionClusterRequestTest.CLUSTER_NAME), provisionClusterRequest.getDescription());
        Assert.assertSame(ProvisionClusterRequestTest.blueprint, provisionClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = provisionClusterRequest.getHostGroupInfo();
        Assert.assertEquals(1, hostGroupInfo.size());
        // group2
        HostGroupInfo group2Info = hostGroupInfo.get("group2");
        Assert.assertEquals("group2", group2Info.getHostGroupName());
        Assert.assertTrue(group2Info.getHostNames().isEmpty());
        Assert.assertEquals(5, group2Info.getRequestedHostCount());
        Assert.assertNull(group2Info.getPredicate());
        // configuration
        Configuration group2Configuration = group2Info.getConfiguration();
        Assert.assertNull(group2Configuration.getParentConfiguration());
        Assert.assertEquals(1, group2Configuration.getProperties().size());
        Map<String, String> group2TypeProperties = group2Configuration.getProperties().get("foo-type");
        Assert.assertEquals(1, group2TypeProperties.size());
        Assert.assertEquals("prop1Value", group2TypeProperties.get("hostGroup2Prop1"));
        // attributes
        Map<String, Map<String, Map<String, String>>> group2Attributes = group2Configuration.getAttributes();
        Assert.assertEquals(1, group2Attributes.size());
        Map<String, Map<String, String>> group2Type1Attributes = group2Attributes.get("foo-type");
        Assert.assertEquals(1, group2Type1Attributes.size());
        Map<String, String> group2Type1Prop1Attributes = group2Type1Attributes.get("attribute1");
        Assert.assertEquals(1, group2Type1Prop1Attributes.size());
        Assert.assertEquals("attribute1Prop10-value", group2Type1Prop1Attributes.get("hostGroup2Prop10"));
        // cluster scoped configuration
        Configuration clusterScopeConfiguration = provisionClusterRequest.getConfiguration();
        Assert.assertSame(ProvisionClusterRequestTest.blueprintConfig, clusterScopeConfiguration.getParentConfiguration());
        Assert.assertEquals(1, clusterScopeConfiguration.getProperties().size());
        Map<String, String> clusterScopedProperties = clusterScopeConfiguration.getProperties().get("someType");
        Assert.assertEquals(1, clusterScopedProperties.size());
        Assert.assertEquals("someValue", clusterScopedProperties.get("property1"));
        // attributes
        Map<String, Map<String, Map<String, String>>> clusterScopedAttributes = clusterScopeConfiguration.getAttributes();
        Assert.assertEquals(1, clusterScopedAttributes.size());
        Map<String, Map<String, String>> clusterScopedTypeAttributes = clusterScopedAttributes.get("someType");
        Assert.assertEquals(1, clusterScopedTypeAttributes.size());
        Map<String, String> clusterScopedTypePropertyAttributes = clusterScopedTypeAttributes.get("attribute1");
        Assert.assertEquals(1, clusterScopedTypePropertyAttributes.size());
        Assert.assertEquals("someAttributePropValue", clusterScopedTypePropertyAttributes.get("property1"));
    }

    @Test
    public void testMultipleGroups() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ProvisionClusterRequest provisionClusterRequest = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals(ProvisionClusterRequestTest.CLUSTER_NAME, provisionClusterRequest.getClusterName());
        Assert.assertEquals(PROVISION, provisionClusterRequest.getType());
        Assert.assertEquals(String.format("Provision Cluster '%s'", ProvisionClusterRequestTest.CLUSTER_NAME), provisionClusterRequest.getDescription());
        Assert.assertSame(ProvisionClusterRequestTest.blueprint, provisionClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = provisionClusterRequest.getHostGroupInfo();
        Assert.assertEquals(2, hostGroupInfo.size());
        // group1
        // host info
        HostGroupInfo group1Info = hostGroupInfo.get("group1");
        Assert.assertEquals("group1", group1Info.getHostGroupName());
        Assert.assertEquals(1, group1Info.getHostNames().size());
        Assert.assertTrue(group1Info.getHostNames().contains("host1.mydomain.com"));
        Assert.assertEquals(1, group1Info.getRequestedHostCount());
        Assert.assertNull(group1Info.getPredicate());
        // configuration
        Configuration group1Configuration = group1Info.getConfiguration();
        Assert.assertNull(group1Configuration.getParentConfiguration());
        Assert.assertEquals(1, group1Configuration.getProperties().size());
        Map<String, String> group1TypeProperties = group1Configuration.getProperties().get("foo-type");
        Assert.assertEquals(2, group1TypeProperties.size());
        Assert.assertEquals("prop1Value", group1TypeProperties.get("hostGroup1Prop1"));
        Assert.assertEquals("prop2Value", group1TypeProperties.get("hostGroup1Prop2"));
        Assert.assertTrue(group1Configuration.getAttributes().isEmpty());
        // group2
        HostGroupInfo group2Info = hostGroupInfo.get("group2");
        Assert.assertEquals("group2", group2Info.getHostGroupName());
        Assert.assertTrue(group2Info.getHostNames().isEmpty());
        Assert.assertEquals(5, group2Info.getRequestedHostCount());
        Assert.assertNotNull(group2Info.getPredicate());
        // configuration
        Configuration group2Configuration = group2Info.getConfiguration();
        Assert.assertNull(group2Configuration.getParentConfiguration());
        Assert.assertEquals(1, group2Configuration.getProperties().size());
        Map<String, String> group2TypeProperties = group2Configuration.getProperties().get("foo-type");
        Assert.assertEquals(1, group2TypeProperties.size());
        Assert.assertEquals("prop1Value", group2TypeProperties.get("hostGroup2Prop1"));
        // attributes
        Map<String, Map<String, Map<String, String>>> group2Attributes = group2Configuration.getAttributes();
        Assert.assertEquals(1, group2Attributes.size());
        Map<String, Map<String, String>> group2Type1Attributes = group2Attributes.get("foo-type");
        Assert.assertEquals(1, group2Type1Attributes.size());
        Map<String, String> group2Type1Prop1Attributes = group2Type1Attributes.get("attribute1");
        Assert.assertEquals(1, group2Type1Prop1Attributes.size());
        Assert.assertEquals("attribute1Prop10-value", group2Type1Prop1Attributes.get("hostGroup2Prop10"));
        // cluster scoped configuration
        Configuration clusterScopeConfiguration = provisionClusterRequest.getConfiguration();
        Assert.assertSame(ProvisionClusterRequestTest.blueprintConfig, clusterScopeConfiguration.getParentConfiguration());
        Assert.assertEquals(1, clusterScopeConfiguration.getProperties().size());
        Map<String, String> clusterScopedProperties = clusterScopeConfiguration.getProperties().get("someType");
        Assert.assertEquals(1, clusterScopedProperties.size());
        Assert.assertEquals("someValue", clusterScopedProperties.get("property1"));
        // attributes
        Map<String, Map<String, Map<String, String>>> clusterScopedAttributes = clusterScopeConfiguration.getAttributes();
        Assert.assertEquals(1, clusterScopedAttributes.size());
        Map<String, Map<String, String>> clusterScopedTypeAttributes = clusterScopedAttributes.get("someType");
        Assert.assertEquals(1, clusterScopedTypeAttributes.size());
        Map<String, String> clusterScopedTypePropertyAttributes = clusterScopedTypeAttributes.get("attribute1");
        Assert.assertEquals(1, clusterScopedTypePropertyAttributes.size());
        Assert.assertEquals("someAttributePropValue", clusterScopedTypePropertyAttributes.get("property1"));
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_NoHostGroupInfo() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ((Collection) (properties.get("host_groups"))).clear();
        // reset default host resource provider expectations to none
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        // should result in an exception
        new ProvisionClusterRequest(properties, null);
    }

    @Test
    public void test_Creditentials() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        HashMap<String, String> credentialHashMap = new HashMap<>();
        credentialHashMap.put("alias", "testAlias");
        credentialHashMap.put("principal", "testPrincipal");
        credentialHashMap.put("key", "testKey");
        credentialHashMap.put("type", "temporary");
        Set<Map<String, String>> credentialsSet = new HashSet<>();
        credentialsSet.add(credentialHashMap);
        properties.put("credentials", credentialsSet);
        ProvisionClusterRequest provisionClusterRequest = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals(provisionClusterRequest.getCredentialsMap().get("testAlias").getAlias(), "testAlias");
        Assert.assertEquals(provisionClusterRequest.getCredentialsMap().get("testAlias").getPrincipal(), "testPrincipal");
        Assert.assertEquals(provisionClusterRequest.getCredentialsMap().get("testAlias").getKey(), "testKey");
        Assert.assertEquals(provisionClusterRequest.getCredentialsMap().get("testAlias").getType().name(), "TEMPORARY");
    }

    @Test
    public void test_CreditentialsInvalidType() throws Exception {
        expectedException.expect(InvalidTopologyTemplateException.class);
        expectedException.expectMessage(("credential.type [TESTTYPE] is invalid. acceptable values: " + (Arrays.toString(CredentialStoreType.values()))));
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        HashMap<String, String> credentialHashMap = new HashMap<>();
        credentialHashMap.put("alias", "testAlias");
        credentialHashMap.put("principal", "testPrincipal");
        credentialHashMap.put("key", "testKey");
        credentialHashMap.put("type", "testType");
        Set<Map<String, String>> credentialsSet = new HashSet<>();
        credentialsSet.add(credentialHashMap);
        properties.put("credentials", credentialsSet);
        ProvisionClusterRequest provisionClusterRequest = new ProvisionClusterRequest(properties, null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_GroupInfoMissingName() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ((Collection<Map<String, Object>>) (properties.get("host_groups"))).iterator().next().remove("name");
        // reset default host resource provider expectations to none
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        // should result in an exception
        new ProvisionClusterRequest(properties, null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_NoHostsInfo() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ((Collection<Map<String, Object>>) (properties.get("host_groups"))).iterator().next().remove("hosts");
        // reset default host resource provider expectations to none
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        // should result in an exception
        new ProvisionClusterRequest(properties, null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_NoHostNameOrHostCount() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        // remove fqdn property for a group that contains fqdn not host_count
        for (Map<String, Object> groupProps : ((Collection<Map<String, Object>>) (properties.get("host_groups")))) {
            Collection<Map<String, Object>> hostInfo = ((Collection<Map<String, Object>>) (groupProps.get("hosts")));
            Map<String, Object> next = hostInfo.iterator().next();
            if (next.containsKey("fqdn")) {
                next.remove("fqdn");
                break;
            }
        }
        // reset default host resource provider expectations to none
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        // should result in an exception
        new ProvisionClusterRequest(properties, null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testInvalidPredicateProperty() throws Exception {
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        // checkPropertyIds() returns invalid property names
        expect(ProvisionClusterRequestTest.hostResourceProvider.checkPropertyIds(Collections.singleton("Hosts/host_name"))).andReturn(Collections.singleton("Hosts/host_name"));
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        // should result in an exception due to invalid property in host predicate
        new ProvisionClusterRequest(ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME), null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testHostNameAndCountSpecified() throws Exception {
        // reset host resource provider expectations to none since we are not specifying a host predicate
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestPropertiesNameOnly(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ((Map) (((List) (properties.get("host_groups"))).iterator().next())).put("host_count", "5");
        // should result in an exception due to both host name and host count being specified
        new ProvisionClusterRequest(properties, null);
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testHostNameAndPredicateSpecified() throws Exception {
        // reset host resource provider expectations to none since we are not specifying a host predicate
        reset(ProvisionClusterRequestTest.hostResourceProvider);
        replay(ProvisionClusterRequestTest.hostResourceProvider);
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestPropertiesNameOnly(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ((Map) (((List) (properties.get("host_groups"))).iterator().next())).put("host_predicate", "Hosts/host_name=myTestHost");
        // should result in an exception due to both host name and host count being specified
        new ProvisionClusterRequest(properties, null);
    }

    @Test
    public void testQuickLinksProfile_NoDataInRequest() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        ProvisionClusterRequest request = new ProvisionClusterRequest(properties, null);
        Assert.assertNull("No quick links profile is expected", request.getQuickLinksProfileJson());
    }

    @Test
    public void testQuickLinksProfile_OnlyGlobalFilterDataInRequest() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        properties.put(QUICKLINKS_PROFILE_FILTERS_PROPERTY, Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, null, true)));
        ProvisionClusterRequest request = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals("Quick links profile doesn't match expected", "{\"filters\":[{\"visible\":true}]}", request.getQuickLinksProfileJson());
    }

    @Test
    public void testQuickLinksProfile_OnlyServiceFilterDataInRequest() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        Map<String, String> filter = QuickLinksProfileBuilderTest.filter(null, null, true);
        Map<String, Object> hdfs = QuickLinksProfileBuilderTest.service("HDFS", null, Sets.newHashSet(filter));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        properties.put(QUICKLINKS_PROFILE_SERVICES_PROPERTY, services);
        ProvisionClusterRequest request = new ProvisionClusterRequest(properties, null);
        Assert.assertEquals("Quick links profile doesn't match expected", "{\"services\":[{\"name\":\"HDFS\",\"filters\":[{\"visible\":true}]}]}", request.getQuickLinksProfileJson());
    }

    @Test
    public void testQuickLinksProfile_BothGlobalAndServiceLevelFilters() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        properties.put(QUICKLINKS_PROFILE_FILTERS_PROPERTY, Sets.newHashSet(QuickLinksProfileBuilderTest.filter(null, null, true)));
        Map<String, String> filter = QuickLinksProfileBuilderTest.filter(null, null, true);
        Map<String, Object> hdfs = QuickLinksProfileBuilderTest.service("HDFS", null, Sets.newHashSet(filter));
        Set<Map<String, Object>> services = Sets.newHashSet(hdfs);
        properties.put(QUICKLINKS_PROFILE_SERVICES_PROPERTY, services);
        ProvisionClusterRequest request = new ProvisionClusterRequest(properties, null);
        System.out.println(request.getQuickLinksProfileJson());
        Assert.assertEquals("Quick links profile doesn't match expected", "{\"filters\":[{\"visible\":true}],\"services\":[{\"name\":\"HDFS\",\"filters\":[{\"visible\":true}]}]}", request.getQuickLinksProfileJson());
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testQuickLinksProfile_InvalidRequestData() throws Exception {
        Map<String, Object> properties = ProvisionClusterRequestTest.createBlueprintRequestProperties(ProvisionClusterRequestTest.CLUSTER_NAME, ProvisionClusterRequestTest.BLUEPRINT_NAME);
        properties.put(QUICKLINKS_PROFILE_SERVICES_PROPERTY, "Hello World!");
        ProvisionClusterRequest request = new ProvisionClusterRequest(properties, null);
    }
}

