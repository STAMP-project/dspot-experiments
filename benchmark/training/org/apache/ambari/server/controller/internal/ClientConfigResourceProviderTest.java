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


import ClientConfigResourceProvider.COMPONENT_CLUSTER_NAME_PROPERTY_ID;
import ClientConfigResourceProvider.COMPONENT_COMPONENT_NAME_PROPERTY_ID;
import ClientConfigResourceProvider.COMPONENT_SERVICE_NAME_PROPERTY_ID;
import Resource.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * ClientConfigResourceProviderTest tests.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientConfigResourceProvider.class, StageUtils.class })
public class ClientConfigResourceProviderTest {
    @Test
    public void testCreateResources() throws Exception {
        Resource.Type type = Type.ClientConfig;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);
        // replay
        replay(managementController, response);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        Map<String, Object> properties = new LinkedHashMap<>();
        // add properties to the request map
        properties.put(COMPONENT_CLUSTER_NAME_PROPERTY_ID, "c1");
        properties.put(COMPONENT_COMPONENT_NAME_PROPERTY_ID, "HDFS_CLIENT");
        properties.put(COMPONENT_SERVICE_NAME_PROPERTY_ID, "HDFS");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        try {
            provider.createResources(request);
            Assert.fail("Expected an UnsupportedOperationException");
        } catch (SystemException e) {
            // expected
        }
        // verify
        verify(managementController, response);
    }

    @Test
    public void testUpdateResources() throws Exception {
        Resource.Type type = Type.ClientConfig;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);
        // replay
        replay(managementController, response);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        // add the property map to a set for the request.
        Map<String, Object> properties = new LinkedHashMap<>();
        // create the request
        Request request = PropertyHelper.getUpdateRequest(properties, null);
        Predicate predicate = new PredicateBuilder().property(COMPONENT_CLUSTER_NAME_PROPERTY_ID).equals("c1").toPredicate();
        try {
            provider.updateResources(request, predicate);
            Assert.fail("Expected an UnsupportedOperationException");
        } catch (SystemException e) {
            // expected
        }
        // verify
        verify(managementController, response);
    }

    @Test
    public void testGetResourcesForAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesForClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesForClusterOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesForServiceAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesForServiceOperator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesForClusterUser() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesForNoRoleUser() throws Exception {
        testGetResources(TestAuthenticationFactory.createNoRoleUser());
    }

    @Test
    public void testGetResourcesFromCommonServicesForAdministrator() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesFromCommonServicesForClusterAdministrator() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesFromCommonServicesForClusterOperator() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createClusterOperator());
    }

    @Test
    public void testGetResourcesFromCommonServicesForServiceAdministrator() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesFromCommonServicesForServiceOperator() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testGetResourcesFromCommonServicesForClusterUser() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createClusterUser());
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResourcesFromCommonServicesForNoRoleUser() throws Exception {
        testGetResourcesFromCommonServices(TestAuthenticationFactory.createNoRoleUser());
    }

    @Test
    public void testDeleteResources() throws Exception {
        Resource.Type type = Type.ClientConfig;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        // replay
        replay(managementController);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Predicate predicate = new PredicateBuilder().property(COMPONENT_COMPONENT_NAME_PROPERTY_ID).equals("HDFS_CLIENT").toPredicate();
        try {
            provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
            Assert.fail("Expected an UnsupportedOperationException");
        } catch (SystemException e) {
            // expected
        }
        // verify
        verify(managementController);
    }
}

