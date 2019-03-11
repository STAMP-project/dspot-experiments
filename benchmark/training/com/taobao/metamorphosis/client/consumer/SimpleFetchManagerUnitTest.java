/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.client.consumer;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.MessageAccessor;
import com.taobao.metamorphosis.client.consumer.SimpleFetchManager.FetchRequestRunner;
import com.taobao.metamorphosis.cluster.Broker;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.consumer.ConsumerMessageFilter;
import com.taobao.metamorphosis.consumer.MessageIterator;
import com.taobao.metamorphosis.utils.MessageUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class SimpleFetchManagerUnitTest {
    private SimpleFetchManager fetchManager;

    private ConsumerConfig consumerConfig;

    private InnerConsumer consumer;

    @Test
    public void testProcessRequestNormal() throws Exception {
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final int msgId = 1111;
        final byte[] data = MessageUtils.makeMessageBuffer(msgId, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        final AtomicReference<Message> msg = new AtomicReference<Message>();
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                msg.set(message);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final FetchRequest newRequest = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, (offset + (data.length)), msgId), maxSize);
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals("hello", new String(msg.get().getData()));
        Assert.assertEquals(msgId, msg.get().getId());
        Assert.assertEquals(topic, msg.get().getTopic());
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestNormalWithFilter() throws Exception {
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final int msgId = 1111;
        final byte[] data = MessageUtils.makeMessageBuffer(msgId, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        final AtomicReference<Message> msg = new AtomicReference<Message>();
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                msg.set(message);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        EasyMock.expect(this.consumer.getMessageFilter(topic)).andReturn(new ConsumerMessageFilter() {
            @Override
            public boolean accept(String group, Message message) {
                return true;
            }
        });
        EasyMock.expect(this.consumer.getConsumerConfig()).andReturn(new ConsumerConfig("test"));
        final FetchRequest newRequest = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, (offset + (data.length)), msgId), maxSize);
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals("hello", new String(msg.get().getData()));
        Assert.assertEquals(msgId, msg.get().getId());
        Assert.assertEquals(topic, msg.get().getTopic());
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestWithMessageFilterNotAccept() throws Exception {
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final int msgId = 1111;
        final byte[] data = MessageUtils.makeMessageBuffer(msgId, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        final AtomicReference<Message> msg = new AtomicReference<Message>();
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        // Don't accept all messages.
        EasyMock.expect(this.consumer.getMessageFilter(topic)).andReturn(new ConsumerMessageFilter() {
            @Override
            public boolean accept(String group, Message message) {
                // always return false.
                return false;
            }
        });
        EasyMock.expect(this.consumer.getConsumerConfig()).andReturn(new ConsumerConfig("test"));
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertNull(msg.get());
    }

    @Test
    public void testProcessRequestWithMessageFilterThrowException() throws Exception {
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final int msgId = 1111;
        final byte[] data = MessageUtils.makeMessageBuffer(msgId, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        final AtomicReference<Message> msg = new AtomicReference<Message>();
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        // Don't accept all messages.
        EasyMock.expect(this.consumer.getMessageFilter(topic)).andReturn(new ConsumerMessageFilter() {
            @Override
            public boolean accept(String group, Message message) {
                throw new RuntimeException();
            }
        });
        EasyMock.expect(this.consumer.getConsumerConfig()).andReturn(new ConsumerConfig("test"));
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertNull(msg.get());
    }

    @Test
    public void testProcessRequestInvalidMessage() throws Exception {
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final int msgId = 1111;
        final byte[] data = SimpleFetchManagerUnitTest.makeInvalidMessageBuffer(msgId, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        // query offset
        final long newOffset = 13;
        EasyMock.expect(this.consumer.offset(request)).andReturn(newOffset);
        // Use new offset
        final FetchRequest newRequest = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, newOffset, (-1)), maxSize);
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestRetryTooMany() throws Exception {
        // ?????????????????
        this.consumerConfig.setMaxFetchRetries(0);
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final byte[] data = MessageUtils.makeMessageBuffer(1111, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        request.incrementRetriesAndGet();
        Assert.assertEquals(1, request.getRetries());
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        // EasyMock.expect(this.fetchManager.isRetryTooMany(request)).andReturn(true);
        final Message message = new Message(topic, "hello".getBytes());
        MessageAccessor.setId(message, 1111);
        this.consumer.appendCouldNotProcessMessage(message);
        EasyMock.expectLastCall();
        // offset???????????????????
        final FetchRequest newRequest = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, (offset + (data.length)), 1111), maxSize);
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        // retry??????
        Assert.assertEquals(0, request.getRetries());
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestDelayed() throws Exception {
        // this.mockConsumerReInitializeFetchManager();
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(null);
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final FetchRequest newRequest = new FetchRequest(broker, ((this.consumerConfig.getMaxDelayFetchTimeInMills()) / 10), new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestException() throws Exception {
        // this.mockConsumerReInitializeFetchManager();
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final byte[] data = MessageUtils.makeMessageBuffer(1111, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                throw new RuntimeException("A stupid bug");
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final FetchRequest newRequest = new FetchRequest(broker, ((this.consumerConfig.getMaxDelayFetchTimeInMills()) / 10), new TopicPartitionRegInfo(topic, partition, offset, 1111), maxSize);
        newRequest.incrementRetriesAndGet();
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestMessageRollbackOnly() throws Exception {
        // this.mockConsumerReInitializeFetchManager();
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final byte[] data = MessageUtils.makeMessageBuffer(1111, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), "hello".getBytes(), null, 0, 0)).array();
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, data));
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                System.out.println("Rollback current message");
                message.setRollbackOnly();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final FetchRequest newRequest = new FetchRequest(broker, ((this.consumerConfig.getMaxDelayFetchTimeInMills()) / 10), new TopicPartitionRegInfo(topic, partition, offset, 1111), maxSize);
        newRequest.incrementRetriesAndGet();
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }

    @Test
    public void testProcessRequestDelayed_IncreaseMaxSize() throws Exception {
        // this.mockConsumerReInitializeFetchManager();
        // ??????????????
        this.consumerConfig.setMaxIncreaseFetchDataRetries(0);
        final String topic = "topic1";
        final int maxSize = 1024;
        final Partition partition = new Partition("0-0");
        final long offset = 12;
        final Broker broker = new Broker(0, "meta://localhost:0");
        final FetchRequest request = new FetchRequest(broker, 0, new TopicPartitionRegInfo(topic, partition, offset), maxSize);
        // ??????????????0??
        request.incrementRetriesAndGet();
        final FetchRequestRunner runner = this.fetchManager.new FetchRequestRunner();
        EasyMock.expect(this.consumer.fetch(request, (-1), null)).andReturn(new MessageIterator(topic, new byte[10]));
        EasyMock.expect(this.consumer.getMessageListener(topic)).andReturn(new MessageListener() {
            @Override
            public void recieveMessages(final Message message) {
                Assert.fail();
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        this.mockFilterAndGroup(topic);
        final int newMaxSize = maxSize * 2;
        final FetchRequest newRequest = new FetchRequest(broker, ((this.consumerConfig.getMaxDelayFetchTimeInMills()) / 10), new TopicPartitionRegInfo(topic, partition, offset), newMaxSize);
        newRequest.incrementRetriesAndGet();
        newRequest.incrementRetriesAndGet();
        EasyMock.replay(this.consumer);
        runner.processRequest(request);
        EasyMock.verify(this.consumer);
        Assert.assertEquals(newRequest, this.fetchManager.takeFetchRequest());
    }
}

