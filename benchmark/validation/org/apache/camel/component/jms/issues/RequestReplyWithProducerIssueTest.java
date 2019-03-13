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
package org.apache.camel.component.jms.issues;


import ExchangePattern.InOut;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class RequestReplyWithProducerIssueTest extends CamelTestSupport {
    @Test
    public void testInOut() throws Exception {
        Endpoint endpoint = context.getEndpoint("activemq:queue:foo");
        Producer producer = endpoint.createProducer();
        producer.start();
        Exchange exchange = endpoint.createExchange(InOut);
        exchange.getIn().setBody("Hello World");
        producer.process(exchange);
        producer.stop();
        assertTrue("Bye World".equals(exchange.getOut().getBody()));
    }
}

