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
package org.springframework.security.oauth2.client;


import OAuth2AccessToken.BEARER_TYPE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;


/**
 *
 *
 * @author Dave Syer
 */
public class DefaultOAuth2RequestAuthenticatorTests {
    private DefaultOAuth2RequestAuthenticator authenticator = new DefaultOAuth2RequestAuthenticator();

    private MockClientHttpRequest request = new MockClientHttpRequest();

    private DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();

    @Test(expected = AccessTokenRequiredException.class)
    public void missingAccessToken() {
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, new DefaultOAuth2ClientContext(), request);
    }

    @Test
    public void addsAccessToken() {
        context.setAccessToken(new DefaultOAuth2AccessToken("FOO"));
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, context, request);
        String header = request.getHeaders().getFirst("Authorization");
        Assert.assertEquals("Bearer FOO", header);
    }

    // gh-1346
    @Test
    public void authenticateWhenTokenTypeBearerUppercaseThenUseBearer() {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        accessToken.setTokenType(BEARER_TYPE.toUpperCase());
        context.setAccessToken(accessToken);
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, context, request);
        String header = request.getHeaders().getFirst("Authorization");
        Assert.assertEquals("Bearer FOO", header);
    }

    // gh-1346
    @Test
    public void authenticateWhenTokenTypeBearerLowercaseThenUseBearer() {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        accessToken.setTokenType(BEARER_TYPE.toLowerCase());
        context.setAccessToken(accessToken);
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, context, request);
        String header = request.getHeaders().getFirst("Authorization");
        Assert.assertEquals("Bearer FOO", header);
    }

    // gh-1346
    @Test
    public void authenticateWhenTokenTypeBearerMixcaseThenUseBearer() {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        accessToken.setTokenType("BeaRer");
        context.setAccessToken(accessToken);
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, context, request);
        String header = request.getHeaders().getFirst("Authorization");
        Assert.assertEquals("Bearer FOO", header);
    }

    // gh-1346
    @Test
    public void authenticateWhenTokenTypeMACThenUseMAC() {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("FOO");
        accessToken.setTokenType("MAC");
        context.setAccessToken(accessToken);
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        authenticator.authenticate(resource, context, request);
        String header = request.getHeaders().getFirst("Authorization");
        Assert.assertEquals("MAC FOO", header);
    }
}

