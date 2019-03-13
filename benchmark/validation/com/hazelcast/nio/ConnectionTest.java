/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * IGNORED THIS TEST COMPLETELY. KEEPING FOR FUTURE REFERENCE.
 * PRONE TO FAIL BECAUSE OF BLOCKING TCP CONNECT-ACCEPT-CLOSE CYCLE.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
@Ignore("See testBlockedClientSockets and testBlockedClientSockets2 tests. Currently we couldn't find a way to make them pass...")
public class ConnectionTest extends HazelcastTestSupport {
    @Test
    public void testBlockedClientSockets() throws Exception {
        final ServerSocket serverSocket = new ServerSocket(13131, 1);
        final int count = 100;
        final CountDownLatch latch = new CountDownLatch(count);
        final AtomicInteger connected = new AtomicInteger();
        final AtomicInteger cc = new AtomicInteger();
        final Set<Socket> sockets = Collections.newSetFromMap(new ConcurrentHashMap<Socket, Boolean>());
        final Thread st = new Thread("server-socket") {
            public void run() {
                while (!(isInterrupted())) {
                    try {
                        Socket socket = serverSocket.accept();
                        sockets.add(socket);
                    } catch (IOException ignored) {
                    }
                } 
            }
        };
        st.start();
        final AtomicBoolean flag = new AtomicBoolean(false);
        for (int i = 0; i < count; i++) {
            final Socket clientSocket = new Socket();
            Thread t = new Thread(("client-socket-" + i)) {
                public void run() {
                    try {
                        if ((((cc.incrementAndGet()) > (count / 5)) && ((Math.random()) > 0.87F)) && (flag.compareAndSet(false, true))) {
                            st.interrupt();
                            serverSocket.close();
                            try {
                                st.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Iterator<Socket> iter = sockets.iterator();
                            while (iter.hasNext()) {
                                Socket socket = iter.next();
                                socket.shutdownOutput();
                                socket.close();
                                iter.remove();
                            } 
                        } else {
                            clientSocket.connect(new InetSocketAddress(13131));
                            connected.incrementAndGet();
                            clientSocket.getInputStream().read();
                        }
                    } catch (IOException ignored) {
                    } finally {
                        latch.countDown();
                    }
                }
            };
            t.setDaemon(true);
            t.start();
        }
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
    }

    @Test
    public void testBlockedClientSockets2() throws Exception {
        final ServerSocket serverSocket = new ServerSocket(13131);
        final int count = 100;
        final CountDownLatch latch = new CountDownLatch(count);
        final AtomicInteger connected = new AtomicInteger();
        final AtomicInteger cc = new AtomicInteger();
        final AtomicBoolean flag = new AtomicBoolean(false);
        for (int i = 0; i < count; i++) {
            final Socket clientSocket = new Socket();
            Thread t = new Thread(("client-socket-" + i)) {
                public void run() {
                    try {
                        if ((((cc.incrementAndGet()) > (count / 5)) && ((Math.random()) > 0.87F)) && (flag.compareAndSet(false, true))) {
                            serverSocket.close();
                        } else {
                            clientSocket.setSoTimeout((1000 * 5));
                            clientSocket.connect(new InetSocketAddress(13131));
                            connected.incrementAndGet();
                            InputStream in = clientSocket.getInputStream();
                            in.read();
                        }
                    } catch (IOException ignored) {
                    } finally {
                        latch.countDown();
                    }
                }
            };
            t.setDaemon(true);
            t.start();
        }
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
    }

    @Test
    public void testDanglingSocketsOnTerminate() throws Exception {
        testDanglingSocketsOnTerminate(false);
    }

    @Test
    public void testDanglingSocketsOnTerminate2() throws Exception {
        testDanglingSocketsOnTerminate(true);
    }
}

