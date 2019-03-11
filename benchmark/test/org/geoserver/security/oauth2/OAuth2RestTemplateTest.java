/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.oauth2;


import OAuth2AccessToken.BEARER_TYPE;
import URLType.SERVICE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RequestAuthenticator;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions S.A.S.
 */
public class OAuth2RestTemplateTest extends AbstractOAuth2RestTemplateTest {
    @Test(expected = AccessTokenRequiredException.class)
    public void testAccessDeneiedException() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("access_token");
        authenticator.authenticate(resource, restTemplate.getOAuth2ClientContext(), request);
    }

    @Test
    public void testNonBearerToken() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("access_token");
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        authenticator.authenticate(resource, restTemplate.getOAuth2ClientContext(), request);
        String auth = request.getHeaders().getFirst("Authorization");
        Assert.assertTrue(auth.startsWith("access_token "));
    }

    @Test
    public void testCustomAuthenticator() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("access_token");
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        OAuth2RequestAuthenticator customAuthenticator = new OAuth2RequestAuthenticator() {
            @Override
            public void authenticate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext clientContext, ClientHttpRequest req) {
                req.getHeaders().set("X-Authorization", (((clientContext.getAccessToken().getTokenType()) + " ") + "Nah-nah-na-nah-nah"));
            }
        };
        customAuthenticator.authenticate(resource, restTemplate.getOAuth2ClientContext(), request);
        String auth = request.getHeaders().getFirst("X-Authorization");
        Assert.assertEquals("access_token Nah-nah-na-nah-nah", auth);
    }

    @Test
    public void testBearerAccessTokenURLMangler() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("12345");
        token.setTokenType("access_token");
        token.setTokenType(BEARER_TYPE);
        restTemplate.getOAuth2ClientContext().setAccessToken(token);
        authenticator.authenticate(resource, restTemplate.getOAuth2ClientContext(), request);
        String auth = request.getHeaders().getFirst("Authorization");
        Assert.assertTrue(auth.startsWith(BEARER_TYPE));
        OAuth2AccessTokenURLMangler urlMangler = new OAuth2AccessTokenURLMangler(getSecurityManager(), configuration, restTemplate);
        urlMangler.geoServerOauth2RestTemplate = restTemplate;
        Assert.assertNotNull(urlMangler);
        Authentication user = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("admin", "geoserver", Arrays.asList(new GrantedAuthority[]{ new SimpleGrantedAuthority("ROLE_ADMINISTRATOR") }));
        SecurityContextHolder.getContext().setAuthentication(user);
        StringBuilder baseURL = new StringBuilder("http://test.geoserver-org/wms");
        StringBuilder path = new StringBuilder();
        Map<String, String> kvp = new HashMap<String, String>();
        kvp.put("request", "GetCapabilities");
        urlMangler.mangleURL(baseURL, path, kvp, SERVICE);
        Assert.assertTrue(kvp.containsKey("access_token"));
        Assert.assertTrue("12345".equals(kvp.get("access_token")));
    }
}

