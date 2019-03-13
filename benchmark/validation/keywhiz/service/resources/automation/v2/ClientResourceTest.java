package keywhiz.service.resources.automation.v2;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.jackson.Jackson;
import java.net.URI;
import java.util.List;
import java.util.Set;
import keywhiz.IntegrationTestRule;
import keywhiz.KeywhizService;
import keywhiz.TestClients;
import keywhiz.api.automation.v2.ClientDetailResponseV2;
import keywhiz.api.automation.v2.CreateClientRequestV2;
import keywhiz.api.automation.v2.ModifyClientRequestV2;
import keywhiz.api.automation.v2.ModifyGroupsRequestV2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class ClientResourceTest {
    private static final ObjectMapper mapper = KeywhizService.customizeObjectMapper(Jackson.newObjectMapper());

    OkHttpClient mutualSslClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void createClient_success() throws Exception {
        Response httpResponse = create(CreateClientRequestV2.builder().name("client1").build());
        assertThat(httpResponse.code()).isEqualTo(201);
        URI location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/clients/client1");
    }

    @Test
    public void createClient_duplicate() throws Exception {
        CreateClientRequestV2 request = CreateClientRequestV2.builder().name("client2").build();
        // Initial request OK
        create(request);
        // Duplicate request fails
        Response httpResponse = create(request);
        assertThat(httpResponse.code()).isEqualTo(409);
    }

    @Test
    public void clientInfo_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/clients/non-existent").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void clientInfo_groupExists() throws Exception {
        // Sample client
        create(CreateClientRequestV2.builder().name("client3").build());
        ClientDetailResponseV2 clientDetail = lookup("client3");
        assertThat(clientDetail.name()).isEqualTo("client3");
        assertThat(clientDetail.description()).isEmpty();
        assertThat(clientDetail.createdBy()).isEqualTo(clientDetail.updatedBy()).isEqualTo("client");
    }

    @Test
    public void clientListing() throws Exception {
        Set<String> clientsBefore = listing();
        Set<String> expected = ImmutableSet.<String>builder().addAll(clientsBefore).add("client4").build();
        create(CreateClientRequestV2.builder().name("client4").build());
        assertThat(listing()).containsAll(expected);
    }

    @Test
    public void clientDelete_success() throws Exception {
        // Sample client
        create(CreateClientRequestV2.builder().name("to-delete").build());
        // Deleting is successful
        Response httpResponse = delete("to-delete");
        assertThat(httpResponse.code()).isEqualTo(204);
        // Deleting again produces not-found
        httpResponse = delete("to-delete");
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void clientDelete_notFound() throws Exception {
        Response httpResponse = delete("non-existent");
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void clientGroupsListing_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/clients/non-existent/groups").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void clientGroupsListing_nonExistingGroup() throws Exception {
        create(CreateClientRequestV2.builder().name("client5").groups("non-existent").build());
        assertThat(groupListing("client5")).isEmpty();
    }

    @Test
    public void clientGroupsListing_groupExists() throws Exception {
        // Sample group and client
        createGroup("group6");
        create(CreateClientRequestV2.builder().name("client6").groups("group6").build());
        assertThat(groupListing("client6")).containsOnly("group6");
    }

    @Test
    public void clientSecretsListing_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/clients/non-existent/secrets").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void modifyClientGroups_notFound() throws Exception {
        ModifyGroupsRequestV2 request = ModifyGroupsRequestV2.builder().build();
        RequestBody body = RequestBody.create(JSON, ClientResourceTest.mapper.writeValueAsString(request));
        Request put = TestClients.clientRequest("/automation/v2/clients/non-existent/groups").put(body).build();
        Response httpResponse = mutualSslClient.newCall(put).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void modifyClientGroups_success() throws Exception {
        // Create sample client and groups
        createGroup("group8a");
        createGroup("group8b");
        createGroup("group8c");
        create(CreateClientRequestV2.builder().name("client8").groups("group8a", "group8b").build());
        // Modify client
        ModifyGroupsRequestV2 request = ModifyGroupsRequestV2.builder().addGroups("group8c", "non-existent1").removeGroups("group8a", "non-existent2").build();
        List<String> groups = modifyGroups("client8", request);
        assertThat(groups).containsOnly("group8b", "group8c");
    }

    @Test
    public void modifyClient_notFound() throws Exception {
        ModifyClientRequestV2 request = ModifyClientRequestV2.forName("non-existent");
        RequestBody body = RequestBody.create(JSON, ClientResourceTest.mapper.writeValueAsString(request));
        Request post = TestClients.clientRequest("/automation/v2/clients/non-existent").post(body).build();
        Response httpResponse = mutualSslClient.newCall(post).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }
}

