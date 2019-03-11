/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.jsr.test.reconnect;


import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.websockets.jsr.ServerWebSocketContainer;
import java.net.URI;
import javax.websocket.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
public class ClientEndpointReconnectTestCase {
    private static ServerWebSocketContainer deployment;

    private static volatile boolean failed = false;

    @Test
    public void testAnnotatedClientEndpoint() throws Exception {
        AnnotatedClientReconnectEndpoint endpoint = new AnnotatedClientReconnectEndpoint();
        Session session = ClientEndpointReconnectTestCase.deployment.connectToServer(endpoint, new URI((((("ws://" + (DefaultServer.getHostAddress("default"))) + ":") + (DefaultServer.getHostPort("default"))) + "/ws/")));
        Assert.assertEquals("OPEN", endpoint.message());
        session.getBasicRemote().sendText("hi");
        Assert.assertEquals("MESSAGE-ECHO-hi", endpoint.message());
        session.getBasicRemote().sendText("close");
        Assert.assertEquals("CLOSE", endpoint.message());
        Assert.assertEquals("OPEN", endpoint.message());
        session.getBasicRemote().sendText("hi");
        Assert.assertEquals("MESSAGE-ECHO-hi", endpoint.message());
        session.getBasicRemote().sendText("close");
        Assert.assertEquals("CLOSE", endpoint.message());
        Assert.assertEquals("OPEN", endpoint.message());
        session.getBasicRemote().sendText("hi");
        Assert.assertEquals("MESSAGE-ECHO-hi", endpoint.message());
        session.getBasicRemote().sendText("close");
        Assert.assertEquals("CLOSE", endpoint.message());
        Assert.assertNull(endpoint.quickMessage());
        Assert.assertFalse(ClientEndpointReconnectTestCase.failed);
    }
}

