/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.broker.filter;


import ExpressionType.SQL92;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;
import org.apache.rocketmq.store.ConsumeQueueExt;
import org.apache.rocketmq.store.DefaultMessageStore;
import org.apache.rocketmq.store.GetMessageResult;
import org.apache.rocketmq.store.MessageExtBrokerInner;
import org.apache.rocketmq.store.MessageFilter;
import org.junit.Test;


public class MessageStoreWithFilterTest {
    private static final String msg = "Once, there was a chance for me!";

    private static final byte[] msgBody = MessageStoreWithFilterTest.msg.getBytes();

    private static final String topic = "topic";

    private static final int queueId = 0;

    private static final String storePath = ("." + (File.separator)) + "unit_test_store";

    private static final int commitLogFileSize = (1024 * 1024) * 256;

    private static final int cqFileSize = 300000 * 20;

    private static final int cqExtFileSize = 300000 * 128;

    private static SocketAddress BornHost;

    private static SocketAddress StoreHost;

    private DefaultMessageStore master;

    private ConsumerFilterManager filterManager;

    private int topicCount = 3;

    private int msgPerTopic = 30;

    static {
        try {
            MessageStoreWithFilterTest.StoreHost = new InetSocketAddress(InetAddress.getLocalHost(), 8123);
        } catch (UnknownHostException e) {
        }
        try {
            MessageStoreWithFilterTest.BornHost = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0);
        } catch (UnknownHostException e) {
        }
    }

    @Test
    public void testGetMessage_withFilterBitMapAndConsumerChanged() throws Exception {
        List<MessageExtBrokerInner> msgs = putMsg(master, topicCount, msgPerTopic);
        Thread.sleep(200);
        // reset consumer;
        String topic = "topic" + 0;
        String resetGroup = "CID_" + 2;
        String normalGroup = "CID_" + 3;
        {
            // reset CID_2@topic0 to get all messages.
            SubscriptionData resetSubData = new SubscriptionData();
            resetSubData.setExpressionType(SQL92);
            resetSubData.setTopic(topic);
            resetSubData.setClassFilterMode(false);
            resetSubData.setSubString("a is not null OR a is null");
            ConsumerFilterData resetFilterData = ConsumerFilterManager.build(topic, resetGroup, resetSubData.getSubString(), resetSubData.getExpressionType(), System.currentTimeMillis());
            GetMessageResult resetGetResult = master.getMessage(resetGroup, topic, MessageStoreWithFilterTest.queueId, 0, 1000, new ExpressionMessageFilter(resetSubData, resetFilterData, filterManager));
            try {
                assertThat(resetGetResult).isNotNull();
                List<MessageExtBrokerInner> filteredMsgs = filtered(msgs, resetFilterData);
                assertThat(resetGetResult.getMessageBufferList().size()).isEqualTo(filteredMsgs.size());
            } finally {
                resetGetResult.release();
            }
        }
        {
            ConsumerFilterData normalFilterData = filterManager.get(topic, normalGroup);
            assertThat(normalFilterData).isNotNull();
            assertThat(normalFilterData.getBornTime()).isLessThan(System.currentTimeMillis());
            SubscriptionData normalSubData = new SubscriptionData();
            normalSubData.setExpressionType(normalFilterData.getExpressionType());
            normalSubData.setTopic(topic);
            normalSubData.setClassFilterMode(false);
            normalSubData.setSubString(normalFilterData.getExpression());
            List<MessageExtBrokerInner> filteredMsgs = filtered(msgs, normalFilterData);
            GetMessageResult normalGetResult = master.getMessage(normalGroup, topic, MessageStoreWithFilterTest.queueId, 0, 1000, new ExpressionMessageFilter(normalSubData, normalFilterData, filterManager));
            try {
                assertThat(normalGetResult).isNotNull();
                assertThat(normalGetResult.getMessageBufferList().size()).isEqualTo(filteredMsgs.size());
            } finally {
                normalGetResult.release();
            }
        }
    }

    @Test
    public void testGetMessage_withFilterBitMap() throws Exception {
        List<MessageExtBrokerInner> msgs = putMsg(master, topicCount, msgPerTopic);
        Thread.sleep(100);
        for (int i = 0; i < (topicCount); i++) {
            String realTopic = (MessageStoreWithFilterTest.topic) + i;
            for (int j = 0; j < (msgPerTopic); j++) {
                String group = "CID_" + j;
                ConsumerFilterData filterData = filterManager.get(realTopic, group);
                assertThat(filterData).isNotNull();
                List<MessageExtBrokerInner> filteredMsgs = filtered(msgs, filterData);
                SubscriptionData subscriptionData = new SubscriptionData();
                subscriptionData.setExpressionType(filterData.getExpressionType());
                subscriptionData.setTopic(filterData.getTopic());
                subscriptionData.setClassFilterMode(false);
                subscriptionData.setSubString(filterData.getExpression());
                GetMessageResult getMessageResult = master.getMessage(group, realTopic, MessageStoreWithFilterTest.queueId, 0, 10000, new ExpressionMessageFilter(subscriptionData, filterData, filterManager));
                String assertMsg = (group + "-") + realTopic;
                try {
                    assertThat(getMessageResult).isNotNull();
                    assertThat(GetMessageStatus.FOUND).isEqualTo(getMessageResult.getStatus());
                    assertThat(getMessageResult.getMessageBufferList()).isNotNull().isNotEmpty();
                    assertThat(getMessageResult.getMessageBufferList().size()).isEqualTo(filteredMsgs.size());
                    for (ByteBuffer buffer : getMessageResult.getMessageBufferList()) {
                        MessageExt messageExt = MessageDecoder.decode(buffer.slice(), false);
                        assertThat(messageExt).isNotNull();
                        Object evlRet = null;
                        try {
                            evlRet = filterData.getCompiledExpression().evaluate(new MessageEvaluationContext(messageExt.getProperties()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            assertThat(true).isFalse();
                        }
                        assertThat(evlRet).isNotNull().isEqualTo(Boolean.TRUE);
                        // check
                        boolean find = false;
                        for (MessageExtBrokerInner messageExtBrokerInner : filteredMsgs) {
                            if (messageExtBrokerInner.getMsgId().equals(messageExt.getMsgId())) {
                                find = true;
                            }
                        }
                        assertThat(find).isTrue();
                    }
                } finally {
                    getMessageResult.release();
                }
            }
        }
    }

    @Test
    public void testGetMessage_withFilter_checkTagsCode() throws Exception {
        putMsg(master, topicCount, msgPerTopic);
        Thread.sleep(200);
        for (int i = 0; i < (topicCount); i++) {
            String realTopic = (MessageStoreWithFilterTest.topic) + i;
            GetMessageResult getMessageResult = master.getMessage("test", realTopic, MessageStoreWithFilterTest.queueId, 0, 10000, new MessageFilter() {
                @Override
                public boolean isMatchedByConsumeQueue(Long tagsCode, ConsumeQueueExt.CqExtUnit cqExtUnit) {
                    if ((tagsCode != null) && (tagsCode <= (ConsumeQueueExt.MAX_ADDR))) {
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean isMatchedByCommitLog(ByteBuffer msgBuffer, Map<String, String> properties) {
                    return true;
                }
            });
            assertThat(getMessageResult.getMessageCount()).isEqualTo(msgPerTopic);
        }
    }
}

