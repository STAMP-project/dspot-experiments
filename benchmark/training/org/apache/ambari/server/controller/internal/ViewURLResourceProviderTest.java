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


import ViewURLResourceProvider.URL_NAME;
import ViewURLResourceProvider.URL_SUFFIX;
import ViewURLResourceProvider.VIEW_INSTANCE_COMMON_NAME;
import ViewURLResourceProvider.VIEW_INSTANCE_NAME;
import ViewURLResourceProvider.VIEW_INSTANCE_VERSION;
import com.google.common.base.Optional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.predicate.EqualsPredicate;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.ViewURLDAO;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.orm.entities.ViewURLEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.view.ViewRegistry;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static ViewURLResourceProvider.URL_NAME;


public class ViewURLResourceProviderTest {
    private static final ViewRegistry viewregistry = createMock(ViewRegistry.class);

    @Test
    public void testToResource() throws Exception {
        ViewURLResourceProvider provider = new ViewURLResourceProvider();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(URL_NAME);
        propertyIds.add(URL_SUFFIX);
        ViewURLEntity viewURLEntity = createNiceMock(ViewURLEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        expect(viewURLEntity.getUrlName()).andReturn("test").once();
        expect(viewURLEntity.getUrlSuffix()).andReturn("url").once();
        expect(viewURLEntity.getViewInstanceEntity()).andReturn(viewInstanceEntity).once();
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).once();
        expect(viewEntity.getCommonName()).andReturn("FILES").once();
        expect(viewEntity.getVersion()).andReturn("1.0.0").once();
        expect(viewInstanceEntity.getName()).andReturn("test").once();
        replay(viewURLEntity, viewEntity, viewInstanceEntity);
        Resource resource = provider.toResource(viewURLEntity);
        Assert.assertEquals(resource.getPropertyValue(URL_NAME), "test");
        Assert.assertEquals(resource.getPropertyValue(URL_SUFFIX), "url");
        Assert.assertEquals(resource.getPropertyValue(VIEW_INSTANCE_NAME), "test");
        Assert.assertEquals(resource.getPropertyValue(VIEW_INSTANCE_VERSION), "1.0.0");
        Assert.assertEquals(resource.getPropertyValue(VIEW_INSTANCE_COMMON_NAME), "FILES");
        verify(viewURLEntity, viewInstanceEntity, viewEntity);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = SystemException.class)
    public void testCreateResources_existingUrlName() throws Exception {
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewURLResourceProvider provider = new ViewURLResourceProvider();
        ViewURLDAO viewURLDAO = createNiceMock(ViewURLDAO.class);
        ViewURLResourceProviderTest.setDao(ViewURLResourceProvider.class.getDeclaredField("viewURLDAO"), viewURLDAO);
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(URL_NAME, "test");
        propertyMap.put(URL_SUFFIX, "suffix");
        propertyMap.put(VIEW_INSTANCE_COMMON_NAME, "FILES");
        propertyMap.put(VIEW_INSTANCE_NAME, "test");
        propertyMap.put(VIEW_INSTANCE_VERSION, "1.0.0");
        expect(ViewURLResourceProviderTest.viewregistry.getInstanceDefinition("FILES", "1.0.0", "test")).andReturn(viewInstanceEntity);
        expect(ViewURLResourceProviderTest.viewregistry.getDefinition("FILES", "1.0.0")).andReturn(viewEntity);
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).once();
        expect(viewEntity.getCommonName()).andReturn("FILES").once();
        expect(viewEntity.isDeployed()).andReturn(true).once();
        expect(viewEntity.getVersion()).andReturn("1.0.0").once();
        expect(viewInstanceEntity.getName()).andReturn("test").once();
        expect(viewInstanceEntity.getViewUrl()).andReturn(null).once();
        expect(viewURLDAO.findByName("test")).andReturn(Optional.of(new ViewURLEntity()));
        replay(ViewURLResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity, viewURLDAO);
        properties.add(propertyMap);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.createResources(PropertyHelper.getCreateRequest(properties, null));
    }

    @Test(expected = SystemException.class)
    public void testCreateResources_existingUrlSuffix() throws Exception {
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewURLResourceProvider provider = new ViewURLResourceProvider();
        ViewURLDAO viewURLDAO = createNiceMock(ViewURLDAO.class);
        ViewURLResourceProviderTest.setDao(ViewURLResourceProvider.class.getDeclaredField("viewURLDAO"), viewURLDAO);
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(URL_NAME, "test");
        propertyMap.put(URL_SUFFIX, "suffix");
        propertyMap.put(VIEW_INSTANCE_COMMON_NAME, "FILES");
        propertyMap.put(VIEW_INSTANCE_NAME, "test");
        propertyMap.put(VIEW_INSTANCE_VERSION, "1.0.0");
        expect(ViewURLResourceProviderTest.viewregistry.getInstanceDefinition("FILES", "1.0.0", "test")).andReturn(viewInstanceEntity);
        expect(ViewURLResourceProviderTest.viewregistry.getDefinition("FILES", "1.0.0")).andReturn(viewEntity);
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).once();
        expect(viewEntity.getCommonName()).andReturn("FILES").once();
        expect(viewEntity.isDeployed()).andReturn(true).once();
        expect(viewEntity.getVersion()).andReturn("1.0.0").once();
        expect(viewInstanceEntity.getName()).andReturn("test").once();
        expect(viewInstanceEntity.getViewUrl()).andReturn(null).once();
        expect(viewURLDAO.findByName("test")).andReturn(Optional.absent());
        expect(viewURLDAO.findBySuffix("suffix")).andReturn(Optional.of(new ViewURLEntity()));
        replay(ViewURLResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity, viewURLDAO);
        properties.add(propertyMap);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.createResources(PropertyHelper.getCreateRequest(properties, null));
    }

    @Test
    public void testUpdateResources() throws Exception {
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewURLResourceProvider provider = new ViewURLResourceProvider();
        ViewURLEntity viewURLEntity = createNiceMock(ViewURLEntity.class);
        ViewURLDAO viewURLDAO = createNiceMock(ViewURLDAO.class);
        ViewURLResourceProviderTest.setDao(ViewURLResourceProvider.class.getDeclaredField("viewURLDAO"), viewURLDAO);
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(URL_NAME, "test");
        propertyMap.put(URL_SUFFIX, "suffix2");
        expect(viewURLDAO.findByName("test")).andReturn(Optional.of(viewURLEntity));
        expect(viewURLEntity.getViewInstanceEntity()).andReturn(viewInstanceEntity).once();
        expect(viewURLEntity.getUrlName()).andReturn("test").once();
        expect(viewURLEntity.getUrlSuffix()).andReturn("suffix2").once();
        expect(viewURLEntity.getViewInstanceEntity()).andReturn(viewInstanceEntity).once();
        viewURLEntity.setUrlSuffix("suffix2");
        Capture<ViewURLEntity> urlEntityCapture = newCapture();
        viewURLDAO.update(capture(urlEntityCapture));
        ViewURLResourceProviderTest.viewregistry.updateViewInstance(viewInstanceEntity);
        ViewURLResourceProviderTest.viewregistry.updateView(viewInstanceEntity);
        replay(ViewURLResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity, viewURLDAO, viewURLEntity);
        properties.add(propertyMap);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        PredicateBuilder predicateBuilder = new PredicateBuilder();
        Predicate predicate = predicateBuilder.property(URL_NAME).equals("test").toPredicate();
        provider.updateResources(PropertyHelper.getCreateRequest(properties, null), predicate);
        ViewURLEntity urlEntity = urlEntityCapture.getValue();
        Assert.assertEquals(urlEntity.getUrlName(), "test");
        Assert.assertEquals(urlEntity.getUrlSuffix(), "suffix2");
        Assert.assertEquals(urlEntity.getViewInstanceEntity(), viewInstanceEntity);
    }

    @Test
    public void testDeleteResources() throws Exception {
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewURLResourceProvider provider = new ViewURLResourceProvider();
        ViewURLEntity viewURLEntity = createNiceMock(ViewURLEntity.class);
        ViewURLDAO viewURLDAO = createNiceMock(ViewURLDAO.class);
        EqualsPredicate<String> equalsPredicate = new EqualsPredicate(URL_NAME, "test");
        ViewURLResourceProviderTest.setDao(ViewURLResourceProvider.class.getDeclaredField("viewURLDAO"), viewURLDAO);
        Set<Map<String, Object>> properties = new HashSet<>();
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(URL_NAME, "test");
        propertyMap.put(URL_SUFFIX, "suffix");
        expect(viewURLDAO.findByName("test")).andReturn(Optional.of(viewURLEntity));
        expect(viewURLEntity.getViewInstanceEntity()).andReturn(viewInstanceEntity).once();
        expect(viewURLEntity.getUrlName()).andReturn("test").once();
        expect(viewURLEntity.getUrlSuffix()).andReturn("suffix").once();
        expect(viewURLEntity.getViewInstanceEntity()).andReturn(viewInstanceEntity).once();
        viewURLEntity.setUrlSuffix("suffix");
        Capture<ViewURLEntity> urlEntityCapture = newCapture();
        viewInstanceEntity.clearUrl();
        viewURLEntity.clearEntity();
        viewURLDAO.delete(capture(urlEntityCapture));
        ViewURLResourceProviderTest.viewregistry.updateViewInstance(viewInstanceEntity);
        ViewURLResourceProviderTest.viewregistry.updateView(viewInstanceEntity);
        replay(ViewURLResourceProviderTest.viewregistry, viewEntity, viewInstanceEntity, viewURLDAO, viewURLEntity);
        properties.add(propertyMap);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.deleteResources(PropertyHelper.getCreateRequest(properties, null), equalsPredicate);
        ViewURLEntity urlEntity = urlEntityCapture.getValue();
        Assert.assertEquals(urlEntity.getUrlName(), "test");
        Assert.assertEquals(urlEntity.getUrlSuffix(), "suffix");
        Assert.assertEquals(urlEntity.getViewInstanceEntity(), viewInstanceEntity);
    }
}

