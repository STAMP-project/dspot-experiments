/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.net;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import junit.framework.TestCase;


public class SocketTest extends TestCase {
    // See http://b/2980559.
    public void test_close() throws Exception {
        Socket s = new Socket();
        s.close();
        // Closing a closed socket does nothing.
        s.close();
    }

    /**
     * Our getLocalAddress and getLocalPort currently use getsockname(3).
     * This means they give incorrect results on closed sockets (as well
     * as requiring an unnecessary call into native code).
     */
    public void test_getLocalAddress_after_close() throws Exception {
        Socket s = new Socket();
        try {
            // Bind to an ephemeral local port.
            s.bind(new InetSocketAddress("localhost", 0));
            TestCase.assertTrue(s.getLocalAddress().toString(), s.getLocalAddress().isLoopbackAddress());
            // What local port did we get?
            int localPort = s.getLocalPort();
            TestCase.assertTrue((localPort > 0));
            // Now close the socket...
            s.close();
            // The RI returns the ANY address but the original local port after close.
            TestCase.assertTrue(s.getLocalAddress().isAnyLocalAddress());
            TestCase.assertEquals(localPort, s.getLocalPort());
        } finally {
            s.close();
        }
    }

    // http://code.google.com/p/android/issues/detail?id=7935
    public void test_newSocket_connection_refused() throws Exception {
        try {
            new Socket("localhost", 80);
            TestCase.fail("connection should have been refused");
        } catch (ConnectException expected) {
        }
    }

    // http://code.google.com/p/android/issues/detail?id=3123
    // http://code.google.com/p/android/issues/detail?id=1933
    public void test_socketLocalAndRemoteAddresses() throws Exception {
        checkSocketLocalAndRemoteAddresses(false);
        checkSocketLocalAndRemoteAddresses(true);
    }

    // SocketOptions.setOption has weird behavior for setSoLinger/SO_LINGER.
    // This test ensures we do what the RI does.
    public void test_SocketOptions_setOption() throws Exception {
        class MySocketImpl extends SocketImpl {
            public int option;

            public Object value;

            public boolean createCalled;

            public boolean createStream;

            public MySocketImpl() {
                super();
            }

            @Override
            protected void accept(SocketImpl arg0) throws IOException {
            }

            @Override
            protected int available() throws IOException {
                return 0;
            }

            @Override
            protected void bind(InetAddress arg0, int arg1) throws IOException {
            }

            @Override
            protected void close() throws IOException {
            }

            @Override
            protected void connect(String arg0, int arg1) throws IOException {
            }

            @Override
            protected void connect(InetAddress arg0, int arg1) throws IOException {
            }

            @Override
            protected void connect(SocketAddress arg0, int arg1) throws IOException {
            }

            @Override
            protected InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            protected OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            protected void listen(int arg0) throws IOException {
            }

            @Override
            protected void sendUrgentData(int arg0) throws IOException {
            }

            public Object getOption(int arg0) throws SocketException {
                return null;
            }

            @Override
            protected void create(boolean isStream) throws IOException {
                this.createCalled = true;
                this.createStream = isStream;
            }

            public void setOption(int option, Object value) throws SocketException {
                this.option = option;
                this.value = value;
            }
        }
        class MySocket extends Socket {
            public MySocket(SocketImpl impl) throws SocketException {
                super(impl);
            }
        }
        MySocketImpl impl = new MySocketImpl();
        Socket s = new MySocket(impl);
        // than -1 to the SocketImpl when setSoLinger is called with the first argument false.
        s.setSoLinger(false, (-1));
        TestCase.assertEquals(Boolean.FALSE, ((Boolean) (impl.value)));
        // We also check that SocketImpl.create was called. SocketChannelImpl.SocketAdapter
        // subclasses Socket, and whether or not to call SocketImpl.create is the main behavioral
        // difference.
        TestCase.assertEquals(true, impl.createCalled);
        s.setSoLinger(false, 0);
        TestCase.assertEquals(Boolean.FALSE, ((Boolean) (impl.value)));
        s.setSoLinger(false, 1);
        TestCase.assertEquals(Boolean.FALSE, ((Boolean) (impl.value)));
        // Check that otherwise, we pass down an Integer.
        s.setSoLinger(true, 0);
        TestCase.assertEquals(Integer.valueOf(0), ((Integer) (impl.value)));
        s.setSoLinger(true, 1);
        TestCase.assertEquals(Integer.valueOf(1), ((Integer) (impl.value)));
    }

    public void test_setTrafficClass() throws Exception {
        Socket s = new Socket();
        s.setTrafficClass(123);
        TestCase.assertEquals(123, s.getTrafficClass());
    }

    public void testReadAfterClose() throws Exception {
        SocketTest.MockServer server = new SocketTest.MockServer();
        server.enqueue(new byte[]{ 5, 3 }, 0);
        Socket socket = new Socket("localhost", server.port);
        InputStream in = socket.getInputStream();
        TestCase.assertEquals(5, in.read());
        TestCase.assertEquals(3, in.read());
        TestCase.assertEquals((-1), in.read());
        TestCase.assertEquals((-1), in.read());
        socket.close();
        in.close();
        /* Rather astonishingly, read() doesn't throw even though the stream is
        closed. This is consistent with the RI's behavior.
         */
        TestCase.assertEquals((-1), in.read());
        TestCase.assertEquals((-1), in.read());
        server.shutdown();
    }

    public void testWriteAfterClose() throws Exception {
        SocketTest.MockServer server = new SocketTest.MockServer();
        server.enqueue(new byte[0], 3);
        Socket socket = new Socket("localhost", server.port);
        OutputStream out = socket.getOutputStream();
        out.write(5);
        out.write(3);
        socket.close();
        out.close();
        try {
            out.write(9);
            TestCase.fail();
        } catch (IOException expected) {
        }
        server.shutdown();
    }

    // http://b/5534202
    public void testAvailable() throws Exception {
        for (int i = 0; i < 100; i++) {
            assertAvailableReturnsZeroAfterSocketReadsAllData();
            System.out.println(("Success on rep " + i));
        }
    }

    static class MockServer {
        private ExecutorService executor;

        private ServerSocket serverSocket;

        private int port = -1;

        MockServer() throws IOException {
            executor = Executors.newCachedThreadPool();
            serverSocket = new ServerSocket(0);
            serverSocket.setReuseAddress(true);
            port = serverSocket.getLocalPort();
        }

        public Future<byte[]> enqueue(final byte[] sendBytes, final int receiveByteCount) throws IOException {
            return executor.submit(new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    Socket socket = serverSocket.accept();
                    OutputStream out = socket.getOutputStream();
                    out.write(sendBytes);
                    InputStream in = socket.getInputStream();
                    byte[] result = new byte[receiveByteCount];
                    int total = 0;
                    while (total < receiveByteCount) {
                        total += in.read(result, total, ((result.length) - total));
                    } 
                    socket.close();
                    return result;
                }
            });
        }

        public void shutdown() throws IOException {
            serverSocket.close();
            executor.shutdown();
        }
    }
}

