/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.ext.cronet;


import C.RESULT_END_OF_INPUT;
import CronetDataSource.OpenException;
import NetworkException.ERROR_HOSTNAME_NOT_RESOLVED;
import UrlRequest.Builder;
import UrlRequest.Callback;
import android.net.Uri;
import android.os.ConditionVariable;
import android.os.SystemClock;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.net.CronetEngine;
import org.chromium.net.NetworkException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link CronetDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class CronetDataSourceTest {
    private static final int TEST_CONNECT_TIMEOUT_MS = 100;

    private static final int TEST_READ_TIMEOUT_MS = 100;

    private static final String TEST_URL = "http://google.com";

    private static final String TEST_CONTENT_TYPE = "test/test";

    private static final byte[] TEST_POST_BODY = Util.getUtf8Bytes("test post body");

    private static final long TEST_CONTENT_LENGTH = 16000L;

    private static final int TEST_CONNECTION_STATUS = 5;

    private static final int TEST_INVALID_CONNECTION_STATUS = -1;

    private DataSpec testDataSpec;

    private DataSpec testPostDataSpec;

    private DataSpec testHeadDataSpec;

    private Map<String, String> testResponseHeader;

    private UrlResponseInfo testUrlResponseInfo;

    @Mock
    private Builder mockUrlRequestBuilder;

    @Mock
    private UrlRequest mockUrlRequest;

    @Mock
    private Predicate<String> mockContentTypePredicate;

    @Mock
    private TransferListener mockTransferListener;

    @Mock
    private Executor mockExecutor;

    @Mock
    private NetworkException mockNetworkException;

    @Mock
    private CronetEngine mockCronetEngine;

    private CronetDataSource dataSourceUnderTest;

    private boolean redirectCalled;

    @Test
    public void testOpeningTwiceThrows() throws HttpDataSourceException {
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testDataSpec);
        try {
            dataSourceUnderTest.open(testDataSpec);
            Assert.fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void testCallbackFromPreviousRequest() throws HttpDataSourceException {
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testDataSpec);
        dataSourceUnderTest.close();
        // Prepare a mock UrlRequest to be used in the second open() call.
        final UrlRequest mockUrlRequest2 = Mockito.mock(UrlRequest.class);
        Mockito.when(mockUrlRequestBuilder.build()).thenReturn(mockUrlRequest2);
        Mockito.doAnswer(( invocation) -> {
            // Invoke the callback for the previous request.
            dataSourceUnderTest.urlRequestCallback.onFailed(mockUrlRequest, testUrlResponseInfo, mockNetworkException);
            dataSourceUnderTest.urlRequestCallback.onResponseStarted(mockUrlRequest2, testUrlResponseInfo);
            return null;
        }).when(mockUrlRequest2).start();
        dataSourceUnderTest.open(testDataSpec);
    }

    @Test
    public void testRequestStartCalled() throws HttpDataSourceException {
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockCronetEngine).newUrlRequestBuilder(ArgumentMatchers.eq(CronetDataSourceTest.TEST_URL), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class));
        Mockito.verify(mockUrlRequest).start();
    }

    @Test
    public void testRequestHeadersSet() throws HttpDataSourceException {
        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 1000, 5000, null);
        mockResponseStartSuccess();
        dataSourceUnderTest.setRequestProperty("firstHeader", "firstValue");
        dataSourceUnderTest.setRequestProperty("secondHeader", "secondValue");
        dataSourceUnderTest.open(testDataSpec);
        // The header value to add is current position to current position + length - 1.
        Mockito.verify(mockUrlRequestBuilder).addHeader("Range", "bytes=1000-5999");
        Mockito.verify(mockUrlRequestBuilder).addHeader("firstHeader", "firstValue");
        Mockito.verify(mockUrlRequestBuilder).addHeader("secondHeader", "secondValue");
        Mockito.verify(mockUrlRequest).start();
    }

    @Test
    public void testRequestOpen() throws HttpDataSourceException {
        mockResponseStartSuccess();
        assertThat(dataSourceUnderTest.open(testDataSpec)).isEqualTo(CronetDataSourceTest.TEST_CONTENT_LENGTH);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onTransferStart(dataSourceUnderTest, testDataSpec, true);
    }

    @Test
    public void testRequestOpenGzippedCompressedReturnsDataSpecLength() throws HttpDataSourceException {
        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 0, 5000, null);
        testResponseHeader.put("Content-Encoding", "gzip");
        testResponseHeader.put("Content-Length", Long.toString(50L));
        mockResponseStartSuccess();
        /* contentLength */
        assertThat(dataSourceUnderTest.open(testDataSpec)).isEqualTo(5000);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onTransferStart(dataSourceUnderTest, testDataSpec, true);
    }

    @Test
    public void testRequestOpenFail() {
        mockResponseStartFailure();
        try {
            dataSourceUnderTest.open(testDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            // Check for connection not automatically closed.
            assertThat(((e.getCause()) instanceof UnknownHostException)).isFalse();
            Mockito.verify(mockUrlRequest, Mockito.never()).cancel();
            /* isNetwork= */
            Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
        }
    }

    @Test
    public void testRequestOpenFailDueToDnsFailure() {
        mockResponseStartFailure();
        Mockito.when(mockNetworkException.getErrorCode()).thenReturn(ERROR_HOSTNAME_NOT_RESOLVED);
        try {
            dataSourceUnderTest.open(testDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            // Check for connection not automatically closed.
            assertThat(((e.getCause()) instanceof UnknownHostException)).isTrue();
            Mockito.verify(mockUrlRequest, Mockito.never()).cancel();
            /* isNetwork= */
            Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
        }
    }

    @Test
    public void testRequestOpenValidatesStatusCode() {
        mockResponseStartSuccess();
        testUrlResponseInfo = createUrlResponseInfo(500);// statusCode

        try {
            dataSourceUnderTest.open(testDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            assertThat((e instanceof HttpDataSource.InvalidResponseCodeException)).isTrue();
            // Check for connection not automatically closed.
            Mockito.verify(mockUrlRequest, Mockito.never()).cancel();
            /* isNetwork= */
            Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
        }
    }

    @Test
    public void testRequestOpenValidatesContentTypePredicate() {
        mockResponseStartSuccess();
        Mockito.when(mockContentTypePredicate.evaluate(ArgumentMatchers.anyString())).thenReturn(false);
        try {
            dataSourceUnderTest.open(testDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            assertThat((e instanceof HttpDataSource.InvalidContentTypeException)).isTrue();
            // Check for connection not automatically closed.
            Mockito.verify(mockUrlRequest, Mockito.never()).cancel();
            Mockito.verify(mockContentTypePredicate).evaluate(CronetDataSourceTest.TEST_CONTENT_TYPE);
        }
    }

    @Test
    public void testPostRequestOpen() throws HttpDataSourceException {
        mockResponseStartSuccess();
        dataSourceUnderTest.setRequestProperty("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
        assertThat(dataSourceUnderTest.open(testPostDataSpec)).isEqualTo(CronetDataSourceTest.TEST_CONTENT_LENGTH);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onTransferStart(dataSourceUnderTest, testPostDataSpec, true);
    }

    @Test
    public void testPostRequestOpenValidatesContentType() {
        mockResponseStartSuccess();
        try {
            dataSourceUnderTest.open(testPostDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            Mockito.verify(mockUrlRequest, Mockito.never()).start();
        }
    }

    @Test
    public void testPostRequestOpenRejects307Redirects() {
        mockResponseStartSuccess();
        mockResponseStartRedirect();
        try {
            dataSourceUnderTest.setRequestProperty("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
            dataSourceUnderTest.open(testPostDataSpec);
            Assert.fail("HttpDataSource.HttpDataSourceException expected");
        } catch (HttpDataSourceException e) {
            Mockito.verify(mockUrlRequest, Mockito.never()).followRedirect();
        }
    }

    @Test
    public void testHeadRequestOpen() throws HttpDataSourceException {
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testHeadDataSpec);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onTransferStart(dataSourceUnderTest, testHeadDataSpec, true);
        dataSourceUnderTest.close();
    }

    @Test
    public void testRequestReadTwice() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[8];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(0, 8));
        assertThat(bytesRead).isEqualTo(8);
        returnedBuffer = new byte[8];
        bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(8, 8));
        assertThat(bytesRead).isEqualTo(8);
        // Should have only called read on cronet once.
        Mockito.verify(mockUrlRequest, Mockito.times(1)).read(ArgumentMatchers.any(ByteBuffer.class));
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.times(2)).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 8);
    }

    @Test
    public void testSecondRequestNoContentLength() throws HttpDataSourceException {
        mockResponseStartSuccess();
        testResponseHeader.put("Content-Length", Long.toString(1L));
        mockReadSuccess(0, 16);
        // First request.
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[8];
        dataSourceUnderTest.read(returnedBuffer, 0, 1);
        dataSourceUnderTest.close();
        testResponseHeader.remove("Content-Length");
        mockReadSuccess(0, 16);
        // Second request.
        dataSourceUnderTest.open(testDataSpec);
        returnedBuffer = new byte[16];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 10);
        assertThat(bytesRead).isEqualTo(10);
        bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 10);
        assertThat(bytesRead).isEqualTo(6);
        bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 10);
        assertThat(bytesRead).isEqualTo(RESULT_END_OF_INPUT);
    }

    @Test
    public void testReadWithOffset() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[16];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 8, 8);
        assertThat(bytesRead).isEqualTo(8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.prefixZeros(CronetDataSourceTest.buildTestDataArray(0, 8), 16));
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 8);
    }

    @Test
    public void testRangeRequestWith206Response() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(1000, 5000);
        testUrlResponseInfo = createUrlResponseInfo(206);// Server supports range requests.

        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 1000, 5000, null);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[16];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 16);
        assertThat(bytesRead).isEqualTo(16);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(1000, 16));
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 16);
    }

    @Test
    public void testRangeRequestWith200Response() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(0, 7000);
        testUrlResponseInfo = createUrlResponseInfo(200);// Server does not support range requests.

        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 1000, 5000, null);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[16];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 16);
        assertThat(bytesRead).isEqualTo(16);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(1000, 16));
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 16);
    }

    @Test
    public void testReadWithUnsetLength() throws HttpDataSourceException {
        testResponseHeader.remove("Content-Length");
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[16];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 8, 8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.prefixZeros(CronetDataSourceTest.buildTestDataArray(0, 8), 16));
        assertThat(bytesRead).isEqualTo(8);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 8);
    }

    @Test
    public void testReadReturnsWhatItCan() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[24];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 24);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.suffixZeros(CronetDataSourceTest.buildTestDataArray(0, 16), 24));
        assertThat(bytesRead).isEqualTo(16);
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 16);
    }

    @Test
    public void testClosedMeansClosed() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        int bytesRead = 0;
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[8];
        bytesRead += dataSourceUnderTest.read(returnedBuffer, 0, 8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(0, 8));
        assertThat(bytesRead).isEqualTo(8);
        dataSourceUnderTest.close();
        /* isNetwork= */
        Mockito.verify(mockTransferListener).onTransferEnd(dataSourceUnderTest, testDataSpec, true);
        try {
            bytesRead += dataSourceUnderTest.read(returnedBuffer, 0, 8);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        // 16 bytes were attempted but only 8 should have been successfully read.
        assertThat(bytesRead).isEqualTo(8);
    }

    @Test
    public void testOverread() throws HttpDataSourceException {
        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 0, 16, null);
        testResponseHeader.put("Content-Length", Long.toString(16L));
        mockResponseStartSuccess();
        mockReadSuccess(0, 16);
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[8];
        int bytesRead = dataSourceUnderTest.read(returnedBuffer, 0, 8);
        assertThat(bytesRead).isEqualTo(8);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.buildTestDataArray(0, 8));
        // The current buffer is kept if not completely consumed by DataSource reader.
        returnedBuffer = new byte[8];
        bytesRead += dataSourceUnderTest.read(returnedBuffer, 0, 6);
        assertThat(bytesRead).isEqualTo(14);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.suffixZeros(CronetDataSourceTest.buildTestDataArray(8, 6), 8));
        // 2 bytes left at this point.
        returnedBuffer = new byte[8];
        bytesRead += dataSourceUnderTest.read(returnedBuffer, 0, 8);
        assertThat(bytesRead).isEqualTo(16);
        assertThat(returnedBuffer).isEqualTo(CronetDataSourceTest.suffixZeros(CronetDataSourceTest.buildTestDataArray(14, 2), 8));
        // Should have only called read on cronet once.
        Mockito.verify(mockUrlRequest, Mockito.times(1)).read(ArgumentMatchers.any(ByteBuffer.class));
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.times(1)).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 8);
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.times(1)).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 6);
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.times(1)).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, 2);
        // Now we already returned the 16 bytes initially asked.
        // Try to read again even though all requested 16 bytes are already returned.
        // Return C.RESULT_END_OF_INPUT
        returnedBuffer = new byte[16];
        int bytesOverRead = dataSourceUnderTest.read(returnedBuffer, 0, 16);
        assertThat(bytesOverRead).isEqualTo(RESULT_END_OF_INPUT);
        assertThat(returnedBuffer).isEqualTo(new byte[16]);
        // C.RESULT_END_OF_INPUT should not be reported though the TransferListener.
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.never()).onBytesTransferred(dataSourceUnderTest, testDataSpec, true, RESULT_END_OF_INPUT);
        // There should still be only one call to read on cronet.
        Mockito.verify(mockUrlRequest, Mockito.times(1)).read(ArgumentMatchers.any(ByteBuffer.class));
        // Check for connection not automatically closed.
        Mockito.verify(mockUrlRequest, Mockito.never()).cancel();
        assertThat(bytesRead).isEqualTo(16);
    }

    @Test
    public void testConnectTimeout() throws InterruptedException {
        long startTimeMs = SystemClock.elapsedRealtime();
        final ConditionVariable startCondition = buildUrlRequestStartedCondition();
        final CountDownLatch timedOutLatch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    dataSourceUnderTest.open(testDataSpec);
                    Assert.fail();
                } catch (HttpDataSourceException e) {
                    // Expected.
                    assertThat((e instanceof CronetDataSource.OpenException)).isTrue();
                    assertThat(((e.getCause()) instanceof SocketTimeoutException)).isTrue();
                    assertThat(((CronetDataSource.OpenException) (e)).cronetConnectionStatus).isEqualTo(CronetDataSourceTest.TEST_CONNECTION_STATUS);
                    timedOutLatch.countDown();
                }
            }
        }.start();
        startCondition.block();
        // We should still be trying to open.
        assertNotCountedDown(timedOutLatch);
        // We should still be trying to open as we approach the timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 1));
        assertNotCountedDown(timedOutLatch);
        // Now we timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) + 10));
        timedOutLatch.await();
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
    }

    @Test
    public void testConnectInterrupted() throws InterruptedException {
        long startTimeMs = SystemClock.elapsedRealtime();
        final ConditionVariable startCondition = buildUrlRequestStartedCondition();
        final CountDownLatch timedOutLatch = new CountDownLatch(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dataSourceUnderTest.open(testDataSpec);
                    Assert.fail();
                } catch (HttpDataSourceException e) {
                    // Expected.
                    assertThat((e instanceof CronetDataSource.OpenException)).isTrue();
                    assertThat(((e.getCause()) instanceof CronetDataSource.InterruptedIOException)).isTrue();
                    assertThat(((CronetDataSource.OpenException) (e)).cronetConnectionStatus).isEqualTo(CronetDataSourceTest.TEST_INVALID_CONNECTION_STATUS);
                    timedOutLatch.countDown();
                }
            }
        };
        thread.start();
        startCondition.block();
        // We should still be trying to open.
        assertNotCountedDown(timedOutLatch);
        // We should still be trying to open as we approach the timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 1));
        assertNotCountedDown(timedOutLatch);
        // Now we interrupt.
        thread.interrupt();
        timedOutLatch.await();
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
    }

    @Test
    public void testConnectResponseBeforeTimeout() throws Exception {
        long startTimeMs = SystemClock.elapsedRealtime();
        final ConditionVariable startCondition = buildUrlRequestStartedCondition();
        final CountDownLatch openLatch = new CountDownLatch(1);
        AtomicReference<Exception> exceptionOnTestThread = new AtomicReference<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    dataSourceUnderTest.open(testDataSpec);
                } catch (HttpDataSourceException e) {
                    exceptionOnTestThread.set(e);
                } finally {
                    openLatch.countDown();
                }
            }
        }.start();
        startCondition.block();
        // We should still be trying to open.
        assertNotCountedDown(openLatch);
        // We should still be trying to open as we approach the timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 1));
        assertNotCountedDown(openLatch);
        // The response arrives just in time.
        dataSourceUnderTest.urlRequestCallback.onResponseStarted(mockUrlRequest, testUrlResponseInfo);
        openLatch.await();
        assertThat(exceptionOnTestThread.get()).isNull();
    }

    @Test
    public void testRedirectIncreasesConnectionTimeout() throws Exception {
        long startTimeMs = SystemClock.elapsedRealtime();
        final ConditionVariable startCondition = buildUrlRequestStartedCondition();
        final CountDownLatch timedOutLatch = new CountDownLatch(1);
        final AtomicInteger openExceptions = new AtomicInteger(0);
        new Thread() {
            @Override
            public void run() {
                try {
                    dataSourceUnderTest.open(testDataSpec);
                    Assert.fail();
                } catch (HttpDataSourceException e) {
                    // Expected.
                    assertThat((e instanceof CronetDataSource.OpenException)).isTrue();
                    assertThat(((e.getCause()) instanceof SocketTimeoutException)).isTrue();
                    openExceptions.getAndIncrement();
                    timedOutLatch.countDown();
                }
            }
        }.start();
        startCondition.block();
        // We should still be trying to open.
        assertNotCountedDown(timedOutLatch);
        // We should still be trying to open as we approach the timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 1));
        assertNotCountedDown(timedOutLatch);
        // A redirect arrives just in time.
        dataSourceUnderTest.urlRequestCallback.onRedirectReceived(mockUrlRequest, testUrlResponseInfo, "RandomRedirectedUrl1");
        long newTimeoutMs = (2 * (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 1;
        SystemClock.setCurrentTimeMillis(((startTimeMs + newTimeoutMs) - 1));
        // We should still be trying to open as we approach the new timeout.
        assertNotCountedDown(timedOutLatch);
        // A redirect arrives just in time.
        dataSourceUnderTest.urlRequestCallback.onRedirectReceived(mockUrlRequest, testUrlResponseInfo, "RandomRedirectedUrl2");
        newTimeoutMs = (3 * (CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS)) - 2;
        SystemClock.setCurrentTimeMillis(((startTimeMs + newTimeoutMs) - 1));
        // We should still be trying to open as we approach the new timeout.
        assertNotCountedDown(timedOutLatch);
        // Now we timeout.
        SystemClock.setCurrentTimeMillis(((startTimeMs + newTimeoutMs) + 10));
        timedOutLatch.await();
        /* isNetwork= */
        Mockito.verify(mockTransferListener, Mockito.never()).onTransferStart(dataSourceUnderTest, testDataSpec, true);
        assertThat(openExceptions.get()).isEqualTo(1);
    }

    @Test
    public void testRedirectParseAndAttachCookie_dataSourceDoesNotHandleSetCookie_followsRedirect() throws HttpDataSourceException {
        mockSingleRedirectSuccess();
        mockFollowRedirectSuccess();
        testResponseHeader.put("Set-Cookie", "testcookie=testcookie; Path=/video");
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder, Mockito.never()).addHeader(ArgumentMatchers.eq("Cookie"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequest).followRedirect();
    }

    @Test
    public void testRedirectParseAndAttachCookie_dataSourceHandlesSetCookie_andPreservesOriginalRequestHeaders() throws HttpDataSourceException {
        dataSourceUnderTest = // resetTimeoutOnRedirects
        new CronetDataSource(mockCronetEngine, mockExecutor, mockContentTypePredicate, CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS, CronetDataSourceTest.TEST_READ_TIMEOUT_MS, true, Clock.DEFAULT, null, true);
        dataSourceUnderTest.addTransferListener(mockTransferListener);
        dataSourceUnderTest.setRequestProperty("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
        mockSingleRedirectSuccess();
        testResponseHeader.put("Set-Cookie", "testcookie=testcookie; Path=/video");
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder).addHeader(ArgumentMatchers.eq("Cookie"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequestBuilder, Mockito.never()).addHeader(ArgumentMatchers.eq("Range"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequestBuilder, Mockito.times(2)).addHeader("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
        Mockito.verify(mockUrlRequest, Mockito.never()).followRedirect();
        Mockito.verify(mockUrlRequest, Mockito.times(2)).start();
    }

    @Test
    public void testRedirectParseAndAttachCookie_dataSourceHandlesSetCookie_andPreservesOriginalRequestHeadersIncludingByteRangeHeader() throws HttpDataSourceException {
        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 1000, 5000, null);
        dataSourceUnderTest = // resetTimeoutOnRedirects
        new CronetDataSource(mockCronetEngine, mockExecutor, mockContentTypePredicate, CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS, CronetDataSourceTest.TEST_READ_TIMEOUT_MS, true, Clock.DEFAULT, null, true);
        dataSourceUnderTest.addTransferListener(mockTransferListener);
        dataSourceUnderTest.setRequestProperty("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
        mockSingleRedirectSuccess();
        testResponseHeader.put("Set-Cookie", "testcookie=testcookie; Path=/video");
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder).addHeader(ArgumentMatchers.eq("Cookie"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequestBuilder, Mockito.times(2)).addHeader("Range", "bytes=1000-5999");
        Mockito.verify(mockUrlRequestBuilder, Mockito.times(2)).addHeader("Content-Type", CronetDataSourceTest.TEST_CONTENT_TYPE);
        Mockito.verify(mockUrlRequest, Mockito.never()).followRedirect();
        Mockito.verify(mockUrlRequest, Mockito.times(2)).start();
    }

    @Test
    public void testRedirectNoSetCookieFollowsRedirect() throws HttpDataSourceException {
        mockSingleRedirectSuccess();
        mockFollowRedirectSuccess();
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder, Mockito.never()).addHeader(ArgumentMatchers.eq("Cookie"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequest).followRedirect();
    }

    @Test
    public void testRedirectNoSetCookieFollowsRedirect_dataSourceHandlesSetCookie() throws HttpDataSourceException {
        dataSourceUnderTest = // resetTimeoutOnRedirects
        new CronetDataSource(mockCronetEngine, mockExecutor, mockContentTypePredicate, CronetDataSourceTest.TEST_CONNECT_TIMEOUT_MS, CronetDataSourceTest.TEST_READ_TIMEOUT_MS, true, Clock.DEFAULT, null, true);
        dataSourceUnderTest.addTransferListener(mockTransferListener);
        mockSingleRedirectSuccess();
        mockFollowRedirectSuccess();
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder, Mockito.never()).addHeader(ArgumentMatchers.eq("Cookie"), ArgumentMatchers.any(String.class));
        Mockito.verify(mockUrlRequest).followRedirect();
    }

    @Test
    public void testExceptionFromTransferListener() throws HttpDataSourceException {
        mockResponseStartSuccess();
        // Make mockTransferListener throw an exception in CronetDataSource.close(). Ensure that
        // the subsequent open() call succeeds.
        /* isNetwork= */
        Mockito.doThrow(new NullPointerException()).when(mockTransferListener).onTransferEnd(dataSourceUnderTest, testDataSpec, true);
        dataSourceUnderTest.open(testDataSpec);
        try {
            dataSourceUnderTest.close();
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected.
        }
        // Open should return successfully.
        dataSourceUnderTest.open(testDataSpec);
    }

    @Test
    public void testReadFailure() throws HttpDataSourceException {
        mockResponseStartSuccess();
        mockReadFailure();
        dataSourceUnderTest.open(testDataSpec);
        byte[] returnedBuffer = new byte[8];
        try {
            dataSourceUnderTest.read(returnedBuffer, 0, 8);
            Assert.fail("dataSourceUnderTest.read() returned, but IOException expected");
        } catch (IOException e) {
            // Expected.
        }
    }

    @Test
    public void testReadInterrupted() throws HttpDataSourceException, InterruptedException {
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testDataSpec);
        final ConditionVariable startCondition = buildReadStartedCondition();
        final CountDownLatch timedOutLatch = new CountDownLatch(1);
        byte[] returnedBuffer = new byte[8];
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    dataSourceUnderTest.read(returnedBuffer, 0, 8);
                    Assert.fail();
                } catch (HttpDataSourceException e) {
                    // Expected.
                    assertThat(((e.getCause()) instanceof CronetDataSource.InterruptedIOException)).isTrue();
                    timedOutLatch.countDown();
                }
            }
        };
        thread.start();
        startCondition.block();
        assertNotCountedDown(timedOutLatch);
        // Now we interrupt.
        thread.interrupt();
        timedOutLatch.await();
    }

    @Test
    public void testAllowDirectExecutor() throws HttpDataSourceException {
        testDataSpec = new DataSpec(Uri.parse(CronetDataSourceTest.TEST_URL), 1000, 5000, null);
        mockResponseStartSuccess();
        dataSourceUnderTest.open(testDataSpec);
        Mockito.verify(mockUrlRequestBuilder).allowDirectExecutor();
    }
}

