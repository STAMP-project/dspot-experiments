/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.client;


import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Socks4ProxyTest {
    private ServerSocketChannel server;

    private HttpClient client;

    @Test
    public void testSocks4Proxy() throws Exception {
        int proxyPort = server.socket().getLocalPort();
        client.getProxyConfiguration().getProxies().add(new Socks4Proxy("localhost", proxyPort));
        final CountDownLatch latch = new CountDownLatch(1);
        byte ip1 = 127;
        byte ip2 = 0;
        byte ip3 = 0;
        byte ip4 = 13;
        String serverHost = (((((ip1 + ".") + ip2) + ".") + ip3) + ".") + ip4;
        int serverPort = proxyPort + 1;// Any port will do

        String method = "GET";
        String path = "/path";
        client.newRequest(serverHost, serverPort).method(method).path(path).timeout(5, TimeUnit.SECONDS).send(( result) -> {
            if (result.isSucceeded())
                latch.countDown();

        });
        try (SocketChannel channel = server.accept()) {
            int socks4MessageLength = 9;
            ByteBuffer buffer = ByteBuffer.allocate(socks4MessageLength);
            int read = channel.read(buffer);
            Assertions.assertEquals(socks4MessageLength, read);
            Assertions.assertEquals(4, ((buffer.get(0)) & 255));
            Assertions.assertEquals(1, ((buffer.get(1)) & 255));
            Assertions.assertEquals(serverPort, ((buffer.getShort(2)) & 65535));
            Assertions.assertEquals(ip1, ((buffer.get(4)) & 255));
            Assertions.assertEquals(ip2, ((buffer.get(5)) & 255));
            Assertions.assertEquals(ip3, ((buffer.get(6)) & 255));
            Assertions.assertEquals(ip4, ((buffer.get(7)) & 255));
            Assertions.assertEquals(0, ((buffer.get(8)) & 255));
            // Socks4 response.
            channel.write(ByteBuffer.wrap(new byte[]{ 0, 90, 0, 0, 0, 0, 0, 0 }));
            buffer = ByteBuffer.allocate((((method.length()) + 1) + (path.length())));
            read = channel.read(buffer);
            Assertions.assertEquals(buffer.capacity(), read);
            buffer.flip();
            Assertions.assertEquals(((method + " ") + path), StandardCharsets.UTF_8.decode(buffer).toString());
            // Response
            String response = "" + ((("HTTP/1.1 200 OK\r\n" + "Content-Length: 0\r\n") + "Connection: close\r\n") + "\r\n");
            channel.write(ByteBuffer.wrap(response.getBytes("UTF-8")));
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testSocks4ProxyWithSplitResponse() throws Exception {
        int proxyPort = server.socket().getLocalPort();
        client.getProxyConfiguration().getProxies().add(new Socks4Proxy("localhost", proxyPort));
        final CountDownLatch latch = new CountDownLatch(1);
        String serverHost = "127.0.0.13";// Test expects an IP address.

        int serverPort = proxyPort + 1;// Any port will do

        String method = "GET";
        client.newRequest(serverHost, serverPort).method(method).path("/path").timeout(5, TimeUnit.SECONDS).send(( result) -> {
            if (result.isSucceeded())
                latch.countDown();
            else
                result.getFailure().printStackTrace();

        });
        try (SocketChannel channel = server.accept()) {
            int socks4MessageLength = 9;
            ByteBuffer buffer = ByteBuffer.allocate(socks4MessageLength);
            int read = channel.read(buffer);
            Assertions.assertEquals(socks4MessageLength, read);
            // Socks4 response, with split bytes.
            byte[] chunk1 = new byte[]{ 0, 90, 0 };
            byte[] chunk2 = new byte[]{ 0, 0, 0, 0, 0 };
            channel.write(ByteBuffer.wrap(chunk1));
            // Wait before sending the second chunk.
            Thread.sleep(1000);
            channel.write(ByteBuffer.wrap(chunk2));
            buffer = ByteBuffer.allocate(method.length());
            read = channel.read(buffer);
            Assertions.assertEquals(buffer.capacity(), read);
            buffer.flip();
            Assertions.assertEquals(method, StandardCharsets.UTF_8.decode(buffer).toString());
            // Response
            String response = "" + ((("HTTP/1.1 200 OK\r\n" + "Content-Length: 0\r\n") + "Connection: close\r\n") + "\r\n");
            channel.write(ByteBuffer.wrap(response.getBytes("UTF-8")));
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }
}

