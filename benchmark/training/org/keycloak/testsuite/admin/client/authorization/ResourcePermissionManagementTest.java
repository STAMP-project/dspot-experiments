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


import DecisionStrategy.AFFIRMATIVE;
import DecisionStrategy.CONSENSUS;
import Logic.NEGATIVE;
import Logic.POSITIVE;
import Response.Status.CONFLICT;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ResourcePermissionResource;
import org.keycloak.admin.client.resource.ResourcePermissionsResource;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourcePermissionManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreateResourcePermission() {
        AuthorizationResource authorization = getClient().authorization();
        ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
        representation.setName("Resource A Permission");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addResource("Resource A");
        representation.addPolicy("Only Marta Policy", "Only Kolo Policy");
        assertCreated(authorization, representation);
    }

    @Test
    public void testCreateResourceType() {
        AuthorizationResource authorization = getClient().authorization();
        ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
        representation.setName("Resource A Type Permission");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.setResourceType("test-resource");
        representation.addPolicy("Only Marta Policy");
        assertCreated(authorization, representation);
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
        representation.setName("Update Test Resource Permission");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addResource("Resource A");
        representation.addPolicy("Only Marta Policy", "Only Kolo Policy");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.getResources().remove("Resource A");
        representation.addResource("Resource B");
        representation.getPolicies().remove("Only Marta Policy");
        ResourcePermissionsResource permissions = authorization.permissions().resource();
        ResourcePermissionResource permission = permissions.findById(representation.getId());
        permission.update(representation);
        assertRepresentation(representation, permission);
        representation.getResources().clear();
        representation.setResourceType("changed");
        permission.update(representation);
        assertRepresentation(representation, permission);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
        representation.setName("Test Delete Permission");
        representation.setResourceType("test-resource");
        representation.addPolicy("Only Marta Policy");
        ResourcePermissionsResource permissions = authorization.permissions().resource();
        try (Response response = permissions.create(representation)) {
            ResourcePermissionRepresentation created = response.readEntity(ResourcePermissionRepresentation.class);
            permissions.findById(created.getId()).remove();
            ResourcePermissionResource removed = permissions.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Permission not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }

    @Test
    public void failCreateWithSameName() {
        AuthorizationResource authorization = getClient().authorization();
        ResourcePermissionRepresentation permission1 = new ResourcePermissionRepresentation();
        permission1.setName("Conflicting Name Permission");
        permission1.setResourceType("test-resource");
        permission1.addPolicy("Only Marta Policy");
        ResourcePermissionsResource permissions = authorization.permissions().resource();
        permissions.create(permission1).close();
        ResourcePermissionRepresentation permission2 = new ResourcePermissionRepresentation();
        permission2.setName(permission1.getName());
        try (Response response = permissions.create(permission2)) {
            Assert.assertEquals(CONFLICT.getStatusCode(), response.getStatus());
        }
    }
}

