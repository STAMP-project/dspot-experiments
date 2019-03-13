/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.jms;


import Session.SESSION_TRANSACTED;
import com.google.common.base.Optional;
import java.util.List;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static JMSDestinationType.QUEUE;
import static JMSDestinationType.TOPIC;


public class TestJMSMessageConsumer extends JMSMessageConsumerTestBase {
    @Test(expected = FlumeException.class)
    public void testCreateConnectionFails() throws Exception {
        Mockito.when(connectionFactory.createConnection(JMSMessageConsumerTestBase.USERNAME, JMSMessageConsumerTestBase.PASSWORD)).thenThrow(new JMSException(""));
        create();
    }

    @Test
    public void testCreateSessionFails() throws Exception {
        Mockito.when(connection.createSession(true, SESSION_TRANSACTED)).thenThrow(new JMSException(""));
        try {
            create();
            Assert.fail("Expected exception: org.apache.flume.FlumeException");
        } catch (FlumeException e) {
            Mockito.verify(connection).close();
        }
    }

    @Test
    public void testCreateQueueFails() throws Exception {
        Mockito.when(session.createQueue(destinationName)).thenThrow(new JMSException(""));
        try {
            create();
            Assert.fail("Expected exception: org.apache.flume.FlumeException");
        } catch (FlumeException e) {
            Mockito.verify(session).close();
            Mockito.verify(connection).close();
        }
    }

    @Test
    public void testCreateTopicFails() throws Exception {
        destinationType = TOPIC;
        Mockito.when(session.createTopic(destinationName)).thenThrow(new JMSException(""));
        try {
            create();
            Assert.fail("Expected exception: org.apache.flume.FlumeException");
        } catch (FlumeException e) {
            Mockito.verify(session).close();
            Mockito.verify(connection).close();
        }
    }

    @Test
    public void testCreateConsumerFails() throws Exception {
        Mockito.when(session.createConsumer(ArgumentMatchers.any(Destination.class), ArgumentMatchers.anyString())).thenThrow(new JMSException(""));
        try {
            create();
            Assert.fail("Expected exception: org.apache.flume.FlumeException");
        } catch (FlumeException e) {
            Mockito.verify(session).close();
            Mockito.verify(connection).close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBatchSizeZero() throws Exception {
        batchSize = 0;
        create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPollTime() throws Exception {
        pollTimeout = -1L;
        create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBatchSizeNegative() throws Exception {
        batchSize = -1;
        create();
    }

    @Test
    public void testQueue() throws Exception {
        destinationType = QUEUE;
        Mockito.when(session.createQueue(destinationName)).thenReturn(queue);
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(session, Mockito.never()).createTopic(ArgumentMatchers.anyString());
    }

    @Test
    public void testTopic() throws Exception {
        destinationType = TOPIC;
        Mockito.when(session.createTopic(destinationName)).thenReturn(topic);
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
        Mockito.verify(session, Mockito.never()).createQueue(ArgumentMatchers.anyString());
    }

    @Test
    public void testUserPass() throws Exception {
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
    }

    @Test
    public void testNoUserPass() throws Exception {
        userName = Optional.absent();
        Mockito.when(connectionFactory.createConnection(JMSMessageConsumerTestBase.USERNAME, JMSMessageConsumerTestBase.PASSWORD)).thenThrow(new AssertionError());
        Mockito.when(connectionFactory.createConnection()).thenReturn(connection);
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(batchSize, events.size());
        assertBodyIsExpected(events);
    }

    @Test
    public void testNoEvents() throws Exception {
        Mockito.when(messageConsumer.receive(ArgumentMatchers.anyLong())).thenReturn(null);
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(0, events.size());
        Mockito.verify(messageConsumer, Mockito.times(1)).receive(ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(messageConsumer);
    }

    @Test
    public void testSingleEvent() throws Exception {
        Mockito.when(messageConsumer.receiveNoWait()).thenReturn(null);
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(1, events.size());
        assertBodyIsExpected(events);
    }

    @Test
    public void testPartialBatch() throws Exception {
        Mockito.when(messageConsumer.receiveNoWait()).thenReturn(message, ((Message) (null)));
        consumer = create();
        List<Event> events = consumer.take();
        Assert.assertEquals(2, events.size());
        assertBodyIsExpected(events);
    }

    @Test
    public void testCommit() throws Exception {
        consumer = create();
        consumer.commit();
        Mockito.verify(session, Mockito.times(1)).commit();
    }

    @Test
    public void testRollback() throws Exception {
        consumer = create();
        consumer.rollback();
        Mockito.verify(session, Mockito.times(1)).rollback();
    }

    @Test
    public void testClose() throws Exception {
        Mockito.doThrow(new JMSException("")).when(session).close();
        consumer = create();
        consumer.close();
        Mockito.verify(session, Mockito.times(1)).close();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testCreateDurableSubscription() throws Exception {
        String name = "SUBSCRIPTION_NAME";
        String clientID = "CLIENT_ID";
        TopicSubscriber mockTopicSubscriber = Mockito.mock(TopicSubscriber.class);
        Mockito.when(session.createDurableSubscriber(ArgumentMatchers.any(Topic.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(mockTopicSubscriber);
        Mockito.when(session.createTopic(destinationName)).thenReturn(topic);
        new JMSMessageConsumer(JMSMessageConsumerTestBase.WONT_USE, connectionFactory, destinationName, destinationLocator, TOPIC, messageSelector, batchSize, pollTimeout, converter, userName, password, Optional.of(clientID), true, name);
        Mockito.verify(connection, Mockito.times(1)).setClientID(clientID);
        Mockito.verify(session, Mockito.times(1)).createDurableSubscriber(topic, name, messageSelector, true);
    }

    @Test(expected = JMSException.class)
    public void testTakeFailsDueToJMSExceptionFromReceive() throws JMSException {
        Mockito.when(messageConsumer.receive(ArgumentMatchers.anyLong())).thenThrow(new JMSException(""));
        consumer = create();
        consumer.take();
    }

    @Test(expected = JMSException.class)
    public void testTakeFailsDueToRuntimeExceptionFromReceive() throws JMSException {
        Mockito.when(messageConsumer.receive(ArgumentMatchers.anyLong())).thenThrow(new RuntimeException());
        consumer = create();
        consumer.take();
    }

    @Test(expected = JMSException.class)
    public void testTakeFailsDueToJMSExceptionFromReceiveNoWait() throws JMSException {
        Mockito.when(messageConsumer.receiveNoWait()).thenThrow(new JMSException(""));
        consumer = create();
        consumer.take();
    }

    @Test(expected = JMSException.class)
    public void testTakeFailsDueToRuntimeExceptionFromReceiveNoWait() throws JMSException {
        Mockito.when(messageConsumer.receiveNoWait()).thenThrow(new RuntimeException());
        consumer = create();
        consumer.take();
    }

    @Test
    public void testCommitFailsDueToJMSException() throws JMSException {
        Mockito.doThrow(new JMSException("")).when(session).commit();
        consumer = create();
        consumer.commit();
    }

    @Test
    public void testCommitFailsDueToRuntimeException() throws JMSException {
        Mockito.doThrow(new RuntimeException()).when(session).commit();
        consumer = create();
        consumer.commit();
    }

    @Test
    public void testRollbackFailsDueToJMSException() throws JMSException {
        Mockito.doThrow(new JMSException("")).when(session).rollback();
        consumer = create();
        consumer.rollback();
    }

    @Test
    public void testRollbackFailsDueToRuntimeException() throws JMSException {
        Mockito.doThrow(new RuntimeException()).when(session).rollback();
        consumer = create();
        consumer.rollback();
    }
}

