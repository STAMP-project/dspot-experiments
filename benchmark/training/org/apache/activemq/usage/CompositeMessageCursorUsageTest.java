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
package org.apache.activemq.usage;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class CompositeMessageCursorUsageTest extends TestCase {
    BrokerService broker;

    public void testCompositeMessageUsage() throws Exception {
        String compositeQueue = "compositeA,compositeB";
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost");
        JmsTemplate jt = new JmsTemplate(cf);
        jt.send(compositeQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage tm = session.createTextMessage();
                tm.setText("test");
                return tm;
            }
        });
        jt.send("noCompositeA", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage tm = session.createTextMessage();
                tm.setText("test");
                return tm;
            }
        });
        jt.send("noCompositeB", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage tm = session.createTextMessage();
                tm.setText("test");
                return tm;
            }
        });
        TestCase.assertEquals("Cursor memory usage wrong for 'noCompositeA' queue", 1032, getQueueView("noCompositeA").getCursorMemoryUsage());
        TestCase.assertEquals("Cursor memory usage wrong for 'noCompositeB' queue", 1032, getQueueView("noCompositeB").getCursorMemoryUsage());
        TestCase.assertEquals("Cursor memory usage wrong for 'CompositeA' queue", 1032, getQueueView("compositeA").getCursorMemoryUsage());
        TestCase.assertEquals("Cursor memory usage wrong for 'CompositeB' queue", 1032, getQueueView("compositeB").getCursorMemoryUsage());
    }
}

