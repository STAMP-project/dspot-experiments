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
import Exchange.AGGREGATED_CORRELATION_KEY;
import Exchange.AGGREGATED_SIZE;
import java.util.concurrent.TimeUnit;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.fusesource.hawtbuf.Buffer;
import org.junit.Test;


public class LevelDBAggregateNotLostTest extends CamelTestSupport {
    private LevelDBAggregationRepository repo;

    @Test
    public void testLevelDBAggregateNotLost() throws Exception {
        getMockEndpoint("mock:aggregated").expectedBodiesReceived("ABCDE");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "A", "id", 123);
        template.sendBodyAndHeader("direct:start", "B", "id", 123);
        template.sendBodyAndHeader("direct:start", "C", "id", 123);
        template.sendBodyAndHeader("direct:start", "D", "id", 123);
        template.sendBodyAndHeader("direct:start", "E", "id", 123);
        assertMockEndpointsSatisfied(30, TimeUnit.SECONDS);
        Thread.sleep(1000);
        String exchangeId = getMockEndpoint("mock:aggregated").getReceivedExchanges().get(0).getExchangeId();
        // the exchange should be in the completed repo where we should be able to find it
        final LevelDBFile levelDBFile = repo.getLevelDBFile();
        final LevelDBCamelCodec codec = new LevelDBCamelCodec();
        byte[] bf = levelDBFile.getDb().get(LevelDBAggregationRepository.keyBuilder("repo1-completed", exchangeId));
        // assert the exchange was not lost and we got all the information still
        assertNotNull(bf);
        Exchange completed = codec.unmarshallExchange(context, new Buffer(bf));
        assertNotNull(completed);
        // should retain the exchange id
        assertEquals(exchangeId, completed.getExchangeId());
        assertEquals("ABCDE", completed.getIn().getBody());
        assertEquals(123, completed.getIn().getHeader("id"));
        assertEquals("size", completed.getProperty(AGGREGATED_COMPLETED_BY));
        assertEquals(5, completed.getProperty(AGGREGATED_SIZE));
        // will store correlation keys as String
        assertEquals("123", completed.getProperty(AGGREGATED_CORRELATION_KEY));
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

