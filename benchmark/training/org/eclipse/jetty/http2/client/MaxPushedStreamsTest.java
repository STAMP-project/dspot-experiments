/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http2.client;


import MetaData.Request;
import Stream.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PushPromiseFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MaxPushedStreamsTest extends AbstractTest {
    @Test
    public void testMaxPushedStreams() throws Exception {
        int maxPushed = 2;
        CountDownLatch resetLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                // Trick the server into thinking it can push unlimited streams.
                setMaxLocalStreams((-1));
                BiFunction<List<Stream>, Stream, List<Stream>> add = ( l, s) -> {
                    l.add(s);
                    return l;
                };
                BinaryOperator<List<Stream>> addAll = ( l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                };
                CompletableFuture<List<Stream>> result = CompletableFuture.completedFuture(new ArrayList<>());
                // Push maxPushed resources...
                // ... then send the response.
                // ... then send the data for the valid pushed streams...
                // ... then push one extra stream, the client must reject it...
                // ... wait for the pushed streams...
                IntStream.range(0, maxPushed).mapToObj(( i) -> new PushPromiseFrame(stream.getId(), 0, newRequest("GET", ("/push_" + i), new HttpFields()))).map(( pushFrame) -> {
                    Promise.Completable<Stream> promise = new Promise.Completable<>();
                    stream.push(pushFrame, promise, new Stream.Listener.Adapter());
                    return promise;
                }).reduce(result, ( cfList, cfStream) -> cfList.thenCombine(cfStream, add), ( cfList1, cfList2) -> cfList1.thenCombine(cfList2, addAll)).thenApply(( streams) -> {
                    PushPromiseFrame extraPushFrame = new PushPromiseFrame(stream.getId(), 0, newRequest("GET", "/push_extra", new HttpFields()));
                    FuturePromise<Stream> extraPromise = new FuturePromise<>();
                    stream.push(extraPushFrame, extraPromise, new Stream.Listener.Adapter() {
                        @Override
                        public void onReset(Stream stream, ResetFrame frame) {
                            assertEquals(ErrorCode.REFUSED_STREAM_ERROR.code, frame.getError());
                            resetLatch.countDown();
                        }
                    });
                    return streams;
                }).thenAccept(( streams) -> streams.forEach(( pushedStream) -> {
                    DataFrame data = new DataFrame(pushedStream.getId(), BufferUtil.EMPTY_BUFFER, true);
                    pushedStream.data(data, Callback.NOOP);
                })).thenRun(() -> {
                    MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                    stream.headers(new HeadersFrame(stream.getId(), response, null, true), Callback.NOOP);
                });
                return null;
            }
        });
        client.setMaxConcurrentPushedStreams(maxPushed);
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request request = newRequest("GET", new HttpFields());
        CountDownLatch responseLatch = new CountDownLatch(1);
        session.newStream(new HeadersFrame(request, null, true), new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (frame.isEndStream())
                    responseLatch.countDown();

            }
        });
        Assertions.assertTrue(resetLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(responseLatch.await(5, TimeUnit.SECONDS));
    }
}

