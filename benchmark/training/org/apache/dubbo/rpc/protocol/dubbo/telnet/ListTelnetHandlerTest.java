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


import java.lang.reflect.Method;
import org.apache.dubbo.common.utils.ReflectUtils;
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
 * CountTelnetHandlerTest.java
 */
public class ListTelnetHandlerTest {
    private static TelnetHandler list = new ListTelnetHandler();

    private Channel mockChannel;

    @Test
    public void testListDetailService() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService");
        ProviderModel providerModel = new ProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", providerModel);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "-l DemoService");
        for (Method method : DemoService.class.getMethods()) {
            Assertions.assertTrue(result.contains(ReflectUtils.getName(method)));
        }
    }

    @Test
    public void testListService() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService");
        ProviderModel providerModel = new ProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", providerModel);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "DemoService");
        for (Method method : DemoService.class.getMethods()) {
            Assertions.assertTrue(result.contains(method.getName()));
        }
    }

    @Test
    public void testList() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        ProviderModel providerModel = new ProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", providerModel);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "");
        Assertions.assertEquals("PROVIDER:\r\norg.apache.dubbo.rpc.protocol.dubbo.support.DemoService\r\n", result);
    }

    @Test
    public void testListDetail() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        ProviderModel providerModel = new ProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", providerModel);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "-l");
        Assertions.assertEquals("PROVIDER:\r\norg.apache.dubbo.rpc.protocol.dubbo.support.DemoService ->  published: N\r\n", result);
    }

    @Test
    public void testListDefault() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService");
        ProviderModel providerModel = new ProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel("org.apache.dubbo.rpc.protocol.dubbo.support.DemoService", providerModel);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "");
        Assertions.assertTrue(result.startsWith(("Use default service org.apache.dubbo.rpc.protocol.dubbo.support.DemoService.\r\n" + "org.apache.dubbo.rpc.protocol.dubbo.support.DemoService (as provider):\r\n")));
        for (Method method : DemoService.class.getMethods()) {
            Assertions.assertTrue(result.contains(method.getName()));
        }
    }

    @Test
    public void testInvalidMessage() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        String result = ListTelnetHandlerTest.list.telnet(mockChannel, "xx");
        Assertions.assertEquals("No such service: xx", result);
    }
}

