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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;

import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.ALERT;
import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.APPLICATION;
import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.CHANGE_CIPHER_SPEC;
import static org.eclipse.jetty.client.ssl.SslBytesTest.TLSRecord.Type.HANDSHAKE;


// This whole test is very specific to how TLS < 1.3 works.
@EnabledOnJre({ JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10 })
public class SslBytesServerTest extends SslBytesTest {
    private final AtomicInteger sslFills = new AtomicInteger();

    private final AtomicInteger sslFlushes = new AtomicInteger();

    private final AtomicInteger httpParses = new AtomicInteger();

    private final AtomicReference<EndPoint> serverEndPoint = new AtomicReference<>();

    private final int idleTimeout = 2000;

    private ExecutorService threadPool;

    private Server server;

    private SslContextFactory sslContextFactory;

    private int serverPort;

    private SSLContext sslContext;

    private SslBytesTest.SimpleProxy proxy;

    private Runnable idleHook;

    @Test
    public void testHandshake() throws Exception {
        final SSLSocket client = newClient();
        Future<Object> handshake = threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Client Hello
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        // Server Hello + Certificate + Server Done
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Client Key Exchange
        record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        // Change Cipher Spec
        record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        // Client Done
        record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        // Change Cipher Spec
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Server Done
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        Assertions.assertNull(handshake.get(5, TimeUnit.SECONDS));
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        closeClient(client);
    }

    @Test
    public void testHandshakeWithResumedSessionThenClose() throws Exception {
        // First socket will establish the SSL session
        SSLSocket client1 = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client1.startHandshake();
        client1.close();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        int proxyPort = proxy.getPort();
        proxy.stop();
        proxy = new SslBytesTest.SimpleProxy(threadPool, proxyPort, "localhost", serverPort);
        proxy.start();
        logger.info("proxy:{} <==> server:{}", proxy.getPort(), serverPort);
        final SSLSocket client2 = newClient(proxy);
        threadPool.submit(() -> {
            client2.startHandshake();
            return null;
        });
        // Client Hello with SessionID
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        // Server Hello
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Change Cipher Spec
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Server Done
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Client Key Exchange
        record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        // Client Done
        SslBytesTest.TLSRecord doneRecord = proxy.readFromClient();
        Assertions.assertNotNull(doneRecord);
        // Close
        client2.close();
        SslBytesTest.TLSRecord closeRecord = proxy.readFromClient();
        Assertions.assertNotNull(closeRecord);
        Assertions.assertEquals(ALERT, closeRecord.getType());
        // Flush to server Client Key Exchange + Client Done + Close in one chunk
        byte[] recordBytes = record.getBytes();
        byte[] doneBytes = doneRecord.getBytes();
        byte[] closeRecordBytes = closeRecord.getBytes();
        byte[] chunk = new byte[((recordBytes.length) + (doneBytes.length)) + (closeRecordBytes.length)];
        System.arraycopy(recordBytes, 0, chunk, 0, recordBytes.length);
        System.arraycopy(doneBytes, 0, chunk, recordBytes.length, doneBytes.length);
        System.arraycopy(closeRecordBytes, 0, chunk, ((recordBytes.length) + (doneBytes.length)), closeRecordBytes.length);
        proxy.flushToServer(0, chunk);
        // Close the raw socket
        proxy.flushToServer(null);
        // Expect the server to send a TLS Alert.
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(ALERT, record.getType());
        record = proxy.readFromServer();
        Assertions.assertNull(record);
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
    }

    @Test
    public void testHandshakeWithSplitBoundary() throws Exception {
        final SSLSocket client = newClient();
        Future<Object> handshake = threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Client Hello
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        byte[] bytes = record.getBytes();
        byte[] chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        byte[] chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Server Hello + Certificate + Server Done
        record = proxy.readFromServer();
        proxy.flushToClient(record);
        // Client Key Exchange
        record = proxy.readFromClient();
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Change Cipher Spec
        record = proxy.readFromClient();
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Client Done
        record = proxy.readFromClient();
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Change Cipher Spec
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        // Server Done
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        proxy.flushToClient(record);
        Assertions.assertNull(handshake.get(5, TimeUnit.SECONDS));
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(40));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
        // Close Alert
        record = proxy.readFromClient();
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Socket close
        record = proxy.readFromClient();
        Assertions.assertNull(record, String.valueOf(record));
        proxy.flushToServer(record);
        // Socket close
        record = proxy.readFromServer();
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
    }

    @Test
    public void testClientHelloIncompleteThenReset() throws Exception {
        final SSLSocket client = newClient();
        threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Client Hello
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        byte[] bytes = record.getBytes();
        byte[] chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        proxy.flushToServer(100, chunk1);
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testClientHelloThenReset() throws Exception {
        final SSLSocket client = newClient();
        threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Client Hello
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertNotNull(record);
        proxy.flushToServer(record);
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testHandshakeThenReset() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testRequestIncompleteThenReset() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        byte[] bytes = record.getBytes();
        byte[] chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        proxy.flushToServer(100, chunk1);
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testRequestResponse() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        proxy.flushToServer(record);
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // Application data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        closeClient(client);
    }

    @Test
    public void testHandshakeAndRequestOneByteAtATime() throws Exception {
        final SSLSocket client = newClient();
        Future<Object> handshake = threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Client Hello
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        // Server Hello + Certificate + Server Done
        record = proxy.readFromServer();
        proxy.flushToClient(record);
        // Client Key Exchange
        record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        // Change Cipher Spec
        record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        // Client Done
        record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        // Change Cipher Spec
        record = proxy.readFromServer();
        proxy.flushToClient(record);
        // Server Done
        record = proxy.readFromServer();
        proxy.flushToClient(record);
        Assertions.assertNull(handshake.get(1, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        Assertions.assertNull(request.get(1, TimeUnit.SECONDS));
        // Application data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(1000);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(2000));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        // An average of 958 httpParses is seen in standard Oracle JDK's
        // An average of 1183 httpParses is seen in OpenJDK JVMs.
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(2000));
        client.close();
        // Close Alert
        record = proxy.readFromClient();
        for (byte b : record.getBytes())
            proxy.flushToServer(5, b);

        // Socket close
        record = proxy.readFromClient();
        Assertions.assertNull(record, String.valueOf(record));
        proxy.flushToServer(record);
        // Socket close
        record = proxy.readFromServer();
        // Raw close or alert
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
    }

    // See next test on why we only run in Linux
    @Test
    @EnabledOnOs(OS.LINUX)
    public void testRequestWithCloseAlertAndShutdown() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        proxy.flushToServer(record);
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        client.close();
        // Close Alert
        record = proxy.readFromClient();
        proxy.flushToServer(record);
        // Socket close
        record = proxy.readFromClient();
        Assertions.assertNull(record, String.valueOf(record));
        proxy.flushToServer(record);
        // Expect response from server
        // SSLSocket is limited and we cannot read the response, but we make sure
        // it is application data and not a close alert
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        // Socket close
        record = proxy.readFromServer();
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    public void testRequestWithCloseAlert() throws Exception {
        // Currently we are ignoring this test on anything other then linux
        // http://tools.ietf.org/html/rfc2246#section-7.2.1
        // TODO (react to this portion which seems to allow win/mac behavior)
        // It is required that the other party respond with a close_notify alert of its own
        // and close down the connection immediately, discarding any pending writes. It is not
        // required for the initiator of the close to wait for the responding
        // close_notify alert before closing the read side of the connection.
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToServer(record);
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        client.close();
        // Close Alert
        record = proxy.readFromClient();
        Assertions.assertEquals(ALERT, record.getType());
        proxy.flushToServer(record);
        // Do not close the raw socket yet
        // Expect response from server
        // SSLSocket is limited and we cannot read the response, but we make sure
        // it is application data and not a close alert
        record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        // Socket close
        record = proxy.readFromServer();
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        // Socket close
        record = proxy.readFromClient();
        Assertions.assertNull(record, String.valueOf(record));
        proxy.flushToServer(record);
    }

    @Test
    public void testRequestWithRawClose() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToServer(record);
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // Application data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        // Close the raw socket, this generates a truncation attack
        proxy.flushToServer(null);
        // Expect raw close from server OR ALERT
        record = proxy.readFromServer();
        // TODO check that this is OK?
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testRequestWithImmediateRawClose() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToServer(record, 0);
        // Close the raw socket, this generates a truncation attack
        proxy.flushToServer(null);
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // Application data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        // Expect raw close from server
        record = proxy.readFromServer();
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    // Don't run on Windows (buggy JVM)
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testRequestWithBigContentWriteBlockedThenReset() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        byte[] data = new byte[128 * 1024];
        Arrays.fill(data, ((byte) ('X')));
        final String content = new String(data, StandardCharsets.UTF_8);
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(((((("" + (("GET /echo HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Length: ")) + (content.length())) + "\r\n") + "\r\n") + content).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Nine TLSRecords will be generated for the request
        for (int i = 0; i < 9; ++i) {
            // Application data
            SslBytesTest.TLSRecord record = proxy.readFromClient();
            Assertions.assertEquals(APPLICATION, record.getType());
            proxy.flushToServer(record, 0);
        }
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // We asked the server to echo back the data we sent
        // but we do not read it, thus causing a write interest
        // on the server.
        // However, we then simulate that the client resets the
        // connection, and this will cause an exception in the
        // server that is trying to write the data
        TimeUnit.MILLISECONDS.sleep(500);
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(40));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(40));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(50));
        client.close();
    }

    // Don't run on Windows (buggy JVM)
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testRequestWithBigContentReadBlockedThenReset() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        byte[] data = new byte[128 * 1024];
        Arrays.fill(data, ((byte) ('X')));
        final String content = new String(data, StandardCharsets.UTF_8);
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(((((("" + (("GET /echo_suppress_exception HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Length: ")) + (content.length())) + "\r\n") + "\r\n") + content).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Nine TLSRecords will be generated for the request,
        // but we write only 5 of them, so the server goes in read blocked state
        for (int i = 0; i < 5; ++i) {
            // Application data
            SslBytesTest.TLSRecord record = proxy.readFromClient();
            Assertions.assertEquals(APPLICATION, record.getType());
            proxy.flushToServer(record, 0);
        }
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // The server should be read blocked, and we send a RST
        TimeUnit.MILLISECONDS.sleep(500);
        proxy.sendRSTToServer();
        // Wait a while to detect spinning
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(40));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(40));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(50));
        client.close();
    }

    // see message below
    @Test
    @EnabledOnOs(OS.LINUX)
    public void testRequestWithCloseAlertWithSplitBoundary() throws Exception {
        // currently we are ignoring this test on anything other then linux
        // http://tools.ietf.org/html/rfc2246#section-7.2.1
        // TODO (react to this portion which seems to allow win/mac behavior)
        // It is required that the other party respond with a close_notify alert of its own
        // and close down the connection immediately, discarding any pending writes. It is not
        // required for the initiator of the close to wait for the responding
        // close_notify alert before closing the read side of the connection.
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord dataRecord = proxy.readFromClient();
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        client.close();
        // Close Alert
        SslBytesTest.TLSRecord closeRecord = proxy.readFromClient();
        // Send request and half of the close alert bytes
        byte[] dataBytes = dataRecord.getBytes();
        byte[] closeBytes = closeRecord.getBytes();
        byte[] bytes = new byte[(dataBytes.length) + ((closeBytes.length) / 2)];
        System.arraycopy(dataBytes, 0, bytes, 0, dataBytes.length);
        System.arraycopy(closeBytes, 0, bytes, dataBytes.length, ((closeBytes.length) / 2));
        proxy.flushToServer(100, bytes);
        // Send the other half of the close alert bytes
        bytes = new byte[(closeBytes.length) - ((closeBytes.length) / 2)];
        System.arraycopy(closeBytes, ((closeBytes.length) / 2), bytes, 0, bytes.length);
        proxy.flushToServer(100, bytes);
        // Do not close the raw socket yet
        // Expect response from server
        // SSLSocket is limited and we cannot read the response, but we make sure
        // it is application data and not a close alert
        SslBytesTest.TLSRecord record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        // Socket close
        record = proxy.readFromServer();
        if (record != null) {
            Assertions.assertEquals(record.getType(), ALERT);
            // Now should be a raw close
            record = proxy.readFromServer();
            Assertions.assertNull(record, String.valueOf(record));
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
    }

    @Test
    public void testRequestWithContentWithSplitBoundary() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        final String content = "0123456789ABCDEF";
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + (content.length())) + "\r\n") + "\r\n") + content).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Application data
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        byte[] chunk1 = new byte[(2 * (record.getBytes().length)) / 3];
        System.arraycopy(record.getBytes(), 0, chunk1, 0, chunk1.length);
        proxy.flushToServer(100, chunk1);
        byte[] chunk2 = new byte[(record.getBytes().length) - (chunk1.length)];
        System.arraycopy(record.getBytes(), chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk2);
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        closeClient(client);
    }

    @Test
    public void testRequestWithBigContentWithSplitBoundary() throws Exception {
        final SSLSocket client = newClient();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Use a content that is larger than the TLS record which is 2^14 (around 16k)
        byte[] data = new byte[128 * 1024];
        Arrays.fill(data, ((byte) ('X')));
        final String content = new String(data, StandardCharsets.UTF_8);
        Future<Object> request = threadPool.submit(() -> {
            OutputStream clientOutput = client.getOutputStream();
            clientOutput.write(((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + (content.length())) + "\r\n") + "\r\n") + content).getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Nine TLSRecords will be generated for the request
        for (int i = 0; i < 9; ++i) {
            // Application data
            SslBytesTest.TLSRecord record = proxy.readFromClient();
            byte[] bytes = record.getBytes();
            byte[] chunk1 = new byte[(2 * (bytes.length)) / 3];
            System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
            byte[] chunk2 = new byte[(bytes.length) - (chunk1.length)];
            System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
            proxy.flushToServer(100, chunk1);
            proxy.flushToServer(100, chunk2);
        }
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(100));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(50));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(100));
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        SslBytesTest.TLSRecord record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(100));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(50));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(100));
        closeClient(client);
    }

    @Test
    public void testRequestWithContentWithRenegotiationInMiddleOfContentWhenRenegotiationIsForbidden() throws Exception {
        assumeJavaVersionSupportsTLSRenegotiations();
        sslContextFactory.setRenegotiationAllowed(false);
        final SSLSocket client = newClient();
        final OutputStream clientOutput = client.getOutputStream();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        byte[] data1 = new byte[1024];
        Arrays.fill(data1, ((byte) ('X')));
        String content1 = new String(data1, StandardCharsets.UTF_8);
        byte[] data2 = new byte[1024];
        Arrays.fill(data2, ((byte) ('Y')));
        final String content2 = new String(data2, StandardCharsets.UTF_8);
        // Write only part of the body
        automaticProxyFlow = proxy.startAutomaticFlow();
        clientOutput.write(((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + ((content1.length()) + (content2.length()))) + "\r\n") + "\r\n") + content1).getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Renegotiate
        threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Renegotiation Handshake
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(HANDSHAKE, record.getType());
        proxy.flushToServer(record);
        // Renegotiation not allowed, server has closed
        loop : while (true) {
            record = proxy.readFromServer();
            if (record == null)
                break;

            switch (record.getType()) {
                case APPLICATION :
                    Assertions.fail("application data not allows after renegotiate");
                case ALERT :
                    break loop;
                default :
                    continue;
            }
        } 
        Assertions.assertEquals(ALERT, record.getType());
        proxy.flushToClient(record);
        record = proxy.readFromServer();
        Assertions.assertNull(record);
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(50));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(50));
        client.close();
    }

    @Test
    public void testRequestWithBigContentWithRenegotiationInMiddleOfContent() throws Exception {
        assumeJavaVersionSupportsTLSRenegotiations();
        final SSLSocket client = newClient();
        final OutputStream clientOutput = client.getOutputStream();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Use a content that is larger than the TLS record which is 2^14 (around 16k)
        byte[] data1 = new byte[80 * 1024];
        Arrays.fill(data1, ((byte) ('X')));
        String content1 = new String(data1, StandardCharsets.UTF_8);
        byte[] data2 = new byte[48 * 1024];
        Arrays.fill(data2, ((byte) ('Y')));
        final String content2 = new String(data2, StandardCharsets.UTF_8);
        // Write only part of the body
        automaticProxyFlow = proxy.startAutomaticFlow();
        clientOutput.write(((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + ((content1.length()) + (content2.length()))) + "\r\n") + "\r\n") + content1).getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Renegotiate
        Future<Object> renegotiation = threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Renegotiation Handshake
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(HANDSHAKE, record.getType());
        proxy.flushToServer(record);
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
        // Trigger a read to have the client write the final renegotiation steps
        client.setSoTimeout(100);
        Assertions.assertThrows(SocketTimeoutException.class, () -> client.getInputStream().read());
        // Renegotiation Change Cipher
        record = proxy.readFromClient();
        Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
        proxy.flushToServer(record);
        // Renegotiation Handshake
        record = proxy.readFromClient();
        Assertions.assertEquals(HANDSHAKE, record.getType());
        proxy.flushToServer(record);
        Assertions.assertNull(renegotiation.get(5, TimeUnit.SECONDS));
        // Write the rest of the request
        Future<Object> request = threadPool.submit(() -> {
            clientOutput.write(content2.getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Three TLSRecords will be generated for the remainder of the content
        for (int i = 0; i < 3; ++i) {
            // Application data
            record = proxy.readFromClient();
            proxy.flushToServer(record);
        }
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // Read response
        // Application Data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(50));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(50));
        closeClient(client);
    }

    @Test
    public void testRequestWithBigContentWithRenegotiationInMiddleOfContentWithSplitBoundary() throws Exception {
        assumeJavaVersionSupportsTLSRenegotiations();
        final SSLSocket client = newClient();
        final OutputStream clientOutput = client.getOutputStream();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Use a content that is larger than the TLS record which is 2^14 (around 16k)
        byte[] data1 = new byte[80 * 1024];
        Arrays.fill(data1, ((byte) ('X')));
        String content1 = new String(data1, StandardCharsets.UTF_8);
        byte[] data2 = new byte[48 * 1024];
        Arrays.fill(data2, ((byte) ('Y')));
        final String content2 = new String(data2, StandardCharsets.UTF_8);
        // Write only part of the body
        automaticProxyFlow = proxy.startAutomaticFlow();
        clientOutput.write(((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + ((content1.length()) + (content2.length()))) + "\r\n") + "\r\n") + content1).getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Renegotiate
        Future<Object> renegotiation = threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Renegotiation Handshake
        SslBytesTest.TLSRecord record = proxy.readFromClient();
        Assertions.assertEquals(HANDSHAKE, record.getType());
        byte[] bytes = record.getBytes();
        byte[] chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        byte[] chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
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
        // Trigger a read to have the client write the final renegotiation steps
        client.setSoTimeout(100);
        Assertions.assertThrows(SocketTimeoutException.class, () -> client.getInputStream().read());
        // Renegotiation Change Cipher
        record = proxy.readFromClient();
        Assertions.assertEquals(CHANGE_CIPHER_SPEC, record.getType());
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        proxy.flushToServer(100, chunk2);
        // Renegotiation Handshake
        record = proxy.readFromClient();
        Assertions.assertEquals(HANDSHAKE, record.getType());
        bytes = record.getBytes();
        chunk1 = new byte[(2 * (bytes.length)) / 3];
        System.arraycopy(bytes, 0, chunk1, 0, chunk1.length);
        chunk2 = new byte[(bytes.length) - (chunk1.length)];
        System.arraycopy(bytes, chunk1.length, chunk2, 0, chunk2.length);
        proxy.flushToServer(100, chunk1);
        // Do not write the second chunk now, but merge it with content, see below
        Assertions.assertNull(renegotiation.get(5, TimeUnit.SECONDS));
        // Write the rest of the request
        Future<Object> request = threadPool.submit(() -> {
            clientOutput.write(content2.getBytes(StandardCharsets.UTF_8));
            clientOutput.flush();
            return null;
        });
        // Three TLSRecords will be generated for the remainder of the content
        // Merge the last chunk of the renegotiation with the first data record
        record = proxy.readFromClient();
        Assertions.assertEquals(APPLICATION, record.getType());
        byte[] dataBytes = record.getBytes();
        byte[] mergedBytes = new byte[(chunk2.length) + (dataBytes.length)];
        System.arraycopy(chunk2, 0, mergedBytes, 0, chunk2.length);
        System.arraycopy(dataBytes, 0, mergedBytes, chunk2.length, dataBytes.length);
        proxy.flushToServer(100, mergedBytes);
        // Write the remaining 2 TLS records
        for (int i = 0; i < 2; ++i) {
            // Application data
            record = proxy.readFromClient();
            Assertions.assertEquals(APPLICATION, record.getType());
            proxy.flushToServer(record);
        }
        Assertions.assertNull(request.get(5, TimeUnit.SECONDS));
        // Read response
        // Application Data
        record = proxy.readFromServer();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToClient(record);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(50));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(100));
        closeClient(client);
    }

    @Test
    public void testServerShutdownOutputClientDoesNotCloseServerCloses() throws Exception {
        final SSLSocket client = newClient();
        final OutputStream clientOutput = client.getOutputStream();
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        byte[] data = new byte[3 * 1024];
        Arrays.fill(data, ((byte) ('Y')));
        String content = new String(data, StandardCharsets.UTF_8);
        automaticProxyFlow = proxy.startAutomaticFlow();
        clientOutput.write((((((("" + ((("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Type: text/plain\r\n") + "Content-Length: ")) + (content.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + content).getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.startsWith("HTTP/1.1 200 "));
        while ((line = reader.readLine()) != null) {
            if ((line.trim().length()) == 0)
                break;

        } 
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        // Check client is at EOF
        Assertions.assertEquals((-1), client.getInputStream().read());
        // Client should close the socket, but let's hold it open.
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        // The server has shutdown the output since the client sent a Connection: close
        // but the client does not close, so the server must idle timeout the endPoint.
        TimeUnit.MILLISECONDS.sleep(((idleTimeout) + ((idleTimeout) / 2)));
        Assertions.assertFalse(serverEndPoint.get().isOpen());
    }

    @Test
    public void testPlainText() throws Exception {
        final SSLSocket client = newClient();
        threadPool.submit(() -> {
            client.startHandshake();
            return null;
        });
        // Instead of passing the Client Hello, we simulate plain text was passed in
        proxy.flushToServer(0, "GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
        // We expect that the server sends the TLS Alert.
        SslBytesTest.TLSRecord record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(ALERT, record.getType());
        record = proxy.readFromServer();
        Assertions.assertNull(record);
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(20));
        client.close();
    }

    @Test
    public void testRequestConcurrentWithIdleExpiration() throws Exception {
        final SSLSocket client = newClient();
        final OutputStream clientOutput = client.getOutputStream();
        final CountDownLatch latch = new CountDownLatch(1);
        idleHook = () -> {
            if ((latch.getCount()) == 0)
                return;

            try {
                // Send request
                clientOutput.write(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n")).getBytes(StandardCharsets.UTF_8));
                clientOutput.flush();
                latch.countDown();
            } catch (Exception x) {
                // Latch won't trigger and test will fail
                x.printStackTrace();
            }
        };
        SslBytesTest.SimpleProxy.AutomaticFlow automaticProxyFlow = proxy.startAutomaticFlow();
        client.startHandshake();
        Assertions.assertTrue(automaticProxyFlow.stop(5, TimeUnit.SECONDS));
        Assertions.assertTrue(latch.await(((idleTimeout) * 2), TimeUnit.MILLISECONDS));
        // Be sure that the server sent a SSL close alert
        SslBytesTest.TLSRecord record = proxy.readFromServer();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(ALERT, record.getType());
        // Write the request to the server, to simulate a request
        // concurrent with the SSL close alert
        record = proxy.readFromClient();
        Assertions.assertEquals(APPLICATION, record.getType());
        proxy.flushToServer(record, 0);
        // Check that we did not spin
        TimeUnit.MILLISECONDS.sleep(500);
        MatcherAssert.assertThat(sslFills.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(sslFlushes.get(), Matchers.lessThan(20));
        MatcherAssert.assertThat(httpParses.get(), Matchers.lessThan(50));
        record = proxy.readFromServer();
        Assertions.assertNull(record);
        TimeUnit.MILLISECONDS.sleep(200);
        MatcherAssert.assertThat(dump(), Matchers.not(Matchers.containsString("SCEP@")));
    }
}

