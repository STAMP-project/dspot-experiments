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


import Exchange.DUPLICATE_MESSAGE;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.junit.Test;


public class IdempotentConsumerTest extends ContextTestSupport {
    protected Endpoint startEndpoint;

    protected MockEndpoint resultEndpoint;

    @Test
    public void testDuplicateMessagesAreFilteredOut() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").idempotentConsumer(TestSupport.header("messageId"), MemoryIdempotentRepository.memoryIdempotentRepository(200)).to("mock:result");
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
    }

    @Test
    public void testNotSkiDuplicate() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                IdempotentRepository repo = MemoryIdempotentRepository.memoryIdempotentRepository(200);
                from("direct:start").idempotentConsumer(TestSupport.header("messageId")).messageIdRepository(repo).skipDuplicate(false).to("mock:result");
            }
        });
        context.start();
        resultEndpoint.expectedBodiesReceived("one", "two", "one", "two", "one", "three");
        resultEndpoint.message(0).exchangeProperty(DUPLICATE_MESSAGE).isNull();
        resultEndpoint.message(1).exchangeProperty(DUPLICATE_MESSAGE).isNull();
        resultEndpoint.message(2).exchangeProperty(DUPLICATE_MESSAGE).isEqualTo(Boolean.TRUE);
        resultEndpoint.message(3).exchangeProperty(DUPLICATE_MESSAGE).isEqualTo(Boolean.TRUE);
        resultEndpoint.message(4).exchangeProperty(DUPLICATE_MESSAGE).isEqualTo(Boolean.TRUE);
        resultEndpoint.message(5).exchangeProperty(DUPLICATE_MESSAGE).isNull();
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNotSkiDuplicateWithFilter() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                IdempotentRepository repo = MemoryIdempotentRepository.memoryIdempotentRepository(200);
                // START SNIPPET: e1
                // and here we process only new messages (no duplicates)
                // filter out duplicate messages by sending them to someplace else and then stop
                // instruct idempotent consumer to not skip duplicates as we will filter then our self
                from("direct:start").idempotentConsumer(TestSupport.header("messageId")).messageIdRepository(repo).skipDuplicate(false).filter(TestSupport.exchangeProperty(DUPLICATE_MESSAGE).isEqualTo(true)).to("mock:duplicate").stop().end().to("mock:result");
                // END SNIPPET: e1
            }
        });
        context.start();
        resultEndpoint.expectedBodiesReceived("one", "two", "three");
        getMockEndpoint("mock:duplicate").expectedBodiesReceived("one", "two", "one");
        getMockEndpoint("mock:duplicate").allMessages().exchangeProperty(DUPLICATE_MESSAGE).isEqualTo(Boolean.TRUE);
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFailedExchangesNotAddedDeadLetterChannel() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:error").maximumRedeliveries(2).redeliveryDelay(0).logStackTrace(false));
                from("direct:start").idempotentConsumer(TestSupport.header("messageId"), MemoryIdempotentRepository.memoryIdempotentRepository(200)).process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("messageId", String.class);
                        if (id.equals("2")) {
                            throw new IllegalArgumentException("Damm I cannot handle id 2");
                        }
                    }
                }).to("mock:result");
            }
        });
        context.start();
        // we send in 2 messages with id 2 that fails
        getMockEndpoint("mock:error").expectedMessageCount(2);
        resultEndpoint.expectedBodiesReceived("one", "three");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFailedExchangesNotAddedDeadLetterChannelNotHandled() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:error").maximumRedeliveries(2).redeliveryDelay(0).logStackTrace(false));
                from("direct:start").idempotentConsumer(TestSupport.header("messageId"), MemoryIdempotentRepository.memoryIdempotentRepository(200)).process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("messageId", String.class);
                        if (id.equals("2")) {
                            throw new IllegalArgumentException("Damm I cannot handle id 2");
                        }
                    }
                }).to("mock:result");
            }
        });
        context.start();
        // we send in 2 messages with id 2 that fails
        getMockEndpoint("mock:error").expectedMessageCount(2);
        resultEndpoint.expectedBodiesReceived("one", "three");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFailedExchangesNotAdded() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // use default error handler
                errorHandler(defaultErrorHandler());
                from("direct:start").idempotentConsumer(TestSupport.header("messageId"), MemoryIdempotentRepository.memoryIdempotentRepository(200)).process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("messageId", String.class);
                        if (id.equals("2")) {
                            throw new IllegalArgumentException("Damm I cannot handle id 2");
                        }
                    }
                }).to("mock:result");
            }
        });
        context.start();
        resultEndpoint.expectedBodiesReceived("one", "three");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("3", "three");
        assertMockEndpointsSatisfied();
    }
}

