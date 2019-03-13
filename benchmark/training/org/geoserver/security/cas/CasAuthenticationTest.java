/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.cas;


import GeoServerCasAuthenticationEntryPoint.CAS_REDIRECT;
import GeoServerCasConstants.CAS_ASSERTION_KEY;
import GeoServerCasConstants.LOGIN_URI;
import GeoServerCasConstants.LOGOUT_URI;
import GeoServerRole.ADMIN_ROLE;
import GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER;
import GeoServerUser.ROOT_USERNAME;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import PreAuthenticatedUserNameRoleSource.UserGroupService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import org.geoserver.security.GeoServerSecurityFilterChainProxy;
import org.geoserver.security.LogoutFilterChain;
import org.geoserver.security.ServiceLoginFilterChain;
import org.geoserver.security.auth.AbstractAuthenticationProviderTest;
import org.geoserver.security.auth.TestingAuthenticationCache;
import org.geoserver.security.filter.GeoServerLogoutFilter;
import org.geoserver.security.impl.GeoServerUser;
import org.jasig.cas.client.validation.Assertion;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static GeoServerCasAuthenticationEntryPoint.CAS_REDIRECT;


/**
 * A running cas server is needed
 *
 * <p>To activate the test a file ".geoserver/cas.properties" in the home directory is needed.
 *
 * <p>Content # Fixture for cas # casserverurlprefix=https://ux-server02.mc-home.local:8443/cas
 * service=https://ux-desktop03.mc-home.local:4711/geoserver/j_spring_cas_security_check
 * proxycallbackurlprefix=https://ux-desktop03.mc-home.local:4711/geoserver/
 *
 * <p>Client ssl configuration: Create a keystore keystore.jks in home_dir/.geoserver with key store
 * key password "changeit"
 *
 * <p>Create self signing certificate keytool -genkey -alias mc-home.local -keystore rsa-keystore
 * -keyalg RSA -sigalg MD5withRSA -validity 365000
 *
 * <p>Only the cn must be set to the full server name "ux-desktop03.mc-home.local"
 *
 * <p>Export the certificate keytool -export -alias mc-home.local -keystore keystore.jks -file
 * ux-desktop03.crt
 *
 * <p>For the cas server copy ux-desktop03.crt to the server
 *
 * <p>Find cacerts file for the virtual machine running cas
 *
 * <p>Import the certificate
 *
 * <p>keytool -import -trustcacerts -alias mc-home.local -file ux-desktop03.crt \ -keystore
 * /usr/lib/jvm/java-6-sun-1.6.0.26/jre/lib/security/cacerts
 *
 * <p>The keystore password for cacerts is "changeit"
 *
 * <p>Next, export the certificate of tomcat and import it into the cacerts of your java sdk
 *
 * @author christian
 */
public class CasAuthenticationTest extends AbstractAuthenticationProviderTest {
    static URL casServerURLPrefix;

    static URL serviceUrl;

    static URL loginUrl;

    static URL proxyCallbackUrlPrefix;

    static HttpsServer httpsServer;

    public class HttpsProxyCallBackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            URI uri = ex.getRequestURI();
            ex.getRequestBody().close();
            LOGGER.info(("Cas proxy callback: " + (uri.toString())));
            String query = uri.getQuery();
            MockHttpServletRequest request = createRequest(GeoServerCasConstants.CAS_PROXY_RECEPTOR_PATTERN);
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();
            // CAS sends the callback twice, the first time without parameters
            if (query != null) {
                request.setQueryString(query);
                String[] kvps = query.split("&");
                for (String kvp : kvps) {
                    String[] tmp = kvp.split("=");
                    request.addParameter(tmp[0], tmp[1]);
                }
            }
            try {
                getProxy().doFilter(request, response, chain);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            Assert.assertEquals(SC_OK, response.getStatus());
            ex.sendResponseHeaders(200, 0);
            ex.getResponseBody().close();
        }
    }

    public class SingleSignOutHandler implements HttpHandler {
        private String service;

        public String getService() {
            return service;
        }

        public SingleSignOutHandler(String servicePath) {
            service = servicePath;
        }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(ex.getRequestBody()));
            String line = "";
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line);
            } 
            in.close();
            MockHttpServletRequest request = createRequest(service);
            request.setMethod("POST");
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();
            String paramValue = URLDecoder.decode(buff.toString(), "utf-8");
            request.addParameter("logoutRequest", paramValue.substring(((paramValue.indexOf("=")) + 1)));
            try {
                GeoServerSecurityFilterChainProxy proxy = getProxy();
                System.out.println(("SERVCIE: " + (service)));
                System.out.println(("URL: " + (request.getRequestURL().toString())));
                for (SecurityFilterChain c : proxy.getFilterChains()) {
                    System.out.println(c.toString());
                }
                proxy.doFilter(request, response, chain);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            Assert.assertEquals(SC_OK, response.getStatus());
            ex.sendResponseHeaders(200, 0);
            ex.getResponseBody().close();
        }
    }

    @Test
    public void testCASLogin() throws Exception {
        String casFilterName = "testCasFilter1";
        CasAuthenticationFilterConfig config = new CasAuthenticationFilterConfig();
        config.setClassName(GeoServerCasAuthenticationFilter.class.getName());
        config.setCasServerUrlPrefix(CasAuthenticationTest.casServerURLPrefix.toString());
        config.setName(casFilterName);
        config.setRoleSource(UserGroupService);
        config.setUserGroupServiceName("ug1");
        config.setSingleSignOut(true);
        getSecurityManager().saveFilter(config);
        prepareFilterChain(pattern, casFilterName);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String redirectURL = response.getHeader("Location");
        Assert.assertTrue(redirectURL.contains(LOGIN_URI));
        Assert.assertTrue(redirectURL.endsWith("bar"));
        // test success
        String username = "castest";
        String password = username;
        CasFormAuthenticationHelper helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        String ticket = loginUsingTicket(helper, request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(username, auth.getPrincipal());
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(auth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        Assert.assertNotNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
        // check unknown user
        username = "unknown";
        password = username;
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        ticket = loginUsingTicket(helper, request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(ctx.getAuthentication());
        Assert.assertEquals(username, auth.getPrincipal());
        Assert.assertEquals(1, auth.getAuthorities().size());
        Assert.assertNotNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
        // test root user
        username = GeoServerUser.ROOT_USERNAME;
        password = username;
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        ticket = loginUsingTicket(helper, request, response, chain);
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // checkForAuthenticatedRole(auth);
        Assert.assertEquals(ROOT_USERNAME, auth.getPrincipal());
        Assert.assertTrue(((auth.getAuthorities().size()) == 1));
        Assert.assertTrue(auth.getAuthorities().contains(ADMIN_ROLE));
        Assert.assertNotNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
        // check disabled user
        username = "castest";
        password = username;
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        updateUser("ug1", username, false);
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        ticket = loginUsingTicket(helper, request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        redirectURL = response.getHeader("Location");
        Assert.assertTrue(redirectURL.contains("login"));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Assert.assertNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        updateUser("ug1", username, true);
        ssoLogout();
        insertAnonymousFilter();
        request = createRequest("foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
        // test invalid ticket
        username = "castest";
        password = username;
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        ticket = getServiceTicket(new URL(request.getRequestURL().toString()));
        ticket += "ST-A";
        request.addParameter("ticket", ticket);
        request.setQueryString(("ticket=" + ticket));
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        redirectURL = response.getHeader("Location");
        Assert.assertTrue(redirectURL.contains(LOGIN_URI));
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNull(ctx);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Assert.assertNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
        // test success with proxy granting ticket
        config.setProxyCallbackUrlPrefix(CasAuthenticationTest.proxyCallbackUrlPrefix.toString());
        getSecurityManager().saveFilter(config);
        username = "castest";
        password = username;
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        ticket = getServiceTicket(new URL(request.getRequestURL().toString()));
        request.addParameter("ticket", ticket);
        request.setQueryString(("ticket=" + ticket));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // assertTrue(response.wasRedirectSent());
        // redirectUrl = response.getHeader("Location");
        // assertNotNull(redirectUrl);
        ctx = ((SecurityContext) (request.getSession(true).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        PreAuthenticatedAuthenticationToken casAuth = ((PreAuthenticatedAuthenticationToken) (ctx.getAuthentication()));
        Assert.assertNotNull(casAuth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(casAuth);
        Assert.assertEquals(username, casAuth.getPrincipal());
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        Assertion ass = ((Assertion) (request.getSession(true).getAttribute(CAS_ASSERTION_KEY)));
        Assert.assertNotNull(ass);
        String proxyTicket = ass.getPrincipal().getProxyTicketFor("http://localhost/blabla");
        Assert.assertNotNull(proxyTicket);
        Assert.assertNotNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
    }

    @Test
    public void testLogout() throws Exception {
        LogoutFilterChain logoutchain = ((LogoutFilterChain) (getSecurityManager().getSecurityConfig().getFilterChain().getRequestChainByName("webLogout")));
        String casFilterName = "testCasFilter2";
        CasAuthenticationFilterConfig config = new CasAuthenticationFilterConfig();
        config.setClassName(GeoServerCasAuthenticationFilter.class.getName());
        config.setCasServerUrlPrefix(CasAuthenticationTest.casServerURLPrefix.toString());
        config.setName(casFilterName);
        config.setRoleSource(UserGroupService);
        config.setUserGroupServiceName("ug1");
        config.setSingleSignOut(true);
        getSecurityManager().saveFilter(config);
        // put a CAS filter on an active chain
        prepareFilterChain(pattern, casFilterName);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        getCache().removeAll();
        // login
        String username = "castest";
        String password = username;
        CasFormAuthenticationHelper helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        MockHttpServletRequest request = createRequest(pattern);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        loginUsingTicket(helper, request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        MockHttpSession session = ((MockHttpSession) (request.getSession(false)));
        Assert.assertNotNull(session);
        Assert.assertFalse(session.isInvalid());
        // logout triggered by geoserver
        request = createRequest(logoutchain.getPatterns().get(0));
        // request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
        SecurityContextHolder.setContext(ctx);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        // getProxy().doFilter(request, response, chain);
        GeoServerLogoutFilter logoutFilter = ((GeoServerLogoutFilter) (getSecurityManager().loadFilter(FORM_LOGOUT_FILTER)));
        logoutFilter.doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String redirectUrl = response.getHeader("Location");
        Assert.assertNotNull(redirectUrl);
        Assert.assertTrue(redirectUrl.contains(LOGOUT_URI));
        session = ((MockHttpSession) (request.getSession(false)));
        // login
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, password);
        helper.ssoLogin();
        request = createRequest(pattern);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        String ticket = loginUsingTicket(helper, request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        session = ((MockHttpSession) (request.getSession(false)));
        Assert.assertNotNull(session);
        Assert.assertFalse(session.isInvalid());
        // logout triggered by cas server
        request = createRequest(pattern);
        // request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
        SecurityContextHolder.setContext(ctx);
        request.setMethod("POST");
        request.setSession(session);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("logoutRequest", getBodyForLogoutRequest(ticket));
        GeoServerCasAuthenticationFilter casFilter = ((GeoServerCasAuthenticationFilter) (getSecurityManager().loadFilter(casFilterName)));
        // getProxy().doFilter(request, response, chain);
        casFilter.doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        redirectUrl = response.getHeader("Location");
        Assert.assertNotNull(redirectUrl);
        Assert.assertFalse(redirectUrl.contains(LOGOUT_URI));
    }

    @Test
    public void testAuthWithServiceTicket() throws Exception {
        pattern = "/wms/**";
        String casProxyFilterName = "testCasProxyFilter1";
        CasAuthenticationFilterConfig pconfig1 = new CasAuthenticationFilterConfig();
        pconfig1.setClassName(GeoServerCasAuthenticationFilter.class.getName());
        pconfig1.setName(casProxyFilterName);
        pconfig1.setCasServerUrlPrefix(CasAuthenticationTest.casServerURLPrefix.toString());
        pconfig1.setRoleSource(UserGroupService);
        pconfig1.setUserGroupServiceName("ug1");
        pconfig1.setSingleSignOut(true);
        getSecurityManager().saveFilter(pconfig1);
        prepareFilterChain(ServiceLoginFilterChain.class, pattern, casProxyFilterName);
        SecurityContextHolder.getContext().setAuthentication(null);
        // test entry point
        MockHttpServletRequest request = createRequest("wms");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        request.addParameter("ticket", "ST-blabla");
        request.setQueryString("ticket=ST-blabla");
        request.addHeader(CAS_REDIRECT, "false");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // test successful
        getCache().removeAll();
        String username = "castest";
        CasFormAuthenticationHelper helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        helper.ssoLogin();
        request = createRequest("wms");
        request.setQueryString("request=getCapabilities");
        request.addHeader(CAS_REDIRECT, "false");
        String ticket = getServiceTicket(new URL((((request.getRequestURL().toString()) + "?") + (request.getQueryString()))));
        Assert.assertNotNull(ticket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", ticket);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        TestingAuthenticationCache cache = getCache();
        Authentication casAuth = cache.get(casProxyFilterName, username);
        Assert.assertNotNull(casAuth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(casAuth);
        Assert.assertEquals(username, casAuth.getPrincipal());
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        Assert.assertNotNull(request.getAttribute(CAS_ASSERTION_KEY));
        Assert.assertNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(ticket));
        ssoLogout();
        // check unknown user
        username = "unknown";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        helper.ssoLogin();
        request = createRequest("wms");
        ticket = getServiceTicket(new URL(request.getRequestURL().toString()));
        Assert.assertNotNull(ticket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", ticket);
        request.setQueryString(("ticket=" + ticket));
        request.addHeader(CAS_REDIRECT, "false");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        cache = getCache();
        casAuth = cache.get(casProxyFilterName, username);
        Assert.assertNotNull(casAuth);
        Assert.assertNotNull(casAuth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(casAuth);
        Assert.assertEquals(username, casAuth.getPrincipal());
        Assert.assertEquals(1, casAuth.getAuthorities().size());
        Assert.assertNotNull(request.getAttribute(CAS_ASSERTION_KEY));
        // check for disabled user
        getCache().removeAll();
        updateUser("ug1", "castest", false);
        username = "castest";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        helper.ssoLogin();
        request = createRequest("wms");
        ticket = getServiceTicket(new URL(request.getRequestURL().toString()));
        Assert.assertNotNull(ticket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", ticket);
        request.setQueryString(("ticket=" + ticket));
        request.addHeader(CAS_REDIRECT, "false");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        cache = getCache();
        casAuth = cache.get(casProxyFilterName, ticket);
        Assert.assertNull(casAuth);
        Assert.assertNull(request.getAttribute(CAS_ASSERTION_KEY));
        Assert.assertNull(request.getSession(false));
        updateUser("ug1", "castest", true);
        ssoLogout();
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("wms");
        request.addHeader(CAS_REDIRECT, "false");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
        // test proxy granting ticket
        pconfig1.setProxyCallbackUrlPrefix(CasAuthenticationTest.proxyCallbackUrlPrefix.toString());
        getSecurityManager().saveFilter(pconfig1);
        getCache().removeAll();
        username = "castest";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        authenticateWithPGT(helper);
        request = createRequest("wms");
        ticket = getServiceTicket(new URL(request.getRequestURL().toString()));
        request.addHeader(CAS_REDIRECT, "false");
        Assert.assertNotNull(ticket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", ticket);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        cache = getCache();
        casAuth = cache.get(casProxyFilterName, username);
        Assert.assertNotNull(casAuth);
        Assert.assertNotNull(casAuth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(casAuth);
        Assert.assertEquals(username, casAuth.getPrincipal());
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        String proxyTicket = getPrincipal().getProxyTicketFor("http://localhost/blabla");
        Assert.assertNotNull(proxyTicket);
        ssoLogout();
    }

    @Test
    public void testAuthWithProxyTicket() throws Exception {
        pattern = "/wms/**";
        String casProxyFilterName = "testCasProxyFilter2";
        CasAuthenticationFilterConfig pconfig1 = new CasAuthenticationFilterConfig();
        pconfig1.setClassName(GeoServerCasAuthenticationFilter.class.getName());
        pconfig1.setName(casProxyFilterName);
        pconfig1.setCasServerUrlPrefix(CasAuthenticationTest.casServerURLPrefix.toString());
        pconfig1.setRoleSource(UserGroupService);
        pconfig1.setUserGroupServiceName("ug1");
        getSecurityManager().saveFilter(pconfig1);
        prepareFilterChain(ServiceLoginFilterChain.class, pattern, casProxyFilterName);
        // prepareFilterChain(GeoServerCasConstants.CAS_PROXY_RECEPTOR_PATTERN,
        // casFilterName);
        // prepareFilterChain("/j_spring_cas_security_check",
        // GeoServerSecurityFilterChain.SECURITY_CONTEXT_ASC_FILTER,
        // casFilterName);
        SecurityContextHolder.getContext().setAuthentication(null);
        // test entry point with header attribute
        MockHttpServletRequest request = createRequest("wms");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        request.addParameter("ticket", "ST-blabla");
        request.setQueryString("ticket=ST-blabla");
        request.addHeader(CAS_REDIRECT, "false");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // test entry point with url param
        request = createRequest("wms");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", "ST-blabla");
        request.addParameter(CAS_REDIRECT, "false");
        request.setQueryString((("ticket=ST-blabla&" + (CAS_REDIRECT)) + "=false"));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        // test successful
        getCache().removeAll();
        String username = "castest";
        CasFormAuthenticationHelper helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        Assertion ass = authenticateWithPGT(helper);
        String proxyTicket = null;
        for (int i = 0; i < 2; i++) {
            request = createRequest("wms");
            request.setQueryString("request=getCapabilities");
            proxyTicket = ass.getPrincipal().getProxyTicketFor((((request.getRequestURL().toString()) + "?") + (request.getQueryString())));
            Assert.assertNotNull(proxyTicket);
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            request.addParameter("ticket", proxyTicket);
            if (i == 0) {
                request.addParameter(CAS_REDIRECT, "false");
                request.setQueryString(((((((request.getQueryString()) + "&ticket=") + proxyTicket) + "&") + (CAS_REDIRECT)) + "=false"));
            } else {
                request.addHeader(CAS_REDIRECT, "false");
                request.setQueryString((((request.getQueryString()) + "&ticket=") + proxyTicket));
            }
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            TestingAuthenticationCache cache = getCache();
            Authentication casAuth = cache.get(casProxyFilterName, username);
            Assert.assertNotNull(casAuth);
            checkForAuthenticatedRole(casAuth);
            Assert.assertEquals(username, casAuth.getPrincipal());
            Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
            Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
            Assert.assertNotNull(request.getAttribute(CAS_ASSERTION_KEY));
            Assert.assertNull(request.getSession(false));
        }
        Assert.assertNull(GeoServerCasAuthenticationFilter.getHandler().getSessionMappingStorage().removeSessionByMappingId(proxyTicket));
        ssoLogout();
        // check unknown user
        username = "unknown";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        ass = authenticateWithPGT(helper);
        for (int i = 0; i < 2; i++) {
            request = createRequest("wms");
            request.setQueryString("request=getCapabilities");
            proxyTicket = ass.getPrincipal().getProxyTicketFor((((request.getRequestURL().toString()) + "?") + (request.getQueryString())));
            Assert.assertNotNull(proxyTicket);
            response = new MockHttpServletResponse();
            chain = new MockFilterChain();
            request.addParameter("ticket", proxyTicket);
            if (i == 0) {
                request.addParameter(CAS_REDIRECT, "false");
                request.setQueryString(((((((request.getQueryString()) + "&ticket=") + proxyTicket) + "&") + (CAS_REDIRECT)) + "=false"));
            } else {
                request.addHeader(CAS_REDIRECT, "false");
                request.setQueryString((((request.getQueryString()) + "&ticket=") + proxyTicket));
            }
            getProxy().doFilter(request, response, chain);
            Assert.assertEquals(SC_OK, response.getStatus());
            TestingAuthenticationCache cache = getCache();
            Authentication casAuth = cache.get(casProxyFilterName, username);
            Assert.assertNotNull(casAuth);
            checkForAuthenticatedRole(casAuth);
            Assert.assertEquals(username, casAuth.getPrincipal());
            Assert.assertEquals(1, casAuth.getAuthorities().size());
            Assert.assertNotNull(request.getAttribute(CAS_ASSERTION_KEY));
            Assert.assertNull(request.getSession(false));
        }
        ssoLogout();
        // check for disabled user
        getCache().removeAll();
        updateUser("ug1", "castest", false);
        username = "castest";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        ass = authenticateWithPGT(helper);
        request = createRequest("wms");
        proxyTicket = ass.getPrincipal().getProxyTicketFor(request.getRequestURL().toString());
        Assert.assertNotNull(proxyTicket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", proxyTicket);
        request.addParameter(CAS_REDIRECT, "false");
        request.setQueryString((((("ticket=" + proxyTicket) + "&") + (CAS_REDIRECT)) + "=false"));
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_UNAUTHORIZED, response.getStatus());
        TestingAuthenticationCache cache = getCache();
        Authentication casAuth = cache.get(casProxyFilterName, proxyTicket);
        Assert.assertNull(casAuth);
        Assert.assertNull(request.getAttribute(CAS_ASSERTION_KEY));
        Assert.assertNull(request.getSession(false));
        updateUser("ug1", "castest", true);
        ssoLogout();
        // Test anonymous
        insertAnonymousFilter();
        request = createRequest("wms");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
        // test proxy granting ticket in proxied auth filter
        pconfig1.setProxyCallbackUrlPrefix(CasAuthenticationTest.proxyCallbackUrlPrefix.toString());
        getSecurityManager().saveFilter(pconfig1);
        getCache().removeAll();
        username = "castest";
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, username, username);
        ass = authenticateWithPGT(helper);
        request = createRequest("wms");
        proxyTicket = ass.getPrincipal().getProxyTicketFor(request.getRequestURL().toString());
        Assert.assertNotNull(proxyTicket);
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.addParameter("ticket", proxyTicket);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        cache = getCache();
        casAuth = cache.get(casProxyFilterName, username);
        Assert.assertNotNull(casAuth);
        checkForAuthenticatedRole(casAuth);
        Assert.assertEquals(username, casAuth.getPrincipal());
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(rootRole)));
        Assert.assertTrue(casAuth.getAuthorities().contains(new org.geoserver.security.impl.GeoServerRole(derivedRole)));
        proxyTicket = getPrincipal().getProxyTicketFor("http://localhost/blabla");
        Assert.assertNotNull(proxyTicket);
        ssoLogout();
    }

    // protected MockHttpServletRequest createRequest(String url) {
    // MockHttpServletRequest request = super.createRequest(url);
    // request.setProtocol(serviceUrl.getProtocol());
    // request.setScheme(serviceUrl.getProtocol());
    // request.setServerName(serviceUrl.getHost());
    // request.setServerPort(serviceUrl.getPort());
    // return request;
    // }
    @Test
    public void testCasAuthenticationHelper() throws Exception {
        CasFormAuthenticationHelper helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, "fail", "abc");
        Assert.assertFalse(helper.ssoLogin());
        helper = new CasFormAuthenticationHelper(CasAuthenticationTest.casServerURLPrefix, "success", "success");
        Assert.assertTrue(helper.ssoLogin());
        Assert.assertNotNull(getTicketGrantingCookie());
        LOGGER.info(("TGC after login : " + (getTicketGrantingCookie())));
        Assert.assertTrue(ssoLogout());
        Assert.assertNotNull(getTicketGrantingCookie());
        LOGGER.info(("TGC after logout : " + (getTicketGrantingCookie())));
        Assert.assertTrue(helper.ssoLogin());
        Assert.assertNotNull(getTicketGrantingCookie());
        String ticket = getServiceTicket(CasAuthenticationTest.serviceUrl);
        Assert.assertNotNull(ticket);
        Assert.assertTrue(ticket.startsWith("ST-"));
        LOGGER.info(("ST : " + ticket));
        ssoLogout();
    }
}

