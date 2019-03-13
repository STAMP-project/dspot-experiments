/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.oauth2.core.oidc.user;


import IdTokenClaimNames.ISS;
import IdTokenClaimNames.SUB;
import StandardClaimNames.EMAIL;
import StandardClaimNames.NAME;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;


/**
 * Tests for {@link DefaultOidcUser}.
 *
 * @author Vedran Pavic
 * @author Joe Grandja
 */
public class DefaultOidcUserTests {
    private static final SimpleGrantedAuthority AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");

    private static final Set<GrantedAuthority> AUTHORITIES = Collections.singleton(DefaultOidcUserTests.AUTHORITY);

    private static final String SUBJECT = "test-subject";

    private static final String EMAIL = "test-subject@example.com";

    private static final String NAME = "test-name";

    private static final Map<String, Object> ID_TOKEN_CLAIMS = new HashMap<>();

    private static final Map<String, Object> USER_INFO_CLAIMS = new HashMap<>();

    static {
        DefaultOidcUserTests.ID_TOKEN_CLAIMS.put(ISS, "https://example.com");
        DefaultOidcUserTests.ID_TOKEN_CLAIMS.put(SUB, DefaultOidcUserTests.SUBJECT);
        DefaultOidcUserTests.USER_INFO_CLAIMS.put(StandardClaimNames.NAME, DefaultOidcUserTests.NAME);
        DefaultOidcUserTests.USER_INFO_CLAIMS.put(StandardClaimNames.EMAIL, DefaultOidcUserTests.EMAIL);
    }

    private static final OidcIdToken ID_TOKEN = new OidcIdToken("id-token-value", Instant.EPOCH, Instant.MAX, DefaultOidcUserTests.ID_TOKEN_CLAIMS);

    private static final OidcUserInfo USER_INFO = new OidcUserInfo(DefaultOidcUserTests.USER_INFO_CLAIMS);

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthoritiesIsNullThenThrowIllegalArgumentException() {
        new DefaultOidcUser(null, DefaultOidcUserTests.ID_TOKEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenIdTokenIsNullThenThrowIllegalArgumentException() {
        new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenNameAttributeKeyInvalidThenThrowIllegalArgumentException() {
        new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, DefaultOidcUserTests.ID_TOKEN, "invalid");
    }

    @Test
    public void constructorWhenAuthoritiesIdTokenProvidedThenCreated() {
        DefaultOidcUser user = new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, DefaultOidcUserTests.ID_TOKEN);
        assertThat(user.getClaims()).containsOnlyKeys(ISS, SUB);
        assertThat(user.getIdToken()).isEqualTo(DefaultOidcUserTests.ID_TOKEN);
        assertThat(user.getName()).isEqualTo(DefaultOidcUserTests.SUBJECT);
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next()).isEqualTo(DefaultOidcUserTests.AUTHORITY);
        assertThat(user.getAttributes()).containsOnlyKeys(ISS, SUB);
    }

    @Test
    public void constructorWhenAuthoritiesIdTokenNameAttributeKeyProvidedThenCreated() {
        DefaultOidcUser user = new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, DefaultOidcUserTests.ID_TOKEN, IdTokenClaimNames.SUB);
        assertThat(user.getClaims()).containsOnlyKeys(ISS, SUB);
        assertThat(user.getIdToken()).isEqualTo(DefaultOidcUserTests.ID_TOKEN);
        assertThat(user.getName()).isEqualTo(DefaultOidcUserTests.SUBJECT);
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next()).isEqualTo(DefaultOidcUserTests.AUTHORITY);
        assertThat(user.getAttributes()).containsOnlyKeys(ISS, SUB);
    }

    @Test
    public void constructorWhenAuthoritiesIdTokenUserInfoProvidedThenCreated() {
        DefaultOidcUser user = new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, DefaultOidcUserTests.ID_TOKEN, DefaultOidcUserTests.USER_INFO);
        assertThat(user.getClaims()).containsOnlyKeys(ISS, SUB, StandardClaimNames.NAME, StandardClaimNames.EMAIL);
        assertThat(user.getIdToken()).isEqualTo(DefaultOidcUserTests.ID_TOKEN);
        assertThat(user.getUserInfo()).isEqualTo(DefaultOidcUserTests.USER_INFO);
        assertThat(user.getName()).isEqualTo(DefaultOidcUserTests.SUBJECT);
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next()).isEqualTo(DefaultOidcUserTests.AUTHORITY);
        assertThat(user.getAttributes()).containsOnlyKeys(ISS, SUB, StandardClaimNames.NAME, StandardClaimNames.EMAIL);
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        DefaultOidcUser user = new DefaultOidcUser(DefaultOidcUserTests.AUTHORITIES, DefaultOidcUserTests.ID_TOKEN, DefaultOidcUserTests.USER_INFO, StandardClaimNames.EMAIL);
        assertThat(user.getClaims()).containsOnlyKeys(ISS, SUB, StandardClaimNames.NAME, StandardClaimNames.EMAIL);
        assertThat(user.getIdToken()).isEqualTo(DefaultOidcUserTests.ID_TOKEN);
        assertThat(user.getUserInfo()).isEqualTo(DefaultOidcUserTests.USER_INFO);
        assertThat(user.getName()).isEqualTo(DefaultOidcUserTests.EMAIL);
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next()).isEqualTo(DefaultOidcUserTests.AUTHORITY);
        assertThat(user.getAttributes()).containsOnlyKeys(ISS, SUB, StandardClaimNames.NAME, StandardClaimNames.EMAIL);
    }
}

