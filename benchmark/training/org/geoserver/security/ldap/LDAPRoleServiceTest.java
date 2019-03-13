/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.ldap;


import org.geoserver.security.GeoServerRoleService;
import org.junit.Assume;
import org.junit.Test;


public class LDAPRoleServiceTest extends LDAPBaseTest {
    GeoServerRoleService service;

    @Test
    public void testGetRoles() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkAllRoles();
    }

    @Test
    public void testGetRolesAuthenticated() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        configureAuthentication();
        checkAllRoles();
    }

    @Test
    public void testGetRolesCount() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkRoleCount();
    }

    @Test
    public void testGetRolesCountAuthenticated() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        configureAuthentication();
        checkRoleCount();
    }

    @Test
    public void testGetRoleByName() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkRoleByName();
    }

    @Test
    public void testGetRoleByNameAuthenticated() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        configureAuthentication();
        checkRoleByName();
    }

    @Test
    public void testGetAdminRoles() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkAdminRoles();
    }

    @Test
    public void testGetAdminRolesAuthenticated() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        configureAuthentication();
        checkAdminRoles();
    }

    @Test
    public void testGetRolesForUser() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkUserRoles("admin", false);
    }

    @Test
    public void testGetRolesForUserAuthenticated() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        configureAuthentication();
        checkUserRoles("admin", false);
    }

    @Test
    public void testGetRolesForUserUsingUserFilter() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath, "data2.ldif"));
        checkUserRoles("admin", true);
    }

    @Test
    public void testGetRolesForUserAuthenticatedUsingUserFilter() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath, "data2.ldif"));
        configureAuthentication();
        checkUserRoles("admin", true);
    }

    @Test
    public void testGetUserNamesForRole() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        checkUserNamesForRole("admin", 1, false);
        checkUserNamesForRole("other", 2, false);
    }

    @Test
    public void testGetUserNamesForRoleUsingUserFilter() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath, "data2.ldif"));
        checkUserNamesForRole("admin", 1, true);
        checkUserNamesForRole("other", 2, true);
    }
}

