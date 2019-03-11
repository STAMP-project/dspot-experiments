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


import PermissionResourceProvider.PERMISSION_ID_PROPERTY_ID;
import PermissionResourceProvider.PERMISSION_NAME_PROPERTY_ID;
import PermissionResourceProvider.RESOURCE_NAME_PROPERTY_ID;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.PermissionDAO;
import org.apache.ambari.server.orm.entities.PermissionEntity;
import org.apache.ambari.server.orm.entities.ResourceTypeEntity;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.view.ViewRegistry;
import org.junit.Assert;
import org.junit.Test;


/**
 * ViewPermissionResourceProvider tests.
 */
public class ViewPermissionResourceProviderTest {
    private static final PermissionDAO dao = createStrictMock(PermissionDAO.class);

    private static final ViewRegistry viewRegistry = createMock(ViewRegistry.class);

    @Test
    public void testGetResources() throws Exception {
        List<PermissionEntity> permissionEntities = new LinkedList<>();
        PermissionEntity permissionEntity = createNiceMock(PermissionEntity.class);
        PermissionEntity viewUsePermissionEntity = createNiceMock(PermissionEntity.class);
        ResourceTypeEntity resourceTypeEntity = createNiceMock(ResourceTypeEntity.class);
        ViewEntity viewEntity = createMock(ViewEntity.class);
        permissionEntities.add(permissionEntity);
        expect(ViewPermissionResourceProviderTest.dao.findViewUsePermission()).andReturn(viewUsePermissionEntity);
        expect(ViewPermissionResourceProviderTest.dao.findAll()).andReturn(Collections.singletonList(permissionEntity));
        expect(permissionEntity.getId()).andReturn(99);
        expect(permissionEntity.getPermissionName()).andReturn("P1");
        expect(permissionEntity.getResourceType()).andReturn(resourceTypeEntity);
        expect(resourceTypeEntity.getName()).andReturn("V1");
        expect(viewEntity.isDeployed()).andReturn(true).anyTimes();
        expect(viewEntity.getCommonName()).andReturn("V1").anyTimes();
        expect(viewEntity.getVersion()).andReturn("1.0.0").anyTimes();
        expect(ViewPermissionResourceProviderTest.viewRegistry.getDefinition(resourceTypeEntity)).andReturn(viewEntity);
        replay(ViewPermissionResourceProviderTest.dao, permissionEntity, viewUsePermissionEntity, resourceTypeEntity, viewEntity, ViewPermissionResourceProviderTest.viewRegistry);
        ViewPermissionResourceProvider provider = new ViewPermissionResourceProvider();
        Set<Resource> resources = provider.getResources(PropertyHelper.getReadRequest(), null);
        // built in permissions
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals(99, resource.getPropertyValue(PERMISSION_ID_PROPERTY_ID));
        Assert.assertEquals("P1", resource.getPropertyValue(PERMISSION_NAME_PROPERTY_ID));
        Assert.assertEquals("V1", resource.getPropertyValue(RESOURCE_NAME_PROPERTY_ID));
        verify(ViewPermissionResourceProviderTest.dao, permissionEntity, viewUsePermissionEntity, resourceTypeEntity, viewEntity, ViewPermissionResourceProviderTest.viewRegistry);
    }

    @Test
    public void testGetResources_viewNotLoaded() throws Exception {
        List<PermissionEntity> permissionEntities = new LinkedList<>();
        PermissionEntity permissionEntity = createNiceMock(PermissionEntity.class);
        PermissionEntity viewUsePermissionEntity = createNiceMock(PermissionEntity.class);
        ResourceTypeEntity resourceTypeEntity = createNiceMock(ResourceTypeEntity.class);
        ViewEntity viewEntity = createMock(ViewEntity.class);
        permissionEntities.add(permissionEntity);
        expect(ViewPermissionResourceProviderTest.dao.findViewUsePermission()).andReturn(viewUsePermissionEntity);
        expect(ViewPermissionResourceProviderTest.dao.findAll()).andReturn(Collections.singletonList(permissionEntity));
        expect(permissionEntity.getResourceType()).andReturn(resourceTypeEntity);
        expect(viewEntity.isDeployed()).andReturn(false).anyTimes();
        expect(ViewPermissionResourceProviderTest.viewRegistry.getDefinition(resourceTypeEntity)).andReturn(viewEntity);
        replay(ViewPermissionResourceProviderTest.dao, permissionEntity, viewUsePermissionEntity, resourceTypeEntity, viewEntity, ViewPermissionResourceProviderTest.viewRegistry);
        ViewPermissionResourceProvider provider = new ViewPermissionResourceProvider();
        Set<Resource> resources = provider.getResources(PropertyHelper.getReadRequest(), null);
        // built in permissions
        Assert.assertEquals(0, resources.size());
        verify(ViewPermissionResourceProviderTest.dao, permissionEntity, viewUsePermissionEntity, resourceTypeEntity, viewEntity, ViewPermissionResourceProviderTest.viewRegistry);
    }
}

