/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jdk.connector.internal;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ServerSocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Petr Janouch (petr.janouch at oracle.com)
 */
public class SslFilterTest {
    private static final int PORT = 8321;

    @Test
    public void testBasicEcho() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            String message = "Hello world\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(message.length());
            Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            clientSocket.close();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals(message, received);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testEcho100k() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
            }
            String message = (sb.toString()) + "\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(message.length());
            Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            clientSocket.close();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals(message, received);
        } finally {
            server.stop();
        }
    }

    /**
     * Like {@link #testBasicEcho()}, but the conversation is terminated by the server.
     */
    @Test
    public void testCloseServer() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            String message = "Hello world\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(message.length());
            Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            server.stop();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals(message, received);
        } finally {
            server.stop();
        }
    }

    /**
     * Test SSL re-handshake triggered by the server.
     * <p/>
     * Sends a short message. When the message has been sent by the client, the server triggers re-handshake
     * and the client send a long message to make sure the re-handshake is performed during application data flow.
     */
    @Test
    public void testRehandshakeServer() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        final SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
            }
            String message1 = "Hello";
            String message2 = (sb.toString()) + "\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(((message1.length()) + (message2.length())));
            final CountDownLatch message1Latch = new CountDownLatch(1);
            Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message1), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void completed(ByteBuffer result) {
                    try {
                        message1Latch.countDown();
                        server.rehandshake();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            Assert.assertTrue(message1Latch.await(5, TimeUnit.SECONDS));
            clientSocket.write(stringToBuffer(message2), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            clientSocket.close();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals((message1 + message2), received);
        } finally {
            server.stop();
        }
    }

    /**
     * Test SSL re-handshake triggered by the client.
     * <p/>
     * The same as {@link #testRehandshakeServer()} except, the client starts re-handshake this time.
     */
    @Test
    public void testRehandshakeClient() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        final SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
            }
            String message1 = "Hello";
            String message2 = (sb.toString()) + "\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(((message1.length()) + (message2.length())));
            final CountDownLatch message1Latch = new CountDownLatch(1);
            final Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message1), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void completed(ByteBuffer result) {
                    message1Latch.countDown();
                    // startSsl is overloaded in the test so it will start re-handshake, calling startSsl on a filter
                    // for a second time will not normally cause a re-handshake
                    clientSocket.startSsl();
                }
            });
            Assert.assertTrue(message1Latch.await(5, TimeUnit.SECONDS));
            clientSocket.write(stringToBuffer(message2), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            clientSocket.close();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals((message1 + message2), received);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHostameVerificationFail() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            System.out.println("=== SSLHandshakeException (certificate_unknown) on the server expected ===");
            openClientSocket("127.0.0.1", ByteBuffer.allocate(0), latch, null);
            Assert.fail();
        } catch (SSLException e) {
            // expected
        } finally {
            server.stop();
        }
    }

    @Test
    public void testCustomHostameVerificationFail() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return false;
                }
            };
            openClientSocket("localhost", ByteBuffer.allocate(0), latch, verifier);
            Assert.fail();
        } catch (SSLException e) {
            // expected
        } finally {
            server.stop();
        }
    }

    @Test
    public void testCustomHostameVerificationPass() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.start();
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            openClientSocket("127.0.0.1", ByteBuffer.allocate(0), latch, verifier);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testClientAuthentication() throws Throwable {
        CountDownLatch latch = new CountDownLatch(1);
        SslFilterTest.SslEchoServer server = new SslFilterTest.SslEchoServer();
        try {
            server.setClientAuthentication();
            server.start();
            String message = "Hello world\n";
            ByteBuffer readBuffer = ByteBuffer.allocate(message.length());
            final Filter<ByteBuffer, ByteBuffer, ByteBuffer, ByteBuffer> clientSocket = openClientSocket("localhost", readBuffer, latch, null);
            clientSocket.write(stringToBuffer(message), new CompletionHandler<ByteBuffer>() {
                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }
            });
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
            clientSocket.close();
            readBuffer.flip();
            String received = bufferToString(readBuffer);
            Assert.assertEquals(message, received);
        } finally {
            server.stop();
        }
    }

    /**
     * SSL echo server. It expects a message to be terminated with \n.
     */
    private static class SslEchoServer {
        private final ServerSocket serverSocket;

        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        private volatile SSLSocket socket;

        private volatile boolean stopped = false;

        SslEchoServer() throws IOException {
            ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
            serverSocket = socketFactory.createServerSocket(SslFilterTest.PORT);
        }

        void setClientAuthentication() {
            ((SSLServerSocket) (serverSocket)).setNeedClientAuth(true);
        }

        void start() {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = ((SSLSocket) (serverSocket.accept()));
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = new BufferedOutputStream(socket.getOutputStream(), 100);
                        while (!(stopped)) {
                            int result = inputStream.read();
                            if (result == (-1)) {
                                return;
                            }
                            outputStream.write(result);
                            // '\n' indicates end of the client message
                            if (result == '\n') {
                                outputStream.flush();
                                return;
                            }
                        } 
                    } catch (IOException e) {
                        if (!(e.getClass().equals(SocketException.class))) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        void stop() throws IOException {
            executorService.shutdown();
            serverSocket.close();
            if ((socket) != null) {
                socket.close();
            }
        }

        void rehandshake() throws IOException {
            socket.startHandshake();
        }
    }
}

