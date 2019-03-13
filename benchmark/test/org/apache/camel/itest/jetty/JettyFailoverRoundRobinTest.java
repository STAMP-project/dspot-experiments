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
package org.apache.camel.itest.jetty;


import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JettyFailoverRoundRobinTest extends CamelTestSupport {
    private static int port1 = AvailablePortFinder.getNextAvailable(23021);

    private static int port2 = AvailablePortFinder.getNextAvailable(23022);

    private static int port3 = AvailablePortFinder.getNextAvailable(23023);

    private static int port4 = AvailablePortFinder.getNextAvailable(23024);

    private String bad = ("jetty:http://localhost:" + (JettyFailoverRoundRobinTest.port1)) + "/bad";

    private String bad2 = ("jetty:http://localhost:" + (JettyFailoverRoundRobinTest.port2)) + "/bad2";

    private String good = ("jetty:http://localhost:" + (JettyFailoverRoundRobinTest.port3)) + "/good";

    private String good2 = ("jetty:http://localhost:" + (JettyFailoverRoundRobinTest.port4)) + "/good2";

    @Test
    public void testJettyFailoverRoundRobin() throws Exception {
        getMockEndpoint("mock:bad").expectedMessageCount(1);
        getMockEndpoint("mock:bad2").expectedMessageCount(1);
        getMockEndpoint("mock:good").expectedMessageCount(1);
        getMockEndpoint("mock:good2").expectedMessageCount(0);
        String reply = template.requestBody("direct:start", null, String.class);
        assertEquals("Good", reply);
        assertMockEndpointsSatisfied();
        // reset mocks and send a message again to see that round robin
        // continue where it should
        resetMocks();
        getMockEndpoint("mock:bad").expectedMessageCount(0);
        getMockEndpoint("mock:bad2").expectedMessageCount(0);
        getMockEndpoint("mock:good").expectedMessageCount(0);
        getMockEndpoint("mock:good2").expectedMessageCount(1);
        reply = template.requestBody("direct:start", null, String.class);
        assertEquals("Also good", reply);
    }
}

