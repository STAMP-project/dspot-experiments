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
package org.apache.activemq.transport.nio;


import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;


@SuppressWarnings("javadoc")
public class NIOSSLWindowSizeTest extends TestCase {
    BrokerService broker;

    Connection connection;

    Session session;

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    public static final int PRODUCER_COUNT = 1;

    public static final int CONSUMER_COUNT = 1;

    public static final int MESSAGE_COUNT = 1;

    public static final int MESSAGE_SIZE = 65536;

    byte[] messageData;

    public void testLargePayload() throws Exception {
        Queue dest = session.createQueue("TEST");
        MessageProducer prod = null;
        try {
            prod = session.createProducer(dest);
            BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(messageData);
            prod.send(msg);
        } finally {
            prod.close();
        }
        MessageConsumer cons = null;
        try {
            cons = session.createConsumer(dest);
            TestCase.assertNotNull(cons.receive(30000L));
        } finally {
            cons.close();
        }
    }
}

