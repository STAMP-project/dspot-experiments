/**
 * Copyright (C) 2015 Square, Inc.
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
package okhttp3.internal.connection;


import Internal.instance;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.TestUtil;
import okhttp3.internal.Internal;
import org.junit.Assert;
import org.junit.Test;


public final class ConnectionPoolTest {
    private final Address addressA = newAddress("a");

    private final Route routeA1 = newRoute(addressA);

    private final Address addressB = newAddress("b");

    private final Route routeB1 = newRoute(addressB);

    private final Address addressC = newAddress("c");

    private final Route routeC1 = newRoute(addressC);

    static {
        Internal.initializeInstanceForTests();
    }

    @Test
    public void connectionsEvictedWhenIdleLongEnough() throws Exception {
        RealConnectionPool pool = new RealConnectionPool(Integer.MAX_VALUE, 100L, TimeUnit.NANOSECONDS);
        pool.cleanupRunning = true;// Prevent the cleanup runnable from being started.

        RealConnection c1 = newConnection(pool, routeA1, 50L);
        // Running at time 50, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(100L, pool.cleanup(50L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        // Running at time 60, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(90L, pool.cleanup(60L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        // Running at time 149, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(1L, pool.cleanup(149L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        // Running at time 150, the pool evicts.
        Assert.assertEquals(0, pool.cleanup(150L));
        Assert.assertEquals(0, pool.connectionCount());
        Assert.assertTrue(c1.socket().isClosed());
        // Running again, the pool reports that no further runs are necessary.
        Assert.assertEquals((-1), pool.cleanup(150L));
        Assert.assertEquals(0, pool.connectionCount());
        Assert.assertTrue(c1.socket().isClosed());
    }

    @Test
    public void inUseConnectionsNotEvicted() throws Exception {
        ConnectionPool poolApi = new ConnectionPool(Integer.MAX_VALUE, 100L, TimeUnit.NANOSECONDS);
        RealConnectionPool pool = instance.realConnectionPool(poolApi);
        pool.cleanupRunning = true;// Prevent the cleanup runnable from being started.

        RealConnection c1 = newConnection(pool, routeA1, 50L);
        synchronized(pool) {
            OkHttpClient client = new OkHttpClient.Builder().connectionPool(poolApi).build();
            Call call = client.newCall(newRequest(addressA));
            Transmitter transmitter = new Transmitter(client, call);
            transmitter.prepareToConnect(call.request());
            transmitter.acquireConnectionNoEvents(c1);
        }
        // Running at time 50, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(100L, pool.cleanup(50L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        // Running at time 60, the pool returns that nothing can be evicted until time 160.
        Assert.assertEquals(100L, pool.cleanup(60L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        // Running at time 160, the pool returns that nothing can be evicted until time 260.
        Assert.assertEquals(100L, pool.cleanup(160L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
    }

    @Test
    public void cleanupPrioritizesEarliestEviction() throws Exception {
        RealConnectionPool pool = new RealConnectionPool(Integer.MAX_VALUE, 100L, TimeUnit.NANOSECONDS);
        pool.cleanupRunning = true;// Prevent the cleanup runnable from being started.

        RealConnection c1 = newConnection(pool, routeA1, 75L);
        RealConnection c2 = newConnection(pool, routeB1, 50L);
        // Running at time 75, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(75L, pool.cleanup(75L));
        Assert.assertEquals(2, pool.connectionCount());
        // Running at time 149, the pool returns that nothing can be evicted until time 150.
        Assert.assertEquals(1L, pool.cleanup(149L));
        Assert.assertEquals(2, pool.connectionCount());
        // Running at time 150, the pool evicts c2.
        Assert.assertEquals(0L, pool.cleanup(150L));
        Assert.assertEquals(1, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        Assert.assertTrue(c2.socket().isClosed());
        // Running at time 150, the pool returns that nothing can be evicted until time 175.
        Assert.assertEquals(25L, pool.cleanup(150L));
        Assert.assertEquals(1, pool.connectionCount());
        // Running at time 175, the pool evicts c1.
        Assert.assertEquals(0L, pool.cleanup(175L));
        Assert.assertEquals(0, pool.connectionCount());
        Assert.assertTrue(c1.socket().isClosed());
        Assert.assertTrue(c2.socket().isClosed());
    }

    @Test
    public void oldestConnectionsEvictedIfIdleLimitExceeded() throws Exception {
        RealConnectionPool pool = new RealConnectionPool(2, 100L, TimeUnit.NANOSECONDS);
        pool.cleanupRunning = true;// Prevent the cleanup runnable from being started.

        RealConnection c1 = newConnection(pool, routeA1, 50L);
        RealConnection c2 = newConnection(pool, routeB1, 75L);
        // With 2 connections, there's no need to evict until the connections time out.
        Assert.assertEquals(50L, pool.cleanup(100L));
        Assert.assertEquals(2, pool.connectionCount());
        Assert.assertFalse(c1.socket().isClosed());
        Assert.assertFalse(c2.socket().isClosed());
        // Add a third connection
        RealConnection c3 = newConnection(pool, routeC1, 75L);
        // The third connection bounces the first.
        Assert.assertEquals(0L, pool.cleanup(100L));
        Assert.assertEquals(2, pool.connectionCount());
        Assert.assertTrue(c1.socket().isClosed());
        Assert.assertFalse(c2.socket().isClosed());
        Assert.assertFalse(c3.socket().isClosed());
    }

    @Test
    public void leakedAllocation() throws Exception {
        ConnectionPool poolApi = new ConnectionPool(Integer.MAX_VALUE, 100L, TimeUnit.NANOSECONDS);
        RealConnectionPool pool = instance.realConnectionPool(poolApi);
        pool.cleanupRunning = true;// Prevent the cleanup runnable from being started.

        RealConnection c1 = newConnection(pool, routeA1, 0L);
        allocateAndLeakAllocation(poolApi, c1);
        TestUtil.awaitGarbageCollection();
        Assert.assertEquals(0L, pool.cleanup(100L));
        Assert.assertEquals(Collections.emptyList(), c1.transmitters);
        Assert.assertTrue(c1.noNewExchanges);// Can't allocate once a leak has been detected.

    }
}

