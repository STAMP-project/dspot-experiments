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
package org.apache.camel.component.nsq;


import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import java.util.concurrent.TimeoutException;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class NsqConsumerTest extends NsqTestSupport {
    private static final int NUMBER_OF_MESSAGES = 10000;

    private static final String TOPIC = "test";

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mockResultEndpoint;

    @Test
    public void testConsumer() throws NSQException, InterruptedException, TimeoutException {
        mockResultEndpoint.expectedMessageCount(1);
        mockResultEndpoint.setAssertPeriod(5000);
        NSQProducer producer = new NSQProducer();
        producer.addAddress("localhost", 4150);
        producer.start();
        producer.produce(NsqConsumerTest.TOPIC, "Hello NSQ!".getBytes());
        mockResultEndpoint.assertIsSatisfied();
        assertEquals("Hello NSQ!", mockResultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class));
    }

    @Test
    public void testLoadConsumer() throws NSQException, InterruptedException, TimeoutException {
        mockResultEndpoint.setExpectedMessageCount(NsqConsumerTest.NUMBER_OF_MESSAGES);
        mockResultEndpoint.setAssertPeriod(5000);
        NSQProducer producer = new NSQProducer();
        producer.addAddress("localhost", 4150);
        producer.start();
        for (int i = 0; i < (NsqConsumerTest.NUMBER_OF_MESSAGES); i++) {
            producer.produce(NsqConsumerTest.TOPIC, String.format("Hello NSQ%d!", i).getBytes());
        }
        mockResultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testRequeue() throws NSQException, InterruptedException, TimeoutException {
        mockResultEndpoint.setExpectedMessageCount(1);
        mockResultEndpoint.setAssertPeriod(5000);
        NSQProducer producer = new NSQProducer();
        producer.addAddress("localhost", 4150);
        producer.start();
        producer.produce(NsqConsumerTest.TOPIC, "Test Requeue".getBytes());
        mockResultEndpoint.assertIsSatisfied();
    }
}

