/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.webapp;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for Node Manager Container Web Socket.
 */
public class TestNMContainerWebSocket {
    private static final Logger LOG = LoggerFactory.getLogger(TestNMContainerWebSocket.class);

    private static final File TESTROOTDIR = new File("target", TestNMWebServer.class.getSimpleName());

    private static File testLogDir = new File("target", ((TestNMWebServer.class.getSimpleName()) + "LogDir"));

    private WebServer server;

    @Test
    public void testWebServerWithServlet() {
        int port = startNMWebAppServer("0.0.0.0");
        TestNMContainerWebSocket.LOG.info(("bind to port: " + port));
        StringBuilder sb = new StringBuilder();
        sb.append("ws://localhost:").append(port).append("/container/abc/");
        String dest = sb.toString();
        WebSocketClient client = new WebSocketClient();
        try {
            ContainerShellClientSocketTest socket = new ContainerShellClientSocketTest();
            client.start();
            URI echoUri = new URI(dest);
            Future<Session> future = client.connect(socket, echoUri);
            Session session = future.get();
            session.getRemote().sendString("hello world");
            session.close();
            client.stop();
        } catch (Throwable t) {
            TestNMContainerWebSocket.LOG.error("Failed to connect WebSocket and send message to server", t);
        } finally {
            try {
                client.stop();
                server.close();
            } catch (Exception e) {
                TestNMContainerWebSocket.LOG.error("Failed to close client", e);
            }
        }
    }

    @Test
    public void testContainerShellWebSocket() {
        Context nm = Mockito.mock(Context.class);
        Session session = Mockito.mock(Session.class);
        Container container = Mockito.mock(Container.class);
        UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
        ApplicationACLsManager aclManager = Mockito.mock(ApplicationACLsManager.class);
        ContainerShellWebSocket.init(nm);
        ContainerShellWebSocket ws = new ContainerShellWebSocket();
        List<String> names = new ArrayList<>();
        names.add("foobar");
        Map<String, List<String>> mockParameters = new HashMap<>();
        mockParameters.put("user.name", names);
        Mockito.when(session.getUpgradeRequest()).thenReturn(request);
        Mockito.when(request.getParameterMap()).thenReturn(mockParameters);
        Mockito.when(container.getUser()).thenReturn("foobar");
        Mockito.when(nm.getApplicationACLsManager()).thenReturn(aclManager);
        Mockito.when(aclManager.areACLsEnabled()).thenReturn(false);
        try {
            boolean authorized = ws.checkAuthorization(session, container);
            Assert.assertTrue("Not authorized", authorized);
        } catch (IOException e) {
            Assert.fail("Should not throw exception.");
        }
    }
}

