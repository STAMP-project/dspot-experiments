/**
 * Copyright 2016 Netflix, Inc.
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
package com.netflix.hystrix.contrib.metrics.controller;


import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.CONTENT_TYPE;
import com.netflix.hystrix.contrib.metrics.HystrixStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;


public class StreamingOutputProviderTest {
    private final Observable<String> streamOfOnNexts = Observable.interval(100, TimeUnit.MILLISECONDS).map(new rx.functions.Func1<Long, String>() {
        @Override
        public String call(Long timestamp) {
            return "test-stream";
        }
    });

    private final Observable<String> streamOfOnNextThenOnError = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? extends String> subscriber) {
            try {
                Thread.sleep(100);
                subscriber.onNext("test-stream");
                Thread.sleep(100);
                subscriber.onError(new RuntimeException("stream failure"));
            } catch ( ex) {
                ex.printStackTrace();
            }
        }
    }).subscribeOn(Schedulers.computation());

    private final Observable<String> streamOfOnNextThenOnCompleted = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? extends String> subscriber) {
            try {
                Thread.sleep(100);
                subscriber.onNext("test-stream");
                Thread.sleep(100);
                subscriber.onCompleted();
            } catch ( ex) {
                ex.printStackTrace();
            }
        }
    }).subscribeOn(Schedulers.computation());

    private AbstractHystrixStreamController sse = new AbstractHystrixStreamController(streamOfOnNexts) {
        private final AtomicInteger concurrentConnections = new AtomicInteger(0);

        @Override
        protected int getMaxNumberConcurrentConnectionsAllowed() {
            return 2;
        }

        @Override
        protected AtomicInteger getCurrentConnections() {
            return concurrentConnections;
        }
    };

    @Test
    public void concurrencyTest() throws Exception {
        Response resp = sse.handleRequest();
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertEquals("text/event-stream;charset=UTF-8", resp.getHeaders().getFirst(CONTENT_TYPE));
        Assert.assertEquals("no-cache, no-store, max-age=0, must-revalidate", resp.getHeaders().getFirst(CACHE_CONTROL));
        Assert.assertEquals("no-cache", resp.getHeaders().getFirst("Pragma"));
        resp = sse.handleRequest();
        Assert.assertEquals(200, resp.getStatus());
        resp = sse.handleRequest();
        Assert.assertEquals(503, resp.getStatus());
        Assert.assertEquals(("MaxConcurrentConnections reached: " + (sse.getMaxNumberConcurrentConnectionsAllowed())), resp.getEntity());
        sse.getCurrentConnections().decrementAndGet();
        resp = sse.handleRequest();
        Assert.assertEquals(200, resp.getStatus());
    }

    @Test
    public void testInfiniteOnNextStream() throws Exception {
        final PipedInputStream is = new PipedInputStream();
        final PipedOutputStream os = new PipedOutputStream(is);
        final AtomicInteger writes = new AtomicInteger(0);
        final HystrixStream stream = new HystrixStream(streamOfOnNexts, 100, new AtomicInteger(1));
        Thread streamingThread = StreamingOutputProviderTest.startStreamingThread(stream, os);
        StreamingOutputProviderTest.verifyStream(is, writes);
        Thread.sleep(1000);// Let the provider stream for some time.

        streamingThread.interrupt();// Stop streaming

        os.close();
        is.close();
        System.out.println(("Total lines:" + (writes.get())));
        assertTrue(((writes.get()) >= 9));// Observable is configured to emit events in every 100 ms. So expect at least 9 in a second.

        assertTrue(((stream.getConcurrentConnections().get()) == 0));// Provider is expected to decrement connection count when streaming process is terminated.

    }

    @Test
    public void testOnError() throws Exception {
        testStreamOnce(streamOfOnNextThenOnError);
    }

    @Test
    public void testOnComplete() throws Exception {
        testStreamOnce(streamOfOnNextThenOnCompleted);
    }
}

