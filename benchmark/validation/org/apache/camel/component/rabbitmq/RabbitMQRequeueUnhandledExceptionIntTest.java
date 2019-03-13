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
package org.apache.camel.component.rabbitmq;


import RabbitMQConstants.REQUEUE;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Integration test to confirm REQUEUE header causes message to be re-queued when an unhandled exception occurs.
 */
public class RabbitMQRequeueUnhandledExceptionIntTest extends AbstractRabbitMQIntTest {
    public static final String ROUTING_KEY = "rk4";

    @Produce(uri = "direct:rabbitMQ")
    protected ProducerTemplate directProducer;

    @EndpointInject(uri = ("rabbitmq:localhost:5672/ex4?username=cameltest&password=cameltest" + "&autoAck=false&queue=q4&routingKey=") + (RabbitMQRequeueUnhandledExceptionIntTest.ROUTING_KEY))
    private Endpoint rabbitMQEndpoint;

    @EndpointInject(uri = "mock:producing")
    private MockEndpoint producingMockEndpoint;

    @EndpointInject(uri = "mock:consuming")
    private MockEndpoint consumingMockEndpoint;

    @Test
    public void testTrueRequeueHeaderWithUnandleExceptionCausesRequeue() throws Exception {
        producingMockEndpoint.expectedMessageCount(1);
        consumingMockEndpoint.setMinimumExpectedMessageCount(2);
        directProducer.sendBodyAndHeader("Hello, World!", REQUEUE, true);
        producingMockEndpoint.assertIsSatisfied();
        consumingMockEndpoint.assertIsSatisfied();
    }
}

