/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.net.unix;


import com.google.common.util.concurrent.Uninterruptibles;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDomainSocketWatcher {
    static final Logger LOG = LoggerFactory.getLogger(TestDomainSocketWatcher.class);

    private Throwable trappedException = null;

    /**
     * Test that we can create a DomainSocketWatcher and then shut it down.
     */
    @Test(timeout = 60000)
    public void testCreateShutdown() throws Exception {
        DomainSocketWatcher watcher = newDomainSocketWatcher(10000000);
        watcher.close();
    }

    /**
     * Test that we can get notifications out a DomainSocketWatcher.
     */
    @Test(timeout = 180000)
    public void testDeliverNotifications() throws Exception {
        DomainSocketWatcher watcher = newDomainSocketWatcher(10000000);
        DomainSocket[] pair = DomainSocket.socketpair();
        final CountDownLatch latch = new CountDownLatch(1);
        watcher.add(pair[1], new DomainSocketWatcher.Handler() {
            @Override
            public boolean handle(DomainSocket sock) {
                latch.countDown();
                return true;
            }
        });
        pair[0].close();
        latch.await();
        watcher.close();
    }

    /**
     * Test that a java interruption can stop the watcher thread
     */
    @Test(timeout = 60000)
    public void testInterruption() throws Exception {
        final DomainSocketWatcher watcher = newDomainSocketWatcher(10);
        watcher.watcherThread.interrupt();
        Uninterruptibles.joinUninterruptibly(watcher.watcherThread);
        watcher.close();
    }

    /**
     * Test that domain sockets are closed when the watcher is closed.
     */
    @Test(timeout = 300000)
    public void testCloseSocketOnWatcherClose() throws Exception {
        final DomainSocketWatcher watcher = newDomainSocketWatcher(10000000);
        DomainSocket[] pair = DomainSocket.socketpair();
        watcher.add(pair[1], new DomainSocketWatcher.Handler() {
            @Override
            public boolean handle(DomainSocket sock) {
                return true;
            }
        });
        watcher.close();
        Uninterruptibles.joinUninterruptibly(watcher.watcherThread);
        Assert.assertFalse(pair[1].isOpen());
    }

    @Test(timeout = 300000)
    public void testStress() throws Exception {
        final int SOCKET_NUM = 250;
        final ReentrantLock lock = new ReentrantLock();
        final DomainSocketWatcher watcher = newDomainSocketWatcher(10000000);
        final ArrayList<DomainSocket[]> pairs = new ArrayList<DomainSocket[]>();
        final AtomicInteger handled = new AtomicInteger(0);
        final Thread adderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < SOCKET_NUM; i++) {
                        DomainSocket[] pair = DomainSocket.socketpair();
                        watcher.add(pair[1], new DomainSocketWatcher.Handler() {
                            @Override
                            public boolean handle(DomainSocket sock) {
                                handled.incrementAndGet();
                                return true;
                            }
                        });
                        lock.lock();
                        try {
                            pairs.add(pair);
                        } finally {
                            lock.unlock();
                        }
                    }
                } catch (Throwable e) {
                    TestDomainSocketWatcher.LOG.error(e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
        final Thread removerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Random random = new Random();
                try {
                    while ((handled.get()) != SOCKET_NUM) {
                        lock.lock();
                        try {
                            if (!(pairs.isEmpty())) {
                                int idx = random.nextInt(pairs.size());
                                DomainSocket[] pair = pairs.remove(idx);
                                if (random.nextBoolean()) {
                                    pair[0].close();
                                } else {
                                    watcher.remove(pair[1]);
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    } 
                } catch (Throwable e) {
                    TestDomainSocketWatcher.LOG.error(e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
        adderThread.start();
        removerThread.start();
        Uninterruptibles.joinUninterruptibly(adderThread);
        Uninterruptibles.joinUninterruptibly(removerThread);
        watcher.close();
    }

    @Test(timeout = 300000)
    public void testStressInterruption() throws Exception {
        final int SOCKET_NUM = 250;
        final ReentrantLock lock = new ReentrantLock();
        final DomainSocketWatcher watcher = newDomainSocketWatcher(10);
        final ArrayList<DomainSocket[]> pairs = new ArrayList<DomainSocket[]>();
        final AtomicInteger handled = new AtomicInteger(0);
        final Thread adderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < SOCKET_NUM; i++) {
                        DomainSocket[] pair = DomainSocket.socketpair();
                        watcher.add(pair[1], new DomainSocketWatcher.Handler() {
                            @Override
                            public boolean handle(DomainSocket sock) {
                                handled.incrementAndGet();
                                return true;
                            }
                        });
                        lock.lock();
                        try {
                            pairs.add(pair);
                        } finally {
                            lock.unlock();
                        }
                        TimeUnit.MILLISECONDS.sleep(1);
                    }
                } catch (Throwable e) {
                    TestDomainSocketWatcher.LOG.error(e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
        final Thread removerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Random random = new Random();
                try {
                    while ((handled.get()) != SOCKET_NUM) {
                        lock.lock();
                        try {
                            if (!(pairs.isEmpty())) {
                                int idx = random.nextInt(pairs.size());
                                DomainSocket[] pair = pairs.remove(idx);
                                if (random.nextBoolean()) {
                                    pair[0].close();
                                } else {
                                    watcher.remove(pair[1]);
                                }
                                TimeUnit.MILLISECONDS.sleep(1);
                            }
                        } finally {
                            lock.unlock();
                        }
                    } 
                } catch (Throwable e) {
                    TestDomainSocketWatcher.LOG.error(e.toString());
                    throw new RuntimeException(e);
                }
            }
        });
        adderThread.start();
        removerThread.start();
        TimeUnit.MILLISECONDS.sleep(100);
        watcher.watcherThread.interrupt();
        Uninterruptibles.joinUninterruptibly(adderThread);
        Uninterruptibles.joinUninterruptibly(removerThread);
        Uninterruptibles.joinUninterruptibly(watcher.watcherThread);
    }
}

