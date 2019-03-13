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
package org.springframework.security.oauth2.server.resource.authentication;


import BearerTokenErrorCodes.INVALID_TOKEN;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;


/**
 * Tests for {@link JwtAuthenticationProvider}
 *
 * @author Josh Cummings
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationProviderTests {
    @Mock
    Converter<Jwt, JwtAuthenticationToken> jwtAuthenticationConverter;

    @Mock
    JwtDecoder jwtDecoder;

    JwtAuthenticationProvider provider;

    @Test
    public void authenticateWhenJwtDecodesThenAuthenticationHasAttributesContainedInJwt() {
        BearerTokenAuthenticationToken token = this.authentication();
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "value");
        Jwt jwt = this.jwt(claims);
        Mockito.when(this.jwtDecoder.decode("token")).thenReturn(jwt);
        Mockito.when(this.jwtAuthenticationConverter.convert(jwt)).thenReturn(new JwtAuthenticationToken(jwt));
        JwtAuthenticationToken authentication = ((JwtAuthenticationToken) (this.provider.authenticate(token)));
        assertThat(authentication.getTokenAttributes()).isEqualTo(claims);
    }

    @Test
    public void authenticateWhenJwtDecodeFailsThenRespondsWithInvalidToken() {
        BearerTokenAuthenticationToken token = this.authentication();
        Mockito.when(this.jwtDecoder.decode("token")).thenThrow(JwtException.class);
        assertThatCode(() -> this.provider.authenticate(token)).matches(( failed) -> failed instanceof OAuth2AuthenticationException).matches(errorCode(INVALID_TOKEN));
    }

    @Test
    public void authenticateWhenDecoderThrowsIncompatibleErrorMessageThenWrapsWithGenericOne() {
        BearerTokenAuthenticationToken token = this.authentication();
        Mockito.when(this.jwtDecoder.decode(token.getToken())).thenThrow(new JwtException("with \"invalid\" chars"));
        assertThatCode(() -> this.provider.authenticate(token)).isInstanceOf(OAuth2AuthenticationException.class).hasFieldOrPropertyWithValue("error.description", "An error occurred while attempting to decode the Jwt: Invalid token");
    }

    @Test
    public void authenticateWhenConverterReturnsAuthenticationThenProviderPropagatesIt() {
        BearerTokenAuthenticationToken token = this.authentication();
        Object details = Mockito.mock(Object.class);
        token.setDetails(details);
        Jwt jwt = this.jwt(Collections.singletonMap("some", "value"));
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        Mockito.when(this.jwtDecoder.decode(token.getToken())).thenReturn(jwt);
        Mockito.when(this.jwtAuthenticationConverter.convert(jwt)).thenReturn(authentication);
        assertThat(this.provider.authenticate(token)).isEqualTo(authentication).hasFieldOrPropertyWithValue("details", details);
    }

    @Test
    public void supportsWhenBearerTokenAuthenticationTokenThenReturnsTrue() {
        assertThat(this.provider.supports(BearerTokenAuthenticationToken.class)).isTrue();
    }
}

