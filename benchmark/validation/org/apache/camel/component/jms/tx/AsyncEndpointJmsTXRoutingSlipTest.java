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
package org.apache.camel.component.jms.tx;


import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class AsyncEndpointJmsTXRoutingSlipTest extends CamelSpringTestSupport {
    private static String beforeThreadName;

    private static String afterThreadName;

    @Test
    public void testAsyncEndpointOK() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:after").expectedBodiesReceived("Bye Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");
        template.sendBody("activemq:queue:inbox", "Hello Camel");
        assertMockEndpointsSatisfied();
        // we are synchronous due to TX so the we are using same threads during the routing
        assertTrue("Should use same threads", AsyncEndpointJmsTXRoutingSlipTest.beforeThreadName.equalsIgnoreCase(AsyncEndpointJmsTXRoutingSlipTest.afterThreadName));
    }
}

