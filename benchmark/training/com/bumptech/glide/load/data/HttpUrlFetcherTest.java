package com.bumptech.glide.load.data;


import HttpUrlFetcher.HttpUrlConnectionFactory;
import Priority.HIGH;
import Priority.IMMEDIATE;
import Priority.LOW;
import Priority.NORMAL;
import com.bumptech.glide.load.model.GlideUrl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class HttpUrlFetcherTest {
    @Mock
    private HttpURLConnection urlConnection;

    @Mock
    private HttpUrlConnectionFactory connectionFactory;

    @Mock
    private GlideUrl glideUrl;

    @Mock
    private InputStream stream;

    @Mock
    private DataFetcher.DataCallback<InputStream> callback;

    private static final int TIMEOUT_MS = 100;

    private HttpUrlFetcher fetcher;

    @Test
    public void testSetsReadTimeout() throws IOException {
        fetcher.loadData(HIGH, callback);
        Mockito.verify(urlConnection).setReadTimeout(ArgumentMatchers.eq(HttpUrlFetcherTest.TIMEOUT_MS));
    }

    @Test
    public void testSetsConnectTimeout() throws IOException {
        fetcher.loadData(IMMEDIATE, callback);
        Mockito.verify(urlConnection).setConnectTimeout(ArgumentMatchers.eq(HttpUrlFetcherTest.TIMEOUT_MS));
    }

    @Test
    public void testReturnsNullIfCancelledBeforeConnects() throws IOException {
        InputStream notExpected = new ByteArrayInputStream(new byte[0]);
        Mockito.when(urlConnection.getInputStream()).thenReturn(notExpected);
        fetcher.cancel();
        fetcher.loadData(LOW, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.isNull(InputStream.class));
    }

    @Test
    public void testDisconnectsUrlOnCleanup() throws IOException {
        fetcher.loadData(HIGH, callback);
        fetcher.cleanup();
        Mockito.verify(urlConnection).disconnect();
    }

    @Test
    public void testDoesNotThrowIfCleanupCalledBeforeStarted() {
        fetcher.cleanup();
    }

    @Test
    public void testDoesNotThrowIfCancelCalledBeforeStart() {
        fetcher.cancel();
    }

    @Test
    public void testCancelDoesNotDisconnectIfAlreadyConnected() throws IOException {
        fetcher.loadData(HIGH, callback);
        fetcher.cancel();
        Mockito.verify(urlConnection, Mockito.never()).disconnect();
    }

    @Test
    public void testClosesStreamInCleanupIfNotNull() throws IOException {
        fetcher.loadData(HIGH, callback);
        fetcher.cleanup();
        Mockito.verify(stream).close();
    }

    @Test
    public void testClosesStreamBeforeDisconnectingConnection() throws IOException {
        fetcher.loadData(NORMAL, callback);
        fetcher.cleanup();
        InOrder order = Mockito.inOrder(stream, urlConnection);
        order.verify(stream).close();
        order.verify(urlConnection).disconnect();
    }
}

