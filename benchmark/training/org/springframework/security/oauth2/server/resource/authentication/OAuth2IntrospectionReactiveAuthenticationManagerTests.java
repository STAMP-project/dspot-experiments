/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.oauth2.server.resource.authentication;


import OAuth2IntrospectionClaimNames.ACTIVE;
import OAuth2IntrospectionClaimNames.CLIENT_ID;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Tests for {@link OAuth2IntrospectionReactiveAuthenticationManager}
 */
public class OAuth2IntrospectionReactiveAuthenticationManagerTests {
    private static final String INTROSPECTION_URL = "https://server.example.com";

    private static final String CLIENT_ID = "client";

    private static final String CLIENT_SECRET = "secret";

    private static final String ACTIVE_RESPONSE = "{\n" + (((((((((("      \"active\": true,\n" + "      \"client_id\": \"l238j323ds-23ij4\",\n") + "      \"username\": \"jdoe\",\n") + "      \"scope\": \"read write dolphin\",\n") + "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n") + "      \"aud\": \"https://protected.example.net/resource\",\n") + "      \"iss\": \"https://server.example.com/\",\n") + "      \"exp\": 1419356238,\n") + "      \"iat\": 1419350238,\n") + "      \"extension_field\": \"twenty-seven\"\n") + "     }");

    private static final String INACTIVE_RESPONSE = "{\n" + ("      \"active\": false\n" + "     }");

    private static final String INVALID_RESPONSE = "{\n" + ((((((((("      \"client_id\": \"l238j323ds-23ij4\",\n" + "      \"username\": \"jdoe\",\n") + "      \"scope\": \"read write dolphin\",\n") + "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n") + "      \"aud\": \"https://protected.example.net/resource\",\n") + "      \"iss\": \"https://server.example.com/\",\n") + "      \"exp\": 1419356238,\n") + "      \"iat\": 1419350238,\n") + "      \"extension_field\": \"twenty-seven\"\n") + "     }");

    private static final String MALFORMED_ISSUER_RESPONSE = "{\n" + (("     \"active\" : \"true\",\n" + "     \"iss\" : \"badissuer\"\n") + "    }");

    @Test
    public void authenticateWhenActiveTokenThenOk() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.setDispatcher(OAuth2IntrospectionReactiveAuthenticationManagerTests.requiresAuth(OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_ID, OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_SECRET, OAuth2IntrospectionReactiveAuthenticationManagerTests.ACTIVE_RESPONSE));
            String introspectUri = server.url("/introspect").toString();
            OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(introspectUri, OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_ID, OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_SECRET);
            Authentication result = provider.authenticate(new BearerTokenAuthenticationToken("token")).block();
            assertThat(result.getPrincipal()).isInstanceOf(Map.class);
            Map<String, Object> attributes = ((Map<String, Object>) (result.getPrincipal()));
            assertThat(attributes).isNotNull().containsEntry(ACTIVE, true).containsEntry(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("https://protected.example.net/resource")).containsEntry(OAuth2IntrospectionClaimNames.CLIENT_ID, "l238j323ds-23ij4").containsEntry(OAuth2IntrospectionClaimNames.EXPIRES_AT, Instant.ofEpochSecond(1419356238)).containsEntry(OAuth2IntrospectionClaimNames.ISSUER, new URL("https://server.example.com/")).containsEntry(OAuth2IntrospectionClaimNames.SCOPE, Arrays.asList("read", "write", "dolphin")).containsEntry(OAuth2IntrospectionClaimNames.SUBJECT, "Z5O3upPC88QrAjx00dis").containsEntry(OAuth2IntrospectionClaimNames.USERNAME, "jdoe").containsEntry("extension_field", "twenty-seven");
            assertThat(result.getAuthorities()).extracting("authority").containsExactly("SCOPE_read", "SCOPE_write", "SCOPE_dolphin");
        }
    }

    @Test
    public void authenticateWhenBadClientCredentialsThenAuthenticationException() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.setDispatcher(OAuth2IntrospectionReactiveAuthenticationManagerTests.requiresAuth(OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_ID, OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_SECRET, OAuth2IntrospectionReactiveAuthenticationManagerTests.ACTIVE_RESPONSE));
            String introspectUri = server.url("/introspect").toString();
            OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(introspectUri, OAuth2IntrospectionReactiveAuthenticationManagerTests.CLIENT_ID, "wrong");
            assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class);
        }
    }

    @Test
    public void authenticateWhenInactiveTokenThenInvalidToken() {
        WebClient webClient = mockResponse(OAuth2IntrospectionReactiveAuthenticationManagerTests.INACTIVE_RESPONSE);
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenActiveTokenThenParsesValuesInResponse() {
        Map<String, Object> introspectedValues = new HashMap<>();
        introspectedValues.put(ACTIVE, true);
        introspectedValues.put(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("aud"));
        introspectedValues.put(OAuth2IntrospectionClaimNames.NOT_BEFORE, 29348723984L);
        WebClient webClient = mockResponse(new JSONObject(introspectedValues).toJSONString());
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        Authentication result = provider.authenticate(new BearerTokenAuthenticationToken("token")).block();
        assertThat(result.getPrincipal()).isInstanceOf(Map.class);
        Map<String, Object> attributes = ((Map<String, Object>) (result.getPrincipal()));
        assertThat(attributes).isNotNull().containsEntry(ACTIVE, true).containsEntry(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("aud")).containsEntry(OAuth2IntrospectionClaimNames.NOT_BEFORE, Instant.ofEpochSecond(29348723984L)).doesNotContainKey(OAuth2IntrospectionClaimNames.CLIENT_ID).doesNotContainKey(OAuth2IntrospectionClaimNames.SCOPE);
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    public void authenticateWhenIntrospectionEndpointThrowsExceptionThenInvalidToken() {
        WebClient webClient = mockResponse(new IllegalStateException("server was unresponsive"));
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionEndpointReturnsMalformedResponseThenInvalidToken() {
        WebClient webClient = mockResponse("malformed");
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionTokenReturnsInvalidResponseThenInvalidToken() {
        WebClient webClient = mockResponse(OAuth2IntrospectionReactiveAuthenticationManagerTests.INVALID_RESPONSE);
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionTokenReturnsMalformedIssuerResponseThenInvalidToken() {
        WebClient webClient = mockResponse(OAuth2IntrospectionReactiveAuthenticationManagerTests.MALFORMED_ISSUER_RESPONSE);
        OAuth2IntrospectionReactiveAuthenticationManager provider = new OAuth2IntrospectionReactiveAuthenticationManager(OAuth2IntrospectionReactiveAuthenticationManagerTests.INTROSPECTION_URL, webClient);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token")).block()).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void constructorWhenIntrospectionUriIsEmptyThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionReactiveAuthenticationManager("", CLIENT_ID, CLIENT_SECRET)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenClientIdIsEmptyThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionReactiveAuthenticationManager(INTROSPECTION_URL, "", CLIENT_SECRET)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenClientSecretIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionReactiveAuthenticationManager(INTROSPECTION_URL, CLIENT_ID, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenRestOperationsIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionReactiveAuthenticationManager(INTROSPECTION_URL, null)).isInstanceOf(IllegalArgumentException.class);
    }
}

