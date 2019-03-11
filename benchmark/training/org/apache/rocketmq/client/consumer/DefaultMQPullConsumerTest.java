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
package org.apache.rocketmq.client.consumer;


import PullStatus.FOUND;
import PullStatus.NO_NEW_MSG;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.impl.CommunicationMode;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.header.PullMessageRequestHeader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQPullConsumerTest {
    @Spy
    private MQClientInstance mQClientFactory = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    @Mock
    private MQClientAPIImpl mQClientAPIImpl;

    private DefaultMQPullConsumer pullConsumer;

    private String consumerGroup = "FooBarGroup";

    private String topic = "FooBar";

    private String brokerName = "BrokerA";

    @Test
    public void testStart_OffsetShouldNotNUllAfterStart() {
        Assert.assertNotNull(pullConsumer.getOffsetStore());
    }

    @Test
    public void testPullMessage_Success() throws Exception {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                PullMessageRequestHeader requestHeader = mock.getArgument(1);
                return createPullResult(requestHeader, FOUND, Collections.singletonList(new MessageExt()));
            }
        }).when(mQClientAPIImpl).pullMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(PullMessageRequestHeader.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CommunicationMode.class), ArgumentMatchers.nullable(PullCallback.class));
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, 0);
        PullResult pullResult = pullConsumer.pull(messageQueue, "*", 1024, 3);
        assertThat(pullResult).isNotNull();
        assertThat(pullResult.getPullStatus()).isEqualTo(FOUND);
        assertThat(pullResult.getNextBeginOffset()).isEqualTo((1024 + 1));
        assertThat(pullResult.getMinOffset()).isEqualTo(123);
        assertThat(pullResult.getMaxOffset()).isEqualTo(2048);
        assertThat(pullResult.getMsgFoundList()).isEqualTo(new ArrayList<Object>());
    }

    @Test
    public void testPullMessage_NotFound() throws Exception {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                PullMessageRequestHeader requestHeader = mock.getArgument(1);
                return createPullResult(requestHeader, NO_NEW_MSG, new ArrayList<MessageExt>());
            }
        }).when(mQClientAPIImpl).pullMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(PullMessageRequestHeader.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CommunicationMode.class), ArgumentMatchers.nullable(PullCallback.class));
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, 0);
        PullResult pullResult = pullConsumer.pull(messageQueue, "*", 1024, 3);
        assertThat(pullResult.getPullStatus()).isEqualTo(NO_NEW_MSG);
    }

    @Test
    public void testPullMessageAsync_Success() throws Exception {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                PullMessageRequestHeader requestHeader = mock.getArgument(1);
                PullResult pullResult = createPullResult(requestHeader, FOUND, Collections.singletonList(new MessageExt()));
                PullCallback pullCallback = mock.getArgument(4);
                pullCallback.onSuccess(pullResult);
                return null;
            }
        }).when(mQClientAPIImpl).pullMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(PullMessageRequestHeader.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CommunicationMode.class), ArgumentMatchers.nullable(PullCallback.class));
        MessageQueue messageQueue = new MessageQueue(topic, brokerName, 0);
        pullConsumer.pull(messageQueue, "*", 1024, 3, new PullCallback() {
            @Override
            public void onSuccess(PullResult pullResult) {
                assertThat(pullResult).isNotNull();
                assertThat(pullResult.getPullStatus()).isEqualTo(FOUND);
                assertThat(pullResult.getNextBeginOffset()).isEqualTo((1024 + 1));
                assertThat(pullResult.getMinOffset()).isEqualTo(123);
                assertThat(pullResult.getMaxOffset()).isEqualTo(2048);
                assertThat(pullResult.getMsgFoundList()).isEqualTo(new ArrayList<Object>());
            }

            @Override
            public void onException(Throwable e) {
            }
        });
    }
}

