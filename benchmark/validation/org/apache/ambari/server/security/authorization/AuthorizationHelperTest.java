/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security.authorization;


import ResourceType.AMBARI;
import ResourceType.CLUSTER;
import ResourceType.VIEW;
import RoleAuthorization.AMBARI_MANAGE_USERS;
import RoleAuthorization.CLUSTER_TOGGLE_KERBEROS;
import RoleAuthorization.CLUSTER_VIEW_METRICS;
import RoleAuthorization.VIEW_USE;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.ambari.server.orm.dao.PrivilegeDAO;
import org.apache.ambari.server.orm.dao.ViewInstanceDAO;
import org.apache.ambari.server.orm.entities.PermissionEntity;
import org.apache.ambari.server.orm.entities.PrincipalEntity;
import org.apache.ambari.server.orm.entities.PrincipalTypeEntity;
import org.apache.ambari.server.orm.entities.PrivilegeEntity;
import org.apache.ambari.server.orm.entities.ResourceEntity;
import org.apache.ambari.server.orm.entities.ResourceTypeEntity;
import org.apache.ambari.server.orm.entities.RoleAuthorizationEntity;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.security.authentication.AmbariUserAuthentication;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static AuthorizationHelper.privilegeDAOProvider;
import static AuthorizationHelper.viewInstanceDAOProvider;


public class AuthorizationHelperTest extends EasyMockSupport {
    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock(type = MockType.NICE)
    private ServletRequestAttributes servletRequestAttributes;

    @Test
    public void testConvertPrivilegesToAuthorities() throws Exception {
        Collection<PrivilegeEntity> privilegeEntities = new ArrayList<>();
        ResourceTypeEntity resourceTypeEntity = new ResourceTypeEntity();
        resourceTypeEntity.setId(1);
        resourceTypeEntity.setName("CLUSTER");
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setId(1L);
        resourceEntity.setResourceType(resourceTypeEntity);
        PrincipalTypeEntity principalTypeEntity = new PrincipalTypeEntity();
        principalTypeEntity.setId(1);
        principalTypeEntity.setName("USER");
        PrincipalEntity principalEntity = new PrincipalEntity();
        principalEntity.setPrincipalType(principalTypeEntity);
        principalEntity.setId(1L);
        PermissionEntity permissionEntity1 = new PermissionEntity();
        permissionEntity1.setPermissionName("Permission1");
        permissionEntity1.setResourceType(resourceTypeEntity);
        permissionEntity1.setId(2);
        permissionEntity1.setPermissionName("CLUSTER.USER");
        PermissionEntity permissionEntity2 = new PermissionEntity();
        permissionEntity2.setPermissionName("Permission1");
        permissionEntity2.setResourceType(resourceTypeEntity);
        permissionEntity2.setId(3);
        permissionEntity2.setPermissionName("CLUSTER.ADMINISTRATOR");
        PrivilegeEntity privilegeEntity1 = new PrivilegeEntity();
        privilegeEntity1.setId(1);
        privilegeEntity1.setPermission(permissionEntity1);
        privilegeEntity1.setPrincipal(principalEntity);
        privilegeEntity1.setResource(resourceEntity);
        PrivilegeEntity privilegeEntity2 = new PrivilegeEntity();
        privilegeEntity2.setId(1);
        privilegeEntity2.setPermission(permissionEntity2);
        privilegeEntity2.setPrincipal(principalEntity);
        privilegeEntity2.setResource(resourceEntity);
        privilegeEntities.add(privilegeEntity1);
        privilegeEntities.add(privilegeEntity2);
        Collection<GrantedAuthority> authorities = new AuthorizationHelper().convertPrivilegesToAuthorities(privilegeEntities);
        Assert.assertEquals("Wrong number of authorities", 2, authorities.size());
        Set<String> authorityNames = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            authorityNames.add(authority.getAuthority());
        }
        Assert.assertTrue(authorityNames.contains("CLUSTER.USER@1"));
        Assert.assertTrue(authorityNames.contains("CLUSTER.ADMINISTRATOR@1"));
    }

    @Test
    public void testAuthName() throws Exception {
        String user = AuthorizationHelper.getAuthenticatedName();
        Assert.assertNull(user);
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        user = AuthorizationHelper.getAuthenticatedName();
        Assert.assertEquals("admin", user);
    }

    @Test
    public void testAuthId() throws Exception {
        Integer userId = AuthorizationHelper.getAuthenticatedId();
        Assert.assertEquals(Integer.valueOf((-1)), userId);
        PrincipalEntity principalEntity = new PrincipalEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        userEntity.setPrincipal(principalEntity);
        User user = new User(userEntity);
        Authentication auth = new AmbariUserAuthentication(null, new org.apache.ambari.server.security.authentication.AmbariUserDetailsImpl(user, null, null));
        SecurityContextHolder.getContext().setAuthentication(auth);
        userId = AuthorizationHelper.getAuthenticatedId();
        Assert.assertEquals(Integer.valueOf(1), userId);
    }

    @Test
    public void testAuthWithoutId() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        Integer userId = AuthorizationHelper.getAuthenticatedId();
        Assert.assertEquals(Integer.valueOf((-1)), userId);
    }

    @Test
    public void testIsAuthorized() {
        Provider viewInstanceDAOProvider = createNiceMock(Provider.class);
        Provider privilegeDAOProvider = createNiceMock(Provider.class);
        ViewInstanceDAO viewInstanceDAO = createNiceMock(ViewInstanceDAO.class);
        PrivilegeDAO privilegeDAO = createNiceMock(PrivilegeDAO.class);
        expect(viewInstanceDAOProvider.get()).andReturn(viewInstanceDAO).anyTimes();
        expect(privilegeDAOProvider.get()).andReturn(privilegeDAO).anyTimes();
        replayAll();
        viewInstanceDAOProvider = viewInstanceDAOProvider;
        privilegeDAOProvider = privilegeDAOProvider;
        RoleAuthorizationEntity readOnlyRoleAuthorizationEntity = new RoleAuthorizationEntity();
        readOnlyRoleAuthorizationEntity.setAuthorizationId(CLUSTER_VIEW_METRICS.getId());
        RoleAuthorizationEntity privilegedRoleAuthorizationEntity = new RoleAuthorizationEntity();
        privilegedRoleAuthorizationEntity.setAuthorizationId(CLUSTER_TOGGLE_KERBEROS.getId());
        RoleAuthorizationEntity administratorRoleAuthorizationEntity = new RoleAuthorizationEntity();
        administratorRoleAuthorizationEntity.setAuthorizationId(AMBARI_MANAGE_USERS.getId());
        ResourceTypeEntity ambariResourceTypeEntity = new ResourceTypeEntity();
        ambariResourceTypeEntity.setId(1);
        ambariResourceTypeEntity.setName(AMBARI.name());
        ResourceTypeEntity clusterResourceTypeEntity = new ResourceTypeEntity();
        clusterResourceTypeEntity.setId(1);
        clusterResourceTypeEntity.setName(CLUSTER.name());
        ResourceTypeEntity cluster2ResourceTypeEntity = new ResourceTypeEntity();
        cluster2ResourceTypeEntity.setId(2);
        cluster2ResourceTypeEntity.setName(CLUSTER.name());
        ResourceEntity ambariResourceEntity = new ResourceEntity();
        ambariResourceEntity.setResourceType(ambariResourceTypeEntity);
        ambariResourceEntity.setId(1L);
        ResourceEntity clusterResourceEntity = new ResourceEntity();
        clusterResourceEntity.setResourceType(clusterResourceTypeEntity);
        clusterResourceEntity.setId(1L);
        ResourceEntity cluster2ResourceEntity = new ResourceEntity();
        cluster2ResourceEntity.setResourceType(cluster2ResourceTypeEntity);
        cluster2ResourceEntity.setId(2L);
        PermissionEntity readOnlyPermissionEntity = new PermissionEntity();
        readOnlyPermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        PermissionEntity privilegedPermissionEntity = new PermissionEntity();
        privilegedPermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        privilegedPermissionEntity.addAuthorization(privilegedRoleAuthorizationEntity);
        PermissionEntity administratorPermissionEntity = new PermissionEntity();
        administratorPermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        administratorPermissionEntity.addAuthorization(privilegedRoleAuthorizationEntity);
        administratorPermissionEntity.addAuthorization(administratorRoleAuthorizationEntity);
        PrivilegeEntity readOnlyPrivilegeEntity = new PrivilegeEntity();
        readOnlyPrivilegeEntity.setPermission(readOnlyPermissionEntity);
        readOnlyPrivilegeEntity.setResource(clusterResourceEntity);
        PrivilegeEntity readOnly2PrivilegeEntity = new PrivilegeEntity();
        readOnly2PrivilegeEntity.setPermission(readOnlyPermissionEntity);
        readOnly2PrivilegeEntity.setResource(cluster2ResourceEntity);
        PrivilegeEntity privilegedPrivilegeEntity = new PrivilegeEntity();
        privilegedPrivilegeEntity.setPermission(privilegedPermissionEntity);
        privilegedPrivilegeEntity.setResource(clusterResourceEntity);
        PrivilegeEntity privileged2PrivilegeEntity = new PrivilegeEntity();
        privileged2PrivilegeEntity.setPermission(privilegedPermissionEntity);
        privileged2PrivilegeEntity.setResource(cluster2ResourceEntity);
        PrivilegeEntity administratorPrivilegeEntity = new PrivilegeEntity();
        administratorPrivilegeEntity.setPermission(administratorPermissionEntity);
        administratorPrivilegeEntity.setResource(ambariResourceEntity);
        GrantedAuthority readOnlyAuthority = new AmbariGrantedAuthority(readOnlyPrivilegeEntity);
        GrantedAuthority readOnly2Authority = new AmbariGrantedAuthority(readOnly2PrivilegeEntity);
        GrantedAuthority privilegedAuthority = new AmbariGrantedAuthority(privilegedPrivilegeEntity);
        GrantedAuthority privileged2Authority = new AmbariGrantedAuthority(privileged2PrivilegeEntity);
        GrantedAuthority administratorAuthority = new AmbariGrantedAuthority(administratorPrivilegeEntity);
        Authentication noAccessUser = new AuthorizationHelperTest.TestAuthentication(Collections.<AmbariGrantedAuthority>emptyList());
        Authentication readOnlyUser = new AuthorizationHelperTest.TestAuthentication(Collections.singleton(readOnlyAuthority));
        Authentication privilegedUser = new AuthorizationHelperTest.TestAuthentication(Arrays.asList(readOnlyAuthority, privilegedAuthority));
        Authentication privileged2User = new AuthorizationHelperTest.TestAuthentication(Arrays.asList(readOnly2Authority, privileged2Authority));
        Authentication administratorUser = new AuthorizationHelperTest.TestAuthentication(Collections.singleton(administratorAuthority));
        SecurityContext context = SecurityContextHolder.getContext();
        // No user (explicit)...
        Assert.assertFalse(AuthorizationHelper.isAuthorized(null, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        // No user (from context)
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        // Explicit user tests...
        Assert.assertFalse(AuthorizationHelper.isAuthorized(noAccessUser, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(noAccessUser, CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(noAccessUser, CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(readOnlyUser, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(readOnlyUser, CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(readOnlyUser, CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(privilegedUser, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(privilegedUser, CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(privilegedUser, CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(privileged2User, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(privileged2User, CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(privileged2User, CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(administratorUser, CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(administratorUser, CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(administratorUser, CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        // Context user tests...
        context.setAuthentication(noAccessUser);
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        context.setAuthentication(readOnlyUser);
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        context.setAuthentication(privilegedUser);
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        context.setAuthentication(privileged2User);
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
        context.setAuthentication(administratorUser);
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_VIEW_METRICS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(CLUSTER_TOGGLE_KERBEROS)));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(CLUSTER, 1L, EnumSet.of(AMBARI_MANAGE_USERS)));
    }

    @Test
    public void testIsAuthorizedForSpecificView() {
        RoleAuthorizationEntity readOnlyRoleAuthorizationEntity = new RoleAuthorizationEntity();
        readOnlyRoleAuthorizationEntity.setAuthorizationId(CLUSTER_VIEW_METRICS.getId());
        RoleAuthorizationEntity viewUseRoleAuthorizationEntity = new RoleAuthorizationEntity();
        viewUseRoleAuthorizationEntity.setAuthorizationId(VIEW_USE.getId());
        RoleAuthorizationEntity administratorRoleAuthorizationEntity = new RoleAuthorizationEntity();
        administratorRoleAuthorizationEntity.setAuthorizationId(AMBARI_MANAGE_USERS.getId());
        ResourceTypeEntity ambariResourceTypeEntity = new ResourceTypeEntity();
        ambariResourceTypeEntity.setId(1);
        ambariResourceTypeEntity.setName(AMBARI.name());
        ResourceTypeEntity clusterResourceTypeEntity = new ResourceTypeEntity();
        clusterResourceTypeEntity.setId(1);
        clusterResourceTypeEntity.setName(CLUSTER.name());
        ResourceTypeEntity viewResourceTypeEntity = new ResourceTypeEntity();
        viewResourceTypeEntity.setId(30);
        viewResourceTypeEntity.setName(VIEW.name());
        ResourceEntity ambariResourceEntity = new ResourceEntity();
        ambariResourceEntity.setResourceType(ambariResourceTypeEntity);
        ambariResourceEntity.setId(1L);
        ResourceEntity clusterResourceEntity = new ResourceEntity();
        clusterResourceEntity.setResourceType(clusterResourceTypeEntity);
        clusterResourceEntity.setId(1L);
        ResourceEntity viewResourceEntity = new ResourceEntity();
        viewResourceEntity.setResourceType(viewResourceTypeEntity);
        viewResourceEntity.setId(53L);
        PermissionEntity readOnlyPermissionEntity = new PermissionEntity();
        readOnlyPermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        PermissionEntity viewUsePermissionEntity = new PermissionEntity();
        viewUsePermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        viewUsePermissionEntity.addAuthorization(viewUseRoleAuthorizationEntity);
        PermissionEntity administratorPermissionEntity = new PermissionEntity();
        administratorPermissionEntity.addAuthorization(readOnlyRoleAuthorizationEntity);
        administratorPermissionEntity.addAuthorization(viewUseRoleAuthorizationEntity);
        administratorPermissionEntity.addAuthorization(administratorRoleAuthorizationEntity);
        PrivilegeEntity readOnlyPrivilegeEntity = new PrivilegeEntity();
        readOnlyPrivilegeEntity.setPermission(readOnlyPermissionEntity);
        readOnlyPrivilegeEntity.setResource(clusterResourceEntity);
        PrivilegeEntity viewUsePrivilegeEntity = new PrivilegeEntity();
        viewUsePrivilegeEntity.setPermission(viewUsePermissionEntity);
        viewUsePrivilegeEntity.setResource(viewResourceEntity);
        PrivilegeEntity administratorPrivilegeEntity = new PrivilegeEntity();
        administratorPrivilegeEntity.setPermission(administratorPermissionEntity);
        administratorPrivilegeEntity.setResource(ambariResourceEntity);
        GrantedAuthority readOnlyAuthority = new AmbariGrantedAuthority(readOnlyPrivilegeEntity);
        GrantedAuthority viewUseAuthority = new AmbariGrantedAuthority(viewUsePrivilegeEntity);
        GrantedAuthority administratorAuthority = new AmbariGrantedAuthority(administratorPrivilegeEntity);
        Authentication readOnlyUser = new AuthorizationHelperTest.TestAuthentication(Collections.singleton(readOnlyAuthority));
        Authentication viewUser = new AuthorizationHelperTest.TestAuthentication(Arrays.asList(readOnlyAuthority, viewUseAuthority));
        Authentication administratorUser = new AuthorizationHelperTest.TestAuthentication(Collections.singleton(administratorAuthority));
        SecurityContext context = SecurityContextHolder.getContext();
        Set<RoleAuthorization> permissionsViewUse = EnumSet.of(VIEW_USE);
        context.setAuthentication(readOnlyUser);
        Assert.assertFalse(AuthorizationHelper.isAuthorized(VIEW, 53L, permissionsViewUse));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(VIEW, 50L, permissionsViewUse));
        context.setAuthentication(viewUser);
        Assert.assertTrue(AuthorizationHelper.isAuthorized(VIEW, 53L, permissionsViewUse));
        Assert.assertFalse(AuthorizationHelper.isAuthorized(VIEW, 50L, permissionsViewUse));
        context.setAuthentication(administratorUser);
        Assert.assertTrue(AuthorizationHelper.isAuthorized(VIEW, 53L, permissionsViewUse));
        Assert.assertTrue(AuthorizationHelper.isAuthorized(VIEW, 50L, permissionsViewUse));
    }

    @Test
    public void testResolveLoginAliasToUserName() throws Exception {
        // Given
        reset(servletRequestAttributes);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        expect(servletRequestAttributes.getAttribute(eq("loginAlias1"), eq(RequestAttributes.SCOPE_SESSION))).andReturn("user1").atLeastOnce();
        replay(servletRequestAttributes);
        // When
        String user = AuthorizationHelper.resolveLoginAliasToUserName("loginAlias1");
        // Then
        verify(servletRequestAttributes);
        Assert.assertEquals("user1", user);
    }

    @Test
    public void testResolveNoLoginAliasToUserName() throws Exception {
        reset(servletRequestAttributes);
        // No request attributes/http session available yet
        RequestContextHolder.setRequestAttributes(null);
        Assert.assertEquals("user", AuthorizationHelper.resolveLoginAliasToUserName("user"));
        // request attributes available but user doesn't have any login aliases
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        expect(servletRequestAttributes.getAttribute(eq("nosuchalias"), eq(RequestAttributes.SCOPE_SESSION))).andReturn(null).atLeastOnce();
        replay(servletRequestAttributes);
        // When
        String user = AuthorizationHelper.resolveLoginAliasToUserName("nosuchalias");
        // Then
        verify(servletRequestAttributes);
        Assert.assertEquals("nosuchalias", user);
    }

    private class TestAuthentication implements Authentication {
        private final Collection<? extends GrantedAuthority> grantedAuthorities;

        public TestAuthentication(Collection<? extends GrantedAuthority> grantedAuthorities) {
            this.grantedAuthorities = grantedAuthorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return grantedAuthorities;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }

        @Override
        public String getName() {
            return null;
        }
    }
}

