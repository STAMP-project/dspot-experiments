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
package org.apache.activemq.transport.udp;


import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.transport.TransportServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public abstract class UdpTestSupport extends TestCase implements TransportListener {
    private static final Logger LOG = LoggerFactory.getLogger(UdpTestSupport.class);

    protected Transport producer;

    protected Transport consumer;

    protected Object lock = new Object();

    protected Command receivedCommand;

    protected TransportServer server;

    protected boolean large;

    // You might want to set this to massive number if debugging
    protected int waitForCommandTimeout = 40000;

    public void testSendingSmallMessage() throws Exception {
        ConsumerInfo expected = new ConsumerInfo();
        expected.setSelector("Cheese");
        expected.setExclusive(true);
        expected.setExclusive(true);
        expected.setPrefetchSize(3456);
        try {
            UdpTestSupport.LOG.info(("About to send: " + expected));
            producer.oneway(expected);
            Command received = assertCommandReceived();
            TestCase.assertTrue(("Should have received a ConsumerInfo but was: " + received), (received instanceof ConsumerInfo));
            ConsumerInfo actual = ((ConsumerInfo) (received));
            TestCase.assertEquals("Selector", expected.getSelector(), actual.getSelector());
            TestCase.assertEquals("isExclusive", expected.isExclusive(), actual.isExclusive());
            TestCase.assertEquals("getPrefetchSize", expected.getPrefetchSize(), actual.getPrefetchSize());
        } catch (Exception e) {
            UdpTestSupport.LOG.info(("Caught: " + e));
            e.printStackTrace();
            TestCase.fail(("Failed to send to transport: " + e));
        }
    }

    public void testSendingMediumMessage() throws Exception {
        String text = createMessageBodyText((4 * 105));
        ActiveMQDestination destination = new ActiveMQQueue("Foo.Bar.Medium");
        assertSendTextMessage(destination, text);
    }

    public void testSendingLargeMessage() throws Exception {
        String text = createMessageBodyText((4 * 1024));
        ActiveMQDestination destination = new ActiveMQQueue("Foo.Bar.Large");
        assertSendTextMessage(destination, text);
    }
}

