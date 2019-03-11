/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.resources;


import Resource.Type;
import Resource.Type.Artifact;
import Resource.Type.ClusterKerberosDescriptor;
import Resource.Type.Host;
import Resource.Type.HostKerberosIdentity;
import Resource.Type.RoleAuthorization;
import Resource.Type.StackArtifact;
import Resource.Type.UserAuthorization;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 * ResourceInstanceFactoryImpl unit tests.
 */
public class ResourceInstanceFactoryImplTest {
    @Test
    public void testGetStackArtifactDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(StackArtifact, null);
        Assert.assertEquals("artifact", resourceDefinition.getSingularName());
        Assert.assertEquals("artifacts", resourceDefinition.getPluralName());
        Assert.assertEquals(StackArtifact, resourceDefinition.getType());
    }

    @Test
    public void testGetArtifactDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(Artifact, null);
        Assert.assertEquals("artifact", resourceDefinition.getSingularName());
        Assert.assertEquals("artifacts", resourceDefinition.getPluralName());
        Assert.assertEquals(Artifact, resourceDefinition.getType());
    }

    @Test
    public void testGetHostDefinition() {
        ResourceInstanceFactoryImpl resourceInstanceFactory = new ResourceInstanceFactoryImpl();
        Map<Resource.Type, String> mapIds = new HashMap<>();
        mapIds.put(Host, "TeSTHost1");
        ResourceInstance resourceInstance = resourceInstanceFactory.createResource(Host, mapIds);
        Assert.assertEquals(mapIds.get(Host), "testhost1");
    }

    @Test
    public void testGetHostKerberosIdentityDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(HostKerberosIdentity, null);
        Assert.assertNotNull(resourceDefinition);
        Assert.assertEquals("kerberos_identity", resourceDefinition.getSingularName());
        Assert.assertEquals("kerberos_identities", resourceDefinition.getPluralName());
        Assert.assertEquals(HostKerberosIdentity, resourceDefinition.getType());
    }

    @Test
    public void testGetRoleAuthorizationDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(RoleAuthorization, null);
        Assert.assertNotNull(resourceDefinition);
        Assert.assertEquals("authorization", resourceDefinition.getSingularName());
        Assert.assertEquals("authorizations", resourceDefinition.getPluralName());
        Assert.assertEquals(RoleAuthorization, resourceDefinition.getType());
    }

    @Test
    public void testGetUserAuthorizationDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(UserAuthorization, null);
        Assert.assertNotNull(resourceDefinition);
        Assert.assertEquals("authorization", resourceDefinition.getSingularName());
        Assert.assertEquals("authorizations", resourceDefinition.getPluralName());
        Assert.assertEquals(UserAuthorization, resourceDefinition.getType());
    }

    @Test
    public void testGetClusterKerberosDescriptorDefinition() {
        ResourceDefinition resourceDefinition = ResourceInstanceFactoryImpl.getResourceDefinition(ClusterKerberosDescriptor, null);
        Assert.assertNotNull(resourceDefinition);
        Assert.assertEquals("kerberos_descriptor", resourceDefinition.getSingularName());
        Assert.assertEquals("kerberos_descriptors", resourceDefinition.getPluralName());
        Assert.assertEquals(ClusterKerberosDescriptor, resourceDefinition.getType());
    }
}

