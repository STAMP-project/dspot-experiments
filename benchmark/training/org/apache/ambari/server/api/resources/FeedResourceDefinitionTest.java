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


import Resource.Type.DRInstance;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for FeedResourceDefinition.
 */
public class FeedResourceDefinitionTest {
    @Test
    public void testGetPluralName() throws Exception {
        FeedResourceDefinition definition = new FeedResourceDefinition();
        Assert.assertEquals("feeds", definition.getPluralName());
    }

    @Test
    public void testGetSingularName() throws Exception {
        FeedResourceDefinition definition = new FeedResourceDefinition();
        Assert.assertEquals("feed", definition.getSingularName());
    }

    @Test
    public void testGetSubResourceDefinitions() throws Exception {
        FeedResourceDefinition definition = new FeedResourceDefinition();
        Set<SubResourceDefinition> subResourceDefinitions = definition.getSubResourceDefinitions();
        Assert.assertEquals(1, subResourceDefinitions.size());
        SubResourceDefinition subResourceDefinition = subResourceDefinitions.iterator().next();
        Assert.assertEquals(DRInstance, subResourceDefinition.getType());
    }
}

