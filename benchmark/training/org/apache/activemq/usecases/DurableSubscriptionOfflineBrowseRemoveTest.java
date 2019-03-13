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
package org.apache.activemq.usecases;


import Session.AUTO_ACKNOWLEDGE;
import java.util.LinkedList;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.activemq.TestSupport.PersistenceAdapterChoice.JDBC;


@RunWith(Parameterized.class)
public class DurableSubscriptionOfflineBrowseRemoveTest extends DurableSubscriptionOfflineTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubscriptionOfflineBrowseRemoveTest.class);

    public static final String IDENTITY = "milly";

    public boolean keepDurableSubsActive;

    public DurableSubscriptionOfflineBrowseRemoveTest(TestSupport.PersistenceAdapterChoice adapter, boolean keepDurableSubsActive) {
        this.defaultPersistenceAdapter = adapter;
        this.usePrioritySupport = true;
        this.keepDurableSubsActive = keepDurableSubsActive;
    }

    @Test(timeout = 60 * 1000)
    public void testBrowseRemoveBrowseOfflineSub() throws Exception {
        // create durable subscription
        Connection con = createConnection();
        Session session = con.createSession(false, AUTO_ACKNOWLEDGE);
        session.createDurableSubscriber(topic, "SubsId");
        session.close();
        con.close();
        // send messages
        con = createConnection();
        session = con.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(null);
        for (int i = 0; i < 10; i++) {
            Message message = session.createMessage();
            message.setStringProperty("filter", "true");
            producer.send(topic, message);
        }
        session.close();
        con.close();
        // browse the durable sub
        ObjectName[] subs = broker.getAdminView().getInactiveDurableTopicSubscribers();
        Assert.assertEquals(1, subs.length);
        ObjectName subName = subs[0];
        DurableSubscriptionViewMBean sub = ((DurableSubscriptionViewMBean) (broker.getManagementContext().newProxyInstance(subName, DurableSubscriptionViewMBean.class, true)));
        CompositeData[] data = sub.browse();
        Assert.assertNotNull(data);
        Assert.assertEquals(10, data.length);
        LinkedList<String> idToRemove = new LinkedList<>();
        idToRemove.add(((String) (data[5].get("JMSMessageID"))));
        idToRemove.add(((String) (data[9].get("JMSMessageID"))));
        idToRemove.add(((String) (data[0].get("JMSMessageID"))));
        DurableSubscriptionOfflineBrowseRemoveTest.LOG.info(("Removing: " + idToRemove));
        for (String id : idToRemove) {
            sub.removeMessage(id);
        }
        if ((defaultPersistenceAdapter.compareTo(JDBC)) == 0) {
            for (int i = 0; i < 10; i++) {
                // each iteration does one priority
                cleanup();
            }
        }
        data = sub.browse();
        Assert.assertNotNull(data);
        Assert.assertEquals(7, data.length);
        for (CompositeData c : data) {
            String id = ((String) (c.get("JMSMessageID")));
            for (String removedId : idToRemove) {
                Assert.assertNotEquals(id, removedId);
            }
        }
        // remove non existent
        DurableSubscriptionOfflineBrowseRemoveTest.LOG.info(("Repeat remove: " + (idToRemove.getFirst())));
        sub.removeMessage(idToRemove.getFirst());
    }
}

