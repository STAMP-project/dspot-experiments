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


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class AMQ2616Test {
    @Rule
    public TestName test = new TestName();

    private static final int NUMBER = 2000;

    private BrokerService brokerService;

    private final String ACTIVEMQ_BROKER_BIND = "tcp://0.0.0.0:0";

    private String connectionUri;

    @Test(timeout = 90000)
    public void testQueueResourcesReleased() throws Exception {
        ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(connectionUri);
        Connection tempConnection = fac.createConnection();
        tempConnection.start();
        Session tempSession = tempConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue tempQueue = tempSession.createTemporaryQueue();
        Connection testConnection = fac.createConnection();
        final long startUsage = brokerService.getSystemUsage().getMemoryUsage().getUsage();
        Session testSession = testConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer testProducer = testSession.createProducer(tempQueue);
        byte[] payload = new byte[1024 * 4];
        for (int i = 0; i < (AMQ2616Test.NUMBER); i++) {
            BytesMessage msg = testSession.createBytesMessage();
            msg.writeBytes(payload);
            testProducer.send(msg);
        }
        long endUsage = brokerService.getSystemUsage().getMemoryUsage().getUsage();
        Assert.assertFalse((startUsage == endUsage));
        tempConnection.close();
        Assert.assertTrue("Usage should return to original", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getSystemUsage().getMemoryUsage().getUsage()) == startUsage;
            }
        }));
    }
}

