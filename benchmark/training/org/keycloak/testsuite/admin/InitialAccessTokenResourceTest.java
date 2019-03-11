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
package org.keycloak.testsuite.admin;


import OperationType.CREATE;
import OperationType.DELETE;
import ResourceType.CLIENT_INITIAL_ACCESS_MODEL;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientInitialAccessResource;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.common.util.Time;
import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientInitialAccessPresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.AdminEventPaths;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class InitialAccessTokenResourceTest extends AbstractAdminTest {
    private ClientInitialAccessResource resource;

    @Test
    public void testInitialAccessTokens() {
        ClientInitialAccessCreatePresentation rep = new ClientInitialAccessCreatePresentation();
        rep.setCount(2);
        rep.setExpiration(100);
        int time = Time.currentTime();
        ClientInitialAccessPresentation response = resource.create(rep);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientInitialAccessPath(response.getId()), rep, CLIENT_INITIAL_ACCESS_MODEL);
        Assert.assertNotNull(response.getId());
        Assert.assertEquals(new Integer(2), response.getCount());
        Assert.assertEquals(new Integer(2), response.getRemainingCount());
        Assert.assertEquals(new Integer(100), response.getExpiration());
        Assert.assertTrue(((time <= (response.getTimestamp())) && ((response.getTimestamp()) <= (Time.currentTime()))));
        Assert.assertNotNull(response.getToken());
        rep.setCount(3);
        response = resource.create(rep);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientInitialAccessPath(response.getId()), rep, CLIENT_INITIAL_ACCESS_MODEL);
        rep.setCount(4);
        response = resource.create(rep);
        String lastId = response.getId();
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientInitialAccessPath(lastId), rep, CLIENT_INITIAL_ACCESS_MODEL);
        List<ClientInitialAccessPresentation> list = resource.list();
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(9, (((list.get(0).getCount()) + (list.get(1).getCount())) + (list.get(2).getCount())));
        Assert.assertNull(list.get(0).getToken());
        // Delete last and assert it was deleted
        resource.delete(lastId);
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientInitialAccessPath(lastId), CLIENT_INITIAL_ACCESS_MODEL);
        list = resource.list();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(5, ((list.get(0).getCount()) + (list.get(1).getCount())));
    }

    @Test
    public void testPeriodicExpiration() throws InterruptedException, ClientRegistrationException {
        ClientInitialAccessPresentation response1 = resource.create(new ClientInitialAccessCreatePresentation(1, 1));
        ClientInitialAccessPresentation response2 = resource.create(new ClientInitialAccessCreatePresentation(1000, 1));
        ClientInitialAccessPresentation response3 = resource.create(new ClientInitialAccessCreatePresentation(1000, 0));
        ClientInitialAccessPresentation response4 = resource.create(new ClientInitialAccessCreatePresentation(0, 1));
        List<ClientInitialAccessPresentation> list = resource.list();
        Assert.assertEquals(4, list.size());
        setTimeOffset(10);
        testingClient.testing().removeExpired(AbstractAdminTest.REALM_NAME);
        list = resource.list();
        Assert.assertEquals(2, list.size());
        List<String> remainingIds = list.stream().map(( initialAccessPresentation) -> initialAccessPresentation.getId()).collect(Collectors.toList());
        assertNames(remainingIds, response2.getId(), response4.getId());
        setTimeOffset(2000);
        testingClient.testing().removeExpired(AbstractAdminTest.REALM_NAME);
        list = resource.list();
        Assert.assertEquals(1, list.size());
        org.keycloak.testsuite.Assert.assertEquals(list.get(0).getId(), response4.getId());
        // Cleanup
        realm.clientInitialAccess().delete(response4.getId());
    }
}

