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
package org.apache.camel.processor.async;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointIdempotentConsumerTest extends ContextTestSupport {
    private static String beforeThreadName;

    private static String afterThreadName;

    @Test
    public void testAsyncEndpoint() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("A", "B", "C");
        MockEndpoint after = getMockEndpoint("mock:after");
        after.expectedBodiesReceived("Bye Camel", "Bye Camel");
        after.message(0).header("myId").isEqualTo(123);
        after.message(1).header("myId").isEqualTo(456);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Bye Camel", "Bye Camel");
        result.message(0).header("myId").isEqualTo(123);
        result.message(1).header("myId").isEqualTo(456);
        template.sendBodyAndHeader("direct:start", "A", "myId", 123);
        template.sendBodyAndHeader("direct:start", "B", "myId", 123);
        template.sendBodyAndHeader("direct:start", "C", "myId", 456);
        assertMockEndpointsSatisfied();
        Assert.assertFalse("Should use different threads", AsyncEndpointIdempotentConsumerTest.beforeThreadName.equalsIgnoreCase(AsyncEndpointIdempotentConsumerTest.afterThreadName));
    }
}

