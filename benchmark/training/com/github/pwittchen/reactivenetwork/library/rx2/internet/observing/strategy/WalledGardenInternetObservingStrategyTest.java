/**
 * Copyright (C) 2017 Piotr Wittchen
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
package com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy;


import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.error.ErrorHandler;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway")
public class WalledGardenInternetObservingStrategyTest {
    private static final int INITIAL_INTERVAL_IN_MS = 0;

    private static final int INTERVAL_IN_MS = 2000;

    private static final int PORT = 80;

    private static final int TIMEOUT_IN_MS = 30;

    private static final int HTTP_RESPONSE = 204;

    private static final String HOST_WITH_HTTP = "http://www.website.com";

    private static final String HOST_WITHOUT_HTTP = "www.website.com";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    private WalledGardenInternetObservingStrategy strategy;

    @Mock
    private ErrorHandler errorHandler;

    @Test
    public void shouldBeConnectedToTheInternet() {
        // given
        Mockito.when(strategy.isConnected(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler)).thenReturn(true);
        // when
        final Observable<Boolean> observable = strategy.observeInternetConnectivity(WalledGardenInternetObservingStrategyTest.INITIAL_INTERVAL_IN_MS, WalledGardenInternetObservingStrategyTest.INTERVAL_IN_MS, getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler);
        boolean isConnected = observable.blockingFirst();
        // then
        assertThat(isConnected).isTrue();
    }

    @Test
    public void shouldNotBeConnectedToTheInternet() {
        // given
        Mockito.when(strategy.isConnected(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler)).thenReturn(false);
        // when
        final Observable<Boolean> observable = strategy.observeInternetConnectivity(WalledGardenInternetObservingStrategyTest.INITIAL_INTERVAL_IN_MS, WalledGardenInternetObservingStrategyTest.INTERVAL_IN_MS, getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler);
        boolean isConnected = observable.blockingFirst();
        // then
        assertThat(isConnected).isFalse();
    }

    @Test
    public void shouldBeConnectedToTheInternetViaSingle() {
        // given
        Mockito.when(strategy.isConnected(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler)).thenReturn(true);
        // when
        final Single<Boolean> observable = strategy.checkInternetConnectivity(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler);
        boolean isConnected = observable.blockingGet();
        // then
        assertThat(isConnected).isTrue();
    }

    @Test
    public void shouldNotBeConnectedToTheInternetViaSingle() {
        // given
        Mockito.when(strategy.isConnected(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler)).thenReturn(false);
        // when
        final Single<Boolean> observable = strategy.checkInternetConnectivity(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler);
        boolean isConnected = observable.blockingGet();
        // then
        assertThat(isConnected).isFalse();
    }

    @Test
    public void shouldCreateHttpUrlConnection() throws IOException {
        // given
        final String parsedDefaultHost = "clients3.google.com";
        // when
        HttpURLConnection connection = strategy.createHttpUrlConnection(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS);
        // then
        assertThat(connection).isNotNull();
        assertThat(connection.getURL().getHost()).isEqualTo(parsedDefaultHost);
        assertThat(connection.getURL().getPort()).isEqualTo(WalledGardenInternetObservingStrategyTest.PORT);
        assertThat(connection.getConnectTimeout()).isEqualTo(WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS);
        assertThat(connection.getReadTimeout()).isEqualTo(WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS);
        assertThat(connection.getInstanceFollowRedirects()).isFalse();
        assertThat(connection.getUseCaches()).isFalse();
    }

    @Test
    public void shouldHandleAnExceptionWhileCreatingUrlConnection() throws IOException {
        // given
        final String errorMsg = "Could not establish connection with WalledGardenStrategy";
        final IOException givenException = new IOException(errorMsg);
        Mockito.when(strategy.createHttpUrlConnection(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS)).thenThrow(givenException);
        // when
        strategy.isConnected(getHost(), WalledGardenInternetObservingStrategyTest.PORT, WalledGardenInternetObservingStrategyTest.TIMEOUT_IN_MS, WalledGardenInternetObservingStrategyTest.HTTP_RESPONSE, errorHandler);
        // then
        Mockito.verify(errorHandler).handleError(givenException, errorMsg);
    }

    @Test
    public void shouldNotTransformHttpHost() {
        // when
        String transformedHost = strategy.adjustHost(WalledGardenInternetObservingStrategyTest.HOST_WITH_HTTP);
        // then
        assertThat(transformedHost).isEqualTo(WalledGardenInternetObservingStrategyTest.HOST_WITH_HTTP);
    }

    @Test
    public void shouldNotTransformHttpsHost() {
        // when
        String transformedHost = strategy.adjustHost(WalledGardenInternetObservingStrategyTest.HOST_WITH_HTTP);
        // then
        assertThat(transformedHost).isEqualTo(WalledGardenInternetObservingStrategyTest.HOST_WITH_HTTP);
    }

    @Test
    public void shouldAddHttpProtocolToHost() {
        // when
        String transformedHost = strategy.adjustHost(WalledGardenInternetObservingStrategyTest.HOST_WITHOUT_HTTP);
        // then
        assertThat(transformedHost).isEqualTo(WalledGardenInternetObservingStrategyTest.HOST_WITH_HTTP);
    }
}

