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
package org.apache.camel.processor;


import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.junit.Assert;
import org.junit.Test;


public class ExchangeIdempotentConsumerTest extends ContextTestSupport {
    protected Endpoint startEndpoint;

    protected MockEndpoint resultEndpoint;

    private ExchangeIdempotentConsumerTest.MyIdempotentRepo repo = new ExchangeIdempotentConsumerTest.MyIdempotentRepo();

    @Test
    public void testDuplicateMessagesAreFilteredOut() throws Exception {
        Assert.assertEquals(0, repo.getExchange().size());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").idempotentConsumer(TestSupport.header("messageId"), repo).to("mock:result");
            }
        });
        context.start();
        resultEndpoint.expectedBodiesReceived("one", "two", "three");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
        // we used 6 different exchanges
        Assert.assertEquals(6, repo.getExchange().size());
        for (Exchange exchange : resultEndpoint.getExchanges()) {
            // should be in repo list
            Assert.assertTrue("Should contain the exchange", repo.getExchange().contains(exchange.getExchangeId()));
        }
    }

    private final class MyIdempotentRepo implements IdempotentRepository {
        private IdempotentRepository delegate;

        private Set<String> exchanges = new LinkedHashSet<>();

        private MyIdempotentRepo() {
            delegate = MemoryIdempotentRepository.memoryIdempotentRepository(200);
        }

        @Override
        public boolean add(Exchange exchange, String key) {
            exchanges.add(exchange.getExchangeId());
            return delegate.add(key);
        }

        @Override
        public boolean contains(Exchange exchange, String key) {
            exchanges.add(exchange.getExchangeId());
            return delegate.contains(key);
        }

        @Override
        public boolean remove(Exchange exchange, String key) {
            exchanges.add(exchange.getExchangeId());
            return delegate.remove(key);
        }

        @Override
        public boolean confirm(Exchange exchange, String key) {
            exchanges.add(exchange.getExchangeId());
            return delegate.confirm(key);
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public boolean add(String key) {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public boolean contains(String key) {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public boolean remove(String key) {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public boolean confirm(String key) {
            throw new UnsupportedOperationException("Should not be called");
        }

        public Set<String> getExchange() {
            return exchanges;
        }

        @Override
        public void start() throws Exception {
            // noop
        }

        @Override
        public void stop() throws Exception {
            // noop
        }
    }
}

