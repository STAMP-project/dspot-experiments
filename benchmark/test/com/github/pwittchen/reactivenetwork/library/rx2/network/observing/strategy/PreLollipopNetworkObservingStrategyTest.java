/**
 * Copyright (C) 2016 Piotr Wittchen
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
package com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy;


import NetworkInfo.State.CONNECTED;
import RuntimeEnvironment.application;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;


// We are suppressing PMD here because we want static imports in unit tests
@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({ "PMD", "NullAway" })
public class PreLollipopNetworkObservingStrategyTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    private PreLollipopNetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();

    @Mock
    private BroadcastReceiver broadcastReceiver;

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void shouldObserveConnectivity() {
        // given
        final NetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();
        final Context context = application.getApplicationContext();
        // when
        strategy.observeNetworkConnectivity(context).subscribe(new io.reactivex.functions.Consumer<Connectivity>() {
            @Override
            public void accept(Connectivity connectivity) throws Exception {
                // then
                assertThat(connectivity.state()).isEqualTo(CONNECTED);
            }
        });
    }

    @Test
    public void shouldStopObservingConnectivity() {
        // given
        final NetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();
        final Context context = application.getApplicationContext();
        final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
        final TestObserver<Connectivity> observer = new TestObserver();
        // when
        observable.subscribe(observer);
        observer.dispose();
        // then
        assertThat(observer.isDisposed()).isTrue();
    }

    @Test
    public void shouldCallOnError() {
        // given
        final String message = "error message";
        final Exception exception = new Exception();
        // when
        strategy.onError(message, exception);
        // then
        Mockito.verify(strategy, Mockito.times(1)).onError(message, exception);
    }

    @Test
    public void shouldTryToUnregisterReceiver() {
        // given
        final PreLollipopNetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();
        final Application context = Mockito.spy(application);
        // when
        strategy.tryToUnregisterReceiver(context, broadcastReceiver);
        // then
        Mockito.verify(context).unregisterReceiver(broadcastReceiver);
    }

    @Test
    public void shouldTryToUnregisterReceiverAfterDispose() {
        // given
        final Context context = application.getApplicationContext();
        final TestObserver<Connectivity> observer = new TestObserver();
        // when
        strategy.observeNetworkConnectivity(context).subscribe(observer);
        observer.dispose();
        // then
        Mockito.verify(strategy).tryToUnregisterReceiver(ArgumentMatchers.eq(context), ArgumentMatchers.any(BroadcastReceiver.class));
    }
}

