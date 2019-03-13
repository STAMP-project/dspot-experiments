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
package org.springframework.security.oauth2.core.endpoint;


import OAuth2AccessToken.TokenType.BEARER;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


/**
 * Tests for {@link OAuth2AccessTokenResponse}.
 *
 * @author Luander Ribeiro
 * @author Joe Grandja
 */
public class OAuth2AccessTokenResponseTests {
    private static final String TOKEN_VALUE = "access-token";

    private static final String REFRESH_TOKEN_VALUE = "refresh-token";

    private static final long EXPIRES_IN = Instant.now().plusSeconds(5).toEpochMilli();

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenTokenValueIsNullThenThrowIllegalArgumentException() {
        OAuth2AccessTokenResponse.withToken(null).tokenType(BEARER).expiresIn(OAuth2AccessTokenResponseTests.EXPIRES_IN).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWhenTokenTypeIsNullThenThrowIllegalArgumentException() {
        OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(null).expiresIn(OAuth2AccessTokenResponseTests.EXPIRES_IN).build();
    }

    @Test
    public void buildWhenExpiresInIsZeroThenExpiresAtOneSecondAfterIssueAt() {
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(BEARER).expiresIn(0).build();
        assertThat(tokenResponse.getAccessToken().getExpiresAt()).isEqualTo(tokenResponse.getAccessToken().getIssuedAt().plusSeconds(1));
    }

    @Test
    public void buildWhenExpiresInIsNegativeThenExpiresAtOneSecondAfterIssueAt() {
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(BEARER).expiresIn((-1L)).build();
        assertThat(tokenResponse.getAccessToken().getExpiresAt()).isEqualTo(tokenResponse.getAccessToken().getIssuedAt().plusSeconds(1));
    }

    @Test
    public void buildWhenAllAttributesProvidedThenAllAttributesAreSet() {
        Instant expiresAt = Instant.now().plusSeconds(5);
        Set<String> scopes = new LinkedHashSet<>(Arrays.asList("scope1", "scope2"));
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("param1", "value1");
        additionalParameters.put("param2", "value2");
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(BEARER).expiresIn(expiresAt.toEpochMilli()).scopes(scopes).refreshToken(OAuth2AccessTokenResponseTests.REFRESH_TOKEN_VALUE).additionalParameters(additionalParameters).build();
        assertThat(tokenResponse.getAccessToken()).isNotNull();
        assertThat(tokenResponse.getAccessToken().getTokenValue()).isEqualTo(OAuth2AccessTokenResponseTests.TOKEN_VALUE);
        assertThat(tokenResponse.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(tokenResponse.getAccessToken().getIssuedAt()).isNotNull();
        assertThat(tokenResponse.getAccessToken().getExpiresAt()).isAfterOrEqualTo(expiresAt);
        assertThat(tokenResponse.getAccessToken().getScopes()).isEqualTo(scopes);
        assertThat(tokenResponse.getRefreshToken().getTokenValue()).isEqualTo(OAuth2AccessTokenResponseTests.REFRESH_TOKEN_VALUE);
        assertThat(tokenResponse.getAdditionalParameters()).isEqualTo(additionalParameters);
    }

    @Test
    public void buildWhenResponseThenAllAttributesAreSet() {
        Instant expiresAt = Instant.now().plusSeconds(5);
        Set<String> scopes = new LinkedHashSet<>(Arrays.asList("scope1", "scope2"));
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("param1", "value1");
        additionalParameters.put("param2", "value2");
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(BEARER).expiresIn(expiresAt.toEpochMilli()).scopes(scopes).refreshToken(OAuth2AccessTokenResponseTests.REFRESH_TOKEN_VALUE).additionalParameters(additionalParameters).build();
        OAuth2AccessTokenResponse withResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse).build();
        assertThat(withResponse.getAccessToken().getTokenValue()).isEqualTo(tokenResponse.getAccessToken().getTokenValue());
        assertThat(withResponse.getAccessToken().getTokenType()).isEqualTo(BEARER);
        assertThat(withResponse.getAccessToken().getIssuedAt()).isEqualTo(tokenResponse.getAccessToken().getIssuedAt());
        assertThat(withResponse.getAccessToken().getExpiresAt()).isEqualTo(tokenResponse.getAccessToken().getExpiresAt());
        assertThat(withResponse.getAccessToken().getScopes()).isEqualTo(tokenResponse.getAccessToken().getScopes());
        assertThat(withResponse.getRefreshToken().getTokenValue()).isEqualTo(tokenResponse.getRefreshToken().getTokenValue());
        assertThat(withResponse.getAdditionalParameters()).isEqualTo(tokenResponse.getAdditionalParameters());
    }

    @Test
    public void buildWhenResponseAndRefreshNullThenRefreshNull() {
        Instant expiresAt = Instant.now().plusSeconds(5);
        Set<String> scopes = new LinkedHashSet<>(Arrays.asList("scope1", "scope2"));
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("param1", "value1");
        additionalParameters.put("param2", "value2");
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(OAuth2AccessTokenResponseTests.TOKEN_VALUE).tokenType(BEARER).expiresIn(expiresAt.toEpochMilli()).scopes(scopes).additionalParameters(additionalParameters).build();
        OAuth2AccessTokenResponse withResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse).build();
        assertThat(withResponse.getRefreshToken()).isNull();
    }
}

