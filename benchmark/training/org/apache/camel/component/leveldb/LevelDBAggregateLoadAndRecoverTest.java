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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LevelDBAggregateLoadAndRecoverTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(LevelDBAggregateLoadAndRecoverTest.class);

    private static final int SIZE = 200;

    private static AtomicInteger counter = new AtomicInteger();

    @Test
    public void testLoadAndRecoverLevelDBAggregate() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(((LevelDBAggregateLoadAndRecoverTest.SIZE) / 10));
        mock.setResultWaitTime((50 * 1000));
        LevelDBAggregateLoadAndRecoverTest.LOG.info((("Staring to send " + (LevelDBAggregateLoadAndRecoverTest.SIZE)) + " messages."));
        for (int i = 0; i < (LevelDBAggregateLoadAndRecoverTest.SIZE); i++) {
            final int value = 1;
            char id = 'A';
            Map<String, Object> headers = new HashMap<>();
            headers.put("id", id);
            headers.put("seq", i);
            LevelDBAggregateLoadAndRecoverTest.LOG.debug("Sending {} with id {}", value, id);
            template.sendBodyAndHeaders("seda:start", value, headers);
            // simulate a little delay
            Thread.sleep(5);
        }
        LevelDBAggregateLoadAndRecoverTest.LOG.info((("Sending all " + (LevelDBAggregateLoadAndRecoverTest.SIZE)) + " message done. Now waiting for aggregation to complete."));
        assertMockEndpointsSatisfied();
        int recovered = 0;
        for (Exchange exchange : mock.getReceivedExchanges()) {
            if ((exchange.getIn().getHeader(REDELIVERED)) != null) {
                recovered++;
            }
        }
        int expected = ((LevelDBAggregateLoadAndRecoverTest.SIZE) / 10) / 10;
        int delta = Math.abs((expected - recovered));
        if (delta == 0) {
            assertEquals((("There should be " + expected) + " recovered"), expected, recovered);
        } else {
            assertTrue(((("We expected " + expected) + " recovered but the delta is within accepted range ") + delta), (delta < 3));
        }
    }

    public static class MyAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            Integer body1 = oldExchange.getIn().getBody(Integer.class);
            Integer body2 = newExchange.getIn().getBody(Integer.class);
            int sum = body1 + body2;
            oldExchange.getIn().setBody(sum);
            return oldExchange;
        }
    }
}

