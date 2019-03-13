/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import GeoServerRole.ADMIN_ROLE;
import com.google.common.util.concurrent.ExecutionError;
import java.io.IOException;
import java.util.SortedSet;
import org.geoserver.security.impl.GeoServerRole;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions S.A.S.
 */
public class GeoServerRestRoleServiceTest {
    public static final String uri = "http://rest.geoserver.org";

    private RestTemplate template;

    private MockRestServiceServer mockServer;

    @Test
    public void testGeoServerRestRoleService() throws IOException {
        GeoServerRestRoleServiceConfig roleServiceconfig = new GeoServerRestRoleServiceConfig();
        roleServiceconfig.setBaseUrl(GeoServerRestRoleServiceTest.uri);
        GeoServerRestRoleService roleService = new GeoServerRestRoleService(roleServiceconfig);
        roleService.setRestTemplate(template);
        final SortedSet<GeoServerRole> roles = roleService.getRoles();
        final GeoServerRole adminRole = roleService.getAdminRole();
        final SortedSet<GeoServerRole> testUserRoles = roleService.getRolesForUser("test");
        final SortedSet<GeoServerRole> testUserEmailRoles = roleService.getRolesForUser("test@geoserver.org");
        final SortedSet<GeoServerRole> adminUserRoles = roleService.getRolesForUser("admin");
        Assert.assertNotNull(roles);
        Assert.assertNotNull(adminRole);
        Assert.assertNotNull(testUserRoles);
        Assert.assertNotNull(testUserEmailRoles);
        Assert.assertNotNull(adminUserRoles);
        Assert.assertEquals(3, roles.size());
        Assert.assertEquals("ROLE_ADMIN", adminRole.getAuthority());
        Assert.assertEquals(testUserEmailRoles.size(), testUserRoles.size());
        Assert.assertTrue((!(testUserRoles.contains(ADMIN_ROLE))));
        Assert.assertTrue((!(testUserRoles.contains(adminRole))));
        Assert.assertTrue(adminUserRoles.contains(ADMIN_ROLE));
    }

    @Test
    public void testGeoServerRestRoleServiceInternalCache() throws IOException, InterruptedException {
        GeoServerRestRoleServiceConfig roleServiceconfig = new GeoServerRestRoleServiceConfig();
        roleServiceconfig.setBaseUrl(GeoServerRestRoleServiceTest.uri);
        GeoServerRestRoleService roleService = new GeoServerRestRoleService(roleServiceconfig);
        roleService.setRestTemplate(template);
        roleService.getRoles();
        roleService.getAdminRole();
        roleService.getRolesForUser("test");
        Thread.sleep((31 * 1000));
        try {
            roleService.getRolesForUser("test@geoserver.org");
            Assert.fail("Expecting ExecutionError to be thrown");
        } catch (ExecutionError e) {
            // OK
        }
    }
}

