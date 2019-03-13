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
package org.apache.camel.processor.onexception;


import Exchange.EXCEPTION_CAUGHT;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for using a processor to peek the caused exception
 */
// END SNIPPET: e2
public class OnExceptionProcessorInspectCausedExceptionWithDefaultErrorHandlerTest extends ContextTestSupport {
    @Test
    public void testInspectExceptionByProcessor() throws Exception {
        getMockEndpoint("mock:myerror").expectedMessageCount(1);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should throw exception");
        } catch (Exception e) {
            // ok
        }
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e2
    public static class MyFunctionFailureHandler implements Processor {
        public void process(Exchange exchange) throws Exception {
            // the caused by exception is stored in a property on the exchange
            Throwable caused = exchange.getProperty(EXCEPTION_CAUGHT, Throwable.class);
            Assert.assertNotNull(caused);
            // here you can do what you want, but Camel regard this exception as handled, and
            // this processor as a failurehandler, so it wont do redeliveries. So this is the
            // end of this route. But if we want to route it somewhere we can just get a
            // producer template and send it.
            // send it to our mock endpoint
            exchange.getContext().createProducerTemplate().send("mock:myerror", exchange);
        }
    }
}

