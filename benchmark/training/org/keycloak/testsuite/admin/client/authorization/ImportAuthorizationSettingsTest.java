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
package org.keycloak.testsuite.admin.client.authorization;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ImportAuthorizationSettingsTest extends AbstractAuthorizationTest {
    @Test
    public void testImportUnorderedSettings() throws Exception {
        ClientResource clientResource = getClientResource();
        ResourceServerRepresentation toImport = JsonSerialization.readValue(getClass().getResourceAsStream("/authorization-test/import-authorization-unordered-settings.json"), ResourceServerRepresentation.class);
        realmsResouce().realm(getRealmId()).roles().create(new RoleRepresentation("user", null, false));
        clientResource.roles().create(new RoleRepresentation("manage-albums", null, false));
        AuthorizationResource authorizationResource = clientResource.authorization();
        authorizationResource.importSettings(toImport);
        Assert.assertEquals(13, authorizationResource.policies().policies().size());
    }
}

