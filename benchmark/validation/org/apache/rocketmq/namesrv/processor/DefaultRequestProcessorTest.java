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
package org.apache.rocketmq.namesrv.processor;


import RequestCode.DELETE_KV_CONFIG;
import RequestCode.GET_KV_CONFIG;
import RequestCode.PUT_KV_CONFIG;
import ResponseCode.QUERY_NOT_FOUND;
import ResponseCode.SUCCESS;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import org.apache.rocketmq.common.namesrv.NamesrvConfig;
import org.apache.rocketmq.common.protocol.header.namesrv.DeleteKVConfigRequestHeader;
import org.apache.rocketmq.common.protocol.header.namesrv.GetKVConfigRequestHeader;
import org.apache.rocketmq.common.protocol.header.namesrv.GetKVConfigResponseHeader;
import org.apache.rocketmq.common.protocol.header.namesrv.PutKVConfigRequestHeader;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.apache.rocketmq.namesrv.routeinfo.RouteInfoManager;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultRequestProcessorTest {
    private DefaultRequestProcessor defaultRequestProcessor;

    private NamesrvController namesrvController;

    private NamesrvConfig namesrvConfig;

    private NettyServerConfig nettyServerConfig;

    private RouteInfoManager routeInfoManager;

    private InternalLogger logger;

    @Test
    public void testProcessRequest_PutKVConfig() throws RemotingCommandException {
        PutKVConfigRequestHeader header = new PutKVConfigRequestHeader();
        RemotingCommand request = RemotingCommand.createRequestCommand(PUT_KV_CONFIG, header);
        request.addExtField("namespace", "namespace");
        request.addExtField("key", "key");
        request.addExtField("value", "value");
        RemotingCommand response = defaultRequestProcessor.processRequest(null, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(response.getRemark()).isNull();
        assertThat(namesrvController.getKvConfigManager().getKVConfig("namespace", "key")).isEqualTo("value");
    }

    @Test
    public void testProcessRequest_GetKVConfigReturnNotNull() throws RemotingCommandException {
        namesrvController.getKvConfigManager().putKVConfig("namespace", "key", "value");
        GetKVConfigRequestHeader header = new GetKVConfigRequestHeader();
        RemotingCommand request = RemotingCommand.createRequestCommand(GET_KV_CONFIG, header);
        request.addExtField("namespace", "namespace");
        request.addExtField("key", "key");
        RemotingCommand response = defaultRequestProcessor.processRequest(null, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(response.getRemark()).isNull();
        GetKVConfigResponseHeader responseHeader = ((GetKVConfigResponseHeader) (response.readCustomHeader()));
        assertThat(responseHeader.getValue()).isEqualTo("value");
    }

    @Test
    public void testProcessRequest_GetKVConfigReturnNull() throws RemotingCommandException {
        GetKVConfigRequestHeader header = new GetKVConfigRequestHeader();
        RemotingCommand request = RemotingCommand.createRequestCommand(GET_KV_CONFIG, header);
        request.addExtField("namespace", "namespace");
        request.addExtField("key", "key");
        RemotingCommand response = defaultRequestProcessor.processRequest(null, request);
        assertThat(response.getCode()).isEqualTo(QUERY_NOT_FOUND);
        assertThat(response.getRemark()).isEqualTo("No config item, Namespace: namespace Key: key");
        GetKVConfigResponseHeader responseHeader = ((GetKVConfigResponseHeader) (response.readCustomHeader()));
        assertThat(responseHeader.getValue()).isNull();
    }

    @Test
    public void testProcessRequest_DeleteKVConfig() throws RemotingCommandException {
        namesrvController.getKvConfigManager().putKVConfig("namespace", "key", "value");
        DeleteKVConfigRequestHeader header = new DeleteKVConfigRequestHeader();
        RemotingCommand request = RemotingCommand.createRequestCommand(DELETE_KV_CONFIG, header);
        request.addExtField("namespace", "namespace");
        request.addExtField("key", "key");
        RemotingCommand response = defaultRequestProcessor.processRequest(null, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(response.getRemark()).isNull();
        assertThat(namesrvController.getKvConfigManager().getKVConfig("namespace", "key")).isNull();
    }

    @Test
    public void testProcessRequest_RegisterBroker() throws IllegalAccessException, NoSuchFieldException, RemotingCommandException {
        RemotingCommand request = DefaultRequestProcessorTest.genSampleRegisterCmd(true);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        Mockito.when(ctx.channel()).thenReturn(null);
        RemotingCommand response = defaultRequestProcessor.processRequest(ctx, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(response.getRemark()).isNull();
        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);
        BrokerData broker = new BrokerData();
        broker.setBrokerName("broker");
        broker.setBrokerAddrs(((HashMap) (Maps.newHashMap(new Long(2333), "10.10.1.1"))));
        assertThat(((java.util.Map) (brokerAddrTable.get(routes)))).contains(new AbstractMap.SimpleEntry("broker", broker));
    }

    @Test
    public void testProcessRequest_RegisterBrokerWithFilterServer() throws IllegalAccessException, NoSuchFieldException, RemotingCommandException {
        RemotingCommand request = DefaultRequestProcessorTest.genSampleRegisterCmd(true);
        // version >= MQVersion.Version.V3_0_11.ordinal() to register with filter server
        request.setVersion(100);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        Mockito.when(ctx.channel()).thenReturn(null);
        RemotingCommand response = defaultRequestProcessor.processRequest(ctx, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(response.getRemark()).isNull();
        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);
        BrokerData broker = new BrokerData();
        broker.setBrokerName("broker");
        broker.setBrokerAddrs(((HashMap) (Maps.newHashMap(new Long(2333), "10.10.1.1"))));
        assertThat(((java.util.Map) (brokerAddrTable.get(routes)))).contains(new AbstractMap.SimpleEntry("broker", broker));
    }

    @Test
    public void testProcessRequest_UnregisterBroker() throws IllegalAccessException, NoSuchFieldException, RemotingCommandException {
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        Mockito.when(ctx.channel()).thenReturn(null);
        // Register broker
        RemotingCommand regRequest = DefaultRequestProcessorTest.genSampleRegisterCmd(true);
        defaultRequestProcessor.processRequest(ctx, regRequest);
        // Unregister broker
        RemotingCommand unregRequest = DefaultRequestProcessorTest.genSampleRegisterCmd(false);
        RemotingCommand unregResponse = defaultRequestProcessor.processRequest(ctx, unregRequest);
        assertThat(unregResponse.getCode()).isEqualTo(SUCCESS);
        assertThat(unregResponse.getRemark()).isNull();
        RouteInfoManager routes = namesrvController.getRouteInfoManager();
        Field brokerAddrTable = RouteInfoManager.class.getDeclaredField("brokerAddrTable");
        brokerAddrTable.setAccessible(true);
        assertThat(((java.util.Map) (brokerAddrTable.get(routes)))).isNotEmpty();
    }
}

