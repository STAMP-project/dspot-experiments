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
package org.apache.camel.component.jetty;


import Exchange.HTTP_RESPONSE_CODE;
import java.io.IOException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.junit.Test;


/**
 * Unit test for jetty http binding ref option.
 */
// END SNIPPET: e1
public class JettyHttpBindingRefTest extends BaseJettyTest {
    @Test
    public void testDefaultJettyHttpBinding() throws Exception {
        Object out = template.requestBody("jetty:http://localhost:{{port}}/myapp/myservice?jettyHttpBindingRef=default", "Hello World");
        assertEquals("Bye World", context.getTypeConverter().convertTo(String.class, out));
        try {
            template.requestBody("jetty:http://localhost:{{port}}/myapp/myotherservice", "Hello World");
            fail();
        } catch (CamelExecutionException e) {
            assertNotNull(e.getCause());
            assertTrue(((e.getCause()) instanceof HttpOperationFailedException));
            assertFalse("Not exactly the message the server returned.".equals(getResponseBody()));
        }
    }

    @Test
    public void testCustomJettyHttpBinding() throws Exception {
        Object out = template.requestBody("jetty:http://localhost:{{port}}/myapp/myotherservice?jettyHttpBindingRef=myownbinder", "Hello World");
        assertEquals("Not exactly the message the server returned.", context.getTypeConverter().convertTo(String.class, out));
    }

    // START SNIPPET: e1
    public class MyJettyHttpBinding extends DefaultJettyHttpBinding {
        @Override
        protected void populateResponse(Exchange exchange, JettyContentExchange httpExchange, Message in, HeaderFilterStrategy strategy, int responseCode) throws IOException {
            Message answer = exchange.getOut();
            answer.setHeaders(in.getHeaders());
            answer.setHeader(HTTP_RESPONSE_CODE, responseCode);
            answer.setBody("Not exactly the message the server returned.");
        }
    }
}

