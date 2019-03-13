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
package org.apache.camel.processor;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ValidateIdTest extends ContextTestSupport {
    @Test
    public void testValidateId() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        RouteDefinition route = context.getRouteDefinition("myRoute");
        Assert.assertNotNull(route);
        // mock:result should be the only with the result as id
        Assert.assertTrue(route.getOutputs().get(0).getId().equals("myValidate"));
        Assert.assertFalse(route.getOutputs().get(1).getId().equals("result"));
        Assert.assertTrue(route.getOutputs().get(2).getId().equals("result"));
        Assert.assertTrue(route.getOutputs().get(3).getId().equals("after"));
    }
}

