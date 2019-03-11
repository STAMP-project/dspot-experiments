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
package org.apache.activemq.network.jms;


import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public class QueueBridgeTest extends TestCase implements MessageListener {
    protected static final int MESSAGE_COUNT = 10;

    private static final Logger LOG = LoggerFactory.getLogger(QueueBridgeTest.class);

    protected AbstractApplicationContext context;

    protected QueueConnection localConnection;

    protected QueueConnection remoteConnection;

    protected QueueRequestor requestor;

    protected QueueSession requestServerSession;

    protected MessageConsumer requestServerConsumer;

    protected MessageProducer requestServerProducer;

    public void testQueueRequestorOverBridge() throws JMSException {
        for (int i = 0; i < (QueueBridgeTest.MESSAGE_COUNT); i++) {
            TextMessage msg = requestServerSession.createTextMessage(("test msg: " + i));
            TextMessage result = ((TextMessage) (requestor.request(msg)));
            TestCase.assertNotNull(result);
            QueueBridgeTest.LOG.info(result.getText());
        }
    }
}

