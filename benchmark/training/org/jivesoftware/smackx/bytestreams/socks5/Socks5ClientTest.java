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


import StringUtils.UTF8;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream.StreamHost;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.JidTestUtil;


/**
 * Test for Socks5Client class.
 *
 * @author Henning Staib
 */
public class Socks5ClientTest {
    // settings
    private int serverPort;

    private String serverAddress;

    private DomainBareJid proxyJID = JidTestUtil.MUC_EXAMPLE_ORG;

    private String digest = "digest";

    private ServerSocket serverSocket;

    /**
     * A SOCKS5 client MUST close connection if server doesn't accept any of the given
     * authentication methods. (See RFC1928 Section 3)
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldCloseSocketIfServerDoesNotAcceptAuthenticationMethod() throws Exception {
        // start thread to connect to SOCKS5 proxy
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                StreamHost streamHost = new StreamHost(proxyJID, serverAddress, serverPort);
                Socks5Client socks5Client = new Socks5Client(streamHost, digest);
                try {
                    socks5Client.getSocket(10000);
                    Assert.fail("exception should be thrown");
                } catch (SmackException e) {
                    Assert.assertTrue(e.getMessage().contains("SOCKS5 negotiation failed"));
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        serverThread.start();
        // accept connection form client
        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        // validate authentication request
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));// version

        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));// number of supported auth methods

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// no-authentication method

        // respond that no authentication method is accepted
        out.write(new byte[]{ ((byte) (5)), ((byte) (255)) });
        out.flush();
        // wait for client to shutdown
        serverThread.join();
        // assert socket is closed
        Assert.assertEquals((-1), in.read());
    }

    /**
     * The SOCKS5 client should close connection if server replies in an unsupported way.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldCloseSocketIfServerRepliesInUnsupportedWay() throws Exception {
        // start thread to connect to SOCKS5 proxy
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                StreamHost streamHost = new StreamHost(proxyJID, serverAddress, serverPort);
                Socks5Client socks5Client = new Socks5Client(streamHost, digest);
                try {
                    socks5Client.getSocket(10000);
                    Assert.fail("exception should be thrown");
                } catch (SmackException e) {
                    Assert.assertTrue(e.getMessage().contains("Unsupported SOCKS5 address type"));
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        serverThread.start();
        // accept connection from client
        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        // validate authentication request
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));// version

        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));// number of supported auth methods

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// no-authentication method

        // respond that no no-authentication method is used
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)) });
        out.flush();
        Socks5Utils.receiveSocks5Message(in);
        // reply with unsupported address type
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)), ((byte) (0)), ((byte) (1)), ((byte) (0)) });
        out.flush();
        // wait for client to shutdown
        serverThread.join();
        // assert socket is closed
        Assert.assertEquals((-1), in.read());
    }

    /**
     * The SOCKS5 client should close connection if server replies with an error.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldCloseSocketIfServerRepliesWithError() throws Exception {
        // start thread to connect to SOCKS5 proxy
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                StreamHost streamHost = new StreamHost(proxyJID, serverAddress, serverPort);
                Socks5Client socks5Client = new Socks5Client(streamHost, digest);
                try {
                    socks5Client.getSocket(10000);
                    Assert.fail("exception should be thrown");
                } catch (SmackException e) {
                    Assert.assertTrue(e.getMessage().contains("SOCKS5 negotiation failed"));
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        serverThread.start();
        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        // validate authentication request
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));// version

        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));// number of supported auth methods

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// no-authentication method

        // respond that no no-authentication method is used
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)) });
        out.flush();
        Socks5Utils.receiveSocks5Message(in);
        // reply with full SOCKS5 message with an error code (01 = general SOCKS server
        // failure)
        out.write(new byte[]{ ((byte) (5)), ((byte) (1)), ((byte) (0)), ((byte) (3)) });
        byte[] address = digest.getBytes(UTF8);
        out.write(address.length);
        out.write(address);
        out.write(new byte[]{ ((byte) (0)), ((byte) (0)) });
        out.flush();
        // wait for client to shutdown
        serverThread.join();
        // assert socket is closed
        Assert.assertEquals((-1), in.read());
    }

    /**
     * The SOCKS5 client should successfully connect to the SOCKS5 server.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSuccessfullyConnectToSocks5Server() throws Exception {
        // start thread to connect to SOCKS5 proxy
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                StreamHost streamHost = new StreamHost(proxyJID, serverAddress, serverPort);
                Socks5Client socks5Client = new Socks5Client(streamHost, digest);
                try {
                    Socket socket = socks5Client.getSocket(10000);
                    Assert.assertNotNull(socket);
                    socket.getOutputStream().write(123);
                    socket.close();
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        serverThread.start();
        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        // validate authentication request
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));// version

        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));// number of supported auth methods

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// no-authentication method

        // respond that no no-authentication method is used
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)) });
        out.flush();
        byte[] address = digest.getBytes(UTF8);
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));// version

        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));// connect request

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// reserved byte (always 0)

        Assert.assertEquals(((byte) (3)), ((byte) (in.read())));// address type (domain)

        Assert.assertEquals(address.length, ((byte) (in.read())));// address length

        for (int i = 0; i < (address.length); i++) {
            Assert.assertEquals(address[i], ((byte) (in.read())));// address

        }
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// port

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        // reply with success SOCKS5 message
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)), ((byte) (0)), ((byte) (3)) });
        out.write(address.length);
        out.write(address);
        out.write(new byte[]{ ((byte) (0)), ((byte) (0)) });
        out.flush();
        // wait for client to shutdown
        serverThread.join();
        // verify data sent from client
        Assert.assertEquals(123, in.read());
        // assert socket is closed
        Assert.assertEquals((-1), in.read());
    }
}

