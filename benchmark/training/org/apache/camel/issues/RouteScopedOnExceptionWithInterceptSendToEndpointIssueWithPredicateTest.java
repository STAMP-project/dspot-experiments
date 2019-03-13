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
package org.apache.camel.issues;


import Exchange.REDELIVERY_COUNTER;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.reifier.RouteReifier;
import org.junit.Assert;
import org.junit.Test;


/**
 * Based on user forum issue
 */
public class RouteScopedOnExceptionWithInterceptSendToEndpointIssueWithPredicateTest extends ContextTestSupport {
    private final AtomicInteger invoked = new AtomicInteger();

    @Test
    public void testIssue() throws Exception {
        final Predicate fail = PredicateBuilder.or(TestSupport.header(REDELIVERY_COUNTER).isNull(), TestSupport.header(REDELIVERY_COUNTER).isLessThan(5));
        RouteDefinition route = context.getRouteDefinitions().get(0);
        RouteReifier.adviceWith(route, context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:*").skipSendToOriginalEndpoint().process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        invoked.incrementAndGet();
                        if (fail.matches(exchange)) {
                            throw new java.net.ConnectException("Forced");
                        }
                    }
                }).to("mock:ok");
            }
        });
        getMockEndpoint("mock:global").expectedMessageCount(0);
        getMockEndpoint("mock:ok").expectedMessageCount(1);
        getMockEndpoint("mock:exhausted").expectedMessageCount(0);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // 5 retry + 1 ok
        Assert.assertEquals(6, invoked.get());
    }
}

