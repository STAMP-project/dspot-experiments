/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.model;


import java.util.Iterator;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ProcessorDefinitionHelperTest extends ContextTestSupport {
    @Test
    public void testFilterTypeInOutputs() throws Exception {
        RouteDefinition route = context.getRouteDefinitions().get(0);
        Iterator<ProcessorDefinition> it = ProcessorDefinitionHelper.filterTypeInOutputs(route.getOutputs(), ProcessorDefinition.class);
        Assert.assertNotNull(it);
        Assert.assertEquals("choice1", it.next().getId());
        Assert.assertEquals("whenfoo", it.next().getId());
        Assert.assertEquals("foo", it.next().getId());
        Assert.assertEquals("whenbar", it.next().getId());
        Assert.assertEquals("bar", it.next().getId());
        Assert.assertEquals("baz", it.next().getId());
        Assert.assertFalse(it.hasNext());
    }
}

