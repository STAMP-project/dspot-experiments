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
package org.apache.rocketmq.broker.processor;


import ResponseCode.SUCCESS;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.UUID;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.client.ClientChannelInfo;
import org.apache.rocketmq.broker.client.ConsumerGroupInfo;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClientManageProcessorTest {
    private ClientManageProcessor clientManageProcessor;

    @Spy
    private BrokerController brokerController = new BrokerController(new BrokerConfig(), new NettyServerConfig(), new NettyClientConfig(), new MessageStoreConfig());

    @Mock
    private ChannelHandlerContext handlerContext;

    @Mock
    private Channel channel;

    private ClientChannelInfo clientChannelInfo;

    private String clientId = UUID.randomUUID().toString();

    private String group = "FooBarGroup";

    private String topic = "FooBar";

    @Test
    public void processRequest_UnRegisterProducer() throws Exception {
        brokerController.getProducerManager().registerProducer(group, clientChannelInfo);
        HashMap<Channel, ClientChannelInfo> channelMap = brokerController.getProducerManager().getGroupChannelTable().get(group);
        assertThat(channelMap).isNotNull();
        assertThat(channelMap.get(channel)).isEqualTo(clientChannelInfo);
        RemotingCommand request = createUnRegisterProducerCommand();
        RemotingCommand response = clientManageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        channelMap = brokerController.getProducerManager().getGroupChannelTable().get(group);
        assertThat(channelMap).isNull();
    }

    @Test
    public void processRequest_UnRegisterConsumer() throws RemotingCommandException {
        ConsumerGroupInfo consumerGroupInfo = brokerController.getConsumerManager().getConsumerGroupInfo(group);
        assertThat(consumerGroupInfo).isNotNull();
        RemotingCommand request = createUnRegisterConsumerCommand();
        RemotingCommand response = clientManageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        consumerGroupInfo = brokerController.getConsumerManager().getConsumerGroupInfo(group);
        assertThat(consumerGroupInfo).isNull();
    }
}

