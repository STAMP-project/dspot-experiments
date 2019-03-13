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
package org.eclipse.jetty.client.ssl;


import HttpScheme.HTTPS;
import HttpStatus.OK_200;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FutureResponseListener;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.function.Executable;

import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.ALERT;
import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.CHANGE_CIPHER_SPEC;
import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.HANDSHAKE;


// This whole test is very specific to how TLS < 1.3 works.
// Starting in Java 11, TLS/1.3 is now enabled by default.
@EnabledOnJre({ JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10 })
public class SslBytesClientTest extends SslBytesTest {
    private ExecutorService threadPool;

    private HttpClient client;

    private SslContextFactory sslContextFactory;

    private SSLServerSocket acceptor;

    private SslBytesTest.SimpleProxy proxy;

    @Test
    public void testHandshake() throws Exception {
        Request request = client.newRequest("localhost", proxy.getPort());
        FutureResponseListener listener = new FutureResponseListener(request);
        request.scheme(HTTPS.asString()).send(listener);
        Assertions.assertTrue(proxy.awaitClient(5, TimeUnit.SECONDS));
        try (SSLSocket server = ((SSLSocket) (acceptor.accept()))) {
            server.setUseClientMode(false);
            Future<Object> handshake = threadPool.submit(() -> {
                server.startHandshake();
                return null;
            });
            // Client Hello
            SslBytesTest.TLSRecord record = proxy.readFromClient();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToServer(record);
            // Server Hello + Certificate + Server Done
            record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            // Client Key Exchange
            record = proxy.readFromClient();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToServer(record);
            // Change Cipher Spec
            record = proxy.readFromClient();
            Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
            proxy.flushToServer(record);
            // Client Done
            record = proxy.readFromClient();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToServer(record);
            // Change Cipher Spec
            record = proxy.readFromServer();
            Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
            proxy.flushToClient(record);
            // Server Done
            record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            Assertions.assertNull(handshake.get(5, TimeUnit.SECONDS));
            SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
            // Read request
            BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));
            String line = reader.readLine();
            Assertions.assertTrue(line.startsWith("GET"));
            while ((line.length()) > 0)
                line = reader.readLine();

            // Write response
            OutputStream output = server.getOutputStream();
            output.write(("HTTP/1.1 200 OK\r\n" + ("Content-Length: 0\r\n" + "\r\n")).getBytes(StandardCharsets.UTF_8));
            output.flush();
            Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
            ContentResponse response = listener.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(OK_200, response.getStatus());
        }
    }

    @Test
    public void testServerRenegotiation() throws Exception {
        Request request = client.newRequest("localhost", proxy.getPort());
        FutureResponseListener listener = new FutureResponseListener(request);
        request.scheme(HTTPS.asString()).send(listener);
        Assertions.assertTrue(proxy.awaitClient(5, TimeUnit.SECONDS));
        try (SSLSocket server = ((SSLSocket) (acceptor.accept()))) {
            server.setUseClientMode(false);
            Future<Object> handshake = threadPool.submit(() -> {
                server.startHandshake();
                return null;
            });
            SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
            Assertions.assertNull(handshake.get(5, TimeUnit.SECONDS));
            // Read request
            InputStream serverInput = server.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(serverInput, StandardCharsets.UTF_8));
            String line = reader.readLine();
            Assertions.assertTrue(line.startsWith("GET"));
            while ((line.length()) > 0)
                line = reader.readLine();

            OutputStream serverOutput = server.getOutputStream();
            byte[] data1 = new byte[1024];
            Arrays.fill(data1, ((byte) ('X')));
            String content1 = new String(data1, StandardCharsets.UTF_8);
            byte[] data2 = new byte[1024];
            Arrays.fill(data2, ((byte) ('Y')));
            final String content2 = new String(data2, StandardCharsets.UTF_8);
            // Write first part of the response
            serverOutput.write(((((("HTTP/1.1 200 OK\r\n" + ("Content-Type: text/plain\r\n" + "Content-Length: ")) + ((content1.length()) + (content2.length()))) + "\r\n") + "\r\n") + content1).getBytes(StandardCharsets.UTF_8));
            serverOutput.flush();
            Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
            // Renegotiate
            Future<Object> renegotiation = threadPool.submit(() -> {
                server.startHandshake();
                return null;
            });
            // Renegotiation Handshake
            SslBytesTest.TLSRecord record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            // Renegotiation Handshake
            record = proxy.readFromClient();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToServer(record);
            // Trigger a read to have the server write the final renegotiation steps
            server.setSoTimeout(100);
            Assertions.assertThrows(SocketTimeoutException.class, () -> serverInput.read());
            // Renegotiation Handshake
            record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            // Renegotiation Change Cipher
            record = proxy.readFromServer();
            Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
            proxy.flushToClient(record);
            // Renegotiation Handshake
            record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            // Renegotiation Change Cipher
            record = proxy.readFromClient();
            Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
            proxy.flushToServer(record);
            // Renegotiation Handshake
            record = proxy.readFromClient();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToServer(record);
            Assertions.assertNull(renegotiation.get(5, TimeUnit.SECONDS));
            // Complete the response
            automaticProxyFlow = proxy.startAutomaticFlow();
            serverOutput.write(data2);
            serverOutput.flush();
            Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
            ContentResponse response = listener.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(OK_200, response.getStatus());
            Assertions.assertEquals(((data1.length) + (data2.length)), response.getContent().length);
        }
    }

    @Test
    public void testServerRenegotiationWhenRenegotiationIsForbidden() throws Exception {
        sslContextFactory.setRenegotiationAllowed(false);
        Request request = client.newRequest("localhost", proxy.getPort());
        FutureResponseListener listener = new FutureResponseListener(request);
        request.scheme(HTTPS.asString()).send(listener);
        Assertions.assertTrue(proxy.awaitClient(5, TimeUnit.SECONDS));
        try (SSLSocket server = ((SSLSocket) (acceptor.accept()))) {
            server.setUseClientMode(false);
            Future<Object> handshake = threadPool.submit(() -> {
                server.startHandshake();
                return null;
            });
            SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
            Assertions.assertNull(handshake.get(5, TimeUnit.SECONDS));
            // Read request
            InputStream serverInput = server.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(serverInput, StandardCharsets.UTF_8));
            String line = reader.readLine();
            Assertions.assertTrue(line.startsWith("GET"));
            while ((line.length()) > 0)
                line = reader.readLine();

            OutputStream serverOutput = server.getOutputStream();
            byte[] data1 = new byte[1024];
            Arrays.fill(data1, ((byte) ('X')));
            String content1 = new String(data1, StandardCharsets.UTF_8);
            byte[] data2 = new byte[1024];
            Arrays.fill(data2, ((byte) ('Y')));
            final String content2 = new String(data2, StandardCharsets.UTF_8);
            // Write first part of the response
            serverOutput.write(((((("HTTP/1.1 200 OK\r\n" + ("Content-Type: text/plain\r\n" + "Content-Length: ")) + ((content1.length()) + (content2.length()))) + "\r\n") + "\r\n") + content1).getBytes(StandardCharsets.UTF_8));
            serverOutput.flush();
            Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
            // Renegotiate
            threadPool.submit(() -> {
                server.startHandshake();
                return null;
            });
            // Renegotiation Handshake
            SslBytesTest.TLSRecord record = proxy.readFromServer();
            Assertions.assertEquals(HANDSHAKE, record.getType());
            proxy.flushToClient(record);
            // Client sends close alert.
            record = proxy.readFromClient();
            Assertions.assertEquals(ALERT, record.getType());
            record = proxy.readFromClient();
            Assertions.assertNull(record);
        }
    }
}

