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
package com.hazelcast.client.cache.impl;


import com.hazelcast.client.impl.clientside.ClientMessageDecoder;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetCodec;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.cache.configuration.Factory;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CallbackAwareClientDelegatingFutureTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "MyCache";

    private static final ClientMessageDecoder CACHE_GET_RESPONSE_DECODER = new ClientMessageDecoder() {
        @Override
        public <T> T decodeClientMessage(ClientMessage clientMessage) {
            return ((T) (CacheGetCodec.decodeResponse(clientMessage).response));
        }
    };

    private TestHazelcastFactory factory;

    private HazelcastClientInstanceImpl client;

    @Test
    public void test_CallbackAwareClientDelegatingFuture_when_noTimeOut_noError() throws InterruptedException, ExecutionException {
        test_CallbackAwareClientDelegatingFuture(false, false);
    }

    @Test
    public void test_CallbackAwareClientDelegatingFuture_when_timeOut_but_noError() throws InterruptedException, ExecutionException {
        test_CallbackAwareClientDelegatingFuture(true, false);
    }

    @Test
    public void test_CallbackAwareClientDelegatingFuture_when_noTimeOut_but_error() throws InterruptedException, ExecutionException {
        test_CallbackAwareClientDelegatingFuture(false, true);
    }

    public static class BlockableCacheLoaderFactory implements Factory<CallbackAwareClientDelegatingFutureTest.BlockableCacheLoader> {
        private final int blockMillis;

        private final boolean throwError;

        public BlockableCacheLoaderFactory(int blockMillis, boolean throwError) {
            this.blockMillis = blockMillis;
            this.throwError = throwError;
        }

        @Override
        public CallbackAwareClientDelegatingFutureTest.BlockableCacheLoader create() {
            return new CallbackAwareClientDelegatingFutureTest.BlockableCacheLoader(blockMillis, throwError);
        }
    }

    public static class BlockableCacheLoader implements CacheLoader<Integer, String> {
        private final int blockMillis;

        private final boolean throwError;

        public BlockableCacheLoader(int blockMillis, boolean throwError) {
            this.blockMillis = blockMillis;
            this.throwError = throwError;
        }

        @Override
        public String load(Integer key) throws CacheLoaderException {
            if (throwError) {
                throw new CacheLoaderException();
            }
            if ((blockMillis) > 0) {
                sleepMillis(blockMillis);
            }
            return "Value-" + key;
        }

        @Override
        public Map<Integer, String> loadAll(Iterable<? extends Integer> keys) throws CacheLoaderException {
            throw new UnsupportedOperationException();
        }
    }
}

