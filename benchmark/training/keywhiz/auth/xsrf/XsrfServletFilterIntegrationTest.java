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
package keywhiz.auth.xsrf;


import DbSeedCommand.defaultPassword;
import DbSeedCommand.defaultUser;
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


public class XsrfServletFilterIntegrationTest {
    OkHttpClient client;

    OkHttpClient noXsrfClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void xsrfNotRequiredForLogin() throws Exception {
        Response response = client.newCall(AuthHelper.buildLoginPost(defaultUser, defaultPassword)).execute();
        assertThat(response.code()).isNotEqualTo(401);
    }

    @Test
    public void xsrfNotRequiredForLogout() throws Exception {
        Request request = new Request.Builder().post(RequestBody.create(MediaType.parse("text/plain"), "")).url(testUrl("/admin/logout")).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isNotEqualTo(401);
    }

    @Test
    public void rejectsForAdminUrlWithoutXsrf() throws Exception {
        noXsrfClient.newCall(AuthHelper.buildLoginPost(defaultUser, defaultPassword)).execute();
        Request request = new Request.Builder().url(testUrl("/admin/clients")).get().build();
        Response response = noXsrfClient.newCall(request).execute();
        assertThat(response.code()).isEqualTo(401);
    }

    @Test
    public void allowsForAdminUrlWithXsrf() throws Exception {
        client.newCall(AuthHelper.buildLoginPost(defaultUser, defaultPassword)).execute();
        Request request = new Request.Builder().url(testUrl("/admin/clients")).get().build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isNotEqualTo(401);
    }
}

