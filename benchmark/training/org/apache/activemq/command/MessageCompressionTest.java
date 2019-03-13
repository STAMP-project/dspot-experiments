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
package org.apache.activemq.command;


import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;


public class MessageCompressionTest extends TestCase {
    private static final String BROKER_URL = "tcp://localhost:0";

    // The following text should compress well
    private static final String TEXT = "The quick red fox jumped over the lazy brown dog. " + ((((((((((((((("The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ") + "The quick red fox jumped over the lazy brown dog. ");

    private BrokerService broker;

    private ActiveMQQueue queue;

    private String connectionUri;

    public void testTextMessageCompression() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionUri);
        factory.setUseCompression(true);
        sendTestMessage(factory, MessageCompressionTest.TEXT);
        ActiveMQTextMessage message = receiveTestMessage(factory);
        int compressedSize = message.getContent().getLength();
        factory = new ActiveMQConnectionFactory(connectionUri);
        factory.setUseCompression(false);
        sendTestMessage(factory, MessageCompressionTest.TEXT);
        message = receiveTestMessage(factory);
        int unCompressedSize = message.getContent().getLength();
        TestCase.assertTrue((((("expected: compressed Size '" + compressedSize) + "' < unCompressedSize '") + unCompressedSize) + "'"), (compressedSize < unCompressedSize));
    }

    public void testBytesMessageCompression() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionUri);
        factory.setUseCompression(true);
        sendTestBytesMessage(factory, MessageCompressionTest.TEXT);
        ActiveMQBytesMessage message = receiveTestBytesMessage(factory);
        int compressedSize = message.getContent().getLength();
        byte[] bytes = new byte[MessageCompressionTest.TEXT.getBytes("UTF8").length];
        message.readBytes(bytes);
        TestCase.assertTrue(((message.readBytes(new byte[255])) == (-1)));
        String rcvString = new String(bytes, "UTF8");
        TestCase.assertEquals(MessageCompressionTest.TEXT, rcvString);
        TestCase.assertTrue(message.isCompressed());
        factory = new ActiveMQConnectionFactory(connectionUri);
        factory.setUseCompression(false);
        sendTestBytesMessage(factory, MessageCompressionTest.TEXT);
        message = receiveTestBytesMessage(factory);
        int unCompressedSize = message.getContent().getLength();
        TestCase.assertTrue((((("expected: compressed Size '" + compressedSize) + "' < unCompressedSize '") + unCompressedSize) + "'"), (compressedSize < unCompressedSize));
    }
}

