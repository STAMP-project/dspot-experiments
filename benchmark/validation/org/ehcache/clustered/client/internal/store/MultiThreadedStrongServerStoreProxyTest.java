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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.common.internal.store.Chain;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MultiThreadedStrongServerStoreProxyTest extends AbstractServerStoreProxyTest {
    private static final String ENTITY_NAME = "testConcurrentHashInvalidationWithAppend";

    private static final int MAX_WAIT_TIME_SECONDS = 30;

    @Test
    public void testConcurrentHashInvalidationListenerWithAppend() throws Exception {
        final AtomicReference<Long> invalidatedHash = new AtomicReference<>();
        SimpleClusterTierClientEntity clientEntity1 = AbstractServerStoreProxyTest.createClientEntity(MultiThreadedStrongServerStoreProxyTest.ENTITY_NAME, MultiThreadedStrongServerStoreProxyTest.getServerStoreConfiguration(), true, true);
        StrongServerStoreProxy serverStoreProxy1 = new StrongServerStoreProxy(MultiThreadedStrongServerStoreProxyTest.ENTITY_NAME, clientEntity1, Mockito.mock(ServerCallback.class));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch beforeValidationLatch = new CountDownLatch(1);
        CountDownLatch afterValidationLatch = new CountDownLatch(1);
        executor.submit(() -> {
            try {
                SimpleClusterTierClientEntity clientEntity2 = AbstractServerStoreProxyTest.createClientEntity(MultiThreadedStrongServerStoreProxyTest.ENTITY_NAME, MultiThreadedStrongServerStoreProxyTest.getServerStoreConfiguration(), false, false);
                StrongServerStoreProxy serverStoreProxy2 = new StrongServerStoreProxy(MultiThreadedStrongServerStoreProxyTest.ENTITY_NAME, clientEntity2, new ServerCallback() {
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
                // avoid a warning
                Assert.assertNotNull(serverStoreProxy2);
                Assert.assertTrue(beforeValidationLatch.await(MultiThreadedStrongServerStoreProxyTest.MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS));
                clientEntity2.validate(MultiThreadedStrongServerStoreProxyTest.getServerStoreConfiguration());
                afterValidationLatch.countDown();
            } catch (Exception e) {
                Assert.fail(("Unexpected Exception " + (e.getMessage())));
            }
        });
        serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
        Assert.assertNull(invalidatedHash.get());
        beforeValidationLatch.countDown();
        Assert.assertTrue(afterValidationLatch.await(MultiThreadedStrongServerStoreProxyTest.MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        serverStoreProxy1.append(1L, ChainUtils.createPayload(1L));
        MatcherAssert.assertThat(invalidatedHash.get(), Is.is(1L));
        executor.shutdownNow();
    }
}

