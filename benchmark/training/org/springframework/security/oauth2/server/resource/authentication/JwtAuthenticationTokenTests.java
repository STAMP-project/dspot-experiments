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


import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * Tests for {@link JwtAuthenticationToken}
 *
 * @author Josh Cummings
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationTokenTests {
    @Test
    public void getNameWhenJwtHasSubjectThenReturnsSubject() {
        Jwt jwt = this.jwt(Maps.newHashMap("sub", "Carl"));
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        assertThat(token.getName()).isEqualTo("Carl");
    }

    @Test
    public void getNameWhenJwtHasNoSubjectThenReturnsNull() {
        Jwt jwt = this.jwt(Maps.newHashMap("claim", "value"));
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        assertThat(token.getName()).isNull();
    }

    @Test
    public void constructorWhenJwtIsNullThenThrowsException() {
        assertThatCode(() -> new JwtAuthenticationToken(null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("token cannot be null");
    }

    @Test
    public void constructorWhenUsingCorrectParametersThenConstructedCorrectly() {
        Collection authorities = Arrays.asList(new SimpleGrantedAuthority("test"));
        Map claims = Maps.newHashMap("claim", "value");
        Jwt jwt = this.jwt(claims);
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        assertThat(token.getAuthorities()).isEqualTo(authorities);
        assertThat(token.getPrincipal()).isEqualTo(jwt);
        assertThat(token.getCredentials()).isEqualTo(jwt);
        assertThat(token.getToken()).isEqualTo(jwt);
        assertThat(token.getTokenAttributes()).isEqualTo(claims);
        assertThat(token.isAuthenticated()).isTrue();
    }

    @Test
    public void constructorWhenUsingOnlyJwtThenConstructedCorrectly() {
        Map claims = Maps.newHashMap("claim", "value");
        Jwt jwt = this.jwt(claims);
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        assertThat(token.getAuthorities()).isEmpty();
        assertThat(token.getPrincipal()).isEqualTo(jwt);
        assertThat(token.getCredentials()).isEqualTo(jwt);
        assertThat(token.getToken()).isEqualTo(jwt);
        assertThat(token.getTokenAttributes()).isEqualTo(claims);
        assertThat(token.isAuthenticated()).isFalse();
    }
}

