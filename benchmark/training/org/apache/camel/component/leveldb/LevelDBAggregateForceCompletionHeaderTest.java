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
package org.apache.camel.component.leveldb;


import Exchange.AGGREGATED_COMPLETED_BY;
import Exchange.AGGREGATION_COMPLETE_ALL_GROUPS;
import Exchange.AGGREGATION_COMPLETE_ALL_GROUPS_INCLUSIVE;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * To test CAMEL-4118 support for completing all aggregation groups with a signal message
 */
public class LevelDBAggregateForceCompletionHeaderTest extends CamelTestSupport {
    @Test
    public void testForceCompletionTrueExclusive() throws Exception {
        getMockEndpoint("mock:aggregated").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "test1", "id", "1");
        template.sendBodyAndHeader("direct:start", "test2", "id", "2");
        template.sendBodyAndHeader("direct:start", "test3", "id", "1");
        template.sendBodyAndHeader("direct:start", "test4", "id", "2");
        assertMockEndpointsSatisfied();
        getMockEndpoint("mock:aggregated").expectedMessageCount(2);
        getMockEndpoint("mock:aggregated").expectedBodiesReceivedInAnyOrder("test1test3", "test2test4");
        getMockEndpoint("mock:aggregated").expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "force");
        // now send the signal message to trigger completion of all groups, message should NOT be aggregated
        template.sendBodyAndHeader("direct:start", "test5", AGGREGATION_COMPLETE_ALL_GROUPS, true);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testForceCompletionTrueInclusive() throws Exception {
        getMockEndpoint("mock:aggregated").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "test1", "id", "1");
        template.sendBodyAndHeader("direct:start", "test2", "id", "2");
        template.sendBodyAndHeader("direct:start", "test3", "id", "1");
        template.sendBodyAndHeader("direct:start", "test4", "id", "2");
        assertMockEndpointsSatisfied();
        getMockEndpoint("mock:aggregated").expectedMessageCount(3);
        getMockEndpoint("mock:aggregated").expectedBodiesReceivedInAnyOrder("test1test3", "test2test4", "test5");
        getMockEndpoint("mock:aggregated").expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "force");
        // now send a message to trigger completion of all groups, message should be aggregated
        Map<String, Object> headers = new HashMap<>();
        headers.put("id", "3");
        headers.put(AGGREGATION_COMPLETE_ALL_GROUPS_INCLUSIVE, true);
        template.sendBodyAndHeaders("direct:start", "test5", headers);
        assertMockEndpointsSatisfied();
    }

    public static class MyAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            String body1 = oldExchange.getIn().getBody(String.class);
            String body2 = newExchange.getIn().getBody(String.class);
            oldExchange.getIn().setBody((body1 + body2));
            return oldExchange;
        }
    }
}

