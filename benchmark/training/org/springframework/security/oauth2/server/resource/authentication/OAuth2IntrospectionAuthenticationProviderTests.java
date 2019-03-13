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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.web.client.RestOperations;


/**
 * Tests for {@link OAuth2IntrospectionAuthenticationProvider}
 *
 * @author Josh Cummings
 * @since 5.2
 */
public class OAuth2IntrospectionAuthenticationProviderTests {
    private static final String INTROSPECTION_URL = "https://server.example.com";

    private static final String CLIENT_ID = "client";

    private static final String CLIENT_SECRET = "secret";

    private static final String ACTIVE_RESPONSE = "{\n" + (((((((((("      \"active\": true,\n" + "      \"client_id\": \"l238j323ds-23ij4\",\n") + "      \"username\": \"jdoe\",\n") + "      \"scope\": \"read write dolphin\",\n") + "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n") + "      \"aud\": \"https://protected.example.net/resource\",\n") + "      \"iss\": \"https://server.example.com/\",\n") + "      \"exp\": 1419356238,\n") + "      \"iat\": 1419350238,\n") + "      \"extension_field\": \"twenty-seven\"\n") + "     }");

    private static final String INACTIVE_RESPONSE = "{\n" + ("      \"active\": false\n" + "     }");

    private static final String INVALID_RESPONSE = "{\n" + ((((((((("      \"client_id\": \"l238j323ds-23ij4\",\n" + "      \"username\": \"jdoe\",\n") + "      \"scope\": \"read write dolphin\",\n") + "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n") + "      \"aud\": \"https://protected.example.net/resource\",\n") + "      \"iss\": \"https://server.example.com/\",\n") + "      \"exp\": 1419356238,\n") + "      \"iat\": 1419350238,\n") + "      \"extension_field\": \"twenty-seven\"\n") + "     }");

    private static final String MALFORMED_ISSUER_RESPONSE = "{\n" + (("     \"active\" : \"true\",\n" + "     \"iss\" : \"badissuer\"\n") + "    }");

    private static final ResponseEntity<String> ACTIVE = OAuth2IntrospectionAuthenticationProviderTests.response(OAuth2IntrospectionAuthenticationProviderTests.ACTIVE_RESPONSE);

    private static final ResponseEntity<String> INACTIVE = OAuth2IntrospectionAuthenticationProviderTests.response(OAuth2IntrospectionAuthenticationProviderTests.INACTIVE_RESPONSE);

    private static final ResponseEntity<String> INVALID = OAuth2IntrospectionAuthenticationProviderTests.response(OAuth2IntrospectionAuthenticationProviderTests.INVALID_RESPONSE);

    private static final ResponseEntity<String> MALFORMED_ISSUER = OAuth2IntrospectionAuthenticationProviderTests.response(OAuth2IntrospectionAuthenticationProviderTests.MALFORMED_ISSUER_RESPONSE);

    @Test
    public void authenticateWhenActiveTokenThenOk() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.setDispatcher(OAuth2IntrospectionAuthenticationProviderTests.requiresAuth(OAuth2IntrospectionAuthenticationProviderTests.CLIENT_ID, OAuth2IntrospectionAuthenticationProviderTests.CLIENT_SECRET, OAuth2IntrospectionAuthenticationProviderTests.ACTIVE_RESPONSE));
            String introspectUri = server.url("/introspect").toString();
            OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(introspectUri, OAuth2IntrospectionAuthenticationProviderTests.CLIENT_ID, OAuth2IntrospectionAuthenticationProviderTests.CLIENT_SECRET);
            Authentication result = provider.authenticate(new BearerTokenAuthenticationToken("token"));
            assertThat(result.getPrincipal()).isInstanceOf(Map.class);
            Map<String, Object> attributes = ((Map<String, Object>) (result.getPrincipal()));
            assertThat(attributes).isNotNull().containsEntry(OAuth2IntrospectionClaimNames.ACTIVE, true).containsEntry(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("https://protected.example.net/resource")).containsEntry(OAuth2IntrospectionClaimNames.CLIENT_ID, "l238j323ds-23ij4").containsEntry(OAuth2IntrospectionClaimNames.EXPIRES_AT, Instant.ofEpochSecond(1419356238)).containsEntry(OAuth2IntrospectionClaimNames.ISSUER, new URL("https://server.example.com/")).containsEntry(OAuth2IntrospectionClaimNames.SCOPE, Arrays.asList("read", "write", "dolphin")).containsEntry(OAuth2IntrospectionClaimNames.SUBJECT, "Z5O3upPC88QrAjx00dis").containsEntry(OAuth2IntrospectionClaimNames.USERNAME, "jdoe").containsEntry("extension_field", "twenty-seven");
            assertThat(result.getAuthorities()).extracting("authority").containsExactly("SCOPE_read", "SCOPE_write", "SCOPE_dolphin");
        }
    }

    @Test
    public void authenticateWhenBadClientCredentialsThenAuthenticationException() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.setDispatcher(OAuth2IntrospectionAuthenticationProviderTests.requiresAuth(OAuth2IntrospectionAuthenticationProviderTests.CLIENT_ID, OAuth2IntrospectionAuthenticationProviderTests.CLIENT_SECRET, OAuth2IntrospectionAuthenticationProviderTests.ACTIVE_RESPONSE));
            String introspectUri = server.url("/introspect").toString();
            OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(introspectUri, OAuth2IntrospectionAuthenticationProviderTests.CLIENT_ID, "wrong");
            assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class);
        }
    }

    @Test
    public void authenticateWhenInactiveTokenThenInvalidToken() {
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(OAuth2IntrospectionAuthenticationProviderTests.INACTIVE);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenActiveTokenThenParsesValuesInResponse() {
        Map<String, Object> introspectedValues = new HashMap<>();
        introspectedValues.put(OAuth2IntrospectionClaimNames.ACTIVE, true);
        introspectedValues.put(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("aud"));
        introspectedValues.put(OAuth2IntrospectionClaimNames.NOT_BEFORE, 29348723984L);
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(OAuth2IntrospectionAuthenticationProviderTests.response(new JSONObject(introspectedValues).toJSONString()));
        Authentication result = provider.authenticate(new BearerTokenAuthenticationToken("token"));
        assertThat(result.getPrincipal()).isInstanceOf(Map.class);
        Map<String, Object> attributes = ((Map<String, Object>) (result.getPrincipal()));
        assertThat(attributes).isNotNull().containsEntry(OAuth2IntrospectionClaimNames.ACTIVE, true).containsEntry(OAuth2IntrospectionClaimNames.AUDIENCE, Arrays.asList("aud")).containsEntry(OAuth2IntrospectionClaimNames.NOT_BEFORE, Instant.ofEpochSecond(29348723984L)).doesNotContainKey(OAuth2IntrospectionClaimNames.CLIENT_ID).doesNotContainKey(OAuth2IntrospectionClaimNames.SCOPE);
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    public void authenticateWhenIntrospectionEndpointThrowsExceptionThenInvalidToken() {
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenThrow(new IllegalStateException("server was unresponsive"));
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionEndpointReturnsMalformedResponseThenInvalidToken() {
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(OAuth2IntrospectionAuthenticationProviderTests.response("malformed"));
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionTokenReturnsInvalidResponseThenInvalidToken() {
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(OAuth2IntrospectionAuthenticationProviderTests.INVALID);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void authenticateWhenIntrospectionTokenReturnsMalformedIssuerResponseThenInvalidToken() {
        RestOperations restOperations = Mockito.mock(RestOperations.class);
        OAuth2IntrospectionAuthenticationProvider provider = new OAuth2IntrospectionAuthenticationProvider(OAuth2IntrospectionAuthenticationProviderTests.INTROSPECTION_URL, restOperations);
        Mockito.when(restOperations.exchange(ArgumentMatchers.any(RequestEntity.class), ArgumentMatchers.eq(String.class))).thenReturn(OAuth2IntrospectionAuthenticationProviderTests.MALFORMED_ISSUER);
        assertThatCode(() -> provider.authenticate(new BearerTokenAuthenticationToken("token"))).isInstanceOf(OAuth2AuthenticationException.class).extracting("error.errorCode").containsExactly("invalid_token");
    }

    @Test
    public void constructorWhenIntrospectionUriIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionAuthenticationProvider(null, CLIENT_ID, CLIENT_SECRET)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenClientIdIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionAuthenticationProvider(INTROSPECTION_URL, null, CLIENT_SECRET)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenClientSecretIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionAuthenticationProvider(INTROSPECTION_URL, CLIENT_ID, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorWhenRestOperationsIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new OAuth2IntrospectionAuthenticationProvider(INTROSPECTION_URL, null)).isInstanceOf(IllegalArgumentException.class);
    }
}

