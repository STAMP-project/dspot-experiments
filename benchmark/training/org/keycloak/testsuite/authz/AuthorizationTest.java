/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AuthorizationTest extends AbstractAuthzTest {
    private AuthzClient authzClient;

    @Test
    public void testResourceWithSameNameDifferentOwner() throws JWSInputException {
        ResourceRepresentation koloResource = createResource("Resource A", "kolo", "Scope A", "Scope B");
        createResourcePermission(koloResource, "Grant Policy");
        ResourceRepresentation martaResource = createResource("Resource A", "marta", "Scope A", "Scope B");
        createResourcePermission(martaResource, "Grant Policy");
        Assert.assertNotEquals(koloResource.getId(), martaResource.getId());
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Resource A");
        List<Permission> permissions = authorize("kolo", "password", request);
        Assert.assertEquals(1, permissions.size());
        Permission permission = permissions.get(0);
        Assert.assertTrue(permission.getScopes().containsAll(Arrays.asList("Scope A", "Scope B")));
        Assert.assertEquals(koloResource.getId(), permission.getResourceId());
        permissions = authorize("marta", "password", request);
        Assert.assertEquals(1, permissions.size());
        permission = permissions.get(0);
        Assert.assertEquals(martaResource.getId(), permission.getResourceId());
        Assert.assertTrue(permission.getScopes().containsAll(Arrays.asList("Scope A", "Scope B")));
    }

    @Test
    public void testResourceServerWithSameNameDifferentOwner() {
        ResourceRepresentation koloResource = createResource("Resource A", "kolo", "Scope A", "Scope B");
        createResourcePermission(koloResource, "Grant Policy");
        ResourceRepresentation serverResource = createResource("Resource A", null, "Scope A", "Scope B");
        createResourcePermission(serverResource, "Grant Policy");
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission("Resource A");
        List<Permission> permissions = authorize("kolo", "password", request);
        Assert.assertEquals(2, permissions.size());
        for (Permission permission : permissions) {
            Assert.assertTrue(((permission.getResourceId().equals(koloResource.getId())) || (permission.getResourceId().equals(serverResource.getId()))));
            Assert.assertEquals("Resource A", permission.getResourceName());
        }
    }
}

