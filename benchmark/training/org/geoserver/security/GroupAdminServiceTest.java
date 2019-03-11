/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import java.io.IOException;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.junit.Assert;
import org.junit.Test;


public class GroupAdminServiceTest extends AbstractSecurityServiceTest {
    protected GeoServerUserGroupStore ugStore;

    protected GeoServerRoleStore roleStore;

    GeoServerUser bob;

    GeoServerUser alice;

    GeoServerUserGroup users;

    GeoServerUserGroup admins;

    @Test
    public void testWrapRoleService() throws Exception {
        GeoServerRoleService roleService = getSecurityManager().getActiveRoleService();
        Assert.assertFalse((roleService instanceof GroupAdminRoleService));
        setAuth();
        roleService = getSecurityManager().getActiveRoleService();
        Assert.assertTrue((roleService instanceof GroupAdminRoleService));
    }

    @Test
    public void testWrapUserGroupService() throws Exception {
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        Assert.assertFalse((ugService instanceof GroupAdminUserGroupService));
        setAuth();
        ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        Assert.assertTrue((ugService instanceof GroupAdminUserGroupService));
    }

    @Test
    public void testHideAdminRole() throws Exception {
        GeoServerRoleService roleService = getSecurityManager().getActiveRoleService();
        GeoServerRole adminRole = roleService.createRoleObject("adminRole");
        Assert.assertTrue(roleService.getRoles().contains(adminRole));
        Assert.assertNotNull(roleService.getAdminRole());
        Assert.assertNotNull(roleService.getRoleByName("adminRole"));
        setAuth();
        roleService = getSecurityManager().getActiveRoleService();
        Assert.assertFalse(roleService.getRoles().contains(adminRole));
        Assert.assertNull(roleService.getAdminRole());
        Assert.assertNull(roleService.getRoleByName("adminRole"));
    }

    @Test
    public void testHideGroups() throws Exception {
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        Assert.assertTrue(ugService.getUserGroups().contains(users));
        Assert.assertNotNull(ugService.getGroupByGroupname("users"));
        Assert.assertTrue(ugService.getUserGroups().contains(admins));
        Assert.assertNotNull(ugService.getGroupByGroupname("admins"));
        setAuth();
        ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        Assert.assertTrue(ugService.getUserGroups().contains(users));
        Assert.assertNotNull(ugService.getGroupByGroupname("users"));
        Assert.assertFalse(ugService.getUserGroups().contains(admins));
        Assert.assertNull(ugService.getGroupByGroupname("admins"));
    }

    @Test
    public void testRoleServiceReadOnly() throws Exception {
        setAuth();
        GeoServerRoleService roleService = getSecurityManager().getActiveRoleService();
        Assert.assertFalse(roleService.canCreateStore());
        Assert.assertNull(roleService.createStore());
    }

    @Test
    public void testCreateNewUser() throws Exception {
        setAuth();
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        GeoServerUserGroupStore ugStore = ugService.createStore();
        GeoServerUser bill = ugStore.createUserObject("bill", "foobar", true);
        ugStore.addUser(bill);
        ugStore.store();
        Assert.assertNotNull(ugService.getUserByUsername("bill"));
    }

    @Test
    public void testAssignUserToGroup() throws Exception {
        testCreateNewUser();
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        GeoServerUserGroupStore ugStore = ugService.createStore();
        GeoServerUser bill = ugStore.getUserByUsername("bill");
        ugStore.associateUserToGroup(bill, users);
        ugStore.store();
        Assert.assertEquals(1, ugStore.getGroupsForUser(bill).size());
        Assert.assertTrue(ugStore.getGroupsForUser(bill).contains(users));
        ugStore.associateUserToGroup(bill, admins);
        ugStore.store();
        Assert.assertEquals(1, ugStore.getGroupsForUser(bill).size());
        Assert.assertTrue(ugStore.getGroupsForUser(bill).contains(users));
        Assert.assertFalse(ugStore.getGroupsForUser(bill).contains(admins));
    }

    @Test
    public void testRemoveUserInGroup() throws Exception {
        testAssignUserToGroup();
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        GeoServerUserGroupStore ugStore = ugService.createStore();
        GeoServerUser bill = ugStore.getUserByUsername("bill");
        ugStore.removeUser(bill);
        ugStore.store();
        Assert.assertNull(ugStore.getUserByUsername("bill"));
    }

    @Test
    public void testRemoveUserNotInGroup() throws Exception {
        GeoServerUserGroupService ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        GeoServerUserGroupStore ugStore = ugService.createStore();
        GeoServerUser sally = ugStore.createUserObject("sally", "foobar", true);
        ugStore.addUser(sally);
        ugStore.associateUserToGroup(sally, admins);
        ugStore.store();
        setAuth();
        ugService = getSecurityManager().loadUserGroupService(ugStore.getName());
        ugStore = ugService.createStore();
        try {
            ugStore.removeUser(sally);
            Assert.fail();
        } catch (IOException e) {
            ugStore.load();
        }
    }
}

