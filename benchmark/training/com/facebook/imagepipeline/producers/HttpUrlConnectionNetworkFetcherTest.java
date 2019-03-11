/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import NetworkFetcher.Callback;
import android.net.Uri;
import com.facebook.common.util.UriUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUrlConnectionNetworkFetcher.class, Uri.class, UriUtil.class })
public class HttpUrlConnectionNetworkFetcherTest {
    public static final String INITIAL_TEST_URL = "http://localhost/";

    public static final String HTTPS_URL = "https://localhost/";

    @Mock
    private FetchState mMockFetchState;

    @Mock
    private ProducerContext mMockProducerContext;

    @Mock
    private Callback mMockCallback;

    private HttpUrlConnectionNetworkFetcher mFetcher;

    private Queue<HttpURLConnection> mConnectionsQueue;

    @Test
    public void testFetchSendsSuccessToCallback() throws IOException {
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        HttpURLConnection mockConnection = mockSuccessWithStream(mockInputStream);
        runFetch();
        InOrder inOrder = Mockito.inOrder(mMockCallback, mockConnection);
        inOrder.verify(mockConnection).getInputStream();
        inOrder.verify(mMockCallback).onResponse(mockInputStream, (-1));
        inOrder.verify(mockConnection).disconnect();
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void testFetchSendsErrorToCallbackAfterHttpError() throws IOException {
        HttpURLConnection mockResponse = mockFailure();
        runFetch();
        Mockito.verify(mMockCallback).onFailure(ArgumentMatchers.any(IOException.class));
        Mockito.verify(mockResponse).disconnect();
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void testFetchSendsSuccessToCallbackAfterRedirect() throws IOException {
        HttpURLConnection mockRedirect = mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.HTTPS_URL);
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        HttpURLConnection mockRedirectedConnection = mockSuccessWithStream(mockInputStream);
        runFetch();
        Mockito.verify(mockRedirect).disconnect();
        InOrder inOrder = Mockito.inOrder(mMockCallback, mockRedirectedConnection);
        inOrder.verify(mMockCallback).onResponse(mockInputStream, (-1));
        inOrder.verify(mockRedirectedConnection).disconnect();
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void testFetchSendsErrorToCallbackAfterRedirectToSameLocation() throws IOException {
        HttpURLConnection mockRedirect = mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.INITIAL_TEST_URL);
        HttpURLConnection mockSuccess = mockSuccess();
        runFetch();
        Mockito.verify(mMockCallback).onFailure(ArgumentMatchers.any(IOException.class));
        Mockito.verify(mockRedirect).disconnect();
        Mockito.verifyZeroInteractions(mockSuccess);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void testFetchSendsErrorToCallbackAfterTooManyRedirects() throws IOException {
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.HTTPS_URL);
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.INITIAL_TEST_URL);
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.HTTPS_URL);
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.INITIAL_TEST_URL);
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.HTTPS_URL);
        mockRedirectTo(HttpUrlConnectionNetworkFetcherTest.INITIAL_TEST_URL);
        HttpURLConnection mockResponseAfterSixRedirects = mockSuccess();
        runFetch();
        Mockito.verify(mMockCallback).onFailure(ArgumentMatchers.any(IOException.class));
        Mockito.verifyZeroInteractions(mockResponseAfterSixRedirects);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void testHttpUrlConnectionTimeout() throws Exception {
        URL mockURL = PowerMockito.mock(URL.class);
        HttpURLConnection mockConnection = PowerMockito.mock(HttpURLConnection.class);
        mockConnection.setConnectTimeout(30000);
        PowerMockito.when(mockURL.openConnection()).thenReturn(mockConnection);
        SocketTimeoutException expectedException = new SocketTimeoutException();
        PowerMockito.when(mockConnection.getResponseCode()).thenThrow(expectedException);
        Mockito.verify(mockConnection).setConnectTimeout(30000);
    }
}

