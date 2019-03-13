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
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class LevelDBAggregateRecoverWithSedaTest extends CamelTestSupport {
    private static AtomicInteger counter = new AtomicInteger(0);

    private LevelDBAggregationRepository repo;

    @Test
    public void testLevelDBAggregateRecoverWithSeda() throws Exception {
        // should fail the first 2 times and then recover
        getMockEndpoint("mock:aggregated").expectedMessageCount(3);
        getMockEndpoint("mock:result").expectedBodiesReceived("ABCDE");
        // should be marked as redelivered
        getMockEndpoint("mock:result").message(0).header(REDELIVERED).isEqualTo(Boolean.TRUE);
        // on the 2nd redelivery attempt we success
        getMockEndpoint("mock:result").message(0).header(REDELIVERY_COUNTER).isEqualTo(2);
        template.sendBodyAndHeader("direct:start", "A", "id", 123);
        template.sendBodyAndHeader("direct:start", "B", "id", 123);
        template.sendBodyAndHeader("direct:start", "C", "id", 123);
        template.sendBodyAndHeader("direct:start", "D", "id", 123);
        template.sendBodyAndHeader("direct:start", "E", "id", 123);
        assertMockEndpointsSatisfied(30, TimeUnit.SECONDS);
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

