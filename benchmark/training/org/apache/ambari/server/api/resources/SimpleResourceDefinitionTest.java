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


import BaseResourceDefinition.DirectiveType;
import BaseResourceDefinition.DirectiveType.CREATE;
import BaseResourceDefinition.DirectiveType.DELETE;
import BaseResourceDefinition.DirectiveType.READ;
import BaseResourceDefinition.DirectiveType.UPDATE;
import Resource.Type;
import Resource.Type.Task;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * SimpleResourceDefinition tests.
 */
public class SimpleResourceDefinitionTest {
    @Test
    public void testGetPluralName() throws Exception {
        ResourceDefinition resourceDefinition = new SimpleResourceDefinition(Type.Stage, "stage", "stages", Type.Task);
        Assert.assertEquals("stages", resourceDefinition.getPluralName());
    }

    @Test
    public void testGetSingularName() throws Exception {
        ResourceDefinition resourceDefinition = new SimpleResourceDefinition(Type.Stage, "stage", "stages", Type.Task);
        Assert.assertEquals("stage", resourceDefinition.getSingularName());
    }

    @Test
    public void testDirectives() {
        ResourceDefinition resourceDefinition;
        resourceDefinition = new SimpleResourceDefinition(Type.Stage, "stage", "stages", Type.Task);
        validateDirectives(Collections.emptySet(), resourceDefinition.getCreateDirectives());
        validateDirectives(Collections.emptySet(), resourceDefinition.getReadDirectives());
        validateDirectives(Collections.emptySet(), resourceDefinition.getUpdateDirectives());
        validateDirectives(Collections.emptySet(), resourceDefinition.getDeleteDirectives());
        HashMap<BaseResourceDefinition.DirectiveType, Collection<String>> directives = new HashMap<>();
        directives.put(CREATE, Arrays.asList("POST1", "POST2"));
        directives.put(READ, Arrays.asList("GET1", "GET2"));
        directives.put(UPDATE, Arrays.asList("PUT1", "PUT2"));
        directives.put(DELETE, Arrays.asList("DEL1", "DEL2"));
        resourceDefinition = new SimpleResourceDefinition(Type.Stage, "stage", "stages", Collections.singleton(Task), directives);
        validateDirectives(directives.get(CREATE), resourceDefinition.getCreateDirectives());
        validateDirectives(directives.get(READ), resourceDefinition.getReadDirectives());
        validateDirectives(directives.get(UPDATE), resourceDefinition.getUpdateDirectives());
        validateDirectives(directives.get(DELETE), resourceDefinition.getDeleteDirectives());
    }
}

