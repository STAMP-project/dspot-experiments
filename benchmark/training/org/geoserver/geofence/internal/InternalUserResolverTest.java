/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.internal;


import java.util.Arrays;
import java.util.Set;
import org.geoserver.security.AbstractSecurityServiceTest;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerRoleStore;
import org.junit.Assert;
import org.junit.Test;


/**
 * *
 *
 * @author Niels Charlier
 */
public class InternalUserResolverTest extends AbstractSecurityServiceTest {
    protected GeoServerRoleService service;

    protected GeoServerRoleStore store;

    @Test
    public void testInternalUserResolver() throws Exception {
        InternalUserResolver resolver = new InternalUserResolver(getSecurityManager());
        // Test the Security Manager default UserGroupService
        Assert.assertEquals("default", getSecurityManager().getActiveRoleService().getName());
        Assert.assertTrue(resolver.existsUser("pippo"));
        Assert.assertTrue(resolver.existsUser("jantje"));
        Assert.assertTrue(resolver.existsUser("role_user_test"));
        Assert.assertTrue(resolver.existsRole("ZEVER"));
        Assert.assertTrue(resolver.existsRole("CIRCUS"));
        Assert.assertTrue(resolver.existsRole("MOPJES"));
        Assert.assertTrue(resolver.existsRole("KLINIEK"));
        Assert.assertTrue(resolver.existsRole("ROLE_TEST"));
        Set<String> roles = resolver.getRoles("pippo");
        Assert.assertEquals(3, roles.size());
        Assert.assertTrue(roles.contains("CIRCUS"));
        Assert.assertTrue(roles.contains("ZEVER"));
        Assert.assertTrue(roles.contains("KLINIEK"));
        roles = resolver.getRoles("jantje");
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("ZEVER"));
        Assert.assertTrue(roles.contains("MOPJES"));
        roles = resolver.getRoles("role_user_test");
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("ROLE_TEST"));
        Assert.assertTrue(roles.contains("ROLE_TEST_2"));
        // Test the GeoFence Default User Group / Role Service
        Assert.assertEquals("test", resolver.getDefaultSecurityService().getName());
        Assert.assertTrue(((resolver.getDefaultSecurityService()) instanceof GeoServerRoleService));
        GeoServerRoleStore store = createStore();
        addTestUser("user1", Arrays.asList("adminRole", "groupAdminRole"), service, store);
        addTestUser("user2", Arrays.asList("adminRole"), service, store);
        addTestUser("user3", Arrays.asList("role1"), service, store);
        Assert.assertTrue(((service.getRoleCount()) == 3));
        Assert.assertTrue(((getRoleCount()) == 3));
        Assert.assertTrue(resolver.existsUser("user1"));
        Assert.assertTrue(resolver.existsUser("user2"));
        Assert.assertTrue(resolver.existsUser("user3"));
        Assert.assertTrue(resolver.existsRole("adminRole"));
        Assert.assertTrue(resolver.existsRole("groupAdminRole"));
        Assert.assertTrue(resolver.existsRole("role1"));
        roles = resolver.getRoles("user1");
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("adminRole"));
        Assert.assertTrue(roles.contains("groupAdminRole"));
        roles = resolver.getRoles("user2");
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains("adminRole"));
        roles = resolver.getRoles("user3");
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains("role1"));
    }
}

