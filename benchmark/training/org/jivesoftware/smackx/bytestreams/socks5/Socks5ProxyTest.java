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


import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.jivesoftware.smack.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for Socks5Proxy class.
 *
 * @author Henning Staib
 */
public class Socks5ProxyTest {
    private static final String loopbackAddress = InetAddress.getLoopbackAddress().getHostAddress();

    /**
     * The SOCKS5 proxy should be a singleton used by all XMPP connections.
     */
    @Test
    public void shouldBeASingleton() {
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        Socks5Proxy proxy1 = Socks5Proxy.getSocks5Proxy();
        Socks5Proxy proxy2 = Socks5Proxy.getSocks5Proxy();
        Assert.assertNotNull(proxy1);
        Assert.assertNotNull(proxy2);
        Assert.assertSame(proxy1, proxy2);
    }

    /**
     * The SOCKS5 proxy should not be started if disabled by configuration.
     */
    @Test
    public void shouldNotBeRunningIfDisabled() {
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        Assert.assertFalse(proxy.isRunning());
    }

    /**
     * The SOCKS5 proxy should use a free port above the one configured.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldUseFreePortOnNegativeValues() throws Exception {
        Socks5Proxy.setLocalSocks5ProxyEnabled(false);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        Assert.assertFalse(proxy.isRunning());
        ServerSocket serverSocket = new ServerSocket(0);
        Socks5Proxy.setLocalSocks5ProxyPort((-(serverSocket.getLocalPort())));
        proxy.start();
        Assert.assertTrue(proxy.isRunning());
        serverSocket.close();
        Assert.assertTrue(((proxy.getPort()) > (serverSocket.getLocalPort())));
    }

    /**
     * When inserting new network addresses to the proxy the order should remain in the order they
     * were inserted.
     */
    @Test
    public void shouldPreserveAddressOrderOnInsertions() {
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        LinkedHashSet<String> addresses = new LinkedHashSet(proxy.getLocalAddresses());
        for (int i = 1; i <= 3; i++) {
            addresses.add(Integer.toString(i));
        }
        for (String address : addresses) {
            proxy.addLocalAddress(address);
        }
        List<String> localAddresses = proxy.getLocalAddresses();
        Iterator<String> iterator = addresses.iterator();
        for (int i = 0; i < (addresses.size()); i++) {
            Assert.assertEquals(iterator.next(), localAddresses.get(i));
        }
    }

    /**
     * When replacing network addresses of the proxy the order should remain in the order if the
     * given list.
     */
    @Test
    public void shouldPreserveAddressOrderOnReplace() {
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        List<String> addresses = new java.util.ArrayList(proxy.getLocalAddresses());
        addresses.add("1");
        addresses.add("2");
        addresses.add("3");
        proxy.replaceLocalAddresses(addresses);
        List<String> localAddresses = proxy.getLocalAddresses();
        for (int i = 0; i < (addresses.size()); i++) {
            Assert.assertEquals(addresses.get(i), localAddresses.get(i));
        }
    }

    /**
     * Inserting the same address multiple times should not cause the proxy to return this address
     * multiple times.
     */
    @Test
    public void shouldNotReturnMultipleSameAddress() {
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        proxy.addLocalAddress("same");
        proxy.addLocalAddress("same");
        proxy.addLocalAddress("same");
        int sameCount = 0;
        for (String localAddress : proxy.getLocalAddresses()) {
            if ("same".equals(localAddress)) {
                sameCount++;
            }
        }
        Assert.assertEquals(1, sameCount);
    }

    /**
     * If the SOCKS5 proxy accepts a connection that is not a SOCKS5 connection it should close the
     * corresponding socket.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldCloseSocketIfNoSocks5Request() throws Exception {
        Socks5Proxy.setLocalSocks5ProxyPort(7890);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        proxy.start();
        @SuppressWarnings("resource")
        Socket socket = new Socket(Socks5ProxyTest.loopbackAddress, proxy.getPort());
        OutputStream out = socket.getOutputStream();
        out.write(new byte[]{ 1, 2, 3 });
        int res;
        try {
            res = socket.getInputStream().read();
        } catch (SocketException e) {
            res = -1;
        }
        Assert.assertEquals((-1), res);
        proxy.stop();
    }

    /**
     * The SOCKS5 proxy should reply with an error message if no supported authentication methods
     * are given in the SOCKS5 request.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldRespondWithErrorIfNoSupportedAuthenticationMethod() throws Exception {
        Socks5Proxy.setLocalSocks5ProxyPort(7890);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        proxy.start();
        @SuppressWarnings("resource")
        Socket socket = new Socket(Socks5ProxyTest.loopbackAddress, proxy.getPort());
        OutputStream out = socket.getOutputStream();
        // request username/password-authentication
        out.write(new byte[]{ ((byte) (5)), ((byte) (1)), ((byte) (2)) });
        InputStream in = socket.getInputStream();
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (255)), ((byte) (in.read())));
        Assert.assertEquals((-1), in.read());
        proxy.stop();
    }

    /**
     * The SOCKS5 proxy should respond with an error message if the client is not allowed to connect
     * with the proxy.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldRespondWithErrorIfConnectionIsNotAllowed() throws Exception {
        Socks5Proxy.setLocalSocks5ProxyPort(7890);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        proxy.start();
        @SuppressWarnings("resource")
        Socket socket = new Socket(Socks5ProxyTest.loopbackAddress, proxy.getPort());
        OutputStream out = socket.getOutputStream();
        out.write(new byte[]{ ((byte) (5)), ((byte) (1)), ((byte) (0)) });
        InputStream in = socket.getInputStream();
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        // send valid SOCKS5 message
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)), ((byte) (0)), ((byte) (3)), ((byte) (1)), ((byte) (170)), ((byte) (0)), ((byte) (0)) });
        // verify error message
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));
        Assert.assertFalse((((byte) (0)) == ((byte) (in.read()))));// something other than 0 == success

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (3)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (170)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Assert.assertEquals((-1), in.read());
        proxy.stop();
    }

    /**
     * A Client should successfully establish a connection to the SOCKS5 proxy.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldSuccessfullyEstablishConnection() throws Exception {
        Socks5Proxy.setLocalSocks5ProxyPort(7890);
        Socks5Proxy proxy = Socks5Proxy.getSocks5Proxy();
        proxy.start();
        Assert.assertTrue(proxy.isRunning());
        String digest = new String(new byte[]{ ((byte) (170)) }, StringUtils.UTF8);
        // add digest to allow connection
        proxy.addTransfer(digest);
        @SuppressWarnings("resource")
        Socket socket = new Socket(Socks5ProxyTest.loopbackAddress, proxy.getPort());
        OutputStream out = socket.getOutputStream();
        out.write(new byte[]{ ((byte) (5)), ((byte) (1)), ((byte) (0)) });
        InputStream in = socket.getInputStream();
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        // send valid SOCKS5 message
        out.write(new byte[]{ ((byte) (5)), ((byte) (0)), ((byte) (0)), ((byte) (3)), ((byte) (1)), ((byte) (170)), ((byte) (0)), ((byte) (0)) });
        // verify response
        Assert.assertEquals(((byte) (5)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));// success

        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (3)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (1)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (170)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Assert.assertEquals(((byte) (0)), ((byte) (in.read())));
        Thread.sleep(200);
        Socket remoteSocket = proxy.getSocket(digest);
        // remove digest
        proxy.removeTransfer(digest);
        // test stream
        OutputStream remoteOut = remoteSocket.getOutputStream();
        byte[] data = new byte[]{ 1, 2, 3, 4, 5 };
        remoteOut.write(data);
        remoteOut.flush();
        for (int i = 0; i < (data.length); i++) {
            Assert.assertEquals(data[i], in.read());
        }
        remoteSocket.close();
        Assert.assertEquals((-1), in.read());
        proxy.stop();
    }
}

