/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.store;


import Timeouts.DEFAULT;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.Matchers;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terracotta.exception.ConnectionClosedException;

import static org.hamcrest.Matchers.greaterThan;


public class StrongServerStoreProxyTest extends AbstractServerStoreProxyTest {
    @Test
    public void testServerSideEvictionFiresInvalidations() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testServerSideEvictionFiresInvalidations", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testServerSideEvictionFiresInvalidations", false);
        final List<Long> store1InvalidatedHashes = new CopyOnWriteArrayList<>();
        final List<Long> store2InvalidatedHashes = new CopyOnWriteArrayList<>();
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testServerSideEvictionFiresInvalidations", clientEntity1, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                store1InvalidatedHashes.add(hash);
            }

            @Override
            public void onInvalidateAll() {
                Assert.fail("should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testServerSideEvictionFiresInvalidations", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                store2InvalidatedHashes.add(hash);
            }

            @Override
            public void onInvalidateAll() {
                Assert.fail("should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                return chain;
            }
        });
        final int ITERATIONS = 40;
        for (int i = 0; i < ITERATIONS; i++) {
            serverStoreProxy1.append(i, ChainUtils.createPayload(i, (512 * 1024)));
        }
        int evictionCount = 0;
        int entryCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            Chain elements1 = serverStoreProxy1.get(i);
            Chain elements2 = serverStoreProxy2.get(i);
            MatcherAssert.assertThat(elements2, Matchers.matchesChain(elements2));
            if (!(elements1.isEmpty())) {
                entryCount++;
            } else {
                evictionCount++;
            }
        }
        // there has to be server-side evictions, otherwise this test is useless
        MatcherAssert.assertThat(store1InvalidatedHashes.size(), greaterThan(0));
        // test that each time the server evicted, the originating client got notified
        MatcherAssert.assertThat(store1InvalidatedHashes.size(), Is.is((ITERATIONS - entryCount)));
        // test that each time the server evicted, the other client got notified on top of normal invalidations
        MatcherAssert.assertThat(store2InvalidatedHashes.size(), Is.is((ITERATIONS + evictionCount)));
    }

    @Test
    public void testHashInvalidationListenerWithAppend() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithAppend", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithAppend", false);
        final AtomicReference<Long> invalidatedHash = new AtomicReference<>();
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testHashInvalidationListenerWithAppend", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testHashInvalidationListenerWithAppend", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                invalidatedHash.set(hash);
            }

            @Override
            public void onInvalidateAll() {
                throw new AssertionError("Should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
        MatcherAssert.assertThat(invalidatedHash.get(), Is.is(1L));
    }

    @Test
    public void testConcurrentHashInvalidationListenerWithAppend() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testConcurrentHashInvalidationListenerWithAppend", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testConcurrentHashInvalidationListenerWithAppend", false);
        final AtomicBoolean invalidating = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testConcurrentHashInvalidationListenerWithAppend", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testConcurrentHashInvalidationListenerWithAppend", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                if (!(invalidating.compareAndSet(false, true))) {
                    Assert.fail("Both threads entered the listener concurrently");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    throw new AssertionError(ie);
                }
                invalidating.set(false);
                latch.countDown();
            }

            @Override
            public void onInvalidateAll() {
                throw new AssertionError("Should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.submit(() -> {
                serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
                return null;
            });
            executor.submit(() -> {
                serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
                return null;
            });
            if (!(latch.await(5, TimeUnit.SECONDS))) {
                Assert.fail("Both listeners were not called");
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testHashInvalidationListenerWithGetAndAppend() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithGetAndAppend", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithGetAndAppend", false);
        final AtomicReference<Long> invalidatedHash = new AtomicReference<>();
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testHashInvalidationListenerWithGetAndAppend", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testHashInvalidationListenerWithGetAndAppend", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                invalidatedHash.set(hash);
            }

            @Override
            public void onInvalidateAll() {
                throw new AssertionError("Should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        serverStoreProxy1.getAndAppend(1L, ChainUtils.createPayload(1L));
        MatcherAssert.assertThat(invalidatedHash.get(), Is.is(1L));
    }

    @Test
    public void testAllInvalidationListener() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testAllInvalidationListener", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testAllInvalidationListener", false);
        final AtomicBoolean invalidatedAll = new AtomicBoolean();
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testAllInvalidationListener", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testAllInvalidationListener", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                throw new AssertionError("Should not be called");
            }

            @Override
            public void onInvalidateAll() {
                invalidatedAll.set(true);
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        serverStoreProxy1.clear();
        MatcherAssert.assertThat(invalidatedAll.get(), Is.is(true));
    }

    @Test
    public void testConcurrentAllInvalidationListener() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testConcurrentAllInvalidationListener", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testConcurrentAllInvalidationListener", false);
        final AtomicBoolean invalidating = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testConcurrentAllInvalidationListener", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testConcurrentAllInvalidationListener", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                throw new AssertionError("Should not be called");
            }

            @Override
            public void onInvalidateAll() {
                if (!(invalidating.compareAndSet(false, true))) {
                    Assert.fail("Both threads entered the listener concurrently");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    throw new AssertionError(ie);
                }
                invalidating.set(false);
                latch.countDown();
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.submit(() -> {
                serverStoreProxy1.clear();
                return null;
            });
            executor.submit(() -> {
                serverStoreProxy1.clear();
                return null;
            });
            if (!(latch.await(5, TimeUnit.SECONDS))) {
                Assert.fail("Both listeners were not called");
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testAppendInvalidationUnblockedByDisconnection() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testAppendInvalidationUnblockedByDisconnection", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testAppendInvalidationUnblockedByDisconnection", false);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testAppendInvalidationUnblockedByDisconnection", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testAppendInvalidationUnblockedByDisconnection", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                clientEntity1.fireDisconnectionEvent();
            }

            @Override
            public void onInvalidateAll() {
                throw new AssertionError("Should not be called");
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        try {
            serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
            Assert.fail("expected RuntimeException");
        } catch (RuntimeException re) {
            MatcherAssert.assertThat(re.getCause(), IsInstanceOf.instanceOf(IllegalStateException.class));
        }
    }

    @Test
    public void testClearInvalidationUnblockedByDisconnection() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = StrongServerStoreProxyTest.createClientEntity("testClearInvalidationUnblockedByDisconnection", true);
        SimpleClusterTierClientEntity clientEntity2 = StrongServerStoreProxyTest.createClientEntity("testClearInvalidationUnblockedByDisconnection", false);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testClearInvalidationUnblockedByDisconnection", clientEntity1, Mockito.mock(ServerCallback.class));
        StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy("testClearInvalidationUnblockedByDisconnection", clientEntity2, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                throw new AssertionError("Should not be called");
            }

            @Override
            public void onInvalidateAll() {
                clientEntity1.fireDisconnectionEvent();
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        try {
            serverStoreProxy1.clear();
            Assert.fail("expected RuntimeException");
        } catch (RuntimeException re) {
            MatcherAssert.assertThat(re.getCause(), IsInstanceOf.instanceOf(IllegalStateException.class));
        }
    }

    @Test
    public void testAppendThrowsConnectionClosedExceptionDuringHashInvalidation() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = Mockito.mock(SimpleClusterTierClientEntity.class);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testAppendThrowsConnectionClosedExceptionDuringHashInvalidation", clientEntity1, Mockito.mock(ServerCallback.class));
        Mockito.doThrow(new ConnectionClosedException("Test")).when(clientEntity1).invokeAndWaitForReceive(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        Mockito.when(clientEntity1.getTimeouts()).thenReturn(DEFAULT);
        Mockito.when(clientEntity1.isConnected()).thenReturn(true);
        try {
            serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
            Assert.fail("Expected ServerStoreProxyException");
        } catch (ServerStoreProxyException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(ConnectionClosedException.class));
        } catch (RuntimeException e) {
            Assert.fail("Expected ServerStoreProxyException");
        }
    }

    @Test
    public void testClearThrowsConnectionClosedExceptionDuringAllInvaildation() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = Mockito.mock(SimpleClusterTierClientEntity.class);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy("testClearThrowsConnectionClosedExceptionDuringAllInvaildation", clientEntity1, Mockito.mock(ServerCallback.class));
        Mockito.doThrow(new ConnectionClosedException("Test")).when(clientEntity1).invokeAndWaitForRetired(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        Mockito.when(clientEntity1.getTimeouts()).thenReturn(DEFAULT);
        Mockito.when(clientEntity1.isConnected()).thenReturn(true);
        try {
            serverStoreProxy1.clear();
            Assert.fail("Expected ServerStoreProxyException");
        } catch (ServerStoreProxyException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(ConnectionClosedException.class));
        } catch (RuntimeException e) {
            Assert.fail("Expected ServerStoreProxyException");
        }
    }
}

