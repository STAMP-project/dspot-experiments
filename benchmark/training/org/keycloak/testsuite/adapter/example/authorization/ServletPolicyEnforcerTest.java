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
package org.keycloak.testsuite.adapter.example.authorization;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT7)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT8)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT9)
public class ServletPolicyEnforcerTest extends AbstractExampleAdapterTest {
    protected static final String REALM_NAME = "servlet-policy-enforcer-authz";

    protected static final String RESOURCE_SERVER_ID = "servlet-policy-enforcer";

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testPattern1() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource/a/b");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 1 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource/a/b");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 1 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource/a/b");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern2() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/a/resource-a");
            Assert.assertFalse(wasDenied());
            navigateTo("/b/resource-a");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 2 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/a/resource-a");
            Assert.assertTrue(wasDenied());
            navigateTo("/b/resource-a");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 2 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/b/resource-a");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern3() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/a/resource-b");
            Assert.assertFalse(wasDenied());
            navigateTo("/b/resource-b");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 3 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/a/resource-b");
            Assert.assertTrue(wasDenied());
            navigateTo("/b/resource-b");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 3 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/b/resource-b");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 2 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/b/resource-a");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 3 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/a/resource-b");
            Assert.assertTrue(wasDenied());
            navigateTo("/b/resource-a");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern4() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource-c");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 4 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource-c");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 4 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource-c");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern5() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/a/a/resource-d");
            Assert.assertFalse(wasDenied());
            navigateTo("/resource/b/resource-d");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 5 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/a/a/resource-d");
            Assert.assertTrue(wasDenied());
            navigateTo("/a/b/resource-d");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 5 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/a/b/resource-d");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern6() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource/a");
            Assert.assertFalse(wasDenied());
            navigateTo("/resource/b");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 6 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource/a");
            Assert.assertTrue(wasDenied());
            navigateTo("/resource/b");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 6 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource/b");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern7() throws Exception {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource/a/f/b");
            Assert.assertFalse(wasDenied());
            navigateTo("/resource/c/f/d");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 7 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource/a/f/b");
            Assert.assertTrue(wasDenied());
            navigateTo("/resource/c/f/d");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 7 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource/c/f/d");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern8() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 8 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 8 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern9() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/file/*.suffix");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 9 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/file/*.suffix");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 9 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/file/*.suffix");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern10() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/resource/a/i/b/c/d/e");
            Assert.assertFalse(wasDenied());
            navigateTo("/resource/a/i/b/c/");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 10 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/resource/a/i/b/c/d/e");
            Assert.assertTrue(wasDenied());
            navigateTo("/resource/a/i/b/c/d");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 10 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/resource/a/i/b/c/d");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPattern11UsingResourceInstancePermission() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/api/v1/resource-a");
            Assert.assertFalse(wasDenied());
            navigateTo("/api/v1/resource-b");
            Assert.assertFalse(wasDenied());
            ResourceRepresentation resource = new ResourceRepresentation("/api/v1/resource-c");
            resource.setUri(resource.getName());
            getAuthorizationResource().resources().create(resource);
            createResourcePermission(((resource.getName()) + " permission"), resource.getName(), "Default Policy");
            login("alice", "alice");
            navigateTo(resource.getUri());
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies(((resource.getName()) + " permission"), "Deny Policy");
            login("alice", "alice");
            navigateTo(resource.getUri());
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies(((resource.getName()) + " permission"), "Default Policy");
            login("alice", "alice");
            navigateTo(resource.getUri());
            Assert.assertFalse(wasDenied());
            navigateTo("/api/v1");
            Assert.assertTrue(wasDenied());
            navigateTo("/api/v1/");
            Assert.assertTrue(wasDenied());
            navigateTo("/api");
            Assert.assertTrue(wasDenied());
            navigateTo("/api/");
            Assert.assertTrue(wasDenied());
        });
    }

    @Test
    public void testPathWithPatternSlashAllAndResourceInstance() {
        performTests(() -> {
            ResourceRepresentation resource = new ResourceRepresentation("Pattern 15 Instance");
            resource.setType("pattern-15");
            resource.setUri("/keycloak-7148/1");
            resource.setOwner("alice");
            getAuthorizationResource().resources().create(resource).close();
            login("alice", "alice");
            navigateTo("/keycloak-7148/1");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a/2");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a/2/sub-b");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 15 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-7148/1");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a/2");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7148/1/sub-a/2/sub-b");
            Assert.assertTrue(wasDenied());
            // does not exist
            navigateTo("/keycloak-7148/2");
            Assert.assertTrue(wasDenied());
        });
    }

    @Test
    public void testPriorityOfURIForResource() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/realm_uri");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak_json_uri");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 12 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/realm_uri");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak_json_uri");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 12 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/realm_uri");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak_json_uri");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testPathOrderWithAllPaths() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/keycloak-6623");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-6623/sub-resource");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 13 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-6623");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-6623/sub-resource");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 14 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-6623");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-6623/sub-resource/resource");
            Assert.assertTrue(wasDenied());
        });
    }

    @Test
    public void testMultipleUriForResourceJSONConfig() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/keycloak-7269/sub-resource1");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource1/whatever/specialSuffix");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2/w/h/a/t/e/v/e/r");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 16 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-7269/sub-resource1");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7269/sub-resource1/whatever/specialSuffix");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2/w/h/a/t/e/v/e/r");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 16 Permission", "Default Policy");
            navigateTo("/keycloak-7269/sub-resource1");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource1/whatever/specialSuffix");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-7269/sub-resource2/w/h/a/t/e/v/e/r");
            Assert.assertFalse(wasDenied());
        });
    }

    @Test
    public void testOverloadedTemplateUri() {
        performTests(() -> {
            login("alice", "alice");
            navigateTo("/keycloak-8823/resource/v1/subresource/123/entities");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-8823/resource/v1/subresource/123/someother");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 17 Entities Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-8823/resource/v1/subresource/123/entities");
            Assert.assertTrue(wasDenied());
            navigateTo("/keycloak-8823/resource/v1/subresource/123/someother");
            Assert.assertFalse(wasDenied());
            updatePermissionPolicies("Pattern 17 Entities Permission", "Default Policy");
            updatePermissionPolicies("Pattern 17 Permission", "Deny Policy");
            login("alice", "alice");
            navigateTo("/keycloak-8823/resource/v1/subresource/123/entities");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-8823/resource/v1/subresource/123/someother");
            Assert.assertTrue(wasDenied());
            updatePermissionPolicies("Pattern 17 Entities Permission", "Default Policy");
            updatePermissionPolicies("Pattern 17 Permission", "Default Policy");
            login("alice", "alice");
            navigateTo("/keycloak-8823/resource/v1/subresource/123/entities");
            Assert.assertFalse(wasDenied());
            navigateTo("/keycloak-8823/resource/v1/subresource/123/someother");
            Assert.assertFalse(wasDenied());
        });
    }

    private interface ExceptionRunnable {
        void run() throws Exception;
    }
}

