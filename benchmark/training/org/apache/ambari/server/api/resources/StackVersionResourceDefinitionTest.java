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
import Resource.Type.CompatibleRepositoryVersion;
import Resource.Type.OperatingSystem;
import Resource.Type.RepositoryVersion;
import Resource.Type.StackArtifact;
import Resource.Type.StackLevelConfiguration;
import Resource.Type.StackService;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


/**
 * StackVersionResourceDefinition unit tests
 */
public class StackVersionResourceDefinitionTest {
    @Test
    public void testDefinitionNames() {
        ResourceDefinition def = new StackVersionResourceDefinition();
        Assert.assertEquals("version", def.getSingularName());
        Assert.assertEquals("versions", def.getPluralName());
    }

    @Test
    public void testGetSubResourceDefinitions() {
        ResourceDefinition def = new StackVersionResourceDefinition();
        Set<SubResourceDefinition> subResources = def.getSubResourceDefinitions();
        Assert.assertEquals(7, subResources.size());
        boolean operatingSystemFound = false;
        boolean serviceFound = false;
        boolean configFound = false;
        boolean repoFound = false;
        boolean artifactReturned = false;
        boolean compatibleFound = false;
        for (SubResourceDefinition subResource : subResources) {
            Resource.Type type = subResource.getType();
            if (type.equals(OperatingSystem)) {
                operatingSystemFound = true;
            } else
                if (type.equals(StackService)) {
                    serviceFound = true;
                } else
                    if (type.equals(StackLevelConfiguration)) {
                        configFound = true;
                    } else
                        if (type.equals(RepositoryVersion)) {
                            repoFound = true;
                        } else
                            if (type.equals(StackArtifact)) {
                                artifactReturned = true;
                            } else
                                if (type.equals(CompatibleRepositoryVersion)) {
                                    compatibleFound = true;
                                }





        }
        Assert.assertTrue(operatingSystemFound);
        Assert.assertTrue(serviceFound);
        Assert.assertTrue(configFound);
        Assert.assertTrue(repoFound);
        Assert.assertTrue(artifactReturned);
        Assert.assertTrue(compatibleFound);
    }
}

