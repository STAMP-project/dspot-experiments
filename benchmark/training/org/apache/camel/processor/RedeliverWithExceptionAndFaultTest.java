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
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class RedeliverWithExceptionAndFaultTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testOk() throws Exception {
        RedeliverWithExceptionAndFaultTest.counter = 0;
        getMockEndpoint("mock:result").expectedMessageCount(1);
        String out = template.requestBody("direct:start", "Hello World", String.class);
        Assert.assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTransientAndPersistentError() throws Exception {
        RedeliverWithExceptionAndFaultTest.counter = 0;
        getMockEndpoint("mock:result").expectedMessageCount(0);
        String out = template.requestBody("direct:start", "Boom", String.class);
        Assert.assertEquals("Persistent error", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTransientAndPersistentErrorWithExchange() throws Exception {
        RedeliverWithExceptionAndFaultTest.counter = 0;
        getMockEndpoint("mock:result").expectedMessageCount(0);
        Exchange out = template.request("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Boom");
            }
        });
        Assert.assertTrue("Should be failed", out.isFailed());
        Assert.assertNull("No exception", out.getException());
        Assert.assertTrue((((out.getOut()) != null) && (out.getOut().isFault())));
        Assert.assertEquals("Persistent error", out.getOut().getBody());
        assertMockEndpointsSatisfied();
    }
}

