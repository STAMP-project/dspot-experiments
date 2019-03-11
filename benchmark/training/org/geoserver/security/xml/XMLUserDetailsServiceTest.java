/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.xml;


import GeoServerRole.ADMIN_ROLE;
import XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE;
import XMLUserGroupService.DEFAULT_NAME;
import java.io.File;
import java.io.IOException;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.impl.AbstractUserDetailsServiceTest;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.password.GeoServerMultiplexingPasswordEncoder;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static XMLConstants.FILE_RR_SCHEMA;
import static XMLConstants.FILE_UR_SCHEMA;


@Category(SystemTest.class)
public class XMLUserDetailsServiceTest extends AbstractUserDetailsServiceTest {
    @Test
    public void testMigration() throws IOException {
        // GeoserverUserGroupService userService = createUserGroupService(
        // XMLUserGroupService.DEFAULT_NAME);
        // GeoserverRoleService roleService = createRoleService(
        // XMLRoleService.DEFAULT_NAME);
        // getSecurityManager().setActiveRoleService(roleService);
        // getSecurityManager().setActiveUserGroupService(userService);
        GeoServerUserGroupService userService = getSecurityManager().loadUserGroupService(DEFAULT_NAME);
        GeoServerRoleService roleService = getSecurityManager().loadRoleService(XMLRoleService.DEFAULT_NAME);
        Assert.assertEquals(3, userService.getUsers().size());
        Assert.assertEquals(3, userService.getUserCount());
        Assert.assertEquals(0, userService.getUserGroups().size());
        Assert.assertEquals(0, userService.getGroupCount());
        Assert.assertEquals(9, roleService.getRoles().size());
        GeoServerUser admin = ((GeoServerUser) (userService.loadUserByUsername("admin")));
        Assert.assertNotNull(admin);
        GeoServerMultiplexingPasswordEncoder enc = getEncoder(userService);
        Assert.assertTrue(enc.isPasswordValid(admin.getPassword(), "gs", null));
        Assert.assertTrue(admin.isEnabled());
        GeoServerUser wfs = ((GeoServerUser) (userService.loadUserByUsername("wfs")));
        Assert.assertNotNull(wfs);
        Assert.assertTrue(enc.isPasswordValid(wfs.getPassword(), "webFeatureService", null));
        Assert.assertTrue(wfs.isEnabled());
        GeoServerUser disabledUser = ((GeoServerUser) (userService.loadUserByUsername("disabledUser")));
        Assert.assertNotNull(disabledUser);
        Assert.assertTrue(enc.isPasswordValid(disabledUser.getPassword(), "nah", null));
        Assert.assertFalse(disabledUser.isEnabled());
        GeoServerRole role_admin = roleService.getRoleByName(DEFAULT_LOCAL_ADMIN_ROLE);
        Assert.assertNotNull(role_admin);
        GeoServerRole role_wfs_read = roleService.getRoleByName("ROLE_WFS_READ");
        Assert.assertNotNull(role_wfs_read);
        GeoServerRole role_wfs_write = roleService.getRoleByName("ROLE_WFS_WRITE");
        Assert.assertNotNull(role_wfs_write);
        GeoServerRole role_test = roleService.getRoleByName("ROLE_TEST");
        Assert.assertNotNull(role_test);
        Assert.assertNotNull(roleService.getRoleByName("NO_ONE"));
        Assert.assertNotNull(roleService.getRoleByName("TRUSTED_ROLE"));
        Assert.assertNotNull(roleService.getRoleByName("ROLE_SERVICE_1"));
        Assert.assertNotNull(roleService.getRoleByName("ROLE_SERVICE_2"));
        Assert.assertEquals(2, admin.getAuthorities().size());
        Assert.assertTrue(admin.getAuthorities().contains(role_admin));
        Assert.assertTrue(admin.getAuthorities().contains(ADMIN_ROLE));
        Assert.assertEquals(2, wfs.getAuthorities().size());
        Assert.assertTrue(wfs.getAuthorities().contains(role_wfs_read));
        Assert.assertTrue(wfs.getAuthorities().contains(role_wfs_write));
        Assert.assertEquals(1, disabledUser.getAuthorities().size());
        Assert.assertTrue(disabledUser.getAuthorities().contains(role_test));
        GeoServerSecurityManager securityManager = getSecurityManager();
        File userfile = new File(securityManager.get("security").dir(), "users.properties");
        Assert.assertFalse(userfile.exists());
        File userfileOld = new File(securityManager.get("security").dir(), "users.properties.old");
        Assert.assertTrue(userfileOld.exists());
        File roleXSD = new File(new File(securityManager.get("security/role").dir(), roleService.getName()), FILE_RR_SCHEMA);
        Assert.assertTrue(roleXSD.exists());
        File userXSD = new File(new File(securityManager.get("security/usergroup").dir(), userService.getName()), FILE_UR_SCHEMA);
        Assert.assertTrue(userXSD.exists());
        /* does not work from the command line

        ServiceAccessRuleDAO sdao = GeoServerExtensions.bean(ServiceAccessRuleDAO.class);
        assertTrue(sdao.getRulesAssociatedWithRole(XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE).isEmpty()==false);
        assertTrue(sdao.getRulesAssociatedWithRole(GeoServerRole.ADMIN_ROLE.getAuthority()).isEmpty());

        DataAccessRuleDAO ddao = GeoServerExtensions.bean(DataAccessRuleDAO.class);
        assertTrue(ddao.getRulesAssociatedWithRole(XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE).isEmpty()==false);
        assertTrue(ddao.getRulesAssociatedWithRole(GeoServerRole.ADMIN_ROLE.getAuthority()).isEmpty());

        RESTAccessRuleDAO rdao = GeoServerExtensions.bean(RESTAccessRuleDAO.class);
        List<String> rules = rdao.getRules();

        boolean found = false;
        for (String rule : rules) {
        if (rule.contains(XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE))
        found=true;
        if (rule.contains(GeoServerRole.ADMIN_ROLE.getAuthority()))
        Assert.fail("Migration of admin role not successful");
        }
        assertTrue(found);
         */
    }
}

