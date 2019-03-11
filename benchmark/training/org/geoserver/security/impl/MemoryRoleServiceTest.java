/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import GeoServerRole.ADMIN_ROLE;
import GeoServerRole.GROUP_ADMIN_ROLE;
import PasswordValidator.DEFAULT_NAME;
import java.util.SortedSet;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerRoleStore;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.config.impl.MemoryRoleServiceConfigImpl;
import org.geoserver.security.config.impl.MemoryUserGroupServiceConfigImpl;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SystemTest.class)
public class MemoryRoleServiceTest extends AbstractRoleServiceTest {
    // @After
    // public void clearRoleService() throws IOException {
    // store.clear();
    // }
    @Test
    public void testInsert() throws Exception {
        super.testInsert();
        for (GeoServerRole role : store.getRoles()) {
            Assert.assertTrue(((role.getClass()) == (MemoryGeoserverRole.class)));
        }
    }

    @Test
    public void testMappedAdminRoles() throws Exception {
        MemoryRoleServiceConfigImpl config = new MemoryRoleServiceConfigImpl();
        config.setName("testAdminRole");
        config.setAdminRoleName("adminRole");
        config.setGroupAdminRoleName("groupAdminRole");
        config.setClassName(MemoryRoleService.class.getName());
        GeoServerRoleService service = new MemoryRoleService();
        service.initializeFromConfig(config);
        GeoServerSecurityManager manager = GeoServerExtensions.bean(GeoServerSecurityManager.class);
        service.setSecurityManager(manager);
        manager.setActiveRoleService(service);
        manager.saveRoleService(config);
        GeoServerRoleStore store = service.createStore();
        GeoServerRole adminRole = store.createRoleObject("adminRole");
        GeoServerRole groupAdminRole = store.createRoleObject("groupAdminRole");
        GeoServerRole role1 = store.createRoleObject("role1");
        store.addRole(adminRole);
        store.addRole(groupAdminRole);
        store.addRole(role1);
        store.associateRoleToUser(adminRole, "user1");
        store.associateRoleToUser(groupAdminRole, "user1");
        store.associateRoleToUser(adminRole, "user2");
        store.associateRoleToUser(role1, "user3");
        store.store();
        MemoryUserGroupServiceConfigImpl ugconfig = new MemoryUserGroupServiceConfigImpl();
        ugconfig.setName("testAdminRole");
        ugconfig.setClassName(MemoryUserGroupService.class.getName());
        ugconfig.setPasswordEncoderName(getPBEPasswordEncoder().getName());
        ugconfig.setPasswordPolicyName(DEFAULT_NAME);
        GeoServerUserGroupService ugService = new MemoryUserGroupService();
        ugService.setSecurityManager(GeoServerExtensions.bean(GeoServerSecurityManager.class));
        ugService.initializeFromConfig(ugconfig);
        RoleCalculator calc = new RoleCalculator(ugService, service);
        SortedSet<GeoServerRole> roles;
        roles = calc.calculateRoles(ugService.createUserObject("user1", "abc", true));
        Assert.assertTrue(((roles.size()) == 4));
        Assert.assertTrue(roles.contains(adminRole));
        Assert.assertTrue(roles.contains(ADMIN_ROLE));
        Assert.assertTrue(roles.contains(groupAdminRole));
        Assert.assertTrue(roles.contains(GROUP_ADMIN_ROLE));
        roles = calc.calculateRoles(ugService.createUserObject("user2", "abc", true));
        Assert.assertTrue(((roles.size()) == 2));
        Assert.assertTrue(roles.contains(adminRole));
        Assert.assertTrue(roles.contains(ADMIN_ROLE));
        roles = calc.calculateRoles(ugService.createUserObject("user3", "abc", true));
        Assert.assertTrue(((roles.size()) == 1));
        Assert.assertTrue(roles.contains(role1));
    }
}

