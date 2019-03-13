/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.auth;


import GeoServerSecurityManager.REALM;
import GeoServerUser.ROOT_USERNAME;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import J2EERoleSource.J2EE;
import PreAuthenticatedUserNameRoleSource.Header;
import PreAuthenticatedUserNameRoleSource.RoleService;
import PreAuthenticatedUserNameRoleSource.UserGroupService;
import RequestMethod.GET;
import java.security.Principal;
import javax.servlet.http.Cookie;
import org.geoserver.security.config.PreAuthenticatedUserNameFilterConfig.PreAuthenticatedUserNameRoleSource;
import org.geoserver.security.filter.GeoServerBasicAuthenticationFilter;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.password.MasterPasswordProviderConfig;
import org.geoserver.test.RunTestSetup;
import org.geoserver.test.SystemTest;
import org.geotools.data.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Category(SystemTest.class)
public class AuthenticationCacheFilterTest extends AbstractAuthenticationProviderTest {
    public static final String testFilterName = "basicAuthTestFilter";

    public static final String testFilterName2 = "digestAuthTestFilter";

    public static final String testFilterName3 = "j2eeAuthTestFilter";

    public static final String testFilterName4 = "requestHeaderTestFilter";

    public static final String testFilterName5 = "basicAuthTestFilterWithRememberMe";

    public static final String testFilterName8 = "x509TestFilter";

    @Test
    public void testBasicAuth() throws Exception {
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod(GET.toString());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        String tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check success
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((testUserName) + ":") + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName, testUserName, null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // check wrong password
        // request= createRequest("/foo/bar");
        // response= new MockHttpServletResponse();
        // chain = new MockFilterChain();
        // 
        // request.addHeader("Authorization",  "Basic " +
        // new String(Base64.encodeBytes((testUserName+":wrongpass").getBytes())));
        // getProxy().doFilter(request, response, chain);
        // tmp = response.getHeader("WWW-Authenticate");
        // assertNotNull(tmp);
        // assert(tmp.indexOf(GeoServerSecurityManager.REALM) !=-1 );
        // assert(tmp.indexOf("Basic") !=-1 );
        // assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getErrorCode());
        // assertNull(SecurityContextHolder.getContext().getAuthentication());
        // auth = getAuth(testFilterName, testUserName,null,null);
        // assertNull(auth);
        // check unknown user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes(("unknwon:" + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        auth = getAuth("unknow", testPassword, null, null);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check root user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((GeoServerUser.ROOT_USERNAME) + ":") + (getMasterPassword())).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        auth = getAuth(ROOT_USERNAME, "geoserver", null, null);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user
        updateUser("ug1", testUserName, false);
        // since the cache is working, disabling has no effect
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((testUserName) + ":") + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName, testUserName, null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // clear cache, user should be disabled
        getCache().removeAll();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((testUserName) + ":") + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", testUserName, true);
    }

    @Test
    public void testJ2eeProxy() throws Exception {
        J2eeAuthenticationFilterConfig config = new J2eeAuthenticationFilterConfig();
        config.setClassName(GeoServerJ2eeAuthenticationFilter.class.getName());
        config.setName(AuthenticationCacheFilterTest.testFilterName3);
        config.setRoleSource(J2EE);
        config.setRoleServiceName("rs1");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName3);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // test preauthenticated with dedicated role service
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setUserPrincipal(new Principal() {
            @Override
            public String getName() {
                return testUserName;
            }
        });
        if (true) {
            request.addUserRole(derivedRole);
        }
        if (false) {
            request.addUserRole(rootRole);
        }
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName3, testUserName, null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, auth.getPrincipal());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // test root
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setUserPrincipal(new Principal() {
            @Override
            public String getName() {
                return GeoServerUser.ROOT_USERNAME;
            }
        });
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName3, ROOT_USERNAME, null, null);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        config.setRoleServiceName(null);
        getSecurityManager().saveFilter(config);
        // test preauthenticated with active role service
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setUserPrincipal(new Principal() {
            @Override
            public String getName() {
                return testUserName;
            }
        });
        if (true) {
            request.addUserRole(derivedRole);
        }
        if (false) {
            request.addUserRole(rootRole);
        }
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName3, testUserName, null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, auth.getPrincipal());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testRequestHeaderProxy() throws Exception {
        RequestHeaderAuthenticationFilterConfig config = new RequestHeaderAuthenticationFilterConfig();
        config.setClassName(GeoServerRequestHeaderAuthenticationFilter.class.getName());
        config.setName(AuthenticationCacheFilterTest.testFilterName4);
        config.setRoleServiceName("rs1");
        config.setPrincipalHeaderAttribute("principal");
        config.setRoleSource(RoleService);
        config.setUserGroupServiceName("ug1");
        config.setPrincipalHeaderAttribute("principal");
        config.setRolesHeaderAttribute("roles");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName4);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
            getCache().removeAll();
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            request.addHeader("principal", testUserName);
            if (rs.equals(Header)) {
                request.addHeader("roles", (((derivedRole) + ";") + (rootRole)));
            }
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName4, testUserName, null, null);
            if (rs.equals(Header)) {
                continue;// no cache

            }
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals(testUserName, auth.getPrincipal());
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        }
        // unknown user
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
            getCache().removeAll();
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            config.setRoleSource(rs);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            request.addHeader("principal", "unknown");
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            if (rs.equals(Header)) {
                continue;// no cache

            }
            Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName4, "unknown", null, null);
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals("unknown", auth.getPrincipal());
        }
        // test disabled user, should not work since cache is active
        config.setRoleSource(UserGroupService);
        // saving a filter empties the cache
        getSecurityManager().saveFilter(config);
        updateUser("ug1", testUserName, false);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addHeader("principal", testUserName);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName4, testUserName, null, null);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", testUserName, true);
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testDigestAuth() throws Exception {
        DigestAuthenticationFilterConfig config = new DigestAuthenticationFilterConfig();
        config.setClassName(GeoServerDigestAuthenticationFilter.class.getName());
        config.setName(AuthenticationCacheFilterTest.testFilterName2);
        config.setUserGroupServiceName("ug1");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName2);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        String tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // test successful login
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        String headerValue = clientDigestString(tmp, testUserName, testPassword, request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, testUserName, 300, 300);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // check wrong password
        // request= createRequest("/foo/bar");
        // response= new MockHttpServletResponse();
        // chain = new MockFilterChain();
        // 
        // headerValue=clientDigestString(tmp, testUserName, "wrongpass",
        // request.getMethod());
        // request.addHeader("Authorization",  headerValue);
        // getProxy().doFilter(request, response, chain);
        // tmp = response.getHeader("WWW-Authenticate");
        // assertNotNull(tmp);
        // assert(tmp.indexOf(GeoServerSecurityManager.REALM) !=-1 );
        // assert(tmp.indexOf("Digest") !=-1 );
        // assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getErrorCode());
        // auth = getAuth(testFilterName2, testUserName,300,300);
        // assertNull(auth);
        // assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check unknown user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        headerValue = clientDigestString(tmp, "unknown", testPassword, request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, "unknown", 300, 300);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check root user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        // We need to enable Master Root login first
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        headerValue = clientDigestString(tmp, ROOT_USERNAME, getMasterPassword(), request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, ROOT_USERNAME, 300, 300);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check root user with wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        headerValue = clientDigestString(tmp, ROOT_USERNAME, "geoserver1", request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, ROOT_USERNAME, 300, 300);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user, should not work becaus of cache
        updateUser("ug1", testUserName, false);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        headerValue = clientDigestString(tmp, testUserName, testPassword, request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, testUserName, 300, 300);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // clear cache, now disabling should work
        getCache().removeAll();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        headerValue = clientDigestString(tmp, "unknown", testPassword, request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, testUserName, 300, 300);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", testUserName, true);
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testBasicAuthWithRememberMe() throws Exception {
        BasicAuthenticationFilterConfig config = new BasicAuthenticationFilterConfig();
        config.setClassName(GeoServerBasicAuthenticationFilter.class.getName());
        config.setUseRememberMe(true);
        config.setName(AuthenticationCacheFilterTest.testFilterName5);
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName5, GeoServerSecurityFilterChain.REMEMBER_ME_FILTER);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(0, response.getCookies().length);
        String tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        // check success
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes("abc@xyz.com:abc".getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName5, "abc@xyz.com", null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(1, response.getCookies().length);
        Cookie cookie = ((Cookie) (response.getCookies()[0]));
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        request.setCookies(cookie);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName5, "abc@xyz.com", null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals("abc@xyz.com", getUsername());
        // assertTrue(auth.getAuthorities().contains(new GeoServerRole(rootRole)));
        // assertTrue(auth.getAuthorities().contains(new GeoServerRole(derivedRole)));
        // send cookie + auth header
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        request.setCookies(cookie);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes("abc@xyz.com:abc".getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName5, "abc@xyz.com", null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals("abc@xyz.com", getUsername());
        // check no remember me for root user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((GeoServerUser.ROOT_USERNAME) + ":") + (getMasterPassword())).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // Let's try again by allowing the Master Root to login
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        response = new MockHttpServletResponse();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName5, ROOT_USERNAME, null, null);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        // no cookie for root user
        Assert.assertEquals(0, response.getCookies().length);
        // check disabled user
        updateUser("ug1", "abc@xyz.com", false);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        request.setCookies(cookie);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // check for cancel cookie
        Assert.assertEquals(1, response.getCookies().length);
        Cookie cancelCookie = ((Cookie) (response.getCookies()[0]));
        Assert.assertNull(cancelCookie.getValue());
        updateUser("ug1", "abc@xyz.com", true);
    }

    @Test
    public void testX509Auth() throws Exception {
        X509CertificateAuthenticationFilterConfig config = new X509CertificateAuthenticationFilterConfig();
        config.setClassName(GeoServerX509CertificateAuthenticationFilter.class.getName());
        config.setName(AuthenticationCacheFilterTest.testFilterName8);
        config.setRoleServiceName("rs1");
        config.setRoleSource(RoleService);
        config.setUserGroupServiceName("ug1");
        config.setRolesHeaderAttribute("roles");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName8);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
            getCache().removeAll();
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            if (rs.equals(Header)) {
                request.addHeader("roles", (((derivedRole) + ";") + (rootRole)));
            }
            setCertifacteForUser(testUserName, request);
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            if (rs.equals(Header)) {
                continue;// no cache

            }
            Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName8, testUserName, null, null);
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals(testUserName, auth.getPrincipal());
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        }
        // unknown user
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
            getCache().removeAll();
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            config.setRoleSource(rs);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            // TODO
            setCertifacteForUser("unknown", request);
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            if (rs.equals(Header)) {
                continue;// no cache

            }
            Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName8, "unknown", null, null);
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals("unknown", auth.getPrincipal());
        }
        // test disabled user, should not work because of active cache
        updateUser("ug1", testUserName, false);
        config.setRoleSource(UserGroupService);
        // saving the filter clears the cache
        getSecurityManager().saveFilter(config);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        setCertifacteForUser(testUserName, request);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName8, testUserName, 0, 0);
        Assert.assertNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", testUserName, true);
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    @RunTestSetup
    public void testCascadingFilters() throws Exception {
        DigestAuthenticationFilterConfig config = new DigestAuthenticationFilterConfig();
        config.setClassName(GeoServerDigestAuthenticationFilter.class.getName());
        config.setName(AuthenticationCacheFilterTest.testFilterName2);
        config.setUserGroupServiceName("ug1");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationCacheFilterTest.testFilterName, AuthenticationCacheFilterTest.testFilterName2);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point, must be digest
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        String tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // test successful login for digest
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        String headerValue = clientDigestString(tmp, testUserName, testPassword, request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        Authentication auth = getAuth(AuthenticationCacheFilterTest.testFilterName2, testUserName, 300, 300);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // check success for basic authentication
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((testUserName) + ":") + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        auth = getAuth(AuthenticationCacheFilterTest.testFilterName, testUserName, null, null);
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
    }
}

