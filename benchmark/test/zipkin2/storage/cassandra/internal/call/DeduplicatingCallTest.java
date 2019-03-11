/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage.cassandra.internal.call;


import com.datastax.driver.core.ResultSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.Mockito;
import zipkin2.Call;


public class DeduplicatingCallTest {
    Function<String, ListenableFuture<ResultSet>> delegate = ( s) -> Futures.immediateFuture(Mockito.mock(ResultSet.class));

    DeduplicatingCallTest.TestDeduplicatingCall.Factory callFactory = new DeduplicatingCallTest.TestDeduplicatingCall.Factory(delegate);

    @Test
    public void exceptionArentCached_immediateFuture() throws Exception {
        AtomicBoolean first = new AtomicBoolean(true);
        callFactory = new DeduplicatingCallTest.TestDeduplicatingCall.Factory(( s) -> {
            if (first.getAndSet(false)) {
                return Futures.immediateFailedFuture(new IllegalArgumentException());
            }
            return Futures.immediateFuture(null);
        });
        exceptionsArentCached();
    }

    @Test
    public void exceptionArentCached_deferredFuture() throws Exception {
        ListeningExecutorService exec = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        AtomicBoolean first = new AtomicBoolean(true);
        try {
            callFactory = new DeduplicatingCallTest.TestDeduplicatingCall.Factory(( s) -> {
                if (first.getAndSet(false)) {
                    return exec.submit(() -> {
                        Thread.sleep(50);
                        throw new IllegalArgumentException();
                    });
                }
                return Futures.immediateFuture(null);
            });
            exceptionsArentCached();
        } finally {
            exec.shutdownNow();
        }
    }

    @Test
    public void exceptionArentCached_creatingFuture() throws Exception {
        AtomicBoolean first = new AtomicBoolean(true);
        callFactory = new DeduplicatingCallTest.TestDeduplicatingCall.Factory(( s) -> {
            if (first.getAndSet(false)) {
                throw new IllegalArgumentException();
            }
            return Futures.immediateFuture(null);
        });
        exceptionsArentCached();
    }

    static class TestDeduplicatingCall extends DeduplicatingCall<String> {
        static class Factory extends DeduplicatingCall.Factory<String, DeduplicatingCallTest.TestDeduplicatingCall> {
            final Function<String, ListenableFuture<ResultSet>> delegate;

            Factory(Function<String, ListenableFuture<ResultSet>> delegate) {
                super(1000, 1000);
                this.delegate = delegate;
            }

            @Override
            protected DeduplicatingCallTest.TestDeduplicatingCall newCall(String string) {
                return new DeduplicatingCallTest.TestDeduplicatingCall(this, string);
            }
        }

        @Override
        protected ListenableFuture<ResultSet> newFuture() {
            return ((DeduplicatingCallTest.TestDeduplicatingCall.Factory) (factory)).delegate.apply(input);
        }

        @Override
        public Call<ResultSet> clone() {
            return new DeduplicatingCallTest.TestDeduplicatingCall(((DeduplicatingCallTest.TestDeduplicatingCall.Factory) (factory)), input);
        }

        TestDeduplicatingCall(DeduplicatingCallTest.TestDeduplicatingCall.Factory factory, String key) {
            super(factory, key);
        }
    }
}

