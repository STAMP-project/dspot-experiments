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
import Resource.Type.StackArtifact;
import Resource.Type.StackConfiguration;
import Resource.Type.StackServiceComponent;
import Resource.Type.Theme;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.spi.Resource;
import org.junit.Test;


/**
 * StackServiceResourceDefinition unit tests
 */
public class StackServiceResourceDefinitionTest {
    @Test
    public void testDefinitionNames() {
        ResourceDefinition def = new StackServiceResourceDefinition();
        Assert.assertEquals("service", def.getSingularName());
        Assert.assertEquals("services", def.getPluralName());
    }

    @Test
    public void testGetSubResourceDefinitions() {
        ResourceDefinition def = new StackServiceResourceDefinition();
        Set<SubResourceDefinition> subResources = def.getSubResourceDefinitions();
        Assert.assertEquals(5, subResources.size());
        boolean configReturned = false;
        boolean componentReturned = false;
        boolean artifactReturned = false;
        boolean themesReturned = false;
        for (SubResourceDefinition subResource : subResources) {
            Resource.Type type = subResource.getType();
            if (type.equals(StackConfiguration)) {
                configReturned = true;
            } else
                if (type.equals(StackServiceComponent)) {
                    componentReturned = true;
                } else
                    if (type.equals(StackArtifact)) {
                        artifactReturned = true;
                    } else
                        if (type.equals(Theme)) {
                            themesReturned = true;
                        }



        }
        Assert.assertTrue(configReturned);
        Assert.assertTrue(componentReturned);
        Assert.assertTrue(artifactReturned);
        Assert.assertTrue(themesReturned);
    }
}

