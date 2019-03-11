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


import Resource.Type.Alert;
import Resource.Type.AlertDefinition;
import Resource.Type.Artifact;
import Resource.Type.ClusterKerberosDescriptor;
import Resource.Type.ClusterPrivilege;
import Resource.Type.ClusterStackVersion;
import Resource.Type.ConfigGroup;
import Resource.Type.Configuration;
import Resource.Type.Host;
import Resource.Type.Request;
import Resource.Type.Service;
import Resource.Type.ServiceConfigVersion;
import Resource.Type.Workflow;
import java.util.Set;
import org.apache.ambari.server.api.query.render.ClusterBlueprintRenderer;
import org.apache.ambari.server.api.query.render.DefaultRenderer;
import org.apache.ambari.server.api.query.render.MinimalRenderer;
import org.junit.Assert;
import org.junit.Test;


/**
 * ClusterResourceDefinition unit tests.
 */
public class ClusterResourceDefinitionTest {
    @Test
    public void testGetPluralName() {
        Assert.assertEquals("clusters", new ClusterResourceDefinition().getPluralName());
    }

    @Test
    public void testGetSingularName() {
        Assert.assertEquals("cluster", new ClusterResourceDefinition().getSingularName());
    }

    @Test
    public void testGetSubResourceDefinitions() {
        ResourceDefinition resource = new ClusterResourceDefinition();
        Set<SubResourceDefinition> subResources = resource.getSubResourceDefinitions();
        Assert.assertEquals(13, subResources.size());
        Assert.assertTrue(includesType(subResources, Service));
        Assert.assertTrue(includesType(subResources, Host));
        Assert.assertTrue(includesType(subResources, Configuration));
        Assert.assertTrue(includesType(subResources, Request));
        Assert.assertTrue(includesType(subResources, Workflow));
        Assert.assertTrue(includesType(subResources, ConfigGroup));
        Assert.assertTrue(includesType(subResources, AlertDefinition));
        Assert.assertTrue(includesType(subResources, ServiceConfigVersion));
        Assert.assertTrue(includesType(subResources, ClusterPrivilege));
        Assert.assertTrue(includesType(subResources, Alert));
        Assert.assertTrue(includesType(subResources, ClusterStackVersion));
        Assert.assertTrue(includesType(subResources, Artifact));
        Assert.assertTrue(includesType(subResources, ClusterKerberosDescriptor));
    }

    @Test
    public void testGetRenderer() {
        ResourceDefinition resource = new ClusterResourceDefinition();
        Assert.assertTrue(((resource.getRenderer(null)) instanceof DefaultRenderer));
        Assert.assertTrue(((resource.getRenderer("default")) instanceof DefaultRenderer));
        Assert.assertTrue(((resource.getRenderer("minimal")) instanceof MinimalRenderer));
        Assert.assertTrue(((resource.getRenderer("blueprint")) instanceof ClusterBlueprintRenderer));
        try {
            resource.getRenderer("foo");
            Assert.fail("Should have thrown an exception due to invalid renderer type");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}

