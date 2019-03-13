/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import RemoteClusterResourceProvider.CLUSTER_NAME_PROPERTY_ID;
import RemoteClusterResourceProvider.CLUSTER_URL_PROPERTY_ID;
import RemoteClusterResourceProvider.PASSWORD_PROPERTY_ID;
import RemoteClusterResourceProvider.SERVICES_PROPERTY_ID;
import RemoteClusterResourceProvider.USERNAME_PROPERTY_ID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.predicate.EqualsPredicate;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.RemoteAmbariClusterDAO;
import org.apache.ambari.server.orm.entities.RemoteAmbariClusterEntity;
import org.apache.ambari.server.orm.entities.RemoteAmbariClusterServiceEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.view.RemoteAmbariClusterRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static RemoteClusterResourceProvider.CLUSTER_NAME_PROPERTY_ID;


public class RemoteClusterResourceProviderTest {
    @Test
    public void testToResource() throws Exception {
        RemoteClusterResourceProvider provider = new RemoteClusterResourceProvider();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(CLUSTER_URL_PROPERTY_ID);
        propertyIds.add(USERNAME_PROPERTY_ID);
        propertyIds.add(PASSWORD_PROPERTY_ID);
        propertyIds.add(SERVICES_PROPERTY_ID);
        RemoteAmbariClusterServiceEntity service1 = createNiceMock(RemoteAmbariClusterServiceEntity.class);
        expect(service1.getServiceName()).andReturn("service1").once();
        RemoteAmbariClusterServiceEntity service2 = createNiceMock(RemoteAmbariClusterServiceEntity.class);
        expect(service2.getServiceName()).andReturn("service2").once();
        List<RemoteAmbariClusterServiceEntity> serviceList = new ArrayList<>();
        serviceList.add(service1);
        serviceList.add(service2);
        RemoteAmbariClusterEntity entity = createNiceMock(RemoteAmbariClusterEntity.class);
        expect(entity.getName()).andReturn("test").once();
        expect(entity.getUrl()).andReturn("url").once();
        expect(entity.getUsername()).andReturn("user").once();
        expect(entity.getServices()).andReturn(serviceList).once();
        replay(service1, service2, entity);
        List<String> services = new ArrayList<>();
        services.add("service1");
        services.add("service2");
        Resource resource = provider.toResource(propertyIds, entity);
        Assert.assertEquals(resource.getPropertyValue(CLUSTER_NAME_PROPERTY_ID), "test");
        Assert.assertEquals(resource.getPropertyValue(CLUSTER_URL_PROPERTY_ID), "url");
        Assert.assertEquals(resource.getPropertyValue(USERNAME_PROPERTY_ID), "user");
        Assert.assertEquals(resource.getPropertyValue(SERVICES_PROPERTY_ID), services);
        verify(service1, service2, entity);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDeleteResources() throws Exception {
        RemoteClusterResourceProvider provider = new RemoteClusterResourceProvider();
        RemoteAmbariClusterDAO clusterDAO = createNiceMock(RemoteAmbariClusterDAO.class);
        RemoteAmbariClusterEntity clusterEntity = new RemoteAmbariClusterEntity();
        RemoteClusterResourceProviderTest.setField(RemoteClusterResourceProvider.class.getDeclaredField("remoteAmbariClusterDAO"), clusterDAO);
        EqualsPredicate equalsPredicate = new EqualsPredicate(CLUSTER_NAME_PROPERTY_ID, "test");
        RemoteAmbariClusterRegistry clusterRegistry = createMock(RemoteAmbariClusterRegistry.class);
        RemoteClusterResourceProviderTest.setField(RemoteClusterResourceProvider.class.getDeclaredField("remoteAmbariClusterRegistry"), clusterRegistry);
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(CLUSTER_NAME_PROPERTY_ID, "test");
        expect(clusterDAO.findByName("test")).andReturn(clusterEntity);
        clusterRegistry.delete(clusterEntity);
        replay(clusterDAO);
        properties.add(propertyMap);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.deleteResources(PropertyHelper.getCreateRequest(properties, null), equalsPredicate);
    }
}

