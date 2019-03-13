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
package org.apache.camel.test.spring;


import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.impl.BreakpointSupport;
import org.junit.Assert;
import org.junit.Test;


public class CamelSpringRunnerProvidesBreakpointTest extends CamelSpringRunnerPlainTest {
    @Test
    @Override
    public void testProvidesBreakpoint() {
        Assert.assertNotNull(camelContext.getDebugger());
        Assert.assertNotNull(camelContext2.getDebugger());
        start.sendBody("David");
        Assert.assertNotNull(camelContext.getDebugger());
        Assert.assertNotNull(camelContext.getDebugger().getBreakpoints());
        Assert.assertEquals(1, camelContext.getDebugger().getBreakpoints().size());
        Assert.assertTrue(((camelContext.getDebugger().getBreakpoints().get(0)) instanceof CamelSpringRunnerProvidesBreakpointTest.TestBreakpoint));
        Assert.assertTrue(((CamelSpringRunnerProvidesBreakpointTest.TestBreakpoint) (camelContext.getDebugger().getBreakpoints().get(0))).isBreakpointHit());
    }

    private static final class TestBreakpoint extends BreakpointSupport {
        private boolean breakpointHit;

        @Override
        public void beforeProcess(Exchange exchange, Processor processor, NamedNode definition) {
            breakpointHit = true;
        }

        public boolean isBreakpointHit() {
            return breakpointHit;
        }
    }
}

