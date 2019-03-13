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


import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class ConsumeJmsBytesMessageTest extends CamelTestSupport {
    protected JmsTemplate jmsTemplate;

    private MockEndpoint endpoint;

    @Test
    public void testConsumeBytesMessage() throws Exception {
        endpoint.expectedMessageCount(1);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.send("test.bytes", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeByte(((byte) (1)));
                bytesMessage.writeByte(((byte) (2)));
                bytesMessage.writeByte(((byte) (3)));
                return bytesMessage;
            }
        });
        endpoint.assertIsSatisfied();
        assertCorrectBytesReceived();
    }

    @Test
    public void testSendBytesMessage() throws Exception {
        endpoint.expectedMessageCount(1);
        byte[] bytes = new byte[]{ 1, 2, 3 };
        template.sendBody("direct:test", bytes);
        endpoint.assertIsSatisfied();
        assertCorrectBytesReceived();
    }
}

