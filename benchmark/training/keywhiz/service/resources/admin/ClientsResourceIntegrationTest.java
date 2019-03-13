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


import DbSeedCommand.defaultPassword;
import DbSeedCommand.defaultUser;
import KeywhizClient.ConflictException;
import KeywhizClient.NotFoundException;
import KeywhizClient.UnauthorizedException;
import com.google.common.primitives.Ints;
import java.io.IOException;
import keywhiz.IntegrationTestRule;
import keywhiz.api.ClientDetailResponse;
import keywhiz.api.model.Client;
import keywhiz.client.KeywhizClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class ClientsResourceIntegrationTest {
    KeywhizClient keywhizClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void listsClients() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(ClientsResourceIntegrationTest.clientsToNames(keywhizClient.allClients())).contains("CN=User1", "CN=User2", "CN=User3", "CN=User4");
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsNonKeywhizUsers() throws IOException {
        keywhizClient.login("username", "password".toCharArray());
        keywhizClient.allClients();
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsClientCerts() throws IOException {
        keywhizClient.allClients();
    }

    @Test
    public void retrievesClientInfoById() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        ClientDetailResponse client = keywhizClient.clientDetailsForId(768);
        assertThat(client.name).isEqualTo("client");
    }

    @Test
    public void retrievesClientInfoByName() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        Client client = keywhizClient.getClientByName("client");
        assertThat(client.getId()).isEqualTo(768);
    }

    @Test
    public void createsClient() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(ClientsResourceIntegrationTest.clientsToNames(keywhizClient.allClients())).doesNotContain("kingpin");
        ClientDetailResponse clientDetails = keywhizClient.createClient("kingpin");
        assertThat(clientDetails.name).isEqualTo("kingpin");
        assertThat(ClientsResourceIntegrationTest.clientsToNames(keywhizClient.allClients())).contains("kingpin");
    }

    @Test(expected = ConflictException.class)
    public void createDuplicateClients() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.createClient("varys");
        keywhizClient.createClient("varys");
    }

    @Test(expected = NotFoundException.class)
    public void notFoundOnMissingId() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.clientDetailsForId(900000);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundOnMissingName() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.getClientByName("non-existent-client");
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsWithoutCookie() throws IOException {
        keywhizClient.clientDetailsForId(768);
    }

    @Test
    public void deletesClients() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        int clientId = Ints.checkedCast(keywhizClient.createClient("deletesClientTest").id);
        keywhizClient.deleteClientWithId(clientId);
        try {
            keywhizClient.clientDetailsForId(clientId);
            failBecauseExceptionWasNotThrown(NotFoundException.class);
        } catch (KeywhizClient e) {
            // Client not found, since it was deleted
        }
    }
}

