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


import HttpHeaders.LOCATION;
import SecretController.SecretBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.dropwizard.jersey.params.LongParam;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import keywhiz.api.ApiDate;
import keywhiz.api.CreateSecretRequest;
import keywhiz.api.SecretDetailResponse;
import keywhiz.api.automation.v2.CreateOrUpdateSecretRequestV2;
import keywhiz.api.automation.v2.PartialUpdateSecretRequestV2;
import keywhiz.api.model.Client;
import keywhiz.api.model.Group;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.auth.User;
import keywhiz.log.AuditLog;
import keywhiz.log.SimpleLogger;
import keywhiz.service.daos.AclDAO;
import keywhiz.service.daos.SecretController;
import keywhiz.service.daos.SecretDAO;
import keywhiz.service.exceptions.ConflictException;
import org.apache.http.HttpStatus;
import org.jooq.exception.DataAccessException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SecretsResourceTest {
    private static final ApiDate NOW = ApiDate.now();

    private static final ApiDate NOWPLUS = new ApiDate(((SecretsResourceTest.NOW.toEpochSecond()) + 10000));

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    AclDAO aclDAO;

    @Mock
    SecretDAO secretDAO;

    @Mock
    SecretController secretController;

    User user = User.named("user");

    ImmutableMap<String, String> emptyMap = ImmutableMap.of();

    Secret secret = new Secret(22, "name", "desc", () -> "secret", "checksum", SecretsResourceTest.NOW, "creator", SecretsResourceTest.NOW, "updater", emptyMap, null, null, 1136214245, 1L, SecretsResourceTest.NOW, "user");

    AuditLog auditLog = new SimpleLogger();

    SecretsResource resource;

    @Test
    public void listSecrets() {
        SanitizedSecret secret1 = SanitizedSecret.of(1, "name1", "desc", "checksum", SecretsResourceTest.NOW, "user", SecretsResourceTest.NOW, "user", emptyMap, null, null, 1136214245, 125L, SecretsResourceTest.NOW, "user");
        SanitizedSecret secret2 = SanitizedSecret.of(2, "name2", "desc", "checksum", SecretsResourceTest.NOW, "user", SecretsResourceTest.NOW, "user", emptyMap, null, null, 1136214245, 250L, SecretsResourceTest.NOW, "user");
        Mockito.when(secretController.getSanitizedSecrets(null, null)).thenReturn(ImmutableList.of(secret1, secret2));
        List<SanitizedSecret> response = resource.listSecrets(user);
        assertThat(response).containsOnly(secret1, secret2);
    }

    @Test
    public void listSecretsBatched() {
        SanitizedSecret secret1 = SanitizedSecret.of(1, "name1", "desc", "checksum", SecretsResourceTest.NOW, "user", SecretsResourceTest.NOW, "user", emptyMap, null, null, 1136214245, 125L, SecretsResourceTest.NOW, "user");
        SanitizedSecret secret2 = SanitizedSecret.of(2, "name2", "desc", "checksum", SecretsResourceTest.NOWPLUS, "user", SecretsResourceTest.NOWPLUS, "user", emptyMap, null, null, 1136214245, 250L, SecretsResourceTest.NOW, "user");
        Mockito.when(secretController.getSecretsBatched(0, 1, false)).thenReturn(ImmutableList.of(secret1));
        Mockito.when(secretController.getSecretsBatched(0, 1, true)).thenReturn(ImmutableList.of(secret2));
        Mockito.when(secretController.getSecretsBatched(1, 1, false)).thenReturn(ImmutableList.of(secret2));
        List<SanitizedSecret> response = resource.listSecretsBatched(user, 0, 1, false);
        assertThat(response).containsOnly(secret1);
        response = resource.listSecretsBatched(user, 1, 1, false);
        assertThat(response).containsOnly(secret2);
        response = resource.listSecretsBatched(user, 0, 1, true);
        assertThat(response).containsOnly(secret2);
    }

    @Test
    public void createsSecret() throws Exception {
        Mockito.when(secretController.getSecretById(secret.getId())).thenReturn(Optional.of(secret));
        SecretController.SecretBuilder secretBuilder = Mockito.mock(SecretBuilder.class);
        Mockito.when(secretController.builder(secret.getName(), secret.getSecret(), user.getName(), 0)).thenReturn(secretBuilder);
        Mockito.when(secretBuilder.create()).thenReturn(secret);
        CreateSecretRequest req = new CreateSecretRequest(secret.getName(), secret.getDescription(), secret.getSecret(), emptyMap, 0);
        Response response = resource.createSecret(user, req);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMetadata().get(LOCATION)).containsExactly(new URI(("/admin/secrets/" + (secret.getId()))));
    }

    @Test
    public void createOrUpdateSecret() throws Exception {
        Mockito.when(secretController.getSecretById(secret.getId())).thenReturn(Optional.of(secret));
        SecretController.SecretBuilder secretBuilder = Mockito.mock(SecretBuilder.class);
        Mockito.when(secretController.builder(secret.getName(), secret.getSecret(), user.getName(), 0)).thenReturn(secretBuilder);
        Mockito.when(secretBuilder.withDescription(ArgumentMatchers.any())).thenReturn(secretBuilder);
        Mockito.when(secretBuilder.withMetadata(ArgumentMatchers.any())).thenReturn(secretBuilder);
        Mockito.when(secretBuilder.withType(ArgumentMatchers.any())).thenReturn(secretBuilder);
        Mockito.when(secretBuilder.createOrUpdate()).thenReturn(secret);
        CreateOrUpdateSecretRequestV2 req = CreateOrUpdateSecretRequestV2.builder().description(secret.getDescription()).content(secret.getSecret()).build();
        Response response = resource.createOrUpdateSecret(user, secret.getName(), req);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMetadata().get(LOCATION)).containsExactly(new URI(("/admin/secrets/" + (secret.getName()))));
    }

    @Test
    public void partialUpdateSecret() throws Exception {
        Mockito.when(secretController.getSecretById(secret.getId())).thenReturn(Optional.of(secret));
        PartialUpdateSecretRequestV2 req = PartialUpdateSecretRequestV2.builder().description(secret.getDescription()).descriptionPresent(true).metadata(ImmutableMap.of("owner", "keywhizAdmin")).metadataPresent(true).expiry(1487268151L).expiryPresent(true).type("test").typePresent(true).content(secret.getSecret()).contentPresent(true).build();
        Mockito.when(secretDAO.partialUpdateSecret(ArgumentMatchers.eq(secret.getName()), ArgumentMatchers.any(), ArgumentMatchers.eq(req))).thenReturn(secret.getId());
        Response response = resource.partialUpdateSecret(user, secret.getName(), req);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMetadata().get(LOCATION)).containsExactly(new URI((("/admin/secrets/" + (secret.getName())) + "/partialupdate")));
    }

    @Test
    public void listSecretVersions() {
        SanitizedSecret secret1 = SanitizedSecret.of(1, "name1", "desc", "checksum", SecretsResourceTest.NOW, "user", SecretsResourceTest.NOW, "user", emptyMap, null, null, 1136214245, 125L, SecretsResourceTest.NOW, "user");
        SanitizedSecret secret2 = SanitizedSecret.of(1, "name1", "desc", "checksum2", SecretsResourceTest.NOWPLUS, "user", SecretsResourceTest.NOWPLUS, "user", emptyMap, null, null, 1136214245, 250L, SecretsResourceTest.NOW, "user");
        Mockito.when(secretDAO.getSecretVersionsByName("name1", 0, 10)).thenReturn(Optional.of(ImmutableList.of(secret2, secret1)));
        Mockito.when(secretDAO.getSecretVersionsByName("name1", 1, 5)).thenReturn(Optional.of(ImmutableList.of(secret1)));
        Mockito.when(secretDAO.getSecretVersionsByName("name1", 2, 10)).thenReturn(Optional.of(ImmutableList.of()));
        List<SanitizedSecret> response = resource.secretVersions(user, "name1", 0, 10);
        assertThat(response).containsExactly(secret2, secret1);
        response = resource.secretVersions(user, "name1", 1, 5);
        assertThat(response).containsExactly(secret1);
        response = resource.secretVersions(user, "name1", 2, 10);
        assertThat(response).isEmpty();
    }

    @Test(expected = NotFoundException.class)
    public void listSecretVersionsThrowsException() {
        Mockito.when(secretDAO.getSecretVersionsByName(ArgumentMatchers.eq("name2"), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Optional.empty());
        resource.secretVersions(user, "name2", 0, 10);
    }

    @Test
    public void rollbackSuccess() {
        Secret secret1 = new Secret(1, "name1", "desc", () -> "secret", "checksum", SecretsResourceTest.NOW, "user", SecretsResourceTest.NOW, "user", emptyMap, null, null, 1136214245, 125L, SecretsResourceTest.NOW, "user");
        Mockito.when(secretController.getSecretByName("name1")).thenReturn(Optional.of(secret1));
        Response response = resource.resetSecretVersion(user, "name1", new LongParam("125"));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test(expected = NotFoundException.class)
    public void rollbackThrowsException() {
        Mockito.doThrow(new NotFoundException()).when(secretDAO).setCurrentSecretVersionByName(ArgumentMatchers.eq("name2"), ArgumentMatchers.anyLong(), ArgumentMatchers.eq("user"));
        resource.resetSecretVersion(user, "name2", new LongParam("125"));
    }

    @Test
    public void canDelete() {
        Mockito.when(secretController.getSecretById(-559038737)).thenReturn(Optional.of(secret));
        HashSet<Group> groups = new HashSet<>();
        groups.add(new Group(0, "group1", "", SecretsResourceTest.NOW, null, SecretsResourceTest.NOW, null, null));
        groups.add(new Group(0, "group2", "", SecretsResourceTest.NOW, null, SecretsResourceTest.NOW, null, null));
        Mockito.when(aclDAO.getGroupsFor(secret)).thenReturn(groups);
        Response response = resource.deleteSecret(user, new LongParam(Long.toString(-559038737)));
        Mockito.verify(secretDAO).deleteSecretsByName("name");
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test(expected = NotFoundException.class)
    public void deleteErrorsOnNotFound() {
        Mockito.when(secretController.getSecretById(-559038737)).thenReturn(Optional.empty());
        resource.deleteSecret(user, new LongParam(Long.toString(-559038737)));
    }

    @Test(expected = ConflictException.class)
    public void triesToCreateDuplicateSecret() throws Exception {
        SecretController.SecretBuilder secretBuilder = Mockito.mock(SecretBuilder.class);
        Mockito.when(secretController.builder("name", "content", user.getName(), 0)).thenReturn(secretBuilder);
        DataAccessException exception = new DataAccessException("");
        Mockito.doThrow(exception).when(secretBuilder).create();
        CreateSecretRequest req = new CreateSecretRequest("name", "desc", "content", emptyMap, 0);
        resource.createSecret(user, req);
    }

    @Test
    public void includesTheSecret() {
        Mockito.when(secretController.getSecretById(22)).thenReturn(Optional.of(secret));
        Mockito.when(aclDAO.getGroupsFor(secret)).thenReturn(Collections.emptySet());
        Mockito.when(aclDAO.getClientsFor(secret)).thenReturn(Collections.emptySet());
        SecretDetailResponse response = resource.retrieveSecret(user, new LongParam("22"));
        assertThat(response.id).isEqualTo(secret.getId());
        assertThat(response.name).isEqualTo(secret.getName());
        assertThat(response.description).isEqualTo(secret.getDescription());
        assertThat(response.createdAt).isEqualTo(secret.getCreatedAt());
        assertThat(response.createdBy).isEqualTo(secret.getCreatedBy());
        assertThat(response.updatedAt).isEqualTo(secret.getUpdatedAt());
        assertThat(response.updatedBy).isEqualTo(secret.getUpdatedBy());
        assertThat(response.metadata).isEqualTo(secret.getMetadata());
    }

    @Test
    public void handlesNoAssociations() {
        Mockito.when(secretController.getSecretById(22)).thenReturn(Optional.of(secret));
        Mockito.when(aclDAO.getGroupsFor(secret)).thenReturn(Collections.emptySet());
        Mockito.when(aclDAO.getClientsFor(secret)).thenReturn(Collections.emptySet());
        SecretDetailResponse response = resource.retrieveSecret(user, new LongParam("22"));
        assertThat(response.groups).isEmpty();
        assertThat(response.clients).isEmpty();
    }

    @Test
    public void includesAssociations() {
        Client client = new Client(0, "client", null, null, null, null, null, null, null, false, false);
        Group group1 = new Group(0, "group1", null, null, null, null, null, null);
        Group group2 = new Group(0, "group2", null, null, null, null, null, null);
        Mockito.when(secretController.getSecretById(22)).thenReturn(Optional.of(secret));
        Mockito.when(aclDAO.getGroupsFor(secret)).thenReturn(Sets.newHashSet(group1, group2));
        Mockito.when(aclDAO.getClientsFor(secret)).thenReturn(Sets.newHashSet(client));
        SecretDetailResponse response = resource.retrieveSecret(user, new LongParam("22"));
        assertThat(response.groups).containsOnly(group1, group2);
        assertThat(response.clients).containsOnly(client);
    }

    @Test(expected = NotFoundException.class)
    public void badIdNotFound() {
        Mockito.when(secretController.getSecretById(765213)).thenReturn(Optional.empty());
        resource.retrieveSecret(user, new LongParam(Long.toString(765213)));
    }

    @Test
    public void findSecretByNameAndVersion() {
        Mockito.when(secretController.getSecretByName(secret.getName())).thenReturn(Optional.of(secret));
        assertThat(resource.retrieveSecret(user, "name")).isEqualTo(SanitizedSecret.fromSecret(secret));
    }

    @Test(expected = NotFoundException.class)
    public void badNameNotFound() {
        Mockito.when(secretController.getSecretByName("non-existent")).thenReturn(Optional.empty());
        resource.retrieveSecret(user, "non-existent");
    }
}

