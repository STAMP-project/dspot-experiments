/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.transport.command.netty;


import HttpServer.handlerMap;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandHandlerProvider;
import com.alibaba.csp.sentinel.command.handler.BasicInfoCommandHandler;
import com.alibaba.csp.sentinel.command.handler.VersionCommandHandler;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static HttpServer.handlerMap;


/**
 * Test cases for {@link HttpServer}.
 *
 * @author cdfive
 * @unknown 2018-12-19
 */
public class HttpServerTest {
    private static HttpServer httpServer;

    @Test
    public void testRegisterCommand() {
        String commandName;
        CommandHandler handler;
        // if commandName is null, no handler added in handlerMap
        commandName = null;
        handler = new VersionCommandHandler();
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(0, handlerMap.size());
        // if commandName is "", no handler added in handlerMap
        commandName = "";
        handler = new VersionCommandHandler();
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(0, handlerMap.size());
        // if handler is null, no handler added in handlerMap
        commandName = "version";
        handler = null;
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(0, handlerMap.size());
        // add one handler, commandName:version, handler:VersionCommandHandler
        commandName = "version";
        handler = new VersionCommandHandler();
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(1, handlerMap.size());
        // add the same name Handler, no handler added in handlerMap
        commandName = "version";
        handler = new VersionCommandHandler();
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(1, handlerMap.size());
        // add another handler, commandName:basicInfo, handler:BasicInfoCommandHandler
        commandName = "basicInfo";
        handler = new BasicInfoCommandHandler();
        HttpServerTest.httpServer.registerCommand(commandName, handler);
        Assert.assertEquals(2, handlerMap.size());
    }

    @Test
    public void testRegisterCommands() {
        Map<String, CommandHandler> handlerMap = null;
        // if handlerMap is null, no handler added in handlerMap
        HttpServerTest.httpServer.registerCommands(handlerMap);
        Assert.assertEquals(0, handlerMap.size());
        // add handler from CommandHandlerProvider
        handlerMap = CommandHandlerProvider.getInstance().namedHandlers();
        HttpServerTest.httpServer.registerCommands(handlerMap);
        // check same size
        Assert.assertEquals(handlerMap.size(), handlerMap.size());
        // check not same reference
        Assert.assertTrue((handlerMap != (handlerMap)));
    }
}

