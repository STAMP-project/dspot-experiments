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


import ViewDefinition.ViewStatus.DEPLOYED;
import ViewDefinition.ViewStatus.DEPLOYING;
import ViewInstanceResourceProvider.CLUSTER_HANDLE;
import ViewInstanceResourceProvider.ICON_PATH;
import ViewInstanceResourceProvider.INSTANCE_NAME;
import ViewInstanceResourceProvider.PROPERTIES;
import ViewInstanceResourceProvider.VERSION;
import ViewInstanceResourceProvider.VIEW_NAME;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceAlreadyExistsException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.view.ViewRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


public class ViewInstanceResourceProviderTest {
    private static final ViewRegistry viewregistry = createMock(ViewRegistry.class);

    @Test
    public void testToResource() throws Exception {
        ViewInstanceResourceProvider provider = new ViewInstanceResourceProvider();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(PROPERTIES);
        propertyIds.add(CLUSTER_HANDLE);
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).anyTimes();
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("par1", "val1");
        propertyMap.put("par2", "val2");
        expect(viewInstanceEntity.getPropertyMap()).andReturn(propertyMap);
        expect(viewInstanceEntity.getData()).andReturn(Collections.emptyList()).anyTimes();
        expect(ViewInstanceResourceProviderTest.viewregistry.checkAdmin()).andReturn(true);
        expect(ViewInstanceResourceProviderTest.viewregistry.checkAdmin()).andReturn(false);
        expect(viewInstanceEntity.getClusterHandle()).andReturn(1L);
        replay(ViewInstanceResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity);
        // as admin
        Resource resource = provider.toResource(viewInstanceEntity, propertyIds);
        Map<String, Map<String, Object>> properties = resource.getPropertiesMap();
        Assert.assertEquals(2, properties.size());
        Map<String, Object> props = properties.get("ViewInstanceInfo");
        Assert.assertNotNull(props);
        Assert.assertEquals(1, props.size());
        Assert.assertEquals(1L, props.get("cluster_handle"));
        props = properties.get("ViewInstanceInfo/properties");
        Assert.assertNotNull(props);
        Assert.assertEquals(2, props.size());
        Assert.assertEquals("val1", props.get("par1"));
        Assert.assertEquals("val2", props.get("par2"));
        // as non-admin
        resource = provider.toResource(viewInstanceEntity, propertyIds);
        properties = resource.getPropertiesMap();
        props = properties.get("ViewInstanceInfo/properties");
        Assert.assertNull(props);
        verify(ViewInstanceResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testCreateResources_existingInstance() throws Exception {
        ViewInstanceResourceProvider provider = new ViewInstanceResourceProvider();
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(VIEW_NAME, "V1");
        propertyMap.put(VERSION, "1.0.0");
        propertyMap.put(INSTANCE_NAME, "I1");
        properties.add(propertyMap);
        ViewInstanceEntity viewInstanceEntity = new ViewInstanceEntity();
        viewInstanceEntity.setViewName("V1{1.0.0}");
        viewInstanceEntity.setName("I1");
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setStatus(DEPLOYED);
        viewEntity.setName("V1{1.0.0}");
        viewInstanceEntity.setViewEntity(viewEntity);
        expect(ViewInstanceResourceProviderTest.viewregistry.instanceExists(viewInstanceEntity)).andReturn(true);
        expect(ViewInstanceResourceProviderTest.viewregistry.getDefinition("V1", "1.0.0")).andReturn(viewEntity).anyTimes();
        expect(ViewInstanceResourceProviderTest.viewregistry.getDefinition("V1", null)).andReturn(viewEntity);
        expect(ViewInstanceResourceProviderTest.viewregistry.checkAdmin()).andReturn(true);
        replay(ViewInstanceResourceProviderTest.viewregistry);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        try {
            provider.createResources(PropertyHelper.getCreateRequest(properties, null));
            Assert.fail("Expected ResourceAlreadyExistsException.");
        } catch (ResourceAlreadyExistsException e) {
            // expected
        }
        verify(ViewInstanceResourceProviderTest.viewregistry);
    }

    @Test
    public void testCreateResources_viewNotLoaded() throws Exception {
        ViewInstanceResourceProvider provider = new ViewInstanceResourceProvider();
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(VIEW_NAME, "V1");
        propertyMap.put(VERSION, "1.0.0");
        propertyMap.put(INSTANCE_NAME, "I1");
        properties.add(propertyMap);
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setName("V1{1.0.0}");
        viewEntity.setStatus(DEPLOYING);
        ViewInstanceEntity viewInstanceEntity = new ViewInstanceEntity();
        viewInstanceEntity.setViewName("V1{1.0.0}");
        viewInstanceEntity.setName("I1");
        viewInstanceEntity.setViewEntity(viewEntity);
        expect(ViewInstanceResourceProviderTest.viewregistry.getDefinition("V1", "1.0.0")).andReturn(viewEntity).anyTimes();
        expect(ViewInstanceResourceProviderTest.viewregistry.getDefinition("V1", null)).andReturn(viewEntity);
        expect(ViewInstanceResourceProviderTest.viewregistry.checkAdmin()).andReturn(true);
        replay(ViewInstanceResourceProviderTest.viewregistry);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        try {
            provider.createResources(PropertyHelper.getCreateRequest(properties, null));
            Assert.fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            // expected
        }
        verify(ViewInstanceResourceProviderTest.viewregistry);
    }

    @Test
    public void testUpdateResources_viewNotLoaded() throws Exception {
        ViewInstanceResourceProvider provider = new ViewInstanceResourceProvider();
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(ICON_PATH, "path");
        properties.add(propertyMap);
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        Predicate predicate = predicateBuilder.property(VIEW_NAME).equals("V1").toPredicate();
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setName("V1{1.0.0}");
        viewEntity.setStatus(DEPLOYING);
        ViewInstanceEntity viewInstanceEntity = new ViewInstanceEntity();
        viewInstanceEntity.setViewName("V1{1.0.0}");
        viewInstanceEntity.setName("I1");
        viewInstanceEntity.setViewEntity(viewEntity);
        expect(ViewInstanceResourceProviderTest.viewregistry.getDefinitions()).andReturn(Collections.singleton(viewEntity));
        replay(ViewInstanceResourceProviderTest.viewregistry);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.updateResources(PropertyHelper.getCreateRequest(properties, null), predicate);
        Assert.assertNull(viewInstanceEntity.getIcon());
        verify(ViewInstanceResourceProviderTest.viewregistry);
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }
}

