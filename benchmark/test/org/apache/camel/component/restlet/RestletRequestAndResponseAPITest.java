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
package org.apache.camel.component.restlet;


import Exchange.CONTENT_TYPE;
import Exchange.HTTP_RESPONSE_CODE;
import RestletConstants.RESTLET_RESPONSE;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;
import org.restlet.Response;


/**
 * Testing that end users can use the {@link Response} API from Restlet directly to have fine grained
 * control of the response they want to use.
 */
public class RestletRequestAndResponseAPITest extends RestletTestSupport {
    @Test
    public void testRestletProducer() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("id", 123);
        headers.put("beverage.beer", "Carlsberg");
        String out = template.requestBodyAndHeaders("direct:start", null, headers, String.class);
        assertEquals("<response>Beer is Good</response>", out);
    }

    @Test
    public void testRestletProducer2() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        headers.put("id", 123);
        headers.put("beverage.beer", "Carlsberg");
        Exchange out = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeaders(headers);
            }
        });
        assertNotNull(out);
        assertEquals("text/xml", out.getOut().getHeader(CONTENT_TYPE));
        assertEquals(200, out.getOut().getHeader(HTTP_RESPONSE_CODE));
        assertEquals("<response>Beer is Good</response>", out.getOut().getBody(String.class));
        // the restlet response should be accessible if needed
        Response response = out.getOut().getHeader(RESTLET_RESPONSE, Response.class);
        assertNotNull(response);
        assertEquals(200, response.getStatus().getCode());
    }
}

