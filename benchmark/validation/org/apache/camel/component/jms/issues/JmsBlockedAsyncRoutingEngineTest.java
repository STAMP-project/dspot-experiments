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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerPluginSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests CAMEL-5769.
 * Camel JMS producer can block a thread under specific circumstances.
 */
public class JmsBlockedAsyncRoutingEngineTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsBlockedAsyncRoutingEngineTest.class);

    private BrokerService broker;

    private final CountDownLatch latch = new CountDownLatch(5);

    private final Synchronization callback = new Synchronization() {
        @Override
        public void onFailure(Exchange exchange) {
            JmsBlockedAsyncRoutingEngineTest.LOG.info(">>>> Callback onFailure");
            latch.countDown();
        }

        @Override
        public void onComplete(Exchange exchange) {
            JmsBlockedAsyncRoutingEngineTest.LOG.info(">>>> Callback onComplete");
            latch.countDown();
        }
    };

    @Test
    public void testBlockedAsyncRoutingEngineTest() throws Exception {
        // 0. This message takes 2000ms to ACK from the broker due to the DelayerBrokerPlugin
        // Until then, the correlation ID doesn't get updated locally
        try {
            template.asyncRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "hello");
        } catch (Exception e) {
        }
        // 1. We wait a bit for the CorrelationTimeoutMap purge process to run
        Thread.sleep(3000);
        // 2. We send 5 messages that take 2 seconds so that they time out
        template.asyncCallbackRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "beSlow", callback);
        template.asyncCallbackRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "beSlow", callback);
        template.asyncCallbackRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "beSlow", callback);
        template.asyncCallbackRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "beSlow", callback);
        template.asyncCallbackRequestBody("activemq:queue:test?requestTimeout=500&useMessageIDAsCorrelationID=true", "beSlow", callback);
        // 3. We assert that we were notified of all timeout exceptions
        assertTrue(latch.await(3000, TimeUnit.MILLISECONDS));
    }

    private class DelayerBrokerPlugin extends BrokerPluginSupport {
        int i;

        @Override
        public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
            String destinationName = messageSend.getDestination().getPhysicalName();
            JmsBlockedAsyncRoutingEngineTest.LOG.info(("******** Received message for destination " + destinationName));
            // do not intercept sends to DLQ
            if ((destinationName.toLowerCase().contains("test")) && ((i) == 0)) {
                Thread.sleep(2000);
                JmsBlockedAsyncRoutingEngineTest.LOG.info(("******** Waited 2 seconds for destination: " + destinationName));
                (i)++;
            }
            super.send(producerExchange, messageSend);
        }
    }
}

