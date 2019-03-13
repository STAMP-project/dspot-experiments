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
import java.util.Base64;
import keywhiz.api.ApiDate;
import keywhiz.api.SecretDetailResponse;
import keywhiz.api.model.Client;
import keywhiz.api.model.Group;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.cli.configs.AddActionConfig;
import keywhiz.client.KeywhizClient;
import keywhiz.client.KeywhizClient.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AddActionTest {
    private static final ApiDate NOW = ApiDate.now();

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    KeywhizClient keywhizClient;

    AddActionConfig addActionConfig;

    AddAction addAction;

    Client client = new Client(4, "newClient", null, null, null, null, null, null, null, true, false);

    Group group = new Group(4, "newGroup", null, null, null, null, null, null);

    Secret secret = new Secret(15, "newSecret", null, () -> "c2VjcmV0MQ==", "checksum", AddActionTest.NOW, null, AddActionTest.NOW, null, null, null, ImmutableMap.of(), 0, 1L, AddActionTest.NOW, null);

    SanitizedSecret sanitizedSecret = SanitizedSecret.fromSecret(secret);

    SecretDetailResponse secretDetailResponse = SecretDetailResponse.fromSecret(secret, null, null);

    @Test
    public void addCallsAddForGroup() throws Exception {
        addActionConfig.addType = Arrays.asList("group");
        addActionConfig.name = group.getName();
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenThrow(new NotFoundException());
        addAction.run();
        Mockito.verify(keywhizClient).createGroup(addActionConfig.name, "", ImmutableMap.of());
    }

    @Test
    public void addCallsAddForSecret() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getDisplayName();
        addActionConfig.expiry = "2006-01-02T15:04:05Z";
        byte[] content = AddActionTest.base64Decoder.decode(secret.getSecret());
        addAction.stream = new ByteArrayInputStream(content);
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());// Call checks for existence.

        Mockito.when(keywhizClient.createSecret(secret.getName(), "", content, secret.getMetadata(), 1136214245)).thenReturn(secretDetailResponse);
        addAction.run();
        Mockito.verify(keywhizClient, Mockito.times(1)).createSecret(secret.getName(), "", content, secret.getMetadata(), 1136214245);
    }

    @Test
    public void addCallsAddForClient() throws Exception {
        addActionConfig.addType = Arrays.asList("client");
        addActionConfig.name = client.getName();
        Mockito.when(keywhizClient.getClientByName(client.getName())).thenThrow(new NotFoundException());
        addAction.run();
        Mockito.verify(keywhizClient).createClient(addActionConfig.name);
    }

    @Test
    public void addSecretCanAssignGroup() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getDisplayName();
        addActionConfig.group = group.getName();
        byte[] content = AddActionTest.base64Decoder.decode(secret.getSecret());
        addAction.stream = new ByteArrayInputStream(content);
        Mockito.when(keywhizClient.getGroupByName(group.getName())).thenReturn(group);
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());// Call checks for existence.

        Mockito.when(keywhizClient.createSecret(secret.getName(), "", content, secret.getMetadata(), 0)).thenReturn(secretDetailResponse);
        addAction.run();
        Mockito.verify(keywhizClient).grantSecretToGroupByIds(secret.getId(), group.getId());
    }

    @Test
    public void addCreatesWithoutVersionByDefault() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getName();// Name without version

        byte[] content = AddActionTest.base64Decoder.decode(secret.getSecret());
        addAction.stream = new ByteArrayInputStream(content);
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());// Call checks for existence.

        Mockito.when(keywhizClient.createSecret(secret.getName(), "", content, secret.getMetadata(), 0)).thenReturn(secretDetailResponse);
        addAction.run();
        Mockito.verify(keywhizClient, Mockito.times(1)).createSecret(secret.getName(), "", content, secret.getMetadata(), 0);
    }

    @Test
    public void addWithMetadata() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getDisplayName();
        addActionConfig.json = "{\"owner\":\"example-name\", \"group\":\"example-group\"}";
        byte[] content = AddActionTest.base64Decoder.decode(secret.getSecret());
        addAction.stream = new ByteArrayInputStream(content);
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());// Call checks for existence.

        ImmutableMap<String, String> expected = ImmutableMap.of("owner", "example-name", "group", "example-group");
        Mockito.when(keywhizClient.createSecret(secret.getName(), "", content, expected, 0)).thenReturn(secretDetailResponse);
        addAction.run();
        Mockito.verify(keywhizClient, Mockito.times(1)).createSecret(secret.getName(), "", content, expected, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsIfMetadataHasBadKeys() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getDisplayName();
        addActionConfig.json = "{\"ThisIsABadKey\":\"doh\"}";
        addAction.stream = new ByteArrayInputStream(AddActionTest.base64Decoder.decode(secret.getSecret()));
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenThrow(new NotFoundException());// Call checks for existence.

        addAction.run();
    }

    @Test(expected = AssertionError.class)
    public void addThrowsIfAddGroupFails() throws Exception {
        addActionConfig.addType = Arrays.asList("group");
        addActionConfig.name = group.getName();
        Mockito.when(keywhizClient.getGroupByName(addActionConfig.name)).thenReturn(group);
        addAction.run();
    }

    @Test(expected = AssertionError.class)
    public void addThrowsIfAddSecretFails() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getName())).thenReturn(sanitizedSecret);
        addAction.run();
    }

    @Test(expected = AssertionError.class)
    public void addThrowsIfAddClientFails() throws Exception {
        addActionConfig.addType = Arrays.asList("client");
        addActionConfig.name = client.getName();
        Mockito.when(keywhizClient.getClientByName(addActionConfig.name)).thenReturn(client);
        addAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsIfNoTypeSpecified() throws Exception {
        addActionConfig.addType = null;
        addAction.run();
    }

    @Test(expected = AssertionError.class)
    public void addThrowsIfInvalidType() throws Exception {
        addActionConfig.addType = Arrays.asList("invalid_type");
        addActionConfig.name = "any-name";
        addAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addValidatesGroupName() throws Exception {
        addActionConfig.addType = Arrays.asList("group");
        addActionConfig.name = "Invalid Name";
        addAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addValidatesClientName() throws Exception {
        addActionConfig.addType = Arrays.asList("client");
        addActionConfig.name = "Invalid Name";
        addAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addValidatesSecretName() throws Exception {
        addActionConfig.addType = Arrays.asList("secret");
        addActionConfig.name = "Invalid Name";
        addAction.run();
    }
}

