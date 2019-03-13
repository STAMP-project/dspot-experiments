/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.oauth2.client.endpoint;


import ClientRegistration.Builder;
import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON_VALUE;
import OAuth2AccessToken.TokenType.BEARER;
import java.time.Instant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;


/**
 * Tests for {@link NimbusAuthorizationCodeTokenResponseClient}.
 *
 * @author Joe Grandja
 */
public class NimbusAuthorizationCodeTokenResponseClientTests {
    private Builder clientRegistrationBuilder;

    private OAuth2AuthorizationRequest authorizationRequest;

    private OAuth2AuthorizationResponse authorizationResponse;

    private OAuth2AuthorizationExchange authorizationExchange;

    private NimbusAuthorizationCodeTokenResponseClient tokenResponseClient = new NimbusAuthorizationCodeTokenResponseClient();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getTokenResponseWhenSuccessResponseThenReturnAccessTokenResponse() throws Exception {
        MockWebServer server = new MockWebServer();
        String accessTokenSuccessResponse = "{\n" + ((((((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"openid profile\",\n") + "\t\"refresh_token\": \"refresh-token-1234\",\n") + "   \"custom_parameter_1\": \"custom-value-1\",\n") + "   \"custom_parameter_2\": \"custom-value-2\"\n") + "}\n");
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(accessTokenSuccessResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        Instant expiresAtBefore = Instant.now().plusSeconds(3600);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
        Instant expiresAtAfter = Instant.now().plusSeconds(3600);
        server.shutdown();
        assertThat(accessTokenResponse.getAccessToken().getTokenValue()).isEqualTo("access-token-1234");
        assertThat(accessTokenResponse.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(accessTokenResponse.getAccessToken().getExpiresAt()).isBetween(expiresAtBefore, expiresAtAfter);
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("openid", "profile");
        assertThat(accessTokenResponse.getRefreshToken().getTokenValue()).isEqualTo("refresh-token-1234");
        assertThat(accessTokenResponse.getAdditionalParameters().size()).isEqualTo(2);
        assertThat(accessTokenResponse.getAdditionalParameters()).containsEntry("custom_parameter_1", "custom-value-1");
        assertThat(accessTokenResponse.getAdditionalParameters()).containsEntry("custom_parameter_2", "custom-value-2");
    }

    @Test
    public void getTokenResponseWhenRedirectUriMalformedThenThrowIllegalArgumentException() throws Exception {
        this.exception.expect(IllegalArgumentException.class);
        String redirectUri = "http:\\example.com";
        OAuth2AuthorizationRequest authorizationRequest = request().redirectUri(redirectUri).build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, this.authorizationResponse);
        this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), authorizationExchange));
    }

    @Test
    public void getTokenResponseWhenTokenUriMalformedThenThrowIllegalArgumentException() throws Exception {
        this.exception.expect(IllegalArgumentException.class);
        String tokenUri = "http:\\provider.com\\oauth2\\token";
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
    }

    @Test
    public void getTokenResponseWhenSuccessResponseInvalidThenThrowOAuth2AuthorizationException() throws Exception {
        this.exception.expect(OAuth2AuthorizationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_token_response"));
        MockWebServer server = new MockWebServer();
        String accessTokenSuccessResponse = "{\n" + ((((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"openid profile\",\n") + "   \"custom_parameter_1\": \"custom-value-1\",\n") + "   \"custom_parameter_2\": \"custom-value-2\"\n");
        // "}\n";		// Make the JSON invalid/malformed
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(accessTokenSuccessResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        try {
            this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void getTokenResponseWhenTokenUriInvalidThenThrowOAuth2AuthorizationException() throws Exception {
        this.exception.expect(OAuth2AuthorizationException.class);
        String tokenUri = "http://invalid-provider.com/oauth2/token";
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
    }

    @Test
    public void getTokenResponseWhenErrorResponseThenThrowOAuth2AuthorizationException() throws Exception {
        this.exception.expect(OAuth2AuthorizationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("unauthorized_client"));
        MockWebServer server = new MockWebServer();
        String accessTokenErrorResponse = "{\n" + ("   \"error\": \"unauthorized_client\"\n" + "}\n");
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setResponseCode(500).setBody(accessTokenErrorResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        try {
            this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
        } finally {
            server.shutdown();
        }
    }

    // gh-5594
    @Test
    public void getTokenResponseWhenServerErrorResponseThenThrowOAuth2AuthorizationException() throws Exception {
        this.exception.expect(OAuth2AuthorizationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("server_error"));
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(500));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        try {
            this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void getTokenResponseWhenSuccessResponseAndNotBearerTokenTypeThenThrowOAuth2AuthorizationException() throws Exception {
        this.exception.expect(OAuth2AuthorizationException.class);
        this.exception.expectMessage(CoreMatchers.containsString("invalid_token_response"));
        MockWebServer server = new MockWebServer();
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"not-bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(accessTokenSuccessResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        try {
            this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), this.authorizationExchange));
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void getTokenResponseWhenSuccessResponseIncludesScopeThenReturnAccessTokenResponseUsingResponseScope() throws Exception {
        MockWebServer server = new MockWebServer();
        String accessTokenSuccessResponse = "{\n" + (((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"openid profile\"\n") + "}\n");
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(accessTokenSuccessResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        OAuth2AuthorizationRequest authorizationRequest = request().scope("openid", "profile", "email", "address").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, this.authorizationResponse);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), authorizationExchange));
        server.shutdown();
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("openid", "profile");
    }

    @Test
    public void getTokenResponseWhenSuccessResponseDoesNotIncludeScopeThenReturnAccessTokenResponseUsingRequestedScope() throws Exception {
        MockWebServer server = new MockWebServer();
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        server.enqueue(new MockResponse().setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).setBody(accessTokenSuccessResponse));
        server.start();
        String tokenUri = server.url("/oauth2/token").toString();
        this.clientRegistrationBuilder.tokenUri(tokenUri);
        OAuth2AuthorizationRequest authorizationRequest = request().scope("openid", "profile", "email", "address").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, this.authorizationResponse);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(this.clientRegistrationBuilder.build(), authorizationExchange));
        server.shutdown();
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("openid", "profile", "email", "address");
    }
}

