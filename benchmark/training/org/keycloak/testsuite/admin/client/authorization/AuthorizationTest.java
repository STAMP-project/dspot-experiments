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


import PolicyEnforcerConfig.EnforcementMode.ENFORCING;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.constants.ServiceAccountConstants;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AuthorizationTest extends AbstractAuthorizationTest {
    @Test
    public void testEnableAuthorizationServices() {
        ClientResource clientResource = getClientResource();
        ClientRepresentation resourceServer = getResourceServer();
        RealmResource realm = realmsResouce().realm(getRealmId());
        UserRepresentation serviceAccount = realm.users().search(((ServiceAccountConstants.SERVICE_ACCOUNT_USER_PREFIX) + (resourceServer.getClientId()))).get(0);
        Assert.assertNotNull(serviceAccount);
        List<RoleRepresentation> serviceAccountRoles = realm.users().get(serviceAccount.getId()).roles().clientLevel(resourceServer.getId()).listAll();
        Assert.assertTrue(serviceAccountRoles.stream().anyMatch(( roleRepresentation) -> "uma_protection".equals(roleRepresentation.getName())));
        enableAuthorizationServices(false);
        enableAuthorizationServices(true);
        serviceAccount = clientResource.getServiceAccountUser();
        Assert.assertNotNull(serviceAccount);
        realm = realmsResouce().realm(getRealmId());
        serviceAccountRoles = realm.users().get(serviceAccount.getId()).roles().clientLevel(resourceServer.getId()).listAll();
        Assert.assertTrue(serviceAccountRoles.stream().anyMatch(( roleRepresentation) -> "uma_protection".equals(roleRepresentation.getName())));
        JSPolicyRepresentation policy = new JSPolicyRepresentation();
        policy.setName("should be removed");
        policy.setCode("");
        clientResource.authorization().policies().js().create(policy);
        List<ResourceRepresentation> defaultResources = clientResource.authorization().resources().resources();
        Assert.assertEquals(1, defaultResources.size());
        List<PolicyRepresentation> defaultPolicies = clientResource.authorization().policies().policies();
        Assert.assertEquals(3, defaultPolicies.size());
        enableAuthorizationServices(false);
        enableAuthorizationServices(true);
        ResourceServerRepresentation settings = clientResource.authorization().getSettings();
        Assert.assertEquals(ENFORCING.name(), settings.getPolicyEnforcementMode().name());
        Assert.assertTrue(settings.isAllowRemoteResourceManagement());
        Assert.assertEquals(resourceServer.getId(), settings.getClientId());
        defaultResources = clientResource.authorization().resources().resources();
        Assert.assertEquals(1, defaultResources.size());
        defaultPolicies = clientResource.authorization().policies().policies();
        Assert.assertEquals(2, defaultPolicies.size());
        serviceAccount = clientResource.getServiceAccountUser();
        Assert.assertNotNull(serviceAccount);
        serviceAccountRoles = realm.users().get(serviceAccount.getId()).roles().clientLevel(resourceServer.getId()).listAll();
        Assert.assertTrue(serviceAccountRoles.stream().anyMatch(( roleRepresentation) -> "uma_protection".equals(roleRepresentation.getName())));
    }

    // KEYCLOAK-6321
    @Test
    public void testRemoveDefaultResourceWithAdminEventsEnabled() {
        RealmResource realmResource = testRealmResource();
        RealmRepresentation realmRepresentation = realmResource.toRepresentation();
        realmRepresentation.setAdminEventsEnabled(true);
        realmResource.update(realmRepresentation);
        ClientResource clientResource = getClientResource();
        ClientRepresentation resourceServer = getResourceServer();
        ResourceServerRepresentation settings = clientResource.authorization().getSettings();
        Assert.assertEquals(ENFORCING.name(), settings.getPolicyEnforcementMode().name());
        Assert.assertEquals(resourceServer.getId(), settings.getClientId());
        List<ResourceRepresentation> defaultResources = clientResource.authorization().resources().resources();
        Assert.assertEquals(1, defaultResources.size());
        clientResource.authorization().resources().resource(defaultResources.get(0).getId()).remove();
        Assert.assertTrue(clientResource.authorization().resources().resources().isEmpty());
    }
}

