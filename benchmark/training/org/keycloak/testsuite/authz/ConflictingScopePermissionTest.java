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
package org.keycloak.testsuite.authz;


import PolicyEnforcementMode.DISABLED;
import PolicyEnforcementMode.ENFORCING;
import PolicyEnforcementMode.PERMISSIVE;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConflictingScopePermissionTest extends AbstractAuthzTest {
    /**
     * <p>Scope Read on Resource A has two conflicting permissions. One is granting access for Marta and the other for Kolo.
     *
     * <p>Scope Read should not be granted for Marta.
     */
    @Test
    public void testMartaCanAccessResourceAWithExecuteAndWrite() throws Exception {
        ClientResource client = getClient(getRealm());
        AuthorizationResource authorization = client.authorization();
        ResourceServerRepresentation settings = authorization.getSettings();
        settings.setPolicyEnforcementMode(ENFORCING);
        authorization.update(settings);
        Collection<Permission> permissions = getEntitlements("marta", "password");
        Assert.assertEquals(1, permissions.size());
        for (Permission permission : new java.util.ArrayList(permissions)) {
            String resourceSetName = permission.getResourceName();
            switch (resourceSetName) {
                case "Resource A" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write"));
                    permissions.remove(permission);
                    break;
                case "Resource C" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                default :
                    Assert.fail((("Unexpected permission for resource [" + resourceSetName) + "]"));
            }
        }
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testWithPermissiveMode() throws Exception {
        ClientResource client = getClient(getRealm());
        AuthorizationResource authorization = client.authorization();
        ResourceServerRepresentation settings = authorization.getSettings();
        settings.setPolicyEnforcementMode(PERMISSIVE);
        authorization.update(settings);
        Collection<Permission> permissions = getEntitlements("marta", "password");
        Assert.assertEquals(3, permissions.size());
        for (Permission permission : new java.util.ArrayList(permissions)) {
            String resourceSetName = permission.getResourceName();
            switch (resourceSetName) {
                case "Resource A" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write"));
                    permissions.remove(permission);
                    break;
                case "Resource C" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                case "Resource B" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                default :
                    Assert.fail((("Unexpected permission for resource [" + resourceSetName) + "]"));
            }
        }
        Assert.assertTrue(permissions.isEmpty());
    }

    @Test
    public void testWithDisabledMode() throws Exception {
        ClientResource client = getClient(getRealm());
        AuthorizationResource authorization = client.authorization();
        ResourceServerRepresentation settings = authorization.getSettings();
        settings.setPolicyEnforcementMode(DISABLED);
        authorization.update(settings);
        Collection<Permission> permissions = getEntitlements("marta", "password");
        Assert.assertEquals(3, permissions.size());
        for (Permission permission : new java.util.ArrayList(permissions)) {
            String resourceSetName = permission.getResourceName();
            switch (resourceSetName) {
                case "Resource A" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                case "Resource C" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                case "Resource B" :
                    Assert.assertThat(permission.getScopes(), Matchers.containsInAnyOrder("execute", "write", "read"));
                    permissions.remove(permission);
                    break;
                default :
                    Assert.fail((("Unexpected permission for resource [" + resourceSetName) + "]"));
            }
        }
        Assert.assertTrue(permissions.isEmpty());
    }
}

