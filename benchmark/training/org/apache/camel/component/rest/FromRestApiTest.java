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
package org.apache.camel.component.rest;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.junit.Assert;
import org.junit.Test;


public class FromRestApiTest extends ContextTestSupport {
    @Test
    public void testFromRestModel() throws Exception {
        Assert.assertEquals(1, context.getRestDefinitions().size());
        RestDefinition rest = context.getRestDefinitions().get(0);
        Assert.assertNotNull(rest);
        Assert.assertEquals("/say/hello", rest.getPath());
        Assert.assertEquals(1, rest.getVerbs().size());
        ToDefinition to = TestSupport.assertIsInstanceOf(ToDefinition.class, rest.getVerbs().get(0).getTo());
        Assert.assertEquals("log:hello", to.getUri());
        // should be 2 routes
        Assert.assertEquals(2, context.getRoutes().size());
    }
}

