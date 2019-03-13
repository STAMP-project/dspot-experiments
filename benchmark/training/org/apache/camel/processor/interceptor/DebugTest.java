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
package org.apache.camel.processor.interceptor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.Breakpoint;
import org.apache.camel.spi.Condition;
import org.junit.Assert;
import org.junit.Test;


public class DebugTest extends ContextTestSupport {
    private List<String> logs = new ArrayList<>();

    private Condition camelCondition;

    private Condition mockCondition;

    private Condition doneCondition;

    private Breakpoint breakpoint;

    @Test
    public void testDebug() throws Exception {
        context.getDebugger().addBreakpoint(breakpoint, camelCondition);
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Hello Camel");
        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("Breakpoint at To[log:foo] with body: Hello Camel", logs.get(0));
        Assert.assertEquals("Breakpoint at To[mock:result] with body: Hello Camel", logs.get(1));
    }

    @Test
    public void testDebugEvent() throws Exception {
        context.getDebugger().addBreakpoint(breakpoint, doneCondition);
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Hello Camel");
        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(2, logs.size());
        Assert.assertEquals("Breakpoint event ExchangeCompletedEvent with body: Hello World", logs.get(0));
        Assert.assertEquals("Breakpoint event ExchangeCompletedEvent with body: Hello Camel", logs.get(1));
    }

    @Test
    public void testDebugSuspended() throws Exception {
        context.getDebugger().addBreakpoint(breakpoint, mockCondition, camelCondition);
        // suspend the breakpoint
        context.getDebugger().suspendAllBreakpoints();
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Hello Camel");
        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(0, logs.size());
        // resume the breakpoint
        context.getDebugger().activateAllBreakpoints();
        // reset and test again now the breakpoint is active
        resetMocks();
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Hello Camel");
        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals("Breakpoint at To[mock:result] with body: Hello Camel", logs.get(0));
    }

    @Test
    public void testDebugRemoveBreakpoint() throws Exception {
        context.getDebugger().addBreakpoint(breakpoint);
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(10, logs.size());
        // remove the breakpoint
        context.getDebugger().removeBreakpoint(breakpoint);
        // reset and test again now the breakpoint is removed
        resetMocks();
        logs.clear();
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello Camel");
        template.sendBody("direct:start", "Hello Camel");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(0, logs.size());
    }
}

