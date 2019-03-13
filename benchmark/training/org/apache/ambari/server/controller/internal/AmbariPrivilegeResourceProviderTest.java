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
import AmbariPrivilegeResourceProvider.PRIVILEGE_ID;
import AmbariPrivilegeResourceProvider.TYPE;
import ClusterPrivilegeResourceProvider.CLUSTER_NAME;
import ResourceType.AMBARI;
import ResourceType.CLUSTER;
import ResourceType.VIEW;
import ViewPrivilegeResourceProvider.INSTANCE_NAME;
import ViewPrivilegeResourceProvider.VERSION;
import ViewPrivilegeResourceProvider.VIEW_NAME;
import com.google.inject.Injector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.ClusterDAO;
import org.apache.ambari.server.orm.dao.PrivilegeDAO;
import org.apache.ambari.server.orm.dao.UserDAO;
import org.apache.ambari.server.orm.entities.ClusterEntity;
import org.apache.ambari.server.orm.entities.GroupEntity;
import org.apache.ambari.server.orm.entities.PermissionEntity;
import org.apache.ambari.server.orm.entities.PrincipalEntity;
import org.apache.ambari.server.orm.entities.PrincipalTypeEntity;
import org.apache.ambari.server.orm.entities.PrivilegeEntity;
import org.apache.ambari.server.orm.entities.ResourceEntity;
import org.apache.ambari.server.orm.entities.ResourceTypeEntity;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.orm.entities.ViewEntity;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.view.ViewRegistry;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * AmbariPrivilegeResourceProvider tests.
 */
public class AmbariPrivilegeResourceProviderTest extends EasyMockSupport {
    @Test
    public void testCreateResources_Administrator() throws Exception {
        createResourcesTest(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResources_NonAdministrator() throws Exception {
        createResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testGetResources_Administrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResources_NonAdministrator() throws Exception {
        getResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testGetResource_Administrator_Self() throws Exception {
        getResourceTest(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testGetResource_Administrator_Other() throws Exception {
        getResourceTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResource_NonAdministrator_Self() throws Exception {
        getResourceTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testGetResource_NonAdministrator_Other() throws Exception {
        getResourceTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }

    @Test
    public void testUpdateResources_Administrator_Self() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "admin");
    }

    @Test
    public void testUpdateResources_Administrator_Other() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createAdministrator("admin"), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_NonAdministrator_Self() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User1");
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResources_NonAdministrator_Other() throws Exception {
        updateResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L), "User10");
    }

    @Test
    public void testDeleteResources_Administrator() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createAdministrator("admin"));
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResources_NonAdministrator() throws Exception {
        deleteResourcesTest(TestAuthenticationFactory.createClusterAdministrator("User1", 2L));
    }

    @Test
    public void testGetResources_allTypes() throws Exception {
        Injector injector = createInjector();
        PrivilegeEntity ambariPrivilegeEntity = createNiceMock(PrivilegeEntity.class);
        ResourceEntity ambariResourceEntity = createNiceMock(ResourceEntity.class);
        ResourceTypeEntity ambariResourceTypeEntity = createNiceMock(ResourceTypeEntity.class);
        UserEntity ambariUserEntity = createNiceMock(UserEntity.class);
        PrincipalEntity ambariPrincipalEntity = createNiceMock(PrincipalEntity.class);
        PrincipalTypeEntity ambariPrincipalTypeEntity = createNiceMock(PrincipalTypeEntity.class);
        PermissionEntity ambariPermissionEntity = createNiceMock(PermissionEntity.class);
        expect(ambariPrivilegeEntity.getResource()).andReturn(ambariResourceEntity).anyTimes();
        expect(ambariPrivilegeEntity.getId()).andReturn(31).anyTimes();
        expect(ambariPrivilegeEntity.getPrincipal()).andReturn(ambariPrincipalEntity).anyTimes();
        expect(ambariPrivilegeEntity.getPermission()).andReturn(ambariPermissionEntity).anyTimes();
        expect(ambariResourceEntity.getResourceType()).andReturn(ambariResourceTypeEntity).anyTimes();
        expect(ambariResourceTypeEntity.getId()).andReturn(AMBARI.getId()).anyTimes();
        expect(ambariResourceTypeEntity.getName()).andReturn(AMBARI.name()).anyTimes();
        expect(ambariPrincipalEntity.getId()).andReturn(1L).anyTimes();
        expect(ambariUserEntity.getPrincipal()).andReturn(ambariPrincipalEntity).anyTimes();
        expect(ambariUserEntity.getUserName()).andReturn("joe").anyTimes();
        expect(ambariPermissionEntity.getPermissionName()).andReturn("AMBARI.ADMINISTRATOR").anyTimes();
        expect(ambariPermissionEntity.getPermissionLabel()).andReturn("Ambari Administrator").anyTimes();
        expect(ambariPrincipalEntity.getPrincipalType()).andReturn(ambariPrincipalTypeEntity).anyTimes();
        expect(ambariPrincipalTypeEntity.getName()).andReturn("USER").anyTimes();
        PrivilegeEntity viewPrivilegeEntity = createNiceMock(PrivilegeEntity.class);
        ResourceEntity viewResourceEntity = createNiceMock(ResourceEntity.class);
        ViewEntity viewEntity = createNiceMock(ViewEntity.class);
        ViewInstanceEntity viewInstanceEntity = createNiceMock(ViewInstanceEntity.class);
        ResourceTypeEntity viewResourceTypeEntity = createNiceMock(ResourceTypeEntity.class);
        UserEntity viewUserEntity = createNiceMock(UserEntity.class);
        PrincipalEntity viewPrincipalEntity = createNiceMock(PrincipalEntity.class);
        PrincipalTypeEntity viewPrincipalTypeEntity = createNiceMock(PrincipalTypeEntity.class);
        PermissionEntity viewPermissionEntity = createNiceMock(PermissionEntity.class);
        expect(viewPrivilegeEntity.getResource()).andReturn(viewResourceEntity).anyTimes();
        expect(viewPrivilegeEntity.getPrincipal()).andReturn(viewPrincipalEntity).anyTimes();
        expect(viewPrivilegeEntity.getPermission()).andReturn(viewPermissionEntity).anyTimes();
        expect(viewPrivilegeEntity.getId()).andReturn(33).anyTimes();
        expect(viewResourceEntity.getResourceType()).andReturn(viewResourceTypeEntity).anyTimes();
        expect(viewResourceTypeEntity.getId()).andReturn(VIEW.getId()).anyTimes();
        expect(viewResourceTypeEntity.getName()).andReturn(VIEW.name()).anyTimes();
        expect(viewPrincipalEntity.getId()).andReturn(5L).anyTimes();
        expect(viewEntity.getInstances()).andReturn(Arrays.asList(viewInstanceEntity)).anyTimes();
        expect(viewInstanceEntity.getViewEntity()).andReturn(viewEntity).anyTimes();
        expect(viewEntity.getCommonName()).andReturn("view").anyTimes();
        expect(viewEntity.getVersion()).andReturn("1.0.1").anyTimes();
        expect(viewEntity.isDeployed()).andReturn(true).anyTimes();
        expect(viewInstanceEntity.getName()).andReturn("inst1").anyTimes();
        expect(viewInstanceEntity.getResource()).andReturn(viewResourceEntity).anyTimes();
        expect(viewUserEntity.getPrincipal()).andReturn(viewPrincipalEntity).anyTimes();
        expect(viewUserEntity.getUserName()).andReturn("bob").anyTimes();
        expect(viewPermissionEntity.getPermissionName()).andReturn("VIEW.USER").anyTimes();
        expect(viewPermissionEntity.getPermissionLabel()).andReturn("View User").anyTimes();
        expect(viewPrincipalEntity.getPrincipalType()).andReturn(viewPrincipalTypeEntity).anyTimes();
        expect(viewPrincipalTypeEntity.getName()).andReturn("USER").anyTimes();
        PrivilegeEntity clusterPrivilegeEntity = createNiceMock(PrivilegeEntity.class);
        ResourceEntity clusterResourceEntity = createNiceMock(ResourceEntity.class);
        ResourceTypeEntity clusterResourceTypeEntity = createNiceMock(ResourceTypeEntity.class);
        UserEntity clusterUserEntity = createNiceMock(UserEntity.class);
        PrincipalEntity clusterPrincipalEntity = createNiceMock(PrincipalEntity.class);
        PrincipalTypeEntity clusterPrincipalTypeEntity = createNiceMock(PrincipalTypeEntity.class);
        PermissionEntity clusterPermissionEntity = createNiceMock(PermissionEntity.class);
        ClusterEntity clusterEntity = createNiceMock(ClusterEntity.class);
        expect(clusterPrivilegeEntity.getResource()).andReturn(clusterResourceEntity).anyTimes();
        expect(clusterPrivilegeEntity.getPrincipal()).andReturn(clusterPrincipalEntity).anyTimes();
        expect(clusterPrivilegeEntity.getPermission()).andReturn(clusterPermissionEntity).anyTimes();
        expect(clusterPrivilegeEntity.getId()).andReturn(32).anyTimes();
        expect(clusterResourceEntity.getId()).andReturn(7L).anyTimes();
        expect(clusterResourceEntity.getResourceType()).andReturn(clusterResourceTypeEntity).anyTimes();
        expect(clusterResourceTypeEntity.getId()).andReturn(CLUSTER.getId()).anyTimes();
        expect(clusterResourceTypeEntity.getName()).andReturn(CLUSTER.name()).anyTimes();
        expect(clusterPrincipalEntity.getId()).andReturn(8L).anyTimes();
        expect(clusterUserEntity.getPrincipal()).andReturn(clusterPrincipalEntity).anyTimes();
        expect(clusterUserEntity.getUserName()).andReturn("jeff").anyTimes();
        expect(clusterPermissionEntity.getPermissionName()).andReturn("CLUSTER.USER").anyTimes();
        expect(clusterPermissionEntity.getPermissionLabel()).andReturn("Cluster User").anyTimes();
        expect(clusterPrincipalEntity.getPrincipalType()).andReturn(clusterPrincipalTypeEntity).anyTimes();
        expect(clusterPrincipalTypeEntity.getName()).andReturn("USER").anyTimes();
        expect(clusterEntity.getResource()).andReturn(clusterResourceEntity).anyTimes();
        expect(clusterEntity.getClusterName()).andReturn("cluster1").anyTimes();
        List<UserEntity> userEntities = new LinkedList<>();
        userEntities.add(ambariUserEntity);
        userEntities.add(viewUserEntity);
        userEntities.add(clusterUserEntity);
        List<PrivilegeEntity> privilegeEntities = new LinkedList<>();
        privilegeEntities.add(ambariPrivilegeEntity);
        privilegeEntities.add(viewPrivilegeEntity);
        privilegeEntities.add(clusterPrivilegeEntity);
        List<ClusterEntity> clusterEntities = new LinkedList<>();
        clusterEntities.add(clusterEntity);
        ClusterDAO clusterDAO = injector.getInstance(ClusterDAO.class);
        expect(clusterDAO.findAll()).andReturn(clusterEntities).atLeastOnce();
        PrivilegeDAO privilegeDAO = injector.getInstance(PrivilegeDAO.class);
        expect(privilegeDAO.findAll()).andReturn(privilegeEntities).atLeastOnce();
        UserDAO userDAO = injector.getInstance(UserDAO.class);
        expect(userDAO.findUsersByPrincipal(EasyMock.anyObject())).andReturn(userEntities).atLeastOnce();
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator("admin"));
        ResourceProvider provider = getResourceProvider(injector);
        ViewRegistry.getInstance().addDefinition(viewEntity);
        Set<Resource> resources = provider.getResources(PropertyHelper.getReadRequest(), null);
        Assert.assertEquals(3, resources.size());
        Map<Object, Resource> resourceMap = new HashMap<>();
        for (Resource resource : resources) {
            resourceMap.put(resource.getPropertyValue(PRIVILEGE_ID), resource);
        }
        Resource resource1 = resourceMap.get(31);
        Assert.assertEquals(6, resource1.getPropertiesMap().get("PrivilegeInfo").size());
        Assert.assertEquals("AMBARI.ADMINISTRATOR", resource1.getPropertyValue(PERMISSION_NAME));
        Assert.assertEquals("Ambari Administrator", resource1.getPropertyValue(PERMISSION_LABEL));
        Assert.assertEquals("joe", resource1.getPropertyValue(PRINCIPAL_NAME));
        Assert.assertEquals("USER", resource1.getPropertyValue(PRINCIPAL_TYPE));
        Assert.assertEquals(31, resource1.getPropertyValue(PRIVILEGE_ID));
        Assert.assertEquals("AMBARI", resource1.getPropertyValue(TYPE));
        Resource resource2 = resourceMap.get(32);
        Assert.assertEquals(7, resource2.getPropertiesMap().get("PrivilegeInfo").size());
        Assert.assertEquals("CLUSTER.USER", resource2.getPropertyValue(PERMISSION_NAME));
        Assert.assertEquals("Cluster User", resource2.getPropertyValue(PERMISSION_LABEL));
        Assert.assertEquals("jeff", resource2.getPropertyValue(PRINCIPAL_NAME));
        Assert.assertEquals("USER", resource2.getPropertyValue(PRINCIPAL_TYPE));
        Assert.assertEquals(32, resource2.getPropertyValue(PRIVILEGE_ID));
        Assert.assertEquals("cluster1", resource2.getPropertyValue(CLUSTER_NAME));
        Assert.assertEquals("CLUSTER", resource2.getPropertyValue(TYPE));
        Resource resource3 = resourceMap.get(33);
        Assert.assertEquals(9, resource3.getPropertiesMap().get("PrivilegeInfo").size());
        Assert.assertEquals("VIEW.USER", resource3.getPropertyValue(PERMISSION_NAME));
        Assert.assertEquals("View User", resource3.getPropertyValue(PERMISSION_LABEL));
        Assert.assertEquals("bob", resource3.getPropertyValue(PRINCIPAL_NAME));
        Assert.assertEquals("USER", resource3.getPropertyValue(PRINCIPAL_TYPE));
        Assert.assertEquals(33, resource3.getPropertyValue(PRIVILEGE_ID));
        Assert.assertEquals("view", resource3.getPropertyValue(VIEW_NAME));
        Assert.assertEquals("1.0.1", resource3.getPropertyValue(VERSION));
        Assert.assertEquals("inst1", resource3.getPropertyValue(INSTANCE_NAME));
        Assert.assertEquals("VIEW", resource3.getPropertyValue(TYPE));
        verifyAll();
    }

    @Test
    public void testToResource_AMBARI() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Ambari Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("USER").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getId()).andReturn(1L).atLeastOnce();
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
        replay(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, resourceEntity, privilegeEntity);
        Map<Long, UserEntity> userEntities = new HashMap<>();
        Map<Long, GroupEntity> groupEntities = new HashMap<>();
        Map<Long, PermissionEntity> roleEntities = new HashMap<>();
        Map<Long, Object> resourceEntities = new HashMap<>();
        AmbariPrivilegeResourceProvider provider = new AmbariPrivilegeResourceProvider();
        Resource resource = provider.toResource(privilegeEntity, userEntities, groupEntities, roleEntities, resourceEntities, provider.getPropertyIds());
        Assert.assertEquals(AMBARI.name(), resource.getPropertyValue(TYPE));
        verify(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, resourceEntity, privilegeEntity);
    }

    @Test
    public void testToResource_CLUSTER() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("USER").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getId()).andReturn(1L).atLeastOnce();
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
        replay(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, clusterEntity, resourceEntity, privilegeEntity);
        Map<Long, UserEntity> userEntities = new HashMap<>();
        Map<Long, GroupEntity> groupEntities = new HashMap<>();
        Map<Long, PermissionEntity> roleEntities = new HashMap<>();
        Map<Long, Object> resourceEntities = new HashMap<>();
        resourceEntities.put(resourceEntity.getId(), clusterEntity);
        AmbariPrivilegeResourceProvider provider = new AmbariPrivilegeResourceProvider();
        Resource resource = provider.toResource(privilegeEntity, userEntities, groupEntities, roleEntities, resourceEntities, provider.getPropertyIds());
        Assert.assertEquals("TestCluster", resource.getPropertyValue(CLUSTER_NAME));
        Assert.assertEquals(CLUSTER.name(), resource.getPropertyValue(TYPE));
        verify(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, clusterEntity, resourceEntity, privilegeEntity);
    }

    @Test
    public void testToResource_VIEW() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("USER").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getId()).andReturn(1L).atLeastOnce();
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
        replay(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, viewInstanceEntity, viewEntity, resourceEntity, privilegeEntity);
        Map<Long, UserEntity> userEntities = new HashMap<>();
        Map<Long, GroupEntity> groupEntities = new HashMap<>();
        Map<Long, PermissionEntity> roleEntities = new HashMap<>();
        Map<Long, Object> resourceEntities = new HashMap<>();
        resourceEntities.put(resourceEntity.getId(), viewInstanceEntity);
        AmbariPrivilegeResourceProvider provider = new AmbariPrivilegeResourceProvider();
        Resource resource = provider.toResource(privilegeEntity, userEntities, groupEntities, roleEntities, resourceEntities, provider.getPropertyIds());
        Assert.assertEquals("Test View", resource.getPropertyValue(INSTANCE_NAME));
        Assert.assertEquals("TestView", resource.getPropertyValue(VIEW_NAME));
        Assert.assertEquals("1.2.3.4", resource.getPropertyValue(VERSION));
        Assert.assertEquals(VIEW.name(), resource.getPropertyValue(TYPE));
        verify(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, viewInstanceEntity, viewEntity, resourceEntity, privilegeEntity);
    }

    @Test
    public void testToResource_SpecificVIEW() {
        PermissionEntity permissionEntity = createMock(PermissionEntity.class);
        expect(permissionEntity.getPermissionName()).andReturn("CLUSTER.ADMINISTRATOR").atLeastOnce();
        expect(permissionEntity.getPermissionLabel()).andReturn("Cluster Administrator").atLeastOnce();
        PrincipalTypeEntity principalTypeEntity = createMock(PrincipalTypeEntity.class);
        expect(principalTypeEntity.getName()).andReturn("USER").atLeastOnce();
        PrincipalEntity principalEntity = createMock(PrincipalEntity.class);
        expect(principalEntity.getId()).andReturn(1L).atLeastOnce();
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
        replay(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, viewInstanceEntity, viewEntity, resourceEntity, privilegeEntity);
        Map<Long, UserEntity> userEntities = new HashMap<>();
        Map<Long, GroupEntity> groupEntities = new HashMap<>();
        Map<Long, PermissionEntity> roleEntities = new HashMap<>();
        Map<Long, Object> resourceEntities = new HashMap<>();
        resourceEntities.put(resourceEntity.getId(), viewInstanceEntity);
        AmbariPrivilegeResourceProvider provider = new AmbariPrivilegeResourceProvider();
        Resource resource = provider.toResource(privilegeEntity, userEntities, groupEntities, roleEntities, resourceEntities, provider.getPropertyIds());
        Assert.assertEquals("Test View", resource.getPropertyValue(INSTANCE_NAME));
        Assert.assertEquals("TestView", resource.getPropertyValue(VIEW_NAME));
        Assert.assertEquals("1.2.3.4", resource.getPropertyValue(VERSION));
        Assert.assertEquals(VIEW.name(), resource.getPropertyValue(TYPE));
        verify(permissionEntity, principalTypeEntity, principalEntity, resourceTypeEntity, viewInstanceEntity, viewEntity, resourceEntity, privilegeEntity);
    }
}

