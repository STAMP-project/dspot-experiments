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
import org.apache.camel.Exchange;
import org.apache.camel.support.SynchronizationAdapter;
import org.junit.Test;


public class OnCompletionContainsTest extends ContextTestSupport {
    class SimpleSynchronizationAdapter extends SynchronizationAdapter {
        private final String endPoint;

        private final String body;

        SimpleSynchronizationAdapter(String endPoint, String body) {
            this.endPoint = endPoint;
            this.body = body;
        }

        @Override
        public void onDone(Exchange exchange) {
            template.sendBody(endPoint, body);
        }

        @Override
        public String toString() {
            return body;
        }
    }

    @Test
    public void testOnCompletionContainsTest() throws Exception {
        getMockEndpoint("mock:sync").expectedBodiesReceived("C", "B", "B", "A", "Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

