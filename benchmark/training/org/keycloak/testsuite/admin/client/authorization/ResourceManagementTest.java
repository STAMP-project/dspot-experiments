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


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourceManagementTest extends AbstractAuthorizationTest {
    @Test
    public void testCreate() {
        ResourceRepresentation newResource = createResource();
        Assert.assertEquals("Test Resource", newResource.getName());
        Assert.assertEquals("/test/*", newResource.getUri());
        Assert.assertEquals("test-resource", newResource.getType());
        Assert.assertEquals("icon-test-resource", newResource.getIconUri());
        Map<String, List<String>> attributes = newResource.getAttributes();
        Assert.assertEquals(2, attributes.size());
        Assert.assertTrue(attributes.containsKey("a"));
        Assert.assertTrue(attributes.containsKey("b"));
        Assert.assertTrue(attributes.get("a").containsAll(Arrays.asList("a1", "a2", "a3")));
        Assert.assertEquals(3, attributes.get("a").size());
        Assert.assertTrue(attributes.get("b").containsAll(Arrays.asList("b1")));
        Assert.assertEquals(1, attributes.get("b").size());
    }

    @Test
    public void failCreateWithSameName() {
        ResourceRepresentation newResource = createResource();
        try {
            doCreateResource(newResource);
            Assert.fail("Can not create resources with the same name and owner");
        } catch (Exception e) {
            Assert.assertEquals(HttpResponseException.class, e.getCause().getClass());
            Assert.assertEquals(409, HttpResponseException.class.cast(e.getCause()).getStatusCode());
        }
        newResource.setName(((newResource.getName()) + " Another"));
        newResource = doCreateResource(newResource);
        Assert.assertNotNull(newResource.getId());
        Assert.assertEquals("Test Resource Another", newResource.getName());
    }

    @Test
    public void failCreateWithSameNameDifferentOwner() {
        ResourceRepresentation martaResource = createResource("Resource A", "marta", null, null, null);
        ResourceRepresentation koloResource = createResource("Resource A", "kolo", null, null, null);
        Assert.assertNotNull(martaResource.getId());
        Assert.assertNotNull(koloResource.getId());
        Assert.assertNotEquals(martaResource.getId(), koloResource.getId());
        Assert.assertEquals(2, getClientResource().authorization().resources().findByName(martaResource.getName()).size());
        List<ResourceRepresentation> martaResources = getClientResource().authorization().resources().findByName(martaResource.getName(), "marta");
        Assert.assertEquals(1, martaResources.size());
        Assert.assertEquals(martaResource.getId(), martaResources.get(0).getId());
        List<ResourceRepresentation> koloResources = getClientResource().authorization().resources().findByName(martaResource.getName(), "kolo");
        Assert.assertEquals(1, koloResources.size());
        Assert.assertEquals(koloResource.getId(), koloResources.get(0).getId());
    }

    @Test
    public void testUpdate() {
        ResourceRepresentation resource = createResource();
        resource.setType("changed");
        resource.setIconUri("changed");
        resource.setUri("changed");
        Map<String, List<String>> attributes = resource.getAttributes();
        attributes.remove("a");
        attributes.put("c", Arrays.asList("c1", "c2"));
        attributes.put("b", Arrays.asList("changed"));
        resource = doUpdateResource(resource);
        Assert.assertEquals("changed", resource.getIconUri());
        Assert.assertEquals("changed", resource.getType());
        Assert.assertEquals("changed", resource.getUri());
        attributes = resource.getAttributes();
        Assert.assertEquals(2, attributes.size());
        Assert.assertFalse(attributes.containsKey("a"));
        Assert.assertTrue(attributes.containsKey("b"));
        Assert.assertTrue(attributes.get("b").containsAll(Arrays.asList("changed")));
        Assert.assertEquals(1, attributes.get("b").size());
        Assert.assertTrue(attributes.get("c").containsAll(Arrays.asList("c1", "c2")));
        Assert.assertEquals(2, attributes.get("c").size());
    }

    @Test(expected = NotFoundException.class)
    public void testDelete() {
        ResourceRepresentation resource = createResource();
        doRemoveResource(resource);
        getClientResource().authorization().resources().resource(resource.getId()).toRepresentation();
    }

    @Test
    public void testAssociateScopes() {
        ResourceRepresentation updated = createResourceWithDefaultScopes();
        Assert.assertEquals(3, updated.getScopes().size());
        Assert.assertTrue(containsScope("Scope A", updated));
        Assert.assertTrue(containsScope("Scope B", updated));
        Assert.assertTrue(containsScope("Scope C", updated));
    }

    @Test
    public void testUpdateScopes() {
        ResourceRepresentation resource = createResourceWithDefaultScopes();
        Set<ScopeRepresentation> scopes = new java.util.HashSet(resource.getScopes());
        Assert.assertEquals(3, scopes.size());
        Assert.assertTrue(scopes.removeIf(( scopeRepresentation) -> scopeRepresentation.getName().equals("Scope B")));
        resource.setScopes(scopes);
        ResourceRepresentation updated = doUpdateResource(resource);
        Assert.assertEquals(2, resource.getScopes().size());
        Assert.assertFalse(containsScope("Scope B", updated));
        Assert.assertTrue(containsScope("Scope A", updated));
        Assert.assertTrue(containsScope("Scope C", updated));
        scopes = new java.util.HashSet(updated.getScopes());
        Assert.assertTrue(scopes.removeIf(( scopeRepresentation) -> scopeRepresentation.getName().equals("Scope A")));
        Assert.assertTrue(scopes.removeIf(( scopeRepresentation) -> scopeRepresentation.getName().equals("Scope C")));
        updated.setScopes(scopes);
        updated = doUpdateResource(updated);
        Assert.assertEquals(0, updated.getScopes().size());
    }
}

