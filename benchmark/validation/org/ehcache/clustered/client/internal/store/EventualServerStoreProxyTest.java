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


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.Matchers;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.greaterThan;


public class EventualServerStoreProxyTest extends AbstractServerStoreProxyTest {
    @Test
    public void testServerSideEvictionFiresInvalidations() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = EventualServerStoreProxyTest.createClientEntity("testServerSideEvictionFiresInvalidations", true);
        SimpleClusterTierClientEntity clientEntity2 = EventualServerStoreProxyTest.createClientEntity("testServerSideEvictionFiresInvalidations", false);
        final List<Long> store1InvalidatedHashes = new CopyOnWriteArrayList<>();
        final List<Long> store2InvalidatedHashes = new CopyOnWriteArrayList<>();
        EventualServerStoreProxy serverStoreProxy1 = new EventualServerStoreProxy("testServerSideEvictionFiresInvalidations", clientEntity1, new ServerCallback() {
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
        EventualServerStoreProxy serverStoreProxy2 = new EventualServerStoreProxy("testServerSideEvictionFiresInvalidations", clientEntity2, new ServerCallback() {
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
            MatcherAssert.assertThat(elements1, Matchers.matchesChain(elements2));
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
        EventualServerStoreProxyTest.assertThatClientsWaitingForInvalidationIsEmpty("testServerSideEvictionFiresInvalidations");
    }

    @Test
    public void testHashInvalidationListenerWithAppend() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = EventualServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithAppend", true);
        SimpleClusterTierClientEntity clientEntity2 = EventualServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithAppend", false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Long> invalidatedHash = new AtomicReference<>();
        EventualServerStoreProxy serverStoreProxy1 = new EventualServerStoreProxy("testHashInvalidationListenerWithAppend", clientEntity1, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                invalidatedHash.set(hash);
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
        EventualServerStoreProxy serverStoreProxy2 = new EventualServerStoreProxy("testServerSideEvictionFiresInvalidations", clientEntity2, Mockito.mock(ServerCallback.class));
        serverStoreProxy2.append(1L, ChainUtils.createPayload(1L));
        latch.await(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(invalidatedHash.get(), Is.is(1L));
        EventualServerStoreProxyTest.assertThatClientsWaitingForInvalidationIsEmpty("testHashInvalidationListenerWithAppend");
    }

    @Test
    public void testHashInvalidationListenerWithGetAndAppend() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = EventualServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithGetAndAppend", true);
        SimpleClusterTierClientEntity clientEntity2 = EventualServerStoreProxyTest.createClientEntity("testHashInvalidationListenerWithGetAndAppend", false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Long> invalidatedHash = new AtomicReference<>();
        EventualServerStoreProxy serverStoreProxy1 = new EventualServerStoreProxy("testHashInvalidationListenerWithGetAndAppend", clientEntity1, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                invalidatedHash.set(hash);
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
        EventualServerStoreProxy serverStoreProxy2 = new EventualServerStoreProxy("testHashInvalidationListenerWithGetAndAppend", clientEntity2, Mockito.mock(ServerCallback.class));
        serverStoreProxy2.getAndAppend(1L, ChainUtils.createPayload(1L));
        latch.await(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(invalidatedHash.get(), Is.is(1L));
        EventualServerStoreProxyTest.assertThatClientsWaitingForInvalidationIsEmpty("testHashInvalidationListenerWithGetAndAppend");
    }

    @Test
    public void testAllInvalidationListener() throws Exception {
        SimpleClusterTierClientEntity clientEntity1 = EventualServerStoreProxyTest.createClientEntity("testAllInvalidationListener", true);
        SimpleClusterTierClientEntity clientEntity2 = EventualServerStoreProxyTest.createClientEntity("testAllInvalidationListener", false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean invalidatedAll = new AtomicBoolean();
        EventualServerStoreProxy serverStoreProxy1 = new EventualServerStoreProxy("testAllInvalidationListener", clientEntity1, new ServerCallback() {
            @Override
            public void onInvalidateHash(long hash) {
                throw new AssertionError("Should not be called");
            }

            @Override
            public void onInvalidateAll() {
                invalidatedAll.set(true);
                latch.countDown();
            }

            @Override
            public Chain compact(Chain chain) {
                throw new AssertionError();
            }
        });
        EventualServerStoreProxy serverStoreProxy2 = new EventualServerStoreProxy("testAllInvalidationListener", clientEntity2, Mockito.mock(ServerCallback.class));
        serverStoreProxy2.clear();
        latch.await(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(invalidatedAll.get(), Is.is(true));
        EventualServerStoreProxyTest.assertThatClientsWaitingForInvalidationIsEmpty("testAllInvalidationListener");
    }
}

