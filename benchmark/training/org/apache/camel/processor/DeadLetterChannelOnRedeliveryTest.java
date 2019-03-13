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


import Exchange.REDELIVERY_COUNTER;
import Exchange.REDELIVERY_MAX_COUNTER;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for testing possibility to modify exchange before redelivering
 */
// END SNIPPET: e2
public class DeadLetterChannelOnRedeliveryTest extends ContextTestSupport {
    static int counter;

    @Test
    public void testOnExceptionAlterMessageBeforeRedelivery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World3");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOnExceptionAlterMessageWithHeadersBeforeRedelivery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World3");
        mock.expectedHeaderReceived("foo", "123");
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", "123");
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e2
    // This is our processor that is executed before every redelivery attempt
    // here we can do what we want in the java code, such as altering the message
    public class MyRedeliverProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            // the message is being redelivered so we can alter it
            // we just append the redelivery counter to the body
            // you can of course do all kind of stuff instead
            String body = exchange.getIn().getBody(String.class);
            int count = exchange.getIn().getHeader(REDELIVERY_COUNTER, Integer.class);
            exchange.getIn().setBody((body + count));
            // the maximum redelivery was set to 5
            int max = exchange.getIn().getHeader(REDELIVERY_MAX_COUNTER, Integer.class);
            Assert.assertEquals(5, max);
        }
    }
}

