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
package org.apache.dubbo.rpc.protocol.dubbo.telnet;


import InvokeTelnetHandler.INVOKE_METHOD_LIST_KEY;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.telnet.TelnetHandler;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.apache.dubbo.rpc.protocol.dubbo.support.DemoService;
import org.apache.dubbo.rpc.protocol.dubbo.support.DemoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * SelectTelnetHandlerTest.java
 */
public class SelectTelnetHandlerTest {
    private static TelnetHandler select = new SelectTelnetHandler();

    private Channel mockChannel;

    List<Method> methods;

    @Test
    public void testInvokeWithoutMethodList() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = SelectTelnetHandlerTest.select.telnet(mockChannel, "1");
        Assertions.assertTrue(result.contains("Please use the invoke command first."));
    }

    @Test
    public void testInvokeWithIllegalMessage() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getAttribute(INVOKE_METHOD_LIST_KEY)).willReturn(methods);
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = SelectTelnetHandlerTest.select.telnet(mockChannel, "index");
        Assertions.assertTrue(result.contains("Illegal index ,please input select 1"));
        result = SelectTelnetHandlerTest.select.telnet(mockChannel, "0");
        Assertions.assertTrue(result.contains("Illegal index ,please input select 1"));
        result = SelectTelnetHandlerTest.select.telnet(mockChannel, "1000");
        Assertions.assertTrue(result.contains("Illegal index ,please input select 1"));
    }

    @Test
    public void testInvokeWithNull() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getAttribute(INVOKE_METHOD_LIST_KEY)).willReturn(methods);
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = SelectTelnetHandlerTest.select.telnet(mockChannel, null);
        Assertions.assertTrue(result.contains("Please input the index of the method you want to invoke"));
    }
}

