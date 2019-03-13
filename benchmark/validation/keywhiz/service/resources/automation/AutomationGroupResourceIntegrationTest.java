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


import HttpHeaders.ACCEPT;
import HttpHeaders.CONTENT_TYPE;
import KeywhizClient.JSON;
import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.jackson.Jackson;
import java.util.List;
import keywhiz.IntegrationTestRule;
import keywhiz.KeywhizService;
import keywhiz.api.CreateGroupRequest;
import keywhiz.api.GroupDetailResponse;
import keywhiz.api.model.Group;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class AutomationGroupResourceIntegrationTest {
    ObjectMapper mapper = KeywhizService.customizeObjectMapper(Jackson.newObjectMapper());

    OkHttpClient mutualSslClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void findGroup() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/automation/groups?name=Web")).addHeader(ACCEPT, APPLICATION_JSON).build();
        Response response = mutualSslClient.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        GroupDetailResponse groupResponse = mapper.readValue(response.body().string(), GroupDetailResponse.class);
        assertThat(groupResponse.getId()).isEqualTo(918);
    }

    @Test
    public void findAllGroups() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/automation/groups")).addHeader(ACCEPT, APPLICATION_JSON).build();
        Response response = mutualSslClient.newCall(get).execute();
        assertThat(response.code()).isEqualTo(200);
        List<GroupDetailResponse> groups = mapper.readValue(response.body().string(), new TypeReference<List<GroupDetailResponse>>() {});
        assertThat(groups).extracting("name").contains("Blackops", "Security", "Web", "iOS");
    }

    @Test
    public void findGroupNotFound() throws Exception {
        Request get = new Request.Builder().get().url(testUrl("/automation/groups?name=non-existent-group")).build();
        Response response = mutualSslClient.newCall(get).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    @Test
    public void createGroup() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest("newgroup", "group-description", ImmutableMap.of("app", "new"));
        String body = mapper.writeValueAsString(request);
        Request post = new Request.Builder().post(RequestBody.create(JSON, body)).url(testUrl("/automation/groups")).addHeader(CONTENT_TYPE, APPLICATION_JSON).addHeader(ACCEPT, APPLICATION_JSON).build();
        Response response = mutualSslClient.newCall(post).execute();
        assertThat(response.code()).isEqualTo(200);
    }

    @Test
    public void createGroupRedundant() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest("Web", "group-description", ImmutableMap.of("app", "Web"));
        String body = mapper.writeValueAsString(request);
        Request post = new Request.Builder().post(RequestBody.create(JSON, body)).url(testUrl("/automation/groups")).addHeader(CONTENT_TYPE, APPLICATION_JSON).addHeader(ACCEPT, APPLICATION_JSON).build();
        Response response = mutualSslClient.newCall(post).execute();
        assertThat(response.code()).isEqualTo(409);
    }

    @Test
    public void deleteGroup() throws Exception {
        String body = mapper.writeValueAsString(new CreateGroupRequest("short-lived", "group-description", ImmutableMap.of("app", "short")));
        Request post = new Request.Builder().post(RequestBody.create(JSON, body)).url(testUrl("/automation/groups")).addHeader(CONTENT_TYPE, APPLICATION_JSON).addHeader(ACCEPT, APPLICATION_JSON).build();
        Response response = mutualSslClient.newCall(post).execute();
        assertThat(response.code()).isEqualTo(200);
        long groupId = mapper.readValue(response.body().string(), Group.class).getId();
        Request delete = new Request.Builder().delete().url(testUrl(("/automation/groups/" + groupId))).build();
        response = mutualSslClient.newCall(delete).execute();
        assertThat(response.code()).isEqualTo(200);
        Request lookup = new Request.Builder().get().url(testUrl(("/automation/groups/" + groupId))).addHeader(ACCEPT, APPLICATION_JSON).build();
        response = mutualSslClient.newCall(lookup).execute();
        assertThat(response.code()).isEqualTo(404);
    }
}

