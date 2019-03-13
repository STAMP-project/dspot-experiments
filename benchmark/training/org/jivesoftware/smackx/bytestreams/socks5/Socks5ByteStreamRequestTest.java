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
package org.jivesoftware.smackx.bytestreams.socks5;


import IQ.Type.error;
import IQ.Type.result;
import StanzaError.Condition.item_not_found;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.util.Protocol;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;
import org.jxmpp.jid.impl.JidCreate;


/**
 * Tests for the Socks5BytestreamRequest class.
 *
 * @author Henning Staib
 */
public class Socks5ByteStreamRequestTest {
    // settings
    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private static final DomainBareJid proxyJID = JidTestUtil.MUC_EXAMPLE_ORG;

    private static final String proxyAddress = "127.0.0.1";

    private static final String sessionID = "session_id";

    private Protocol protocol;

    private XMPPConnection connection;

    /**
     * Accepting a SOCKS5 Bytestream request should fail if the request doesn't contain any Socks5
     * proxies.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldFailIfRequestHasNoStreamHosts() throws Exception {
        try {
            // build SOCKS5 Bytestream initialization request with no SOCKS5 proxies
            Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
            // get SOCKS5 Bytestream manager for connection
            Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
            // build SOCKS5 Bytestream request with the bytestream initialization
            Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
            // accept the stream (this is the call that is tested here)
            byteStreamRequest.accept();
            Assert.fail("exception should be thrown");
        } catch (XMPPErrorException e) {
            Assert.assertTrue(e.getStanzaError().getDescriptiveText("en").contains("Could not establish socket with any provided host"));
        }
        // verify targets response
        Assert.assertEquals(1, protocol.getRequests().size());
        Stanza targetResponse = protocol.getRequests().remove(0);
        Assert.assertTrue(IQ.class.isInstance(targetResponse));
        Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
        Assert.assertEquals(error, getType());
        Assert.assertEquals(item_not_found, targetResponse.getError().getCondition());
    }

    /**
     * Accepting a SOCKS5 Bytestream request should fail if target is not able to connect to any of
     * the provided SOCKS5 proxies.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldFailIfRequestHasInvalidStreamHosts() throws Exception {
        try {
            // build SOCKS5 Bytestream initialization request
            Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
            // add proxy that is not running
            bytestreamInitialization.addStreamHost(Socks5ByteStreamRequestTest.proxyJID, Socks5ByteStreamRequestTest.proxyAddress, 7778);
            // get SOCKS5 Bytestream manager for connection
            Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
            // build SOCKS5 Bytestream request with the bytestream initialization
            Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
            // accept the stream (this is the call that is tested here)
            byteStreamRequest.accept();
            Assert.fail("exception should be thrown");
        } catch (XMPPErrorException e) {
            Assert.assertTrue(e.getStanzaError().getDescriptiveText("en").contains("Could not establish socket with any provided host"));
        }
        // verify targets response
        Assert.assertEquals(1, protocol.getRequests().size());
        Stanza targetResponse = protocol.getRequests().remove(0);
        Assert.assertTrue(IQ.class.isInstance(targetResponse));
        Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
        Assert.assertEquals(error, getType());
        Assert.assertEquals(item_not_found, targetResponse.getError().getCondition());
    }

    /**
     * Target should not try to connect to SOCKS5 proxies that already failed twice.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldBlacklistInvalidProxyAfter2Failures() throws Exception {
        // build SOCKS5 Bytestream initialization request
        Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
        bytestreamInitialization.addStreamHost(JidCreate.from(("invalid." + (Socks5ByteStreamRequestTest.proxyJID))), "127.0.0.2", 7778);
        // get SOCKS5 Bytestream manager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        // try to connect several times
        for (int i = 0; i < 2; i++) {
            try {
                // build SOCKS5 Bytestream request with the bytestream initialization
                Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
                // set timeouts
                byteStreamRequest.setTotalConnectTimeout(600);
                byteStreamRequest.setMinimumConnectTimeout(300);
                // accept the stream (this is the call that is tested here)
                byteStreamRequest.accept();
                Assert.fail("exception should be thrown");
            } catch (XMPPErrorException e) {
                Assert.assertTrue(e.getStanzaError().getDescriptiveText("en").contains("Could not establish socket with any provided host"));
            }
            // verify targets response
            Assert.assertEquals(1, protocol.getRequests().size());
            Stanza targetResponse = protocol.getRequests().remove(0);
            Assert.assertTrue(IQ.class.isInstance(targetResponse));
            Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
            Assert.assertEquals(error, getType());
            Assert.assertEquals(item_not_found, targetResponse.getError().getCondition());
        }
        // create test data for stream
        byte[] data = new byte[]{ 1, 2, 3 };
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7779);
        Assert.assertTrue(socks5Proxy.isRunning());
        // add a valid SOCKS5 proxy
        bytestreamInitialization.addStreamHost(Socks5ByteStreamRequestTest.proxyJID, Socks5ByteStreamRequestTest.proxyAddress, 7779);
        // build SOCKS5 Bytestream request with the bytestream initialization
        Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
        // set timeouts
        byteStreamRequest.setTotalConnectTimeout(600);
        byteStreamRequest.setMinimumConnectTimeout(300);
        // accept the stream (this is the call that is tested here)
        InputStream inputStream = byteStreamRequest.accept().getInputStream();
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamRequestTest.sessionID, Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID);
        // test stream by sending some data
        OutputStream outputStream = socks5Proxy.getSocket(digest).getOutputStream();
        outputStream.write(data);
        // verify that data is transferred correctly
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        // verify targets response
        Assert.assertEquals(1, protocol.getRequests().size());
        Stanza targetResponse = protocol.getRequests().remove(0);
        Assert.assertEquals(Bytestream.class, targetResponse.getClass());
        Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
        Assert.assertEquals(result, getType());
        Assert.assertEquals(Socks5ByteStreamRequestTest.proxyJID, getUsedHost().getJID());
    }

    /**
     * Target should not not blacklist any SOCKS5 proxies regardless of failing connections.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotBlacklistInvalidProxy() throws Exception {
        // disable blacklisting
        Socks5BytestreamRequest.setConnectFailureThreshold(0);
        // build SOCKS5 Bytestream initialization request
        Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
        bytestreamInitialization.addStreamHost(JidCreate.from(("invalid." + (Socks5ByteStreamRequestTest.proxyJID))), "127.0.0.2", 7778);
        // get SOCKS5 Bytestream manager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        // try to connect several times
        for (int i = 0; i < 10; i++) {
            try {
                // build SOCKS5 Bytestream request with the bytestream initialization
                Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
                // set timeouts
                byteStreamRequest.setTotalConnectTimeout(600);
                byteStreamRequest.setMinimumConnectTimeout(300);
                // accept the stream (this is the call that is tested here)
                byteStreamRequest.accept();
                Assert.fail("exception should be thrown");
            } catch (XMPPErrorException e) {
                Assert.assertTrue(e.getStanzaError().getDescriptiveText("en").contains("Could not establish socket with any provided host"));
            }
            // verify targets response
            Assert.assertEquals(1, protocol.getRequests().size());
            Stanza targetResponse = protocol.getRequests().remove(0);
            Assert.assertTrue(IQ.class.isInstance(targetResponse));
            Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
            Assert.assertEquals(error, getType());
            Assert.assertEquals(item_not_found, targetResponse.getError().getCondition());
        }
        // enable blacklisting
        Socks5BytestreamRequest.setConnectFailureThreshold(2);
    }

    /**
     * If the SOCKS5 Bytestream request contains multiple SOCKS5 proxies and the first one doesn't
     * respond, the connection attempt to this proxy should not consume the whole timeout for
     * connecting to the proxies.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotTimeoutIfFirstSocks5ProxyDoesNotRespond() throws Exception {
        // start a local SOCKS5 proxy
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7778);
        // create a fake SOCKS5 proxy that doesn't respond to a request
        ServerSocket serverSocket = new ServerSocket(7779);
        // build SOCKS5 Bytestream initialization request
        Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
        bytestreamInitialization.addStreamHost(Socks5ByteStreamRequestTest.proxyJID, Socks5ByteStreamRequestTest.proxyAddress, 7779);
        bytestreamInitialization.addStreamHost(Socks5ByteStreamRequestTest.proxyJID, Socks5ByteStreamRequestTest.proxyAddress, 7778);
        // create test data for stream
        byte[] data = new byte[]{ 1, 2, 3 };
        // get SOCKS5 Bytestream manager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        // build SOCKS5 Bytestream request with the bytestream initialization
        Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
        // set timeouts
        byteStreamRequest.setTotalConnectTimeout(2000);
        byteStreamRequest.setMinimumConnectTimeout(1000);
        // accept the stream (this is the call that is tested here)
        InputStream inputStream = byteStreamRequest.accept().getInputStream();
        // assert that client tries to connect to dumb SOCKS5 proxy
        Socket socket = serverSocket.accept();
        Assert.assertNotNull(socket);
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamRequestTest.sessionID, Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID);
        // test stream by sending some data
        OutputStream outputStream = socks5Proxy.getSocket(digest).getOutputStream();
        outputStream.write(data);
        // verify that data is transferred correctly
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        // verify targets response
        Assert.assertEquals(1, protocol.getRequests().size());
        Stanza targetResponse = protocol.getRequests().remove(0);
        Assert.assertEquals(Bytestream.class, targetResponse.getClass());
        Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
        Assert.assertEquals(result, getType());
        Assert.assertEquals(Socks5ByteStreamRequestTest.proxyJID, getUsedHost().getJID());
        serverSocket.close();
    }

    /**
     * Accepting the SOCKS5 Bytestream request should be successfully.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldAcceptSocks5BytestreamRequestAndReceiveData() throws Exception {
        // start a local SOCKS5 proxy
        Socks5TestProxy socks5Proxy = Socks5TestProxy.getProxy(7778);
        // build SOCKS5 Bytestream initialization request
        Bytestream bytestreamInitialization = Socks5PacketUtils.createBytestreamInitiation(Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID, Socks5ByteStreamRequestTest.sessionID);
        bytestreamInitialization.addStreamHost(Socks5ByteStreamRequestTest.proxyJID, Socks5ByteStreamRequestTest.proxyAddress, 7778);
        // create test data for stream
        byte[] data = new byte[]{ 1, 2, 3 };
        // get SOCKS5 Bytestream manager for connection
        Socks5BytestreamManager byteStreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
        // build SOCKS5 Bytestream request with the bytestream initialization
        Socks5BytestreamRequest byteStreamRequest = new Socks5BytestreamRequest(byteStreamManager, bytestreamInitialization);
        // accept the stream (this is the call that is tested here)
        InputStream inputStream = byteStreamRequest.accept().getInputStream();
        // create digest to get the socket opened by target
        String digest = Socks5Utils.createDigest(Socks5ByteStreamRequestTest.sessionID, Socks5ByteStreamRequestTest.initiatorJID, Socks5ByteStreamRequestTest.targetJID);
        // test stream by sending some data
        OutputStream outputStream = socks5Proxy.getSocket(digest).getOutputStream();
        outputStream.write(data);
        // verify that data is transferred correctly
        byte[] result = new byte[3];
        inputStream.read(result);
        Assert.assertArrayEquals(data, result);
        // verify targets response
        Assert.assertEquals(1, protocol.getRequests().size());
        Stanza targetResponse = protocol.getRequests().remove(0);
        Assert.assertEquals(Bytestream.class, targetResponse.getClass());
        Assert.assertEquals(Socks5ByteStreamRequestTest.initiatorJID, targetResponse.getTo());
        Assert.assertEquals(result, getType());
        Assert.assertEquals(Socks5ByteStreamRequestTest.proxyJID, getUsedHost().getJID());
    }
}

