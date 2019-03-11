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


import AmbariPrivilegeResourceProvider.PERMISSION_LABEL;
import AmbariPrivilegeResourceProvider.PERMISSION_NAME;
import AmbariPrivilegeResourceProvider.PRINCIPAL_NAME;
import AmbariPrivilegeResourceProvider.PRINCIPAL_TYPE;
import PermissionEntity.VIEW_USER_PERMISSION;
import ViewDefinition.ViewStatus.DEPLOYED;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.GroupDAO;
import org.apache.ambari.server.orm.dao.MemberDAO;
import org.apache.ambari.server.orm.dao.PermissionDAO;
import org.apache.ambari.server.orm.dao.PrincipalDAO;
import org.apache.ambari.server.orm.dao.PrivilegeDAO;
import org.apache.ambari.server.orm.dao.ResourceDAO;
import org.apache.ambari.server.orm.dao.ResourceTypeDAO;
import org.apache.ambari.server.orm.dao.UserDAO;
import org.apache.ambari.server.orm.dao.ViewDAO;
import org.apache.ambari.server.orm.dao.ViewInstanceDAO;
import org.apache.ambari.server.orm.entities.PermissionEntity;
import org.apache.ambari.server.orm.entities.PrincipalEntity;
import org.apache.ambari.server.orm.entities.PrincipalTypeEntity;
import org.apache.ambari.server.orm.entities.PrivilegeEntity;
import org.apache.ambari.server.orm.entities.ResourceEntity;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.orm.entities.ViewEntityTest;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntityTest;
import org.apache.ambari.server.security.SecurityHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.view.ViewInstanceHandlerList;
import org.apache.ambari.server.view.ViewRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * ViewPrivilegeResourceProvider tests.
 */
public class ViewPrivilegeResourceProviderTest {
    private static final PrivilegeDAO privilegeDAO = createStrictMock(PrivilegeDAO.class);

    private static final UserDAO userDAO = createStrictMock(UserDAO.class);

    private static final GroupDAO groupDAO = createStrictMock(GroupDAO.class);

    private static final PrincipalDAO principalDAO = createStrictMock(PrincipalDAO.class);

    private static final PermissionDAO permissionDAO = createStrictMock(PermissionDAO.class);

    private static final ResourceDAO resourceDAO = createStrictMock(ResourceDAO.class);

    private static final ViewDAO viewDAO = createMock(ViewDAO.class);

    private static final ViewInstanceDAO viewInstanceDAO = createNiceMock(ViewInstanceDAO.class);

    private static final MemberDAO memberDAO = createNiceMock(MemberDAO.class);

    private static final ResourceTypeDAO resourceTypeDAO = createNiceMock(ResourceTypeDAO.class);

    private static final SecurityHelper securityHelper = createNiceMock(SecurityHelper.class);

    private static final ViewInstanceHandlerList handlerList = createNiceMock(ViewInstanceHandlerList.class);

    @Test
    public void testGetResources() throws Exception {
        ViewEntity viewDefinition = ViewEntityTest.getViewEntity();
        ViewInstanceEntity viewInstanceDefinition = ViewInstanceEntityTest.getViewInstanceEntity();
        viewDefinition.addInstanceDefinition(viewInstanceDefinition);
        viewInstanceDefinition.setViewEntity(viewDefinition);
        viewDefinition.setStatus(DEPLOYED);
        ViewRegistry registry = ViewRegistry.getInstance();
        registry.addDefinition(viewDefinition);
        registry.addInstanceDefinition(viewDefinition, viewInstanceDefinition);
        List<PrivilegeEntity> privilegeEntities = new LinkedList<>();
        PrivilegeEntity privilegeEntity = createNiceMock(PrivilegeEntity.class);
        ResourceEntity resourceEntity = createNiceMock(ResourceEntity.class);
        UserEntity userEntity = createNiceMock(UserEntity.class);
        PrincipalEntity principalEntity = createNiceMock(PrincipalEntity.class);
        PrincipalTypeEntity principalTypeEntity = createNiceMock(PrincipalTypeEntity.class);
        PermissionEntity permissionEntity = createNiceMock(PermissionEntity.class);
        List<PrincipalEntity> principalEntities = new LinkedList<>();
        principalEntities.add(principalEntity);
        List<UserEntity> userEntities = new LinkedList<>();
        userEntities.add(userEntity);
        privilegeEntities.add(privilegeEntity);
        expect(ViewPrivilegeResourceProviderTest.privilegeDAO.findAll()).andReturn(privilegeEntities);
        expect(privilegeEntity.getResource()).andReturn(resourceEntity).anyTimes();
        expect(privilegeEntity.getPrincipal()).andReturn(principalEntity).anyTimes();
        expect(privilegeEntity.getPermission()).andReturn(permissionEntity).anyTimes();
        expect(resourceEntity.getId()).andReturn(20L).anyTimes();
        expect(principalEntity.getId()).andReturn(20L).anyTimes();
        expect(userEntity.getPrincipal()).andReturn(principalEntity).anyTimes();
        expect(userEntity.getUserName()).andReturn("joe").anyTimes();
        expect(permissionEntity.getPermissionName()).andReturn("VIEW.USER").anyTimes();
        expect(permissionEntity.getPermissionLabel()).andReturn("View User").anyTimes();
        expect(principalEntity.getPrincipalType()).andReturn(principalTypeEntity).anyTimes();
        expect(principalTypeEntity.getName()).andReturn("USER").anyTimes();
        expect(ViewPrivilegeResourceProviderTest.permissionDAO.findById(VIEW_USER_PERMISSION)).andReturn(permissionEntity);
        expect(ViewPrivilegeResourceProviderTest.userDAO.findUsersByPrincipal(principalEntities)).andReturn(userEntities);
        replay(ViewPrivilegeResourceProviderTest.privilegeDAO, ViewPrivilegeResourceProviderTest.userDAO, ViewPrivilegeResourceProviderTest.groupDAO, ViewPrivilegeResourceProviderTest.principalDAO, ViewPrivilegeResourceProviderTest.permissionDAO, ViewPrivilegeResourceProviderTest.resourceDAO, privilegeEntity, resourceEntity, userEntity, principalEntity, permissionEntity, principalTypeEntity);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("admin"));
        PrivilegeResourceProvider provider = new ViewPrivilegeResourceProvider();
        Set<Resource> resources = provider.getResources(PropertyHelper.getReadRequest(), null);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals("VIEW.USER", resource.getPropertyValue(PERMISSION_NAME));
        Assert.assertEquals("View User", resource.getPropertyValue(PERMISSION_LABEL));
        Assert.assertEquals("joe", resource.getPropertyValue(PRINCIPAL_NAME));
        Assert.assertEquals("USER", resource.getPropertyValue(PRINCIPAL_TYPE));
        verify(ViewPrivilegeResourceProviderTest.privilegeDAO, ViewPrivilegeResourceProviderTest.userDAO, ViewPrivilegeResourceProviderTest.groupDAO, ViewPrivilegeResourceProviderTest.principalDAO, ViewPrivilegeResourceProviderTest.permissionDAO, ViewPrivilegeResourceProviderTest.resourceDAO, privilegeEntity, resourceEntity, userEntity, principalEntity, permissionEntity, principalTypeEntity);
    }
}

