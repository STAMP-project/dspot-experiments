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
package org.springframework.security.oauth2.jwt;


import JwsAlgorithms.RS256;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link Jwt}.
 *
 * @author Joe Grandja
 */
public class JwtTests {
    private static final String ISS_CLAIM = "iss";

    private static final String SUB_CLAIM = "sub";

    private static final String AUD_CLAIM = "aud";

    private static final String EXP_CLAIM = "exp";

    private static final String NBF_CLAIM = "nbf";

    private static final String IAT_CLAIM = "iat";

    private static final String JTI_CLAIM = "jti";

    private static final String ISS_VALUE = "https://provider.com";

    private static final String SUB_VALUE = "subject1";

    private static final List<String> AUD_VALUE = Arrays.asList("aud1", "aud2");

    private static final long EXP_VALUE = Instant.now().plusSeconds(60).toEpochMilli();

    private static final long NBF_VALUE = Instant.now().plusSeconds(5).toEpochMilli();

    private static final long IAT_VALUE = Instant.now().toEpochMilli();

    private static final String JTI_VALUE = "jwt-id-1";

    private static final Map<String, Object> HEADERS;

    private static final Map<String, Object> CLAIMS;

    private static final String JWT_TOKEN_VALUE = "jwt-token-value";

    static {
        HEADERS = new HashMap<>();
        JwtTests.HEADERS.put("alg", RS256);
        CLAIMS = new HashMap<>();
        JwtTests.CLAIMS.put(JwtTests.ISS_CLAIM, JwtTests.ISS_VALUE);
        JwtTests.CLAIMS.put(JwtTests.SUB_CLAIM, JwtTests.SUB_VALUE);
        JwtTests.CLAIMS.put(JwtTests.AUD_CLAIM, JwtTests.AUD_VALUE);
        JwtTests.CLAIMS.put(JwtTests.EXP_CLAIM, JwtTests.EXP_VALUE);
        JwtTests.CLAIMS.put(JwtTests.NBF_CLAIM, JwtTests.NBF_VALUE);
        JwtTests.CLAIMS.put(JwtTests.IAT_CLAIM, JwtTests.IAT_VALUE);
        JwtTests.CLAIMS.put(JwtTests.JTI_CLAIM, JwtTests.JTI_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenTokenValueIsNullThenThrowIllegalArgumentException() {
        new Jwt(null, Instant.ofEpochMilli(JwtTests.IAT_VALUE), Instant.ofEpochMilli(JwtTests.EXP_VALUE), JwtTests.HEADERS, JwtTests.CLAIMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenHeadersIsEmptyThenThrowIllegalArgumentException() {
        new Jwt(JwtTests.JWT_TOKEN_VALUE, Instant.ofEpochMilli(JwtTests.IAT_VALUE), Instant.ofEpochMilli(JwtTests.EXP_VALUE), Collections.emptyMap(), JwtTests.CLAIMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenClaimsIsEmptyThenThrowIllegalArgumentException() {
        new Jwt(JwtTests.JWT_TOKEN_VALUE, Instant.ofEpochMilli(JwtTests.IAT_VALUE), Instant.ofEpochMilli(JwtTests.EXP_VALUE), JwtTests.HEADERS, Collections.emptyMap());
    }

    @Test
    public void constructorWhenParametersProvidedAndValidThenCreated() {
        Jwt jwt = new Jwt(JwtTests.JWT_TOKEN_VALUE, Instant.ofEpochMilli(JwtTests.IAT_VALUE), Instant.ofEpochMilli(JwtTests.EXP_VALUE), JwtTests.HEADERS, JwtTests.CLAIMS);
        assertThat(jwt.getTokenValue()).isEqualTo(JwtTests.JWT_TOKEN_VALUE);
        assertThat(jwt.getHeaders()).isEqualTo(JwtTests.HEADERS);
        assertThat(jwt.getClaims()).isEqualTo(JwtTests.CLAIMS);
        assertThat(jwt.getIssuer().toString()).isEqualTo(JwtTests.ISS_VALUE);
        assertThat(jwt.getSubject()).isEqualTo(JwtTests.SUB_VALUE);
        assertThat(jwt.getAudience()).isEqualTo(JwtTests.AUD_VALUE);
        assertThat(jwt.getExpiresAt().toEpochMilli()).isEqualTo(JwtTests.EXP_VALUE);
        assertThat(jwt.getNotBefore().getEpochSecond()).isEqualTo(JwtTests.NBF_VALUE);
        assertThat(jwt.getIssuedAt().toEpochMilli()).isEqualTo(JwtTests.IAT_VALUE);
        assertThat(jwt.getId()).isEqualTo(JwtTests.JTI_VALUE);
    }
}

