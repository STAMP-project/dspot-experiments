/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.block.stream;


import Status.UNAUTHENTICATED;
import alluxio.Constants;
import alluxio.exception.status.CanceledException;
import alluxio.exception.status.DeadlineExceededException;
import alluxio.exception.status.UnauthenticatedException;
import alluxio.grpc.WriteRequest;
import alluxio.grpc.WriteResponse;
import alluxio.util.ThreadFactoryUtils;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


/**
 * Tests for {@link GrpcBlockingStream}.
 */
public final class GrpcBlockingStreamTest {
    private static final int BUFFER_SIZE = 5;

    private static final long TIMEOUT = 10 * (Constants.SECOND_MS);

    private static final long SHORT_TIMEOUT = (Constants.SECOND) / 2;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, ThreadFactoryUtils.build("test-executor-%d", true));

    private static final String TEST_MESSAGE = "test message";

    private BlockWorkerClient mClient;

    private ClientCallStreamObserver<WriteRequest> mRequestObserver;

    private ClientResponseObserver<WriteRequest, WriteResponse> mResponseObserver;

    private GrpcBlockingStream<WriteRequest, WriteResponse> mStream;

    private Runnable mOnReadyHandler;

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Checks send request is called on the request observer.
     */
    @Test
    public void send() throws Exception {
        WriteRequest request = WriteRequest.newBuilder().build();
        mStream.send(request, GrpcBlockingStreamTest.TIMEOUT);
        Mockito.verify(mRequestObserver).onNext(request);
    }

    /**
     * Checks response posted on the response observer is received.
     */
    @Test
    public void receive() throws Exception {
        WriteResponse response = WriteResponse.newBuilder().build();
        mResponseObserver.onNext(response);
        WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
        Assert.assertEquals(response, actualResponse);
    }

    /**
     * Checks onCompleted is called on request observer upon close.
     */
    @Test
    public void close() throws Exception {
        mStream.close();
        Assert.assertTrue(mStream.isClosed());
        Assert.assertFalse(mStream.isOpen());
        Mockito.verify(mRequestObserver).onCompleted();
    }

    /**
     * Checks cancel is called on request observer upon cancel.
     */
    @Test
    public void cancel() throws Exception {
        mStream.cancel();
        Assert.assertTrue(mStream.isCanceled());
        Assert.assertFalse(mStream.isOpen());
        Mockito.verify(mRequestObserver).cancel(any(String.class), eq(null));
    }

    /**
     * Checks onCompleted posted on the response observer is received.
     */
    @Test
    public void onCompleted() throws Exception {
        mResponseObserver.onCompleted();
        WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
        Assert.assertNull(actualResponse);
    }

    /**
     * Checks expected error during send.
     */
    @Test
    public void sendError() throws Exception {
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mResponseObserver.onError(UNAUTHENTICATED.asRuntimeException());
        mStream.send(WriteRequest.newBuilder().build(), GrpcBlockingStreamTest.TIMEOUT);
    }

    /**
     * Checks send fails after stream is closed.
     */
    @Test
    public void sendFailsAfterClosed() throws Exception {
        mStream.close();
        mThrown.expect(CanceledException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.send(WriteRequest.newBuilder().build(), GrpcBlockingStreamTest.TIMEOUT);
    }

    /**
     * Checks send fails after stream is canceled.
     */
    @Test
    public void sendFailsAfterCanceled() throws Exception {
        mStream.cancel();
        mThrown.expect(CanceledException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.send(WriteRequest.newBuilder().build(), GrpcBlockingStreamTest.TIMEOUT);
    }

    /**
     * Checks receive fails after stream is canceled.
     */
    @Test
    public void receiveFailsAfterCanceled() throws Exception {
        mStream.cancel();
        mThrown.expect(CanceledException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
    }

    /**
     * Checks expected error during receive.
     */
    @Test
    public void receiveError() throws Exception {
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mResponseObserver.onError(UNAUTHENTICATED.asRuntimeException());
        mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
    }

    /**
     * Checks send fails after timeout waiting for stream to be ready.
     */
    @Test
    public void sendFailsAfterTimeout() throws Exception {
        Mockito.when(mRequestObserver.isReady()).thenReturn(false);
        mThrown.expect(DeadlineExceededException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.send(WriteRequest.newBuilder().build(), GrpcBlockingStreamTest.SHORT_TIMEOUT);
    }

    /**
     * Checks receive fails after timeout waiting for message from response stream.
     */
    @Test
    public void receiveFailsAfterTimeout() throws Exception {
        mThrown.expect(DeadlineExceededException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.receive(GrpcBlockingStreamTest.SHORT_TIMEOUT);
    }

    /**
     * Checks send after stream is ready.
     */
    @Test
    public void sendAfterStreamReady() throws Exception {
        Mockito.when(mRequestObserver.isReady()).thenReturn(false);
        Mockito.doAnswer(( args) -> {
            mOnReadyHandler = getArgumentAt(0, Runnable.class);
            return null;
        }).when(mRequestObserver).setOnReadyHandler(org.mockito.ArgumentMatchers.any(Runnable.class));
        mResponseObserver.beforeStart(mRequestObserver);
        GrpcBlockingStreamTest.EXECUTOR.submit(() -> {
            try {
                // notify ready after a short period of time
                Thread.sleep(GrpcBlockingStreamTest.SHORT_TIMEOUT);
                Mockito.when(mRequestObserver.isReady()).thenReturn(true);
                mOnReadyHandler.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        WriteRequest request = WriteRequest.newBuilder().build();
        mStream.send(request, GrpcBlockingStreamTest.TIMEOUT);
        Mockito.verify(mRequestObserver).onNext(request);
    }

    /**
     * Checks receive after response arrives.
     */
    @Test
    public void receiveAfterResponseArrives() throws Exception {
        WriteResponse response = WriteResponse.newBuilder().build();
        GrpcBlockingStreamTest.EXECUTOR.submit(() -> {
            try {
                // push response after a short period of time
                Thread.sleep(GrpcBlockingStreamTest.SHORT_TIMEOUT);
                mResponseObserver.onNext(response);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
        Assert.assertEquals(response, actualResponse);
    }

    /**
     * Checks receive responses more than buffer size in order.
     */
    @Test
    public void receiveMoreThanBufferSize() throws Exception {
        WriteResponse[] responses = Stream.generate(() -> WriteResponse.newBuilder().build()).limit(((GrpcBlockingStreamTest.BUFFER_SIZE) * 2)).toArray(WriteResponse[]::new);
        GrpcBlockingStreamTest.EXECUTOR.submit(() -> {
            for (WriteResponse response : responses) {
                mResponseObserver.onNext(response);
            }
        });
        Thread.sleep(GrpcBlockingStreamTest.SHORT_TIMEOUT);
        for (WriteResponse response : responses) {
            WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
            Assert.assertEquals(response, actualResponse);
        }
    }

    /**
     * Checks receive fails immediately upon error even if buffer is full.
     */
    @Test
    public void receiveErrorWhenBufferFull() throws Exception {
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        WriteResponse[] responses = Stream.generate(() -> WriteResponse.newBuilder().build()).limit(GrpcBlockingStreamTest.BUFFER_SIZE).toArray(WriteResponse[]::new);
        for (WriteResponse response : responses) {
            mResponseObserver.onNext(response);
        }
        mResponseObserver.onError(UNAUTHENTICATED.asRuntimeException());
        for (WriteResponse response : responses) {
            WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
            Assert.assertEquals(response, actualResponse);
        }
    }

    /**
     * Checks waitForComplete succeed after onCompleted is triggered on response stream.
     */
    @Test
    public void waitForComplete() throws Exception {
        WriteResponse[] responses = Stream.generate(() -> WriteResponse.newBuilder().build()).limit(((GrpcBlockingStreamTest.BUFFER_SIZE) * 2)).toArray(WriteResponse[]::new);
        GrpcBlockingStreamTest.EXECUTOR.submit(() -> {
            for (WriteResponse response : responses) {
                mResponseObserver.onNext(response);
            }
            try {
                Thread.sleep(GrpcBlockingStreamTest.SHORT_TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            mResponseObserver.onCompleted();
        });
        WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
        Assert.assertEquals(responses[0], actualResponse);
        mStream.waitForComplete(GrpcBlockingStreamTest.TIMEOUT);
        actualResponse = mStream.receive(GrpcBlockingStreamTest.TIMEOUT);
        Assert.assertEquals(null, actualResponse);
    }

    /**
     * Checks waitForComplete fails after times out.
     */
    @Test
    public void waitForCompleteTimeout() throws Exception {
        WriteResponse[] responses = Stream.generate(() -> WriteResponse.newBuilder().build()).limit(((GrpcBlockingStreamTest.BUFFER_SIZE) * 2)).toArray(WriteResponse[]::new);
        GrpcBlockingStreamTest.EXECUTOR.submit(() -> {
            for (WriteResponse response : responses) {
                mResponseObserver.onNext(response);
            }
            try {
                Thread.sleep(GrpcBlockingStreamTest.TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            mResponseObserver.onCompleted();
        });
        WriteResponse actualResponse = mStream.receive(GrpcBlockingStreamTest.SHORT_TIMEOUT);
        Assert.assertEquals(responses[0], actualResponse);
        mThrown.expect(DeadlineExceededException.class);
        mThrown.expectMessage(Matchers.containsString(GrpcBlockingStreamTest.TEST_MESSAGE));
        mStream.waitForComplete(GrpcBlockingStreamTest.SHORT_TIMEOUT);
    }
}

