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
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Based on end user on forum how to get the 404 error code in his enrich aggregator
 */
public class JettyHandle404Test extends BaseJettyTest {
    @Test
    public void testSimulate404() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Page not found");
        mock.expectedHeaderReceived(HTTP_RESPONSE_CODE, 404);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testCustomerErrorHandler() throws Exception {
        String response = template.requestBody("http://localhost:{{port}}/myserver1?throwExceptionOnFailure=false", null, String.class);
        // look for the error message which is sent by MyErrorHandler
        log.info("Response: {}", response);
        assertTrue("Get a wrong error message", ((response.indexOf("MyErrorHandler")) > 0));
    }
}

