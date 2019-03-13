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


import org.apache.camel.CamelExchangeException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class SplitterStopOnExceptionTest extends ContextTestSupport {
    @Test
    public void testSplitStopOnExceptionOk() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:split");
        mock.expectedBodiesReceived("Hello World", "Bye World");
        template.sendBody("direct:start", "Hello World,Bye World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitStopOnExceptionStop() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:split");
        // we do stop so we stop splitting when the exception occurs and thus we only receive 1 message
        mock.expectedBodiesReceived("Hello World");
        try {
            template.sendBody("direct:start", "Hello World,Kaboom,Bye World");
            Assert.fail("Should thrown an exception");
        } catch (CamelExecutionException e) {
            CamelExchangeException cause = TestSupport.assertIsInstanceOf(CamelExchangeException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Multicast processing failed for number 1."));
            Assert.assertEquals("Forced", cause.getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
    }

    public static class MyProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            String body = exchange.getIn().getBody(String.class);
            if ("Kaboom".equals(body)) {
                throw new IllegalArgumentException("Forced");
            }
        }
    }
}

