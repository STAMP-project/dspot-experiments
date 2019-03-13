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


import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SendDefinition;
import org.junit.Assert;
import org.junit.Test;


public class SimpleProcessorIdAwareTest extends ContextTestSupport {
    @Test
    public void testIdAware() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        List<Processor> matches = context.getRoute("foo").filter("b*");
        Assert.assertEquals(2, matches.size());
        Processor bar = matches.get(0);
        Processor baz = matches.get(1);
        Assert.assertEquals("bar", getId());
        Assert.assertEquals("baz", getId());
        bar = context.getProcessor("bar");
        Assert.assertNotNull(bar);
        baz = context.getProcessor("baz");
        Assert.assertNotNull(baz);
        Processor unknown = context.getProcessor("unknown");
        Assert.assertNull(unknown);
        Processor result = context.getProcessor("result");
        Assert.assertNotNull(result);
        ProcessorDefinition def = context.getProcessorDefinition("result");
        Assert.assertNotNull(def);
        Assert.assertEquals("result", def.getId());
        SendDefinition send = TestSupport.assertIsInstanceOf(SendDefinition.class, def);
        Assert.assertNotNull(send);
        Assert.assertEquals("mock:result", send.getEndpointUri());
    }
}

