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


import ClientAuthenticationMethod.POST;
import ClientRegistration.Builder;
import HttpHeaders.AUTHORIZATION;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


/**
 *
 *
 * @author Rob Winch
 */
public class WebClientReactiveClientCredentialsTokenResponseClientTests {
    private MockWebServer server;

    private WebClientReactiveClientCredentialsTokenResponseClient client = new WebClientReactiveClientCredentialsTokenResponseClient();

    private Builder clientRegistration;

    @Test
    public void getTokenResponseWhenHeaderThenSuccess() throws Exception {
        enqueueJson(("{\n" + ((((("  \"access_token\":\"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3\",\n" + "  \"token_type\":\"bearer\",\n") + "  \"expires_in\":3600,\n") + "  \"refresh_token\":\"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk\",\n") + "  \"scope\":\"create\"\n") + "}")));
        OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(this.clientRegistration.build());
        OAuth2AccessTokenResponse response = this.client.getTokenResponse(request).block();
        RecordedRequest actualRequest = this.server.takeRequest();
        String body = actualRequest.getUtf8Body();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(actualRequest.getHeader(AUTHORIZATION)).isEqualTo("Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=");
        assertThat(body).isEqualTo("grant_type=client_credentials&scope=read%3Auser");
    }

    @Test
    public void getTokenResponseWhenPostThenSuccess() throws Exception {
        ClientRegistration registration = this.clientRegistration.clientAuthenticationMethod(POST).build();
        enqueueJson(("{\n" + ((((("  \"access_token\":\"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3\",\n" + "  \"token_type\":\"bearer\",\n") + "  \"expires_in\":3600,\n") + "  \"refresh_token\":\"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk\",\n") + "  \"scope\":\"create\"\n") + "}")));
        OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(registration);
        OAuth2AccessTokenResponse response = this.client.getTokenResponse(request).block();
        String body = this.server.takeRequest().getUtf8Body();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(body).isEqualTo("grant_type=client_credentials&scope=read%3Auser&client_id=client-id&client_secret=client-secret");
    }

    @Test
    public void getTokenResponseWhenNoScopeThenClientRegistrationScopesDefaulted() {
        ClientRegistration registration = this.clientRegistration.build();
        enqueueJson(("{\n" + (((("  \"access_token\":\"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3\",\n" + "  \"token_type\":\"bearer\",\n") + "  \"expires_in\":3600,\n") + "  \"refresh_token\":\"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk\"\n") + "}")));
        OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(registration);
        OAuth2AccessTokenResponse response = this.client.getTokenResponse(request).block();
        assertThat(response.getAccessToken().getScopes()).isEqualTo(registration.getScopes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWebClientNullThenIllegalArgumentException() {
        client.setWebClient(null);
    }

    @Test
    public void setWebClientCustomThenCustomClientIsUsed() {
        WebClient customClient = Mockito.mock(WebClient.class);
        Mockito.when(customClient.post()).thenReturn(WebClient.builder().build().post());
        this.client.setWebClient(customClient);
        ClientRegistration registration = this.clientRegistration.build();
        enqueueJson(("{\n" + (((("  \"access_token\":\"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3\",\n" + "  \"token_type\":\"bearer\",\n") + "  \"expires_in\":3600,\n") + "  \"refresh_token\":\"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk\"\n") + "}")));
        OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(registration);
        OAuth2AccessTokenResponse response = this.client.getTokenResponse(request).block();
        Mockito.verify(customClient, Mockito.atLeastOnce()).post();
    }

    // gh-6089
    @Test(expected = WebClientResponseException.class)
    public void getTokenResponseWhenInvalidResponse() throws WebClientResponseException {
        ClientRegistration registration = this.clientRegistration.build();
        enqueueUnexpectedResponse();
        OAuth2ClientCredentialsGrantRequest request = new OAuth2ClientCredentialsGrantRequest(registration);
        OAuth2AccessTokenResponse response = this.client.getTokenResponse(request).block();
    }
}

