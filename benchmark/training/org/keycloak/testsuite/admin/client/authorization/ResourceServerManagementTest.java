/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.admin.client.authorization;


import PolicyEnforcementMode.PERMISSIVE;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourceServerManagementTest extends AbstractAuthorizationTest {
    @Test
    public void testCreateAndDeleteResourceServer() throws Exception {
        ClientsResource clientsResource = testRealmResource().clients();
        clientsResource.create(JsonSerialization.readValue(getClass().getResourceAsStream("/authorization-test/client-with-authz-settings.json"), ClientRepresentation.class)).close();
        List<ClientRepresentation> clients = clientsResource.findByClientId("authz-client");
        Assert.assertFalse(clients.isEmpty());
        String clientId = clients.get(0).getId();
        AuthorizationResource settings = clientsResource.get(clientId).authorization();
        Assert.assertEquals(PERMISSIVE, settings.exportSettings().getPolicyEnforcementMode());
        Assert.assertFalse(settings.resources().findByName("Resource 1").isEmpty());
        Assert.assertFalse(settings.resources().findByName("Resource 15").isEmpty());
        Assert.assertFalse(settings.resources().findByName("Resource 20").isEmpty());
        Assert.assertNotNull(settings.permissions().resource().findByName("Resource 15 Permission"));
        Assert.assertNotNull(settings.policies().role().findByName("Resource 1 Policy"));
        clientsResource.get(clientId).remove();
        clients = clientsResource.findByClientId("authz-client");
        Assert.assertTrue(clients.isEmpty());
    }
}

