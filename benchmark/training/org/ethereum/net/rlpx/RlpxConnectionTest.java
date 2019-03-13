/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.net.rlpx;


import FrameCodec.Frame;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.security.SecureRandom;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by devrandom on 2015-04-11.
 */
public class RlpxConnectionTest {
    private FrameCodec iCodec;

    private FrameCodec rCodec;

    private EncryptionHandshake initiator;

    private EncryptionHandshake responder;

    private HandshakeMessage iMessage;

    private PipedInputStream to;

    private PipedOutputStream toOut;

    private PipedInputStream from;

    private PipedOutputStream fromOut;

    @Test
    public void testFrame() throws Exception {
        byte[] payload = new byte[123];
        new SecureRandom().nextBytes(payload);
        FrameCodec.Frame frame = new FrameCodec.Frame(12345, 123, new ByteArrayInputStream(payload));
        iCodec.writeFrame(frame, toOut);
        FrameCodec.Frame frame1 = rCodec.readFrames(new DataInputStream(to)).get(0);
        byte[] payload1 = new byte[frame1.size];
        Assert.assertEquals(frame.size, frame1.size);
        frame1.payload.read(payload1);
        Assert.assertArrayEquals(payload, payload1);
        Assert.assertEquals(frame.type, frame1.type);
    }

    @Test
    public void testMessageEncoding() throws IOException {
        byte[] wire = iMessage.encode();
        HandshakeMessage message1 = HandshakeMessage.parse(wire);
        Assert.assertEquals(123, message1.version);
        Assert.assertEquals("abcd", message1.name);
        Assert.assertEquals(3333, message1.listenPort);
        Assert.assertArrayEquals(message1.nodeId, message1.nodeId);
        Assert.assertEquals(iMessage.caps, message1.caps);
    }

    @Test
    public void testHandshake() throws IOException {
        RlpxConnection iConn = new RlpxConnection(initiator.getSecrets(), from, toOut);
        RlpxConnection rConn = new RlpxConnection(responder.getSecrets(), to, fromOut);
        iConn.sendProtocolHandshake(iMessage);
        rConn.handleNextMessage();
        HandshakeMessage receivedMessage = rConn.getHandshakeMessage();
        Assert.assertNotNull(receivedMessage);
        Assert.assertArrayEquals(iMessage.nodeId, receivedMessage.nodeId);
    }
}

