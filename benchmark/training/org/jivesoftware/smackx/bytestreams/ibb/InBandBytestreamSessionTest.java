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


import StanzaError.Condition.bad_request;
import StanzaError.Condition.unexpected_request;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;
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
 * Tests the basic behavior of an In-Band Bytestream session along with sending data encapsulated in
 * IQ stanzas.
 *
 * @author Henning Staib
 */
public class InBandBytestreamSessionTest extends InitExtensions {
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

    private Verification<Data, IQ> incrementingSequence;

    /**
     * Test the output stream write(byte[]) method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendThreeDataPackets1() throws Exception {
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        byte[] controlData = new byte[(InBandBytestreamSessionTest.blockSize) * 3];
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
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        byte[] controlData = new byte[(InBandBytestreamSessionTest.blockSize) * 3];
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
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        byte[] controlData = new byte[((InBandBytestreamSessionTest.blockSize) * 3) - 2];
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
        byte[] controlData = new byte[(InBandBytestreamSessionTest.blockSize) * 3];
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        for (int i = 0; i < (controlData.length); i++) {
            protocol.addResponse(resultIQ, incrementingSequence);
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
        byte[] controlData = new byte[(InBandBytestreamSessionTest.blockSize) * 3];
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        protocol.addResponse(resultIQ, incrementingSequence);
        OutputStream outputStream = session.getOutputStream();
        outputStream.write(controlData);
        outputStream.flush();
        outputStream.flush();
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * Test that the data is correctly chunked.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSendDataCorrectly() throws Exception {
        // create random data
        Random rand = new Random();
        final byte[] controlData = new byte[256 * (InBandBytestreamSessionTest.blockSize)];
        rand.nextBytes(controlData);
        // compares the data of each packet with the control data
        Verification<Data, IQ> dataVerification = new Verification<Data, IQ>() {
            @Override
            public void verify(Data request, IQ response) {
                byte[] decodedData = request.getDataPacketExtension().getDecodedData();
                int seq = ((int) (request.getDataPacketExtension().getSeq()));
                for (int i = 0; i < (decodedData.length); i++) {
                    Assert.assertEquals(controlData[((seq * (InBandBytestreamSessionTest.blockSize)) + i)], decodedData[i]);
                }
            }
        };
        // set acknowledgments for the data packets
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        for (int i = 0; i < ((controlData.length) / (InBandBytestreamSessionTest.blockSize)); i++) {
            protocol.addResponse(resultIQ, incrementingSequence, dataVerification);
        }
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        OutputStream outputStream = session.getOutputStream();
        outputStream.write(controlData);
        outputStream.flush();
        protocol.verifyAll();
    }

    /**
     * If the input stream is closed the output stream should not be closed as well.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotCloseBothStreamsIfOutputStreamIsClosed() throws Exception {
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        OutputStream outputStream = session.getOutputStream();
        outputStream.close();
        // verify data packet confirmation is of type RESULT
        protocol.addResponse(null, Verification.requestTypeRESULT);
        // insert data to read
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, base64Data);
        Data data = new Data(dpe);
        listener.processStanza(data);
        // verify no packet send
        protocol.verifyAll();
        try {
            outputStream.flush();
            Assert.fail("should throw an exception");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("closed"));
        }
        Assert.assertTrue(((inputStream.read()) != 0));
    }

    /**
     * Valid data packets should be confirmed.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldConfirmReceivedDataPacket() throws Exception {
        // verify data packet confirmation is of type RESULT
        protocol.addResponse(null, Verification.requestTypeRESULT);
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, base64Data);
        Data data = new Data(dpe);
        listener.processStanza(data);
        protocol.verifyAll();
    }

    /**
     * If the data stanza has a sequence that is already used an 'unexpected-request' error should
     * be returned. See XEP-0047 Section 2.2.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReplyWithErrorIfAlreadyUsedSequenceIsReceived() throws Exception {
        // verify reply to first valid data packet is of type RESULT
        protocol.addResponse(null, Verification.requestTypeRESULT);
        // verify reply to invalid data packet is an error
        protocol.addResponse(null, Verification.requestTypeERROR, new Verification<IQ, IQ>() {
            @Override
            public void verify(IQ request, IQ response) {
                Assert.assertEquals(unexpected_request, request.getError().getCondition());
            }
        });
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build data packets
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, base64Data);
        Data data1 = new Data(dpe);
        Data data2 = new Data(dpe);
        // notify listener
        listener.processStanza(data1);
        listener.processStanza(data2);
        protocol.verifyAll();
    }

    /**
     * If the data stanza contains invalid Base64 encoding an 'bad-request' error should be
     * returned. See XEP-0047 Section 2.2.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReplyWithErrorIfDataIsInvalid() throws Exception {
        // verify reply to invalid data packet is an error
        protocol.addResponse(null, Verification.requestTypeERROR, new Verification<IQ, IQ>() {
            @Override
            public void verify(IQ request, IQ response) {
                Assert.assertEquals(bad_request, request.getError().getCondition());
            }
        });
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build data packets
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, "AA=BB");
        Data data = new Data(dpe);
        // notify listener
        listener.processStanza(data);
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
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        // confirm data packet with invalid sequence
        protocol.addResponse(resultIQ);
        // confirm close request
        protocol.addResponse(resultIQ, Verification.requestTypeSET, Verification.correspondingSenderReceiver);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build invalid packet with out of order sequence
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 123, base64Data);
        Data data = new Data(dpe);
        // add data packets
        listener.processStanza(data);
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
        byte[] controlData = new byte[3 * (InBandBytestreamSessionTest.blockSize)];
        rand.nextBytes(controlData);
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // set data packet acknowledgment and notify listener
        for (int i = 0; i < ((controlData.length) / (InBandBytestreamSessionTest.blockSize)); i++) {
            protocol.addResponse(resultIQ);
            String base64Data = Base64.encodeToString(controlData, (i * (InBandBytestreamSessionTest.blockSize)), InBandBytestreamSessionTest.blockSize);
            DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, i, base64Data);
            Data data = new Data(dpe);
            listener.processStanza(data);
        }
        byte[] bytes = new byte[3 * (InBandBytestreamSessionTest.blockSize)];
        int read = 0;
        read = inputStream.read(bytes, 0, InBandBytestreamSessionTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionTest.blockSize, read);
        read = inputStream.read(bytes, 10, InBandBytestreamSessionTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionTest.blockSize, read);
        read = inputStream.read(bytes, 20, InBandBytestreamSessionTest.blockSize);
        Assert.assertEquals(InBandBytestreamSessionTest.blockSize, read);
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
        byte[] controlData = new byte[3 * (InBandBytestreamSessionTest.blockSize)];
        rand.nextBytes(controlData);
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // set data packet acknowledgment and notify listener
        for (int i = 0; i < ((controlData.length) / (InBandBytestreamSessionTest.blockSize)); i++) {
            protocol.addResponse(resultIQ);
            String base64Data = Base64.encodeToString(controlData, (i * (InBandBytestreamSessionTest.blockSize)), InBandBytestreamSessionTest.blockSize);
            DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, i, base64Data);
            Data data = new Data(dpe);
            listener.processStanza(data);
        }
        // read data
        byte[] bytes = new byte[3 * (InBandBytestreamSessionTest.blockSize)];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (inputStream.read()));
        }
        // verify data
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(controlData[i], bytes[i]);
        }
        protocol.verifyAll();
    }

    /**
     * If the output stream is closed the input stream should not be closed as well.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotCloseBothStreamsIfInputStreamIsClosed() throws Exception {
        // acknowledgment for data packet
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build data packet
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, base64Data);
        Data data = new Data(dpe);
        // add data packets
        listener.processStanza(data);
        inputStream.close();
        protocol.verifyAll();
        try {
            while ((inputStream.read()) != (-1)) {
            } 
            inputStream.read();
            Assert.fail("should throw an exception");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("closed"));
        }
        session.getOutputStream().flush();
    }

    /**
     * If the input stream is closed concurrently there should be no deadlock.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotDeadlockIfInputStreamIsClosed() throws Exception {
        // acknowledgment for data packet
        IQ resultIQ = IBBPacketUtils.createResultIQ(InBandBytestreamSessionTest.initiatorJID, InBandBytestreamSessionTest.targetJID);
        protocol.addResponse(resultIQ);
        // get IBB sessions data packet listener
        InBandBytestreamSession session = new InBandBytestreamSession(connection, initBytestream, InBandBytestreamSessionTest.initiatorJID);
        final InputStream inputStream = session.getInputStream();
        StanzaListener listener = Whitebox.getInternalState(inputStream, StanzaListener.class);
        // build data packet
        String base64Data = Base64.encode("Data");
        DataPacketExtension dpe = new DataPacketExtension(InBandBytestreamSessionTest.sessionID, 0, base64Data);
        Data data = new Data(dpe);
        // add data packets
        listener.processStanza(data);
        Thread closer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    inputStream.close();
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        });
        closer.start();
        try {
            byte[] bytes = new byte[20];
            while ((inputStream.read(bytes)) != (-1)) {
            } 
            inputStream.read();
            Assert.fail("should throw an exception");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("closed"));
        }
        protocol.verifyAll();
    }
}

