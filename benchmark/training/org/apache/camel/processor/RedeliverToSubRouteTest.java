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


import java.io.IOException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;


/**
 *
 */
// END SNIPPET: e2
public class RedeliverToSubRouteTest extends ContextTestSupport {
    @Test
    public void testRedeliverToSubRoute() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:b").expectedBodiesReceived("Hello World", "Hello World", "Hello World");
        getMockEndpoint("mock:c").expectedBodiesReceived("Bye World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e2
    public static class MyProcessor implements Processor {
        private int counter;

        @Override
        public void process(Exchange exchange) throws Exception {
            // use a processor to simulate error in the first 2 calls
            if (((counter)++) < 2) {
                throw new IOException("Forced");
            }
            exchange.getIn().setBody("Bye World");
        }
    }
}

