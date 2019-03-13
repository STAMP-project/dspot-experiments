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
package org.apache.camel.component.test;


import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.junit.Test;


/**
 *
 */
public class TestEndpointTest extends ContextTestSupport {
    private String expectedBody = "Hello World";

    @Test
    public void testMocksAreValid() throws Exception {
        // now run the test and send in a message with the expected body
        template.sendBody("seda:foo", expectedBody);
        assertMockEndpointsSatisfied();
    }

    private final class MyEndpoint extends DefaultEndpoint {
        private MyEndpoint(String endpointUri, CamelContext camelContext) {
            super(endpointUri, null);
            setCamelContext(camelContext);
        }

        @Override
        public Producer createProducer() throws Exception {
            // not needed for this test
            return null;
        }

        @Override
        public Consumer createConsumer(final Processor processor) throws Exception {
            return new Consumer() {
                @Override
                public Endpoint getEndpoint() {
                    return TestEndpointTest.MyEndpoint.this;
                }

                @Override
                public Processor getProcessor() {
                    return null;
                }

                @Override
                public void start() throws Exception {
                    // when starting then send a message to the processor
                    Exchange exchange = new org.apache.camel.support.DefaultExchange(getEndpoint());
                    exchange.getIn().setBody(expectedBody);
                    processor.process(exchange);
                }

                @Override
                public void stop() throws Exception {
                    // noop
                }
            };
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}

