/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.service.resources.admin;


import DbSeedCommand.defaultPassword;
import DbSeedCommand.defaultUser;
import HttpHeaders.SET_COOKIE;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;
import keywhiz.AuthHelper;
import keywhiz.IntegrationTestRule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class SessionLoginResourceIntegrationTest {
    OkHttpClient client;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void respondsToLogin() throws Exception {
        Request post = new Request.Builder().post(RequestBody.create(MediaType.parse("text/plain"), "")).url(testUrl("/admin/login")).build();
        Response response = client.newCall(post).execute();
        assertThat(response.code()).isNotEqualTo(404);
    }

    @Test
    public void setsValidCookieForValidCredentials() throws Exception {
        Request post = AuthHelper.buildLoginPost(defaultUser, defaultPassword);
        Response response = client.newCall(post).execute();
        assertThat(response.code()).isEqualTo(200);
        List<String> cookieNames = Lists.newArrayList();
        String sessionCookie = null;
        for (String cookieString : response.headers(SET_COOKIE)) {
            cookieString = cookieString.substring(0, cookieString.indexOf(";"));
            String cookieName = cookieString.substring(0, cookieString.indexOf("="));
            cookieNames.add(cookieName);
            if (cookieName.equals("session")) {
                sessionCookie = cookieString;
            }
        }
        assertThat(cookieNames).containsOnly("session", "XSRF-TOKEN");
        Pattern pattern = Pattern.compile("^session=(.+)$");
        assertThat(sessionCookie).matches(pattern);
    }

    @Test
    public void invalidCredentialsAreUnauthorized() throws Exception {
        Request request = AuthHelper.buildLoginPost("username", "badpassword");
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(401);
    }

    @Test
    public void insufficientRolesAreUnauthorized() throws Exception {
        Request request = AuthHelper.buildLoginPost("username", "password");
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(401);
    }

    @Test
    public void noFormDataIsBadRequest() throws Exception {
        Request request = AuthHelper.buildLoginPost(null, null);
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void missingUsernameIsBadRequest() throws Exception {
        Request request = AuthHelper.buildLoginPost(null, "password");
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void missingPasswordIsBadRequest() throws Exception {
        Request request = AuthHelper.buildLoginPost("username", null);
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(400);
    }
}

