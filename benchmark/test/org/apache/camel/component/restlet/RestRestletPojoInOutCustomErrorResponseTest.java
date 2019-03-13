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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;


public class RestRestletPojoInOutCustomErrorResponseTest extends RestletTestSupport {
    @Test
    public void testRestletPojoInOutOk() throws Exception {
        String body = "{\"id\": 123, \"name\": \"Donald Duck\"}";
        String out = template.requestBody((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/lives"), body, String.class);
        assertNotNull(out);
        assertEquals("{\"iso\":\"EN\",\"country\":\"England\"}", out);
    }

    @Test
    public void testRestletPojoInOutError() throws Exception {
        final String body = "{\"id\": 77, \"name\": \"John Doe\"}";
        Exchange reply = template.request((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/lives?throwExceptionOnFailure=false"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(body);
            }
        });
        assertNotNull(reply);
        assertEquals("id value is too low", reply.getOut().getBody(String.class));
        assertEquals(400, reply.getOut().getHeader(HTTP_RESPONSE_CODE));
        String type = reply.getOut().getHeader(CONTENT_TYPE, String.class);
        assertTrue(type.contains("text/plain"));
    }
}

