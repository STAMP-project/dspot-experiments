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


import RepositoryResourceProvider.REPOSITORY_BASE_URL_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_CLUSTER_STACK_VERSION_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_COMPONENTS_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_DISTRIBUTION_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_OS_TYPE_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_REPO_ID_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_REPO_NAME_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_STACK_NAME_PROPERTY_ID;
import RepositoryResourceProvider.REPOSITORY_STACK_VERSION_PROPERTY_ID;
import Resource.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.RepositoryRequest;
import org.apache.ambari.server.controller.RepositoryResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.state.stack.RepoTag;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class RepositoryResourceProviderTest {
    private static final String VAL_STACK_NAME = "HDP";

    private static final String VAL_STACK_VERSION = "0.2";

    private static final String VAL_OS = "centos6";

    private static final String VAL_REPO_ID = "HDP-0.2";

    private static final String VAL_REPO_NAME = "HDP1";

    private static final String VAL_BASE_URL = "http://foo.com";

    private static final String VAL_DISTRIBUTION = "mydist";

    private static final String VAL_COMPONENT_NAME = "mycomponentname";

    @Test
    public void testGetResources() throws Exception {
        AmbariManagementController managementController = EasyMock.createMock(AmbariManagementController.class);
        RepositoryResponse rr = new RepositoryResponse(RepositoryResourceProviderTest.VAL_BASE_URL, RepositoryResourceProviderTest.VAL_OS, RepositoryResourceProviderTest.VAL_REPO_ID, RepositoryResourceProviderTest.VAL_REPO_NAME, RepositoryResourceProviderTest.VAL_DISTRIBUTION, RepositoryResourceProviderTest.VAL_COMPONENT_NAME, null, null, Collections.<RepoTag>emptySet(), Collections.emptyList());
        rr.setStackName(RepositoryResourceProviderTest.VAL_STACK_NAME);
        rr.setStackVersion(RepositoryResourceProviderTest.VAL_STACK_VERSION);
        Set<RepositoryResponse> allResponse = new HashSet<>();
        allResponse.add(rr);
        // set expectations
        expect(managementController.getRepositories(EasyMock.anyObject())).andReturn(allResponse).times(2);
        // replay
        replay(managementController);
        ResourceProvider provider = new RepositoryResourceProvider(managementController);
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(REPOSITORY_STACK_NAME_PROPERTY_ID);
        propertyIds.add(REPOSITORY_STACK_VERSION_PROPERTY_ID);
        propertyIds.add(REPOSITORY_REPO_NAME_PROPERTY_ID);
        propertyIds.add(REPOSITORY_BASE_URL_PROPERTY_ID);
        propertyIds.add(REPOSITORY_OS_TYPE_PROPERTY_ID);
        propertyIds.add(REPOSITORY_REPO_ID_PROPERTY_ID);
        propertyIds.add(REPOSITORY_CLUSTER_STACK_VERSION_PROPERTY_ID);
        propertyIds.add(REPOSITORY_DISTRIBUTION_PROPERTY_ID);
        propertyIds.add(REPOSITORY_COMPONENTS_PROPERTY_ID);
        Predicate predicate = new PredicateBuilder().property(REPOSITORY_STACK_NAME_PROPERTY_ID).equals(RepositoryResourceProviderTest.VAL_STACK_NAME).and().property(REPOSITORY_STACK_VERSION_PROPERTY_ID).equals(RepositoryResourceProviderTest.VAL_STACK_VERSION).toPredicate();
        // create the request
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // get all ... no predicate
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(allResponse.size(), resources.size());
        for (Resource resource : resources) {
            Object o = resource.getPropertyValue(REPOSITORY_STACK_NAME_PROPERTY_ID);
            Assert.assertEquals(RepositoryResourceProviderTest.VAL_STACK_NAME, o);
            o = resource.getPropertyValue(REPOSITORY_STACK_VERSION_PROPERTY_ID);
            Assert.assertEquals(RepositoryResourceProviderTest.VAL_STACK_VERSION, o);
            o = resource.getPropertyValue(REPOSITORY_REPO_NAME_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_REPO_NAME);
            o = resource.getPropertyValue(REPOSITORY_BASE_URL_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_BASE_URL);
            o = resource.getPropertyValue(REPOSITORY_OS_TYPE_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_OS);
            o = resource.getPropertyValue(REPOSITORY_REPO_ID_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_REPO_ID);
            o = resource.getPropertyValue(REPOSITORY_CLUSTER_STACK_VERSION_PROPERTY_ID);
            Assert.assertNull(o);
            o = resource.getPropertyValue(REPOSITORY_DISTRIBUTION_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_DISTRIBUTION);
            o = resource.getPropertyValue(REPOSITORY_COMPONENTS_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_COMPONENT_NAME);
        }
        // !!! check that the stack version id is returned
        rr.setClusterVersionId(525L);
        resources = provider.getResources(request, predicate);
        Assert.assertEquals(allResponse.size(), resources.size());
        for (Resource resource : resources) {
            Object o = resource.getPropertyValue(REPOSITORY_STACK_NAME_PROPERTY_ID);
            Assert.assertEquals(RepositoryResourceProviderTest.VAL_STACK_NAME, o);
            o = resource.getPropertyValue(REPOSITORY_STACK_VERSION_PROPERTY_ID);
            Assert.assertEquals(RepositoryResourceProviderTest.VAL_STACK_VERSION, o);
            o = resource.getPropertyValue(REPOSITORY_REPO_NAME_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_REPO_NAME);
            o = resource.getPropertyValue(REPOSITORY_BASE_URL_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_BASE_URL);
            o = resource.getPropertyValue(REPOSITORY_OS_TYPE_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_OS);
            o = resource.getPropertyValue(REPOSITORY_REPO_ID_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_REPO_ID);
            o = resource.getPropertyValue(REPOSITORY_CLUSTER_STACK_VERSION_PROPERTY_ID);
            Assert.assertEquals(525L, o);
            o = resource.getPropertyValue(REPOSITORY_DISTRIBUTION_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_DISTRIBUTION);
            o = resource.getPropertyValue(REPOSITORY_COMPONENTS_PROPERTY_ID);
            Assert.assertEquals(o, RepositoryResourceProviderTest.VAL_COMPONENT_NAME);
        }
        // verify
        verify(managementController);
    }

    @Test
    public void testUpdateResources() throws Exception {
        Resource.Type type = Type.Repository;
        AmbariManagementController managementController = EasyMock.createMock(AmbariManagementController.class);
        RepositoryResponse rr = new RepositoryResponse(RepositoryResourceProviderTest.VAL_BASE_URL, RepositoryResourceProviderTest.VAL_OS, RepositoryResourceProviderTest.VAL_REPO_ID, RepositoryResourceProviderTest.VAL_REPO_NAME, null, null, null, null, Collections.<RepoTag>emptySet(), Collections.emptyList());
        Set<RepositoryResponse> allResponse = new HashSet<>();
        allResponse.add(rr);
        // set expectations
        expect(managementController.getRepositories(EasyMock.anyObject())).andReturn(allResponse).times(1);
        managementController.verifyRepositories(EasyMock.<Set<RepositoryRequest>>anyObject());
        // replay
        replay(managementController);
        ResourceProvider provider = new RepositoryResourceProvider(managementController);
        // add the property map to a set for the request.
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(REPOSITORY_BASE_URL_PROPERTY_ID, "http://garbage.eu.co");
        // create the request
        Request request = PropertyHelper.getUpdateRequest(properties, null);
        Predicate predicate = new PredicateBuilder().property(REPOSITORY_STACK_NAME_PROPERTY_ID).equals(RepositoryResourceProviderTest.VAL_STACK_NAME).and().property(REPOSITORY_STACK_VERSION_PROPERTY_ID).equals(RepositoryResourceProviderTest.VAL_STACK_VERSION).toPredicate();
        provider.updateResources(request, predicate);
        // verify
        verify(managementController);
    }
}

