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


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class OAuthBearerUnsecuredValidatorCallbackHandlerTest {
    private static final String UNSECURED_JWT_HEADER_JSON = ("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("alg", "none"))) + "}";

    private static final Time MOCK_TIME = new MockTime();

    private static final String QUOTE = "\"";

    private static final String PRINCIPAL_CLAIM_VALUE = "username";

    private static final String PRINCIPAL_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("principal", OAuthBearerUnsecuredValidatorCallbackHandlerTest.PRINCIPAL_CLAIM_VALUE);

    private static final String SUB_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("sub", OAuthBearerUnsecuredValidatorCallbackHandlerTest.PRINCIPAL_CLAIM_VALUE);

    private static final String BAD_PRINCIPAL_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("principal", 1);

    private static final long LIFETIME_SECONDS_TO_USE = (1000 * 60) * 60;

    private static final String EXPIRATION_TIME_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.expClaimText(OAuthBearerUnsecuredValidatorCallbackHandlerTest.LIFETIME_SECONDS_TO_USE);

    private static final String TOO_EARLY_EXPIRATION_TIME_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.expClaimText(0);

    private static final String ISSUED_AT_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("iat", ((OAuthBearerUnsecuredValidatorCallbackHandlerTest.MOCK_TIME.milliseconds()) / 1000.0));

    private static final String SCOPE_CLAIM_TEXT = OAuthBearerUnsecuredValidatorCallbackHandlerTest.claimOrHeaderText("scope", "scope1");

    private static final Map<String, String> MODULE_OPTIONS_MAP_NO_SCOPE_REQUIRED;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("unsecuredValidatorPrincipalClaimName", "principal");
        tmp.put("unsecuredValidatorAllowableClockSkewMs", "1");
        MODULE_OPTIONS_MAP_NO_SCOPE_REQUIRED = Collections.unmodifiableMap(tmp);
    }

    private static final Map<String, String> MODULE_OPTIONS_MAP_REQUIRE_EXISTING_SCOPE;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("unsecuredValidatorRequiredScope", "scope1");
        MODULE_OPTIONS_MAP_REQUIRE_EXISTING_SCOPE = Collections.unmodifiableMap(tmp);
    }

    private static final Map<String, String> MODULE_OPTIONS_MAP_REQUIRE_ADDITIONAL_SCOPE;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("unsecuredValidatorRequiredScope", "scope1 scope2");
        MODULE_OPTIONS_MAP_REQUIRE_ADDITIONAL_SCOPE = Collections.unmodifiableMap(tmp);
    }

    @Test
    public void validToken() {
        for (final boolean includeOptionalIssuedAtClaim : new boolean[]{ true, false }) {
            String claimsJson = ((("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.PRINCIPAL_CLAIM_TEXT)) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.EXPIRATION_TIME_CLAIM_TEXT))) + (includeOptionalIssuedAtClaim ? OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.ISSUED_AT_CLAIM_TEXT) : "")) + "}";
            Object validationResult = OAuthBearerUnsecuredValidatorCallbackHandlerTest.validationResult(OAuthBearerUnsecuredValidatorCallbackHandlerTest.UNSECURED_JWT_HEADER_JSON, claimsJson, OAuthBearerUnsecuredValidatorCallbackHandlerTest.MODULE_OPTIONS_MAP_NO_SCOPE_REQUIRED);
            Assert.assertTrue((validationResult instanceof OAuthBearerValidatorCallback));
            Assert.assertTrue(((token()) instanceof OAuthBearerUnsecuredJws));
        }
    }

    @Test
    public void badOrMissingPrincipal() throws IOException, UnsupportedCallbackException {
        for (boolean exists : new boolean[]{ true, false }) {
            String claimsJson = (("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.EXPIRATION_TIME_CLAIM_TEXT)) + (exists ? OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.BAD_PRINCIPAL_CLAIM_TEXT) : "")) + "}";
            OAuthBearerUnsecuredValidatorCallbackHandlerTest.confirmFailsValidation(OAuthBearerUnsecuredValidatorCallbackHandlerTest.UNSECURED_JWT_HEADER_JSON, claimsJson, OAuthBearerUnsecuredValidatorCallbackHandlerTest.MODULE_OPTIONS_MAP_NO_SCOPE_REQUIRED);
        }
    }

    @Test
    public void tooEarlyExpirationTime() throws IOException, UnsupportedCallbackException {
        String claimsJson = ((("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.PRINCIPAL_CLAIM_TEXT)) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.ISSUED_AT_CLAIM_TEXT))) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.TOO_EARLY_EXPIRATION_TIME_CLAIM_TEXT))) + "}";
        OAuthBearerUnsecuredValidatorCallbackHandlerTest.confirmFailsValidation(OAuthBearerUnsecuredValidatorCallbackHandlerTest.UNSECURED_JWT_HEADER_JSON, claimsJson, OAuthBearerUnsecuredValidatorCallbackHandlerTest.MODULE_OPTIONS_MAP_NO_SCOPE_REQUIRED);
    }

    @Test
    public void includesRequiredScope() {
        String claimsJson = ((("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.SUB_CLAIM_TEXT)) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.EXPIRATION_TIME_CLAIM_TEXT))) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.SCOPE_CLAIM_TEXT))) + "}";
        Object validationResult = OAuthBearerUnsecuredValidatorCallbackHandlerTest.validationResult(OAuthBearerUnsecuredValidatorCallbackHandlerTest.UNSECURED_JWT_HEADER_JSON, claimsJson, OAuthBearerUnsecuredValidatorCallbackHandlerTest.MODULE_OPTIONS_MAP_REQUIRE_EXISTING_SCOPE);
        Assert.assertTrue((validationResult instanceof OAuthBearerValidatorCallback));
        Assert.assertTrue(((token()) instanceof OAuthBearerUnsecuredJws));
    }

    @Test
    public void missingRequiredScope() throws IOException, UnsupportedCallbackException {
        String claimsJson = ((("{" + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.SUB_CLAIM_TEXT)) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.EXPIRATION_TIME_CLAIM_TEXT))) + (OAuthBearerUnsecuredValidatorCallbackHandlerTest.comma(OAuthBearerUnsecuredValidatorCallbackHandlerTest.SCOPE_CLAIM_TEXT))) + "}";
        OAuthBearerUnsecuredValidatorCallbackHandlerTest.confirmFailsValidation(OAuthBearerUnsecuredValidatorCallbackHandlerTest.UNSECURED_JWT_HEADER_JSON, claimsJson, OAuthBearerUnsecuredValidatorCallbackHandlerTest.MODULE_OPTIONS_MAP_REQUIRE_ADDITIONAL_SCOPE, "[scope1, scope2]");
    }
}

