package com.bumptech.glide.load.data;


import Priority.HIGH;
import Priority.IMMEDIATE;
import Priority.LOW;
import Priority.NORMAL;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.testutil.TestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests {@link com.bumptech.glide.load.data.HttpUrlFetcher} against server responses. Tests for
 * behavior (connection/disconnection/options) should go in
 * {@link com.bumptech.glide.load.data.HttpUrlFetcherTest}, response handling should go here.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class HttpUrlFetcherServerTest {
    private static final String DEFAULT_PATH = "/fakepath";

    private static final int TIMEOUT_TIME_MS = 300;

    @Mock
    private DataFetcher.DataCallback<InputStream> callback;

    private MockWebServer mockWebServer;

    private boolean defaultFollowRedirects;

    private ArgumentCaptor<InputStream> streamCaptor;

    @Test
    public void testReturnsInputStreamOnStatusOk() throws Exception {
        String expected = "fakedata";
        mockWebServer.enqueue(new MockResponse().setBody(expected).setResponseCode(200));
        HttpUrlFetcher fetcher = getFetcher();
        fetcher.loadData(HIGH, callback);
        Mockito.verify(callback).onDataReady(streamCaptor.capture());
        TestUtil.assertStreamOf(expected, streamCaptor.getValue());
        assertThat(mockWebServer.takeRequest().getMethod()).isEqualTo("GET");
    }

    @Test
    public void testHandlesRedirect301s() throws Exception {
        String expected = "fakedata";
        mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.url("/redirect").toString()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
        getFetcher().loadData(LOW, callback);
        Mockito.verify(callback).onDataReady(streamCaptor.capture());
        TestUtil.assertStreamOf(expected, streamCaptor.getValue());
        assertThat(mockWebServer.takeRequest().getMethod()).isEqualTo("GET");
        assertThat(mockWebServer.takeRequest().getMethod()).isEqualTo("GET");
    }

    @Test
    public void testHandlesRedirect302s() throws Exception {
        String expected = "fakedata";
        mockWebServer.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", mockWebServer.url("/redirect").toString()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
        getFetcher().loadData(LOW, callback);
        Mockito.verify(callback).onDataReady(streamCaptor.capture());
        TestUtil.assertStreamOf(expected, streamCaptor.getValue());
        assertThat(mockWebServer.takeRequest().getMethod()).isEqualTo("GET");
        assertThat(mockWebServer.takeRequest().getMethod()).isEqualTo("GET");
    }

    @Test
    public void testHandlesRelativeRedirects() throws Exception {
        String expected = "fakedata";
        mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", "/redirect"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onDataReady(streamCaptor.capture());
        TestUtil.assertStreamOf(expected, streamCaptor.getValue());
        RecordedRequest first = mockWebServer.takeRequest();
        assertThat(first.getMethod()).isEqualTo("GET");
        RecordedRequest second = mockWebServer.takeRequest();
        assertThat(second.getPath()).endsWith("/redirect");
        assertThat(second.getMethod()).isEqualTo("GET");
    }

    @Test
    public void testHandlesUpToFiveRedirects() throws Exception {
        int numRedirects = 4;
        String expected = "redirectedData";
        String redirectBase = "/redirect";
        for (int i = 0; i < numRedirects; i++) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.url((redirectBase + i)).toString()));
        }
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expected));
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onDataReady(streamCaptor.capture());
        TestUtil.assertStreamOf(expected, streamCaptor.getValue());
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains(HttpUrlFetcherServerTest.DEFAULT_PATH);
        assertThat(request.getMethod()).isEqualTo("GET");
        for (int i = 0; i < numRedirects; i++) {
            RecordedRequest current = mockWebServer.takeRequest();
            assertThat(current.getPath()).contains((redirectBase + i));
            assertThat(current.getMethod()).isEqualTo("GET");
        }
    }

    @Test
    public void testFailsOnRedirectLoops() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.url("/redirect").toString()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.url("/redirect").toString()));
        getFetcher().loadData(IMMEDIATE, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsIfRedirectLocationIsNotPresent() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(301));
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsIfRedirectLocationIsPresentAndEmpty() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", ""));
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsIfStatusCodeIsNegativeOne() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode((-1)));
        getFetcher().loadData(LOW, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsAfterTooManyRedirects() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(301).setHeader("Location", mockWebServer.url(("/redirect" + i)).toString()));
        }
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsIfStatusCodeIs500() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        getFetcher().loadData(NORMAL, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testFailsIfStatusCodeIs400() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));
        getFetcher().loadData(LOW, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testSetsReadTimeout() throws Exception {
        MockWebServer tempWebServer = new MockWebServer();
        tempWebServer.enqueue(new MockResponse().setBody("test").throttleBody(1, HttpUrlFetcherServerTest.TIMEOUT_TIME_MS, TimeUnit.MILLISECONDS));
        tempWebServer.start();
        try {
            getFetcher().loadData(HIGH, callback);
        } finally {
            tempWebServer.shutdown();
            // shutdown() called before any enqueue() blocks until it times out.
            mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        }
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(IOException.class));
    }

    @Test
    public void testAppliesHeadersInGlideUrl() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        String headerField = "field";
        String headerValue = "value";
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put(headerField, headerValue);
        Headers headers = Mockito.mock(Headers.class);
        Mockito.when(headers.getHeaders()).thenReturn(headersMap);
        getFetcher(headers).loadData(HIGH, callback);
        assertThat(mockWebServer.takeRequest().getHeader(headerField)).isEqualTo(headerValue);
    }
}

