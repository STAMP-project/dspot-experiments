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


import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class RestletHeaderFilterStrategyTest extends RestletTestSupport {
    private static final String HEADER_FILTER = "filter";

    @Test
    public void testRestletProducerInFilterAllowedHeader() throws Exception {
        String acceptedHeaderKey = "dontFilter";
        MockEndpoint mock = getMockEndpoint("mock:out");
        mock.expectedHeaderReceived(acceptedHeaderKey, "any value");
        template.requestBodyAndHeader("direct:start", null, acceptedHeaderKey, "any value", String.class);
        mock.assertIsSatisfied();
    }

    @Test
    public void testRestletProducerInFilterNotAllowedHeader() throws Exception {
        String notAcceptedHeaderKey = (RestletHeaderFilterStrategyTest.HEADER_FILTER) + "ThisHeader";
        MockEndpoint mock = getMockEndpoint("mock:out");
        mock.whenAnyExchangeReceived(new Processor() {
            public void process(Exchange exchange) throws Exception {
                Map<String, Object> headers = exchange.getIn().getHeaders();
                for (String key : headers.keySet()) {
                    assertFalse(("Header should have been filtered: " + key), key.startsWith(RestletHeaderFilterStrategyTest.HEADER_FILTER));
                }
            }
        });
        template.requestBodyAndHeader("direct:start", null, notAcceptedHeaderKey, "any value", String.class);
        mock.assertIsSatisfied();
    }
}

