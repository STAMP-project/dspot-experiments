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
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.spi.Policy;
import org.apache.camel.spi.RouteContext;
import org.junit.Assert;
import org.junit.Test;


public class FromRestGetPolicyTest extends ContextTestSupport {
    @Test
    public void testFromRestModel() throws Exception {
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertEquals(1, context.getRestDefinitions().size());
        getMockEndpoint("mock:hello").expectedMessageCount(1);
        template.sendBody("seda:get-say-hello", null);
        assertMockEndpointsSatisfied();
    }

    private class MyDummyPolicy implements Policy {
        @Override
        public void beforeWrap(RouteContext routeContext, NamedNode definition) {
            // noop
        }

        @Override
        public Processor wrap(RouteContext routeContext, Processor processor) {
            return processor;
        }
    }
}

