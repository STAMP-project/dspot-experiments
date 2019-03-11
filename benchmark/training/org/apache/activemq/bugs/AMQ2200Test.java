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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.MessageConsumer;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.management.ObjectName;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.TopicSubscriptionViewMBean;
import org.junit.Assert;
import org.junit.Test;


public class AMQ2200Test {
    private static final String bindAddress = "tcp://0.0.0.0:0";

    private BrokerService broker;

    private ActiveMQConnectionFactory cf;

    @Test
    public void testTopicSubscriptionView() throws Exception {
        TopicConnection connection = cf.createTopicConnection();
        TopicSession session = connection.createTopicSession(false, AUTO_ACKNOWLEDGE);
        Topic destination = session.createTopic("TopicViewTestTopic");
        MessageConsumer consumer = session.createConsumer(destination);
        Assert.assertNotNull(consumer);
        TimeUnit.SECONDS.sleep(1);
        ObjectName[] subscriptionNames = broker.getAdminView().getTopicSubscribers();
        Assert.assertTrue(((subscriptionNames.length) > 0));
        boolean fail = true;
        for (ObjectName name : subscriptionNames) {
            if (name.toString().contains("TopicViewTestTopic")) {
                TopicSubscriptionViewMBean sub = ((TopicSubscriptionViewMBean) (broker.getManagementContext().newProxyInstance(name, TopicSubscriptionViewMBean.class, true)));
                Assert.assertNotNull(sub);
                Assert.assertTrue(((sub.getSessionId()) != (-1)));
                // Check that its the default value then configure something new.
                Assert.assertTrue(((sub.getMaximumPendingQueueSize()) == (-1)));
                sub.setMaximumPendingQueueSize(1000);
                Assert.assertTrue(((sub.getMaximumPendingQueueSize()) != (-1)));
                fail = false;
            }
        }
        if (fail) {
            Assert.fail("Didn't find the TopicSubscriptionView");
        }
    }
}

