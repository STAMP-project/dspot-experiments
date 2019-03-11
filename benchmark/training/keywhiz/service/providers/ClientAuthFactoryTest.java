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
package keywhiz.service.providers;


import java.security.Principal;
import java.util.Optional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.SecurityContext;
import keywhiz.api.ApiDate;
import keywhiz.api.model.Client;
import keywhiz.auth.mutualssl.SimplePrincipal;
import keywhiz.service.daos.ClientDAO;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ClientAuthFactoryTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private static final Principal principal = SimplePrincipal.of("CN=principal,OU=organizational-unit");

    private static final Client client = new Client(0, "principal", null, null, null, null, null, null, null, true, false);

    @Mock
    ContainerRequest request;

    @Mock
    SecurityContext securityContext;

    @Mock
    ClientDAO clientDAO;

    ClientAuthFactory factory;

    @Test
    public void clientWhenClientPresent() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(ClientAuthFactoryTest.principal);
        assertThat(factory.provide(request)).isEqualTo(ClientAuthFactoryTest.client);
    }

    @Test(expected = NotAuthorizedException.class)
    public void clientWhenPrincipalAbsentThrows() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(null);
        factory.provide(request);
    }

    @Test(expected = NotAuthorizedException.class)
    public void rejectsDisabledClients() {
        Client disabledClient = /* disabled */
        new Client(1, "disabled", null, null, null, null, null, null, null, false, false);
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(SimplePrincipal.of("CN=disabled"));
        Mockito.when(clientDAO.getClient("disabled")).thenReturn(Optional.of(disabledClient));
        factory.provide(request);
    }

    @Test
    public void createsDbRecordForNewClient() throws Exception {
        ApiDate now = ApiDate.now();
        Client newClient = new Client(2345L, "new-client", "desc", now, "automatic", now, "automatic", null, null, true, false);
        // lookup doesn't find client
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(SimplePrincipal.of("CN=new-client"));
        Mockito.when(clientDAO.getClient("new-client")).thenReturn(Optional.empty());
        // a new DB record is created
        Mockito.when(clientDAO.createClient(ArgumentMatchers.eq("new-client"), ArgumentMatchers.eq("automatic"), ArgumentMatchers.any())).thenReturn(2345L);
        Mockito.when(clientDAO.getClientById(2345L)).thenReturn(Optional.of(newClient));
        assertThat(factory.provide(request)).isEqualTo(newClient);
    }

    @Test
    public void updatesClientLastSeen() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(ClientAuthFactoryTest.principal);
        factory.provide(request);
        Mockito.verify(clientDAO, Mockito.times(1)).sawClient(ArgumentMatchers.any(), ArgumentMatchers.eq(ClientAuthFactoryTest.principal));
    }
}

