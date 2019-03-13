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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * LogTelnetHandlerTest.java
 */
public class LogTelnetHandlerTest {
    private static TelnetHandler log = new LogTelnetHandler();

    private Channel mockChannel;

    @Test
    public void testChangeLogLevel() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        String result = LogTelnetHandlerTest.log.telnet(mockChannel, "error");
        Assertions.assertTrue(result.contains("\r\nCURRENT LOG LEVEL:ERROR"));
        String result2 = LogTelnetHandlerTest.log.telnet(mockChannel, "warn");
        Assertions.assertTrue(result2.contains("\r\nCURRENT LOG LEVEL:WARN"));
    }

    @Test
    public void testPrintLog() throws RemotingException {
        mockChannel = Mockito.mock(Channel.class);
        String result = LogTelnetHandlerTest.log.telnet(mockChannel, "100");
        Assertions.assertTrue(result.contains("CURRENT LOG APPENDER"));
    }
}

