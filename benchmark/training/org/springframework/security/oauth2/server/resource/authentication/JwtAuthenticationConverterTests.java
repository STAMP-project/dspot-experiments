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
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * Tests for {@link JwtAuthenticationConverter}
 *
 * @author Josh Cummings
 */
public class JwtAuthenticationConverterTests {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    @Test
    public void convertWhenDefaultGrantedAuthoritiesConverterSet() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", "message:read message:write"));
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt);
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_message:read"), new SimpleGrantedAuthority("SCOPE_message:write"));
    }

    @Test
    public void whenSettingNullGrantedAuthoritiesConverter() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(null)).withMessage("jwtGrantedAuthoritiesConverter cannot be null");
    }

    @Test
    public void convertWithOverriddenGrantedAuthoritiesConverter() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", "message:read message:write"));
        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter = ( token) -> Arrays.asList(new SimpleGrantedAuthority("blah"));
        this.jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        AbstractAuthenticationToken authentication = this.jwtAuthenticationConverter.convert(jwt);
        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("blah"));
    }
}

