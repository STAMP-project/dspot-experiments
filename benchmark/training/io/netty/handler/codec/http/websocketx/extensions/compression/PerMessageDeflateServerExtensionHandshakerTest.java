/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;


import WebSocketServerExtension.RSV1;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PerMessageDeflateServerExtensionHandshakerTest {
    @Test
    public void testNormalHandshake() {
        WebSocketServerExtension extension;
        WebSocketExtensionData data;
        Map<String, String> parameters;
        // initialize
        PerMessageDeflateServerExtensionHandshaker.PerMessageDeflateServerExtensionHandshaker handshaker = new PerMessageDeflateServerExtensionHandshaker.PerMessageDeflateServerExtensionHandshaker();
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, Collections.<String, String>emptyMap()));
        // test
        Assert.assertNotNull(extension);
        Assert.assertEquals(RSV1, extension.rsv());
        Assert.assertTrue(((extension.newExtensionDecoder()) instanceof PerMessageDeflateDecoder));
        Assert.assertTrue(((extension.newExtensionEncoder()) instanceof PerMessageDeflateEncoder));
        // execute
        data = extension.newReponseData();
        Assert.assertEquals(PERMESSAGE_DEFLATE_EXTENSION, data.name());
        Assert.assertTrue(data.parameters().isEmpty());
        // initialize
        parameters = new HashMap<String, String>();
        parameters.put(CLIENT_MAX_WINDOW, null);
        parameters.put(CLIENT_NO_CONTEXT, null);
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, Collections.<String, String>emptyMap()));
        // test
        Assert.assertNotNull(extension);
        Assert.assertEquals(RSV1, extension.rsv());
        Assert.assertTrue(((extension.newExtensionDecoder()) instanceof PerMessageDeflateDecoder));
        Assert.assertTrue(((extension.newExtensionEncoder()) instanceof PerMessageDeflateEncoder));
        // execute
        data = extension.newReponseData();
        // test
        Assert.assertEquals(PERMESSAGE_DEFLATE_EXTENSION, data.name());
        Assert.assertTrue(data.parameters().isEmpty());
        // initialize
        parameters = new HashMap<String, String>();
        parameters.put(SERVER_MAX_WINDOW, "12");
        parameters.put(SERVER_NO_CONTEXT, null);
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, parameters));
        // test
        Assert.assertNull(extension);
    }

    @Test
    public void testCustomHandshake() {
        WebSocketServerExtension extension;
        Map<String, String> parameters;
        WebSocketExtensionData data;
        // initialize
        PerMessageDeflateServerExtensionHandshaker.PerMessageDeflateServerExtensionHandshaker handshaker = new PerMessageDeflateServerExtensionHandshaker.PerMessageDeflateServerExtensionHandshaker(6, true, 10, true, true);
        parameters = new HashMap<String, String>();
        parameters.put(CLIENT_MAX_WINDOW, null);
        parameters.put(SERVER_MAX_WINDOW, "12");
        parameters.put(CLIENT_NO_CONTEXT, null);
        parameters.put(SERVER_NO_CONTEXT, null);
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, parameters));
        // test
        Assert.assertNotNull(extension);
        Assert.assertEquals(RSV1, extension.rsv());
        Assert.assertTrue(((extension.newExtensionDecoder()) instanceof PerMessageDeflateDecoder));
        Assert.assertTrue(((extension.newExtensionEncoder()) instanceof PerMessageDeflateEncoder));
        // execute
        data = extension.newReponseData();
        // test
        Assert.assertEquals(PERMESSAGE_DEFLATE_EXTENSION, data.name());
        Assert.assertTrue(data.parameters().containsKey(CLIENT_MAX_WINDOW));
        Assert.assertEquals("10", data.parameters().get(CLIENT_MAX_WINDOW));
        Assert.assertTrue(data.parameters().containsKey(SERVER_MAX_WINDOW));
        Assert.assertEquals("12", data.parameters().get(SERVER_MAX_WINDOW));
        Assert.assertTrue(data.parameters().containsKey(CLIENT_MAX_WINDOW));
        Assert.assertTrue(data.parameters().containsKey(SERVER_MAX_WINDOW));
        // initialize
        parameters = new HashMap<String, String>();
        parameters.put(SERVER_MAX_WINDOW, "12");
        parameters.put(SERVER_NO_CONTEXT, null);
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, parameters));
        // test
        Assert.assertNotNull(extension);
        Assert.assertEquals(RSV1, extension.rsv());
        Assert.assertTrue(((extension.newExtensionDecoder()) instanceof PerMessageDeflateDecoder));
        Assert.assertTrue(((extension.newExtensionEncoder()) instanceof PerMessageDeflateEncoder));
        // execute
        data = extension.newReponseData();
        // test
        Assert.assertEquals(PERMESSAGE_DEFLATE_EXTENSION, data.name());
        Assert.assertEquals(2, data.parameters().size());
        Assert.assertTrue(data.parameters().containsKey(SERVER_MAX_WINDOW));
        Assert.assertEquals("12", data.parameters().get(SERVER_MAX_WINDOW));
        Assert.assertTrue(data.parameters().containsKey(SERVER_NO_CONTEXT));
        // initialize
        parameters = new HashMap<String, String>();
        // execute
        extension = handshaker.handshakeExtension(new WebSocketExtensionData(PERMESSAGE_DEFLATE_EXTENSION, parameters));
        // test
        Assert.assertNotNull(extension);
        // execute
        data = extension.newReponseData();
        // test
        Assert.assertEquals(PERMESSAGE_DEFLATE_EXTENSION, data.name());
        Assert.assertTrue(data.parameters().isEmpty());
    }
}

