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
package org.apache.rocketmq.store;


import GetMessageStatus.FOUND;
import MessageDecoder.CHARSET_UTF8;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageExtBatch;
import org.junit.Test;


public class BatchPutMessageTest {
    private MessageStore messageStore;

    public static final char NAME_VALUE_SEPARATOR = 1;

    public static final char PROPERTY_SEPARATOR = 2;

    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Test
    public void testPutMessages() throws Exception {
        List<Message> messages = new ArrayList<>();
        String topic = "batch-write-topic";
        int queue = 0;
        int[] msgLengthArr = new int[11];
        msgLengthArr[0] = 0;
        int j = 1;
        for (int i = 0; i < 10; i++) {
            Message msg = new Message();
            msg.setBody(("body" + i).getBytes());
            msg.setTopic(topic);
            msg.setTags("TAG1");
            msg.setKeys(String.valueOf(System.currentTimeMillis()));
            messages.add(msg);
            String properties = messageProperties2String(msg.getProperties());
            byte[] propertiesBytes = properties.getBytes(BatchPutMessageTest.CHARSET_UTF8);
            short propertiesLength = ((short) (propertiesBytes.length));
            final byte[] topicData = msg.getTopic().getBytes(MessageDecoder.CHARSET_UTF8);
            final int topicLength = topicData.length;
            msgLengthArr[j] = (calMsgLength(msg.getBody().length, topicLength, propertiesLength)) + (msgLengthArr[(j - 1)]);
            j++;
        }
        byte[] batchMessageBody = MessageDecoder.encodeMessages(messages);
        MessageExtBatch messageExtBatch = new MessageExtBatch();
        messageExtBatch.setTopic(topic);
        messageExtBatch.setQueueId(queue);
        messageExtBatch.setBody(batchMessageBody);
        messageExtBatch.setBornTimestamp(System.currentTimeMillis());
        messageExtBatch.setStoreHost(new InetSocketAddress("127.0.0.1", 125));
        messageExtBatch.setBornHost(new InetSocketAddress("127.0.0.1", 126));
        PutMessageResult putMessageResult = messageStore.putMessages(messageExtBatch);
        assertThat(putMessageResult.isOk()).isTrue();
        Thread.sleep((3 * 1000));
        for (long i = 0; i < 10; i++) {
            MessageExt messageExt = messageStore.lookMessageByOffset(msgLengthArr[((int) (i))]);
            assertThat(messageExt).isNotNull();
            GetMessageResult result = messageStore.getMessage("batch_write_group", topic, queue, i, (1024 * 1024), null);
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(FOUND);
            result.release();
        }
    }

    private class MyMessageArrivingListener implements MessageArrivingListener {
        @Override
        public void arriving(String topic, int queueId, long logicOffset, long tagsCode, long msgStoreTime, byte[] filterBitMap, Map<String, String> properties) {
        }
    }
}

