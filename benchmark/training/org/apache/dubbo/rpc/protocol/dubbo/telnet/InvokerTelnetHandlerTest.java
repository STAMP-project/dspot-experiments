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
 * InvokeTelnetHandlerTest.java
 */
public class InvokerTelnetHandlerTest {
    private static TelnetHandler invoke = new InvokeTelnetHandler();

    private static TelnetHandler select = new SelectTelnetHandler();

    private Channel mockChannel;

    @SuppressWarnings("unchecked")
    @Test
    public void testInvokeDefaultService() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "echo(\"ok\")");
        Assertions.assertTrue(result.contains("result: \"ok\""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvokeWithSpecifyService() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "DemoService.echo(\"ok\")");
        Assertions.assertTrue(result.contains("result: \"ok\""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvokeByPassingNullValue() {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        try {
            InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "sayHello(null)");
        } catch (Exception ex) {
            Assertions.assertTrue((ex instanceof NullPointerException));
        }
    }

    @Test
    public void testInvokeByPassingEnumValue() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "getType(\"High\")");
        Assertions.assertTrue(result.contains("result: \"High\""));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOverriddenMethodWithSpecifyParamType() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "getPerson({\"name\":\"zhangsan\",\"age\":12,\"class\":\"org.apache.dubbo.rpc.protocol.dubbo.support.Person\"})");
        Assertions.assertTrue(result.contains("result: 12"));
    }

    @Test
    public void testInvokeOverriddenMethodBySelect() throws RemotingException {
        // create a real instance to keep the attribute values;
        mockChannel = Mockito.spy(getChannelInstance());
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(DemoService.class.getName());
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String param = "{\"name\":\"Dubbo\",\"age\":8}";
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, (("getPerson(" + param) + ")"));
        Assertions.assertTrue(result.contains("Please use the select command to select the method you want to invoke. eg: select 1"));
        result = InvokerTelnetHandlerTest.select.telnet(mockChannel, "1");
        // result dependent on method order.
        Assertions.assertTrue(((result.contains("result: 8")) || (result.contains("result: \"Dubbo\""))));
    }

    @Test
    public void testInvokeMultiJsonParamMethod() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        BDDMockito.given(mockChannel.getLocalAddress()).willReturn(NetUtils.toAddress("127.0.0.1:5555"));
        BDDMockito.given(mockChannel.getRemoteAddress()).willReturn(NetUtils.toAddress("127.0.0.1:20886"));
        ProviderModel providerModel = new ProviderModel(DemoService.class.getName(), new DemoServiceImpl(), DemoService.class);
        ApplicationModel.initProviderModel(DemoService.class.getName(), providerModel);
        String param = "{\"name\":\"Dubbo\",\"age\":8},{\"name\":\"Apache\",\"age\":20}";
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, (("getPerson(" + param) + ")"));
        Assertions.assertTrue(result.contains("result: 28"));
    }

    @Test
    public void testMessageNull() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, null);
        Assertions.assertEquals("Please input method name, eg: \r\ninvoke xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})\r\ninvoke XxxService.xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})\r\ninvoke com.xxx.XxxService.xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})", result);
    }

    @Test
    public void testInvalidMessage() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        BDDMockito.given(mockChannel.getAttribute("telnet.service")).willReturn(null);
        String result = InvokerTelnetHandlerTest.invoke.telnet(mockChannel, "(");
        Assertions.assertEquals("Invalid parameters, format: service.method(args)", result);
    }
}

