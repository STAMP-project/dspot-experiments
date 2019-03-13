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


import SecretController.SecretBuilder;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import keywhiz.api.ApiDate;
import keywhiz.api.AutomationSecretResponse;
import keywhiz.api.CreateSecretRequest;
import keywhiz.api.model.AutomationClient;
import keywhiz.api.model.Group;
import keywhiz.api.model.Secret;
import keywhiz.log.AuditLog;
import keywhiz.log.SimpleLogger;
import keywhiz.service.daos.AclDAO;
import keywhiz.service.daos.SecretController;
import keywhiz.service.daos.SecretDAO;
import keywhiz.service.exceptions.ConflictException;
import org.jooq.exception.DataAccessException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AutomationSecretResourceTest {
    private static final ApiDate NOW = ApiDate.now();

    AutomationSecretResource resource;

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    SecretController secretController;

    @Mock
    SecretBuilder secretBuilder;

    @Mock
    AclDAO aclDAO;

    @Mock
    SecretDAO secretDAO;

    AutomationClient automation = AutomationClient.of(new keywhiz.api.model.Client(1, "automation", "Automation client", AutomationSecretResourceTest.NOW, "test", AutomationSecretResourceTest.NOW, "test", null, null, true, true));

    AuditLog auditLog = new SimpleLogger();

    @Test
    public void addSecret() {
        CreateSecretRequest request = new CreateSecretRequest("mySecret", "some secret", "ponies", null, 0);
        Secret secret = /* Set by DB */
        /* updatedAt set by DB */
        new Secret(0, request.name, request.description, () -> Base64.getUrlEncoder().encodeToString(request.content.getBytes(StandardCharsets.UTF_8)), "checksum", AutomationSecretResourceTest.NOW, automation.getName(), AutomationSecretResourceTest.NOW, automation.getName(), request.metadata, null, null, 0, 1L, AutomationSecretResourceTest.NOW, automation.getName());
        Mockito.when(secretBuilder.create()).thenReturn(secret);
        Mockito.when(secretController.getSecretByName(ArgumentMatchers.eq(request.name))).thenReturn(Optional.of(secret));
        AutomationSecretResponse response = resource.createSecret(automation, request);
        assertThat(response.id()).isEqualTo(secret.getId());
        assertThat(response.secret()).isEqualTo(secret.getSecret());
        assertThat(response.name()).isEqualTo(secret.getDisplayName());
        assertThat(response.metadata()).isEqualTo(secret.getMetadata());
    }

    @Test
    public void deleteSecret() throws Exception {
        Secret secret = new Secret(0, "mySecret", null, ((Secret.LazyString) (() -> "meh")), "checksum", AutomationSecretResourceTest.NOW, null, AutomationSecretResourceTest.NOW, null, ImmutableMap.of(), null, null, 0, 1L, AutomationSecretResourceTest.NOW, null);
        HashSet<Group> groups = new HashSet<>();
        groups.add(new Group(0, "group1", "", AutomationSecretResourceTest.NOW, null, AutomationSecretResourceTest.NOW, null, null));
        groups.add(new Group(0, "group2", "", AutomationSecretResourceTest.NOW, null, AutomationSecretResourceTest.NOW, null, null));
        Mockito.when(secretController.getSecretByName(secret.getName())).thenReturn(Optional.of(secret));
        Mockito.when(aclDAO.getGroupsFor(secret)).thenReturn(groups);
        resource.deleteSecretSeries(automation, secret.getName());
        Mockito.verify(secretDAO).deleteSecretsByName(secret.getName());
    }

    @Test(expected = ConflictException.class)
    public void triesToCreateDuplicateSecret() throws Exception {
        DataAccessException exception = new DataAccessException("");
        ImmutableMap<String, String> emptyMap = ImmutableMap.of();
        Mockito.doThrow(exception).when(secretBuilder).create();
        CreateSecretRequest req = new CreateSecretRequest("name", "desc", "content", emptyMap, 0);
        resource.createSecret(automation, req);
    }
}

