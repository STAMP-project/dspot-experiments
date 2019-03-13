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
package org.apache.camel.component.netty4.http;


import org.apache.camel.component.netty4.NettyServerBootstrapConfiguration;
import org.junit.Test;


public class NettyHttpTwoRoutesBootstrapConfigurationTest extends BaseNettyTest {
    private NettyServerBootstrapConfiguration bootstrapConfiguration;

    @Test
    public void testTwoRoutes() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:bar").expectedBodiesReceived("Hello Camel");
        String out = template.requestBody("netty4-http:http://localhost:{{port}}/foo", "Hello World", String.class);
        assertEquals("Bye World", out);
        out = template.requestBody("netty4-http:http://localhost:{{port}}/bar", "Hello Camel", String.class);
        assertEquals("Bye Camel", out);
        assertMockEndpointsSatisfied();
        // validate the options
        NettyHttpConsumer consumer = ((NettyHttpConsumer) (context.getRoute("foo").getConsumer()));
        assertEquals(200, consumer.getConfiguration().getBacklog());
        assertEquals(4, consumer.getConfiguration().getWorkerCount());
        assertEquals(true, consumer.getConfiguration().isKeepAlive());
        assertEquals(5000, consumer.getConfiguration().getConnectTimeout());
        consumer = ((NettyHttpConsumer) (context.getRoute("bar").getConsumer()));
        assertEquals(200, consumer.getConfiguration().getBacklog());
        assertEquals(4, consumer.getConfiguration().getWorkerCount());
        assertEquals(true, consumer.getConfiguration().isKeepAlive());
        assertEquals(5000, consumer.getConfiguration().getConnectTimeout());
    }
}

