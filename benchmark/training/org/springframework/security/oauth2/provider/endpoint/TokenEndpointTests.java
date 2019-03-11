/**
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.endpoint;


import HttpMethod.GET;
import HttpStatus.OK;
import OAuth2Utils.GRANT_TYPE;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.web.HttpRequestMethodNotSupportedException;


/**
 *
 *
 * @author Dave Syer
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenEndpointTests {
    @Mock
    private TokenGranter tokenGranter;

    @Mock
    private OAuth2RequestFactory authorizationRequestFactory;

    @Mock
    private ClientDetailsService clientDetailsService;

    private String clientId = "client";

    private BaseClientDetails clientDetails = new BaseClientDetails();

    private TokenEndpoint endpoint;

    private Principal clientAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("client", null, Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")));

    @Test
    public void testGetAccessTokenWithNoClientId() throws HttpRequestMethodNotSupportedException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GRANT_TYPE, "authorization_code");
        OAuth2AccessToken expectedToken = new DefaultOAuth2AccessToken("FOO");
        Mockito.when(tokenGranter.grant(ArgumentMatchers.eq("authorization_code"), Mockito.any(TokenRequest.class))).thenReturn(expectedToken);
        @SuppressWarnings("unchecked")
        Map<String, String> anyMap = Mockito.any(Map.class);
        Mockito.when(authorizationRequestFactory.createTokenRequest(anyMap, Mockito.any(ClientDetails.class))).thenReturn(createFromParameters(parameters));
        clientAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(null, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")));
        ResponseEntity<OAuth2AccessToken> response = endpoint.postAccessToken(clientAuthentication, parameters);
        Assert.assertNotNull(response);
        Assert.assertEquals(OK, response.getStatusCode());
        OAuth2AccessToken body = response.getBody();
        Assert.assertEquals(body, expectedToken);
        Assert.assertTrue(("Wrong body: " + body), ((body.getTokenType()) != null));
    }

    @Test
    public void testGetAccessTokenWithScope() throws HttpRequestMethodNotSupportedException {
        Mockito.when(clientDetailsService.loadClientByClientId(clientId)).thenReturn(clientDetails);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("scope", "read");
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", "kJAHDFG");
        OAuth2AccessToken expectedToken = new DefaultOAuth2AccessToken("FOO");
        ArgumentCaptor<TokenRequest> captor = ArgumentCaptor.forClass(TokenRequest.class);
        Mockito.when(tokenGranter.grant(ArgumentMatchers.eq("authorization_code"), captor.capture())).thenReturn(expectedToken);
        @SuppressWarnings("unchecked")
        Map<String, String> anyMap = Mockito.any(Map.class);
        Mockito.when(authorizationRequestFactory.createTokenRequest(anyMap, ArgumentMatchers.eq(clientDetails))).thenReturn(createFromParameters(parameters));
        ResponseEntity<OAuth2AccessToken> response = endpoint.postAccessToken(clientAuthentication, parameters);
        Assert.assertNotNull(response);
        Assert.assertEquals(OK, response.getStatusCode());
        OAuth2AccessToken body = response.getBody();
        Assert.assertEquals(body, expectedToken);
        Assert.assertTrue(("Wrong body: " + body), ((body.getTokenType()) != null));
        Assert.assertTrue("Scope of token request not cleared", captor.getValue().getScope().isEmpty());
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testGetAccessTokenWithUnsupportedRequestParameters() throws HttpRequestMethodNotSupportedException {
        endpoint.getAccessToken(clientAuthentication, new HashMap<String, String>());
    }

    @Test
    public void testGetAccessTokenWithSupportedRequestParametersNotPost() throws HttpRequestMethodNotSupportedException {
        endpoint.setAllowedRequestMethods(new HashSet<org.springframework.http.HttpMethod>(Arrays.asList(GET)));
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("scope", "read");
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", "kJAHDFG");
        OAuth2AccessToken expectedToken = new DefaultOAuth2AccessToken("FOO");
        Mockito.when(tokenGranter.grant(ArgumentMatchers.eq("authorization_code"), Mockito.any(TokenRequest.class))).thenReturn(expectedToken);
        @SuppressWarnings("unchecked")
        Map<String, String> anyMap = Mockito.any(Map.class);
        Mockito.when(authorizationRequestFactory.createTokenRequest(anyMap, Mockito.any(ClientDetails.class))).thenReturn(createFromParameters(parameters));
        ResponseEntity<OAuth2AccessToken> response = endpoint.getAccessToken(clientAuthentication, parameters);
        Assert.assertNotNull(response);
        Assert.assertEquals(OK, response.getStatusCode());
        OAuth2AccessToken body = response.getBody();
        Assert.assertEquals(body, expectedToken);
        Assert.assertTrue(("Wrong body: " + body), ((body.getTokenType()) != null));
    }

    @Test(expected = InvalidGrantException.class)
    public void testImplicitGrant() throws HttpRequestMethodNotSupportedException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(GRANT_TYPE, "implicit");
        parameters.put("client_id", clientId);
        parameters.put("scope", "read");
        @SuppressWarnings("unchecked")
        Map<String, String> anyMap = Mockito.any(Map.class);
        Mockito.when(authorizationRequestFactory.createTokenRequest(anyMap, ArgumentMatchers.eq(clientDetails))).thenReturn(createFromParameters(parameters));
        Mockito.when(clientDetailsService.loadClientByClientId(clientId)).thenReturn(clientDetails);
        endpoint.postAccessToken(clientAuthentication, parameters);
    }

    // gh-1268
    @Test
    public void testGetAccessTokenReturnsHeaderContentTypeJson() throws Exception {
        Mockito.when(clientDetailsService.loadClientByClientId(clientId)).thenReturn(clientDetails);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("scope", "read");
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", "kJAHDFG");
        OAuth2AccessToken expectedToken = new DefaultOAuth2AccessToken("FOO");
        Mockito.when(tokenGranter.grant(ArgumentMatchers.eq("authorization_code"), ArgumentMatchers.any(TokenRequest.class))).thenReturn(expectedToken);
        Mockito.when(authorizationRequestFactory.createTokenRequest(ArgumentMatchers.any(Map.class), ArgumentMatchers.eq(clientDetails))).thenReturn(createFromParameters(parameters));
        ResponseEntity<OAuth2AccessToken> response = endpoint.postAccessToken(clientAuthentication, parameters);
        Assert.assertNotNull(response);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("application/json;charset=UTF-8", response.getHeaders().get("Content-Type").iterator().next());
    }
}

