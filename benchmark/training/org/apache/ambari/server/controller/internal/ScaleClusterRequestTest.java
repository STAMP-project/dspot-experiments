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


import HostResourceProvider.HOST_HOST_NAME_PROPERTY_ID;
import TopologyRequest.Type.SCALE;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.BlueprintFactory;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.HostGroup;
import org.apache.ambari.server.topology.HostGroupInfo;
import org.apache.ambari.server.topology.InvalidTopologyTemplateException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for ScaleClusterRequest.
 */
@SuppressWarnings("unchecked")
public class ScaleClusterRequestTest {
    private static final String CLUSTER_NAME = "cluster_name";

    private static final String BLUEPRINT_NAME = "blueprint_name";

    private static final String HOST1_NAME = "host1.test.com";

    private static final String HOST2_NAME = "host2.test.com";

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String GROUP3_NAME = "group3";

    private static final String PREDICATE = "test/prop=foo";

    private static final BlueprintFactory blueprintFactory = createStrictMock(BlueprintFactory.class);

    private static final Blueprint blueprint = createNiceMock(Blueprint.class);

    private static final ResourceProvider hostResourceProvider = createMock(ResourceProvider.class);

    private static final HostGroup hostGroup1 = createNiceMock(HostGroup.class);

    private static final Configuration blueprintConfig = new Configuration(Collections.emptyMap(), Collections.emptyMap());

    @Test
    public void test_basic_hostName() throws Exception {
        Map<String, Object> props = ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME);
        addSingleHostByName(props);
        addSingleHostByName(ScaleClusterRequestTest.replaceWithPlainHostNameKey(props));
    }

    @Test
    public void testMultipleHostNames() throws Exception {
        Set<Map<String, Object>> propertySet = new HashSet<>();
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName2(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        addMultipleHostsByName(propertySet);
        for (Map<String, Object> props : propertySet) {
            ScaleClusterRequestTest.replaceWithPlainHostNameKey(props);
        }
        addMultipleHostsByName(propertySet);
    }

    @Test
    public void test_basic_hostCount() throws Exception {
        // reset default host resource provider expectations to none since no host predicate is used
        reset(ScaleClusterRequestTest.hostResourceProvider);
        replay(ScaleClusterRequestTest.hostResourceProvider);
        ScaleClusterRequest scaleClusterRequest = new ScaleClusterRequest(Collections.singleton(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCount(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME)));
        Assert.assertEquals(SCALE, scaleClusterRequest.getType());
        Assert.assertEquals(String.format("Scale Cluster '%s' (+%s hosts)", ScaleClusterRequestTest.CLUSTER_NAME, "1"), scaleClusterRequest.getDescription());
        Assert.assertEquals(ScaleClusterRequestTest.CLUSTER_NAME, scaleClusterRequest.getClusterName());
        Assert.assertSame(ScaleClusterRequestTest.blueprint, scaleClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = scaleClusterRequest.getHostGroupInfo();
        Assert.assertEquals(1, hostGroupInfo.size());
        // group2
        // host info
        HostGroupInfo group2Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP2_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP2_NAME, group2Info.getHostGroupName());
        Assert.assertEquals(0, group2Info.getHostNames().size());
        Assert.assertEquals(1, group2Info.getRequestedHostCount());
        Assert.assertNull(group2Info.getPredicate());
    }

    @Test
    public void test_basic_hostCount2() throws Exception {
        // reset default host resource provider expectations to none since no host predicate is used
        reset(ScaleClusterRequestTest.hostResourceProvider);
        replay(ScaleClusterRequestTest.hostResourceProvider);
        ScaleClusterRequest scaleClusterRequest = new ScaleClusterRequest(Collections.singleton(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCount2(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME)));
        Assert.assertEquals(SCALE, scaleClusterRequest.getType());
        Assert.assertEquals(String.format("Scale Cluster '%s' (+%s hosts)", ScaleClusterRequestTest.CLUSTER_NAME, "2"), scaleClusterRequest.getDescription());
        Assert.assertEquals(ScaleClusterRequestTest.CLUSTER_NAME, scaleClusterRequest.getClusterName());
        Assert.assertSame(ScaleClusterRequestTest.blueprint, scaleClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = scaleClusterRequest.getHostGroupInfo();
        Assert.assertEquals(1, hostGroupInfo.size());
        // group2
        // host info
        HostGroupInfo group2Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP3_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP3_NAME, group2Info.getHostGroupName());
        Assert.assertEquals(0, group2Info.getHostNames().size());
        Assert.assertEquals(2, group2Info.getRequestedHostCount());
        Assert.assertNull(group2Info.getPredicate());
    }

    @Test
    public void test_basic_hostCountAndPredicate() throws Exception {
        ScaleClusterRequest scaleClusterRequest = new ScaleClusterRequest(Collections.singleton(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCountAndPredicate(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME)));
        Assert.assertEquals(SCALE, scaleClusterRequest.getType());
        Assert.assertEquals(String.format("Scale Cluster '%s' (+%s hosts)", ScaleClusterRequestTest.CLUSTER_NAME, "1"), scaleClusterRequest.getDescription());
        Assert.assertEquals(ScaleClusterRequestTest.CLUSTER_NAME, scaleClusterRequest.getClusterName());
        Assert.assertSame(ScaleClusterRequestTest.blueprint, scaleClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = scaleClusterRequest.getHostGroupInfo();
        Assert.assertEquals(1, hostGroupInfo.size());
        // group3
        // host info
        HostGroupInfo group3Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP3_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP3_NAME, group3Info.getHostGroupName());
        Assert.assertEquals(0, group3Info.getHostNames().size());
        Assert.assertEquals(1, group3Info.getRequestedHostCount());
        Assert.assertEquals(ScaleClusterRequestTest.PREDICATE, group3Info.getPredicateString());
    }

    @Test
    public void testMultipleHostGroups() throws Exception {
        Set<Map<String, Object>> propertySet = new HashSet<>();
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCountAndPredicate(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCount(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        ScaleClusterRequest scaleClusterRequest = new ScaleClusterRequest(propertySet);
        Assert.assertEquals(SCALE, scaleClusterRequest.getType());
        Assert.assertEquals(String.format("Scale Cluster '%s' (+%s hosts)", ScaleClusterRequestTest.CLUSTER_NAME, "3"), scaleClusterRequest.getDescription());
        Assert.assertEquals(ScaleClusterRequestTest.CLUSTER_NAME, scaleClusterRequest.getClusterName());
        Assert.assertSame(ScaleClusterRequestTest.blueprint, scaleClusterRequest.getBlueprint());
        Map<String, HostGroupInfo> hostGroupInfo = scaleClusterRequest.getHostGroupInfo();
        Assert.assertEquals(3, hostGroupInfo.size());
        // group
        // host info
        HostGroupInfo group1Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP1_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP1_NAME, group1Info.getHostGroupName());
        Assert.assertEquals(1, group1Info.getHostNames().size());
        Assert.assertTrue(group1Info.getHostNames().contains(ScaleClusterRequestTest.HOST1_NAME));
        Assert.assertEquals(1, group1Info.getRequestedHostCount());
        Assert.assertNull(group1Info.getPredicate());
        // group2
        // host info
        HostGroupInfo group2Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP2_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP2_NAME, group2Info.getHostGroupName());
        Assert.assertEquals(0, group2Info.getHostNames().size());
        Assert.assertEquals(1, group2Info.getRequestedHostCount());
        Assert.assertNull(group2Info.getPredicate());
        // group3
        // host info
        HostGroupInfo group3Info = hostGroupInfo.get(ScaleClusterRequestTest.GROUP3_NAME);
        Assert.assertEquals(ScaleClusterRequestTest.GROUP3_NAME, group3Info.getHostGroupName());
        Assert.assertEquals(0, group3Info.getHostNames().size());
        Assert.assertEquals(1, group3Info.getRequestedHostCount());
        Assert.assertEquals(ScaleClusterRequestTest.PREDICATE, group3Info.getPredicateString());
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_GroupInfoMissingName() throws Exception {
        Map<String, Object> properties = ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME);
        // remove host group name
        properties.remove("host_group");
        // reset default host resource provider expectations to none
        reset(ScaleClusterRequestTest.hostResourceProvider);
        replay(ScaleClusterRequestTest.hostResourceProvider);
        // should result in an exception
        new ScaleClusterRequest(Collections.singleton(properties));
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void test_NoHostNameOrHostCount() throws Exception {
        Map<String, Object> properties = ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME);
        // remove host name
        properties.remove(HOST_HOST_NAME_PROPERTY_ID);
        // reset default host resource provider expectations to none
        reset(ScaleClusterRequestTest.hostResourceProvider);
        replay(ScaleClusterRequestTest.hostResourceProvider);
        // should result in an exception because neither host name or host count are specified
        new ScaleClusterRequest(Collections.singleton(properties));
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testInvalidPredicateProperty() throws Exception {
        reset(ScaleClusterRequestTest.hostResourceProvider);
        // checkPropertyIds() returns invalid property names
        expect(ScaleClusterRequestTest.hostResourceProvider.checkPropertyIds(Collections.singleton("test/prop"))).andReturn(Collections.singleton("test/prop"));
        replay(ScaleClusterRequestTest.hostResourceProvider);
        // should result in an exception due to invalid property in host predicate
        new ScaleClusterRequest(Collections.singleton(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostCountAndPredicate(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME)));
    }

    @Test(expected = InvalidTopologyTemplateException.class)
    public void testMultipleBlueprints() throws Exception {
        reset(ScaleClusterRequestTest.hostResourceProvider);
        replay(ScaleClusterRequestTest.hostResourceProvider);
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName(ScaleClusterRequestTest.CLUSTER_NAME, ScaleClusterRequestTest.BLUEPRINT_NAME));
        propertySet.add(ScaleClusterRequestTest.createScaleClusterPropertiesGroup1_HostName2(ScaleClusterRequestTest.CLUSTER_NAME, "OTHER_BLUEPRINT"));
        // should result in an exception due to different blueprints being specified
        new ScaleClusterRequest(propertySet);
    }
}

