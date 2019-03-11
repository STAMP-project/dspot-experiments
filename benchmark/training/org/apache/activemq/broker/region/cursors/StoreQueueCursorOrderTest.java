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
package org.apache.activemq.broker.region.cursors;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.AbstractMessageStore;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.usage.SystemUsage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StoreQueueCursorOrderTest {
    private static final Logger LOG = LoggerFactory.getLogger(StoreQueueCursorOrderTest.class);

    ActiveMQQueue destination = new ActiveMQQueue(("queue-" + (StoreQueueCursorOrderTest.class.getSimpleName())));

    BrokerService brokerService;

    static final String mesageIdRoot = "11111:22222:0:";

    final int messageBytesSize = 1024;

    final String text = new String(new byte[messageBytesSize]);

    @Test
    public void tesBlockedFuture() throws Exception {
        final int count = 2;
        final Message[] messages = new Message[count];
        final StoreQueueCursorOrderTest.TestMessageStore queueMessageStore = new StoreQueueCursorOrderTest.TestMessageStore(messages, destination);
        final ConsumerInfo consumerInfo = new ConsumerInfo();
        final DestinationStatistics destinationStatistics = new DestinationStatistics();
        consumerInfo.setExclusive(true);
        final Queue queue = new Queue(brokerService, destination, queueMessageStore, destinationStatistics, null);
        start();
        registerIndexListener(null);
        QueueStorePrefetch underTest = new QueueStorePrefetch(queue, brokerService.getBroker());
        SystemUsage systemUsage = new SystemUsage();
        // ensure memory limit is reached
        systemUsage.getMemoryUsage().setLimit(((messageBytesSize) * 1));
        underTest.setSystemUsage(systemUsage);
        underTest.setEnableAudit(false);
        underTest.start();
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        ActiveMQTextMessage msg = getMessage(0);
        messages[1] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        FutureTask<Long> future = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
            }
        }, 2L) {};
        msg.getMessageId().setFutureOrSequenceLong(future);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        // second message will flip the cache but will be stored before the future task
        msg = getMessage(1);
        messages[0] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(1L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache is disabled as limit reached", (!(underTest.isCacheEnabled())));
        Assert.assertEquals("setBatch unset", 0L, queueMessageStore.batch.get());
        int dequeueCount = 0;
        underTest.setMaxBatchSize(2);
        underTest.reset();
        while ((underTest.hasNext()) && (dequeueCount < count)) {
            MessageReference ref = underTest.next();
            ref.decrementReferenceCount();
            underTest.remove();
            StoreQueueCursorOrderTest.LOG.info("Received message: {} with body: {}", ref.getMessageId(), getText());
            Assert.assertEquals((dequeueCount++), ref.getMessageId().getProducerSequenceId());
        } 
        underTest.release();
        Assert.assertEquals(count, dequeueCount);
    }

    @Test
    public void testNoSetBatchWithUnOrderedFutureCurrentSync() throws Exception {
        final int count = 2;
        final Message[] messages = new Message[count];
        final StoreQueueCursorOrderTest.TestMessageStore queueMessageStore = new StoreQueueCursorOrderTest.TestMessageStore(messages, destination);
        final ConsumerInfo consumerInfo = new ConsumerInfo();
        final DestinationStatistics destinationStatistics = new DestinationStatistics();
        consumerInfo.setExclusive(true);
        final Queue queue = new Queue(brokerService, destination, queueMessageStore, destinationStatistics, null);
        start();
        registerIndexListener(null);
        QueueStorePrefetch underTest = new QueueStorePrefetch(queue, brokerService.getBroker());
        SystemUsage systemUsage = new SystemUsage();
        // ensure memory limit is reached
        systemUsage.getMemoryUsage().setLimit(((messageBytesSize) * 1));
        underTest.setSystemUsage(systemUsage);
        underTest.setEnableAudit(false);
        underTest.start();
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        ActiveMQTextMessage msg = getMessage(0);
        messages[1] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        final ActiveMQTextMessage msgRef = msg;
        FutureTask<Long> future = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
                msgRef.getMessageId().setFutureOrSequenceLong(1L);
            }
        }, 1L) {};
        msg.getMessageId().setFutureOrSequenceLong(future);
        Executors.newSingleThreadExecutor().submit(future);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        // second message will flip the cache but will be stored before the future task
        msg = getMessage(1);
        messages[0] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(0L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache is disabled as limit reached", (!(underTest.isCacheEnabled())));
        Assert.assertEquals("setBatch unset", 0L, queueMessageStore.batch.get());
        int dequeueCount = 0;
        underTest.setMaxBatchSize(2);
        underTest.reset();
        while ((underTest.hasNext()) && (dequeueCount < count)) {
            MessageReference ref = underTest.next();
            ref.decrementReferenceCount();
            underTest.remove();
            StoreQueueCursorOrderTest.LOG.info("Received message: {} with body: {}", ref.getMessageId(), getText());
            Assert.assertEquals((dequeueCount++), ref.getMessageId().getProducerSequenceId());
        } 
        underTest.release();
        Assert.assertEquals(count, dequeueCount);
    }

    @Test
    public void testSetBatchWithOrderedFutureCurrentFuture() throws Exception {
        final int count = 2;
        final Message[] messages = new Message[count];
        final StoreQueueCursorOrderTest.TestMessageStore queueMessageStore = new StoreQueueCursorOrderTest.TestMessageStore(messages, destination);
        final ConsumerInfo consumerInfo = new ConsumerInfo();
        final DestinationStatistics destinationStatistics = new DestinationStatistics();
        consumerInfo.setExclusive(true);
        final Queue queue = new Queue(brokerService, destination, queueMessageStore, destinationStatistics, null);
        start();
        registerIndexListener(null);
        QueueStorePrefetch underTest = new QueueStorePrefetch(queue, brokerService.getBroker());
        SystemUsage systemUsage = new SystemUsage();
        // ensure memory limit is reached
        systemUsage.getMemoryUsage().setLimit(((messageBytesSize) * 1));
        underTest.setSystemUsage(systemUsage);
        underTest.setEnableAudit(false);
        underTest.start();
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        ActiveMQTextMessage msg = getMessage(0);
        messages[0] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        final ActiveMQTextMessage msgRef = msg;
        FutureTask<Long> future = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
                msgRef.getMessageId().setFutureOrSequenceLong(0L);
            }
        }, 0L) {};
        msg.getMessageId().setFutureOrSequenceLong(future);
        Executors.newSingleThreadExecutor().submit(future);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        // second message will flip the cache but will be stored before the future task
        msg = getMessage(1);
        messages[1] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        final ActiveMQTextMessage msgRe2f = msg;
        FutureTask<Long> future2 = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
                msgRe2f.getMessageId().setFutureOrSequenceLong(1L);
            }
        }, 1L) {};
        msg.getMessageId().setFutureOrSequenceLong(future2);
        Executors.newSingleThreadExecutor().submit(future2);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache is disabled as limit reached", (!(underTest.isCacheEnabled())));
        Assert.assertEquals("setBatch set", 1L, queueMessageStore.batch.get());
        int dequeueCount = 0;
        underTest.setMaxBatchSize(2);
        underTest.reset();
        while ((underTest.hasNext()) && (dequeueCount < count)) {
            MessageReference ref = underTest.next();
            ref.decrementReferenceCount();
            underTest.remove();
            StoreQueueCursorOrderTest.LOG.info("Received message: {} with body: {}", ref.getMessageId(), getText());
            Assert.assertEquals((dequeueCount++), ref.getMessageId().getProducerSequenceId());
        } 
        underTest.release();
        Assert.assertEquals(count, dequeueCount);
    }

    @Test
    public void testSetBatchWithFuture() throws Exception {
        final int count = 4;
        final Message[] messages = new Message[count];
        final StoreQueueCursorOrderTest.TestMessageStore queueMessageStore = new StoreQueueCursorOrderTest.TestMessageStore(messages, destination);
        final ConsumerInfo consumerInfo = new ConsumerInfo();
        final DestinationStatistics destinationStatistics = new DestinationStatistics();
        consumerInfo.setExclusive(true);
        final Queue queue = new Queue(brokerService, destination, queueMessageStore, destinationStatistics, null);
        start();
        registerIndexListener(null);
        QueueStorePrefetch underTest = new QueueStorePrefetch(queue, brokerService.getBroker());
        SystemUsage systemUsage = new SystemUsage();
        // ensure memory limit is reached
        systemUsage.getMemoryUsage().setLimit(((messageBytesSize) * (count + 6)));
        underTest.setSystemUsage(systemUsage);
        underTest.setEnableAudit(false);
        underTest.start();
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        ActiveMQTextMessage msg = getMessage(0);
        messages[0] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        final ActiveMQTextMessage msgRef = msg;
        FutureTask<Long> future0 = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
                msgRef.getMessageId().setFutureOrSequenceLong(0L);
            }
        }, 0L) {};
        msg.getMessageId().setFutureOrSequenceLong(future0);
        underTest.addMessageLast(msg);
        Executors.newSingleThreadExecutor().submit(future0);
        msg = getMessage(1);
        messages[3] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.setRecievedByDFBridge(true);
        final ActiveMQTextMessage msgRef1 = msg;
        FutureTask<Long> future1 = new FutureTask<Long>(new Runnable() {
            @Override
            public void run() {
                msgRef1.getMessageId().setFutureOrSequenceLong(3L);
            }
        }, 3L) {};
        msg.getMessageId().setFutureOrSequenceLong(future1);
        underTest.addMessageLast(msg);
        msg = getMessage(2);
        messages[1] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(1L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        // out of order future
        Executors.newSingleThreadExecutor().submit(future1);
        // sync add to flip cache
        msg = getMessage(3);
        messages[2] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(2L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache is disabled as limit reached", (!(underTest.isCacheEnabled())));
        Assert.assertEquals("setBatch set", 2L, queueMessageStore.batch.get());
        int dequeueCount = 0;
        underTest.setMaxBatchSize(count);
        underTest.reset();
        while ((underTest.hasNext()) && (dequeueCount < count)) {
            MessageReference ref = underTest.next();
            ref.decrementReferenceCount();
            underTest.remove();
            StoreQueueCursorOrderTest.LOG.info("Received message: {} with body: {}", ref.getMessageId(), getText());
            Assert.assertEquals((dequeueCount++), ref.getMessageId().getProducerSequenceId());
        } 
        underTest.release();
        Assert.assertEquals(count, dequeueCount);
        msg = getMessage(4);
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(4L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled on empty store", underTest.isCacheEnabled());
    }

    @Test
    public void testSetBatch() throws Exception {
        final int count = 3;
        final Message[] messages = new Message[count];
        final StoreQueueCursorOrderTest.TestMessageStore queueMessageStore = new StoreQueueCursorOrderTest.TestMessageStore(messages, destination);
        final ConsumerInfo consumerInfo = new ConsumerInfo();
        final DestinationStatistics destinationStatistics = new DestinationStatistics();
        consumerInfo.setExclusive(true);
        final Queue queue = new Queue(brokerService, destination, queueMessageStore, destinationStatistics, null);
        start();
        registerIndexListener(null);
        QueueStorePrefetch underTest = new QueueStorePrefetch(queue, brokerService.getBroker());
        SystemUsage systemUsage = new SystemUsage();
        // ensure memory limit is reached
        systemUsage.getMemoryUsage().setLimit(((messageBytesSize) * 5));
        underTest.setSystemUsage(systemUsage);
        underTest.setEnableAudit(false);
        underTest.start();
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        ActiveMQTextMessage msg = getMessage(0);
        messages[0] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(0L);
        underTest.addMessageLast(msg);
        msg = getMessage(1);
        messages[1] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(1L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache enabled", ((underTest.isUseCache()) && (underTest.isCacheEnabled())));
        msg = getMessage(2);
        messages[2] = msg;
        msg.setMemoryUsage(systemUsage.getMemoryUsage());
        msg.getMessageId().setFutureOrSequenceLong(2L);
        underTest.addMessageLast(msg);
        Assert.assertTrue("cache is disabled as limit reached", (!(underTest.isCacheEnabled())));
        Assert.assertEquals("setBatch set", 2L, queueMessageStore.batch.get());
        int dequeueCount = 0;
        underTest.setMaxBatchSize(2);
        underTest.reset();
        while ((underTest.hasNext()) && (dequeueCount < count)) {
            MessageReference ref = underTest.next();
            ref.decrementReferenceCount();
            underTest.remove();
            StoreQueueCursorOrderTest.LOG.info("Received message: {} with body: {}", ref.getMessageId(), getText());
            Assert.assertEquals((dequeueCount++), ref.getMessageId().getProducerSequenceId());
        } 
        underTest.release();
        Assert.assertEquals(count, dequeueCount);
    }

    class TestMessageStore extends AbstractMessageStore {
        final Message[] messages;

        public AtomicLong batch = new AtomicLong();

        public TestMessageStore(Message[] messages, ActiveMQDestination dest) {
            super(dest);
            this.messages = messages;
        }

        @Override
        public void addMessage(ConnectionContext context, Message message) throws IOException {
        }

        @Override
        public Message getMessage(MessageId identity) throws IOException {
            return null;
        }

        @Override
        public void removeMessage(ConnectionContext context, MessageAck ack) throws IOException {
        }

        @Override
        public void removeAllMessages(ConnectionContext context) throws IOException {
        }

        @Override
        public void recover(MessageRecoveryListener container) throws Exception {
        }

        @Override
        public void resetBatching() {
        }

        @Override
        public void recoverNextMessages(int maxReturned, MessageRecoveryListener listener) throws Exception {
            for (int i = batch.intValue(); i < (messages.length); i++) {
                StoreQueueCursorOrderTest.LOG.info(("recovered index:" + i));
                listener.recoverMessage(messages[i]);
            }
        }

        @Override
        public void setBatch(MessageId message) {
            batch.set(((Long) (message.getFutureOrSequenceLong())));
            batch.incrementAndGet();
        }

        @Override
        public void recoverMessageStoreStatistics() throws IOException {
            getMessageStoreStatistics().reset();
        }
    }
}

