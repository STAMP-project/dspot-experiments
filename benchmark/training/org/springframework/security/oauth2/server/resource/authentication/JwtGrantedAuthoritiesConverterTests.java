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
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * Tests for {@link JwtGrantedAuthoritiesConverter}
 *
 * @author Eric Deandrea
 * @since 5.2
 */
public class JwtGrantedAuthoritiesConverterTests {
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Test
    public void convertWhenTokenHasScopeAttributeThenTranslatedToAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", "message:read message:write"));
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_message:read"), new SimpleGrantedAuthority("SCOPE_message:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScopeAttributeThenTranslatedToNoAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scope", ""));
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly();
    }

    @Test
    public void convertWhenTokenHasScpAttributeThenTranslatedToAuthorities() {
        Jwt jwt = this.jwt(Collections.singletonMap("scp", Arrays.asList("message:read", "message:write")));
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_message:read"), new SimpleGrantedAuthority("SCOPE_message:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScpAttributeThenTranslatedToNoAuthorities() {
        Jwt jwt = this.jwt(Maps.newHashMap("scp", Arrays.asList()));
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly();
    }

    @Test
    public void convertWhenTokenHasBothScopeAndScpThenScopeAttributeIsTranslatedToAuthorities() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scp", Arrays.asList("message:read", "message:write"));
        claims.put("scope", "missive:read missive:write");
        Jwt jwt = this.jwt(claims);
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly(new SimpleGrantedAuthority("SCOPE_missive:read"), new SimpleGrantedAuthority("SCOPE_missive:write"));
    }

    @Test
    public void convertWhenTokenHasEmptyScopeAndNonEmptyScpThenScopeAttributeIsTranslatedToNoAuthorities() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scp", Arrays.asList("message:read", "message:write"));
        claims.put("scope", "");
        Jwt jwt = this.jwt(claims);
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        assertThat(authorities).containsExactly();
    }
}

