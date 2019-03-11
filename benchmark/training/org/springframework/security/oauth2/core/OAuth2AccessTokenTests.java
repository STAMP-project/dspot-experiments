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
package org.springframework.security.oauth2.core;


import OAuth2AccessToken.TokenType;
import OAuth2AccessToken.TokenType.BEARER;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 * Tests for {@link OAuth2AccessToken}.
 *
 * @author Joe Grandja
 */
public class OAuth2AccessTokenTests {
    private static final TokenType TOKEN_TYPE = TokenType.BEARER;

    private static final String TOKEN_VALUE = "access-token";

    private static final Instant ISSUED_AT = Instant.now();

    private static final Instant EXPIRES_AT = Instant.from(OAuth2AccessTokenTests.ISSUED_AT).plusSeconds(60);

    private static final Set<String> SCOPES = new LinkedHashSet<>(Arrays.asList("scope1", "scope2"));

    @Test
    public void tokenTypeGetValueWhenTokenTypeBearerThenReturnBearer() {
        assertThat(BEARER.getValue()).isEqualTo("Bearer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenTokenTypeIsNullThenThrowIllegalArgumentException() {
        new OAuth2AccessToken(null, OAuth2AccessTokenTests.TOKEN_VALUE, OAuth2AccessTokenTests.ISSUED_AT, OAuth2AccessTokenTests.EXPIRES_AT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenTokenValueIsNullThenThrowIllegalArgumentException() {
        new OAuth2AccessToken(OAuth2AccessTokenTests.TOKEN_TYPE, null, OAuth2AccessTokenTests.ISSUED_AT, OAuth2AccessTokenTests.EXPIRES_AT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenIssuedAtAfterExpiresAtThenThrowIllegalArgumentException() {
        new OAuth2AccessToken(OAuth2AccessTokenTests.TOKEN_TYPE, OAuth2AccessTokenTests.TOKEN_VALUE, Instant.from(OAuth2AccessTokenTests.EXPIRES_AT).plusSeconds(1), OAuth2AccessTokenTests.EXPIRES_AT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenExpiresAtBeforeIssuedAtThenThrowIllegalArgumentException() {
        new OAuth2AccessToken(OAuth2AccessTokenTests.TOKEN_TYPE, OAuth2AccessTokenTests.TOKEN_VALUE, OAuth2AccessTokenTests.ISSUED_AT, Instant.from(OAuth2AccessTokenTests.ISSUED_AT).minusSeconds(1));
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessTokenTests.TOKEN_TYPE, OAuth2AccessTokenTests.TOKEN_VALUE, OAuth2AccessTokenTests.ISSUED_AT, OAuth2AccessTokenTests.EXPIRES_AT, OAuth2AccessTokenTests.SCOPES);
        assertThat(accessToken.getTokenType()).isEqualTo(OAuth2AccessTokenTests.TOKEN_TYPE);
        assertThat(accessToken.getTokenValue()).isEqualTo(OAuth2AccessTokenTests.TOKEN_VALUE);
        assertThat(accessToken.getIssuedAt()).isEqualTo(OAuth2AccessTokenTests.ISSUED_AT);
        assertThat(accessToken.getExpiresAt()).isEqualTo(OAuth2AccessTokenTests.EXPIRES_AT);
        assertThat(accessToken.getScopes()).isEqualTo(OAuth2AccessTokenTests.SCOPES);
    }

    // gh-5492
    @Test
    public void constructorWhenCreatedThenIsSerializableAndDeserializable() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessTokenTests.TOKEN_TYPE, OAuth2AccessTokenTests.TOKEN_VALUE, OAuth2AccessTokenTests.ISSUED_AT, OAuth2AccessTokenTests.EXPIRES_AT, OAuth2AccessTokenTests.SCOPES);
        byte[] serialized = SerializationUtils.serialize(accessToken);
        accessToken = ((OAuth2AccessToken) (SerializationUtils.deserialize(serialized)));
        assertThat(serialized).isNotNull();
        assertThat(accessToken.getTokenType()).isEqualTo(OAuth2AccessTokenTests.TOKEN_TYPE);
        assertThat(accessToken.getTokenValue()).isEqualTo(OAuth2AccessTokenTests.TOKEN_VALUE);
        assertThat(accessToken.getIssuedAt()).isEqualTo(OAuth2AccessTokenTests.ISSUED_AT);
        assertThat(accessToken.getExpiresAt()).isEqualTo(OAuth2AccessTokenTests.EXPIRES_AT);
        assertThat(accessToken.getScopes()).isEqualTo(OAuth2AccessTokenTests.SCOPES);
    }
}

