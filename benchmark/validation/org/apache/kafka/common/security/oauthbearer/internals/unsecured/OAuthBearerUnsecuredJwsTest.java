/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.oauthbearer.internals.unsecured;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OAuthBearerUnsecuredJwsTest {
    private static final String QUOTE = "\"";

    private static final String HEADER_COMPACT_SERIALIZATION = (Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8))) + ".";

    @Test
    public void validClaims() throws OAuthBearerIllegalTokenException {
        double issuedAtSeconds = 100.1;
        double expirationTimeSeconds = 300.3;
        StringBuilder sb = new StringBuilder("{");
        OAuthBearerUnsecuredJwsTest.appendJsonText(sb, "sub", "SUBJECT");
        OAuthBearerUnsecuredJwsTest.appendCommaJsonText(sb, "iat", issuedAtSeconds);
        OAuthBearerUnsecuredJwsTest.appendCommaJsonText(sb, "exp", expirationTimeSeconds);
        sb.append("}");
        String compactSerialization = ((OAuthBearerUnsecuredJwsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
        OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
        Assert.assertEquals(compactSerialization, testJwt.value());
        Assert.assertEquals("sub", testJwt.principalClaimName());
        Assert.assertEquals(1, testJwt.header().size());
        Assert.assertEquals("none", testJwt.header().get("alg"));
        Assert.assertEquals("scope", testJwt.scopeClaimName());
        Assert.assertEquals(expirationTimeSeconds, testJwt.expirationTime());
        Assert.assertTrue(testJwt.isClaimType("exp", Number.class));
        Assert.assertEquals(issuedAtSeconds, testJwt.issuedAt());
        Assert.assertEquals("SUBJECT", testJwt.subject());
    }

    @Test
    public void validCompactSerialization() {
        String subject = "foo";
        long issuedAt = 100;
        long expirationTime = issuedAt + (60 * 60);
        List<String> scope = Arrays.asList("scopeValue1", "scopeValue2");
        String validCompactSerialization = OAuthBearerUnsecuredJwsTest.compactSerialization(subject, issuedAt, expirationTime, scope);
        OAuthBearerUnsecuredJws jws = new OAuthBearerUnsecuredJws(validCompactSerialization, "sub", "scope");
        Assert.assertEquals(1, jws.header().size());
        Assert.assertEquals("none", jws.header().get("alg"));
        Assert.assertEquals(4, jws.claims().size());
        Assert.assertEquals(subject, jws.claims().get("sub"));
        Assert.assertEquals(subject, jws.principalName());
        Assert.assertEquals(issuedAt, Number.class.cast(jws.claims().get("iat")).longValue());
        Assert.assertEquals(expirationTime, Number.class.cast(jws.claims().get("exp")).longValue());
        Assert.assertEquals((expirationTime * 1000), jws.lifetimeMs());
        Assert.assertEquals(scope, jws.claims().get("scope"));
        Assert.assertEquals(new HashSet(scope), jws.scope());
        Assert.assertEquals(3, jws.splits().size());
        Assert.assertEquals(validCompactSerialization.split("\\.")[0], jws.splits().get(0));
        Assert.assertEquals(validCompactSerialization.split("\\.")[1], jws.splits().get(1));
        Assert.assertEquals("", jws.splits().get(2));
    }

    @Test(expected = OAuthBearerIllegalTokenException.class)
    public void missingPrincipal() {
        String subject = null;
        long issuedAt = 100;
        Long expirationTime = null;
        List<String> scope = Arrays.asList("scopeValue1", "scopeValue2");
        String validCompactSerialization = OAuthBearerUnsecuredJwsTest.compactSerialization(subject, issuedAt, expirationTime, scope);
        new OAuthBearerUnsecuredJws(validCompactSerialization, "sub", "scope");
    }

    @Test(expected = OAuthBearerIllegalTokenException.class)
    public void blankPrincipalName() {
        String subject = "   ";
        long issuedAt = 100;
        long expirationTime = issuedAt + (60 * 60);
        List<String> scope = Arrays.asList("scopeValue1", "scopeValue2");
        String validCompactSerialization = OAuthBearerUnsecuredJwsTest.compactSerialization(subject, issuedAt, expirationTime, scope);
        new OAuthBearerUnsecuredJws(validCompactSerialization, "sub", "scope");
    }
}

