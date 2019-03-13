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


import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import java.util.concurrent.TimeUnit;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class LevelDBAggregateRecoverDeadLetterChannelFailedTest extends CamelTestSupport {
    private LevelDBAggregationRepository repo;

    @Test
    public void testLevelDBAggregateRecoverDeadLetterChannelFailed() throws Exception {
        // should fail all times
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:aggregated").expectedMessageCount(3);
        // it should keep sending to DLC if it failed, so test for min 2 attempts
        getMockEndpoint("mock:dead").expectedMinimumMessageCount(2);
        // all the details should be the same about redelivered and redelivered 2 times
        getMockEndpoint("mock:dead").message(0).header(REDELIVERED).isEqualTo(Boolean.TRUE);
        getMockEndpoint("mock:dead").message(0).header(REDELIVERY_COUNTER).isEqualTo(2);
        getMockEndpoint("mock:dead").message(1).header(REDELIVERY_COUNTER).isEqualTo(2);
        getMockEndpoint("mock:dead").message(1).header(REDELIVERED).isEqualTo(Boolean.TRUE);
        template.sendBodyAndHeader("direct:start", "A", "id", 123);
        template.sendBodyAndHeader("direct:start", "B", "id", 123);
        template.sendBodyAndHeader("direct:start", "C", "id", 123);
        template.sendBodyAndHeader("direct:start", "D", "id", 123);
        template.sendBodyAndHeader("direct:start", "E", "id", 123);
        assertMockEndpointsSatisfied(30, TimeUnit.SECONDS);
        // all the details should be the same about redelivered and redelivered 2 times
        Exchange first = getMockEndpoint("mock:dead").getReceivedExchanges().get(0);
        assertEquals(true, first.getIn().getHeader(REDELIVERED));
        assertEquals(2, first.getIn().getHeader(REDELIVERY_COUNTER));
        Exchange second = getMockEndpoint("mock:dead").getReceivedExchanges().get(1);
        assertEquals(true, second.getIn().getHeader(REDELIVERED));
        assertEquals(2, first.getIn().getHeader(REDELIVERY_COUNTER));
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

