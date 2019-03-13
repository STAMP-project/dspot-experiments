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


import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.support.processor.DelegateProcessor;
import org.junit.Assert;
import org.junit.Test;


public class AuditInterceptorDelegateIssueTest extends ContextTestSupport {
    private AuditInterceptorDelegateIssueTest.MyIntercepStrategy strategy;

    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:handled").expectedMessageCount(0);
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(true, strategy.isInvoked());
    }

    @Test
    public void testILE() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:handled").expectedMessageCount(1);
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        template.sendBody("direct:iae", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(true, strategy.isInvoked());
    }

    private static final class MyIntercepStrategy implements InterceptStrategy {
        private volatile boolean invoked;

        public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) throws Exception {
            return new DelegateProcessor(target) {
                protected void processNext(Exchange exchange) throws Exception {
                    invoked = true;
                    super.processNext(exchange);
                }
            };
        }

        public boolean isInvoked() {
            return invoked;
        }
    }
}

