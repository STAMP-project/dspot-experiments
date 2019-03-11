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
package org.apache.activemq.camel;


import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.Wait;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactedConsumeTest extends CamelSpringTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(TransactedConsumeTest.class);

    BrokerService broker = null;

    int messageCount = 100000;

    @Test
    public void testConsume() throws Exception {
        TransactedConsumeTest.LOG.info("Wait for dequeue message...");
        assertTrue(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView().getTotalDequeueCount()) >= (messageCount);
            }
        }, ((20 * 60) * 1000)));
        long duration = (System.currentTimeMillis()) - (TransactedConsumeTest.firstConsumed.get());
        TransactedConsumeTest.LOG.info((("Done message consumption in " + duration) + "millis"));
    }

    static AtomicLong firstConsumed = new AtomicLong();

    static AtomicLong consumed = new AtomicLong();

    static class ConnectionLog implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            if ((TransactedConsumeTest.consumed.getAndIncrement()) == 0) {
                TransactedConsumeTest.firstConsumed.set(System.currentTimeMillis());
            }
            ActiveMQTextMessage m = ((ActiveMQTextMessage) (getJmsMessage()));
            // Thread.currentThread().sleep(500);
            if (((TransactedConsumeTest.consumed.get()) % 500) == 0) {
                TransactedConsumeTest.LOG.info(("received on " + (m.getConnection().toString())));
            }
        }
    }
}

