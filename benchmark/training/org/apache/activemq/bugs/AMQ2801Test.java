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
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2801Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2801Test.class);

    private static final String TOPICNAME = "InvalidPendingQueueTest";

    private static final String SELECTOR1 = "JMS_ID" + ((" = '" + "TEST") + "'");

    private static final String SELECTOR2 = "JMS_ID" + ((" = '" + "TEST2") + "'");

    private static final String SUBSCRIPTION1 = "InvalidPendingQueueTest_1";

    private static final String SUBSCRIPTION2 = "InvalidPendingQueueTest_2";

    private static final int MSG_COUNT = 2500;

    private Session session1;

    private Connection conn1;

    private Topic topic1;

    private MessageConsumer consumer1;

    private Session session2;

    private Connection conn2;

    private Topic topic2;

    private MessageConsumer consumer2;

    private BrokerService broker;

    private String connectionUri;

    @Test
    public void testInvalidPendingQueue() throws Exception {
        activeateSubscribers();
        Assert.assertNotNull(consumer1);
        Assert.assertNotNull(consumer2);
        produceMessages();
        AMQ2801Test.LOG.debug("Sent messages to a single subscriber");
        Thread.sleep(2000);
        AMQ2801Test.LOG.debug("Closing durable subscriber connections");
        conn1.close();
        conn2.close();
        AMQ2801Test.LOG.debug("Closed durable subscriber connections");
        Thread.sleep(2000);
        AMQ2801Test.LOG.debug("Re-starting durable subscriber connections");
        activeateSubscribers();
        AMQ2801Test.LOG.debug("Started up durable subscriber connections - now view activemq console to see pending queue size on the other subscriber");
        ObjectName[] subs = broker.getAdminView().getDurableTopicSubscribers();
        for (int i = 0; i < (subs.length); i++) {
            ObjectName subName = subs[i];
            DurableSubscriptionViewMBean sub = ((DurableSubscriptionViewMBean) (broker.getManagementContext().newProxyInstance(subName, DurableSubscriptionViewMBean.class, true)));
            AMQ2801Test.LOG.info((((((sub.getSubscriptionName()) + ": pending = ") + (sub.getPendingQueueSize())) + ", dispatched: ") + (sub.getDispatchedQueueSize())));
            if (sub.getSubscriptionName().equals(AMQ2801Test.SUBSCRIPTION1)) {
                Assert.assertEquals("Incorrect number of pending messages", AMQ2801Test.MSG_COUNT, ((sub.getPendingQueueSize()) + (sub.getDispatchedQueueSize())));
            } else {
                Assert.assertEquals("Incorrect number of pending messages", 0, sub.getPendingQueueSize());
            }
        }
    }
}

