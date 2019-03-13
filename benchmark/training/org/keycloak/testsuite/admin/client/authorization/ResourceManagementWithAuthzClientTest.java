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


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourceManagementWithAuthzClientTest extends ResourceManagementTest {
    private AuthzClient authzClient;

    @Test
    public void testFindMatchingUri() {
        doCreateResource(new ResourceRepresentation("/*", Collections.emptySet(), "/*", null));
        doCreateResource(new ResourceRepresentation("/resources/*", Collections.emptySet(), "/resources/*", null));
        doCreateResource(new ResourceRepresentation("/resources-a/*", Collections.emptySet(), "/resources-a/*", null));
        doCreateResource(new ResourceRepresentation("/resources-b/{pattern}", Collections.emptySet(), "/resources-b/{pattern}", null));
        doCreateResource(new ResourceRepresentation("/resources-c/{pattern}/*", Collections.emptySet(), "/resources-c/{pattern}/*", null));
        doCreateResource(new ResourceRepresentation("/resources/{pattern}/{pattern}/*", Collections.emptySet(), "/resources/{pattern}/{pattern}/*", null));
        doCreateResource(new ResourceRepresentation("/resources/{pattern}/sub-resources/{pattern}/*", Collections.emptySet(), "/resources/{pattern}/sub-resources/{pattern}/*", null));
        doCreateResource(new ResourceRepresentation("/resources/{pattern}/sub-resource", Collections.emptySet(), "/resources/{pattern}/sub-resources/{pattern}/*", null));
        AuthzClient authzClient = getAuthzClient();
        List<ResourceRepresentation> resources = authzClient.protection().resource().findByMatchingUri("/test");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/*", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources-a/test");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources-a/*", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources/*", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources-b/a");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources-b/{pattern}", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources-c/a/b");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources-c/{pattern}/*", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources/a/b/c");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources/{pattern}/{pattern}/*", resources.get(0).getUri());
        resources = authzClient.protection().resource().findByMatchingUri("/resources/a/sub-resources/c/d");
        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals("/resources/{pattern}/sub-resources/{pattern}/*", resources.get(0).getUri());
    }

    @Test
    public void testFindDeep() {
        ResourceRepresentation resource1 = new ResourceRepresentation("/*", new HashSet());
        resource1.addScope("a", "b", "c");
        resource1.setType("type");
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("a", Arrays.asList("a"));
        attributes.put("b", Arrays.asList("b"));
        attributes.put("c", Arrays.asList("c"));
        resource1.setAttributes(attributes);
        resource1.setIconUri("icon");
        resource1.setUris(new HashSet(Arrays.asList("/a", "/b", "/c")));
        ResourceRepresentation resource = doCreateResource(resource1);
        AuthzClient authzClient = getAuthzClient();
        List<ResourceRepresentation> representations = authzClient.protection().resource().find(resource.getId(), null, null, null, null, null, false, true, null, null);
        Assert.assertEquals(1, representations.size());
        Assert.assertEquals(resource.getId(), representations.get(0).getId());
        Assert.assertEquals(resource.getName(), representations.get(0).getName());
        Assert.assertEquals(resource.getIconUri(), representations.get(0).getIconUri());
        Assert.assertThat(resource.getUris(), Matchers.containsInAnyOrder(representations.get(0).getUris().toArray()));
        Assert.assertThat(resource.getAttributes().entrySet(), Matchers.containsInAnyOrder(representations.get(0).getAttributes().entrySet().toArray()));
    }
}

