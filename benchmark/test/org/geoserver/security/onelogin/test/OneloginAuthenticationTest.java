/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.onelogin.test;


import GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER;
import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import PreAuthenticatedUserNameRoleSource.RoleService;
import PreAuthenticatedUserNameRoleSource.UserGroupService;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.geoserver.security.LogoutFilterChain;
import org.geoserver.security.auth.AbstractAuthenticationProviderTest;
import org.geoserver.security.filter.GeoServerLogoutFilter;
import org.geoserver.security.onelogin.OneloginAuthenticationFilterConfig;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opensaml.common.SAMLObject;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


public class OneloginAuthenticationTest extends AbstractAuthenticationProviderTest {
    private static final String METADATA_URL = "/saml/metadata";

    private static final String REDIRECT_URL = "/trust/saml2/http-redirect/sso";

    private static final Integer IDP_PORT = 8443;

    private static final String IDP_LOGIN_URL = ("http://localhost:" + (OneloginAuthenticationTest.IDP_PORT)) + "/login";

    private static OneloginAuthenticationFilterConfig config;

    private static WireMockServer idpSamlService;

    @Test
    public void metadataDiscovery() throws Exception {
        confgiureFilter(UserGroupService);
        verify(getRequestedFor(urlEqualTo(OneloginAuthenticationTest.METADATA_URL)).withUrl(OneloginAuthenticationTest.METADATA_URL));
    }

    @Test
    public void notAuthenticatedRedirect() throws Exception {
        confgiureFilter(UserGroupService);
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String redirectURL = response.getHeader("Location");
        Assert.assertThat(redirectURL, CoreMatchers.containsString(OneloginAuthenticationTest.REDIRECT_URL));
        URIBuilder uriBuilder = new URIBuilder(redirectURL);
        List<NameValuePair> urlParameters = uriBuilder.getQueryParams();
        String samlRequest = null;
        for (NameValuePair par : urlParameters) {
            if (par.getName().equals("SAMLRequest")) {
                samlRequest = par.getValue();
                break;
            }
        }
        Assert.assertNotNull(samlRequest);
        StringSamlDecoder decoder = new StringSamlDecoder();
        SAMLObject samlRequestObject = decoder.decode(samlRequest);
        Assert.assertNotNull(samlRequestObject);
    }

    @Test
    public void autorizationWithGroup() throws Exception {
        confgiureFilter(UserGroupService);
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        /* Build POST request form IDP to GeoServer */
        String encodedResponseMessage = buildSAMLRespons("abc@xyz.com");
        request = createRequest(SAMLProcessingFilter.FILTER_URL);
        request.setMethod("POST");
        request.addParameter("SAMLResponse", encodedResponseMessage);
        chain = new MockFilterChain();
        response = new MockHttpServletResponse();
        getProxy().doFilter(request, response, chain);
        /* Check user */
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals("abc@xyz.com", auth.getPrincipal());
    }

    @Test
    public void authenticationWithRoles() throws Exception {
        confgiureFilter(RoleService);
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        /* Build POST request form IDP to GeoServer */
        String encodedResponseMessage = buildSAMLRespons(testUserName);
        request = createRequest(SAMLProcessingFilter.FILTER_URL);
        request.setMethod("POST");
        request.addParameter("SAMLResponse", encodedResponseMessage);
        chain = new MockFilterChain();
        response = new MockHttpServletResponse();
        getProxy().doFilter(request, response, chain);
        /* Check user */
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        boolean hasRootRole = false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (a.getAuthority().equals(rootRole)) {
                hasRootRole = true;
                break;
            }
        }
        Assert.assertTrue(hasRootRole);
        Assert.assertEquals(testUserName, auth.getPrincipal());
    }

    @Test
    public void logoutTest() throws Exception {
        LogoutFilterChain logoutchain = ((LogoutFilterChain) (getSecurityManager().getSecurityConfig().getFilterChain().getRequestChainByName("webLogout")));
        confgiureFilter(RoleService);
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        /* Build POST request form IDP to GeoServer */
        String encodedResponseMessage = buildSAMLRespons(testUserName);
        request = createRequest(SAMLProcessingFilter.FILTER_URL);
        request.setMethod("POST");
        request.addParameter("SAMLResponse", encodedResponseMessage);
        chain = new MockFilterChain();
        response = new MockHttpServletResponse();
        getProxy().doFilter(request, response, chain);
        /* Check user */
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertEquals(testUserName, auth.getPrincipal());
        /* Logout */
        SecurityContextHolder.setContext(ctx);
        request = createRequest(logoutchain.getPatterns().get(0));
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        GeoServerLogoutFilter logoutFilter = ((GeoServerLogoutFilter) (getSecurityManager().loadFilter(FORM_LOGOUT_FILTER)));
        logoutFilter.doFilter(request, response, chain);
        Assert.assertTrue(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        String redirectURL = response.getHeader("Location");
        /* Check if SAML logut URL will be called */
        Assert.assertThat(redirectURL, CoreMatchers.containsString(SAMLLogoutFilter.FILTER_URL));
    }
}

