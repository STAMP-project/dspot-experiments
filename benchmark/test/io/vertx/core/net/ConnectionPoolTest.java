/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.net;


import io.vertx.core.Context;
import io.vertx.core.impl.ContextInternal;
import io.vertx.test.core.AsyncTestBase;
import io.vertx.test.core.VertxTestBase;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.Test;

import static FakeConnection.CONNECTED;


/**
 *
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ConnectionPoolTest extends VertxTestBase {
    class FakeConnectionManager {
        private final Context context = vertx.getOrCreateContext();

        private final ConnectionProvider<ConnectionPoolTest.FakeConnection> connector;

        private final int queueMaxSize;

        private final int poolMaxSize;

        private Pool<ConnectionPoolTest.FakeConnection> pool;

        private Set<ConnectionPoolTest.FakeConnection> active = new HashSet<>();

        private boolean closed = true;

        private int closeCount;

        private int seq;

        private final boolean fifo;

        FakeConnectionManager(int queueMaxSize, int poolMaxSize, ConnectionProvider<ConnectionPoolTest.FakeConnection> connector) {
            this(queueMaxSize, poolMaxSize, connector, false);
        }

        FakeConnectionManager(int queueMaxSize, int poolMaxSize, ConnectionProvider<ConnectionPoolTest.FakeConnection> connector, boolean fifo) {
            this.queueMaxSize = queueMaxSize;
            this.poolMaxSize = poolMaxSize;
            this.connector = connector;
            this.fifo = fifo;
        }

        synchronized int sequence() {
            return seq;
        }

        synchronized boolean closed() {
            return closed;
        }

        synchronized boolean contains(ConnectionPoolTest.FakeConnection conn) {
            return active.contains(conn);
        }

        synchronized int size() {
            return active.size();
        }

        int removeExpired(long timestamp) {
            return pool.closeIdle(timestamp);
        }

        synchronized Pool<ConnectionPoolTest.FakeConnection> pool() {
            return pool;
        }

        synchronized int closeCount() {
            return closeCount;
        }

        void getConnection(ConnectionPoolTest.FakeWaiter waiter) {
            synchronized(this) {
                if (closed) {
                    (seq)++;
                    closed = false;
                    pool = new Pool(context, connector, queueMaxSize, 1, poolMaxSize, ( v) -> {
                        synchronized(io.vertx.core.net.FakeConnectionManager.this) {
                            closed = true;
                            (closeCount)++;
                        }
                    }, ( conn) -> {
                        synchronized(io.vertx.core.net.FakeConnectionManager.this) {
                            active.add(conn);
                        }
                    }, ( conn) -> {
                        synchronized(io.vertx.core.net.FakeConnectionManager.this) {
                            active.remove(conn);
                        }
                    }, fifo);
                }
            }
            pool.getConnection(waiter.handler);
        }
    }

    @Test
    public void testConnectSuccess() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 4, connector);
        AtomicReference<Boolean> handleLock = new AtomicReference<>();
        ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter() {
            @Override
            public synchronized void handleConnection(ConnectionPoolTest.FakeConnection conn) {
                assertSame(conn.context, mgr.context);
                Pool<ConnectionPoolTest.FakeConnection> pool = mgr.pool();
                handleLock.set(Thread.holdsLock(pool));
                super.handleConnection(conn);
            }
        };
        mgr.getConnection(waiter);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter::isComplete);
        assertEquals(Boolean.FALSE, handleLock.get());
        waiter.assertSuccess(conn);
        waiter.recycle();
        assertEquals(0, mgr.size());
        AsyncTestBase.assertWaitUntil(() -> mgr.closed());
    }

    @Test
    public void testConnectFailure() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 4, connector);
        AtomicReference<Boolean> holdsLock = new AtomicReference<>();
        ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter() {
            @Override
            public synchronized void handleFailure(Throwable failure) {
                assertNull(Vertx.currentContext());
                Pool<ConnectionPoolTest.FakeConnection> pool = mgr.pool();
                holdsLock.set(Thread.holdsLock(pool));
                super.handleFailure(failure);
            }
        };
        mgr.getConnection(waiter);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        Throwable failure = new Throwable();
        conn.fail(failure);
        AsyncTestBase.assertWaitUntil(waiter::isComplete);
        assertEquals(Boolean.FALSE, holdsLock.get());
        waiter.assertFailure(failure);
    }

    @Test
    public void testConnectPoolEmptyWaiterCancelledAfterConnectRequest() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 3, connector);
        ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        waiter.cancel();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter::isComplete);
        assertFalse(waiter.isSuccess());
        assertFalse(waiter.isFailure());
        assertFalse(mgr.contains(conn));
    }

    @Test
    public void testConnectionFailure() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 3, connector);
        ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        Exception expected = new Exception();
        conn.fail(expected);
        AsyncTestBase.assertWaitUntil(waiter::isComplete);
        waiter.assertFailure(expected);
        assertTrue(waiter.isFailure());
        AsyncTestBase.assertWaitUntil(mgr::closed);
    }

    @Test
    public void testSynchronousConnectionFailure() {
        Throwable cause = new Throwable();
        ConnectionProvider<ConnectionPoolTest.FakeConnection> connector = new ConnectionPoolTest.FakeConnectionProviderBase() {
            @Override
            public void connect(ConnectionListener<ConnectionPoolTest.FakeConnection> listener, ContextInternal context, Handler<AsyncResult<ConnectResult<ConnectionPoolTest.FakeConnection>>> handler) {
                handler.handle(Future.failedFuture(cause));
            }
        };
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 3, connector);
        for (int i = 0; i < 4; i++) {
            ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter();
            mgr.getConnection(waiter);
            AsyncTestBase.waitUntil(waiter::isFailure);
            waiter.assertFailure(cause);
            assertEquals(0, mgr.pool().weight());
        }
        assertTrue(mgr.closed());
    }

    @Test
    public void testRecycleConnection() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isComplete);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        connector.assertRequests(0);
        waiter1.recycle();
        AsyncTestBase.assertWaitUntil(waiter2::isComplete);
        waiter2.assertSuccess(conn);
    }

    @Test
    public void testRecycleDiscardedConnection() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isComplete);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        conn.close();
        waiter1.recycle();
        AsyncTestBase.assertWaitUntil(() -> (connector.requests()) == 1);
        assertFalse(mgr.closed());
        ConnectionPoolTest.FakeConnection conn2 = connector.assertRequest();
        conn2.connect();
        AsyncTestBase.assertWaitUntil(waiter2::isSuccess);
    }

    /* @Test
    public void testWaiterThrowsException() {
    FakeConnectionProvider connector = new FakeConnectionProvider();
    FakeConnectionManager mgr = new FakeConnectionManager(3, 1, connector);
    Exception failure = new Exception();
    FakeWaiter waiter = new FakeWaiter() {
    @Override
    public synchronized boolean handleConnection(ContextInternal ctx, FakeConnection conn) throws Exception {
    throw failure;
    }
    };
    mgr.getConnection(waiter);
    FakeConnection conn = connector.assertRequest();
    conn.connect();
    assertEquals(0, mgr.size());
    }
     */
    @Test
    public void testEndpointLifecycle() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        conn.close();
        AsyncTestBase.assertWaitUntil(mgr::closed);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        assertEquals(2, mgr.sequence());
    }

    @Test
    public void testDontCloseEndpointWithInflightRequest() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(3, 2, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isComplete);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        conn.close();
        AsyncTestBase.assertWaitUntil(() -> !(mgr.contains(conn)));
        assertFalse(mgr.closed());
    }

    @Test
    public void testInitialConcurrency() {
        int n = 10;
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 1, connector);
        List<ConnectionPoolTest.FakeWaiter> waiters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter();
            mgr.getConnection(waiter);
            waiters.add(waiter);
        }
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.concurrency(n).connect();
        waiters.forEach(( waiter) -> {
            AsyncTestBase.assertWaitUntil(waiter::isSuccess);
        });
        waiters.forEach(ConnectionPoolTest.FakeWaiter::recycle);
    }

    @Test
    public void testInitialNoConcurrency() {
        int n = 10;
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 1, connector);
        List<ConnectionPoolTest.FakeWaiter> waiters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ConnectionPoolTest.FakeWaiter waiter = new ConnectionPoolTest.FakeWaiter();
            mgr.getConnection(waiter);
            waiters.add(waiter);
        }
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.concurrency(0).connect().awaitConnected();
        conn.concurrency((n - 1));
        AsyncTestBase.assertWaitUntil(() -> (waiters.stream().filter(ConnectionPoolTest.FakeWaiter::isSuccess).count()) == (n - 1));
        waiters.stream().filter(ConnectionPoolTest.FakeWaiter::isSuccess).findFirst().get().recycle();
        waiters.forEach(( waiter) -> {
            AsyncTestBase.assertWaitUntil(waiter::isSuccess);
        });
    }

    @Test
    public void testRecycleWithoutDispose() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        conn.recycle(false);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        AsyncTestBase.assertWaitUntil(waiter2::isSuccess);
        waiter2.assertSuccess(conn);
        conn.recycle(true);
        assertEquals(0, mgr.size());
    }

    @Test
    public void testRecycleFIFO() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 2, connector, true);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection firstInConnection = connector.assertRequest();
        firstInConnection.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        ConnectionPoolTest.FakeConnection lastInConnection = connector.assertRequest();
        lastInConnection.connect();
        AsyncTestBase.assertWaitUntil(waiter2::isSuccess);
        waiter2.assertSuccess(lastInConnection);
        firstInConnection.recycle(false);
        lastInConnection.recycle(false);
        assertEquals(2, mgr.size());
        ConnectionPoolTest.FakeWaiter waiter3 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter3);
        AsyncTestBase.assertWaitUntil(waiter3::isSuccess);
        waiter3.assertSuccess(firstInConnection);
    }

    @Test
    public void testRecycleLIFO() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 2, connector, false);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection firstInConnection = connector.assertRequest();
        firstInConnection.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        ConnectionPoolTest.FakeConnection lastInConnection = connector.assertRequest();
        lastInConnection.connect();
        AsyncTestBase.assertWaitUntil(waiter2::isSuccess);
        waiter2.assertSuccess(lastInConnection);
        firstInConnection.recycle(false);
        lastInConnection.recycle(false);
        assertEquals(2, mgr.size());
        ConnectionPoolTest.FakeWaiter waiter3 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter3);
        AsyncTestBase.assertWaitUntil(waiter3::isSuccess);
        waiter3.assertSuccess(lastInConnection);
    }

    @Test
    public void testDiscardWaiterWhenFull() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(2, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter2);
        ConnectionPoolTest.FakeWaiter waiter3 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter3);
        ConnectionPoolTest.FakeWaiter waiter4 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter4);
        AsyncTestBase.assertWaitUntil(waiter4::isFailure);// Full

    }

    /* @Test
    public void testDiscardConnectionDuringInit() {
    FakeConnectionProvider connector = new FakeConnectionProvider();
    FakeConnectionManager mgr = new FakeConnectionManager(2, 1, connector);
    FakeWaiter waiter1 = new FakeWaiter() {
    @Override
    public synchronized void initConnection(ContextInternal ctx, FakeConnection conn) {
    super.initConnection(ctx, conn);
    conn.close(); // Close during init
    }
    };
    mgr.getConnection(waiter1);
    FakeConnection conn = connector.assertRequest();
    conn.connect();
    assertWaitUntil(() -> connector.requests() == 1); // Connection close during init - reattempt to connect
    assertFalse(mgr.closed());
    }
     */
    @Test
    public void testDiscardExpiredConnections() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(2, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        conn.recycle(2L);
        assertEquals(1, mgr.size());
        assertEquals(0, mgr.removeExpired(1L));
        assertEquals(1, mgr.size());
        assertEquals(1, mgr.removeExpired(2L));
        assertEquals(0, mgr.size());
        AsyncTestBase.assertWaitUntil(mgr::closed);
    }

    @Test
    public void testCloseRecycledConnection() {
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider();
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager(2, 1, connector);
        ConnectionPoolTest.FakeWaiter waiter1 = new ConnectionPoolTest.FakeWaiter();
        mgr.getConnection(waiter1);
        ConnectionPoolTest.FakeConnection conn = connector.assertRequest();
        conn.connect();
        AsyncTestBase.assertWaitUntil(waiter1::isSuccess);
        conn.recycle(2L);
        ConnectionPoolTest.FakeWaiter waiter2 = new ConnectionPoolTest.FakeWaiter();
        // Recycle connection
        mgr.getConnection(waiter2);
        // But close it
        conn.close();
        // We have a race here
        if (waiter2.isComplete()) {
            // Either the waiter acquires the recycled connection before it's closed
        } else {
            // Or a connection request happens
            conn = connector.assertRequest();
        }
    }

    @Test
    public void testQueueMaxSize() {
        checkQueueMaxSize(2, 3);
        checkQueueMaxSize(0, 3);
    }

    @Test
    public void testStress() {
        int numActors = 16;
        int numConnections = 1000;
        ConnectionPoolTest.FakeConnectionProvider connector = new ConnectionPoolTest.FakeConnectionProvider() {
            @Override
            public void connect(ConnectionListener<ConnectionPoolTest.FakeConnection> listener, ContextInternal context, Handler<AsyncResult<ConnectResult<ConnectionPoolTest.FakeConnection>>> handler) {
                int i = ThreadLocalRandom.current().nextInt(100);
                ConnectionPoolTest.FakeConnection conn = new ConnectionPoolTest.FakeConnection(context, listener, Future.<ConnectResult<ConnectionPoolTest.FakeConnection>>future().setHandler(handler));
                if (i < 10) {
                    conn.fail(new Exception("Could not connect"));
                } else {
                    conn.connect();
                }
            }
        };
        ConnectionPoolTest.FakeConnectionManager mgr = new ConnectionPoolTest.FakeConnectionManager((-1), 16, connector);
        Thread[] actors = new Thread[numActors];
        for (int i = 0; i < numActors; i++) {
            actors[i] = new Thread(() -> {
                CountDownLatch latch = new CountDownLatch(numConnections);
                for (int i1 = 0; i1 < numConnections; i1++) {
                    mgr.getConnection(new ConnectionPoolTest.FakeWaiter() {
                        @Override
                        public void handleFailure(Throwable failure) {
                            latch.countDown();
                        }

                        @Override
                        public void handleConnection(ConnectionPoolTest.FakeConnection conn) {
                            int action = ThreadLocalRandom.current().nextInt(100);
                            if (action < (-1)) {
                                latch.countDown();
                                conn.listener.onRecycle(0L);
                            } else /* else if (i < 30) {
                            latch.countDown();
                            throw new Exception();
                            }
                             */
                            {
                                vertx.setTimer(10, ( id) -> {
                                    if (action < 15) {
                                        conn.close();
                                    } else {
                                        conn.recycle();
                                    }
                                    latch.countDown();
                                });
                            }
                        }
                    });
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            actors[i].start();
        }
        for (int i = 0; i < (actors.length); i++) {
            try {
                actors[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AsyncTestBase.assertWaitUntil(() -> mgr.closed());
        assertEquals(1, mgr.closeCount());
        // Check state at the end
        assertEquals(0, mgr.size());
        assertEquals(0, mgr.pool.waitersInQueue());
        assertEquals(0, mgr.pool.weight());
        assertEquals(0, mgr.pool.capacity());
    }

    class FakeWaiter {
        protected final ContextInternal context;

        private boolean cancelled;

        private boolean completed;

        private Object result;

        private final io.vertx.core.Handler<AsyncResult<ConnectionPoolTest.FakeConnection>> handler;

        FakeWaiter() {
            context = ((ContextInternal) (vertx.getOrCreateContext()));
            handler = ( ar) -> {
                if (ar.succeeded()) {
                    handleConnection(ar.result());
                } else {
                    handleFailure(ar.cause());
                }
            };
        }

        synchronized boolean cancel() {
            if (completed) {
                return false;
            } else {
                cancelled = true;
                return true;
            }
        }

        synchronized void assertSuccess(ConnectionPoolTest.FakeConnection conn) {
            assertSame(conn, result);
        }

        synchronized void assertFailure(Throwable failure) {
            assertSame(failure, result);
        }

        synchronized boolean isComplete() {
            return completed;
        }

        synchronized boolean isSuccess() {
            return (completed) && ((result) instanceof ConnectionPoolTest.FakeConnection);
        }

        synchronized boolean isFailure() {
            return (completed) && ((result) instanceof Throwable);
        }

        public synchronized void handleFailure(Throwable failure) {
            assertFalse(completed);
            completed = true;
            result = failure;
        }

        public synchronized void handleConnection(ConnectionPoolTest.FakeConnection conn) {
            assertFalse(completed);
            completed = true;
            if (cancelled) {
                conn.listener.onRecycle(0L);
            } else {
                synchronized(conn) {
                    (conn.inflight)++;
                }
                result = conn;
            }
        }

        long recycle() {
            ConnectionPoolTest.FakeConnection conn = ((ConnectionPoolTest.FakeConnection) (result));
            return conn.recycle();
        }
    }

    /* class FakeConnnectionPool implements ConnectionPool<FakeConnection>, Function<SocketAddress, ConnectionPool<FakeConnection>> {

    private final SocketAddress address = SocketAddress.inetSocketAddress(8080, "localhost");
    private final int maxSize;
    private final ArrayDeque<FakeConnection> available = new ArrayDeque<>();
    private final Set<FakeConnection> all = new HashSet<>();
    private boolean closed = true;
    private int sequence;

    FakeConnnectionPool(int maxSize) {
    this.maxSize = maxSize;
    }

    @Override
    public Deque<FakeConnection> available() {
    return available;
    }

    @Override
    public Set<FakeConnection> all() {
    return all;
    }

    synchronized int size() {
    return available.size();
    }

    synchronized boolean contains(FakeConnection conn) {
    Deque<ConnectionHolder<FakeConnection>> a = (Deque<ConnectionHolder<FakeConnection>>)(Deque) available;
    for (ConnectionHolder<FakeConnection> b : a) {
    if (b.connection() == conn) {
    return true;
    }
    }
    return false;
    }

    synchronized int sequence() {
    return sequence;
    }

    @Override
    public synchronized FakeConnnectionPool apply(SocketAddress socketAddress) {
    if (!socketAddress.equals(address)) {
    throw new AssertionError();
    }
    if (!closed) {
    throw new AssertionError();
    }
    closed = false;
    sequence++;
    return this;
    }

    @Override
    public synchronized int maxSize() {
    if (closed) {
    throw new AssertionError();
    }
    return maxSize;
    }

    @Override
    public synchronized boolean canBorrow(int connCount) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized FakeConnection pollConnection() {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean canCreateConnection(int connCount) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean initConnection(FakeConnection conn) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void recycleConnection(FakeConnection conn) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void evictConnection(FakeConnection conn) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean isValid(FakeConnection conn) {
    throw new UnsupportedOperationException();
    }

    @Override
    public synchronized ContextImpl getContext(FakeConnection conn) {
    throw new UnsupportedOperationException();
    }

    public synchronized void close() {
    if (closed) {
    throw new AssertionError();
    }
    closed = true;
    available.clear();
    all.clear();
    }

    synchronized boolean isClosed() {
    return closed;
    }
    }
     */
    class FakeConnection {
        private static final int DISCONNECTED = 0;

        private static final int CONNECTING = 1;

        private static final int CONNECTED = 2;

        private static final int CLOSED = 3;

        private final ContextInternal context;

        private final ConnectionListener<ConnectionPoolTest.FakeConnection> listener;

        private final io.vertx.core.Future<ConnectResult<ConnectionPoolTest.FakeConnection>> future;

        private long inflight;

        private long concurrency = 1;

        private int status = ConnectionPoolTest.FakeConnection.DISCONNECTED;

        FakeConnection(ContextInternal context, ConnectionListener<ConnectionPoolTest.FakeConnection> listener, Future<ConnectResult<ConnectionPoolTest.FakeConnection>> future) {
            this.context = context;
            this.listener = listener;
            this.future = future;
        }

        synchronized void close() {
            if ((status) != (ConnectionPoolTest.FakeConnection.CONNECTED)) {
                throw new IllegalStateException();
            }
            status = ConnectionPoolTest.FakeConnection.CLOSED;
            listener.onEvict();
        }

        synchronized long recycle(boolean dispose) {
            return recycle((dispose ? 0L : Long.MAX_VALUE));
        }

        synchronized long recycle() {
            return recycle(true);
        }

        synchronized long recycle(long timestamp) {
            inflight -= 1;
            listener.onRecycle(timestamp);
            return inflight;
        }

        synchronized ConnectionPoolTest.FakeConnection concurrency(long value) {
            if (value < 0) {
                throw new IllegalArgumentException("Invalid concurrency");
            }
            if ((status) == (ConnectionPoolTest.FakeConnection.CONNECTED)) {
                if ((concurrency) != value) {
                    concurrency = value;
                    listener.onConcurrencyChange(value);
                }
            } else {
                concurrency = value;
            }
            return this;
        }

        ConnectionPoolTest.FakeConnection awaitConnected() {
            AsyncTestBase.waitUntil(() -> {
                synchronized(this) {
                    return (status) == (ConnectionPoolTest.FakeConnection.CONNECTED);
                }
            });
            return this;
        }

        synchronized ConnectionPoolTest.FakeConnection connect() {
            if ((status) != (ConnectionPoolTest.FakeConnection.DISCONNECTED)) {
                throw new IllegalStateException();
            }
            status = ConnectionPoolTest.FakeConnection.CONNECTING;
            context.nettyEventLoop().execute(() -> {
                synchronized(io.vertx.core.net.FakeConnection.this) {
                    status = FakeConnection.CONNECTED;
                    future.complete(new ConnectResult<>(this, concurrency, 1));
                }
            });
            return this;
        }

        void fail(Throwable err) {
            context.nettyEventLoop().execute(() -> future.tryFail(err));
        }
    }

    abstract class FakeConnectionProviderBase implements ConnectionProvider<ConnectionPoolTest.FakeConnection> {
        @Override
        public void close(ConnectionPoolTest.FakeConnection conn) {
            conn.listener.onEvict();
        }
    }

    class FakeConnectionProvider extends ConnectionPoolTest.FakeConnectionProviderBase {
        private final Deque<ConnectionPoolTest.FakeConnection> pendingRequests = new ConcurrentLinkedDeque<>();

        void assertRequests(int expectedSize) {
            assertEquals(expectedSize, pendingRequests.size());
        }

        int requests() {
            return pendingRequests.size();
        }

        ConnectionPoolTest.FakeConnection assertRequest() {
            AsyncTestBase.waitUntil(() -> (pendingRequests.size()) > 0);
            ConnectionPoolTest.FakeConnection request = pendingRequests.poll();
            assertNotNull(request);
            return request;
        }

        @Override
        public void connect(ConnectionListener<ConnectionPoolTest.FakeConnection> listener, ContextInternal context, Handler<AsyncResult<ConnectResult<ConnectionPoolTest.FakeConnection>>> handler) {
            pendingRequests.add(new ConnectionPoolTest.FakeConnection(context, listener, Future.<ConnectResult<ConnectionPoolTest.FakeConnection>>future().setHandler(handler)));
        }
    }
}

