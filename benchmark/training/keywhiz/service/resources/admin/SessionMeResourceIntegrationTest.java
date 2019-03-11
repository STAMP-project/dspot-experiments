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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import keywhiz.AuthHelper;
import keywhiz.IntegrationTestRule;
import keywhiz.KeywhizService;
import keywhiz.auth.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class SessionMeResourceIntegrationTest {
    ObjectMapper mapper = KeywhizService.customizeObjectMapper(Jackson.newObjectMapper());

    OkHttpClient client;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void getInformation() throws IOException {
        User validUser = User.named(defaultUser);
        client.newCall(AuthHelper.buildLoginPost(validUser.getName(), defaultPassword)).execute();
        Request get = new Request.Builder().get().url(testUrl("/admin/me/")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.body().string()).isEqualTo(mapper.writeValueAsString(validUser));
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    public void adminRejectsNonLoggedInUser() throws IOException {
        client.newCall(AuthHelper.buildLoginPost("username", "password")).execute();
        Request get = new Request.Builder().get().url(testUrl("/admin/me/")).build();
        int status = client.newCall(get).execute().code();
        assertThat(status).isEqualTo(401);
    }
}

