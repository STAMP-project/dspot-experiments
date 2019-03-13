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
package keywhiz.service.resources;


import keywhiz.IntegrationTestRule;
import keywhiz.api.SecretDeliveryResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class SecretsDeliveryResourceIntegrationTest {
    OkHttpClient client;

    OkHttpClient noSecretsClient;

    OkHttpClient clientNoClientCert;

    SecretDeliveryResponse generalPassword;

    SecretDeliveryResponse databasePassword;

    SecretDeliveryResponse nobodyPgPassPassword;

    SecretDeliveryResponse nonExistentOwnerPass;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void returnsJsonArrayWhenUserHasMultipleSecrets() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secrets")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        String responseString = response.body().string();
        assertThat(responseString).contains(generalPassword.getName()).contains(databasePassword.getName()).contains(nobodyPgPassPassword.getName()).contains(nonExistentOwnerPass.getName());
    }

    @Test
    public void returnsJsonArray() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secrets")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).startsWith("[").endsWith("]");
    }

    @Test
    public void returnsUnauthorizedWhenUnauthenticated() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secrets")).build();
        Response response = clientNoClientCert.newCall(get).execute();
        assertThat(response.code()).isEqualTo(401);
    }

    @Test
    public void returnsEmptyJsonArrayWhenUserHasNoSecrets() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secrets")).build();
        Response response = noSecretsClient.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("[]");
    }
}

