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
package org.apache.camel.component.jms;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class JmsInOutFixedReplyQueueTimeoutTest extends CamelTestSupport {
    protected String componentName = "activemq";

    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");
        String out = template.requestBody("direct:start", "Camel", String.class);
        assertEquals("Bye Camel", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTimeout() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.requestBody("direct:start", "World", String.class);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(ExchangeTimedOutException.class, e.getCause());
        }
        assertMockEndpointsSatisfied();
    }
}

