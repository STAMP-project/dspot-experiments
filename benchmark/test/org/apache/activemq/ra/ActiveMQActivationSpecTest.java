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
package org.apache.activemq.ra;


import ActiveMQActivationSpec.AUTO_ACKNOWLEDGE_MODE;
import ActiveMQActivationSpec.DUPS_OK_ACKNOWLEDGE_MODE;
import ActiveMQActivationSpec.DURABLE_SUBSCRIPTION;
import ActiveMQActivationSpec.INVALID_ACKNOWLEDGE_MODE;
import ActiveMQActivationSpec.NON_DURABLE_SUBSCRIPTION;
import Session.AUTO_ACKNOWLEDGE;
import Session.DUPS_OK_ACKNOWLEDGE;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import javax.jms.Queue;
import javax.jms.Topic;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.Assert;
import org.junit.Test;


public class ActiveMQActivationSpecTest {
    private static final String DESTINATION = "defaultQueue";

    private static final String DESTINATION_TYPE = Queue.class.getName();

    private static final String EMPTY_STRING = "   ";

    private ActiveMQActivationSpec activationSpec;

    private PropertyDescriptor destinationProperty;

    private PropertyDescriptor destinationTypeProperty;

    private PropertyDescriptor acknowledgeModeProperty;

    private PropertyDescriptor subscriptionDurabilityProperty;

    private PropertyDescriptor clientIdProperty;

    private PropertyDescriptor subscriptionNameProperty;

    @Test(timeout = 60000)
    public void testDefaultContructionValidation() throws IntrospectionException {
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ destinationTypeProperty, destinationProperty };
        assertActivationSpecInvalid(new ActiveMQActivationSpec(), expected);
    }

    @Test(timeout = 60000)
    public void testMinimalSettings() {
        Assert.assertEquals(ActiveMQActivationSpecTest.DESTINATION, activationSpec.getDestination());
        Assert.assertEquals(ActiveMQActivationSpecTest.DESTINATION_TYPE, activationSpec.getDestinationType());
        assertActivationSpecValid();
    }

    @Test(timeout = 60000)
    public void testNoDestinationTypeFailure() {
        activationSpec.setDestinationType(null);
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ destinationTypeProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testInvalidDestinationTypeFailure() {
        activationSpec.setDestinationType("foobar");
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ destinationTypeProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testQueueDestinationType() {
        activationSpec.setDestinationType(Queue.class.getName());
        assertActivationSpecValid();
    }

    @Test(timeout = 60000)
    public void testTopicDestinationType() {
        activationSpec.setDestinationType(Topic.class.getName());
        assertActivationSpecValid();
    }

    @Test(timeout = 60000)
    public void testSuccessfulCreateQueueDestination() {
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination(ActiveMQActivationSpecTest.DESTINATION);
        assertActivationSpecValid();
        ActiveMQDestination destination = activationSpec.createDestination();
        Assert.assertNotNull("ActiveMQDestination not created", destination);
        Assert.assertEquals("Physical name not the same", activationSpec.getDestination(), destination.getPhysicalName());
        Assert.assertTrue("Destination is not a Queue", (destination instanceof Queue));
    }

    @Test(timeout = 60000)
    public void testSuccessfulCreateTopicDestination() {
        activationSpec.setDestinationType(Topic.class.getName());
        activationSpec.setDestination(ActiveMQActivationSpecTest.DESTINATION);
        assertActivationSpecValid();
        ActiveMQDestination destination = activationSpec.createDestination();
        Assert.assertNotNull("ActiveMQDestination not created", destination);
        Assert.assertEquals("Physical name not the same", activationSpec.getDestination(), destination.getPhysicalName());
        Assert.assertTrue("Destination is not a Topic", (destination instanceof Topic));
    }

    @Test(timeout = 60000)
    public void testCreateDestinationIncorrectType() {
        activationSpec.setDestinationType(null);
        activationSpec.setDestination(ActiveMQActivationSpecTest.DESTINATION);
        ActiveMQDestination destination = activationSpec.createDestination();
        Assert.assertNull("ActiveMQDestination should not have been created", destination);
    }

    @Test(timeout = 60000)
    public void testCreateDestinationIncorrectDestinationName() {
        activationSpec.setDestinationType(Topic.class.getName());
        activationSpec.setDestination(null);
        ActiveMQDestination destination = activationSpec.createDestination();
        Assert.assertNull("ActiveMQDestination should not have been created", destination);
    }

    // ----------- acknowledgeMode tests
    @Test(timeout = 60000)
    public void testDefaultAcknowledgeModeSetCorrectly() {
        Assert.assertEquals("Incorrect default value", AUTO_ACKNOWLEDGE_MODE, activationSpec.getAcknowledgeMode());
        Assert.assertEquals("Incorrect default value", AUTO_ACKNOWLEDGE, activationSpec.getAcknowledgeModeForSession());
    }

    @Test(timeout = 60000)
    public void testInvalidAcknowledgeMode() {
        activationSpec.setAcknowledgeMode("foobar");
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ acknowledgeModeProperty };
        assertActivationSpecInvalid(expected);
        Assert.assertEquals("Incorrect acknowledge mode", INVALID_ACKNOWLEDGE_MODE, activationSpec.getAcknowledgeModeForSession());
    }

    @Test(timeout = 60000)
    public void testNoAcknowledgeMode() {
        activationSpec.setAcknowledgeMode(null);
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ acknowledgeModeProperty };
        assertActivationSpecInvalid(expected);
        Assert.assertEquals("Incorrect acknowledge mode", INVALID_ACKNOWLEDGE_MODE, activationSpec.getAcknowledgeModeForSession());
    }

    @Test(timeout = 60000)
    public void testSettingAutoAcknowledgeMode() {
        activationSpec.setAcknowledgeMode(AUTO_ACKNOWLEDGE_MODE);
        assertActivationSpecValid();
        Assert.assertEquals("Incorrect acknowledge mode", AUTO_ACKNOWLEDGE, activationSpec.getAcknowledgeModeForSession());
    }

    @Test(timeout = 60000)
    public void testSettingDupsOkAcknowledgeMode() {
        activationSpec.setAcknowledgeMode(DUPS_OK_ACKNOWLEDGE_MODE);
        assertActivationSpecValid();
        Assert.assertEquals("Incorrect acknowledge mode", DUPS_OK_ACKNOWLEDGE, activationSpec.getAcknowledgeModeForSession());
    }

    // ----------- subscriptionDurability tests
    @Test(timeout = 60000)
    public void testDefaultSubscriptionDurabilitySetCorrectly() {
        Assert.assertEquals("Incorrect default value", NON_DURABLE_SUBSCRIPTION, activationSpec.getSubscriptionDurability());
    }

    @Test(timeout = 60000)
    public void testInvalidSubscriptionDurability() {
        activationSpec.setSubscriptionDurability("foobar");
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ subscriptionDurabilityProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testNullSubscriptionDurability() {
        activationSpec.setSubscriptionDurability(null);
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ subscriptionDurabilityProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testSettingNonDurableSubscriptionDurability() {
        activationSpec.setSubscriptionDurability(NON_DURABLE_SUBSCRIPTION);
        assertActivationSpecValid();
    }

    // ----------- durable subscriber tests
    @Test(timeout = 60000)
    public void testValidDurableSubscriber() {
        activationSpec.setDestinationType(Topic.class.getName());
        activationSpec.setSubscriptionDurability(DURABLE_SUBSCRIPTION);
        activationSpec.setClientId("foobar");
        activationSpec.setSubscriptionName("foobar");
        assertActivationSpecValid();
        Assert.assertTrue(activationSpec.isDurableSubscription());
    }

    @Test(timeout = 60000)
    public void testDurableSubscriberWithQueueDestinationTypeFailure() {
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setSubscriptionDurability(DURABLE_SUBSCRIPTION);
        activationSpec.setClientId("foobar");
        activationSpec.setSubscriptionName("foobar");
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ subscriptionDurabilityProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testDurableSubscriberNoClientIdNoSubscriptionNameFailure() {
        activationSpec.setDestinationType(Topic.class.getName());
        activationSpec.setSubscriptionDurability(DURABLE_SUBSCRIPTION);
        activationSpec.setClientId(null);
        Assert.assertNull(activationSpec.getClientId());
        activationSpec.setSubscriptionName(null);
        Assert.assertNull(activationSpec.getSubscriptionName());
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ clientIdProperty, subscriptionNameProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testDurableSubscriberEmptyClientIdEmptySubscriptionNameFailure() {
        activationSpec.setDestinationType(Topic.class.getName());
        activationSpec.setSubscriptionDurability(DURABLE_SUBSCRIPTION);
        activationSpec.setClientId(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull(activationSpec.getClientId());
        activationSpec.setSubscriptionName(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull(activationSpec.getSubscriptionName());
        PropertyDescriptor[] expected = new PropertyDescriptor[]{ clientIdProperty, subscriptionNameProperty };
        assertActivationSpecInvalid(expected);
    }

    @Test(timeout = 60000)
    public void testSetEmptyStringButGetNullValue() {
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getDestinationType());
        activationSpec.setMessageSelector(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getMessageSelector());
        activationSpec.setDestination(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getDestination());
        activationSpec.setUserName(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getUserName());
        activationSpec.setPassword(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getPassword());
        activationSpec.setClientId(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getClientId());
        activationSpec.setSubscriptionName(ActiveMQActivationSpecTest.EMPTY_STRING);
        Assert.assertNull("Property not null", activationSpec.getSubscriptionName());
    }
}

