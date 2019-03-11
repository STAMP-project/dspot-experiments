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
package io.openmessaging.rocketmq.consumer;


import Message.BuiltinKeys.MESSAGE_ID;
import NonStandardKeys.MESSAGE_DESTINATION;
import io.openmessaging.Message;
import io.openmessaging.consumer.MessageListener;
import io.openmessaging.consumer.PushConsumer;
import java.util.Collections;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PushConsumerImplTest {
    private PushConsumer consumer;

    @Mock
    private DefaultMQPushConsumer rocketmqPushConsumer;

    @Test
    public void testConsumeMessage() {
        final byte[] testBody = new byte[]{ 'a', 'b' };
        MessageExt consumedMsg = new MessageExt();
        consumedMsg.setMsgId("NewMsgId");
        consumedMsg.setBody(testBody);
        consumedMsg.putUserProperty(MESSAGE_DESTINATION, "TOPIC");
        consumedMsg.setTopic("HELLO_QUEUE");
        consumer.attachQueue("HELLO_QUEUE", new MessageListener() {
            @Override
            public void onReceived(Message message, Context context) {
                assertThat(message.sysHeaders().getString(MESSAGE_ID)).isEqualTo("NewMsgId");
                assertThat(getBody(byte[].class)).isEqualTo(testBody);
                context.ack();
            }
        });
        ((MessageListenerConcurrently) (rocketmqPushConsumer.getMessageListener())).consumeMessage(Collections.singletonList(consumedMsg), null);
    }
}

