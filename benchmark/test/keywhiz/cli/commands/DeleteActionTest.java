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
package keywhiz.cli.commands;


import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import keywhiz.api.ApiDate;
import keywhiz.api.model.Client;
import keywhiz.api.model.Group;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.cli.configs.DeleteActionConfig;
import keywhiz.client.KeywhizClient;
import keywhiz.client.KeywhizClient.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class DeleteActionTest {
    private static final ApiDate NOW = ApiDate.now();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    KeywhizClient keywhizClient;

    DeleteActionConfig deleteActionConfig;

    DeleteAction deleteAction;

    Secret secret = new Secret(0, "secret", null, () -> "c2VjcmV0MQ==", "checksum", DeleteActionTest.NOW, null, DeleteActionTest.NOW, null, null, null, ImmutableMap.of(), 0, 1L, DeleteActionTest.NOW, null);

    SanitizedSecret sanitizedSecret = SanitizedSecret.fromSecret(secret);

    ByteArrayInputStream yes;

    ByteArrayInputStream no;

    @Test
    public void deleteCallsDeleteForGroup() throws Exception {
        deleteAction.inputStream = yes;
        deleteActionConfig.deleteType = Arrays.asList("group");
        deleteActionConfig.name = "Web";
        Group group = new Group(0, deleteActionConfig.name, null, null, null, null, null, null);
        Mockito.when(keywhizClient.getGroupByName(deleteActionConfig.name)).thenReturn(group);
        deleteAction.run();
        Mockito.verify(keywhizClient).deleteGroupWithId(group.getId());
    }

    @Test
    public void deleteCallsDeleteForClient() throws Exception {
        deleteAction.inputStream = yes;
        deleteActionConfig.deleteType = Arrays.asList("client");
        deleteActionConfig.name = "newClient";
        Client client = new Client(657, "newClient", null, DeleteActionTest.NOW, null, DeleteActionTest.NOW, null, null, null, true, false);
        Mockito.when(keywhizClient.getClientByName(deleteActionConfig.name)).thenReturn(client);
        deleteAction.run();
        Mockito.verify(keywhizClient).deleteClientWithId(client.getId());
    }

    @Test
    public void deleteCallsDeleteForSecret() throws Exception {
        deleteAction.inputStream = yes;
        deleteActionConfig.deleteType = Arrays.asList("secret");
        deleteActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenReturn(sanitizedSecret);
        deleteAction.run();
        Mockito.verify(keywhizClient).deleteSecretWithId(sanitizedSecret.id());
    }

    @Test
    public void deleteSkipsWithoutConfirmation() throws Exception {
        deleteAction.inputStream = no;
        deleteActionConfig.deleteType = Arrays.asList("secret");
        deleteActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenReturn(sanitizedSecret);
        deleteAction.run();
        Mockito.verify(keywhizClient, Mockito.never()).deleteSecretWithId(ArgumentMatchers.anyInt());
    }

    @Test(expected = AssertionError.class)
    public void deleteThrowsIfDeleteGroupFails() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("group");
        deleteActionConfig.name = "Web";
        Mockito.when(keywhizClient.getGroupByName(deleteActionConfig.name)).thenThrow(new NotFoundException());
        deleteAction.run();
    }

    @Test(expected = AssertionError.class)
    public void deleteThrowsIfDeleteClientFails() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("client");
        deleteActionConfig.name = "nonexistent-client-name";
        Mockito.when(keywhizClient.getClientByName(deleteActionConfig.name)).thenThrow(new NotFoundException());
        deleteAction.run();
    }

    @Test(expected = AssertionError.class)
    public void deleteThrowsIfDeleteSecretFails() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("secret");
        deleteActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteThrowsIfNoTypeSpecified() throws Exception {
        deleteActionConfig.deleteType = null;
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteThrowsIfTooManyArguments() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("group", "secret");
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteThrowsIfInvalidType() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("invalid_type");
        deleteActionConfig.name = "any-name";
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteValidatesGroupName() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("group");
        deleteActionConfig.name = "Invalid Name";
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteValidatesClientName() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("client");
        deleteActionConfig.name = "Invalid Name";
        deleteAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteValidatesSecretName() throws Exception {
        deleteActionConfig.deleteType = Arrays.asList("secret");
        deleteActionConfig.name = "Invalid Name";
        deleteAction.run();
    }
}

