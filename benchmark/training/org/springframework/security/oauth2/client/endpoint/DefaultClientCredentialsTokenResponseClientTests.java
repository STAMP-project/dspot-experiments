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


import HttpHeaders.ACCEPT;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CONTENT_TYPE;
import HttpMethod.POST;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import OAuth2AccessToken.TokenType.BEARER;
import java.time.Instant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;


/**
 * Tests for {@link DefaultClientCredentialsTokenResponseClient}.
 *
 * @author Joe Grandja
 */
public class DefaultClientCredentialsTokenResponseClientTests {
    private DefaultClientCredentialsTokenResponseClient tokenResponseClient = new DefaultClientCredentialsTokenResponseClient();

    private ClientRegistration clientRegistration;

    private MockWebServer server;

    @Test
    public void setRequestEntityConverterWhenConverterIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.tokenResponseClient.setRequestEntityConverter(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setRestOperationsWhenRestOperationsIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.tokenResponseClient.setRestOperations(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getTokenResponseWhenRequestIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getTokenResponseWhenSuccessResponseThenReturnAccessTokenResponse() throws Exception {
        String accessTokenSuccessResponse = "{\n" + (((((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"read write\",\n") + "   \"custom_parameter_1\": \"custom-value-1\",\n") + "   \"custom_parameter_2\": \"custom-value-2\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        Instant expiresAtBefore = Instant.now().plusSeconds(3600);
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest);
        Instant expiresAtAfter = Instant.now().plusSeconds(3600);
        RecordedRequest recordedRequest = this.server.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(POST.toString());
        assertThat(recordedRequest.getHeader(ACCEPT)).isEqualTo(APPLICATION_JSON_UTF8_VALUE);
        assertThat(recordedRequest.getHeader(CONTENT_TYPE)).isEqualTo(((MediaType.APPLICATION_FORM_URLENCODED_VALUE) + ";charset=UTF-8"));
        String formParameters = recordedRequest.getBody().readUtf8();
        assertThat(formParameters).contains("grant_type=client_credentials");
        assertThat(formParameters).contains("scope=read+write");
        assertThat(accessTokenResponse.getAccessToken().getTokenValue()).isEqualTo("access-token-1234");
        assertThat(accessTokenResponse.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(accessTokenResponse.getAccessToken().getExpiresAt()).isBetween(expiresAtBefore, expiresAtAfter);
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("read", "write");
        assertThat(accessTokenResponse.getRefreshToken()).isNull();
        assertThat(accessTokenResponse.getAdditionalParameters().size()).isEqualTo(2);
        assertThat(accessTokenResponse.getAdditionalParameters()).containsEntry("custom_parameter_1", "custom-value-1");
        assertThat(accessTokenResponse.getAdditionalParameters()).containsEntry("custom_parameter_2", "custom-value-2");
    }

    @Test
    public void getTokenResponseWhenClientAuthenticationBasicThenAuthorizationHeaderIsSent() throws Exception {
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest);
        RecordedRequest recordedRequest = this.server.takeRequest();
        assertThat(recordedRequest.getHeader(AUTHORIZATION)).startsWith("Basic ");
    }

    @Test
    public void getTokenResponseWhenClientAuthenticationPostThenFormParametersAreSent() throws Exception {
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        ClientRegistration clientRegistration = this.from(this.clientRegistration).clientAuthenticationMethod(ClientAuthenticationMethod.POST).build();
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(clientRegistration);
        this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest);
        RecordedRequest recordedRequest = this.server.takeRequest();
        assertThat(recordedRequest.getHeader(AUTHORIZATION)).isNull();
        String formParameters = recordedRequest.getBody().readUtf8();
        assertThat(formParameters).contains("client_id=client-1");
        assertThat(formParameters).contains("client_secret=secret");
    }

    @Test
    public void getTokenResponseWhenSuccessResponseAndNotBearerTokenTypeThenThrowOAuth2AuthorizationException() {
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"not-bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("[invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response").hasMessageContaining("tokenType cannot be null");
    }

    @Test
    public void getTokenResponseWhenSuccessResponseAndMissingTokenTypeParameterThenThrowOAuth2AuthorizationException() {
        String accessTokenSuccessResponse = "{\n" + ("\t\"access_token\": \"access-token-1234\"\n" + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("[invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response").hasMessageContaining("tokenType cannot be null");
    }

    @Test
    public void getTokenResponseWhenSuccessResponseIncludesScopeThenAccessTokenHasResponseScope() {
        String accessTokenSuccessResponse = "{\n" + (((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"read\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest);
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("read");
    }

    @Test
    public void getTokenResponseWhenSuccessResponseDoesNotIncludeScopeThenAccessTokenHasDefaultScope() {
        String accessTokenSuccessResponse = "{\n" + ((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\"\n") + "}\n");
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        OAuth2AccessTokenResponse accessTokenResponse = this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest);
        assertThat(accessTokenResponse.getAccessToken().getScopes()).containsExactly("read", "write");
    }

    @Test
    public void getTokenResponseWhenTokenUriInvalidThenThrowOAuth2AuthorizationException() {
        String invalidTokenUri = "http://invalid-provider.com/oauth2/token";
        ClientRegistration clientRegistration = this.from(this.clientRegistration).tokenUri(invalidTokenUri).build();
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("[invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response");
    }

    @Test
    public void getTokenResponseWhenMalformedResponseThenThrowOAuth2AuthorizationException() {
        String accessTokenSuccessResponse = "{\n" + ((((("\t\"access_token\": \"access-token-1234\",\n" + "   \"token_type\": \"bearer\",\n") + "   \"expires_in\": \"3600\",\n") + "   \"scope\": \"read write\",\n") + "   \"custom_parameter_1\": \"custom-value-1\",\n") + "   \"custom_parameter_2\": \"custom-value-2\"\n");
        // "}\n";		// Make the JSON invalid/malformed
        this.server.enqueue(jsonResponse(accessTokenSuccessResponse));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("[invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response");
    }

    @Test
    public void getTokenResponseWhenErrorResponseThenThrowOAuth2AuthorizationException() {
        String accessTokenErrorResponse = "{\n" + ("   \"error\": \"unauthorized_client\"\n" + "}\n");
        this.server.enqueue(jsonResponse(accessTokenErrorResponse).setResponseCode(400));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("[unauthorized_client]");
    }

    @Test
    public void getTokenResponseWhenServerErrorResponseThenThrowOAuth2AuthorizationException() {
        this.server.enqueue(new MockResponse().setResponseCode(500));
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration);
        assertThatThrownBy(() -> this.tokenResponseClient.getTokenResponse(clientCredentialsGrantRequest)).isInstanceOf(OAuth2AuthorizationException.class).hasMessage("[invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: 500 Server Error");
    }
}

