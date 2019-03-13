/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.networknt.client.oauth;


import TokenRequest.AUTHORIZATION_CODE;
import com.networknt.monad.Result;
import io.undertow.Undertow;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is the tests for OauthHelper and it doesn't need live light-oauth2
 * server up and running.
 */
public class OauthHelperTest {
    static final Logger logger = LoggerFactory.getLogger(OauthHelperTest.class);

    static final String token = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTc5MDAzNTcwOSwianRpIjoiSTJnSmdBSHN6NzJEV2JWdUFMdUU2QSIsImlhdCI6MTQ3NDY3NTcwOSwibmJmIjoxNDc0Njc1NTg5LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJzY29wZSI6WyJ3cml0ZTpwZXRzIiwicmVhZDpwZXRzIl19.mue6eh70kGS3Nt2BCYz7ViqwO7lh_4JSFwcHYdJMY6VfgKTHhsIGKq2uEDt3zwT56JFAePwAxENMGUTGvgceVneQzyfQsJeVGbqw55E9IfM_uSM-YcHwTfR7eSLExN4pbqzVDI353sSOvXxA98ZtJlUZKgXNE1Ngun3XFORCRIB_eH8B0FY_nT_D1Dq2WJrR-re-fbR6_va95vwoUdCofLRa4IpDfXXx19ZlAtfiVO44nw6CS8O87eGfAm7rCMZIzkWlCOFWjNHnCeRsh7CVdEH34LF-B48beiG5lM7h4N12-EME8_VDefgMjZ8eqs1ICvJMxdIut58oYbdnkwTjkA";

    static Undertow server = null;

    @Test
    public void testGetTokenResult() throws Exception {
        AuthorizationCodeRequest tokenRequest = new AuthorizationCodeRequest();
        tokenRequest.setClientId("test_client");
        tokenRequest.setClientSecret("test_secret");
        tokenRequest.setGrantType(AUTHORIZATION_CODE);
        List<String> list = new ArrayList<>();
        list.add("test.r");
        list.add("test.w");
        tokenRequest.setScope(list);
        tokenRequest.setServerUrl("http://localhost:8887");
        tokenRequest.setEnableHttp2(true);
        tokenRequest.setUri("/oauth2/token");
        tokenRequest.setRedirectUri("https://localhost:8443/authorize");
        tokenRequest.setAuthCode("test_code");
        Result<TokenResponse> result = OauthHelper.getTokenResult(tokenRequest);
        Assert.assertTrue(result.isSuccess());
        TokenResponse tokenResponse = result.getResult();
        System.out.println(("tokenResponse = " + tokenResponse));
    }

    @Test
    public void testGetToken() throws Exception {
        AuthorizationCodeRequest tokenRequest = new AuthorizationCodeRequest();
        tokenRequest.setClientId("test_client");
        tokenRequest.setClientSecret("test_secret");
        tokenRequest.setGrantType(AUTHORIZATION_CODE);
        List<String> list = new ArrayList<>();
        list.add("test.r");
        list.add("test.w");
        tokenRequest.setScope(list);
        tokenRequest.setServerUrl("http://localhost:8887");
        tokenRequest.setEnableHttp2(true);
        tokenRequest.setUri("/oauth2/token");
        tokenRequest.setRedirectUri("https://localhost:8443/authorize");
        tokenRequest.setAuthCode("test_code");
        TokenResponse tokenResponse = OauthHelper.getToken(tokenRequest);
        Assert.assertNotNull(tokenResponse);
        System.out.println(("tokenResponse = " + tokenResponse));
    }

    @Test
    public void testGetKey() throws Exception {
        KeyRequest keyRequest = new KeyRequest("100");
        keyRequest.setClientId("test_client");
        keyRequest.setClientSecret("test_secret");
        keyRequest.setServerUrl("http://localhost:8887");
        keyRequest.setUri("/oauth2/key");
        keyRequest.setEnableHttp2(true);
        String key = OauthHelper.getKey(keyRequest);
        System.out.println(("key = " + key));
    }

    @Test
    public void testDerefToken() throws Exception {
        DerefRequest derefRequest = new DerefRequest("xW-IFk66TKGlxLWDEaDkvg");
        derefRequest.setClientId("test_client");
        derefRequest.setClientSecret("test_secret");
        derefRequest.setServerUrl("http://localhost:8887");
        derefRequest.setUri("/oauth2/deref");
        derefRequest.setEnableHttp2(true);
        String jwt = OauthHelper.derefToken(derefRequest);
        System.out.println(("jwt = " + jwt));
    }
}

