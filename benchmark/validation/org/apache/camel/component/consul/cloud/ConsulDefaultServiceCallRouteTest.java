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
package org.apache.camel.component.consul.cloud;


import com.orbitz.consul.AgentClient;
import com.orbitz.consul.model.agent.Registration;
import java.util.List;
import org.apache.camel.component.consul.ConsulTestSupport;
import org.junit.Test;


public class ConsulDefaultServiceCallRouteTest extends ConsulTestSupport {
    private static final String SERVICE_NAME = "http-service";

    private static final int SERVICE_COUNT = 5;

    private static final int SERVICE_PORT_BASE = 8080;

    private AgentClient client;

    private List<Registration> registrations;

    private List<String> expectedBodies;

    // *************************************************************************
    // Test
    // *************************************************************************
    @Test
    public void testServiceCall() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(ConsulDefaultServiceCallRouteTest.SERVICE_COUNT);
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder(expectedBodies);
        registrations.forEach(( r) -> template.sendBody("direct:start", "ping"));
        assertMockEndpointsSatisfied();
    }
}

