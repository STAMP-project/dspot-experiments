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


import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test covers the Message Compression feature of the ActiveMQConnectionFactory.setUseCompression
 * and has no relation to Http transport level compression.  The Messages are compressed using the
 * deflate algorithm by the ActiveMQ layer before marshalled to XML so only the Message body will
 * be compressed.
 */
public class HttpSendCompressedMessagesTest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSendCompressedMessagesTest.class);

    private BrokerService broker;

    private static final String tcpBindAddress = "tcp://0.0.0.0:0";

    private static final String httpBindAddress = "http://0.0.0.0:8171";

    private ActiveMQConnectionFactory tcpConnectionFactory;

    private ActiveMQConnectionFactory httpConnectionFactory;

    private ActiveMQConnection tcpConnection;

    private ActiveMQConnection httpConnection;

    private Session tcpSession;

    private Session httpSession;

    private Topic destination;

    private MessageConsumer tcpConsumer;

    private MessageConsumer httpConsumer;

    private static final String destinationName = "HttpCompressionTopic";

    @Test
    public void testTextMessageCompressionFromTcp() throws Exception {
        sendTextMessage(true);
        doTestTextMessageCompression();
    }

    @Test
    public void testTextMessageCompressionFromHttp() throws Exception {
        sendTextMessage(httpConnectionFactory, true);
        doTestTextMessageCompression();
    }

    @Test
    public void testBytesMessageCompressionFromTcp() throws Exception {
        sendBytesMessage(true);
        doTestBytesMessageCompression();
    }

    @Test
    public void testBytesMessageCompressionFromHttp() throws Exception {
        sendBytesMessage(httpConnectionFactory, true);
        doTestBytesMessageCompression();
    }

    @Test
    public void testStreamMessageCompressionFromTcp() throws Exception {
        sendStreamMessage(true);
        doTestStreamMessageCompression();
    }

    @Test
    public void testStreamMessageCompressionFromHttp() throws Exception {
        sendStreamMessage(httpConnectionFactory, true);
        doTestStreamMessageCompression();
    }

    @Test
    public void testMapMessageCompressionFromTcp() throws Exception {
        sendMapMessage(true);
        doTestMapMessageCompression();
    }

    @Test
    public void testMapMessageCompressionFromHttp() throws Exception {
        sendMapMessage(httpConnectionFactory, true);
        doTestMapMessageCompression();
    }
}

