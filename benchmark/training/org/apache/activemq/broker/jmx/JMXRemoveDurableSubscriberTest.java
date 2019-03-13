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


import javax.jms.InvalidDestinationException;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Makes sure a durable subscriber can be added and deleted from the
 * brokerServer.getAdminView() when JAAS authentication and authorization are
 * setup
 */
public class JMXRemoveDurableSubscriberTest {
    private static final Logger LOG = LoggerFactory.getLogger(JMXRemoveDurableSubscriberTest.class);

    private BrokerService brokerService;

    /**
     * Creates a durable subscription via the AdminView
     */
    @Test(timeout = 60000)
    public void testCreateDurableSubsciber() throws Exception {
        String clientId = "10";
        // Add a topic called test topic
        brokerService.getAdminView().addTopic("testTopic");
        boolean createSubscriberSecurityException = false;
        String subscriberName = "testSubscriber";
        // Create a durable subscriber with the name testSubscriber
        try {
            brokerService.getAdminView().createDurableSubscriber(clientId, subscriberName, "testTopic", null);
            JMXRemoveDurableSubscriberTest.LOG.info((("Successfully created durable subscriber " + subscriberName) + " via AdminView"));
        } catch (SecurityException se1) {
            if (se1.getMessage().equals("User is not authenticated.")) {
                createSubscriberSecurityException = true;
            }
        }
        Assert.assertFalse(createSubscriberSecurityException);
        // Delete the durable subscriber that was created earlier.
        boolean destroySubscriberSecurityException = false;
        try {
            brokerService.getAdminView().destroyDurableSubscriber(clientId, subscriberName);
            JMXRemoveDurableSubscriberTest.LOG.info((("Successfully destroyed durable subscriber " + subscriberName) + " via AdminView"));
        } catch (SecurityException se2) {
            if (se2.getMessage().equals("User is not authenticated.")) {
                destroySubscriberSecurityException = true;
            }
        }
        Assert.assertFalse(destroySubscriberSecurityException);
        // Just to make sure the subscriber was actually deleted, try deleting
        // the subscriber again
        // and that should throw an exception
        boolean subscriberAlreadyDeleted = false;
        try {
            brokerService.getAdminView().destroyDurableSubscriber(clientId, subscriberName);
            JMXRemoveDurableSubscriberTest.LOG.info((("Successfully destroyed durable subscriber " + subscriberName) + " via AdminView"));
        } catch (javax.jms t) {
            if (t.getMessage().equals("No durable subscription exists for clientID: 10 and subscriptionName: testSubscriber")) {
                subscriberAlreadyDeleted = true;
            }
        }
        Assert.assertTrue(subscriberAlreadyDeleted);
    }
}

