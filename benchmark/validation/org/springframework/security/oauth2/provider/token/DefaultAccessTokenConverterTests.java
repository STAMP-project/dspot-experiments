/**
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.provider.token;


import AccessTokenConverter.AUD;
import AccessTokenConverter.AUTHORITIES;
import AccessTokenConverter.SCOPE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;


/**
 * Tests for {@link DefaultAccessTokenConverter}.
 *
 * @author Dave Syer
 * @author Vedran Pavic
 */
public class DefaultAccessTokenConverterTests {
    private String ROLE_CLIENT = "ROLE_CLIENT";

    private String ROLE_USER = "ROLE_USER";

    private DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();

    private UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken("foo", "bar", Collections.singleton(new SimpleGrantedAuthority(ROLE_USER)));

    private OAuth2Request request;

    @Test
    public void extractAuthentication() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        token.setScope(authentication.getOAuth2Request().getScope());
        Map<String, ?> map = converter.convertAccessToken(token, authentication);
        Assert.assertTrue(map.containsKey(AUD));
        Assert.assertTrue(map.containsKey(SCOPE));
        Assert.assertTrue(map.containsKey(AUTHORITIES));
        Assert.assertEquals(Collections.singleton(ROLE_USER), map.get(AUTHORITIES));
        OAuth2Authentication extracted = converter.extractAuthentication(map);
        Assert.assertTrue(extracted.getOAuth2Request().getResourceIds().contains("resource"));
        Assert.assertEquals("[ROLE_USER]", extracted.getAuthorities().toString());
    }

    @Test
    public void extractAuthenticationFromClientToken() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
        OAuth2Authentication authentication = new OAuth2Authentication(request, null);
        token.setScope(authentication.getOAuth2Request().getScope());
        Map<String, ?> map = converter.convertAccessToken(token, authentication);
        Assert.assertTrue(map.containsKey(AUD));
        Assert.assertTrue(map.containsKey(SCOPE));
        Assert.assertTrue(map.containsKey(AUTHORITIES));
        Assert.assertEquals(Collections.singleton(ROLE_CLIENT), map.get(AUTHORITIES));
        OAuth2Authentication extracted = converter.extractAuthentication(map);
        Assert.assertTrue(extracted.getOAuth2Request().getResourceIds().contains("resource"));
        Assert.assertEquals("[ROLE_CLIENT]", extracted.getAuthorities().toString());
    }

    @Test
    public void extractAuthenticationFromClientTokenSingleValuedAudience() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
        OAuth2Authentication authentication = new OAuth2Authentication(request, null);
        token.setScope(authentication.getOAuth2Request().getScope());
        Map<String, Object> map = new LinkedHashMap<String, Object>(converter.convertAccessToken(token, authentication));
        @SuppressWarnings("unchecked")
        Object aud = ((Collection<Object>) (map.get(AUD))).iterator().next();
        map.put(AUD, aud);
        Assert.assertTrue(map.containsKey(AUD));
        OAuth2Authentication extracted = converter.extractAuthentication(map);
        Assert.assertEquals((("[" + aud) + "]"), extracted.getOAuth2Request().getResourceIds().toString());
    }

    // gh-745
    @Test
    public void extractAuthenticationSingleScopeString() {
        String scope = "read";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(SCOPE, scope);
        OAuth2Authentication authentication = converter.extractAuthentication(tokenAttrs);
        Assert.assertEquals(Collections.singleton(scope), authentication.getOAuth2Request().getScope());
    }

    // gh-745
    @Test
    public void extractAuthenticationMultiScopeCollection() {
        Set<String> scopes = new HashSet<String>(Arrays.asList("read", "write", "read-write"));
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(SCOPE, scopes);
        OAuth2Authentication authentication = converter.extractAuthentication(tokenAttrs);
        Assert.assertEquals(scopes, authentication.getOAuth2Request().getScope());
    }

    // gh-836 (passes incidentally per gh-745)
    @Test
    public void extractAuthenticationMultiScopeString() {
        String scopes = "read write read-write";
        Assert.assertEquals(new HashSet<String>(Arrays.asList(scopes.split(" "))), converter.extractAuthentication(Collections.singletonMap(SCOPE, scopes)).getOAuth2Request().getScope());
    }

    // gh-745
    @Test
    public void extractAccessTokenSingleScopeString() {
        String scope = "read";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(SCOPE, scope);
        OAuth2AccessToken accessToken = converter.extractAccessToken("token-value", tokenAttrs);
        Assert.assertEquals(Collections.singleton(scope), accessToken.getScope());
    }

    // gh-745
    @Test
    public void extractAccessTokenMultiScopeCollection() {
        Set<String> scopes = new HashSet<String>(Arrays.asList("read", "write", "read-write"));
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(SCOPE, scopes);
        OAuth2AccessToken accessToken = converter.extractAccessToken("token-value", tokenAttrs);
        Assert.assertEquals(scopes, accessToken.getScope());
    }

    // gh-836
    @Test
    public void extractAccessTokenMultiScopeString() {
        String scopes = "read write read-write";
        Assert.assertEquals(new HashSet<String>(Arrays.asList(scopes.split(" "))), converter.extractAccessToken("token-value", Collections.singletonMap(SCOPE, scopes)).getScope());
    }

    // gh-1214
    @Test
    public void convertAccessTokenCustomScopeAttribute() {
        String scopeAttribute = "scp";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setScopeAttribute(scopeAttribute);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        token.setScope(authentication.getOAuth2Request().getScope());
        Map<String, ?> map = converter.convertAccessToken(token, authentication);
        Assert.assertEquals(Collections.singleton("read"), map.get(scopeAttribute));
    }

    // gh-1214
    @Test
    public void convertAccessTokenCustomClientIdAttribute() {
        String clientIdAttribute = "cid";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setClientIdAttribute(clientIdAttribute);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("FOO");
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        Map<String, ?> map = converter.convertAccessToken(token, authentication);
        Assert.assertEquals("id", map.get(clientIdAttribute));
    }

    // gh-1214
    @Test
    public void extractAccessTokenCustomScopeAttribute() {
        String scopeAttribute = "scp";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setScopeAttribute(scopeAttribute);
        String scope = "read";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(scopeAttribute, scope);
        OAuth2AccessToken accessToken = converter.extractAccessToken("token-value", tokenAttrs);
        Assert.assertEquals(Collections.singleton(scope), accessToken.getScope());
    }

    // gh-1214
    @Test
    public void extractAccessTokenCustomClientIdAttribute() {
        String clientIdAttribute = "cid";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setClientIdAttribute(clientIdAttribute);
        String clientId = "id";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(clientIdAttribute, clientId);
        OAuth2AccessToken accessToken = converter.extractAccessToken("token-value", tokenAttrs);
        Assert.assertFalse(accessToken.getAdditionalInformation().containsKey("cid"));
    }

    // gh-1214
    @Test
    public void extractAuthenticationCustomScopeAttribute() {
        String scopeAttribute = "scp";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setScopeAttribute(scopeAttribute);
        String scope = "read";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(scopeAttribute, scope);
        OAuth2Authentication authentication = converter.extractAuthentication(tokenAttrs);
        Assert.assertEquals(Collections.singleton(scope), authentication.getOAuth2Request().getScope());
    }

    // gh-1214
    @Test
    public void extractAuthenticationClientIdAttribute() {
        String clientIdAttribute = "cid";
        DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
        converter.setClientIdAttribute(clientIdAttribute);
        String clientId = "id";
        Map<String, Object> tokenAttrs = new HashMap<String, Object>();
        tokenAttrs.put(clientIdAttribute, clientId);
        OAuth2Authentication authentication = converter.extractAuthentication(tokenAttrs);
        Assert.assertEquals(clientId, authentication.getOAuth2Request().getClientId());
    }
}

