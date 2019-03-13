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
package org.apache.camel.component.mina2;


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class Mina2SslContextParametersUdpTest extends BaseMina2Test {
    protected int messageCount = 3;

    public Mina2SslContextParametersUdpTest() {
    }

    @Test
    public void testMinaRoute() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedBodiesReceived("Hello Message: 0", "Hello Message: 1", "Hello Message: 2");
        sendUdpMessages();
        assertMockEndpointsSatisfied();
    }
}

