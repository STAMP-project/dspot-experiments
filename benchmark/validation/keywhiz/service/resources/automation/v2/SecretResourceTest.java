package keywhiz.service.resources.automation.v2;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.dropwizard.jackson.Jackson;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import keywhiz.IntegrationTestRule;
import keywhiz.KeywhizService;
import keywhiz.TestClients;
import keywhiz.api.automation.v2.CreateOrUpdateSecretRequestV2;
import keywhiz.api.automation.v2.CreateSecretRequestV2;
import keywhiz.api.automation.v2.ModifyGroupsRequestV2;
import keywhiz.api.automation.v2.PartialUpdateSecretRequestV2;
import keywhiz.api.automation.v2.SecretContentsRequestV2;
import keywhiz.api.automation.v2.SecretContentsResponseV2;
import keywhiz.api.automation.v2.SecretDetailResponseV2;
import keywhiz.api.automation.v2.SetSecretVersionRequestV2;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.SanitizedSecretWithGroups;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class SecretResourceTest {
    private static final ObjectMapper mapper = KeywhizService.customizeObjectMapper(Jackson.newObjectMapper());

    private static final Base64.Encoder encoder = Base64.getEncoder();

    OkHttpClient mutualSslClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    // ---------------------------------------------------------------------------------------
    // createSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void createSecret_successUnVersioned() throws Exception {
        CreateSecretRequestV2 request = CreateSecretRequestV2.builder().name("secret1").content(SecretResourceTest.encoder.encodeToString("supa secret".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build();
        Response httpResponse = create(request);
        assertThat(httpResponse.code()).isEqualTo(201);
        URI location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/secrets/secret1");
    }

    @Test
    public void createSecret_duplicateUnVersioned() throws Exception {
        CreateSecretRequestV2 request = CreateSecretRequestV2.builder().name("secret2").content(SecretResourceTest.encoder.encodeToString("supa secret2".getBytes(StandardCharsets.UTF_8))).description("desc").build();
        Response httpResponse = create(request);
        assertThat(httpResponse.code()).isEqualTo(201);
        httpResponse = create(request);
        assertThat(httpResponse.code()).isEqualTo(409);
    }

    // ---------------------------------------------------------------------------------------
    // createOrUpdateSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void createOrUpdateSecret() throws Exception {
        CreateOrUpdateSecretRequestV2 request = CreateOrUpdateSecretRequestV2.builder().content(SecretResourceTest.encoder.encodeToString("supa secret".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build();
        Response httpResponse = createOrUpdate(request, "secret3");
        assertThat(httpResponse.code()).isEqualTo(201);
        URI location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/secrets/secret3");
        httpResponse = createOrUpdate(request, "secret3");
        assertThat(httpResponse.code()).isEqualTo(201);
        location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/secrets/secret3");
    }

    // ---------------------------------------------------------------------------------------
    // partialUpdateSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void partialUpdateSecret_success() throws Exception {
        // Create a secret to update
        CreateOrUpdateSecretRequestV2 createRequest = CreateOrUpdateSecretRequestV2.builder().content(SecretResourceTest.encoder.encodeToString("supa secret".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build();
        Response httpResponse = createOrUpdate(createRequest, "secret3");
        assertThat(httpResponse.code()).isEqualTo(201);
        URI location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/secrets/secret3");
        // Update the secret's description and set its expiry
        PartialUpdateSecretRequestV2 request = PartialUpdateSecretRequestV2.builder().description("a more detailed description").descriptionPresent(true).expiry(1487268151L).expiryPresent(true).build();
        httpResponse = partialUpdate(request, "secret3");
        assertThat(httpResponse.code()).isEqualTo(201);
        location = URI.create(httpResponse.header(LOCATION));
        assertThat(location.getPath()).isEqualTo("/automation/v2/secrets/secret3");
        // Check the characteristics of the updated secret
        SecretDetailResponseV2 response = lookup("secret3");
        assertThat(response.name()).isEqualTo("secret3");
        assertThat(response.createdBy()).isEqualTo("client");
        assertThat(response.updatedBy()).isEqualTo("client");
        assertThat(response.contentCreatedBy()).isEqualTo("client");
        assertThat(response.updatedAtSeconds()).isEqualTo(response.contentCreatedAtSeconds());
        assertThat(response.type()).isEqualTo("password");
        assertThat(response.metadata()).isEqualTo(ImmutableMap.of("owner", "root", "mode", "0440"));
        assertThat(response.description()).isEqualTo("a more detailed description");
        assertThat(response.expiry()).isEqualTo(1487268151L);
    }

    @Test
    public void partialUpdateSecret_notFound() throws Exception {
        PartialUpdateSecretRequestV2 request = PartialUpdateSecretRequestV2.builder().description("a more detailed description").descriptionPresent(true).expiry(1487268151L).expiryPresent(true).build();
        RequestBody body = RequestBody.create(JSON, SecretResourceTest.mapper.writeValueAsString(request));
        Request post = TestClients.clientRequest("/automation/v2/secrets/non-existent/partialupdate").post(body).build();
        Response httpResponse = mutualSslClient.newCall(post).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    // ---------------------------------------------------------------------------------------
    // secretInfo
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretInfo_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/secrets/non-existent").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void secretInfo_success() throws Exception {
        // Sample secret
        create(CreateSecretRequestV2.builder().name("secret6").content(SecretResourceTest.encoder.encodeToString("supa secret6".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build());
        SecretDetailResponseV2 response = lookup("secret6");
        assertThat(response.name()).isEqualTo("secret6");
        assertThat(response.createdBy()).isEqualTo("client");
        assertThat(response.updatedBy()).isEqualTo("client");
        assertThat(response.createdAtSeconds()).isEqualTo(response.updatedAtSeconds());
        assertThat(response.contentCreatedBy()).isEqualTo("client");
        assertThat(response.createdAtSeconds()).isEqualTo(response.contentCreatedAtSeconds());
        assertThat(response.description()).isEqualTo("desc");
        assertThat(response.type()).isEqualTo("password");
        assertThat(response.metadata()).isEqualTo(ImmutableMap.of("owner", "root", "mode", "0440"));
    }

    // ---------------------------------------------------------------------------------------
    // getSanitizedSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void getSanitizedSecret_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/secrets/non-existent/sanitized").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void getSanitizedSecret_success() throws Exception {
        // Sample secret
        create(CreateSecretRequestV2.builder().name("secret12455").content(SecretResourceTest.encoder.encodeToString("supa secret12455".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build());
        SanitizedSecret response = lookupSanitizedSecret("secret12455");
        assertThat(response.name()).isEqualTo("secret12455");
        assertThat(response.createdBy()).isEqualTo("client");
        assertThat(response.updatedBy()).isEqualTo("client");
        assertThat(response.contentCreatedBy()).isEqualTo("client");
        assertThat(response.description()).isEqualTo("desc");
        assertThat(response.type()).isEqualTo(Optional.of("password"));
        assertThat(response.metadata()).isEqualTo(ImmutableMap.of("owner", "root", "mode", "0440"));
    }

    // ---------------------------------------------------------------------------------------
    // secretContents
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretContents_empty() throws Exception {
        // No error expected when the list of requested secrets is empty
        SecretContentsResponseV2 resp = contents(SecretContentsRequestV2.fromParts(ImmutableSet.of()));
        assertThat(resp.successSecrets().isEmpty()).isTrue();
        assertThat(resp.missingSecrets().isEmpty()).isTrue();
    }

    @Test
    public void secretContents_success() throws Exception {
        // Sample secrets
        create(CreateSecretRequestV2.builder().name("secret23a").content(SecretResourceTest.encoder.encodeToString("supa secret23a".getBytes(StandardCharsets.UTF_8))).description("desc").metadata(ImmutableMap.of("owner", "root", "mode", "0440")).type("password").build());
        create(CreateSecretRequestV2.builder().name("secret23b").content(SecretResourceTest.encoder.encodeToString("supa secret23b".getBytes(StandardCharsets.UTF_8))).description("desc").build());
        SecretContentsRequestV2 request = SecretContentsRequestV2.fromParts(ImmutableSet.of("secret23a", "secret23b", "non-existent"));
        SecretContentsResponseV2 response = contents(request);
        assertThat(response.successSecrets()).isEqualTo(ImmutableMap.of("secret23a", SecretResourceTest.encoder.encodeToString("supa secret23a".getBytes(StandardCharsets.UTF_8)), "secret23b", SecretResourceTest.encoder.encodeToString("supa secret23b".getBytes(StandardCharsets.UTF_8))));
        assertThat(response.missingSecrets()).isEqualTo(ImmutableList.of("non-existent"));
    }

    // ---------------------------------------------------------------------------------------
    // secretGroupsListing
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretGroupsListing_notFound() throws Exception {
        Request get = TestClients.clientRequest("/automation/v2/secrets/non-existent/groups").get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void secretGroupsListing_success() throws Exception {
        createGroup("group7a");
        createGroup("group7b");
        // Sample secret
        create(CreateSecretRequestV2.builder().name("secret7").content(SecretResourceTest.encoder.encodeToString("supa secret7".getBytes(StandardCharsets.UTF_8))).groups("group7a", "group7b").build());
        assertThat(groupsListing("secret7")).containsOnly("group7a", "group7b");
    }

    // ---------------------------------------------------------------------------------------
    // modifySecretGroups
    // ---------------------------------------------------------------------------------------
    @Test
    public void modifySecretGroups_notFound() throws Exception {
        ModifyGroupsRequestV2 request = ModifyGroupsRequestV2.builder().build();
        RequestBody body = RequestBody.create(JSON, SecretResourceTest.mapper.writeValueAsString(request));
        Request put = TestClients.clientRequest("/automation/v2/secrets/non-existent/groups").put(body).build();
        Response httpResponse = mutualSslClient.newCall(put).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void modifySecretGroups_success() throws Exception {
        // Create sample secret and groups
        createGroup("group8a");
        createGroup("group8b");
        createGroup("group8c");
        create(CreateSecretRequestV2.builder().name("secret8").content(SecretResourceTest.encoder.encodeToString("supa secret8".getBytes(StandardCharsets.UTF_8))).groups("group8a", "group8b").build());
        // Modify secret
        ModifyGroupsRequestV2 request = ModifyGroupsRequestV2.builder().addGroups("group8c", "non-existent1").removeGroups("group8a", "non-existent2").build();
        List<String> groups = modifyGroups("secret8", request);
        assertThat(groups).containsOnly("group8b", "group8c");
    }

    // ---------------------------------------------------------------------------------------
    // deleteSecretSeries
    // ---------------------------------------------------------------------------------------
    @Test
    public void deleteSecretSeries_notFound() throws Exception {
        assertThat(deleteSeries("non-existent").code()).isEqualTo(404);
    }

    @Test
    public void deleteSecretSeries_success() throws Exception {
        // Sample secret
        create(CreateSecretRequestV2.builder().name("secret12").content(SecretResourceTest.encoder.encodeToString("supa secret12".getBytes(StandardCharsets.UTF_8))).build());
        createGroup("testGroup");
        ModifyGroupsRequestV2 request = ModifyGroupsRequestV2.builder().addGroups("testGroup", "secret12").build();
        modifyGroups("secret12", request);
        // Delete works
        assertThat(deleteSeries("secret12").code()).isEqualTo(204);
        // Subsequent deletes can't find the secret series
        assertThat(deleteSeries("secret12").code()).isEqualTo(404);
    }

    // ---------------------------------------------------------------------------------------
    // secretListing
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretListing_success() throws Exception {
        // Listing without secret16
        assertThat(listing()).doesNotContain("secret16");
        // Sample secret
        create(CreateSecretRequestV2.builder().name("secret16").description("test secret 16").content(SecretResourceTest.encoder.encodeToString("supa secret16".getBytes(StandardCharsets.UTF_8))).build());
        // Listing with secret16
        assertThat(listing()).contains("secret16");
        List<SanitizedSecret> secrets = listingV2();
        boolean found = false;
        for (SanitizedSecret s : secrets) {
            if (s.name().equals("secret16")) {
                found = true;
                assertThat(s.description()).isEqualTo("test secret 16");
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void secretListingBatch_success() throws Exception {
        // Listing without secret23, 24, 25
        String name1 = "secret23";
        String name2 = "secret24";
        String name3 = "secret25";
        List<String> s = listing();
        assertThat(s).doesNotContain(name1);
        assertThat(s).doesNotContain(name2);
        assertThat(s).doesNotContain(name3);
        // create groups
        createGroup("group16a");
        createGroup("group16b");
        // get current time to calculate timestamps off for expiry
        long now = (System.currentTimeMillis()) / 1000L;
        // add some secrets
        create(CreateSecretRequestV2.builder().name(name1).content(SecretResourceTest.encoder.encodeToString("supa secret17".getBytes(StandardCharsets.UTF_8))).expiry((now + (86400 * 3))).groups("group16a", "group16b").build());
        create(CreateSecretRequestV2.builder().name(name2).content(SecretResourceTest.encoder.encodeToString("supa secret18".getBytes(StandardCharsets.UTF_8))).expiry((now + 86400)).groups("group16a").build());
        create(CreateSecretRequestV2.builder().name(name3).content(SecretResourceTest.encoder.encodeToString("supa secret19".getBytes(StandardCharsets.UTF_8))).expiry((now + (86400 * 2))).groups("group16b").build());
        // check limiting by batch (hard to test because the batch results heavily depend on other
        // tests, which may be run in parallel and often execute fast enough that different tests'
        // secrets have the same creation time as the secrets created in this test)
        List<String> s1 = listBatch(0, 2, false);
        assertThat(s1.size()).isEqualTo(2);
        List<SanitizedSecret> s3 = listBatchV2(0, 2, true);
        assertThat(s3.size()).isEqualTo(2);
    }

    @Test
    public void secretListingBatch_failure() throws Exception {
        // check that negative inputs fail
        Request get = TestClients.clientRequest(String.format("/automation/v2/secrets?idx=%d&num=%d&newestFirst=%s", (-1), 3, false)).get().build();
        Response httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(400);
        get = TestClients.clientRequest(String.format("/automation/v2/secrets?idx=%d&num=%d&newestFirst=%s", 0, (-3), true)).get().build();
        httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(400);
        get = TestClients.clientRequest(String.format("/automation/v2/secrets/v2?idx=%d&num=%d&newestFirst=%s", (-1), 3, false)).get().build();
        httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(400);
        get = TestClients.clientRequest(String.format("/automation/v2/secrets/v2?idx=%d&num=%d&newestFirst=%s", 0, (-3), true)).get().build();
        httpResponse = mutualSslClient.newCall(get).execute();
        assertThat(httpResponse.code()).isEqualTo(400);
    }

    // ---------------------------------------------------------------------------------------
    // backfillExpiration
    // ---------------------------------------------------------------------------------------
    @Test
    public void backfillExpirationTest() throws Exception {
        byte[] certs = Resources.toByteArray(Resources.getResource("fixtures/expiring-certificates.crt"));
        byte[] pubring = Resources.toByteArray(Resources.getResource("fixtures/expiring-pubring.gpg"));
        byte[] p12 = Resources.toByteArray(Resources.getResource("fixtures/expiring-keystore.p12"));
        byte[] jceks = Resources.toByteArray(Resources.getResource("fixtures/expiring-keystore.jceks"));
        create(CreateSecretRequestV2.builder().name("certificate-chain.crt").content(SecretResourceTest.encoder.encodeToString(certs)).build());
        create(CreateSecretRequestV2.builder().name("public-keyring.gpg").content(SecretResourceTest.encoder.encodeToString(pubring)).build());
        create(CreateSecretRequestV2.builder().name("keystore.p12").content(SecretResourceTest.encoder.encodeToString(p12)).build());
        create(CreateSecretRequestV2.builder().name("keystore.jceks").content(SecretResourceTest.encoder.encodeToString(jceks)).build());
        Response response = backfillExpiration("certificate-chain.crt", ImmutableList.of());
        assertThat(response.isSuccessful()).isTrue();
        response = backfillExpiration("public-keyring.gpg", ImmutableList.of());
        assertThat(response.isSuccessful()).isTrue();
        response = backfillExpiration("keystore.p12", ImmutableList.of("password"));
        assertThat(response.isSuccessful()).isTrue();
        response = backfillExpiration("keystore.jceks", ImmutableList.of("password"));
        assertThat(response.isSuccessful()).isTrue();
        SecretDetailResponseV2 details = lookup("certificate-chain.crt");
        assertThat(details.expiry()).isEqualTo(1501533950);
        details = lookup("public-keyring.gpg");
        assertThat(details.expiry()).isEqualTo(1536442365);
        details = lookup("keystore.p12");
        assertThat(details.expiry()).isEqualTo(1681596851);
        details = lookup("keystore.jceks");
        assertThat(details.expiry()).isEqualTo(1681596851);
    }

    // ---------------------------------------------------------------------------------------
    // secretListingExpiry
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretListingExpiry_success() throws Exception {
        // Listing without secret17,18,19
        List<String> s = listing();
        assertThat(s).doesNotContain("secret17");
        assertThat(s).doesNotContain("secret18");
        assertThat(s).doesNotContain("secret19");
        assertThat(s).doesNotContain("secret19a");
        // create groups
        createGroup("group15a");
        createGroup("group15b");
        // get current time to calculate timestamps off for expiry
        long now = (System.currentTimeMillis()) / 1000L;
        // add some secrets
        create(CreateSecretRequestV2.builder().name("secret17").content(SecretResourceTest.encoder.encodeToString("supa secret17".getBytes(StandardCharsets.UTF_8))).expiry((now + (86400 * 3))).groups("group15a", "group15b").build());
        create(CreateSecretRequestV2.builder().name("secret18").content(SecretResourceTest.encoder.encodeToString("supa secret18".getBytes(StandardCharsets.UTF_8))).expiry((now + 86400)).groups("group15a").build());
        create(CreateSecretRequestV2.builder().name("secret19").content(SecretResourceTest.encoder.encodeToString("supa secret19".getBytes(StandardCharsets.UTF_8))).expiry((now + (86400 * 2))).groups("group15b").build());
        create(CreateSecretRequestV2.builder().name("secret19a").content(SecretResourceTest.encoder.encodeToString("supa secret19a".getBytes(StandardCharsets.UTF_8))).expiry((now + (86400 * 2))).build());
        // check limiting by group and expiry
        List<String> s1 = listExpiring((now + (86400 * 4)), "group15a");
        assertThat(s1).contains("secret17");
        assertThat(s1).contains("secret18");
        List<String> s2 = listExpiring((now + (86400 * 4)), "group15b");
        assertThat(s2).contains("secret19");
        assertThat(s2).doesNotContain("secret18");
        List<String> s3 = listExpiring((now + (86400 * 2)), null);
        assertThat(s3).contains("secret18");
        assertThat(s3).doesNotContain("secret17");
        List<SanitizedSecret> s4 = listExpiringV2((now + (86400 * 2)), null);
        assertThat(s4).hasSize(3);
        assertThat(s4.get(0).name()).isEqualTo("secret18");
        assertThat(s4.get(0).expiry()).isEqualTo((now + 86400));
        assertThat(s4.get(1).name()).isEqualTo("secret19");
        assertThat(s4.get(1).expiry()).isEqualTo((now + (86400 * 2)));
        assertThat(s4.get(2).name()).isEqualTo("secret19a");
        assertThat(s4.get(2).expiry()).isEqualTo((now + (86400 * 2)));
        List<SanitizedSecretWithGroups> s5 = listExpiringV3((now + (86400 * 2)), null);
        assertThat(s5).hasSize(3);
        assertThat(s5.get(0).secret().name()).isEqualTo("secret18");
        assertThat(s5.get(0).secret().expiry()).isEqualTo((now + 86400));
        assertThat(s5.get(0).groups().stream().map(Group::getName).collect(Collectors.toList())).containsExactly("group15a");
        assertThat(s5.get(1).secret().name()).isEqualTo("secret19");
        assertThat(s5.get(1).secret().expiry()).isEqualTo((now + (86400 * 2)));
        assertThat(s5.get(1).groups().stream().map(Group::getName).collect(Collectors.toList())).containsExactly("group15b");
        assertThat(s5.get(2).secret().name()).isEqualTo("secret19a");
        assertThat(s5.get(2).secret().expiry()).isEqualTo((now + (86400 * 2)));
        assertThat(s5.get(2).groups()).isEmpty();
    }

    // ---------------------------------------------------------------------------------------
    // secretVersions
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretVersionListing_notFound() throws Exception {
        Request put = TestClients.clientRequest("/automation/v2/secrets/non-existent/versions/0-0").build();
        Response httpResponse = mutualSslClient.newCall(put).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void secretVersionListing_success() throws Exception {
        int totalVersions = 6;
        int sleepInterval = 1100;// Delay so secrets have different creation timestamps

        List<SecretDetailResponseV2> versions;
        assertThat(listing()).doesNotContain("secret20");
        // get current time to calculate timestamps off for expiry
        long now = (System.currentTimeMillis()) / 1000L;
        // Create secrets 1 second apart, so that the order of the versions, which
        // will be listed by content creation time, is fixed
        for (int i = 0; i < totalVersions; i++) {
            createOrUpdate(CreateOrUpdateSecretRequestV2.builder().content(SecretResourceTest.encoder.encodeToString(String.format("supa secret20_v%d", i).getBytes(StandardCharsets.UTF_8))).description(String.format("secret20, version %d", i)).expiry((now + (86400 * 2))).metadata(ImmutableMap.of("version", Integer.toString(i))).build(), "secret20");
            Thread.sleep(sleepInterval);
        }
        // List all versions of this secret
        versions = listVersions("secret20", 0, 1000);
        checkSecretVersions(versions, "secret20", totalVersions, 0, 1000);
        // List the newest half of the versions of this secret
        versions = listVersions("secret20", 0, (totalVersions / 2));
        checkSecretVersions(versions, "secret20", totalVersions, 0, (totalVersions / 2));
        // List the oldest half of the versions of this secret
        versions = listVersions("secret20", (totalVersions / 2), totalVersions);
        checkSecretVersions(versions, "secret20", totalVersions, (totalVersions / 2), totalVersions);
        // List the middle half of the versions of this secret
        versions = listVersions("secret20", (totalVersions / 4), (totalVersions / 2));
        checkSecretVersions(versions, "secret20", totalVersions, (totalVersions / 4), (totalVersions / 2));
    }

    // ---------------------------------------------------------------------------------------
    // resetSecretVersion
    // ---------------------------------------------------------------------------------------
    @Test
    public void secretChangeVersion_notFound() throws Exception {
        Request post = TestClients.clientRequest("/automation/v2/secrets/non-existent/setversion").post(RequestBody.create(JSON, SecretResourceTest.mapper.writeValueAsString(SetSecretVersionRequestV2.builder().name("non-existent").version(0).build()))).build();
        Response httpResponse = mutualSslClient.newCall(post).execute();
        assertThat(httpResponse.code()).isEqualTo(404);
    }

    @Test
    public void secretChangeVersion_success() throws Exception {
        int totalVersions = 6;
        String name = "secret21";
        List<SecretDetailResponseV2> versions;
        SecretDetailResponseV2 initialCurrentVersion;
        SecretDetailResponseV2 finalCurrentVersion;
        assertThat(listing()).doesNotContain(name);
        // get current time to calculate timestamps off for expiry
        long now = (System.currentTimeMillis()) / 1000L;
        // Create secrets
        for (int i = 0; i < totalVersions; i++) {
            createOrUpdate(CreateOrUpdateSecretRequestV2.builder().content(SecretResourceTest.encoder.encodeToString(String.format("supa secret21_v%d", i).getBytes(StandardCharsets.UTF_8))).description(String.format("%s, version %d", name, i)).expiry((now + (86400 * 2))).metadata(ImmutableMap.of("version", Integer.toString(i))).build(), name);
            Thread.sleep((2000 / totalVersions));
        }
        // Get the current version (the last version created)
        initialCurrentVersion = lookup(name);
        assertThat(initialCurrentVersion.name()).isEqualTo(name);
        assertThat(initialCurrentVersion.description()).isEqualTo(String.format("%s, version %d", name, (totalVersions - 1)));
        // Get the earliest version of this secret
        versions = listVersions(name, (totalVersions - 3), 1);
        assertThat(versions.get(0)).isNotEqualTo(initialCurrentVersion);
        // Reset the current version to this version
        setCurrentVersion(SetSecretVersionRequestV2.builder().name(name).version(versions.get(0).version()).build());
        // Get the current version
        finalCurrentVersion = lookup(name);
        assertThat(finalCurrentVersion).isEqualToIgnoringGivenFields(versions.get(0), "updatedAtSeconds");
        assertThat(finalCurrentVersion).isNotEqualTo(initialCurrentVersion);
    }

    @Test
    public void secretChangeVersion_invalidVersion() throws Exception {
        int totalVersions = 3;
        String name = "secret22";
        List<SecretDetailResponseV2> versions;
        SecretDetailResponseV2 initialCurrentVersion;
        SecretDetailResponseV2 finalCurrentVersion;
        assertThat(listing()).doesNotContain(name);
        // get current time to calculate timestamps off for expiry
        long now = (System.currentTimeMillis()) / 1000L;
        // Create secrets
        for (int i = 0; i < totalVersions; i++) {
            createOrUpdate(CreateOrUpdateSecretRequestV2.builder().content(SecretResourceTest.encoder.encodeToString(String.format("supa secret22_v%d", i).getBytes(StandardCharsets.UTF_8))).description(String.format("%s, version %d", name, i)).expiry((now + (86400 * 2))).metadata(ImmutableMap.of("version", Integer.toString(i))).build(), name);
        }
        // Get the current version (the last version created)
        initialCurrentVersion = lookup(name);
        assertThat(initialCurrentVersion.name()).isEqualTo(name);
        assertThat(initialCurrentVersion.description()).isEqualTo(String.format("%s, version %d", name, (totalVersions - 1)));
        // Get an invalid version of this secret
        versions = listVersions(name, 0, totalVersions);
        Optional<Long> maxValidVersion = versions.stream().map(SecretDetailResponseV2::version).max(Long::compare);
        if (maxValidVersion.isPresent()) {
            // Reset the current version to this version
            Request post = TestClients.clientRequest(String.format("/automation/v2/secrets/%s/setversion", name)).post(RequestBody.create(JSON, SecretResourceTest.mapper.writeValueAsString(SetSecretVersionRequestV2.builder().name(name).version(((maxValidVersion.get()) + 1)).build()))).build();
            Response httpResponse = mutualSslClient.newCall(post).execute();
            assertThat(httpResponse.code()).isEqualTo(400);
            // Get the current version, which should not have changed
            finalCurrentVersion = lookup(name);
            assertThat(finalCurrentVersion).isEqualTo(initialCurrentVersion);
        }
    }

    // ---------------------------------------------------------------------------------------
    // Version handling after creation and deletion
    // ---------------------------------------------------------------------------------------
    /**
     * A test which verifies that when a secret is created and deleted, and another secret
     * with the same name is created later, listing versions of the current secret
     * does not include versions from the original secret.
     */
    @Test
    public void secretVersionManagement_createAndDelete() throws Exception {
        String name = "versionManagementSecret";
        String firstDescription = "the first secret with this name";
        String secondDescription = "the second secret with this name";
        // Create a secret
        create(CreateSecretRequestV2.builder().name(name).description(firstDescription).content(SecretResourceTest.encoder.encodeToString("secret version 1".getBytes(StandardCharsets.UTF_8))).build());
        // Check that the secret's current versions are as expected
        List<SecretDetailResponseV2> versions = listVersions(name, 0, 1000);
        assertThat(versions.size()).isEqualTo(1);
        assertThat(versions.get(0).description()).isEqualTo(firstDescription);
        // Delete the secret and recreate it
        deleteSeries(name);
        create(CreateSecretRequestV2.builder().name(name).description(secondDescription).content(SecretResourceTest.encoder.encodeToString("secret version 2".getBytes(StandardCharsets.UTF_8))).build());
        // Check that the original secret's versions were not retrieved
        versions = listVersions(name, 0, 1000);
        assertThat(versions.size()).isEqualTo(1);
        assertThat(versions.get(0).description()).isEqualTo(secondDescription);
    }
}

