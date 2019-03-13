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
package org.apache.camel.component.sap.netweaver;


import NetWeaverConstants.COMMAND;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class NetWeaverFlightDataTest extends CamelTestSupport {
    private String username = "P1909969254";

    private String password = "TODO";

    @Test
    public void testNetWeaverFlight() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Dummy", COMMAND, NetWeaverTestConstants.NETWEAVER_FLIGHT_COMMAND);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNetWeaverFlight2() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Dummy", COMMAND, NetWeaverTestConstants.NETWEAVER_FLIGHT_BOOKING_COMMAND);
        assertMockEndpointsSatisfied();
    }
}

