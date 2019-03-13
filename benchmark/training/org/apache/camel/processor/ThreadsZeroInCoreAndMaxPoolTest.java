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
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Assert;
import org.junit.Test;


public class ThreadsZeroInCoreAndMaxPoolTest extends ContextTestSupport {
    @Test
    public void testThreadsCoreBeZero() throws Exception {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    // will use a a custom thread pool with -1 in core and 2 max
                    from("direct:start").threads((-1), 2).to("mock:result");
                }
            });
            Assert.fail("Expect FailedToCreateRouteException exception here");
        } catch (FailedToCreateRouteException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testThreadsCoreAndMaxPoolBuilder() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:foo", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

