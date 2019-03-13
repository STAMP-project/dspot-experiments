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
package org.apache.camel.processor.routingslip;


import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class RoutingSlipDataModificationTest extends ContextTestSupport {
    protected static final String ANSWER = "answer";

    protected static final String ROUTING_SLIP_HEADER = "routingSlipHeader";

    protected RoutingSlipDataModificationTest.MyBean myBean = new RoutingSlipDataModificationTest.MyBean();

    @Test
    public void testModificationOfDataAlongRoute() throws Exception {
        MockEndpoint x = getMockEndpoint("mock:x");
        MockEndpoint y = getMockEndpoint("mock:y");
        x.expectedBodiesReceived(RoutingSlipDataModificationTest.ANSWER);
        y.expectedBodiesReceived(((RoutingSlipDataModificationTest.ANSWER) + (RoutingSlipDataModificationTest.ANSWER)));
        sendBody();
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        public MyBean() {
        }

        public String modifyData(@Body
        String body) {
            return body + body;
        }
    }
}

