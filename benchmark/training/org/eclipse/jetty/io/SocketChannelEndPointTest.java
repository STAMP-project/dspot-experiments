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


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.Scheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


@SuppressWarnings("Duplicates")
public class SocketChannelEndPointTest {
    private static final Logger LOG = Log.getLogger(SocketChannelEndPoint.class);

    public interface Scenario {
        Socket newClient(ServerSocketChannel connector) throws IOException;

        Connection newConnection(SelectableChannel channel, EndPoint endPoint, Executor executor, AtomicInteger blockAt, AtomicInteger writeCount);

        boolean supportsHalfCloses();
    }

    private SocketChannelEndPointTest.Scenario _scenario;

    private ServerSocketChannel _connector;

    private QueuedThreadPool _threadPool;

    private Scheduler _scheduler;

    private SelectorManager _manager;

    private volatile EndPoint _lastEndPoint;

    private CountDownLatch _lastEndPointLatch;

    // Must be volatile or the test may fail spuriously
    private AtomicInteger _blockAt = new AtomicInteger(0);

    private AtomicInteger _writeCount = new AtomicInteger(1);

    @Test
    public void testShutdown() throws Exception {
        // We don't test SSL as JVM SSL doesn't support half-close
        init(new SocketChannelEndPointTest.NormalScenario());
        try (Socket client = _scenario.newClient(_connector)) {
            client.setSoTimeout(500);
            try (SocketChannel server = _connector.accept()) {
                server.configureBlocking(false);
                _manager.accept(server);
                // Write client to server
                client.getOutputStream().write("HelloWorld".getBytes(StandardCharsets.UTF_8));
                // Verify echo server to client
                for (char c : "HelloWorld".toCharArray()) {
                    int b = client.getInputStream().read();
                    Assertions.assertTrue((b > 0));
                    Assertions.assertEquals(c, ((char) (b)));
                }
                // wait for read timeout
                long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                Assertions.assertThrows(SocketTimeoutException.class, () -> client.getInputStream().read());
                Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) >= 400));
                // write then shutdown
                client.getOutputStream().write("Goodbye Cruel TLS".getBytes(StandardCharsets.UTF_8));
                client.shutdownOutput();
                // Verify echo server to client
                for (char c : "Goodbye Cruel TLS".toCharArray()) {
                    int b = client.getInputStream().read();
                    Assertions.assertTrue((b > 0));
                    Assertions.assertEquals(c, ((char) (b)));
                }
                // Read close
                Assertions.assertEquals((-1), client.getInputStream().read());
            }
        }
    }

    public class ScenarioSelectorManager extends SelectorManager {
        protected ScenarioSelectorManager(Executor executor, Scheduler scheduler) {
            super(executor, scheduler);
        }

        protected EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey key) {
            SocketChannelEndPoint endp = new SocketChannelEndPoint(channel, selector, key, getScheduler());
            endp.setIdleTimeout(60000);
            _lastEndPoint = endp;
            _lastEndPointLatch.countDown();
            return endp;
        }

        @Override
        public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Object attachment) {
            return _scenario.newConnection(channel, endpoint, getExecutor(), _blockAt, _writeCount);
        }
    }

    public static class NormalScenario implements SocketChannelEndPointTest.Scenario {
        @Override
        public Socket newClient(ServerSocketChannel connector) throws IOException {
            return new Socket(connector.socket().getInetAddress(), connector.socket().getLocalPort());
        }

        @Override
        public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Executor executor, AtomicInteger blockAt, AtomicInteger writeCount) {
            return new SocketChannelEndPointTest.TestConnection(endpoint, executor, blockAt, writeCount);
        }

        @Override
        public boolean supportsHalfCloses() {
            return true;
        }

        @Override
        public String toString() {
            return "normal";
        }
    }

    public static class SslScenario implements SocketChannelEndPointTest.Scenario {
        private final SocketChannelEndPointTest.NormalScenario _normalScenario;

        private final SslContextFactory __sslCtxFactory = new SslContextFactory();

        private final ByteBufferPool __byteBufferPool = new MappedByteBufferPool();

        public SslScenario(SocketChannelEndPointTest.NormalScenario normalScenario) throws Exception {
            _normalScenario = normalScenario;
            File keystore = MavenTestingUtils.getTestResourceFile("keystore");
            __sslCtxFactory.setKeyStorePath(keystore.getAbsolutePath());
            __sslCtxFactory.setKeyStorePassword("storepwd");
            __sslCtxFactory.setKeyManagerPassword("keypwd");
            __sslCtxFactory.setEndpointIdentificationAlgorithm("");
            __sslCtxFactory.start();
        }

        @Override
        public Socket newClient(ServerSocketChannel connector) throws IOException {
            SSLSocket socket = __sslCtxFactory.newSslSocket();
            socket.connect(connector.socket().getLocalSocketAddress());
            return socket;
        }

        @Override
        public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Executor executor, AtomicInteger blockAt, AtomicInteger writeCount) {
            SSLEngine engine = __sslCtxFactory.newSSLEngine();
            engine.setUseClientMode(false);
            SslConnection sslConnection = new SslConnection(__byteBufferPool, executor, endpoint, engine);
            sslConnection.setRenegotiationAllowed(__sslCtxFactory.isRenegotiationAllowed());
            sslConnection.setRenegotiationLimit(__sslCtxFactory.getRenegotiationLimit());
            Connection appConnection = _normalScenario.newConnection(channel, sslConnection.getDecryptedEndPoint(), executor, blockAt, writeCount);
            sslConnection.getDecryptedEndPoint().setConnection(appConnection);
            return sslConnection;
        }

        @Override
        public boolean supportsHalfCloses() {
            return false;
        }

        @Override
        public String toString() {
            return "ssl";
        }
    }

    @SuppressWarnings("Duplicates")
    public static class TestConnection extends AbstractConnection {
        private static final Logger LOG = Log.getLogger(SocketChannelEndPointTest.TestConnection.class);

        volatile FutureCallback _blockingRead;

        final AtomicInteger _blockAt;

        final AtomicInteger _writeCount;

        // volatile int _blockAt = 0;
        ByteBuffer _in = BufferUtil.allocate((32 * 1024));

        ByteBuffer _out = BufferUtil.allocate((32 * 1024));

        long _last = -1;

        final CountDownLatch _latch;

        public TestConnection(EndPoint endp, Executor executor, AtomicInteger blockAt, AtomicInteger writeCount) {
            super(endp, executor);
            _latch = null;
            this._blockAt = blockAt;
            this._writeCount = writeCount;
        }

        public TestConnection(EndPoint endp, CountDownLatch latch, Executor executor, AtomicInteger blockAt, AtomicInteger writeCount) {
            super(endp, executor);
            _latch = latch;
            this._blockAt = blockAt;
            this._writeCount = writeCount;
        }

        @Override
        public void onOpen() {
            super.onOpen();
            fillInterested();
        }

        @Override
        public void onFillInterestedFailed(Throwable cause) {
            Callback blocking = _blockingRead;
            if (blocking != null) {
                _blockingRead = null;
                blocking.failed(cause);
                return;
            }
            super.onFillInterestedFailed(cause);
        }

        @Override
        public void onFillable() {
            if ((_latch) != null) {
                try {
                    _latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Callback blocking = _blockingRead;
            if (blocking != null) {
                _blockingRead = null;
                blocking.succeeded();
                return;
            }
            EndPoint _endp = getEndPoint();
            try {
                _last = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                boolean progress = true;
                while (progress) {
                    progress = false;
                    // Fill the input buffer with everything available
                    BufferUtil.compact(_in);
                    if (BufferUtil.isFull(_in))
                        throw new IllegalStateException(("FULL " + (BufferUtil.toDetailString(_in))));

                    int filled = _endp.fill(_in);
                    if (filled > 0)
                        progress = true;

                    // If the tests wants to block, then block
                    while ((((_blockAt.get()) > 0) && (_endp.isOpen())) && ((_in.remaining()) < (_blockAt.get()))) {
                        FutureCallback future = _blockingRead = new FutureCallback();
                        fillInterested();
                        future.get();
                        filled = _endp.fill(_in);
                        progress |= filled > 0;
                    } 
                    // Copy to the out buffer
                    if ((BufferUtil.hasContent(_in)) && ((BufferUtil.append(_out, _in)) > 0))
                        progress = true;

                    // Blocking writes
                    if (BufferUtil.hasContent(_out)) {
                        ByteBuffer out = _out.duplicate();
                        BufferUtil.clear(_out);
                        for (int i = 0; i < (_writeCount.get()); i++) {
                            FutureCallback blockingWrite = new FutureCallback();
                            _endp.write(blockingWrite, out.asReadOnlyBuffer());
                            blockingWrite.get();
                        }
                        progress = true;
                    }
                    // are we done?
                    if (_endp.isInputShutdown())
                        _endp.shutdownOutput();

                } 
                if (_endp.isOpen())
                    fillInterested();

            } catch (ExecutionException e) {
                // Timeout does not close, so echo exception then shutdown
                try {
                    FutureCallback blockingWrite = new FutureCallback();
                    _endp.write(blockingWrite, BufferUtil.toBuffer(("EE: " + (BufferUtil.toString(_in)))));
                    blockingWrite.get();
                    _endp.shutdownOutput();
                } catch (Exception e2) {
                    // e2.printStackTrace();
                }
            } catch (InterruptedException | EofException e) {
                if (SocketChannelEndPointTest.TestConnection.LOG.isDebugEnabled())
                    SocketChannelEndPointTest.TestConnection.LOG.debug(e);
                else
                    SocketChannelEndPointTest.TestConnection.LOG.info(e.getClass().getName());

            } catch (Exception e) {
                SocketChannelEndPointTest.TestConnection.LOG.warn(e);
            }
        }
    }
}

