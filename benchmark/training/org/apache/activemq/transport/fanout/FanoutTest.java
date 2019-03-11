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
package org.apache.activemq.transport.fanout;


import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;


public class FanoutTest extends TestCase {
    BrokerService broker1;

    BrokerService broker2;

    ActiveMQConnectionFactory producerFactory = new ActiveMQConnectionFactory("fanout:(static:(tcp://localhost:61616,tcp://localhost:61617))?fanOutQueues=true");

    Connection producerConnection;

    Session producerSession;

    int messageCount = 100;

    public void testSendReceive() throws Exception {
        MessageProducer prod = createProducer();
        for (int i = 0; i < (messageCount); i++) {
            Message msg = producerSession.createTextMessage(("Message " + i));
            prod.send(msg);
        }
        prod.close();
        assertMessagesReceived("tcp://localhost:61616");
        assertMessagesReceived("tcp://localhost:61617");
    }
}

