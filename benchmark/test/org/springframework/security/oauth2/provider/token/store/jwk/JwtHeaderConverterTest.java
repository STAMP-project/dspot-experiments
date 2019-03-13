/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.security.oauth2.provider.token.store.jwk;


import JwkAttributes.ALGORITHM;
import JwkAttributes.KEY_ID;
import JwkDefinition.CryptoAlgorithm.RS256;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;


/**
 * Tests for {@link JwtHeaderConverter}.
 *
 * @author Joe Grandja
 * @author Vedran Pavic
 */
public class JwtHeaderConverterTest {
    private final JwtHeaderConverter converter = new JwtHeaderConverter();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void convertWhenJwtTokenIsNullThenThrowNullPointerException() throws Exception {
        this.thrown.expect(NullPointerException.class);
        this.converter.convert(null);
    }

    @Test
    public void convertWhenJwtTokenInvalidThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage("Invalid JWT. Missing JOSE Header.");
        this.converter.convert("");
    }

    @Test
    public void convertWhenJwtTokenValidThenReturnJwtHeaders() throws Exception {
        Map<String, String> jwtHeaders = this.converter.convert(JwtTestUtil.createJwt());
        Assert.assertEquals("key-id-1", jwtHeaders.get(KEY_ID));
        Assert.assertEquals(RS256.headerParamValue(), jwtHeaders.get(ALGORITHM));
    }

    @Test
    public void convertWhenJwtTokenWithMalformedHeaderThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage("Invalid JWT. Malformed JOSE Header.");
        this.converter.convert(("f." + (new String(JwtTestUtil.createDefaultJwtPayload()))));
    }
}

