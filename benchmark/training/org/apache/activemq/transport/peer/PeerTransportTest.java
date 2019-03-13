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
package org.apache.activemq.transport.peer;


import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.MessageIdList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class PeerTransportTest extends TestCase {
    protected static final int MESSAGE_COUNT = 50;

    protected static final int NUMBER_IN_CLUSTER = 3;

    private static final Logger LOG = LoggerFactory.getLogger(PeerTransportTest.class);

    protected ActiveMQDestination destination;

    protected boolean topic = true;

    protected int deliveryMode = DeliveryMode.NON_PERSISTENT;

    protected MessageProducer[] producers;

    protected Connection[] connections;

    protected MessageIdList[] messageIdList;

    /**
     *
     *
     * @throws Exception
     * 		
     */
    public void testSendReceive() throws Exception {
        for (int i = 0; i < (PeerTransportTest.MESSAGE_COUNT); i++) {
            for (int x = 0; x < (producers.length); x++) {
                TextMessage textMessage = new ActiveMQTextMessage();
                textMessage.setText(((("MSG-NO: " + i) + " in cluster: ") + x));
                producers[x].send(textMessage);
            }
        }
        for (int i = 0; i < (PeerTransportTest.NUMBER_IN_CLUSTER); i++) {
            messageIdList[i].assertMessagesReceived(expectedReceiveCount());
        }
    }
}

