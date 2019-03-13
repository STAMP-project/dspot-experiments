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


import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AuthzCleanupTest extends AbstractKeycloakTest {
    @Test
    public void testCreate() throws Exception {
        ClientsResource clients = getAdminClient().realms().realm(TEST).clients();
        ClientRepresentation client = clients.findByClientId("myclient").get(0);
        ResourceServerRepresentation settings = JsonSerialization.readValue(getClass().getResourceAsStream("/authorization-test/acme-resource-server-cleanup-test.json"), ResourceServerRepresentation.class);
        clients.get(client.getId()).authorization().importSettings(settings);
        testingClient.server().run(AuthzCleanupTest::setup);
    }
}

