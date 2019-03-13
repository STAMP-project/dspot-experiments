/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent;


import com.thoughtworks.go.agent.common.ssl.GoAgentServerWebSocketClientBuilder;
import com.thoughtworks.go.util.URLService;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.client.io.UpgradeListener;
import org.junit.Test;
import org.mockito.Mockito;


public class WebSocketClientHandlerTest {
    private WebSocketClientHandler webSocketClientHandler;

    private GoAgentServerWebSocketClientBuilder builder;

    private URLService urlService;

    private Future session;

    @Test
    public void shouldVerifyThatWebSocketClientIsStarted() throws Exception {
        webSocketClientHandler.connect(createAgentController());
        Mockito.verify(builder).build();
        Mockito.verify(session).get();
    }

    @Test
    public void shouldVerifyThatWebSocketClientIsNotStartedIfAlreadyRunning() throws Exception {
        webSocketClientHandler.connect(createAgentController());
        webSocketClientHandler.connect(createAgentController());
        Mockito.verify(builder, Mockito.times(1)).build();
        Mockito.verify(session, Mockito.times(2)).get();
    }

    class WebSocketClientStub extends WebSocketClient {
        @Override
        public Future<Session> connect(Object websocket, URI toUri, ClientUpgradeRequest request, UpgradeListener upgradeListener) throws IOException {
            return session;
        }
    }
}

