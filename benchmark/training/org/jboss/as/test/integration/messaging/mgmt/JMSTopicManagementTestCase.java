/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.messaging.mgmt;


import ModelType.STRING;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the management API for JMS topics.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
@RunAsClient
@RunWith(Arquillian.class)
public class JMSTopicManagementTestCase {
    private static final String EXPORTED_PREFIX = "java:jboss/exported/";

    private static long count = System.currentTimeMillis();

    private ConnectionFactory cf;

    @ContainerResource
    private ManagementClient managementClient;

    @ContainerResource
    private Context remoteContext;

    private JMSOperations adminSupport;

    private Connection conn;

    private Topic topic;

    private Session session;

    private Connection consumerConn;

    private Session consumerSession;

    @Test
    public void testListMessagesForSubscription() throws Exception {
        consumerSession.createDurableSubscriber(topic, "testListMessagesForSubscription", null, false);
        consumerSession.close();
        consumerSession = null;
        MessageProducer producer = session.createProducer(topic);
        producer.send(session.createTextMessage("A"));
        producer.send(session.createTextMessage("B"));
        // session.commit();
        ModelNode result = execute(getTopicOperation("list-all-subscriptions"), true);
        final ModelNode subscriber = result.asList().get(0);
        // System.out.println(result);
        ModelNode operation = getTopicOperation("list-messages-for-subscription");
        operation.get("queue-name").set(subscriber.get("queueName"));
        result = execute(operation, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(2, result.asList().size());
    }

    @Test
    public void testCountMessagesForSubscription() throws Exception {
        consumerSession.createDurableSubscriber(topic, "testCountMessagesForSubscription", null, false);
        consumerSession.close();
        consumerSession = null;
        MessageProducer producer = session.createProducer(topic);
        producer.send(session.createTextMessage("A"));
        producer.send(session.createTextMessage("B"));
        // session.commit();
        ModelNode result = execute(getTopicOperation("list-all-subscriptions"), true);
        final ModelNode subscriber = result.asList().get(0);
        ModelNode operation = getTopicOperation("count-messages-for-subscription");
        operation.get("client-id").set(subscriber.get("clientID"));
        operation.get("subscription-name").set("testCountMessagesForSubscription");
        result = execute(operation, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(2, result.asInt());
    }

    @Test
    public void testListAllSubscriptions() throws Exception {
        session.createDurableSubscriber(topic, "testListAllSubscriptions", "foo=bar", false);
        session.createConsumer(topic);
        final ModelNode result = execute(getTopicOperation("list-all-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(2, result.asList().size());
    }

    @Test
    public void testListAllSubscriptionsAsJSON() throws Exception {
        session.createDurableSubscriber(topic, "testListAllSubscriptionsAsJSON", "foo=bar", false);
        session.createConsumer(topic);
        final ModelNode result = execute(getTopicOperation("list-all-subscriptions-as-json"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(STRING, result.getType());
    }

    @Test
    public void testListDurableSubscriptions() throws Exception {
        session.createDurableSubscriber(topic, "testListDurableSubscriptions", "foo=bar", false);
        session.createConsumer(topic);
        final ModelNode result = execute(getTopicOperation("list-durable-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asList().size());
    }

    @Test
    public void testListDurableSubscriptionsAsJSON() throws Exception {
        session.createDurableSubscriber(topic, "testListDurableSubscriptionsAsJSON", "foo=bar", false);
        session.createConsumer(topic);
        final ModelNode result = execute(getTopicOperation("list-durable-subscriptions-as-json"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(STRING, result.getType());
    }

    @Test
    public void testListNonDurableSubscriptions() throws Exception {
        session.createDurableSubscriber(topic, "testListNonDurableSubscriptions", "foo=bar", false);
        session.createConsumer(topic, "foo=bar", false);
        final ModelNode result = execute(getTopicOperation("list-non-durable-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asList().size());
    }

    @Test
    public void testListNonDurableSubscriptionsAsJSON() throws Exception {
        session.createDurableSubscriber(topic, "testListNonDurableSubscriptionsAsJSON", "foo=bar", false);
        session.createConsumer(topic, "foo=bar", false);
        final ModelNode result = execute(getTopicOperation("list-non-durable-subscriptions-as-json"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(STRING, result.getType());
    }

    @Test
    public void testDropDurableSubscription() throws Exception {
        consumerSession.createDurableSubscriber(topic, "testDropDurableSubscription", "foo=bar", false);
        consumerSession.close();
        consumerSession = null;
        ModelNode result = execute(getTopicOperation("list-durable-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asList().size());
        ModelNode op = getTopicOperation("drop-durable-subscription");
        op.get("client-id").set("consumer");
        op.get("subscription-name").set("testDropDurableSubscription");
        result = execute(op, true);
        Assert.assertFalse(result.isDefined());
        result = execute(getTopicOperation("list-durable-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(0, result.asList().size());
    }

    @Test
    public void testDropAllSubscription() throws Exception {
        consumerSession.createDurableSubscriber(topic, "testDropAllSubscription", "foo=bar", false);
        consumerSession.createDurableSubscriber(topic, "testDropAllSubscription2", null, false);
        consumerSession.close();
        consumerSession = null;
        ModelNode result = execute(getTopicOperation("list-all-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(2, result.asList().size());
        result = execute(getTopicOperation("drop-all-subscriptions"), true);
        Assert.assertFalse(result.isDefined());
        result = execute(getTopicOperation("list-all-subscriptions"), true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(0, result.asList().size());
    }

    @Test
    public void testAddJndi() throws Exception {
        String jndiName = "topic/added" + (JMSTopicManagementTestCase.count);
        ModelNode op = getTopicOperation("add-jndi");
        op.get("jndi-binding").set(jndiName);
        ModelNode result = execute(op, true);
        Assert.assertFalse(result.isDefined());
        op = getTopicOperation("read-attribute");
        op.get("name").set("entries");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        for (ModelNode binding : result.asList()) {
            if (binding.asString().equals(jndiName)) {
                return;
            }
        }
        Assert.fail((jndiName + " was not found"));
    }

    @Test
    public void testRemoveJndi() throws Exception {
        String jndiName = "topic/added" + (JMSTopicManagementTestCase.count);
        ModelNode op = getTopicOperation("add-jndi");
        op.get("jndi-binding").set(jndiName);
        ModelNode result = execute(op, true);
        Assert.assertFalse(result.isDefined());
        op = getTopicOperation("remove-jndi");
        op.get("jndi-binding").set(jndiName);
        result = execute(op, true);
        Assert.assertFalse(result.isDefined());
        op = getTopicOperation("read-attribute");
        op.get("name").set("entries");
        result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        for (ModelNode binding : result.asList()) {
            if (binding.asString().equals(jndiName)) {
                Assert.fail((("found " + jndiName) + " while it must be removed"));
            }
        }
    }

    @Test
    public void testRemoveLastJndi() throws Exception {
        ModelNode op = getTopicOperation("remove-jndi");
        op.get("jndi-binding").set(((JMSTopicManagementTestCase.EXPORTED_PREFIX) + (getTopicJndiName())));
        // removing the last jndi name must generate a failure
        execute(op, false);
        op = getTopicOperation("read-attribute");
        op.get("name").set("entries");
        ModelNode result = execute(op, true);
        Assert.assertTrue(result.isDefined());
        for (ModelNode binding : result.asList()) {
            if (binding.asString().equals(((JMSTopicManagementTestCase.EXPORTED_PREFIX) + (getTopicJndiName())))) {
                return;
            }
        }
        Assert.fail(((getTopicJndiName()) + " was not found"));
    }

    @Test
    public void removeJMSTopicRemovesAllMessages() throws Exception {
        // create a durable subscriber
        final String subscriptionName = "removeJMSTopicRemovesAllMessages";
        // stop the consumer connection to prevent eager consumption of messages
        consumerConn.stop();
        TopicSubscriber consumer = consumerSession.createDurableSubscriber(topic, subscriptionName);
        MessageProducer producer = session.createProducer(topic);
        producer.send(session.createTextMessage("A"));
        TextMessage message = ((TextMessage) (consumer.receive(TimeoutUtil.adjust(500))));
        Assert.assertNull("The message was received by the consumer, this is wrong as the connection is stopped", message);
        ModelNode operation = getTopicOperation("count-messages-for-subscription");
        operation.get("client-id").set(consumerConn.getClientID());
        operation.get("subscription-name").set(subscriptionName);
        ModelNode result = execute(operation, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(1, result.asInt());
        // remove the topic
        adminSupport.removeJmsTopic(getTopicName());
        // add the topic
        adminSupport.createJmsTopic(getTopicName(), getTopicJndiName());
        // and recreate the durable subscriber to check all the messages have
        // been removed from the topic
        consumerSession.createDurableSubscriber(topic, subscriptionName);
        result = execute(operation, true);
        Assert.assertTrue(result.isDefined());
        Assert.assertEquals(0, result.asInt());
    }
}

