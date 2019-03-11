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
package org.apache.activemq.transport.amqp.interop;


import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.junit.Test;


/**
 * Tests for JMS temporary destination mappings to AMQP
 */
public class AmqpTempDestinationTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testCannotCreateSenderWithNamedTempQueue() throws Exception {
        doTestCannotCreateSenderWithNamedTempDestination(false);
    }

    @Test(timeout = 60000)
    public void testCannotCreateSenderWithNamedTempTopic() throws Exception {
        doTestCannotCreateSenderWithNamedTempDestination(true);
    }

    @Test(timeout = 60000)
    public void testCanntCreateReceverWithNamedTempQueue() throws Exception {
        doTestCannotCreateReceiverWithNamedTempDestination(false);
    }

    @Test(timeout = 60000)
    public void testCannotCreateReceiverWithNamedTempTopic() throws Exception {
        doTestCannotCreateReceiverWithNamedTempDestination(true);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicSenderToTopic() throws Exception {
        doTestCreateDynamicSender(true);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicSenderToQueue() throws Exception {
        doTestCreateDynamicSender(false);
    }

    @Test(timeout = 60000)
    public void testDynamicSenderLifetimeBoundToLinkTopic() throws Exception {
        doTestDynamicSenderLifetimeBoundToLinkQueue(true);
    }

    @Test(timeout = 60000)
    public void testDynamicSenderLifetimeBoundToLinkQueue() throws Exception {
        doTestDynamicSenderLifetimeBoundToLinkQueue(false);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicReceiverToTopic() throws Exception {
        doTestCreateDynamicSender(true);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicReceiverToQueue() throws Exception {
        doTestCreateDynamicSender(false);
    }

    @Test(timeout = 60000)
    public void testDynamicReceiverLifetimeBoundToLinkTopic() throws Exception {
        doTestDynamicReceiverLifetimeBoundToLinkQueue(true);
    }

    @Test(timeout = 60000)
    public void testDynamicReceiverLifetimeBoundToLinkQueue() throws Exception {
        doTestDynamicReceiverLifetimeBoundToLinkQueue(false);
    }

    @Test(timeout = 60000)
    public void TestCreateDynamicQueueSenderAndPublish() throws Exception {
        doTestCreateDynamicSenderAndPublish(false);
    }

    @Test(timeout = 60000)
    public void TestCreateDynamicTopicSenderAndPublish() throws Exception {
        doTestCreateDynamicSenderAndPublish(true);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicReceiverToTopicAndSend() throws Exception {
        doTestCreateDynamicSender(true);
    }

    @Test(timeout = 60000)
    public void testCreateDynamicReceiverToQueueAndSend() throws Exception {
        doTestCreateDynamicSender(false);
    }
}

