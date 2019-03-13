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
package org.apache.nifi.cluster.protocol.impl;


import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.nifi.cluster.protocol.ProtocolMessageMarshaller;
import org.apache.nifi.cluster.protocol.ProtocolMessageUnmarshaller;
import org.apache.nifi.cluster.protocol.impl.testutils.DelayedProtocolHandler;
import org.apache.nifi.cluster.protocol.impl.testutils.ReflexiveProtocolHandler;
import org.apache.nifi.cluster.protocol.message.PingMessage;
import org.apache.nifi.cluster.protocol.message.ProtocolMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SocketProtocolListenerTest {
    private SocketProtocolListener listener;

    private Socket socket;

    private ProtocolMessageMarshaller<ProtocolMessage> marshaller;

    private ProtocolMessageUnmarshaller<ProtocolMessage> unmarshaller;

    @Test
    public void testBadRequest() throws Exception {
        DelayedProtocolHandler handler = new DelayedProtocolHandler(0);
        listener.addHandler(handler);
        socket.getOutputStream().write(5);
        Thread.sleep(250);
        Assert.assertEquals(0, handler.getMessages().size());
    }

    @Test
    public void testRequest() throws Exception {
        ProtocolMessage msg = new PingMessage();
        ReflexiveProtocolHandler handler = new ReflexiveProtocolHandler();
        listener.addHandler(handler);
        // marshal message to output stream
        marshaller.marshal(msg, socket.getOutputStream());
        // unmarshall response and return
        ProtocolMessage response = unmarshaller.unmarshal(socket.getInputStream());
        Assert.assertEquals(msg.getType(), response.getType());
        Assert.assertEquals(1, handler.getMessages().size());
        Assert.assertEquals(msg.getType(), handler.getMessages().get(0).getType());
    }

    @Test
    public void testDelayedRequest() throws Exception {
        ProtocolMessage msg = new PingMessage();
        DelayedProtocolHandler handler = new DelayedProtocolHandler(2000);
        listener.addHandler(handler);
        // marshal message to output stream
        marshaller.marshal(msg, socket.getOutputStream());
        try {
            socket.getInputStream().read();
            Assert.fail("Socket timeout not received.");
        } catch (SocketTimeoutException ste) {
        }
        Assert.assertEquals(1, handler.getMessages().size());
        Assert.assertEquals(msg.getType(), handler.getMessages().get(0).getType());
    }
}

