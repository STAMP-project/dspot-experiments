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
package org.apache.activemq.xbean;


import junit.framework.TestCase;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class XBeanConfigTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(XBeanConfigTest.class);

    protected BrokerService brokerService;

    protected Broker broker;

    protected ConnectionContext context;

    protected ConnectionInfo info;

    public void testBrokerConfiguredCorrectly() throws Exception {
        // Validate the system properties are being evaluated in xbean.
        TestCase.assertEquals("testbroker", brokerService.getBrokerName());
        Topic topic = ((Topic) (broker.addDestination(context, new ActiveMQTopic("FOO.BAR"), true)));
        DispatchPolicy dispatchPolicy = topic.getDispatchPolicy();
        TestCase.assertTrue(("dispatchPolicy should be RoundRobinDispatchPolicy: " + dispatchPolicy), (dispatchPolicy instanceof RoundRobinDispatchPolicy));
        SubscriptionRecoveryPolicy subscriptionRecoveryPolicy = topic.getSubscriptionRecoveryPolicy();
        subscriptionRecoveryPolicy = getWrapped();
        TestCase.assertTrue(("subscriptionRecoveryPolicy should be LastImageSubscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy), (subscriptionRecoveryPolicy instanceof LastImageSubscriptionRecoveryPolicy));
        XBeanConfigTest.LOG.info(("destination: " + topic));
        XBeanConfigTest.LOG.info(("dispatchPolicy: " + dispatchPolicy));
        XBeanConfigTest.LOG.info(("subscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy));
        topic = ((Topic) (broker.addDestination(context, new ActiveMQTopic("ORDERS.BOOKS"), true)));
        dispatchPolicy = topic.getDispatchPolicy();
        TestCase.assertTrue(("dispatchPolicy should be StrictOrderDispatchPolicy: " + dispatchPolicy), (dispatchPolicy instanceof StrictOrderDispatchPolicy));
        subscriptionRecoveryPolicy = topic.getSubscriptionRecoveryPolicy();
        subscriptionRecoveryPolicy = getWrapped();
        TestCase.assertTrue(("subscriptionRecoveryPolicy should be TimedSubscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy), (subscriptionRecoveryPolicy instanceof TimedSubscriptionRecoveryPolicy));
        TimedSubscriptionRecoveryPolicy timedSubscriptionPolicy = ((TimedSubscriptionRecoveryPolicy) (subscriptionRecoveryPolicy));
        TestCase.assertEquals("getRecoverDuration()", 60000, timedSubscriptionPolicy.getRecoverDuration());
        XBeanConfigTest.LOG.info(("destination: " + topic));
        XBeanConfigTest.LOG.info(("dispatchPolicy: " + dispatchPolicy));
        XBeanConfigTest.LOG.info(("subscriptionRecoveryPolicy: " + subscriptionRecoveryPolicy));
    }
}

