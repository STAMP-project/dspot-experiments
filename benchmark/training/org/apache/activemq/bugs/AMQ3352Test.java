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


import DeliveryMode.PERSISTENT;
import Session.CLIENT_ACKNOWLEDGE;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.junit.Test;


public class AMQ3352Test {
    TransportConnector connector;

    BrokerService brokerService;

    @Test
    public void verifyEnqueueLargeNumWithStateTracker() throws Exception {
        String url = ("failover:(" + (connector.getPublishableConnectString())) + ")?jms.useAsyncSend=true&trackMessages=true&maxCacheSize=131072";
        ActiveMQConnection conn = ((ActiveMQConnection) (new ActiveMQConnectionFactory(url).createConnection(null, null)));
        Session session = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(session.createQueue("EVENTQ"));
        producer.setDeliveryMode(PERSISTENT);
        producer.setDisableMessageID(true);
        producer.setDisableMessageTimestamp(true);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 1024; i++) {
            buffer.append(String.valueOf(Math.random()));
        }
        String payload = buffer.toString();
        for (int i = 0; i < 10000; i++) {
            StringBuffer buff = new StringBuffer("x");
            buff.append(payload);
            producer.send(session.createTextMessage(buff.toString()));
        }
    }
}

