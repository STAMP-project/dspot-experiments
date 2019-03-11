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
package com.taobao.metamorphosis.client.producer;


import com.taobao.gecko.core.util.OpaqueGenerator;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.RemotingClientWrapper;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.exception.InvalidMessageException;
import com.taobao.metamorphosis.exception.MetaClientException;
import com.taobao.metamorphosis.exception.TransactionInProgressException;
import com.taobao.metamorphosis.network.BooleanCommand;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.TransactionInfo.TransactionType;
import com.taobao.metamorphosis.utils.CheckSum;
import com.taobao.metamorphosis.utils.MessageFlagUtils;
import com.taobao.metamorphosis.utils.MessageUtils;
import java.util.concurrent.TimeUnit;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class SimpleMessageProducerUnitTest {
    private SimpleMessageProducer producer;

    private ProducerZooKeeper producerZooKeeper;

    private PartitionSelector partitionSelector;

    private RemotingClientWrapper remotingClient;

    private IMocksControl mocksControl;

    private final String sessionId = "testSession";

    @Test
    public void testSetTransactionRequestTimeout() {
        Assert.assertEquals(5000L, this.producer.getTransactionRequestTimeoutInMills());
        this.producer.setTransactionRequestTimeout(3, TimeUnit.SECONDS);
        Assert.assertEquals(3000L, this.producer.getTransactionRequestTimeoutInMills());
    }

    @Test
    public void testSendInvalidMessage() throws Exception {
        try {
            this.producer.sendMessage(null);
            Assert.fail();
        } catch (final InvalidMessageException e) {
            Assert.assertEquals("Null message", e.getMessage());
        }
        try {
            this.producer.sendMessage(new Message(null, "hello".getBytes()));
            Assert.fail();
        } catch (final InvalidMessageException e) {
            Assert.assertEquals("Blank topic", e.getMessage());
        }
        try {
            this.producer.sendMessage(new Message("topic", null));
            Assert.fail();
        } catch (final InvalidMessageException e) {
            Assert.assertEquals("Null data", e.getMessage());
        }
    }

    @Test
    public void testSendMessageNormal_NoPartitions() throws Exception {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final Message message = new Message(topic, data);
        EasyMock.expect(this.producerZooKeeper.selectPartition(topic, message, this.partitionSelector)).andReturn(null);
        this.mocksControl.replay();
        try {
            this.producer.sendMessage(message);
            Assert.fail();
        } catch (final MetaClientException e) {
        }
        this.mocksControl.verify();
    }

    @Test
    public void testSendMessageNormal_NoBroker() throws Exception {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final Message message = new Message(topic, data);
        final Partition partition = new Partition("0-0");
        EasyMock.expect(this.producerZooKeeper.selectPartition(topic, message, this.partitionSelector)).andReturn(partition);
        EasyMock.expect(this.producerZooKeeper.selectBroker(topic, partition)).andReturn(null);
        this.mocksControl.replay();
        try {
            this.producer.sendMessage(new Message(topic, data));
            Assert.fail();
        } catch (final MetaClientException e) {
            // e.printStackTrace();
        }
        this.mocksControl.verify();
    }

    @Test
    public void testSendOrderedMessageServerError() throws Exception {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final Message message = new Message(topic, data);
        final String url = "meta://localhost:0";
        final Partition partition = new Partition("0-0");
        // ???????3??
        EasyMock.expect(this.producerZooKeeper.selectPartition(topic, message, this.partitionSelector)).andReturn(partition);// .times(3);

        EasyMock.expect(this.producerZooKeeper.selectBroker(topic, partition)).andReturn(url);// .times(3);

        OpaqueGenerator.resetOpaque();
        final int flag = MessageFlagUtils.getFlag(null);
        EasyMock.expect(this.remotingClient.invokeToGroup(url, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), data, flag, CheckSum.crc32(data), null, Integer.MIN_VALUE), 3000, TimeUnit.MILLISECONDS)).andReturn(new BooleanCommand(500, "server error", Integer.MIN_VALUE));
        // EasyMock.expect(
        // this.remotingClient.invokeToGroup(url, new PutCommand(topic,
        // partition.getPartition(), data, null, flag,
        // Integer.MIN_VALUE + 1), 3000, TimeUnit.MILLISECONDS)).andReturn(
        // new BooleanCommand(Integer.MIN_VALUE, 500, "server error"));
        // EasyMock.expect(
        // this.remotingClient.invokeToGroup(url, new PutCommand(topic,
        // partition.getPartition(), data, null, flag,
        // Integer.MIN_VALUE + 2), 3000, TimeUnit.MILLISECONDS)).andReturn(
        // new BooleanCommand(Integer.MIN_VALUE, 500, "server error"));
        this.mocksControl.replay();
        Assert.assertEquals(0, message.getId());
        final SendResult sendResult = this.producer.sendMessage(message);
        this.mocksControl.verify();
        Assert.assertFalse(sendResult.isSuccess());
        Assert.assertEquals((-1), sendResult.getOffset());
        Assert.assertNull(sendResult.getPartition());
        Assert.assertEquals("server error", sendResult.getErrorMessage());
    }

    @Test
    public void testSendMessageInterrupted() throws Exception {
        boolean interrupted = false;
        try {
            final String topic = "topic1";
            final byte[] data = "hello".getBytes();
            final Message message = new Message(topic, data);
            final String url = "meta://localhost:0";
            final Partition partition = new Partition("0-0");
            EasyMock.expect(this.producerZooKeeper.selectPartition(topic, message, this.partitionSelector)).andReturn(partition);
            EasyMock.expect(this.producerZooKeeper.selectBroker(topic, partition)).andReturn(url);
            OpaqueGenerator.resetOpaque();
            final int flag = MessageFlagUtils.getFlag(null);
            EasyMock.expect(this.remotingClient.invokeToGroup(url, new com.taobao.metamorphosis.network.PutCommand(topic, partition.getPartition(), data, flag, CheckSum.crc32(data), null, Integer.MIN_VALUE), 3000, TimeUnit.MILLISECONDS)).andThrow(new InterruptedException());
            this.mocksControl.replay();
            this.producer.sendMessage(message);
        } catch (final InterruptedException e) {
            interrupted = true;
        }
        this.mocksControl.verify();
        Assert.assertTrue(interrupted);
    }

    @Test
    public void testBeginTransactionCommit() throws Exception {
        this.producer.beginTransaction();
        final String serverUrl = "meta://localhost:8123";
        this.mockIsConnected(serverUrl, true);
        this.mockInvokeSuccess(serverUrl, new com.taobao.metamorphosis.transaction.TransactionInfo(new LocalTransactionId(this.sessionId, 1), this.sessionId, TransactionType.BEGIN), null);
        this.mockInvokeSuccess(serverUrl, new com.taobao.metamorphosis.transaction.TransactionInfo(new LocalTransactionId(this.sessionId, 1), this.sessionId, TransactionType.COMMIT_ONE_PHASE), null);
        this.mocksControl.replay();
        OpaqueGenerator.resetOpaque();
        this.producer.beforeSendMessageFirstTime(serverUrl);
        Assert.assertTrue(this.producer.isInTransaction());
        this.producer.commit();
        this.mocksControl.verify();
        Assert.assertFalse(this.producer.isInTransaction());
    }

    @Test
    public void testBeginTransactionRollback() throws Exception {
        this.producer.beginTransaction();
        final String serverUrl = "meta://localhost:8123";
        this.mockIsConnected(serverUrl, true);
        this.mockInvokeSuccess(serverUrl, new com.taobao.metamorphosis.transaction.TransactionInfo(new LocalTransactionId(this.sessionId, 1), this.sessionId, TransactionType.BEGIN), null);
        this.mockInvokeSuccess(serverUrl, new com.taobao.metamorphosis.transaction.TransactionInfo(new LocalTransactionId(this.sessionId, 1), this.sessionId, TransactionType.ROLLBACK), null);
        this.mocksControl.replay();
        OpaqueGenerator.resetOpaque();
        this.producer.beforeSendMessageFirstTime(serverUrl);
        Assert.assertTrue(this.producer.isInTransaction());
        this.producer.rollback();
        this.mocksControl.verify();
        Assert.assertFalse(this.producer.isInTransaction());
    }

    @Test(expected = TransactionInProgressException.class)
    public void testBeginTwice() throws Exception {
        this.producer.beginTransaction();
        this.producer.beginTransaction();
        Assert.fail();
    }

    @Test(expected = MetaClientException.class)
    public void testCommitUnBegin() throws Exception {
        this.producer.commit();
        Assert.fail();
    }

    @Test(expected = MetaClientException.class)
    public void tesRollbackUnBegin() throws Exception {
        this.producer.rollback();
        Assert.fail();
    }

    @Test
    public void testEncodeData_NoAttribute() {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final Message message = new Message(topic, data);
        final byte[] encoded = MessageUtils.encodePayload(message);
        Assert.assertEquals("hello", new String(encoded));
    }

    @Test
    public void testEncodeData_HasAttribute() throws Exception {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final String attribute = "attribute";
        final Message message = new Message(topic, data, attribute);
        final byte[] encoded = MessageUtils.encodePayload(message);
        Assert.assertEquals(((4 + (attribute.length())) + (data.length)), encoded.length);
        Assert.assertEquals(attribute.length(), MessageUtils.getInt(0, encoded));
        Assert.assertEquals(attribute, new String(encoded, 4, attribute.length()));
        Assert.assertEquals("hello", new String(encoded, (4 + (attribute.length())), data.length));
    }

    @Test
    public void testEncodeData_EmptyAttribute() throws Exception {
        final String topic = "topic1";
        final byte[] data = "hello".getBytes();
        final String attribute = "";
        final Message message = new Message(topic, data, attribute);
        final byte[] encoded = MessageUtils.encodePayload(message);
        Assert.assertEquals(((4 + (attribute.length())) + (data.length)), encoded.length);
        Assert.assertEquals(attribute.length(), MessageUtils.getInt(0, encoded));
        Assert.assertEquals(attribute, new String(encoded, 4, attribute.length()));
        Assert.assertEquals("hello", new String(encoded, (4 + (attribute.length())), data.length));
    }
}

