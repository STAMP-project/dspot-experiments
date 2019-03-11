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
package org.eclipse.jetty.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.TimerScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class SslConnectionTest {
    private static final int TIMEOUT = 1000000;

    private static ByteBufferPool __byteBufferPool = new LeakTrackingByteBufferPool(new MappedByteBufferPool.Tagged());

    private final SslContextFactory _sslCtxFactory = new SslContextFactory();

    protected volatile EndPoint _lastEndp;

    private volatile boolean _testFill = true;

    private volatile FutureCallback _writeCallback;

    protected ServerSocketChannel _connector;

    final AtomicInteger _dispatches = new AtomicInteger();

    protected QueuedThreadPool _threadPool = new QueuedThreadPool() {
        @Override
        public void execute(Runnable job) {
            _dispatches.incrementAndGet();
            super.execute(job);
        }
    };

    protected Scheduler _scheduler = new TimerScheduler();

    protected SelectorManager _manager = new SelectorManager(_threadPool, _scheduler) {
        @Override
        public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Object attachment) {
            SSLEngine engine = _sslCtxFactory.newSSLEngine();
            engine.setUseClientMode(false);
            SslConnection sslConnection = new SslConnection(SslConnectionTest.__byteBufferPool, getExecutor(), endpoint, engine);
            sslConnection.setRenegotiationAllowed(_sslCtxFactory.isRenegotiationAllowed());
            sslConnection.setRenegotiationLimit(_sslCtxFactory.getRenegotiationLimit());
            Connection appConnection = new SslConnectionTest.TestConnection(sslConnection.getDecryptedEndPoint());
            sslConnection.getDecryptedEndPoint().setConnection(appConnection);
            return sslConnection;
        }

        @Override
        protected EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) {
            SocketChannelEndPoint endp = new SslConnectionTest.TestEP(channel, selector, selectionKey, getScheduler());
            endp.setIdleTimeout(SslConnectionTest.TIMEOUT);
            _lastEndp = endp;
            return endp;
        }
    };

    static final AtomicInteger __startBlocking = new AtomicInteger();

    static final AtomicInteger __blockFor = new AtomicInteger();

    private static class TestEP extends SocketChannelEndPoint {
        public TestEP(SelectableChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
            super(((SocketChannel) (channel)), selector, key, scheduler);
        }

        @Override
        protected void onIncompleteFlush() {
            super.onIncompleteFlush();
        }

        @Override
        public boolean flush(ByteBuffer... buffers) throws IOException {
            if (((SslConnectionTest.__startBlocking.get()) == 0) || ((SslConnectionTest.__startBlocking.decrementAndGet()) == 0)) {
                if (((SslConnectionTest.__blockFor.get()) > 0) && ((SslConnectionTest.__blockFor.getAndDecrement()) > 0)) {
                    return false;
                }
            }
            return super.flush(buffers);
        }
    }

    public class TestConnection extends AbstractConnection {
        ByteBuffer _in = BufferUtil.allocate((8 * 1024));

        public TestConnection(EndPoint endp) {
            super(endp, _threadPool);
        }

        @Override
        public void onOpen() {
            super.onOpen();
            if (_testFill)
                fillInterested();
            else {
                getExecutor().execute(() -> getEndPoint().write(org.eclipse.jetty.io._writeCallback, BufferUtil.toBuffer("Hello Client")));
            }
        }

        @Override
        public void onClose() {
            super.onClose();
        }

        @Override
        public synchronized void onFillable() {
            EndPoint endp = getEndPoint();
            try {
                boolean progress = true;
                while (progress) {
                    progress = false;
                    // Fill the input buffer with everything available
                    int filled = endp.fill(_in);
                    while (filled > 0) {
                        progress = true;
                        filled = endp.fill(_in);
                    } 
                    // Write everything
                    int l = _in.remaining();
                    if (l > 0) {
                        FutureCallback blockingWrite = new FutureCallback();
                        endp.write(blockingWrite, _in);
                        blockingWrite.get();
                    }
                    // are we done?
                    if (endp.isInputShutdown()) {
                        endp.shutdownOutput();
                    }
                } 
            } catch (InterruptedException | EofException e) {
                Log.getRootLogger().ignore(e);
            } catch (Exception e) {
                Log.getRootLogger().warn(e);
            } finally {
                if (endp.isOpen())
                    fillInterested();

            }
        }
    }

    @Test
    public void testHelloWorld() throws Exception {
        startSSL();
        try (Socket client = newClient()) {
            client.setSoTimeout(SslConnectionTest.TIMEOUT);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                client.getOutputStream().write("Hello".getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("Hello", new String(buffer, 0, len, StandardCharsets.UTF_8));
                _dispatches.set(0);
                client.getOutputStream().write("World".getBytes(StandardCharsets.UTF_8));
                len = 5;
                while (len > 0)
                    len -= client.getInputStream().read(buffer);

            }
        }
    }

    @Test
    public void testRenegotiate() throws Exception {
        startSSL();
        try (SSLSocket client = newClient()) {
            client.setSoTimeout(SslConnectionTest.TIMEOUT);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                client.getOutputStream().write("Hello".getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("Hello", new String(buffer, 0, len, StandardCharsets.UTF_8));
                client.startHandshake();
                client.getOutputStream().write("World".getBytes(StandardCharsets.UTF_8));
                len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("World", new String(buffer, 0, len, StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void testRenegotiateNotAllowed() throws Exception {
        // TLS 1.3 and beyond do not support renegotiation.
        _sslCtxFactory.setIncludeProtocols("TLSv1.2");
        _sslCtxFactory.setRenegotiationAllowed(false);
        startSSL();
        try (SSLSocket client = newClient()) {
            client.setSoTimeout(SslConnectionTest.TIMEOUT);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                client.getOutputStream().write("Hello".getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("Hello", new String(buffer, 0, len, StandardCharsets.UTF_8));
                // Try to renegotiate, must fail.
                client.startHandshake();
                client.getOutputStream().write("World".getBytes(StandardCharsets.UTF_8));
                Assertions.assertThrows(SSLException.class, () -> client.getInputStream().read(buffer));
            }
        }
    }

    @Test
    public void testRenegotiateLimit() throws Exception {
        // TLS 1.3 and beyond do not support renegotiation.
        _sslCtxFactory.setIncludeProtocols("TLSv1.2");
        _sslCtxFactory.setRenegotiationAllowed(true);
        _sslCtxFactory.setRenegotiationLimit(2);
        startSSL();
        try (SSLSocket client = newClient()) {
            client.setSoTimeout(SslConnectionTest.TIMEOUT);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                client.getOutputStream().write("Good".getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals(4, len);
                Assertions.assertEquals("Good", new String(buffer, 0, len, StandardCharsets.UTF_8));
                client.startHandshake();
                client.getOutputStream().write("Bye".getBytes(StandardCharsets.UTF_8));
                len = client.getInputStream().read(buffer);
                Assertions.assertEquals(3, len);
                Assertions.assertEquals("Bye", new String(buffer, 0, len, StandardCharsets.UTF_8));
                client.startHandshake();
                client.getOutputStream().write("Cruel".getBytes(StandardCharsets.UTF_8));
                len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("Cruel", new String(buffer, 0, len, StandardCharsets.UTF_8));
                client.startHandshake();
                client.getOutputStream().write("World".getBytes(StandardCharsets.UTF_8));
                Assertions.assertThrows(SSLException.class, () -> client.getInputStream().read(buffer));
            }
        }
    }

    @Test
    public void testWriteOnConnect() throws Exception {
        _testFill = false;
        _writeCallback = new FutureCallback();
        startSSL();
        try (SSLSocket client = newClient()) {
            client.setSoTimeout(SslConnectionTest.TIMEOUT);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                // The server side will write something, and in order
                // to proceed with the initial TLS handshake we need
                // to start reading before waiting for the callback.
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals("Hello Client", new String(buffer, 0, len, StandardCharsets.UTF_8));
                Assertions.assertNull(_writeCallback.get(1, TimeUnit.SECONDS));
            }
        }
    }

    @Test
    public void testBlockedWrite() throws Exception {
        startSSL();
        try (Socket client = newClient()) {
            client.setSoTimeout(5000);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                SslConnectionTest.__startBlocking.set(5);
                SslConnectionTest.__blockFor.set(3);
                client.getOutputStream().write("Hello".getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[1024];
                int len = client.getInputStream().read(buffer);
                Assertions.assertEquals(5, len);
                Assertions.assertEquals("Hello", new String(buffer, 0, len, StandardCharsets.UTF_8));
                _dispatches.set(0);
                client.getOutputStream().write("World".getBytes(StandardCharsets.UTF_8));
                len = 5;
                while (len > 0)
                    len -= client.getInputStream().read(buffer);

                Assertions.assertEquals(0, len);
            }
        }
    }

    @Test
    public void testManyLines() throws Exception {
        startSSL();
        try (Socket client = newClient()) {
            client.setSoTimeout(10000);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                final int LINES = 20;
                final CountDownLatch count = new CountDownLatch(LINES);
                new Thread(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                        while ((count.getCount()) > 0) {
                            String line = in.readLine();
                            if (line == null)
                                break;

                            // System.err.println(line);
                            count.countDown();
                        } 
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                for (int i = 0; i < LINES; i++) {
                    client.getOutputStream().write((("HelloWorld " + i) + "\n").getBytes(StandardCharsets.UTF_8));
                    // System.err.println("wrote");
                    if ((i % 1000) == 0) {
                        client.getOutputStream().flush();
                        Thread.sleep(10);
                    }
                }
                Assertions.assertTrue(count.await(20, TimeUnit.SECONDS));
            }
        }
    }
}

