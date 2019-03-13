/**
 * Copyright 2015 Victor Albertos
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
package io.rx_cache2.internal;


import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public final class ProvidersRxCacheReactiveTypesTest {
    private static final String message = "message";

    private static final List<Mock> mocks = Arrays.asList(new Mock(ProvidersRxCacheReactiveTypesTest.message));

    private static final RuntimeException error = new RuntimeException(ProvidersRxCacheReactiveTypesTest.message);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProvidersRxCache providersRxCache;

    @Test
    public void Verify_Observable() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocks(Observable.just(ProvidersRxCacheReactiveTypesTest.mocks)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
        Assert.assertThat(testObserver.values().get(0).get(0).getMessage(), Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Observable_Error() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocks(Observable.<List<Mock>>error(ProvidersRxCacheReactiveTypesTest.error)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
        Assert.assertThat(testObserver.errors().size(), Is.is(1));
        Assert.assertThat(getExceptions().get(0).getMessage(), Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Single() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocksSingle(Single.just(ProvidersRxCacheReactiveTypesTest.mocks)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
        String expected = testObserver.values().get(0).get(0).getMessage();
        Assert.assertThat(expected, Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Single_Error() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocksSingle(Single.<List<Mock>>error(ProvidersRxCacheReactiveTypesTest.error)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
        Assert.assertThat(testObserver.errors().size(), Is.is(1));
        Assert.assertThat(getExceptions().get(0).getMessage(), Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Maybe() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocksMaybe(Maybe.just(ProvidersRxCacheReactiveTypesTest.mocks)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
        String expected = testObserver.values().get(0).get(0).getMessage();
        Assert.assertThat(expected, Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Maybe_Error() {
        TestObserver<List<Mock>> testObserver = providersRxCache.getMocksMaybe(Maybe.<List<Mock>>error(ProvidersRxCacheReactiveTypesTest.error)).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoValues();
        testObserver.assertNotComplete();
        Assert.assertThat(testObserver.errors().size(), Is.is(1));
        Assert.assertThat(getExceptions().get(0).getMessage(), Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Flowable() {
        TestSubscriber<List<Mock>> testSubscriber = providersRxCache.getMocksFlowable(Flowable.just(ProvidersRxCacheReactiveTypesTest.mocks)).test();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
        String expected = testSubscriber.values().get(0).get(0).getMessage();
        Assert.assertThat(expected, Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }

    @Test
    public void Verify_Flowable_Error() {
        TestSubscriber<List<Mock>> testSubscriber = providersRxCache.getMocksFlowable(Flowable.<List<Mock>>error(ProvidersRxCacheReactiveTypesTest.error)).test();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
        testSubscriber.assertNotComplete();
        Assert.assertThat(testSubscriber.errors().size(), Is.is(1));
        Assert.assertThat(getExceptions().get(0).getMessage(), Is.is(ProvidersRxCacheReactiveTypesTest.message));
    }
}

