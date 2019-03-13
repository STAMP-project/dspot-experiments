/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import NetworkFetchProducer.INTERMEDIATE_RESULT_PRODUCER_EVENT;
import NetworkFetchProducer.PRODUCER_NAME;
import NetworkFetcher.Callback;
import android.os.SystemClock;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.concurrent.GuardedBy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static NetworkFetchProducer.TIME_BETWEEN_PARTIAL_RESULTS_MS;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = NONE)
@PrepareForTest({ SystemClock.class })
public class NetworkFetchProducerTest {
    @Mock
    public ByteArrayPool mByteArrayPool;

    @Mock
    public PooledByteBuffer mPooledByteBuffer;

    @Mock
    public PooledByteBufferOutputStream mPooledByteBufferOutputStream;

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Consumer mConsumer;

    @Mock
    public NetworkFetcher mNetworkFetcher;

    @Mock
    public Map<String, String> mExtrasMap;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private byte[] mCommonByteArray;

    private final String mRequestId = "mRequestId";

    private NetworkFetchProducer mNetworkFetchProducer;

    private SettableProducerContext mProducerContext;

    private FetchState mFetchState;

    private ExecutorService mTestExecutor;

    @Test
    public void testExceptionInFetchImage() {
        NetworkFetcher.Callback callback = performFetch();
        callback.onFailure(new RuntimeException());
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(PRODUCER_NAME), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.isNull(Map.class));
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, PRODUCER_NAME, false);
    }

    @Test(timeout = 5000)
    public void testNoIntermediateResults() throws Exception {
        long currentTime = 86400L;
        Mockito.when(SystemClock.uptimeMillis()).thenReturn(currentTime);
        NetworkFetcher.Callback callback = performFetch();
        Mockito.when(mNetworkFetcher.shouldPropagate(ArgumentMatchers.any(FetchState.class))).thenReturn(false);
        final NetworkFetchProducerTest.BlockingInputStream inputStream = new NetworkFetchProducerTest.BlockingInputStream();
        final Future requestHandlerFuture = performResponse(inputStream, (-1), callback);
        // Consumer should not be notified before any data is read
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mPooledByteBufferFactory).newOutputStream();
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.anyInt());
        verifyPooledByteBufferUsed(0);
        // Allow NetworkFetchProducer to read 1024 bytes and check that consumer is not notified
        inputStream.increaseBytesToRead(1024);
        inputStream.waitUntilReadingThreadBlocked();
        inputStream.increaseBytesToRead(1024);
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.anyInt());
        verifyPooledByteBufferUsed(0);
        inputStream.signalEof();
        requestHandlerFuture.get();
        // Check no intermediate results were propagated
        Mockito.verify(mProducerListener, Mockito.times(0)).onProducerEvent(mRequestId, PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        // Test final result
        Mockito.verify(mConsumer, Mockito.times(1)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        verifyPooledByteBufferUsed(1);
        // When everything is over, pooled byte buffer output stream should be closed
        Mockito.verify(mPooledByteBufferOutputStream).close();
    }

    @Test(timeout = 5000)
    public void testDownloadHandler() throws Exception {
        long currentTime = 86400L;
        Mockito.when(SystemClock.uptimeMillis()).thenReturn(currentTime);
        NetworkFetcher.Callback callback = performFetch();
        Mockito.when(mNetworkFetcher.shouldPropagate(ArgumentMatchers.any(FetchState.class))).thenReturn(true);
        final NetworkFetchProducerTest.BlockingInputStream inputStream = new NetworkFetchProducerTest.BlockingInputStream();
        final Future requestHandlerFuture = performResponse(inputStream, (-1), callback);
        // Consumer should not be notified before any data is read
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mPooledByteBufferFactory).newOutputStream();
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.anyInt());
        verifyPooledByteBufferUsed(0);
        // Allow NetworkFetchProducer to read 1024 bytes and check that consumer is notified once
        inputStream.increaseBytesToRead(1024);
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mProducerListener, Mockito.times(1)).onProducerEvent(mRequestId, PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        Mockito.verify(mConsumer, Mockito.times(1)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(NO_FLAGS));
        verifyPooledByteBufferUsed(1);
        // Read another 1024 bytes, but do not bump timer - consumer should not be notified
        inputStream.increaseBytesToRead(1024);
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mProducerListener, Mockito.times(1)).onProducerEvent(mRequestId, PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        Mockito.verify(mConsumer, Mockito.times(1)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(NO_FLAGS));
        verifyPooledByteBufferUsed(1);
        // Read another 1024 bytes - this time bump timer. Consumer should be notified
        currentTime += TIME_BETWEEN_PARTIAL_RESULTS_MS;
        Mockito.when(SystemClock.uptimeMillis()).thenReturn(currentTime);
        inputStream.increaseBytesToRead(1024);
        inputStream.waitUntilReadingThreadBlocked();
        Mockito.verify(mProducerListener, Mockito.times(2)).onProducerEvent(mRequestId, PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        Mockito.verify(mConsumer, Mockito.times(2)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(NO_FLAGS));
        verifyPooledByteBufferUsed(2);
        // Test final result
        Mockito.verify(mConsumer, Mockito.times(0)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        inputStream.signalEof();
        requestHandlerFuture.get();
        Mockito.verify(mProducerListener, Mockito.times(2)).onProducerEvent(mRequestId, PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(PRODUCER_NAME), ArgumentMatchers.eq(mExtrasMap));
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, PRODUCER_NAME, true);
        Mockito.verify(mConsumer, Mockito.times(1)).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        verifyPooledByteBufferUsed(3);
        // When everything is over, pooled byte buffer output stream should be closed
        Mockito.verify(mPooledByteBufferOutputStream).close();
    }

    @Test
    public void testExceptionInResponseHandler() throws IOException {
        NetworkFetcher.Callback callback = performFetch();
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.read(ArgumentMatchers.any(byte[].class))).thenThrow(new IOException());
        Mockito.when(mNetworkFetcher.shouldPropagate(ArgumentMatchers.any(FetchState.class))).thenReturn(false);
        try {
            callback.onResponse(inputStream, 100);
            Assert.fail();
        } catch (Exception e) {
            Mockito.verify(mPooledByteBufferFactory).newOutputStream(100);
            Mockito.verify(mPooledByteBufferOutputStream).close();
            Mockito.verify(mProducerListener, Mockito.never()).onProducerEvent(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
        }
    }

    private class BlockingInputStream extends InputStream {
        @GuardedBy("BlockingInputStream.this")
        private int mBytesLeft = 0;

        @GuardedBy("BlockingInputStream.this")
        private boolean mFinished = false;

        @GuardedBy("BlockingInputStream.this")
        private boolean mReaderBlocked = false;

        @Override
        public int read() throws IOException {
            Assert.fail();
            return 0;
        }

        @Override
        public synchronized int read(byte[] buffer, int offset, int length) throws IOException {
            while (true) {
                if ((mBytesLeft) > 0) {
                    final int bytesToRead = Math.min(mBytesLeft, length);
                    mBytesLeft -= bytesToRead;
                    return bytesToRead;
                } else
                    if (mFinished) {
                        return -1;
                    } else {
                        mReaderBlocked = true;
                        try {
                            notify();
                            wait();
                        } catch (InterruptedException ie) {
                            throw Throwables.propagate(ie);
                        } finally {
                            mReaderBlocked = false;
                        }
                    }

            } 
        }

        public synchronized void increaseBytesToRead(int n) {
            mBytesLeft += n;
            notify();
        }

        public synchronized void waitUntilReadingThreadBlocked() {
            while (((mBytesLeft) > 0) || (!(mReaderBlocked))) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                    throw Throwables.propagate(ie);
                }
            } 
        }

        public synchronized void signalEof() {
            mFinished = true;
            notify();
        }
    }
}

