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


import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.telnet.TelnetHandler;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.apache.dubbo.rpc.protocol.dubbo.support.DemoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * ChangeTelnetHandlerTest.java
 */
public class ChangeTelnetHandlerTest {
    private static TelnetHandler change = new ChangeTelnetHandler();

    private Channel mockChannel;

    private Invoker<DemoService> mockInvoker;

    @Test
    public void testChangeSimpleName() throws RemotingException {
        DubboProtocol.getDubboProtocol().export(mockInvoker);
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "DemoService");
        Assertions.assertEquals("Used the DemoService as default.\r\nYou can cancel default service by command: cd /", result);
    }

    @Test
    public void testChangeName() throws RemotingException {
        DubboProtocol.getDubboProtocol().export(mockInvoker);
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "org.apache.dubbo.rpc.protocol.dubbo.support.DemoService");
        Assertions.assertEquals("Used the org.apache.dubbo.rpc.protocol.dubbo.support.DemoService as default.\r\nYou can cancel default service by command: cd /", result);
    }

    @Test
    public void testChangePath() throws RemotingException {
        DubboProtocol.getDubboProtocol().export(mockInvoker);
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "demo");
        Assertions.assertEquals("Used the demo as default.\r\nYou can cancel default service by command: cd /", result);
    }

    @Test
    public void testChangeMessageNull() throws RemotingException {
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, null);
        Assertions.assertEquals("Please input service name, eg: \r\ncd XxxService\r\ncd com.xxx.XxxService", result);
    }

    @Test
    public void testChangeServiceNotExport() throws RemotingException {
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "demo");
        Assertions.assertEquals("No such service demo", result);
    }

    @Test
    public void testChangeCancel() throws RemotingException {
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "..");
        Assertions.assertEquals("Cancelled default service org.apache.dubbo.rpc.protocol.dubbo.support.DemoService.", result);
    }

    @Test
    public void testChangeCancel2() throws RemotingException {
        String result = ChangeTelnetHandlerTest.change.telnet(mockChannel, "/");
        Assertions.assertEquals("Cancelled default service org.apache.dubbo.rpc.protocol.dubbo.support.DemoService.", result);
    }
}

