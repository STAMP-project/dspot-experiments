/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.ldap;


import java.util.Collections;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.MemoryRoleService;
import org.geoserver.security.impl.MemoryRoleStore;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


/**
 *
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 */
public class LDAPAuthenticationProviderTest extends LDAPBaseTest {
    private LDAPAuthenticationProvider authProvider;

    /**
     * LdapTestUtils Test that bindBeforeGroupSearch correctly enables roles fetching on a server
     * without anonymous access enabled.
     */
    @Test
    public void testBindBeforeGroupSearch() throws Exception {
        // no anonymous access
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        setUserDnPattern("uid={0},ou=People");
        config.setBindBeforeGroupSearch(true);
        createAuthenticationProvider();
        Authentication result = authProvider.authenticate(authentication);
        Assert.assertNotNull(result);
        Assert.assertEquals("admin", result.getName());
        Assert.assertEquals(3, result.getAuthorities().size());
    }

    /**
     * Test that without bindBeforeGroupSearch we get an exception during roles fetching on a server
     * without anonymous access enabled.
     */
    @Test
    public void testBindBeforeGroupSearchRequiredIfAnonymousDisabled() throws Exception {
        // no anonymous access
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(false, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        setUserDnPattern("uid={0},ou=People");
        // we don't bind
        config.setBindBeforeGroupSearch(false);
        createAuthenticationProvider();
        boolean error = false;
        try {
            authProvider.authenticate(authentication);
        } catch (Exception e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    /**
     * Test that authentication can be done using the couple userFilter and userFormat instead of
     * userDnPattern.
     */
    @Test
    public void testUserFilterAndFormat() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        // filter to extract user data
        setUserFilter("(telephonenumber=1)");
        // username to bind to
        setUserFormat("uid={0},ou=People,dc=example,dc=com");
        createAuthenticationProvider();
        Authentication result = authProvider.authenticate(authentication);
        Assert.assertEquals(3, result.getAuthorities().size());
    }

    /**
     * Test that authentication can be done using the couple userFilter and userFormat instead of
     * userDnPattern, using placemarks in userFilter.
     */
    @Test
    public void testUserFilterPlacemarks() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        // filter to extract user data
        setUserFilter("(givenName={1})");
        // username to bind to
        setUserFormat("uid={0},ou=People,dc=example,dc=com");
        createAuthenticationProvider();
        Authentication result = authProvider.authenticate(authentication);
        Assert.assertEquals(3, result.getAuthorities().size());
        // filter to extract user data
        setUserFilter("(cn={0})");
        // username to bind to
        setUserFormat("uid={0},ou=People,dc=example,dc=com");
        createAuthenticationProvider();
        result = authProvider.authenticate(authentication);
        Assert.assertEquals(3, result.getAuthorities().size());
    }

    /**
     * Test that if and adminGroup is defined, the roles contain ROLE_ADMINISTRATOR
     */
    @Test
    public void testAdminGroup() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        setUserDnPattern("uid={0},ou=People");
        config.setAdminGroup("other");
        createAuthenticationProvider();
        Authentication result = authProvider.authenticate(authenticationOther);
        boolean foundAdmin = false;
        for (GrantedAuthority authority : result.getAuthorities()) {
            if (authority.getAuthority().equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
                foundAdmin = true;
            }
        }
        Assert.assertTrue(foundAdmin);
    }

    /**
     * Test that if and groupAdminGroup is defined, the roles contain ROLE_GROUP_ADMIN
     */
    @Test
    public void testGroupAdminGroup() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        setUserDnPattern("uid={0},ou=People");
        config.setGroupAdminGroup("other");
        createAuthenticationProvider();
        Authentication result = authProvider.authenticate(authenticationOther);
        boolean foundAdmin = false;
        for (GrantedAuthority authority : result.getAuthorities()) {
            if (authority.getAuthority().equalsIgnoreCase("ROLE_GROUP_ADMIN")) {
                foundAdmin = true;
            }
        }
        Assert.assertTrue(foundAdmin);
    }

    /**
     * Test that active role service is applied in the LDAPAuthenticationProvider
     */
    @Test
    public void testRoleService() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath));
        setUserDnPattern("uid={0},ou=People");
        createAuthenticationProvider();
        authProvider.setSecurityManager(securityManager);
        securityManager.setProviders(Collections.singletonList(authProvider));
        MemoryRoleStore roleService = new MemoryRoleStore();
        roleService.initializeFromService(new MemoryRoleService());
        roleService.setSecurityManager(securityManager);
        GeoServerRole role = roleService.createRoleObject("MyRole");
        roleService.addRole(role);
        roleService.associateRoleToUser(role, "other");
        securityManager.setActiveRoleService(roleService);
        Authentication result = authProvider.authenticate(authenticationOther);
        Assert.assertTrue(result.getAuthorities().contains(role));
        Assert.assertEquals(3, result.getAuthorities().size());
    }

    /**
     * Test that LDAPAuthenticationProvider finds roles even if there is a colon in the password
     */
    @Test
    public void testColonPassword() throws Exception {
        Assume.assumeTrue(LDAPTestUtils.initLdapServer(true, LDAPBaseTest.ldapServerUrl, LDAPBaseTest.basePath, "data3.ldif"));
        setUserDnPattern("uid={0},ou=People");
        createAuthenticationProvider();
        authentication = new UsernamePasswordAuthenticationToken("colon", "da:da");
        Authentication result = authProvider.authenticate(authentication);
        Assert.assertEquals(2, result.getAuthorities().size());
    }
}

