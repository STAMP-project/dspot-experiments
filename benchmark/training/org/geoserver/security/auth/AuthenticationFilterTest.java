/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.auth;


import GeoServerLogoutFilter.URL_AFTER_LOGOUT;
import GeoServerRole.ADMIN_ROLE;
import GeoServerRole.AUTHENTICATED_ROLE;
import GeoServerRoleFilter.DEFAULT_HEADER_ATTRIBUTE;
import GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER;
import GeoServerSecurityManager.REALM;
import GeoServerUser.ROOT_USERNAME;
import GeoServerUserNamePasswordAuthenticationFilter.URL_LOGIN_FAILURE;
import GeoServerUserNamePasswordAuthenticationFilter.URL_LOGIN_FORM;
import GeoServerUserNamePasswordAuthenticationFilter.URL_LOGIN_SUCCCESS;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import J2EERoleSource.J2EE;
import PreAuthenticatedUserNameRoleSource.Header;
import PreAuthenticatedUserNameRoleSource.RoleService;
import PreAuthenticatedUserNameRoleSource.UserGroupService;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.geoserver.security.ConstantFilterChain;
import org.geoserver.security.config.J2eeAuthenticationBaseFilterConfig.J2EERoleSource;
import org.geoserver.security.config.PreAuthenticatedUserNameFilterConfig.PreAuthenticatedUserNameRoleSource;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@Category(SystemTest.class)
public class AuthenticationFilterTest extends AbstractAuthenticationProviderTest {
    public static final String testFilterName = "basicAuthTestFilter";

    public static final String testFilterName2 = "digestAuthTestFilter";

    public static final String testFilterName3 = "j2eeAuthTestFilter";

    public static final String testFilterName4 = "requestHeaderTestFilter";

    public static final String testFilterName5 = "basicAuthTestFilterWithRememberMe";

    public static final String testFilterName6 = "formLoginTestFilter";

    public static final String testFilterName7 = "formLoginTestFilterWithRememberMe";

    public static final String testFilterName8 = "x509TestFilter";

    public static final String testFilterName9 = "logoutTestFilter";

    public static final String testFilterName10 = "credentialsFromHeaderTestFilter";

    @Test
    public void testBasicAuth() throws Exception {
        // BasicAuthenticationFilterConfig config = new BasicAuthenticationFilterConfig();
        // config.setClassName(GeoServerBasicAuthenticationFilter.class.getName());
        // config.setUseRememberMe(false);
        // config.setName(testFilterName);
        // getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        String tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        modifyChain(pattern, false, true, GeoServerSecurityFilterChain.ROLE_FILTER);
        // check success
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((testUserName) + ":") + (testPassword)).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        String roleString = response.getHeader(DEFAULT_HEADER_ATTRIBUTE);
        Assert.assertNotNull(roleString);
        String[] roles = roleString.split(";");
        Assert.assertEquals(3, roles.length);
        List<String> roleList = Arrays.asList(roles);
        Assert.assertTrue(roleList.contains(AUTHENTICATED_ROLE.getAuthority()));
        Assert.assertTrue(roleList.contains(rootRole));
        Assert.assertTrue(roleList.contains(derivedRole));
        // check wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes(((testUserName) + ":wrongpass").getBytes())))));
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check unknown user
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes(("unknwon:" + (testPassword)).getBytes())))));
        request.setMethod("GET");
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((GeoServerUser.ROOT_USERNAME) + ":") + (getMasterPassword())).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertTrue(((auth.getAuthorities().size()) == 1));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
        // check root user with wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes(((GeoServerUser.ROOT_USERNAME) + ":geoserver1").getBytes())))));
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Basic")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user, clear cache first
        getSecurityManager().getAuthenticationCache().removeAll();
        updateUser("ug1", testUserName, false);
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
    public void testCredentialsFromHeader() throws Exception {
        CredentialsFromRequestHeaderFilterConfig config = new CredentialsFromRequestHeaderFilterConfig();
        config.setClassName(GeoServerCredentialsFromRequestHeaderFilter.class.getName());
        config.setUserNameHeaderName("X-Credentials");
        config.setPasswordHeaderName("X-Credentials");
        config.setUserNameRegex("private-user=([^&]*)");
        config.setPasswordRegex("private-pw=([^&]*)");
        config.setParseAsUriComponents(true);
        config.setName(AuthenticationFilterTest.testFilterName10);
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName10);
        modifyChain(pattern, false, true, null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check success
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("X-Credentials", ((("private-user=" + (testUserName)) + "&private-pw=") + (testPassword)));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // check wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("X-Credentials", (("private-user=" + (testUserName)) + "&private-pw=wrongpass"));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check unknown user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("X-Credentials", ("private-user=wronguser&private-pw=" + (testPassword)));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check root user
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        String masterPassword = URLEncoder.encode(getMasterPassword(), "UTF-8");
        request.addHeader("X-Credentials", ((("private-user=" + (GeoServerUser.ROOT_USERNAME)) + "&private-pw=") + masterPassword));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertTrue(((auth.getAuthorities().size()) == 2));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
        // check root user with wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("X-Credentials", (("private-user=" + (GeoServerUser.ROOT_USERNAME)) + "&private-pw=geoserver1"));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user, clear cache first
        getSecurityManager().getAuthenticationCache().removeAll();
        updateUser("ug1", testUserName, false);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addHeader("X-Credentials", ((("private-user=" + (testUserName)) + "&private-pw=") + (testPassword)));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
    public void testJ2eeProxy() throws Exception {
        J2eeAuthenticationFilterConfig config = new J2eeAuthenticationFilterConfig();
        config.setClassName(GeoServerJ2eeAuthenticationFilter.class.getName());
        config.setName(AuthenticationFilterTest.testFilterName3);
        config.setRoleSource(J2EE);
        config.setRoleServiceName("rs1");
        config.setUserGroupServiceName("ug1");
        config.setRolesHeaderAttribute("roles");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName3);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Authentication auth;
        for (J2EERoleSource rs : J2EERoleSource.values()) {
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            // test preauthenticated with various role sources
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
            if (rs == (J2EERoleSource.Header)) {
                request.addHeader("roles", (((derivedRole) + ";") + (rootRole)));
            }
            if (rs == (J2EERoleSource.J2EE)) {
                if (true) {
                    request.addUserRole(derivedRole);
                }
                if (false) {
                    request.addUserRole(rootRole);
                }
            }
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
            Assert.assertNotNull(ctx);
            auth = ctx.getAuthentication();
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals(testUserName, auth.getPrincipal());
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        }
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertTrue(((auth.getAuthorities().size()) == 1));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
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
        config.setName(AuthenticationFilterTest.testFilterName4);
        config.setRoleServiceName("rs1");
        config.setPrincipalHeaderAttribute("principal");
        config.setRoleSource(RoleService);
        config.setUserGroupServiceName("ug1");
        config.setPrincipalHeaderAttribute("principal");
        config.setRolesHeaderAttribute("roles");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName4);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
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
            ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
            Assert.assertNotNull(ctx);
            Authentication auth = ctx.getAuthentication();
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals(testUserName, auth.getPrincipal());
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        }
        // unknown user
        for (PreAuthenticatedUserNameRoleSource rs : PreAuthenticatedUserNameRoleSource.values()) {
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            config.setRoleSource(rs);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            request.addHeader("principal", "unknwon");
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
            Assert.assertNotNull(ctx);
            Authentication auth = ctx.getAuthentication();
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals("unknwon", auth.getPrincipal());
        }
        // test disabled user
        updateUser("ug1", testUserName, false);
        config.setRoleSource(UserGroupService);
        getSecurityManager().saveFilter(config);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addHeader("principal", testUserName);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        config.setName(AuthenticationFilterTest.testFilterName2);
        config.setUserGroupServiceName("ug1");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName2);
        modifyChain(pattern, false, true, null);
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
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // check wrong password
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        headerValue = clientDigestString(tmp, testUserName, "wrongpass", request.getMethod());
        request.addHeader("Authorization", headerValue);
        getProxy().doFilter(request, response, chain);
        tmp = response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(tmp);
        assert (tmp.indexOf(REALM)) != (-1);
        assert (tmp.indexOf("Digest")) != (-1);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, getUsername());
        Assert.assertTrue(((auth.getAuthorities().size()) == 1));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user
        updateUser("ug1", testUserName, false);
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        config.setName(AuthenticationFilterTest.testFilterName5);
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName5, GeoServerSecurityFilterChain.REMEMBER_ME_FILTER);
        modifyChain(pattern, false, true, null);
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
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
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
        // We need to enable Master Root login first
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        request.addHeader("Authorization", ("Basic " + (new String(Base64.encodeBytes((((GeoServerUser.ROOT_USERNAME) + ":") + (getMasterPassword())).getBytes())))));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
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
    public void testFormLogin() throws Exception {
        UsernamePasswordAuthenticationFilterConfig config = new UsernamePasswordAuthenticationFilterConfig();
        config.setClassName(GeoServerUserNamePasswordAuthenticationFilter.class.getName());
        config.setUsernameParameterName("username");
        config.setPasswordParameterName("password");
        config.setName(AuthenticationFilterTest.testFilterName6);
        getSecurityManager().saveFilter(config);
        // LogoutFilterConfig loConfig = new LogoutFilterConfig();
        // loConfig.setClassName(GeoServerLogoutFilter.class.getName());
        // loConfig.setName(testFilterName9);
        // getSecurityManager().saveFilter(loConfig);
        prepareFilterChain(pattern, GeoServerSecurityFilterChain.FORM_LOGIN_FILTER);
        modifyChain(pattern, false, true, null);
        prepareFilterChain(ConstantFilterChain.class, "/j_spring_security_check_foo/", AuthenticationFilterTest.testFilterName6);
        modifyChain("/j_spring_security_check_foo/", false, true, null);
        // prepareFilterChain(LogoutFilterChain.class,"/j_spring_security_logout_foo",
        // GeoServerSecurityFilterChain.SECURITY_CONTEXT_ASC_FILTER,
        // testFilterName9);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String tmp = response.getHeader("Location");
        Assert.assertTrue(tmp.endsWith(URL_LOGIN_FORM));
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check success
        request = createRequest("/j_spring_security_check_foo");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), testUserName);
        request.addParameter(config.getPasswordParameterName(), testPassword);
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_SUCCCESS));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        // Test logout
        GeoServerLogoutFilter logoutFilter = ((GeoServerLogoutFilter) (getSecurityManager().loadFilter(FORM_LOGOUT_FILTER)));
        request = createRequest("/j_spring_security_logout_foo");
        request.setMethod("GET");
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, ctx);
        SecurityContextHolder.getContext().setAuthentication(auth);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        // getProxy().doFilter(request, response, chain);
        logoutFilter.doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        tmp = response.getHeader("Location");
        Assert.assertNotNull(tmp);
        Assert.assertTrue(tmp.endsWith(URL_AFTER_LOGOUT));
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // test invalid password
        request = createRequest("/j_spring_security_check_foo");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), testUserName);
        request.addParameter(config.getPasswordParameterName(), "wrongpass");
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_FAILURE));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check unknown user
        request = createRequest("/j_spring_security_check_foo");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), "unknwon");
        request.addParameter(config.getPasswordParameterName(), testPassword);
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_FAILURE));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check root user
        request = createRequest("/j_spring_security_check_foo");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), ROOT_USERNAME);
        request.addParameter(config.getPasswordParameterName(), getMasterPassword());
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_SUCCCESS));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertTrue(((auth.getAuthorities().size()) == 1));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
        // check root user with wrong password
        request = createRequest("/j_spring_security_check_foo");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), ROOT_USERNAME);
        request.addParameter(config.getPasswordParameterName(), "geoserver1");
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_FAILURE));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user
        updateUser("ug1", testUserName, false);
        request = createRequest("/j_spring_security_check_foo");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), testUserName);
        request.addParameter(config.getPasswordParameterName(), testPassword);
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_FAILURE));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", testUserName, true);
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testFormLoginWithRememberMe() throws Exception {
        UsernamePasswordAuthenticationFilterConfig config = new UsernamePasswordAuthenticationFilterConfig();
        config.setClassName(GeoServerUserNamePasswordAuthenticationFilter.class.getName());
        config.setUsernameParameterName("username");
        config.setPasswordParameterName("password");
        config.setName(AuthenticationFilterTest.testFilterName7);
        getSecurityManager().saveFilter(config);
        // LogoutFilterConfig loConfig = new LogoutFilterConfig();
        // loConfig.setClassName(GeoServerLogoutFilter.class.getName());
        // loConfig.setName(testFilterName9);
        // getSecurityManager().saveFilter(loConfig);
        prepareFilterChain(pattern, GeoServerSecurityFilterChain.REMEMBER_ME_FILTER, GeoServerSecurityFilterChain.FORM_LOGIN_FILTER);
        modifyChain(pattern, false, true, null);
        prepareFilterChain("/j_spring_security_check_foo/", AuthenticationFilterTest.testFilterName7);
        modifyChain("/j_spring_security_check_foo/", false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.addParameter("_spring_security_remember_me", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String tmp = response.getHeader("Location");
        Assert.assertTrue(tmp.endsWith(URL_LOGIN_FORM));
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check success
        request = createRequest("/j_spring_security_check_foo");
        request.addParameter("_spring_security_remember_me", "yes");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), testUserName);
        request.addParameter(config.getPasswordParameterName(), testPassword);
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_SUCCCESS));
        HttpSession session = request.getSession(true);
        ctx = ((SecurityContext) (session.getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        Assert.assertEquals(1, response.getCookies().length);
        Cookie cookie = ((Cookie) (response.getCookies()[0]));
        Assert.assertNotNull(cookie.getValue());
        // check logout
        GeoServerLogoutFilter logoutFilter = ((GeoServerLogoutFilter) (getSecurityManager().loadFilter(FORM_LOGOUT_FILTER)));
        request = createRequest("/j_spring_security_logout_foo");
        request.setMethod("GET");
        session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, ctx);
        SecurityContextHolder.getContext().setAuthentication(auth);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        // getProxy().doFilter(request, response, chain);
        logoutFilter.doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        tmp = response.getHeader("Location");
        Assert.assertNotNull(tmp);
        Assert.assertTrue(tmp.endsWith(URL_AFTER_LOGOUT));
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Cookie cancelCookie = ((Cookie) (response.getCookies()[0]));
        Assert.assertNull(cancelCookie.getValue());
        // check no remember me for root user
        request = createRequest("/j_spring_security_check_foo");
        request.addParameter("_spring_security_remember_me", "yes");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        // We need to enable Master Root login first
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        request.setMethod("POST");
        request.addParameter(config.getUsernameParameterName(), ROOT_USERNAME);
        request.addParameter(config.getPasswordParameterName(), getMasterPassword());
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Assert.assertTrue(response.getHeader("Location").endsWith(URL_LOGIN_SUCCCESS));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertEquals(0, response.getCookies().length);
        // check disabled user
        updateUser("ug1", testUserName, false);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        request.setCookies(cookie);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        tmp = response.getHeader("Location");
        Assert.assertTrue(tmp.endsWith(URL_LOGIN_FORM));
        // check for cancel cookie
        Assert.assertEquals(1, response.getCookies().length);
        cancelCookie = ((Cookie) (response.getCookies()[0]));
        Assert.assertNull(cancelCookie.getValue());
        updateUser("ug1", testUserName, true);
    }

    @Test
    public void testX509Auth() throws Exception {
        X509CertificateAuthenticationFilterConfig config = new X509CertificateAuthenticationFilterConfig();
        config.setClassName(GeoServerX509CertificateAuthenticationFilter.class.getName());
        config.setName(AuthenticationFilterTest.testFilterName8);
        config.setRoleServiceName("rs1");
        config.setRoleSource(J2EERoleSource.RoleService);
        config.setUserGroupServiceName("ug1");
        config.setRolesHeaderAttribute("roles");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName8);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        for (J2EERoleSource rs : J2EERoleSource.values()) {
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            if (rs == (J2EERoleSource.Header)) {
                request.addHeader("roles", (((derivedRole) + ";") + (rootRole)));
            }
            if (rs == (J2EERoleSource.J2EE)) {
                if (true) {
                    request.addUserRole(derivedRole);
                }
                if (false) {
                    request.addUserRole(rootRole);
                }
            }
            setCertifacteForUser(testUserName, request);
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
            Assert.assertNotNull(ctx);
            Authentication auth = ctx.getAuthentication();
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals(testUserName, auth.getPrincipal());
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        }
        // unknown user
        for (J2EERoleSource rs : J2EERoleSource.values()) {
            config.setRoleSource(rs);
            getSecurityManager().saveFilter(config);
            config.setRoleSource(rs);
            request = createRequest("/foo/bar");
            request.setMethod("GET");
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            if (rs == (J2EERoleSource.J2EE)) {
                if (false) {
                    request.addUserRole(derivedRole);
                }
                if (false) {
                    request.addUserRole(rootRole);
                }
            }
            // TODO
            setCertifacteForUser("unknown", request);
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
            Assert.assertNotNull(ctx);
            Authentication auth = ctx.getAuthentication();
            Assert.assertNotNull(auth);
            Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            checkForAuthenticatedRole(auth);
            Assert.assertEquals("unknown", auth.getPrincipal());
        }
        // test disabled user
        updateUser("ug1", testUserName, false);
        config.setRoleSource(J2EERoleSource.UserGroupService);
        getSecurityManager().saveFilter(config);
        request = createRequest("/foo/bar");
        request.setMethod("GET");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        setCertifacteForUser(testUserName, request);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        // BasicAuthenticationFilterConfig bconfig = new BasicAuthenticationFilterConfig();
        // bconfig.setClassName(GeoServerBasicAuthenticationFilter.class.getName());
        // bconfig.setUseRememberMe(false);
        // bconfig.setName(testFilterName);
        // getSecurityManager().saveFilter(bconfig);
        DigestAuthenticationFilterConfig config = new DigestAuthenticationFilterConfig();
        config.setClassName(GeoServerDigestAuthenticationFilter.class.getName());
        config.setName(AuthenticationFilterTest.testFilterName2);
        config.setUserGroupServiceName("ug1");
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, AuthenticationFilterTest.testFilterName, AuthenticationFilterTest.testFilterName2);
        modifyChain(pattern, false, true, null);
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
        SecurityContext ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
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
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, getUsername());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
    }
}

