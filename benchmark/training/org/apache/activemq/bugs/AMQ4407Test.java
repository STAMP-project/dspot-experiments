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


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4407Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ4407Test.class);

    private static final int maxFileLength = (1024 * 1024) * 32;

    private static final String PREFIX_DESTINATION_NAME = "queue";

    private static final String DESTINATION_NAME = (AMQ4407Test.PREFIX_DESTINATION_NAME) + ".test";

    private static final String DESTINATION_NAME_2 = (AMQ4407Test.PREFIX_DESTINATION_NAME) + "2.test";

    private static final String DESTINATION_NAME_3 = (AMQ4407Test.PREFIX_DESTINATION_NAME) + "3.test";

    BrokerService broker;

    @Test
    public void testRestartAfterQueueDelete() throws Exception {
        // Ensure we have an Admin View.
        Assert.assertTrue("Broker doesn't have an Admin View.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView()) != null;
            }
        }));
        AMQ4407Test.LOG.info("Adding destinations: {}, {}, {}", new Object[]{ AMQ4407Test.DESTINATION_NAME, AMQ4407Test.DESTINATION_NAME_3, AMQ4407Test.DESTINATION_NAME_3 });
        sendMessage(AMQ4407Test.DESTINATION_NAME, "test 1");
        sendMessage(AMQ4407Test.DESTINATION_NAME_2, "test 1");
        sendMessage(AMQ4407Test.DESTINATION_NAME_3, "test 1");
        Assert.assertNotNull(broker.getDestination(new ActiveMQQueue(AMQ4407Test.DESTINATION_NAME)));
        Assert.assertNotNull(broker.getDestination(new ActiveMQQueue(AMQ4407Test.DESTINATION_NAME_2)));
        Assert.assertNotNull(broker.getDestination(new ActiveMQQueue(AMQ4407Test.DESTINATION_NAME_3)));
        AMQ4407Test.LOG.info("Removing destination: {}", AMQ4407Test.DESTINATION_NAME_2);
        broker.getAdminView().removeQueue(AMQ4407Test.DESTINATION_NAME_2);
        AMQ4407Test.LOG.info("Recreating destination: {}", AMQ4407Test.DESTINATION_NAME_2);
        sendMessage(AMQ4407Test.DESTINATION_NAME_2, "test 1");
        Destination destination2 = broker.getDestination(new ActiveMQQueue(AMQ4407Test.DESTINATION_NAME_2));
        Assert.assertNotNull(destination2);
        Assert.assertEquals(1, destination2.getMessageStore().getMessageCount());
    }

    @Test
    public void testRemoveOfOneDestFromSharedPa() throws Exception {
        // Ensure we have an Admin View.
        Assert.assertTrue("Broker doesn't have an Admin View.", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView()) != null;
            }
        }));
        // will both use first persistence adapter
        sendMessage("queue.A", "test 1");
        sendMessage("queue.B", "test 1");
        broker.getAdminView().removeQueue("queue.A");
        sendMessage("queue.B", "test 1");
        Destination destination2 = broker.getDestination(new ActiveMQQueue("queue.B"));
        Assert.assertNotNull(destination2);
        Assert.assertEquals(2, destination2.getMessageStore().getMessageCount());
    }
}

