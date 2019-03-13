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
import org.apache.camel.TestSupport;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.PipelineDefinition;
import org.apache.camel.model.SendDefinition;
import org.junit.Assert;
import org.junit.Test;


public class DualPipelineTest extends ContextTestSupport {
    @Test
    public void testDualPipeline() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:b").expectedBodiesReceived("After A");
        getMockEndpoint("mock:c").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:d").expectedBodiesReceived("After C");
        getMockEndpoint("mock:e").expectedBodiesReceived("After C");
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // now check the route
        MulticastDefinition mc = TestSupport.assertIsInstanceOf(MulticastDefinition.class, context.getRouteDefinitions().get(0).getOutputs().get(0));
        PipelineDefinition pd1 = TestSupport.assertIsInstanceOf(PipelineDefinition.class, mc.getOutputs().get(0));
        PipelineDefinition pd2 = TestSupport.assertIsInstanceOf(PipelineDefinition.class, mc.getOutputs().get(1));
        Assert.assertEquals(3, pd1.getOutputs().size());
        Assert.assertEquals(4, pd2.getOutputs().size());
        SendDefinition<?> send1 = TestSupport.assertIsInstanceOf(SendDefinition.class, pd1.getOutputs().get(2));
        Assert.assertEquals("mock:b", send1.getUri());
        SendDefinition<?> send2 = TestSupport.assertIsInstanceOf(SendDefinition.class, pd2.getOutputs().get(3));
        Assert.assertEquals("mock:e", send2.getUri());
        SendDefinition<?> send = TestSupport.assertIsInstanceOf(SendDefinition.class, context.getRouteDefinitions().get(0).getOutputs().get(1));
        Assert.assertEquals("mock:result", send.getUri());
    }
}

