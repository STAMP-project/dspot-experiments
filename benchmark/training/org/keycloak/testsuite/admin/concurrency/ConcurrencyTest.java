/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.admin.concurrency;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ConcurrencyTest extends AbstractConcurrencyTest {
    @Test
    public void testAllConcurrently() throws Throwable {
        AtomicInteger uniqueCounter = new AtomicInteger(100000);
        concurrentTest(new ConcurrencyTest.CreateClient(uniqueCounter), new ConcurrencyTest.CreateRemoveClient(uniqueCounter), new ConcurrencyTest.CreateGroup(uniqueCounter), new ConcurrencyTest.CreateRole(uniqueCounter));
    }

    @Test
    public void createClient() throws Throwable {
        AtomicInteger uniqueCounter = new AtomicInteger();
        concurrentTest(new ConcurrencyTest.CreateClient(uniqueCounter));
    }

    @Test
    public void createGroup() throws Throwable {
        AtomicInteger uniqueCounter = new AtomicInteger();
        concurrentTest(new ConcurrencyTest.CreateGroup(uniqueCounter));
    }

    @Test
    public void createRemoveClient() throws Throwable {
        // FYI< this will fail as HSQL seems to be trying to perform table locks.
        AtomicInteger uniqueCounter = new AtomicInteger();
        concurrentTest(new ConcurrencyTest.CreateRemoveClient(uniqueCounter));
    }

    @Test
    public void createClientRole() throws Throwable {
        ClientRepresentation c = new ClientRepresentation();
        c.setClientId("client");
        Response response = adminClient.realm(AbstractConcurrencyTest.REALM_NAME).clients().create(c);
        final String clientId = ApiUtil.getCreatedId(response);
        response.close();
        AtomicInteger uniqueCounter = new AtomicInteger();
        concurrentTest(new ConcurrencyTest.CreateClientRole(uniqueCounter, clientId));
    }

    @Test
    public void createRole() throws Throwable {
        AtomicInteger uniqueCounter = new AtomicInteger();
        run(new ConcurrencyTest.CreateRole(uniqueCounter));
    }

    private class CreateClient implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger clientIndex;

        public CreateClient(AtomicInteger clientIndex) {
            this.clientIndex = clientIndex;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            String name = "c-" + (clientIndex.getAndIncrement());
            ClientRepresentation c = new ClientRepresentation();
            c.setClientId(name);
            Response response = realm.clients().create(c);
            String id = ApiUtil.getCreatedId(response);
            response.close();
            c = realm.clients().get(id).toRepresentation();
            Assert.assertNotNull(c);
            Assert.assertTrue((("Client " + name) + " not found in client list"), realm.clients().findAll().stream().map(ClientRepresentation::getClientId).filter(Objects::nonNull).anyMatch(name::equals));
        }
    }

    private class CreateRemoveClient implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger clientIndex;

        public CreateRemoveClient(AtomicInteger clientIndex) {
            this.clientIndex = clientIndex;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            String name = "c-" + (clientIndex.getAndIncrement());
            ClientRepresentation c = new ClientRepresentation();
            c.setClientId(name);
            final ClientsResource clients = realm.clients();
            Response response = clients.create(c);
            String id = ApiUtil.getCreatedId(response);
            response.close();
            final ClientResource client = clients.get(id);
            c = client.toRepresentation();
            Assert.assertNotNull(c);
            Assert.assertTrue((("Client " + name) + " not found in client list"), clients.findAll().stream().map(ClientRepresentation::getClientId).filter(Objects::nonNull).anyMatch(name::equals));
            client.remove();
            try {
                client.toRepresentation();
                Assert.fail((("Client " + name) + " should not be found.  Should throw a 404"));
            } catch (NotFoundException e) {
            }
            Assert.assertFalse((("Client " + name) + " should now not present in client list"), clients.findAll().stream().map(ClientRepresentation::getClientId).filter(Objects::nonNull).anyMatch(name::equals));
        }
    }

    private class CreateGroup implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger uniqueIndex;

        public CreateGroup(AtomicInteger uniqueIndex) {
            this.uniqueIndex = uniqueIndex;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            String name = "g-" + (uniqueIndex.getAndIncrement());
            GroupRepresentation c = new GroupRepresentation();
            c.setName(name);
            Response response = realm.groups().add(c);
            String id = ApiUtil.getCreatedId(response);
            response.close();
            c = realm.groups().group(id).toRepresentation();
            Assert.assertNotNull(c);
            Assert.assertTrue((("Group " + name) + " not found in group list"), realm.groups().groups().stream().map(GroupRepresentation::getName).filter(Objects::nonNull).anyMatch(name::equals));
        }
    }

    private class CreateClientRole implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger uniqueCounter;

        private final String clientId;

        public CreateClientRole(AtomicInteger uniqueCounter, String clientId) {
            this.uniqueCounter = uniqueCounter;
            this.clientId = clientId;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            String name = "cr-" + (uniqueCounter.getAndIncrement());
            RoleRepresentation r = new RoleRepresentation(name, null, false);
            final RolesResource roles = realm.clients().get(clientId).roles();
            roles.create(r);
            Assert.assertNotNull(roles.get(name).toRepresentation());
        }
    }

    private class CreateRole implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger uniqueCounter;

        public CreateRole(AtomicInteger uniqueCounter) {
            this.uniqueCounter = uniqueCounter;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            String name = "r-" + (uniqueCounter.getAndIncrement());
            RoleRepresentation r = new RoleRepresentation(name, null, false);
            final RolesResource roles = realm.roles();
            roles.create(r);
            Assert.assertNotNull(roles.get(name).toRepresentation());
        }
    }
}

