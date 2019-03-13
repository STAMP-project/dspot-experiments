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
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;


/**
 * Tests for {@link OidcUserAuthority}.
 *
 * @author Joe Grandja
 */
public class OidcUserAuthorityTests {
    private static final String AUTHORITY = "ROLE_USER";

    private static final String SUBJECT = "test-subject";

    private static final String EMAIL = "test-subject@example.com";

    private static final String NAME = "test-name";

    private static final Map<String, Object> ID_TOKEN_CLAIMS = new HashMap<>();

    private static final Map<String, Object> USER_INFO_CLAIMS = new HashMap<>();

    static {
        OidcUserAuthorityTests.ID_TOKEN_CLAIMS.put(ISS, "https://example.com");
        OidcUserAuthorityTests.ID_TOKEN_CLAIMS.put(SUB, OidcUserAuthorityTests.SUBJECT);
        OidcUserAuthorityTests.USER_INFO_CLAIMS.put(StandardClaimNames.NAME, OidcUserAuthorityTests.NAME);
        OidcUserAuthorityTests.USER_INFO_CLAIMS.put(StandardClaimNames.EMAIL, OidcUserAuthorityTests.EMAIL);
    }

    private static final OidcIdToken ID_TOKEN = new OidcIdToken("id-token-value", Instant.EPOCH, Instant.MAX, OidcUserAuthorityTests.ID_TOKEN_CLAIMS);

    private static final OidcUserInfo USER_INFO = new OidcUserInfo(OidcUserAuthorityTests.USER_INFO_CLAIMS);

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenIdTokenIsNullThenThrowIllegalArgumentException() {
        new OidcUserAuthority(null);
    }

    @Test
    public void constructorWhenUserInfoIsNullThenDoesNotThrowAnyException() {
        assertThatCode(() -> new OidcUserAuthority(ID_TOKEN, null)).doesNotThrowAnyException();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthorityIsNullThenThrowIllegalArgumentException() {
        new OidcUserAuthority(null, OidcUserAuthorityTests.ID_TOKEN, OidcUserAuthorityTests.USER_INFO);
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        OidcUserAuthority userAuthority = new OidcUserAuthority(OidcUserAuthorityTests.AUTHORITY, OidcUserAuthorityTests.ID_TOKEN, OidcUserAuthorityTests.USER_INFO);
        assertThat(userAuthority.getIdToken()).isEqualTo(OidcUserAuthorityTests.ID_TOKEN);
        assertThat(userAuthority.getUserInfo()).isEqualTo(OidcUserAuthorityTests.USER_INFO);
        assertThat(userAuthority.getAuthority()).isEqualTo(OidcUserAuthorityTests.AUTHORITY);
        assertThat(userAuthority.getAttributes()).containsOnlyKeys(ISS, SUB, StandardClaimNames.NAME, StandardClaimNames.EMAIL);
    }
}

