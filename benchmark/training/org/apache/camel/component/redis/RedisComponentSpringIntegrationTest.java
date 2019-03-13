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
package org.apache.camel.component.redis;


import RedisConstants.CHANNEL;
import RedisConstants.COMMAND;
import RedisConstants.MESSAGE;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("IntegrationTest")
public class RedisComponentSpringIntegrationTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "direct:start")
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @Test
    public void shouldFilterDuplicateMessagesUsingIdempotentRepository() throws Exception {
        result.expectedMessageCount(2);
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(COMMAND, "PUBLISH");
                exchange.getIn().setHeader(CHANNEL, "testChannel");
                exchange.getIn().setHeader(MESSAGE, "Message one");
            }
        });
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(COMMAND, "PUBLISH");
                exchange.getIn().setHeader(CHANNEL, "testChannel");
                exchange.getIn().setHeader(MESSAGE, "Message one");
            }
        });
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(COMMAND, "PUBLISH");
                exchange.getIn().setHeader(CHANNEL, "testChannel");
                exchange.getIn().setHeader(MESSAGE, "Message two");
            }
        });
        assertMockEndpointsSatisfied();
        Exchange resultExchangeOne = result.getExchanges().get(0);
        Exchange resultExchangeTwo = result.getExchanges().get(1);
        assertEquals("Message one", resultExchangeOne.getIn().getBody());
        assertEquals("Message two", resultExchangeTwo.getIn().getBody());
    }
}

