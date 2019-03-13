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
package org.apache.camel.component.metrics;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class MetricsComponentRouteTest extends CamelTestSupport {
    @Produce(uri = "direct:start-1")
    protected ProducerTemplate template1;

    @Produce(uri = "direct:start-2")
    protected ProducerTemplate template2;

    @Test
    public void testMetrics() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        template1.sendBody(new Object());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMessageContentDelivery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        String body = "Message Body";
        String header1 = "Header 1";
        String header2 = "Header 2";
        Object value1 = new Date();
        Object value2 = System.currentTimeMillis();
        mock.expectedBodiesReceived(body);
        mock.expectedHeaderReceived(header1, value1);
        mock.expectedHeaderReceived(header2, value2);
        Map<String, Object> headers = new HashMap<>();
        headers.put(header1, value1);
        headers.put(header2, value2);
        template1.sendBodyAndHeaders(body, headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHeaderRemoval() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        Object body = new Object();
        Date now = new Date();
        mock.expectedBodiesReceived(body);
        mock.expectedHeaderReceived(("." + (MetricsConstants.HEADER_PREFIX)), "value");
        mock.expectedHeaderReceived("date", now);
        Map<String, Object> headers = new HashMap<>();
        headers.put(MetricsConstants.HEADER_METRIC_NAME, "a name");
        headers.put(MetricsConstants.HEADER_HISTOGRAM_VALUE, 34L);
        headers.put(((MetricsConstants.HEADER_PREFIX) + "notExistingHeader"), "?");
        headers.put(("." + (MetricsConstants.HEADER_PREFIX)), "value");
        headers.put("date", now);
        template2.sendBodyAndHeaders(body, headers);
        assertMockEndpointsSatisfied();
    }
}

