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
package org.apache.camel.component.cxf.transport;


import Exchange.CONTENT_ENCODING;
import Exchange.CONTENT_TYPE;
import javax.xml.ws.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


// Test the CamelDestination with whole CXF context
public class JaxWSCamelDestinationTest extends JaxWSCamelTestSupport {
    private Endpoint endpoint;

    @Test
    public void testDestinationContentType() {
        // publish the endpoint
        endpoint = publishSampleWS("direct:endpoint");
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(JaxWSCamelTestSupport.REQUEST);
            }
        });
        assertThat(exchange.getOut().getHeader(CONTENT_TYPE, String.class), CoreMatchers.is("text/xml; charset=UTF-8"));
        assertTrue(((exchange.getOut().getBody(String.class).indexOf("something!")) > 0));
    }

    @Test
    public void testDestinationWithGzip() {
        // publish the endpoint
        endpoint = publishSampleWSWithGzipEnabled("direct:endpoint");
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(JaxWSCamelTestSupport.REQUEST);
                exchange.getIn().setHeader("Accept-Encoding", "gzip");
            }
        });
        assertThat(exchange.getOut().getHeader(CONTENT_ENCODING, String.class), CoreMatchers.is("gzip"));
    }
}

