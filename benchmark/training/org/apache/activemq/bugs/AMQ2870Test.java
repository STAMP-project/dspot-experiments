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
package org.apache.activemq.bugs;


import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class AMQ2870Test extends TestSupport {
    static final Logger LOG = LoggerFactory.getLogger(AMQ2870Test.class);

    BrokerService broker = null;

    ActiveMQTopic topic;

    ActiveMQConnection consumerConnection = null;

    ActiveMQConnection producerConnection = null;

    Session producerSession;

    MessageProducer producer;

    final int minPercentUsageForStore = 10;

    String data;

    private final TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    public AMQ2870Test(TestSupport.PersistenceAdapterChoice choice) {
        this.persistenceAdapterChoice = choice;
    }

    @Test(timeout = 300000)
    public void testSize() throws Exception {
        openConsumer();
        assertEquals(0, broker.getAdminView().getStorePercentUsage());
        for (int i = 0; i < 5000; i++) {
            sendMessage(false);
        }
        final BrokerView brokerView = broker.getAdminView();
        // wait for reclaim
        assertTrue("in range with consumer", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                // usage percent updated only on send check for isFull so once
                // sends complete it is no longer updated till next send via a call to isFull
                // this is optimal as it is only used to block producers
                broker.getSystemUsage().getStoreUsage().isFull();
                AMQ2870Test.LOG.info(("store percent usage: " + (brokerView.getStorePercentUsage())));
                return (broker.getAdminView().getStorePercentUsage()) < (minPercentUsageForStore);
            }
        }));
        closeConsumer();
        assertTrue("in range with closed consumer", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                broker.getSystemUsage().getStoreUsage().isFull();
                AMQ2870Test.LOG.info(("store precent usage: " + (brokerView.getStorePercentUsage())));
                return (broker.getAdminView().getStorePercentUsage()) < (minPercentUsageForStore);
            }
        }));
        for (int i = 0; i < 5000; i++) {
            sendMessage(false);
        }
        // What if i drop the subscription?
        broker.getAdminView().destroyDurableSubscriber("cliID", "subName");
        assertTrue("in range after send with consumer", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                broker.getSystemUsage().getStoreUsage().isFull();
                AMQ2870Test.LOG.info(("store precent usage: " + (brokerView.getStorePercentUsage())));
                return (broker.getAdminView().getStorePercentUsage()) < (minPercentUsageForStore);
            }
        }));
    }
}

