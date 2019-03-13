package com.ctrip.framework.apollo.portal.spi.defaultImpl;


import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.defaultimpl.DefaultRoleInitializationService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RoleInitializationServiceTest extends AbstractUnitTest {
    private final String APP_ID = "1000";

    private final String APP_NAME = "app-test";

    private final String CLUSTER = "cluster-test";

    private final String NAMESPACE = "namespace-test";

    private final String CURRENT_USER = "user";

    @Mock
    private RolePermissionService rolePermissionService;

    @Mock
    private UserInfoHolder userInfoHolder;

    @Mock
    private PortalConfig portalConfig;

    @InjectMocks
    private DefaultRoleInitializationService roleInitializationService;

    @Test
    public void testInitAppRoleHasInitBefore() {
        Mockito.when(rolePermissionService.findRoleByRoleName(ArgumentMatchers.anyString())).thenReturn(mockRole(RoleUtils.buildAppMasterRoleName(APP_ID)));
        roleInitializationService.initAppRoles(mockApp());
        Mockito.verify(rolePermissionService, Mockito.times(1)).findRoleByRoleName(RoleUtils.buildAppMasterRoleName(APP_ID));
        Mockito.verify(rolePermissionService, Mockito.times(0)).assignRoleToUsers(ArgumentMatchers.anyString(), ArgumentMatchers.anySet(), ArgumentMatchers.anyString());
    }

    @Test
    public void testInitAppRole() {
        Mockito.when(rolePermissionService.findRoleByRoleName(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(userInfoHolder.getUser()).thenReturn(mockUser());
        Mockito.when(rolePermissionService.createPermission(ArgumentMatchers.any())).thenReturn(mockPermission());
        Mockito.when(portalConfig.portalSupportedEnvs()).thenReturn(mockPortalSupportedEnvs());
        roleInitializationService.initAppRoles(mockApp());
        Mockito.verify(rolePermissionService, Mockito.times(7)).findRoleByRoleName(ArgumentMatchers.anyString());
        Mockito.verify(rolePermissionService, Mockito.times(1)).assignRoleToUsers(RoleUtils.buildAppMasterRoleName(APP_ID), Sets.newHashSet(CURRENT_USER), CURRENT_USER);
        Mockito.verify(rolePermissionService, Mockito.times(6)).createPermission(ArgumentMatchers.any());
        Mockito.verify(rolePermissionService, Mockito.times(7)).createRoleWithPermissions(ArgumentMatchers.any(), ArgumentMatchers.anySet());
    }

    @Test
    public void testInitNamespaceRoleHasExisted() {
        String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName)).thenReturn(mockRole(modifyNamespaceRoleName));
        String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName)).thenReturn(mockRole(releaseNamespaceRoleName));
        roleInitializationService.initNamespaceRoles(APP_ID, NAMESPACE, CURRENT_USER);
        Mockito.verify(rolePermissionService, Mockito.times(2)).findRoleByRoleName(ArgumentMatchers.anyString());
        Mockito.verify(rolePermissionService, Mockito.times(0)).createPermission(ArgumentMatchers.any());
        Mockito.verify(rolePermissionService, Mockito.times(0)).createRoleWithPermissions(ArgumentMatchers.any(), ArgumentMatchers.anySet());
    }

    @Test
    public void testInitNamespaceRoleNotExisted() {
        String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName)).thenReturn(null);
        String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName)).thenReturn(null);
        Mockito.when(userInfoHolder.getUser()).thenReturn(mockUser());
        Mockito.when(rolePermissionService.createPermission(ArgumentMatchers.any())).thenReturn(mockPermission());
        roleInitializationService.initNamespaceRoles(APP_ID, NAMESPACE, CURRENT_USER);
        Mockito.verify(rolePermissionService, Mockito.times(2)).findRoleByRoleName(ArgumentMatchers.anyString());
        Mockito.verify(rolePermissionService, Mockito.times(2)).createPermission(ArgumentMatchers.any());
        Mockito.verify(rolePermissionService, Mockito.times(2)).createRoleWithPermissions(ArgumentMatchers.any(), ArgumentMatchers.anySet());
    }

    @Test
    public void testInitNamespaceRoleModifyNSExisted() {
        String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName)).thenReturn(mockRole(modifyNamespaceRoleName));
        String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(APP_ID, NAMESPACE);
        Mockito.when(rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName)).thenReturn(null);
        Mockito.when(userInfoHolder.getUser()).thenReturn(mockUser());
        Mockito.when(rolePermissionService.createPermission(ArgumentMatchers.any())).thenReturn(mockPermission());
        roleInitializationService.initNamespaceRoles(APP_ID, NAMESPACE, CURRENT_USER);
        Mockito.verify(rolePermissionService, Mockito.times(2)).findRoleByRoleName(ArgumentMatchers.anyString());
        Mockito.verify(rolePermissionService, Mockito.times(1)).createPermission(ArgumentMatchers.any());
        Mockito.verify(rolePermissionService, Mockito.times(1)).createRoleWithPermissions(ArgumentMatchers.any(), ArgumentMatchers.anySet());
    }
}

