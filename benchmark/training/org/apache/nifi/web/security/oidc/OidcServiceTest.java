/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.security.oidc;


import com.nimbusds.oauth2.sdk.id.State;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class OidcServiceTest {
    public static final String TEST_REQUEST_IDENTIFIER = "test-request-identifier";

    public static final String TEST_STATE = "test-state";

    @Test(expected = IllegalStateException.class)
    public void testOidcNotEnabledCreateState() throws Exception {
        final OidcService service = getServiceWithNoOidcSupport();
        service.createState(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateStateMultipleInvocations() throws Exception {
        final OidcService service = getServiceWithOidcSupport();
        service.createState(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
        service.createState(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
    }

    @Test(expected = IllegalStateException.class)
    public void testOidcNotEnabledValidateState() throws Exception {
        final OidcService service = getServiceWithNoOidcSupport();
        service.isStateValid(OidcServiceTest.TEST_REQUEST_IDENTIFIER, new State(OidcServiceTest.TEST_STATE));
    }

    @Test
    public void testOidcUnknownState() throws Exception {
        final OidcService service = getServiceWithOidcSupport();
        Assert.assertFalse(service.isStateValid(OidcServiceTest.TEST_REQUEST_IDENTIFIER, new State(OidcServiceTest.TEST_STATE)));
    }

    @Test
    public void testValidateState() throws Exception {
        final OidcService service = getServiceWithOidcSupport();
        final State state = service.createState(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
        Assert.assertTrue(service.isStateValid(OidcServiceTest.TEST_REQUEST_IDENTIFIER, state));
    }

    @Test
    public void testValidateStateExpiration() throws Exception {
        final OidcService service = getServiceWithOidcSupportAndCustomExpiration(1, TimeUnit.SECONDS);
        final State state = service.createState(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
        Thread.sleep((3 * 1000));
        Assert.assertFalse(service.isStateValid(OidcServiceTest.TEST_REQUEST_IDENTIFIER, state));
    }

    @Test(expected = IllegalStateException.class)
    public void testOidcNotEnabledExchangeCode() throws Exception {
        final OidcService service = getServiceWithNoOidcSupport();
        service.exchangeAuthorizationCode(OidcServiceTest.TEST_REQUEST_IDENTIFIER, getAuthorizationCodeGrant());
    }

    @Test(expected = IllegalStateException.class)
    public void testExchangeCodeMultipleInvocation() throws Exception {
        final OidcService service = getServiceWithOidcSupport();
        service.exchangeAuthorizationCode(OidcServiceTest.TEST_REQUEST_IDENTIFIER, getAuthorizationCodeGrant());
        service.exchangeAuthorizationCode(OidcServiceTest.TEST_REQUEST_IDENTIFIER, getAuthorizationCodeGrant());
    }

    @Test(expected = IllegalStateException.class)
    public void testOidcNotEnabledGetJwt() throws Exception {
        final OidcService service = getServiceWithNoOidcSupport();
        service.getJwt(OidcServiceTest.TEST_REQUEST_IDENTIFIER);
    }

    @Test
    public void testGetJwt() throws Exception {
        final OidcService service = getServiceWithOidcSupport();
        service.exchangeAuthorizationCode(OidcServiceTest.TEST_REQUEST_IDENTIFIER, getAuthorizationCodeGrant());
        Assert.assertNotNull(service.getJwt(OidcServiceTest.TEST_REQUEST_IDENTIFIER));
    }

    @Test
    public void testGetJwtExpiration() throws Exception {
        final OidcService service = getServiceWithOidcSupportAndCustomExpiration(1, TimeUnit.SECONDS);
        service.exchangeAuthorizationCode(OidcServiceTest.TEST_REQUEST_IDENTIFIER, getAuthorizationCodeGrant());
        Thread.sleep((3 * 1000));
        Assert.assertNull(service.getJwt(OidcServiceTest.TEST_REQUEST_IDENTIFIER));
    }
}

