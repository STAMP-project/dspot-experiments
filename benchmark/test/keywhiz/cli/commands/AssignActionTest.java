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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import keywhiz.api.ApiDate;
import keywhiz.api.GroupDetailResponse;
import keywhiz.api.model.Client;
import keywhiz.api.model.Group;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.cli.configs.AssignActionConfig;
import keywhiz.client.KeywhizClient;
import keywhiz.client.KeywhizClient.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AssignActionTest {
    private static final ApiDate NOW = ApiDate.now();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    KeywhizClient keywhizClient;

    AssignActionConfig assignActionConfig;

    AssignAction assignAction;

    Group group = new Group(5, "group", null, null, null, null, null, null);

    GroupDetailResponse groupDetailResponse = GroupDetailResponse.fromGroup(group, ImmutableList.<SanitizedSecret>of(), ImmutableList.<Client>of());

    Secret secret = new Secret(16, "secret", null, () -> "c2VjcmV0MQ==", "checksum", AssignActionTest.NOW, null, AssignActionTest.NOW, null, null, null, ImmutableMap.of(), 0, 1L, AssignActionTest.NOW, null);

    SanitizedSecret sanitizedSecret = SanitizedSecret.fromSecret(secret);

    @Test
    public void assignClientAddsClientsThatDoNotExist() throws Exception {
        assignActionConfig.assignType = Arrays.asList("client");
        assignActionConfig.name = "non-existent-client-name";
        assignActionConfig.group = group.getName();
        Client client = new Client(543, assignActionConfig.name, null, null, null, null, null, null, null, false, false);
        // Group exists
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenReturn(group);
        // Client does not exist, but 2nd call returns the created client.
        Mockito.when(keywhizClient.getClientByName(client.getName())).thenThrow(new NotFoundException()).thenReturn(client);
        // Client not assigned to group
        Mockito.when(keywhizClient.groupDetailsForId(group.getId())).thenReturn(groupDetailResponse);
        assignAction.run();
        Mockito.verify(keywhizClient).createClient(assignActionConfig.name);
        Mockito.verify(keywhizClient).enrollClientInGroupByIds(client.getId(), group.getId());
    }

    @Test
    public void assignCallsAssignForClient() throws Exception {
        assignActionConfig.assignType = Arrays.asList("client");
        assignActionConfig.name = "existing-client-name";
        assignActionConfig.group = group.getName();
        Client client = new Client(5673, assignActionConfig.name, null, null, null, null, null, null, null, false, true);
        // Group exists
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenReturn(group);
        // Client exists
        Mockito.when(keywhizClient.getClientByName(assignActionConfig.name)).thenReturn(client);
        // Client not assigned to group
        Mockito.when(keywhizClient.groupDetailsForId(group.getId())).thenReturn(groupDetailResponse);
        assignAction.run();
        Mockito.verify(keywhizClient, Mockito.never()).createClient(ArgumentMatchers.anyString());
        Mockito.verify(keywhizClient).enrollClientInGroupByIds(client.getId(), group.getId());
    }

    @Test
    public void assignCallsAssignForSecret() throws Exception {
        assignActionConfig.assignType = Arrays.asList("secret");
        assignActionConfig.name = secret.getDisplayName();
        assignActionConfig.group = group.getName();
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenReturn(group);
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenReturn(sanitizedSecret);
        Mockito.when(keywhizClient.groupDetailsForId(group.getId())).thenReturn(groupDetailResponse);
        assignAction.run();
        Mockito.verify(keywhizClient).grantSecretToGroupByIds(secret.getId(), group.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignThrowsIfNoTypeSpecified() throws Exception {
        assignActionConfig.assignType = null;
        assignAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignThrowsIfInvalidType() throws Exception {
        assignActionConfig.assignType = Arrays.asList("invalid_type");
        assignActionConfig.name = group.getName();
        assignActionConfig.group = group.getName();
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenReturn(group);
        assignAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignValidatesGroupName() throws Exception {
        assignActionConfig.assignType = Arrays.asList("secret");
        assignActionConfig.name = "General_Password";
        assignActionConfig.group = "Invalid Name";
        assignAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void assignValidatesSecretName() throws Exception {
        assignActionConfig.assignType = Arrays.asList("secret");
        assignActionConfig.name = "Invalid Name";
        assignActionConfig.group = "Web";
        assignAction.run();
    }
}

