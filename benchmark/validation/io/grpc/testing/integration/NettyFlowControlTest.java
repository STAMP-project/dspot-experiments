/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.testing.integration;


import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.InternalHandlerSettings;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.integration.Messages.StreamingOutputCallResponse;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class NettyFlowControlTest {
    // in bytes
    private static final int LOW_BAND = (2 * 1024) * 1024;

    private static final int HIGH_BAND = (30 * 1024) * 1024;

    // in milliseconds
    private static final int MED_LAT = 10;

    // in bytes
    private static final int TINY_WINDOW = 1;

    private static final int REGULAR_WINDOW = 64 * 1024;

    private static final int MAX_WINDOW = (8 * 1024) * 1024;

    private static ManagedChannel channel;

    private static Server server;

    private static TrafficControlProxy proxy;

    private int proxyPort;

    private int serverPort;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("flowcontrol-test-pool", true));

    @Test
    public void largeBdp() throws IOException, InterruptedException {
        NettyFlowControlTest.proxy = new TrafficControlProxy(serverPort, NettyFlowControlTest.HIGH_BAND, NettyFlowControlTest.MED_LAT, TimeUnit.MILLISECONDS);
        NettyFlowControlTest.proxy.start();
        proxyPort = NettyFlowControlTest.proxy.getPort();
        resetConnection(NettyFlowControlTest.REGULAR_WINDOW);
        doTest(NettyFlowControlTest.HIGH_BAND, NettyFlowControlTest.MED_LAT);
    }

    @Test
    public void smallBdp() throws IOException, InterruptedException {
        NettyFlowControlTest.proxy = new TrafficControlProxy(serverPort, NettyFlowControlTest.LOW_BAND, NettyFlowControlTest.MED_LAT, TimeUnit.MILLISECONDS);
        NettyFlowControlTest.proxy.start();
        proxyPort = NettyFlowControlTest.proxy.getPort();
        resetConnection(NettyFlowControlTest.REGULAR_WINDOW);
        doTest(NettyFlowControlTest.LOW_BAND, NettyFlowControlTest.MED_LAT);
    }

    @Test
    public void verySmallWindowMakesProgress() throws IOException, InterruptedException {
        NettyFlowControlTest.proxy = new TrafficControlProxy(serverPort, NettyFlowControlTest.HIGH_BAND, NettyFlowControlTest.MED_LAT, TimeUnit.MILLISECONDS);
        NettyFlowControlTest.proxy.start();
        proxyPort = NettyFlowControlTest.proxy.getPort();
        resetConnection(NettyFlowControlTest.TINY_WINDOW);
        doTest(NettyFlowControlTest.HIGH_BAND, NettyFlowControlTest.MED_LAT);
    }

    /**
     * Simple stream observer to measure elapsed time of the call.
     */
    private static class TestStreamObserver implements StreamObserver<StreamingOutputCallResponse> {
        long startRequestNanos;

        long endRequestNanos;

        private final CountDownLatch latch = new CountDownLatch(1);

        long expectedWindow;

        int lastWindow;

        public TestStreamObserver(long window) {
            startRequestNanos = System.nanoTime();
            expectedWindow = window;
        }

        @Override
        public void onNext(StreamingOutputCallResponse value) {
            lastWindow = InternalHandlerSettings.getLatestClientWindow();
            if ((lastWindow) >= (expectedWindow)) {
                onCompleted();
            }
        }

        @Override
        public void onError(Throwable t) {
            latch.countDown();
            throw new RuntimeException(t);
        }

        @Override
        public void onCompleted() {
            latch.countDown();
        }

        public long getElapsedTime() {
            return (endRequestNanos) - (startRequestNanos);
        }

        public int waitFor() throws InterruptedException {
            latch.await();
            return lastWindow;
        }
    }
}

