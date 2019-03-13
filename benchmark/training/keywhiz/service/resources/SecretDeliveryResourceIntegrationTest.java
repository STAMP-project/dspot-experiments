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


import DbSeedCommand.defaultPassword;
import DbSeedCommand.defaultUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.time.Instant;
import keywhiz.IntegrationTestRule;
import keywhiz.KeywhizService;
import keywhiz.api.SecretDeliveryResponse;
import keywhiz.api.model.Client;
import keywhiz.api.model.Secret;
import keywhiz.client.KeywhizClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class SecretDeliveryResourceIntegrationTest {
    ObjectMapper mapper = KeywhizService.customizeObjectMapper(Jackson.newObjectMapper());

    OkHttpClient client;

    Secret generalPassword;

    KeywhizClient keywhizClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void returnsSecretWhenAllowed() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secret/General_Password")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo(mapper.writeValueAsString(SecretDeliveryResponse.fromSecret(generalPassword)));
    }

    @Test
    public void recordsClientExpirationInDatabase() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secret/General_Password")).build();
        // Call into service using mutual-SSL client
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        // Check that expiration was recorded in database
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        Client client = keywhizClient.getClientByName("client");
        assertThat(client.getExpiration()).isNotNull();
        assertThat(client.getExpiration().toInstant().isAfter(Instant.EPOCH)).isTrue();
    }

    @Test
    public void returnsNotFoundWhenSecretUnspecified() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secret/")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    @Test
    public void returnsNotFoundWhenSecretDoesNotExist() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secret/nonexistent")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    @Test
    public void returnsUnauthorizedWhenDenied() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/secret/Hacking_Password")).build();
        Response response = client.newCall(get).execute();
        assertThat(response.code()).isEqualTo(403);
    }
}

