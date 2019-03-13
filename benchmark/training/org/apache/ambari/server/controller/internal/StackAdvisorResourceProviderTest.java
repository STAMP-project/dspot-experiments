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


import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Request;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StackAdvisorResourceProviderTest {
    private RecommendationResourceProvider provider;

    @Test
    public void testCalculateConfigurations() throws Exception {
        Request request = createMockRequest(((StackAdvisorResourceProvider.CONFIGURATIONS_PROPERTY_ID) + "site/properties/string_prop"), "string", ((StackAdvisorResourceProvider.CONFIGURATIONS_PROPERTY_ID) + "site/properties/array_prop"), Lists.newArrayList("array1", "array2"));
        Map<String, Map<String, Map<String, String>>> calculatedConfigurations = provider.calculateConfigurations(request);
        Assert.assertNotNull(calculatedConfigurations);
        Assert.assertEquals(1, calculatedConfigurations.size());
        Map<String, Map<String, String>> site = calculatedConfigurations.get("site");
        Assert.assertNotNull(site);
        Assert.assertEquals(1, site.size());
        Map<String, String> properties = site.get("properties");
        Assert.assertNotNull(properties);
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("string", properties.get("string_prop"));
        Assert.assertEquals("[array1, array2]", properties.get("array_prop"));
    }

    @Test
    public void testReadUserContext() throws Exception {
        Request request = createMockRequest(((StackAdvisorResourceProvider.CONFIGURATIONS_PROPERTY_ID) + "site/properties/string_prop"), "string", StackAdvisorResourceProvider.USER_CONTEXT_OPERATION_PROPERTY, "op1", StackAdvisorResourceProvider.USER_CONTEXT_OPERATION_DETAILS_PROPERTY, "op_det");
        Map<String, String> userContext = provider.readUserContext(request);
        Assert.assertNotNull(userContext);
        Assert.assertEquals(2, userContext.size());
        Assert.assertEquals("op1", userContext.get("operation"));
        Assert.assertEquals("op_det", userContext.get("operation_details"));
    }

    @Test
    public void testCalculateConfigurationsWithNullPropertyValues() throws Exception {
        Request request = createMockRequest(((StackAdvisorResourceProvider.CONFIGURATIONS_PROPERTY_ID) + "site/properties/string_prop"), null, ((StackAdvisorResourceProvider.CONFIGURATIONS_PROPERTY_ID) + "site/properties/array_prop"), Lists.newArrayList("array1", "array2"));
        Map<String, Map<String, Map<String, String>>> calculatedConfigurations = provider.calculateConfigurations(request);
        Assert.assertNotNull(calculatedConfigurations);
        Assert.assertEquals(1, calculatedConfigurations.size());
        Map<String, Map<String, String>> site = calculatedConfigurations.get("site");
        Assert.assertNotNull(site);
        Assert.assertEquals(1, site.size());
        Map<String, String> properties = site.get("properties");
        Assert.assertNotNull(properties);
        Assert.assertEquals("[array1, array2]", properties.get("array_prop"));
        // config properties with null values should be ignored
        Assert.assertFalse(properties.containsKey("string_prop"));
    }

    @Test
    public void testStackAdvisorWithEmptyHosts() {
        AmbariManagementController ambariManagementController = Mockito.mock(AmbariManagementController.class);
        RecommendationResourceProvider provider = new RecommendationResourceProvider(ambariManagementController);
        Request request = Mockito.mock(Request.class);
        Set<Map<String, Object>> propertiesSet = new HashSet<>();
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("hosts", new LinkedHashSet<>());
        propertiesMap.put("recommend", "configurations");
        propertiesSet.add(propertiesMap);
        Mockito.doReturn(propertiesSet).when(request).getProperties();
        try {
            provider.createResources(request);
            Assert.fail();
        } catch (Exception e) {
        }
    }
}

