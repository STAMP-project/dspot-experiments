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


import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ResourceScopeResource;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ScopeManagementTest extends AbstractAuthorizationTest {
    @Test
    public void testCreate() {
        ScopeRepresentation newScope = createDefaultScope().toRepresentation();
        Assert.assertEquals("Test Scope", newScope.getName());
        Assert.assertEquals("Scope Icon", newScope.getIconUri());
    }

    @Test
    public void testUpdate() {
        ResourceScopeResource scopeResource = createDefaultScope();
        ScopeRepresentation scope = scopeResource.toRepresentation();
        scope.setName("changed");
        scope.setIconUri("changed");
        scopeResource.update(scope);
        scope = scopeResource.toRepresentation();
        Assert.assertEquals("changed", scope.getName());
        Assert.assertEquals("changed", scope.getIconUri());
    }

    @Test(expected = NotFoundException.class)
    public void testDelete() {
        ResourceScopeResource scopeResource = createDefaultScope();
        scopeResource.remove();
        scopeResource.toRepresentation();
    }
}

