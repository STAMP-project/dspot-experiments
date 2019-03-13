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
package org.springframework.security.oauth2.core.oidc;


import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link OidcIdToken}.
 *
 * @author Joe Grandja
 */
public class OidcIdTokenTests {
    private static final String ISS_CLAIM = "iss";

    private static final String SUB_CLAIM = "sub";

    private static final String AUD_CLAIM = "aud";

    private static final String IAT_CLAIM = "iat";

    private static final String EXP_CLAIM = "exp";

    private static final String AUTH_TIME_CLAIM = "auth_time";

    private static final String NONCE_CLAIM = "nonce";

    private static final String ACR_CLAIM = "acr";

    private static final String AMR_CLAIM = "amr";

    private static final String AZP_CLAIM = "azp";

    private static final String AT_HASH_CLAIM = "at_hash";

    private static final String C_HASH_CLAIM = "c_hash";

    private static final String ISS_VALUE = "https://provider.com";

    private static final String SUB_VALUE = "subject1";

    private static final List<String> AUD_VALUE = Arrays.asList("aud1", "aud2");

    private static final long IAT_VALUE = Instant.now().toEpochMilli();

    private static final long EXP_VALUE = Instant.now().plusSeconds(60).toEpochMilli();

    private static final long AUTH_TIME_VALUE = Instant.now().minusSeconds(5).toEpochMilli();

    private static final String NONCE_VALUE = "nonce";

    private static final String ACR_VALUE = "acr";

    private static final List<String> AMR_VALUE = Arrays.asList("amr1", "amr2");

    private static final String AZP_VALUE = "azp";

    private static final String AT_HASH_VALUE = "at_hash";

    private static final String C_HASH_VALUE = "c_hash";

    private static final Map<String, Object> CLAIMS;

    private static final String ID_TOKEN_VALUE = "id-token-value";

    static {
        CLAIMS = new HashMap<>();
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.ISS_CLAIM, OidcIdTokenTests.ISS_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.SUB_CLAIM, OidcIdTokenTests.SUB_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.AUD_CLAIM, OidcIdTokenTests.AUD_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.IAT_CLAIM, OidcIdTokenTests.IAT_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.EXP_CLAIM, OidcIdTokenTests.EXP_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.AUTH_TIME_CLAIM, OidcIdTokenTests.AUTH_TIME_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.NONCE_CLAIM, OidcIdTokenTests.NONCE_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.ACR_CLAIM, OidcIdTokenTests.ACR_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.AMR_CLAIM, OidcIdTokenTests.AMR_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.AZP_CLAIM, OidcIdTokenTests.AZP_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.AT_HASH_CLAIM, OidcIdTokenTests.AT_HASH_VALUE);
        OidcIdTokenTests.CLAIMS.put(OidcIdTokenTests.C_HASH_CLAIM, OidcIdTokenTests.C_HASH_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenTokenValueIsNullThenThrowIllegalArgumentException() {
        new OidcIdToken(null, Instant.ofEpochMilli(OidcIdTokenTests.IAT_VALUE), Instant.ofEpochMilli(OidcIdTokenTests.EXP_VALUE), OidcIdTokenTests.CLAIMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenClaimsIsEmptyThenThrowIllegalArgumentException() {
        new OidcIdToken(OidcIdTokenTests.ID_TOKEN_VALUE, Instant.ofEpochMilli(OidcIdTokenTests.IAT_VALUE), Instant.ofEpochMilli(OidcIdTokenTests.EXP_VALUE), Collections.emptyMap());
    }

    @Test
    public void constructorWhenParametersProvidedAndValidThenCreated() {
        OidcIdToken idToken = new OidcIdToken(OidcIdTokenTests.ID_TOKEN_VALUE, Instant.ofEpochMilli(OidcIdTokenTests.IAT_VALUE), Instant.ofEpochMilli(OidcIdTokenTests.EXP_VALUE), OidcIdTokenTests.CLAIMS);
        assertThat(idToken.getClaims()).isEqualTo(OidcIdTokenTests.CLAIMS);
        assertThat(idToken.getTokenValue()).isEqualTo(OidcIdTokenTests.ID_TOKEN_VALUE);
        assertThat(idToken.getIssuer().toString()).isEqualTo(OidcIdTokenTests.ISS_VALUE);
        assertThat(idToken.getSubject()).isEqualTo(OidcIdTokenTests.SUB_VALUE);
        assertThat(idToken.getAudience()).isEqualTo(OidcIdTokenTests.AUD_VALUE);
        assertThat(idToken.getIssuedAt().toEpochMilli()).isEqualTo(OidcIdTokenTests.IAT_VALUE);
        assertThat(idToken.getExpiresAt().toEpochMilli()).isEqualTo(OidcIdTokenTests.EXP_VALUE);
        assertThat(idToken.getAuthenticatedAt().getEpochSecond()).isEqualTo(OidcIdTokenTests.AUTH_TIME_VALUE);
        assertThat(idToken.getNonce()).isEqualTo(OidcIdTokenTests.NONCE_VALUE);
        assertThat(idToken.getAuthenticationContextClass()).isEqualTo(OidcIdTokenTests.ACR_VALUE);
        assertThat(idToken.getAuthenticationMethods()).isEqualTo(OidcIdTokenTests.AMR_VALUE);
        assertThat(idToken.getAuthorizedParty()).isEqualTo(OidcIdTokenTests.AZP_VALUE);
        assertThat(idToken.getAccessTokenHash()).isEqualTo(OidcIdTokenTests.AT_HASH_VALUE);
        assertThat(idToken.getAuthorizationCodeHash()).isEqualTo(OidcIdTokenTests.C_HASH_VALUE);
    }
}

