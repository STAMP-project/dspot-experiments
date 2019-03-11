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
package org.apache.activemq.bugs;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3145Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ3145Test.class);

    private final String MESSAGE_TEXT = new String(new byte[1024]);

    BrokerService broker;

    ConnectionFactory factory;

    Connection connection;

    Session session;

    Queue queue;

    MessageConsumer consumer;

    @Test
    public void testCacheDisableReEnable() throws Exception {
        createProducerAndSendMessages(1);
        QueueViewMBean proxy = getProxyToQueueViewMBean();
        Assert.assertTrue("cache is enabled", proxy.isCacheEnabled());
        tearDown();
        createBroker(false);
        proxy = getProxyToQueueViewMBean();
        Assert.assertEquals("one pending message", 1, proxy.getQueueSize());
        Assert.assertTrue("cache is disabled when there is a pending message", (!(proxy.isCacheEnabled())));
        createConsumer(1);
        createProducerAndSendMessages(1);
        Assert.assertTrue("cache is enabled again on next send when there are no messages", proxy.isCacheEnabled());
    }
}

