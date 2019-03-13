/**
 * Copyright the original author or authors
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
package org.jivesoftware.smackx.bytestreams.ibb;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.util.Protocol;
import org.jivesoftware.util.Verification;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;
import org.powermock.reflect.Whitebox;


/**
 * Test for InBandBytestreamSession.
 * <p>
 * Tests sending data encapsulated in message stanzas.
 *
 * @author Henning Staib
 */
public class InBandBytestreamSessionMessageTest extends InitExtensions {
    // settings
    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private static final String sessionID = "session_id";

    private static final int blockSize = 10;

    // protocol verifier
    private Protocol protocol;

    // mocked XMPP connection
    private XMPPConnection connection;

    private InBandBytestreamManager byteStreamManager;

    private Open initBytestream;

    private Verification<Message, IQ> incrementingSequence;

    /**
     * Test the output stream write(byte[]) method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendThreeDataPackets1() throws Exception {
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        // verify the data packets
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        byte[] controlData = new byte[(InBandBytestreamSessionMessageTest.blockSize) * 3];
        OutputStream outputStream = session.getOutputStream();
        outputStream.write(controlData);
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * Test the output stream write(byte) method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendThreeDataPackets2() throws Exception {
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        // verify the data packets
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        byte[] controlData = new byte[(InBandBytestreamSessionMessageTest.blockSize) * 3];
        OutputStream outputStream = session.getOutputStream();
        for (byte b : controlData) {
            outputStream.write(b);
        }
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * Test the output stream write(byte[], int, int) method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendThreeDataPackets3() throws Exception {
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        // verify the data packets
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        byte[] controlData = new byte[((InBandBytestreamSessionMessageTest.blockSize) * 3) - 2];
        OutputStream outputStream = session.getOutputStream();
        int off = 0;
        for (int i = 1; i <= 7; i++) {
            outputStream.write(controlData, off, i);
            off += i;
        }
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * Test the output stream flush() method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendThirtyDataPackets() throws Exception {
        byte[] controlData = new byte[(InBandBytestreamSessionMessageTest.blockSize) * 3];
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        // verify the data packets
        for (int i = 0; i < (controlData.length); i++) {
            protocol.addResponse(null, incrementingSequence);
        }
        OutputStream outputStream = session.getOutputStream();
        for (byte b : controlData) {
            outputStream.write(b);
            outputStream.flush();
        }
        protocol.verifyAll();
    }

    /**
     * Test successive calls to the output stream flush() method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendNothingOnSuccessiveCallsToFlush() throws Exception {
        byte[] controlData = new byte[(InBandBytestreamSessionMessageTest.blockSize) * 3];
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        // verify the data packets
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        protocol.addResponse(null, incrementingSequence);
        OutputStream outputStream = session.getOutputStream();
        outputStream.write(controlData);
        outputStream.flush();
        outputStream.flush();
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * If a data stanza is received out of order the session should be closed. See XEP-0047 Section
     * 2.2.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendCloseRequestIfInvalidSequenceReceived() throws Exception {
        // confirm close request
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionMessageTest.initiatorJID, InBandBytestreamSessionMessageTest.targetJID);
        protocol.addResponse(resultIQ, Verification.requestTypeSET, Verification.correspondingSenderReceiver);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build invalid packet with out of order sequence
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionMessageTest.sessionID, 123, base64Data);
        Message dataMessage = new Message();
        dataMessage.addExtension(dpe);
        // add data packets
        listener.processStanza(dataMessage);
        // read until exception is thrown
        try {
            inputStream.read();
            Assert.fail("exception should be thrown");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Packets out of sequence"));
        }
        protocol.verifyAll();
    }

    /**
     * Test the input stream read(byte[], int, int) method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReadAllReceivedData1() throws Exception {
        // create random data
        Random rand = new Random();
        byte[] controlData = new byte[3 * (InBandBytestreamSessionMessageTest.blockSize)];
        rand.nextBytes(controlData);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // verify data packet and notify listener
        for (int i = 0; i < ((controlData.length) / (InBandBytestreamSessionMessageTest.blockSize)); i++) {
            String base64Data = Base64.encodeToString(controlData, (i * (InBandBytestreamSessionMessageTest.blockSize)), InBandBytestreamSessionMessageTest.blockSize);
            DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionMessageTest.sessionID, i, base64Data);
            Message dataMessage = new Message();
            dataMessage.addExtension(dpe);
            listener.processStanza(dataMessage);
        }
        byte[] bytes = new byte[3 * (InBandBytestreamSessionMessageTest.blockSize)];
        int read = 0;
        read = inputStream.read(bytes, 0, InBandBytestreamSessionMessageTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionMessageTest.blockSize, read);
        read = inputStream.read(bytes, 10, InBandBytestreamSessionMessageTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionMessageTest.blockSize, read);
        read = inputStream.read(bytes, 20, InBandBytestreamSessionMessageTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionMessageTest.blockSize, read);
        // verify data
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(controlData[i], bytes[i]);
        }
        protocol.verifyAll();
    }

    /**
     * Test the input stream read() method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReadAllReceivedData2() throws Exception {
        // create random data
        Random rand = new Random();
        byte[] controlData = new byte[3 * (InBandBytestreamSessionMessageTest.blockSize)];
        rand.nextBytes(controlData);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionMessageTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // verify data packet and notify listener
        for (int i = 0; i < ((controlData.length) / (InBandBytestreamSessionMessageTest.blockSize)); i++) {
            String base64Data = Base64.encodeToString(controlData, (i * (InBandBytestreamSessionMessageTest.blockSize)), InBandBytestreamSessionMessageTest.blockSize);
            DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionMessageTest.sessionID, i, base64Data);
            Message dataMessage = new Message();
            dataMessage.addExtension(dpe);
            listener.processStanza(dataMessage);
        }
        // read data
        byte[] bytes = new byte[3 * (InBandBytestreamSessionMessageTest.blockSize)];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (inputStream.read()));
        }
        // verify data
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(controlData[i], bytes[i]);
        }
        protocol.verifyAll();
    }
}

