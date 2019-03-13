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
package org.apache.activemq.broker.policy;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.IllegalStateException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;


/**
 * This unit test is to test that setting the property "maxDestinations" on
 * PolicyEntry works correctly. If this property is set, it will limit the
 * number of destinations that can be created. Advisory topics will be ignored
 * during calculations.
 */
public class MaxDestinationsPolicyTest {
    BrokerService broker;

    ConnectionFactory factory;

    Connection connection;

    Session session;

    MessageProducer producer;

    /**
     * Test that 10 queues can be created when default policy allows it.
     */
    @Test
    public void testMaxDestinationDefaultPolicySuccess() throws Exception {
        applyDefaultMaximumDestinationPolicy(10);
        for (int i = 0; i < 10; i++) {
            createQueue(("queue." + i));
        }
    }

    /**
     * Test that default policy prevents going beyond max
     */
    @Test(expected = IllegalStateException.class)
    public void testMaxDestinationDefaultPolicyFail() throws Exception {
        applyDefaultMaximumDestinationPolicy(10);
        for (int i = 0; i < 11; i++) {
            createQueue(("queue." + i));
        }
    }

    /**
     * Test that a queue policy overrides the default policy
     */
    @Test(expected = IllegalStateException.class)
    public void testMaxDestinationOnQueuePolicy() throws Exception {
        PolicyMap policyMap = applyDefaultMaximumDestinationPolicy(10);
        applyMaximumDestinationPolicy(policyMap, new ActiveMQQueue("queue.>"), 5);
        // This should fail even though the default policy is set to a limit of
        // 10 because the
        // queue policy overrides it
        for (int i = 0; i < 6; i++) {
            createQueue(("queue." + i));
        }
    }

    /**
     * Test that 10 topics can be created when default policy allows it.
     */
    @Test
    public void testTopicMaxDestinationDefaultPolicySuccess() throws Exception {
        applyDefaultMaximumDestinationPolicy(10);
        for (int i = 0; i < 10; i++) {
            createTopic(("topic." + i));
        }
    }

    /**
     * Test that topic creation will faill when exceeding the limit
     */
    @Test(expected = IllegalStateException.class)
    public void testTopicMaxDestinationDefaultPolicyFail() throws Exception {
        applyDefaultMaximumDestinationPolicy(20);
        for (int i = 0; i < 21; i++) {
            createTopic(("topic." + i));
        }
    }

    /**
     * Test that no limit is enforced
     */
    @Test
    public void testTopicDefaultPolicyNoMaxDestinations() throws Exception {
        // -1 is the default and signals no max destinations
        applyDefaultMaximumDestinationPolicy((-1));
        for (int i = 0; i < 100; i++) {
            createTopic(("topic." + i));
        }
    }

    /**
     * Test a mixture of queue and topic policies
     */
    @Test
    public void testComplexMaxDestinationPolicy() throws Exception {
        PolicyMap policyMap = applyMaximumDestinationPolicy(new PolicyMap(), new ActiveMQQueue("queue.>"), 5);
        applyMaximumDestinationPolicy(policyMap, new ActiveMQTopic("topic.>"), 10);
        for (int i = 0; i < 5; i++) {
            createQueue(("queue." + i));
        }
        for (int i = 0; i < 10; i++) {
            createTopic(("topic." + i));
        }
        // Make sure that adding one more of either a topic or a queue fails
        boolean fail = false;
        try {
            createTopic("topic.test");
        } catch (javax.jms e) {
            fail = true;
        }
        Assert.assertTrue(fail);
        fail = false;
        try {
            createQueue("queue.test");
        } catch (javax.jms e) {
            fail = true;
        }
        Assert.assertTrue(fail);
    }

    /**
     * Test child destinations of a policy
     */
    @Test
    public void testMaxDestinationPolicyOnChildDests() throws Exception {
        applyMaximumDestinationPolicy(new PolicyMap(), new ActiveMQTopic("topic.>"), 10);
        for (int i = 0; i < 10; i++) {
            createTopic(("topic.test" + i));
        }
        // Make sure that adding one more fails
        boolean fail = false;
        try {
            createTopic("topic.abc.test");
        } catch (javax.jms e) {
            fail = true;
        }
        Assert.assertTrue(fail);
    }

    /**
     * Test a topic policy overrides the default
     */
    @Test(expected = IllegalStateException.class)
    public void testMaxDestinationOnTopicPolicy() throws Exception {
        PolicyMap policyMap = applyDefaultMaximumDestinationPolicy(10);
        applyMaximumDestinationPolicy(policyMap, new ActiveMQTopic("topic.>"), 5);
        // This should fail even though the default policy is set to a limit of
        // 10 because the
        // queue policy overrides it
        for (int i = 0; i < 6; i++) {
            createTopic(("topic." + i));
        }
    }
}

