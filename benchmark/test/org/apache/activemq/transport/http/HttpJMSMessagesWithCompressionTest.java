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
package org.apache.activemq.transport.http;


import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class HttpJMSMessagesWithCompressionTest {
    private static final AtomicInteger counter = new AtomicInteger(1);

    enum DESTINATION_TYPE {

        TOPIC,
        QUEUE;}

    protected BrokerService broker;

    protected Connection connection;

    protected HttpJMSMessagesWithCompressionTest.DESTINATION_TYPE destinationType = HttpJMSMessagesWithCompressionTest.DESTINATION_TYPE.QUEUE;

    abstract class MessageCommand<M extends Message> {
        public final void assertMessage(M message) throws JMSException {
            Assert.assertNotNull(message);
            completeCheck(message);
        }

        public abstract void completeCheck(M message) throws JMSException;

        public abstract M createMessage(Session session) throws JMSException;
    }

    @Test
    public void testTextMessage() throws Exception {
        executeTest(new HttpJMSMessagesWithCompressionTest.MessageCommand<TextMessage>() {
            private String textString = "This is a simple text string";

            public TextMessage createMessage(Session session) throws JMSException {
                return session.createTextMessage(textString);
            }

            public void completeCheck(TextMessage message) throws JMSException {
                Assert.assertEquals("The returned text string was different", textString, message.getText());
            }
        });
    }

    @Test
    public void testBytesMessage() throws Exception {
        executeTest(new HttpJMSMessagesWithCompressionTest.MessageCommand<BytesMessage>() {
            private byte[] bytes = "This is a simple text string".getBytes();

            public BytesMessage createMessage(Session session) throws JMSException {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(bytes);
                return message;
            }

            public void completeCheck(BytesMessage message) throws JMSException {
                byte[] result = new byte[bytes.length];
                message.readBytes(result);
                Assert.assertArrayEquals("The returned byte array was different", bytes, result);
            }
        });
    }

    @Test
    public void testMapMessage() throws Exception {
        executeTest(new HttpJMSMessagesWithCompressionTest.MessageCommand<MapMessage>() {
            public MapMessage createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setInt("value", 13);
                return message;
            }

            public void completeCheck(MapMessage message) throws JMSException {
                Assert.assertEquals("The returned mapped value was different", 13, message.getInt("value"));
            }
        });
    }

    @Test
    public void testObjectMessage() throws Exception {
        executeTest(new HttpJMSMessagesWithCompressionTest.MessageCommand<ObjectMessage>() {
            private Long value = new Long(101);

            public ObjectMessage createMessage(Session session) throws JMSException {
                return session.createObjectMessage(value);
            }

            public void completeCheck(ObjectMessage message) throws JMSException {
                Assert.assertEquals("The returned object was different", value, message.getObject());
            }
        });
    }

    @Test
    public void testStreamMessage() throws Exception {
        executeTest(new HttpJMSMessagesWithCompressionTest.MessageCommand<StreamMessage>() {
            private Long value = new Long(1013);

            public StreamMessage createMessage(Session session) throws JMSException {
                StreamMessage message = session.createStreamMessage();
                message.writeObject(value);
                return message;
            }

            public void completeCheck(StreamMessage message) throws JMSException {
                Assert.assertEquals("The returned stream object was different", value, message.readObject());
            }
        });
    }
}

