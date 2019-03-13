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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * Tests for {@link ReactiveJwtAuthenticationConverterAdapter}
 *
 * @author Josh Cummings
 */
public class ReactiveJwtAuthenticationConverterAdapterTests {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(converter);

    @Test
    public void convertWhenTokenHasScopeAttributeThenTranslatedToAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", "message:read message:write"));
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_message:read"), new SimpleGrantedAuthority("SCOPE_message:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScopeAttributeThenTranslatedToNoAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", ""));
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly();
    }

    @Test
    public void convertWhenTokenHasScpAttributeThenTranslatedToAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scp", Arrays.asList("message:read", "message:write")));
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_message:read"), new SimpleGrantedAuthority("SCOPE_message:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScpAttributeThenTranslatedToNoAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scp", Arrays.asList()));
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly();
    }

    @Test
    public void convertWhenTokenHasBothScopeAndScpThenScopeAttributeIsTranslatedToAuthorities() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scp", Arrays.asList("message:read", "message:write"));
        claims.put("scope", "missive:read missive:write");
        Jwt jwt = this.jwt(claims);
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_missive:read"), new SimpleGrantedAuthority("SCOPE_missive:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScopeAndNonEmptyScpThenScopeAttributeIsTranslatedToNoAuthorities() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scp", Arrays.asList("message:read", "message:write"));
        claims.put("scope", "");
        Jwt jwt = this.jwt(claims);
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt).block();
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly();
    }
}

