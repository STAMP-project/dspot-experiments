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


import Resource.Type.Recommendation;
import StackAdvisorRequest.StackAdvisorRequestType.CONFIGURATIONS;
import StackAdvisorRequest.StackAdvisorRequestType.HOST_GROUPS;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.junit.Assert;
import org.junit.Test;


public class RecommendationResourceProviderTest {
    @Test
    public void testCreateConfigurationResources() throws Exception {
        Set<String> hosts = new HashSet<>(Arrays.asList(new String[]{ "hostName1", "hostName2", "hostName3" }));
        Set<String> services = new HashSet<>(Arrays.asList(new String[]{ "serviceName1", "serviceName2", "serviceName3" }));
        RequestStatus requestStatus = testCreateResources(hosts, services, CONFIGURATIONS, true);
        Assert.assertFalse((requestStatus == null));
        Assert.assertEquals(1, requestStatus.getAssociatedResources().size());
        Assert.assertEquals(Recommendation, requestStatus.getAssociatedResources().iterator().next().getType());
        Map<String, Map<String, Object>> propertiesMap = requestStatus.getAssociatedResources().iterator().next().getPropertiesMap();
        Assert.assertEquals(2, propertiesMap.size());
        Assert.assertTrue(propertiesMap.containsKey("recommendations"));
        Assert.assertTrue(propertiesMap.containsKey("recommendations/blueprint/configurations"));
        Assert.assertEquals(1, propertiesMap.get("recommendations").size());
        Assert.assertTrue(propertiesMap.get("recommendations").containsKey("config-groups"));
        Assert.assertNotNull(propertiesMap.get("recommendations").get("config-groups"));
        Assert.assertEquals(0, propertiesMap.get("recommendations/blueprint/configurations").size());
    }

    @Test
    public void testCreateNotConfigurationResources() throws Exception {
        Set<String> hosts = new HashSet<>(Arrays.asList(new String[]{ "hostName1", "hostName2", "hostName3" }));
        Set<String> services = new HashSet<>(Arrays.asList(new String[]{ "serviceName1", "serviceName2", "serviceName3" }));
        RequestStatus requestStatus = testCreateResources(hosts, services, HOST_GROUPS, false);
        Assert.assertFalse((requestStatus == null));
        Assert.assertEquals(1, requestStatus.getAssociatedResources().size());
        Assert.assertEquals(Recommendation, requestStatus.getAssociatedResources().iterator().next().getType());
        Map<String, Map<String, Object>> propertiesMap = requestStatus.getAssociatedResources().iterator().next().getPropertiesMap();
        Assert.assertEquals(7, propertiesMap.size());
        Assert.assertTrue(propertiesMap.containsKey(""));
        Assert.assertTrue(propertiesMap.containsKey("Recommendation"));
        Assert.assertTrue(propertiesMap.containsKey("Versions"));
        Assert.assertTrue(propertiesMap.containsKey("recommendations"));
        Assert.assertTrue(propertiesMap.containsKey("recommendations/blueprint"));
        Assert.assertTrue(propertiesMap.containsKey("recommendations/blueprint/configurations"));
        Assert.assertTrue(propertiesMap.containsKey("recommendations/blueprint_cluster_binding"));
        Assert.assertEquals(2, propertiesMap.get("").size());
        Assert.assertTrue(propertiesMap.get("").containsKey("hosts"));
        Assert.assertTrue(propertiesMap.get("").containsKey("services"));
        Assert.assertEquals(hosts, propertiesMap.get("").get("hosts"));
        Assert.assertEquals(services, propertiesMap.get("").get("services"));
        Assert.assertEquals(1, propertiesMap.get("Recommendation").size());
        Assert.assertTrue(propertiesMap.get("Recommendation").containsKey("id"));
        Assert.assertEquals(1, propertiesMap.get("Recommendation").get("id"));
        Assert.assertEquals(2, propertiesMap.get("Versions").size());
        Assert.assertTrue(propertiesMap.get("Versions").containsKey("stack_name"));
        Assert.assertTrue(propertiesMap.get("Versions").containsKey("stack_version"));
        Assert.assertEquals("stackName", propertiesMap.get("Versions").get("stack_name"));
        Assert.assertEquals("stackVersion", propertiesMap.get("Versions").get("stack_version"));
        Assert.assertEquals(1, propertiesMap.get("recommendations").size());
        Assert.assertTrue(propertiesMap.get("recommendations").containsKey("config-groups"));
        Assert.assertNotNull(propertiesMap.get("recommendations").get("config-groups"));
        Assert.assertEquals(1, propertiesMap.get("recommendations/blueprint").size());
        Assert.assertTrue(propertiesMap.get("recommendations/blueprint").containsKey("host_groups"));
        Assert.assertNotNull(propertiesMap.get("recommendations/blueprint").get("host_groups"));
        Assert.assertEquals(0, propertiesMap.get("recommendations/blueprint/configurations").size());
        Assert.assertEquals(1, propertiesMap.get("recommendations/blueprint_cluster_binding").size());
        Assert.assertTrue(propertiesMap.get("recommendations/blueprint_cluster_binding").containsKey("host_groups"));
        Assert.assertNotNull(propertiesMap.get("recommendations/blueprint_cluster_binding").get("host_groups"));
    }
}

