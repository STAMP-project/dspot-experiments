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
package keywhiz.service.resources.automation;


import keywhiz.IntegrationTestRule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class AutomationEnrollClientGroupResourceIntegrationTest {
    OkHttpClient mutualSslClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void enrollClient() throws Exception {
        // Enroll "CN=User4" in "Blackops"
        Request post = new Request.Builder().put(RequestBody.create(MediaType.parse("text/plain"), "")).url(testUrl("/automation/clients/772/groups/916")).build();
        Response httpResponse = mutualSslClient.newCall(post).execute();
        assertThat(httpResponse.code()).isEqualTo(200);
    }

    @Test
    public void evictClient() throws Exception {
        // Evict "CN=User4" from "Web"
        Request delete = new Request.Builder().delete().url(testUrl("/automation/clients/772/groups/918")).build();
        Response httpResponse = mutualSslClient.newCall(delete).execute();
        assertThat(httpResponse.code()).isEqualTo(200);
    }

    @Test
    public void evictClientNotFound() throws Exception {
        // Evict non-existent client from "Web"
        Request delete = new Request.Builder().delete().url(testUrl("/automation/clients/2/groups/918")).build();
        Response httpResponse = mutualSslClient.newCall(delete).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void evictGroupNotFound() throws Exception {
        // Evict non-existent client from "Web"
        Request delete = new Request.Builder().delete().url(testUrl("/automation/clients/772/groups/5")).build();
        Response httpResponse = mutualSslClient.newCall(delete).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }
}

