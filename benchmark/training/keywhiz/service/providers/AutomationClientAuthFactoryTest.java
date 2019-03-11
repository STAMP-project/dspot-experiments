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
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.SecurityContext;
import keywhiz.api.model.AutomationClient;
import keywhiz.api.model.Client;
import keywhiz.auth.mutualssl.SimplePrincipal;
import keywhiz.service.daos.ClientDAO;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AutomationClientAuthFactoryTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private static final Principal principal = SimplePrincipal.of("CN=principal,OU=blah");

    private static final Client client = new Client(0, "principal", null, null, null, null, null, null, null, true, true);

    private static final AutomationClient automationClient = AutomationClient.of(AutomationClientAuthFactoryTest.client);

    @Mock
    ContainerRequest request;

    @Mock
    SecurityContext securityContext;

    @Mock
    ClientDAO clientDAO;

    AutomationClientAuthFactory factory;

    @Test
    public void automationClientWhenClientPresent() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(AutomationClientAuthFactoryTest.principal);
        assertThat(factory.provide(request)).isEqualTo(AutomationClientAuthFactoryTest.automationClient);
    }

    @Test(expected = ForbiddenException.class)
    public void automationClientRejectsClientsWithoutAutomation() {
        Client clientWithoutAutomation = new Client(3423, "clientWithoutAutomation", null, null, null, null, null, null, null, true, false);
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(SimplePrincipal.of("CN=clientWithoutAutomation"));
        Mockito.when(clientDAO.getClient("clientWithoutAutomation")).thenReturn(Optional.of(clientWithoutAutomation));
        factory.provide(request);
    }

    @Test(expected = ForbiddenException.class)
    public void automationClientRejectsClientsWithoutDBEntries() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(SimplePrincipal.of("CN=clientWithoutDBEntry"));
        Mockito.when(clientDAO.getClient("clientWithoutDBEntry")).thenReturn(Optional.empty());
        factory.provide(request);
    }

    @Test(expected = NotAuthorizedException.class)
    public void automationClientWhenPrincipalAbsent() {
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(null);
        factory.provide(request);
    }
}

