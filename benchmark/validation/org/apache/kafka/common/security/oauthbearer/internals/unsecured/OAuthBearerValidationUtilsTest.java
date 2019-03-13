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
import java.util.Collections;
import java.util.List;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class OAuthBearerValidationUtilsTest {
    private static final String QUOTE = "\"";

    private static final String HEADER_COMPACT_SERIALIZATION = (Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8))) + ".";

    private static final Time TIME = Time.SYSTEM;

    @Test
    public void validateClaimForExistenceAndType() throws OAuthBearerIllegalTokenException {
        String claimName = "foo";
        for (Boolean exists : new Boolean[]{ null, Boolean.TRUE, Boolean.FALSE }) {
            boolean useErrorValue = exists == null;
            for (Boolean required : new boolean[]{ true, false }) {
                StringBuilder sb = new StringBuilder("{");
                OAuthBearerValidationUtilsTest.appendJsonText(sb, "exp", 100);
                OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
                if (useErrorValue)
                    OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, claimName, 1);
                else
                    if ((exists != null) && (exists.booleanValue()))
                        OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, claimName, claimName);


                sb.append("}");
                String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
                OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
                OAuthBearerValidationResult result = OAuthBearerValidationUtils.validateClaimForExistenceAndType(testJwt, required, claimName, String.class);
                if (useErrorValue || (required && (!(exists.booleanValue()))))
                    Assert.assertTrue(OAuthBearerValidationUtilsTest.isFailureWithMessageAndNoFailureScope(result));
                else
                    Assert.assertTrue(OAuthBearerValidationUtilsTest.isSuccess(result));

            }
        }
    }

    @Test
    public void validateIssuedAt() {
        long nowMs = OAuthBearerValidationUtilsTest.TIME.milliseconds();
        double nowClaimValue = ((double) (nowMs)) / 1000;
        for (boolean exists : new boolean[]{ true, false }) {
            StringBuilder sb = new StringBuilder("{");
            OAuthBearerValidationUtilsTest.appendJsonText(sb, "exp", nowClaimValue);
            OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
            if (exists)
                OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "iat", nowClaimValue);

            sb.append("}");
            String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
            OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
            for (boolean required : new boolean[]{ true, false }) {
                for (int allowableClockSkewMs : new int[]{ 0, 5, 10, 20 }) {
                    for (long whenCheckOffsetMs : new long[]{ -10, 0, 10 }) {
                        long whenCheckMs = nowMs + whenCheckOffsetMs;
                        OAuthBearerValidationResult result = OAuthBearerValidationUtils.validateIssuedAt(testJwt, required, whenCheckMs, allowableClockSkewMs);
                        if (required && (!exists))
                            Assert.assertTrue("useErrorValue || required && !exists", OAuthBearerValidationUtilsTest.isFailureWithMessageAndNoFailureScope(result));
                        else
                            if ((!required) && (!exists))
                                Assert.assertTrue("!required && !exists", OAuthBearerValidationUtilsTest.isSuccess(result));
                            else
                                // issued in future
                                if ((nowClaimValue * 1000) > (whenCheckMs + allowableClockSkewMs))
                                    Assert.assertTrue(OAuthBearerValidationUtilsTest.assertionFailureMessage(nowClaimValue, allowableClockSkewMs, whenCheckMs), OAuthBearerValidationUtilsTest.isFailureWithMessageAndNoFailureScope(result));
                                else
                                    Assert.assertTrue(OAuthBearerValidationUtilsTest.assertionFailureMessage(nowClaimValue, allowableClockSkewMs, whenCheckMs), OAuthBearerValidationUtilsTest.isSuccess(result));



                    }
                }
            }
        }
    }

    @Test
    public void validateExpirationTime() {
        long nowMs = OAuthBearerValidationUtilsTest.TIME.milliseconds();
        double nowClaimValue = ((double) (nowMs)) / 1000;
        StringBuilder sb = new StringBuilder("{");
        OAuthBearerValidationUtilsTest.appendJsonText(sb, "exp", nowClaimValue);
        OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
        sb.append("}");
        String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
        OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
        for (int allowableClockSkewMs : new int[]{ 0, 5, 10, 20 }) {
            for (long whenCheckOffsetMs : new long[]{ -10, 0, 10 }) {
                long whenCheckMs = nowMs + whenCheckOffsetMs;
                OAuthBearerValidationResult result = OAuthBearerValidationUtils.validateExpirationTime(testJwt, whenCheckMs, allowableClockSkewMs);
                // expired
                if ((whenCheckMs - allowableClockSkewMs) >= (nowClaimValue * 1000))
                    Assert.assertTrue(OAuthBearerValidationUtilsTest.assertionFailureMessage(nowClaimValue, allowableClockSkewMs, whenCheckMs), OAuthBearerValidationUtilsTest.isFailureWithMessageAndNoFailureScope(result));
                else
                    Assert.assertTrue(OAuthBearerValidationUtilsTest.assertionFailureMessage(nowClaimValue, allowableClockSkewMs, whenCheckMs), OAuthBearerValidationUtilsTest.isSuccess(result));

            }
        }
    }

    @Test
    public void validateExpirationTimeAndIssuedAtConsistency() throws OAuthBearerIllegalTokenException {
        long nowMs = OAuthBearerValidationUtilsTest.TIME.milliseconds();
        double nowClaimValue = ((double) (nowMs)) / 1000;
        for (boolean issuedAtExists : new boolean[]{ true, false }) {
            if (!issuedAtExists) {
                StringBuilder sb = new StringBuilder("{");
                OAuthBearerValidationUtilsTest.appendJsonText(sb, "exp", nowClaimValue);
                OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
                sb.append("}");
                String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
                OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
                Assert.assertTrue(OAuthBearerValidationUtilsTest.isSuccess(OAuthBearerValidationUtils.validateTimeConsistency(testJwt)));
            } else
                for (int expirationTimeOffset = -1; expirationTimeOffset <= 1; ++expirationTimeOffset) {
                    StringBuilder sb = new StringBuilder("{");
                    OAuthBearerValidationUtilsTest.appendJsonText(sb, "iat", nowClaimValue);
                    OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "exp", (nowClaimValue + expirationTimeOffset));
                    OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
                    sb.append("}");
                    String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
                    OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
                    OAuthBearerValidationResult result = OAuthBearerValidationUtils.validateTimeConsistency(testJwt);
                    if (expirationTimeOffset <= 0)
                        Assert.assertTrue(OAuthBearerValidationUtilsTest.isFailureWithMessageAndNoFailureScope(result));
                    else
                        Assert.assertTrue(OAuthBearerValidationUtilsTest.isSuccess(result));

                }

        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void validateScope() {
        long nowMs = OAuthBearerValidationUtilsTest.TIME.milliseconds();
        double nowClaimValue = ((double) (nowMs)) / 1000;
        final List<String> noScope = Collections.emptyList();
        final List<String> scope1 = Arrays.asList("scope1");
        final List<String> scope1And2 = Arrays.asList("scope1", "scope2");
        for (boolean actualScopeExists : new boolean[]{ true, false }) {
            List<? extends List> scopes = (!actualScopeExists) ? Arrays.asList(((List) (null))) : Arrays.asList(noScope, scope1, scope1And2);
            for (List<String> actualScope : scopes) {
                for (boolean requiredScopeExists : new boolean[]{ true, false }) {
                    List<? extends List> requiredScopes = (!requiredScopeExists) ? Arrays.asList(((List) (null))) : Arrays.asList(noScope, scope1, scope1And2);
                    for (List<String> requiredScope : requiredScopes) {
                        StringBuilder sb = new StringBuilder("{");
                        OAuthBearerValidationUtilsTest.appendJsonText(sb, "exp", nowClaimValue);
                        OAuthBearerValidationUtilsTest.appendCommaJsonText(sb, "sub", "principalName");
                        if (actualScope != null)
                            sb.append(',').append(OAuthBearerValidationUtilsTest.scopeJson(actualScope));

                        sb.append("}");
                        String compactSerialization = ((OAuthBearerValidationUtilsTest.HEADER_COMPACT_SERIALIZATION) + (Base64.getUrlEncoder().withoutPadding().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8)))) + ".";
                        OAuthBearerUnsecuredJws testJwt = new OAuthBearerUnsecuredJws(compactSerialization, "sub", "scope");
                        OAuthBearerValidationResult result = OAuthBearerValidationUtils.validateScope(testJwt, requiredScope);
                        if ((!requiredScopeExists) || (requiredScope.isEmpty()))
                            Assert.assertTrue(OAuthBearerValidationUtilsTest.isSuccess(result));
                        else
                            if ((!actualScopeExists) || ((actualScope.size()) < (requiredScope.size())))
                                Assert.assertTrue(OAuthBearerValidationUtilsTest.isFailureWithMessageAndFailureScope(result));
                            else
                                Assert.assertTrue(OAuthBearerValidationUtilsTest.isSuccess(result));


                    }
                }
            }
        }
    }
}

