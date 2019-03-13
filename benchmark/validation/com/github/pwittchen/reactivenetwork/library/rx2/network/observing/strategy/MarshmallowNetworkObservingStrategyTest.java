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


import Build.VERSION_CODES;
import ConnectivityManager.NetworkCallback;
import MarshmallowNetworkObservingStrategy.ERROR_MSG_NETWORK_CALLBACK;
import MarshmallowNetworkObservingStrategy.ERROR_MSG_RECEIVER;
import NetworkInfo.State.CONNECTED;
import RuntimeEnvironment.application;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.PowerManager;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
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


// we're suppressing PMD warnings because we want static imports in tests
@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({ "PMD", "NullAway" })
public class MarshmallowNetworkObservingStrategyTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    private MarshmallowNetworkObservingStrategy strategy = new MarshmallowNetworkObservingStrategy();

    @Mock
    private PowerManager powerManager;

    @Mock
    private ConnectivityManager connectivityManager;

    @Mock
    private Context contextMock;

    @Mock
    private Intent intent;

    @Mock
    private Network network;

    @Spy
    private Context context;

    @Test
    public void shouldObserveConnectivity() {
        // given
        final Context context = application.getApplicationContext();
        // when
        Connectivity connectivity = strategy.observeNetworkConnectivity(context).blockingFirst();
        // then
        assertThat(connectivity.state()).isEqualTo(CONNECTED);
    }

    @Test
    public void shouldStopObservingConnectivity() {
        // given
        final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
        // when
        final Disposable disposable = observable.subscribe();
        disposable.dispose();
        // then
        assertThat(disposable.isDisposed()).isTrue();
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
    public void shouldTryToUnregisterCallbackOnDispose() {
        // given
        final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
        final TestObserver<Connectivity> observer = new TestObserver();
        // when
        observable.subscribe(observer);
        observer.dispose();
        // then
        Mockito.verify(strategy).tryToUnregisterCallback(ArgumentMatchers.any(ConnectivityManager.class));
    }

    @Test
    public void shouldTryToUnregisterReceiverOnDispose() {
        // given
        final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
        final TestObserver<Connectivity> observer = new TestObserver();
        // when
        observable.subscribe(observer);
        observer.dispose();
        // then
        Mockito.verify(strategy).tryToUnregisterReceiver(context);
    }

    @Test
    public void shouldNotBeInIdleModeWhenDeviceIsNotInIdleAndIsNotIgnoringBatteryOptimizations() {
        // given
        preparePowerManagerMocks(Boolean.FALSE, Boolean.FALSE);
        // when
        final boolean isIdleMode = strategy.isIdleMode(contextMock);
        // then
        assertThat(isIdleMode).isFalse();
    }

    @Test
    public void shouldBeInIdleModeWhenDeviceIsNotIgnoringBatteryOptimizations() {
        // given
        preparePowerManagerMocks(Boolean.TRUE, Boolean.FALSE);
        // when
        final boolean isIdleMode = strategy.isIdleMode(contextMock);
        // then
        assertThat(isIdleMode).isTrue();
    }

    @Test
    public void shouldNotBeInIdleModeWhenDeviceIsInIdleModeAndIgnoringBatteryOptimizations() {
        // given
        preparePowerManagerMocks(Boolean.TRUE, Boolean.TRUE);
        // when
        final boolean isIdleMode = strategy.isIdleMode(contextMock);
        // then
        assertThat(isIdleMode).isFalse();
    }

    @Test
    public void shouldNotBeInIdleModeWhenDeviceIsNotInIdleMode() {
        // given
        preparePowerManagerMocks(Boolean.FALSE, Boolean.TRUE);
        // when
        final boolean isIdleMode = strategy.isIdleMode(contextMock);
        // then
        assertThat(isIdleMode).isFalse();
    }

    @Test
    public void shouldReceiveIntentInIdleMode() {
        // given
        preparePowerManagerMocks(Boolean.TRUE, Boolean.FALSE);
        BroadcastReceiver broadcastReceiver = strategy.createIdleBroadcastReceiver();
        // when
        broadcastReceiver.onReceive(contextMock, intent);
        // then
        Mockito.verify(strategy).onNext(ArgumentMatchers.any(Connectivity.class));
    }

    @Test
    public void shouldReceiveIntentWhenIsNotInIdleMode() {
        // given
        preparePowerManagerMocks(Boolean.FALSE, Boolean.FALSE);
        BroadcastReceiver broadcastReceiver = strategy.createIdleBroadcastReceiver();
        // when
        broadcastReceiver.onReceive(contextMock, intent);
        // then
        Mockito.verify(strategy).onNext(ArgumentMatchers.any(Connectivity.class));
    }

    @Test
    public void shouldCreateNetworkCallbackOnSubscribe() {
        // given
        final Observable<Connectivity> observable = strategy.observeNetworkConnectivity(context);
        // when
        observable.subscribe();
        // then
        Mockito.verify(strategy).createNetworkCallback(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Test
    public void shouldInvokeOnNextOnNetworkAvailable() {
        // given
        ConnectivityManager.NetworkCallback networkCallback = strategy.createNetworkCallback(context);
        // when
        networkCallback.onAvailable(network);
        // then
        Mockito.verify(strategy).onNext(ArgumentMatchers.any(Connectivity.class));
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Test
    public void shouldInvokeOnNextOnNetworkLost() {
        // given
        ConnectivityManager.NetworkCallback networkCallback = strategy.createNetworkCallback(context);
        // when
        networkCallback.onLost(network);
        // then
        Mockito.verify(strategy).onNext(ArgumentMatchers.any(Connectivity.class));
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Test
    public void shouldHandleErrorWhileTryingToUnregisterCallback() {
        // given
        strategy.observeNetworkConnectivity(context);
        final IllegalArgumentException exception = new IllegalArgumentException();
        Mockito.doThrow(exception).when(connectivityManager).unregisterNetworkCallback(ArgumentMatchers.any(NetworkCallback.class));
        // when
        strategy.tryToUnregisterCallback(connectivityManager);
        // then
        Mockito.verify(strategy).onError(ERROR_MSG_NETWORK_CALLBACK, exception);
    }

    @Test
    public void shouldHandleErrorWhileTryingToUnregisterReceiver() {
        // given
        strategy.observeNetworkConnectivity(context);
        final RuntimeException exception = new RuntimeException();
        Mockito.doThrow(exception).when(contextMock).unregisterReceiver(ArgumentMatchers.any(BroadcastReceiver.class));
        // when
        strategy.tryToUnregisterReceiver(contextMock);
        // then
        Mockito.verify(strategy).onError(ERROR_MSG_RECEIVER, exception);
    }
}

