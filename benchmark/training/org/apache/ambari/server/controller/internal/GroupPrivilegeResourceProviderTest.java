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


import ClusterPrivilegeResourceProvider.CLUSTER_NAME;
import GroupPrivilegeResourceProvider.TYPE;
import ResourceType.AMBARI;
import ResourceType.CLUSTER;
import ResourceType.VIEW;
import ViewPrivilegeResourceProvider.INSTANCE_NAME;
import ViewPrivilegeResourceProvider.VERSION;
import ViewPrivilegeResourceProvider.VIEW_NAME;
import junit.framework.Assert;
import org.apache.ambari.server.controller.GroupPrivilegeResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.orm.dao.ClusterDAO;
import org.apache.ambari.server.orm.dao.GroupDAO;
import org.apache.ambari.server.orm.dao.ViewInstanceDAO;
import org.apache.ambari.server.orm.entities.ClusterEntity;
import org.apache.ambari.server.orm.entities.GroupEntity;
import org.apache.ambari.server.orm.entities.PermissionEntity;
import org.apache.ambari.server.orm.entities.PrincipalEntity;
import org.apache.ambari.server.orm.entities.PrincipalTypeEntity;
import org.apache.ambari.server.orm.entities.PrivilegeEntity;
import org.apache.ambari.server.orm.entities.ResourceEntity;
import org.apache.ambari.server.orm.entities.ResourceTypeEntity;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.security.authorization.Users;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * GroupPrivilegeResourceProvider tests.
 */
public class GroupPrivilegeResourceProviderTest extends EasyMockSupport {
    @Test(expected = SystemException.class)
    public void testCreateResources() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        GroupPrivilegeResourceProvider resourceProvider = new GroupPrivilegeResourceProvider();
        resourceProvider.createResources(createNiceMock(Request.class));
    }

    @Test
    public void testGetResources_Administrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "Group1");
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_NonAdministrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("user1", 2L), "Group1");
    }

    @Test(expected = SystemException.class)
    public void testUpdateResources() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        GroupPrivilegeResourceProvider resourceProvider = new GroupPrivilegeResourceProvider();
        resourceProvider.updateResources(createNiceMock(Request.class), createNiceMock(Predicate.class));
    }

    @Test(expected = SystemException.class)
    public void testDeleteResources() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createClusterAdministrator("user1", 2L));
        GroupPrivilegeResourceProvider resourceProvider = new GroupPrivilegeResourceProvider();
        resourceProvider.deleteResources(new RequestImpl(null, null, null, null), createNiceMock(Predicate.class));
    }

    @Test
    public void testToResource_AMBARI() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Ambari Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("GROUP").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getPrincipalType()).andReturn(principalTypeEntity).atLeastOnce();
        ResourceTypeEntity resourceTypeEntity = createMock(ResourceTypeEntity.class);
        expect(resourceTypeEntity.getName()).andReturn("AMBARI").atLeastOnce();
        ResourceEntity resourceEntity = createMock(ResourceEntity.class);
        expect(resourceEntity.getResourceType()).andReturn(resourceTypeEntity).atLeastOnce();
        PrivilegeEntity privilegeEntity = createMock(PrivilegeEntity.class);
        expect(privilegeEntity.getId()).andReturn(1).atLeastOnce();
        expect(privilegeEntity.getPermission()).andReturn(permissionEntity).atLeastOnce();
        expect(privilegeEntity.getPrincipal()).andReturn(principalEntity).atLeastOnce();
        expect(privilegeEntity.getResource()).andReturn(resourceEntity).atLeastOnce();
        GroupEntity groupEntity = createMock(GroupEntity.class);
        expect(groupEntity.getGroupName()).andReturn("group1").atLeastOnce();
        GroupDAO groupDAO = createMock(GroupDAO.class);
        expect(groupDAO.findGroupByPrincipal(anyObject(PrincipalEntity.class))).andReturn(groupEntity).anyTimes();
        ClusterDAO clusterDAO = createMock(ClusterDAO.class);
        ViewInstanceDAO viewInstanceDAO = createMock(ViewInstanceDAO.class);
        Users users = createNiceMock(Users.class);
        replayAll();
        GroupPrivilegeResourceProvider.init(clusterDAO, groupDAO, viewInstanceDAO, users);
        GroupPrivilegeResourceProvider provider = new GroupPrivilegeResourceProvider();
        GroupPrivilegeResponse response = provider.getResponse(privilegeEntity, "group1");
        Resource resource = provider.toResource(response, provider.getPropertyIds());
        Assert.assertEquals(AMBARI.name(), resource.getPropertyValue(TYPE));
        verifyAll();
    }

    @Test
    public void testToResource_CLUSTER() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("GROUP").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getPrincipalType()).andReturn(principalTypeEntity).atLeastOnce();
        ClusterEntity clusterEntity = createMock(ClusterEntity.class);
        expect(clusterEntity.getClusterName()).andReturn("TestCluster").atLeastOnce();
        ResourceTypeEntity resourceTypeEntity = createMock(ResourceTypeEntity.class);
        expect(resourceTypeEntity.getName()).andReturn("CLUSTER").atLeastOnce();
        ResourceEntity resourceEntity = createMock(ResourceEntity.class);
        expect(resourceEntity.getId()).andReturn(1L).atLeastOnce();
        expect(resourceEntity.getResourceType()).andReturn(resourceTypeEntity).atLeastOnce();
        PrivilegeEntity privilegeEntity = createMock(PrivilegeEntity.class);
        expect(privilegeEntity.getId()).andReturn(1).atLeastOnce();
        expect(privilegeEntity.getPermission()).andReturn(permissionEntity).atLeastOnce();
        expect(privilegeEntity.getPrincipal()).andReturn(principalEntity).atLeastOnce();
        expect(privilegeEntity.getResource()).andReturn(resourceEntity).atLeastOnce();
        GroupEntity groupEntity = createMock(GroupEntity.class);
        expect(groupEntity.getGroupName()).andReturn("group1").atLeastOnce();
        ClusterDAO clusterDAO = createMock(ClusterDAO.class);
        expect(clusterDAO.findByResourceId(1L)).andReturn(clusterEntity).atLeastOnce();
        ViewInstanceDAO viewInstanceDAO = createMock(ViewInstanceDAO.class);
        GroupDAO groupDAO = createMock(GroupDAO.class);
        expect(groupDAO.findGroupByPrincipal(anyObject(PrincipalEntity.class))).andReturn(groupEntity).anyTimes();
        Users users = createNiceMock(Users.class);
        replayAll();
        GroupPrivilegeResourceProvider.init(clusterDAO, groupDAO, viewInstanceDAO, users);
        GroupPrivilegeResourceProvider provider = new GroupPrivilegeResourceProvider();
        GroupPrivilegeResponse response = provider.getResponse(privilegeEntity, "group1");
        Resource resource = provider.toResource(response, provider.getPropertyIds());
        Assert.assertEquals("TestCluster", resource.getPropertyValue(CLUSTER_NAME));
        Assert.assertEquals(CLUSTER.name(), resource.getPropertyValue(TYPE));
        verifyAll();
    }

    @Test
    public void testToResource_VIEW() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("GROUP").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getPrincipalType()).andReturn(principalTypeEntity).atLeastOnce();
        ViewEntity viewEntity = createMock(ViewEntity.class);
        expect(viewEntity.getCommonName()).andReturn("TestView").atLeastOnce();
        expect(viewEntity.getVersion()).andReturn("1.2.3.4").atLeastOnce();
        ViewInstanceEntity viewInstanceEntity = createMock(ViewInstanceEntity.class);
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).atLeastOnce();
        expect(viewInstanceEntity.getName()).andReturn("Test View").atLeastOnce();
        ResourceTypeEntity resourceTypeEntity = createMock(ResourceTypeEntity.class);
        expect(resourceTypeEntity.getName()).andReturn("VIEW").atLeastOnce();
        ResourceEntity resourceEntity = createMock(ResourceEntity.class);
        expect(resourceEntity.getId()).andReturn(1L).atLeastOnce();
        expect(resourceEntity.getResourceType()).andReturn(resourceTypeEntity).atLeastOnce();
        PrivilegeEntity privilegeEntity = createMock(PrivilegeEntity.class);
        expect(privilegeEntity.getId()).andReturn(1).atLeastOnce();
        expect(privilegeEntity.getPermission()).andReturn(permissionEntity).atLeastOnce();
        expect(privilegeEntity.getPrincipal()).andReturn(principalEntity).atLeastOnce();
        expect(privilegeEntity.getResource()).andReturn(resourceEntity).atLeastOnce();
        GroupEntity groupEntity = createMock(GroupEntity.class);
        expect(groupEntity.getGroupName()).andReturn("group1").atLeastOnce();
        ClusterDAO clusterDAO = createMock(ClusterDAO.class);
        ViewInstanceDAO viewInstanceDAO = createMock(ViewInstanceDAO.class);
        expect(viewInstanceDAO.findByResourceId(1L)).andReturn(viewInstanceEntity).atLeastOnce();
        GroupDAO groupDAO = createMock(GroupDAO.class);
        expect(groupDAO.findGroupByPrincipal(anyObject(PrincipalEntity.class))).andReturn(groupEntity).anyTimes();
        Users users = createNiceMock(Users.class);
        replayAll();
        GroupPrivilegeResourceProvider.init(clusterDAO, groupDAO, viewInstanceDAO, users);
        GroupPrivilegeResourceProvider provider = new GroupPrivilegeResourceProvider();
        GroupPrivilegeResponse response = provider.getResponse(privilegeEntity, "group1");
        Resource resource = provider.toResource(response, provider.getPropertyIds());
        Assert.assertEquals("Test View", resource.getPropertyValue(INSTANCE_NAME));
        Assert.assertEquals("TestView", resource.getPropertyValue(VIEW_NAME));
        Assert.assertEquals("1.2.3.4", resource.getPropertyValue(VERSION));
        Assert.assertEquals(VIEW.name(), resource.getPropertyValue(TYPE));
        verifyAll();
    }

    @Test
    public void testToResource_SpecificVIEW() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("GROUP").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getPrincipalType()).andReturn(principalTypeEntity).atLeastOnce();
        ViewEntity viewEntity = createMock(ViewEntity.class);
        expect(viewEntity.getCommonName()).andReturn("TestView").atLeastOnce();
        expect(viewEntity.getVersion()).andReturn("1.2.3.4").atLeastOnce();
        ViewInstanceEntity viewInstanceEntity = createMock(ViewInstanceEntity.class);
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).atLeastOnce();
        expect(viewInstanceEntity.getName()).andReturn("Test View").atLeastOnce();
        ResourceTypeEntity resourceTypeEntity = createMock(ResourceTypeEntity.class);
        expect(resourceTypeEntity.getName()).andReturn("TestView{1.2.3.4}").atLeastOnce();
        ResourceEntity resourceEntity = createMock(ResourceEntity.class);
        expect(resourceEntity.getId()).andReturn(1L).atLeastOnce();
        expect(resourceEntity.getResourceType()).andReturn(resourceTypeEntity).atLeastOnce();
        PrivilegeEntity privilegeEntity = createMock(PrivilegeEntity.class);
        expect(privilegeEntity.getId()).andReturn(1).atLeastOnce();
        expect(privilegeEntity.getPermission()).andReturn(permissionEntity).atLeastOnce();
        expect(privilegeEntity.getPrincipal()).andReturn(principalEntity).atLeastOnce();
        expect(privilegeEntity.getResource()).andReturn(resourceEntity).atLeastOnce();
        GroupEntity groupEntity = createMock(GroupEntity.class);
        expect(groupEntity.getGroupName()).andReturn("group1").atLeastOnce();
        ClusterDAO clusterDAO = createMock(ClusterDAO.class);
        ViewInstanceDAO viewInstanceDAO = createMock(ViewInstanceDAO.class);
        expect(viewInstanceDAO.findByResourceId(1L)).andReturn(viewInstanceEntity).atLeastOnce();
        GroupDAO groupDAO = createMock(GroupDAO.class);
        expect(groupDAO.findGroupByPrincipal(anyObject(PrincipalEntity.class))).andReturn(groupEntity).anyTimes();
        Users users = createNiceMock(Users.class);
        replayAll();
        GroupPrivilegeResourceProvider.init(clusterDAO, groupDAO, viewInstanceDAO, users);
        GroupPrivilegeResourceProvider provider = new GroupPrivilegeResourceProvider();
        GroupPrivilegeResponse response = provider.getResponse(privilegeEntity, "group1");
        Resource resource = provider.toResource(response, provider.getPropertyIds());
        Assert.assertEquals("Test View", resource.getPropertyValue(INSTANCE_NAME));
        Assert.assertEquals("TestView", resource.getPropertyValue(VIEW_NAME));
        Assert.assertEquals("1.2.3.4", resource.getPropertyValue(VERSION));
        Assert.assertEquals(VIEW.name(), resource.getPropertyValue(TYPE));
        verifyAll();
    }
}

