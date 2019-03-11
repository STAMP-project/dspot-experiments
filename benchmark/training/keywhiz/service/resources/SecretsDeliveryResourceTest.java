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


import com.google.common.collect.ImmutableSet;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import keywhiz.api.ApiDate;
import keywhiz.api.SecretDeliveryResponse;
import keywhiz.api.model.Client;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.service.daos.AclDAO;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SecretsDeliveryResourceTest {
    private static final ApiDate NOW = ApiDate.now();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    AclDAO aclDAO;

    SecretsDeliveryResource secretsDeliveryResource;

    Secret firstSecret = new Secret(0, "first_secret_name", null, () -> Base64.getEncoder().encodeToString("first_secret_contents".getBytes(StandardCharsets.UTF_8)), "checksum", SecretsDeliveryResourceTest.NOW, null, SecretsDeliveryResourceTest.NOW, null, null, null, null, 0, 1L, SecretsDeliveryResourceTest.NOW, null);

    SanitizedSecret sanitizedFirstSecret = SanitizedSecret.fromSecret(firstSecret);

    Secret secondSecret = new Secret(1, "second_secret_name", null, () -> Base64.getEncoder().encodeToString("second_secret_contents".getBytes(StandardCharsets.UTF_8)), "checksum", SecretsDeliveryResourceTest.NOW, null, SecretsDeliveryResourceTest.NOW, null, null, null, null, 0, 1L, SecretsDeliveryResourceTest.NOW, null);

    SanitizedSecret sanitizedSecondSecret = SanitizedSecret.fromSecret(secondSecret);

    Client client;

    @Test
    public void returnsEmptyJsonArrayWhenUserHasNoSecrets() throws Exception {
        Mockito.when(aclDAO.getSanitizedSecretsFor(client)).thenReturn(ImmutableSet.of());
        List<SecretDeliveryResponse> secrets = secretsDeliveryResource.getSecrets(client);
        assertThat(secrets).isEmpty();
    }

    @Test
    public void returnsJsonArrayWhenUserHasOneSecret() throws Exception {
        Mockito.when(aclDAO.getSanitizedSecretsFor(client)).thenReturn(ImmutableSet.of(sanitizedFirstSecret));
        List<SecretDeliveryResponse> secrets = secretsDeliveryResource.getSecrets(client);
        assertThat(secrets).containsOnly(SecretDeliveryResponse.fromSanitizedSecret(SanitizedSecret.fromSecret(firstSecret)));
    }

    @Test
    public void returnsJsonArrayWhenUserHasMultipleSecrets() throws Exception {
        Mockito.when(aclDAO.getSanitizedSecretsFor(client)).thenReturn(ImmutableSet.of(sanitizedFirstSecret, sanitizedSecondSecret));
        List<SecretDeliveryResponse> secrets = secretsDeliveryResource.getSecrets(client);
        assertThat(secrets).containsOnly(SecretDeliveryResponse.fromSanitizedSecret(SanitizedSecret.fromSecret(firstSecret)), SecretDeliveryResponse.fromSanitizedSecret(SanitizedSecret.fromSecret(secondSecret)));
    }
}

