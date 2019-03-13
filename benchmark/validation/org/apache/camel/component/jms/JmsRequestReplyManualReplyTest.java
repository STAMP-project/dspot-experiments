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
package org.apache.camel.component.jms;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class JmsRequestReplyManualReplyTest extends CamelTestSupport {
    private static volatile String tempName;

    private CountDownLatch latch = new CountDownLatch(1);

    private JmsTemplate jms;

    @Test
    public void testManualRequestReply() throws Exception {
        context.start();
        // send using pure JMS API to set a custom JMSReplyTo
        jms.send(new ActiveMQQueue("foo"), new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage msg = session.createTextMessage("Hello World");
                msg.setJMSReplyTo(new ActiveMQQueue("bar"));
                return msg;
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        String reply = consumer.receiveBody(JmsRequestReplyManualReplyTest.tempName, 5000, String.class);
        assertEquals("Bye World", reply);
    }
}

