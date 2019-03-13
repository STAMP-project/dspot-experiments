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


import java.util.concurrent.TimeUnit;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class LevelDBAggregateDiscardOnTimeoutTest extends CamelTestSupport {
    private LevelDBAggregationRepository repo;

    @Test
    public void testAggregateDiscardOnTimeout() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:aggregated");
        mock.expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "A", "id", 123);
        template.sendBodyAndHeader("direct:start", "B", "id", 123);
        // wait 3 seconds
        Thread.sleep(3000);
        mock.assertIsSatisfied();
        // now send 3 which does not timeout
        mock.reset();
        mock.expectedBodiesReceived("C+D+E");
        template.sendBodyAndHeader("direct:start", "A", "id", 123);
        template.sendBodyAndHeader("direct:start", "B", "id", 123);
        template.sendBodyAndHeader("direct:start", "C", "id", 123);
        // should complete before timeout
        mock.await(1500, TimeUnit.MILLISECONDS);
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

