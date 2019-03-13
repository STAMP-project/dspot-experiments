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


import Exchange.HTTP_METHOD;
import Exchange.HTTP_RESPONSE_CODE;
import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class RestRestletCorsTest extends RestletTestSupport {
    Splitter headerSplitter = Splitter.on(",").trimResults();

    @Test
    public void testCors() throws Exception {
        Exchange out = template.request((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/123/basic"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_METHOD, "OPTIONS");
            }
        });
        assertEquals("https://localhost:443", out.getOut().getHeader("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", out.getOut().getHeader("Access-Control-Allow-Methods"));
        assertEquals("Origin, Accept, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers", out.getOut().getHeader("Access-Control-Allow-Headers"));
        assertEquals("1234", out.getOut().getHeader("Access-Control-Max-Age"));
    }

    @Test
    public void testRestletProducerGet() throws Exception {
        Exchange exchange = template.request((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/123/basic"), null);
        // verify no problems have occurred:
        assertFalse(exchange.isFailed());
        Message message = exchange.getOut();
        assertThat(message.getHeader(HTTP_RESPONSE_CODE, Integer.class), CoreMatchers.is(200));
        // verify all header values match those specified in restConfiguration:
        assertThat(message.getHeader("Access-Control-Allow-Origin", String.class), CoreMatchers.is("https://localhost:443"));
        assertHeaderSet(message, "Access-Control-Allow-Methods", "GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertHeaderSet(message, "Access-Control-Allow-Headers", "Origin", "Accept", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers");
        assertThat(message.getHeader("Access-Control-Max-Age", Integer.class), CoreMatchers.is(1234));
    }
}

