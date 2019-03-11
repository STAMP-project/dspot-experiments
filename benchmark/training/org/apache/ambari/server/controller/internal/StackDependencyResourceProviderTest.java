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


import StackDependencyResourceProvider.COMPONENT_NAME_ID;
import StackDependencyResourceProvider.DEPENDENT_COMPONENT_NAME_ID;
import StackDependencyResourceProvider.DEPENDENT_SERVICE_NAME_ID;
import StackDependencyResourceProvider.SCOPE_ID;
import StackDependencyResourceProvider.SERVICE_NAME_ID;
import StackDependencyResourceProvider.STACK_NAME_ID;
import StackDependencyResourceProvider.STACK_VERSION_ID;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.StackAccessException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.spi.UnsupportedPropertyException;
import org.apache.ambari.server.state.DependencyInfo;
import org.junit.Assert;
import org.junit.Test;

import static StackDependencyResourceProvider.COMPONENT_NAME_ID;
import static StackDependencyResourceProvider.DEPENDENT_COMPONENT_NAME_ID;
import static StackDependencyResourceProvider.DEPENDENT_SERVICE_NAME_ID;
import static StackDependencyResourceProvider.STACK_NAME_ID;
import static StackDependencyResourceProvider.STACK_VERSION_ID;


/**
 * StackDependencyResourceProvider unit tests.
 */
public class StackDependencyResourceProviderTest {
    private static final AmbariMetaInfo metaInfo = createMock(AmbariMetaInfo.class);

    @Test
    public void testGetResources() throws AmbariException, NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        Request request = createNiceMock(Request.class);
        DependencyInfo dependencyInfo = new DependencyInfo();
        dependencyInfo.setName("service_name/comp_name");
        dependencyInfo.setScope("cluster");
        Predicate namePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(COMPONENT_NAME_ID, "comp_name");
        Predicate depServicePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(DEPENDENT_SERVICE_NAME_ID, "dep_service_name");
        Predicate depCompPredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(DEPENDENT_COMPONENT_NAME_ID, "dep_comp_name");
        Predicate stackNamePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(STACK_NAME_ID, "stack_name");
        Predicate stackVersionPredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(STACK_VERSION_ID, "stack_version");
        Predicate andPredicate = new org.apache.ambari.server.controller.predicate.AndPredicate(namePredicate, depServicePredicate, depCompPredicate, stackNamePredicate, stackVersionPredicate);
        // mock expectations
        expect(StackDependencyResourceProviderTest.metaInfo.getComponentDependency("stack_name", "stack_version", "dep_service_name", "dep_comp_name", "comp_name")).andReturn(dependencyInfo);
        replay(StackDependencyResourceProviderTest.metaInfo, request);
        ResourceProvider provider = createProvider();
        Set<Resource> resources = provider.getResources(request, andPredicate);
        verify(StackDependencyResourceProviderTest.metaInfo);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals("cluster", resource.getPropertyValue(SCOPE_ID));
        Assert.assertEquals("comp_name", resource.getPropertyValue(COMPONENT_NAME_ID));
        Assert.assertEquals("service_name", resource.getPropertyValue(SERVICE_NAME_ID));
        Assert.assertEquals("dep_service_name", resource.getPropertyValue(DEPENDENT_SERVICE_NAME_ID));
        Assert.assertEquals("dep_comp_name", resource.getPropertyValue(DEPENDENT_COMPONENT_NAME_ID));
        Assert.assertEquals("stack_name", resource.getPropertyValue(STACK_NAME_ID));
        Assert.assertEquals("stack_version", resource.getPropertyValue(STACK_VERSION_ID));
    }

    @Test
    public void testGetResources_Query() throws AmbariException, NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        Request request = createNiceMock(Request.class);
        DependencyInfo dependencyInfo = new DependencyInfo();
        dependencyInfo.setName("service_name/comp_name");
        dependencyInfo.setScope("cluster");
        Predicate namePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(COMPONENT_NAME_ID, "comp_name");
        Predicate name2Predicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(COMPONENT_NAME_ID, "comp_name2");
        Predicate depServicePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(DEPENDENT_SERVICE_NAME_ID, "dep_service_name");
        Predicate depCompPredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(DEPENDENT_COMPONENT_NAME_ID, "dep_comp_name");
        Predicate stackNamePredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(STACK_NAME_ID, "stack_name");
        Predicate stackVersionPredicate = new org.apache.ambari.server.controller.predicate.EqualsPredicate(STACK_VERSION_ID, "stack_version");
        Predicate andPredicate1 = new org.apache.ambari.server.controller.predicate.AndPredicate(namePredicate, depServicePredicate, depCompPredicate, stackNamePredicate, stackVersionPredicate);
        Predicate andPredicate2 = new org.apache.ambari.server.controller.predicate.AndPredicate(name2Predicate, depServicePredicate, depCompPredicate, stackNamePredicate, stackVersionPredicate);
        Predicate orPredicate = new org.apache.ambari.server.controller.predicate.OrPredicate(andPredicate1, andPredicate2);
        // mock expectations
        expect(StackDependencyResourceProviderTest.metaInfo.getComponentDependency("stack_name", "stack_version", "dep_service_name", "dep_comp_name", "comp_name")).andReturn(dependencyInfo);
        expect(StackDependencyResourceProviderTest.metaInfo.getComponentDependency("stack_name", "stack_version", "dep_service_name", "dep_comp_name", "comp_name2")).andThrow(new StackAccessException("test"));
        replay(StackDependencyResourceProviderTest.metaInfo, request);
        ResourceProvider provider = createProvider();
        Set<Resource> resources = provider.getResources(request, orPredicate);
        verify(StackDependencyResourceProviderTest.metaInfo);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals("cluster", resource.getPropertyValue(SCOPE_ID));
        Assert.assertEquals("comp_name", resource.getPropertyValue(COMPONENT_NAME_ID));
        Assert.assertEquals("service_name", resource.getPropertyValue(SERVICE_NAME_ID));
        Assert.assertEquals("dep_service_name", resource.getPropertyValue(DEPENDENT_SERVICE_NAME_ID));
        Assert.assertEquals("dep_comp_name", resource.getPropertyValue(DEPENDENT_COMPONENT_NAME_ID));
        Assert.assertEquals("stack_name", resource.getPropertyValue(STACK_NAME_ID));
        Assert.assertEquals("stack_version", resource.getPropertyValue(STACK_VERSION_ID));
    }
}

