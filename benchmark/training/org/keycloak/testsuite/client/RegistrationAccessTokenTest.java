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
package org.keycloak.testsuite.client;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.ClientRepresentation;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RegistrationAccessTokenTest extends AbstractClientRegistrationTest {
    private ClientRepresentation client;

    @Test
    public void getClientWithRegistrationToken() throws ClientRegistrationException {
        setTimeOffset(10);
        ClientRepresentation rep = reg.get(client.getClientId());
        Assert.assertNotNull(rep);
        Assert.assertEquals(client.getRegistrationAccessToken(), rep.getRegistrationAccessToken());
        Assert.assertNotNull(rep.getRegistrationAccessToken());
        // KEYCLOAK-4984 check registration access token is not updated
        assertRead(client.getClientId(), client.getRegistrationAccessToken(), true);
    }

    @Test
    public void getClientWrongClient() throws ClientRegistrationException {
        try {
            reg.get("SomeOtherClient");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getClientMissingClient() throws ClientRegistrationException {
        try {
            reg.get("nosuch");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getClientWithBadRegistrationToken() throws ClientRegistrationException {
        String oldToken = client.getRegistrationAccessToken();
        reg.update(client);
        reg.auth(Auth.token(oldToken));
        try {
            reg.get(client.getClientId());
            Assert.fail("Expected 401");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateClientWithRegistrationToken() throws ClientRegistrationException {
        client.setRootUrl("http://newroot");
        ClientRepresentation rep = reg.update(client);
        Assert.assertEquals("http://newroot", getClient(client.getId()).getRootUrl());
        Assert.assertNotEquals(client.getRegistrationAccessToken(), rep.getRegistrationAccessToken());
        // check registration access token is updated
        assertRead(client.getClientId(), client.getRegistrationAccessToken(), false);
        assertRead(client.getClientId(), rep.getRegistrationAccessToken(), true);
    }

    @Test
    public void updateClientWithBadRegistrationToken() throws ClientRegistrationException {
        String oldToken = client.getRegistrationAccessToken();
        reg.update(client);
        reg.auth(Auth.token(oldToken));
        try {
            reg.update(client);
            Assert.fail("Expected 401");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
        Assert.assertEquals("http://root", getClient(client.getId()).getRootUrl());
    }

    @Test
    public void deleteClientWithRegistrationToken() throws ClientRegistrationException {
        reg.delete(client);
        Assert.assertNull(getClient(client.getId()));
    }

    @Test
    public void deleteClientWithBadRegistrationToken() throws ClientRegistrationException {
        String oldToken = client.getRegistrationAccessToken();
        reg.update(client);
        reg.auth(Auth.token(oldToken));
        try {
            reg.delete(client);
            Assert.fail("Expected 401");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
        Assert.assertNotNull(getClient(client.getId()));
    }
}

