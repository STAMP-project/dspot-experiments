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
package org.apache.activemq.broker.jmx;


import java.util.concurrent.TimeoutException;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MBeanOperationTimeoutTest {
    private static final Logger LOG = LoggerFactory.getLogger(MBeanOperationTimeoutTest.class);

    private ActiveMQConnectionFactory connectionFactory;

    private BrokerService broker;

    private String connectionUri;

    private static final String destinationName = "MBeanOperationTimeoutTestQ";

    private static final String moveToDestinationName = "MBeanOperationTimeoutTestQ.Moved";

    protected MBeanServer mbeanServer;

    protected String domain = "org.apache.activemq";

    protected int messageCount = 50000;

    @Test(expected = TimeoutException.class)
    public void testLongOperationTimesOut() throws Exception {
        sendMessages(messageCount);
        MBeanOperationTimeoutTest.LOG.info((("Produced " + (messageCount)) + " messages to the broker."));
        // Now get the QueueViewMBean and purge
        String objectNameStr = broker.getBrokerObjectName().toString();
        objectNameStr += ",destinationType=Queue,destinationName=" + (MBeanOperationTimeoutTest.destinationName);
        ObjectName queueViewMBeanName = assertRegisteredObjectName(objectNameStr);
        QueueViewMBean proxy = ((QueueViewMBean) (MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true)));
        long count = proxy.getQueueSize();
        Assert.assertEquals("Queue size", count, messageCount);
        MBeanOperationTimeoutTest.LOG.info("Attempting to move one message, TimeoutException expected");
        proxy.moveMatchingMessagesTo(null, MBeanOperationTimeoutTest.moveToDestinationName);
    }
}

