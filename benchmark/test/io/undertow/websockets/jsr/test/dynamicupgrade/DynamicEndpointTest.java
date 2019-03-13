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
package io.undertow.websockets.jsr.test.dynamicupgrade;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.websockets.jsr.ServerWebSocketContainer;
import io.undertow.websockets.utils.WebSocketTestClient;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.FutureResult;


/**
 *
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
public class DynamicEndpointTest {
    private static ServerWebSocketContainer deployment;

    @Test
    public void testDynamicAnnotatedEndpoint() throws Exception {
        final byte[] payload = "hello".getBytes();
        final FutureResult latch = new FutureResult();
        WebSocketTestClient client = new WebSocketTestClient(WebSocketVersion.V13, new URI((((("ws://" + (DefaultServer.getHostAddress("default"))) + ":") + (DefaultServer.getHostPort("default"))) + "/ws/dynamicEchoEndpoint?annotated=true")));
        client.connect();
        client.send(new TextWebSocketFrame(Unpooled.wrappedBuffer(payload)), new io.undertow.websockets.utils.FrameChecker(TextWebSocketFrame.class, "opened:true /dynamicEchoEndpoint hello".getBytes(), latch));
        latch.getIoFuture().get();
        client.destroy();
    }

    @Test
    public void testDynamicProgramaticEndpoint() throws Exception {
        final byte[] payload = "hello".getBytes();
        final FutureResult latch = new FutureResult();
        WebSocketTestClient client = new WebSocketTestClient(WebSocketVersion.V13, new URI((((("ws://" + (DefaultServer.getHostAddress("default"))) + ":") + (DefaultServer.getHostPort("default"))) + "/ws/dynamicEchoEndpoint")));
        client.connect();
        client.send(new TextWebSocketFrame(Unpooled.wrappedBuffer(payload)), new io.undertow.websockets.utils.FrameChecker(TextWebSocketFrame.class, "/dynamicEchoEndpoint hello".getBytes(), latch));
        latch.getIoFuture().get();
        client.destroy();
    }
}

